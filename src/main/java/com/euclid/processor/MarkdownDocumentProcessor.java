package com.euclid.processor;

import com.euclid.TranspileResult;
import com.euclid.Transpiler;
import com.euclid.exception.Diagnostic;
import com.euclid.exception.DiagnosticCollector;
import com.euclid.lang.EuclidAliasHandling;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.SourceSpan;
import org.commonmark.parser.IncludeSourceSpans;
import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Processes Markdown documents that contain explicit Euclid math spans.
 * Only `$...$` and `$$...$$` regions are treated as Euclid, while protected
 * Markdown regions are determined from a CommonMark parse tree.
 */
public final class MarkdownDocumentProcessor {
    private static final Parser MARKDOWN_PARSER = Parser.builder()
            .includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES)
            .build();

    private MarkdownDocumentProcessor() {
    }

    public static TranspileResult transpileDocument(
            String source,
            boolean verbose,
            EuclidAliasHandling aliasHandling) {
        DiagnosticCollector collector = new DiagnosticCollector();
        String output = processDocument(source, collector, Mode.TRANSPILE, verbose, aliasHandling);
        return new TranspileResult(output, collector.getAll());
    }

    public static TranspileResult checkDocument(
            String source,
            boolean verbose,
            EuclidAliasHandling aliasHandling) {
        return transpileDocument(source, verbose, aliasHandling);
    }

    public static String canonicalizeDocument(String source) {
        return processDocument(source, null, Mode.CANONICALIZE, false, EuclidAliasHandling.WARN);
    }

    private static String processDocument(
            String source,
            DiagnosticCollector collector,
            Mode mode,
            boolean verbose,
            EuclidAliasHandling aliasHandling) {
        List<ProtectedRange> protectedRanges = collectProtectedRanges(source);
        StringBuilder output = new StringBuilder(source.length());
        SourceLocation location = new SourceLocation(1, 1);
        int index = 0;
        int protectedIndex = 0;

        while (index < source.length()) {
            while (protectedIndex < protectedRanges.size() && protectedRanges.get(protectedIndex).end() <= index) {
                protectedIndex++;
            }

            if (protectedIndex < protectedRanges.size()) {
                ProtectedRange range = protectedRanges.get(protectedIndex);
                if (range.start() <= index) {
                    String protectedText = source.substring(index, range.end());
                    output.append(protectedText);
                    location = advance(location, protectedText);
                    index = range.end();
                    continue;
                }
            }

            if (startsDisplayMath(source, index)) {
                int closing = findClosingDisplayMath(source, index + 2);
                if (closing < 0) {
                    if (collector != null) {
                        collector.addError(
                                "document.unclosed-display-math",
                                "Unclosed '$$' display math span",
                                location.line(),
                                location.column(),
                                "Close the display math span with '$$'",
                                null);
                    }
                    String remainder = source.substring(index);
                    output.append(remainder);
                    break;
                }

                String rawSpan = source.substring(index, closing + 2);
                output.append(processDisplayMathSpan(rawSpan, collector, location, mode, verbose, aliasHandling));
                location = advance(location, rawSpan);
                index = closing + 2;
                continue;
            }

            if (source.charAt(index) == '$' && !isEscaped(source, index)) {
                int closing = findClosingInlineMath(source, index + 1);
                if (closing >= 0) {
                    String rawSpan = source.substring(index, closing + 1);
                    String inner = rawSpan.substring(1, rawSpan.length() - 1);
                    String rendered = processMathContent(
                            inner,
                            collector,
                            location.line(),
                            location.column() + 1,
                            mode,
                            verbose,
                            aliasHandling);
                    output.append("$").append(rendered).append("$");
                    location = advance(location, rawSpan);
                    index = closing + 1;
                    continue;
                }
            }

            char current = source.charAt(index);
            output.append(current);
            location = advance(location, current);
            index++;
        }

        return output.toString();
    }

    private static List<ProtectedRange> collectProtectedRanges(String source) {
        Node document = MARKDOWN_PARSER.parse(source);
        List<ProtectedRange> ranges = new ArrayList<>();

        document.accept(new AbstractVisitor() {
            @Override
            public void visit(Code code) {
                addRanges(code);
            }

            @Override
            public void visit(FencedCodeBlock fencedCodeBlock) {
                addRanges(fencedCodeBlock);
            }

            @Override
            public void visit(IndentedCodeBlock indentedCodeBlock) {
                addRanges(indentedCodeBlock);
            }

            @Override
            public void visit(HtmlInline htmlInline) {
                addRanges(htmlInline);
            }

            @Override
            public void visit(HtmlBlock htmlBlock) {
                addRanges(htmlBlock);
            }

            @Override
            public void visit(Link link) {
                addRanges(link);
            }

            @Override
            public void visit(Image image) {
                addRanges(image);
            }

            private void addRanges(Node node) {
                for (SourceSpan sourceSpan : node.getSourceSpans()) {
                    if (sourceSpan.getLength() > 0) {
                        ranges.add(new ProtectedRange(
                                sourceSpan.getInputIndex(),
                                sourceSpan.getInputIndex() + sourceSpan.getLength()));
                    }
                }
            }
        });

        return mergeRanges(ranges);
    }

    private static List<ProtectedRange> mergeRanges(List<ProtectedRange> ranges) {
        if (ranges.isEmpty()) {
            return List.of();
        }

        List<ProtectedRange> sorted = new ArrayList<>(ranges);
        sorted.sort(Comparator.comparingInt(ProtectedRange::start));

        List<ProtectedRange> merged = new ArrayList<>();
        ProtectedRange current = sorted.get(0);
        for (int i = 1; i < sorted.size(); i++) {
            ProtectedRange next = sorted.get(i);
            if (next.start() <= current.end()) {
                current = new ProtectedRange(current.start(), Math.max(current.end(), next.end()));
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

    private static String processDisplayMathSpan(
            String rawSpan,
            DiagnosticCollector collector,
            SourceLocation location,
            Mode mode,
            boolean verbose,
            EuclidAliasHandling aliasHandling) {
        DisplayMathSpan span = createDisplayMathSpan(rawSpan, location);
        String rendered = processMathContent(
                span.innerSource(),
                collector,
                span.innerLine(),
                span.innerColumn(),
                mode,
                verbose,
                aliasHandling);
        if (span.blockStyle()) {
            return "$$\n" + rendered + "\n$$";
        }
        return "$$" + rendered + "$$";
    }

    private static DisplayMathSpan createDisplayMathSpan(String rawSpan, SourceLocation location) {
        String inner = rawSpan.substring(2, rawSpan.length() - 2);
        boolean blockStyle = rawSpan.startsWith("$$\n") && rawSpan.endsWith("\n$$");
        int innerLine = location.line();
        int innerColumn = location.column() + 2;
        if (blockStyle) {
            inner = inner.substring(1, inner.length() - 1);
            innerLine++;
            innerColumn = 1;
        }
        return new DisplayMathSpan(inner, innerLine, innerColumn, blockStyle);
    }

    private static String processMathContent(
            String mathSource,
            DiagnosticCollector collector,
            int spanLine,
            int spanColumn,
            Mode mode,
            boolean verbose,
            EuclidAliasHandling aliasHandling) {
        if (mode == Mode.CANONICALIZE) {
            return Transpiler.canonicalize(mathSource);
        }

        TranspileResult result = Transpiler.transpileWithDiagnostics(
                mathSource,
                verbose,
                com.euclid.transpiler.MathMode.NONE,
                aliasHandling);
        if (collector != null) {
            mergeDiagnostics(collector, result.diagnostics(), spanLine, spanColumn);
        }
        if (result.hasErrors() || result.output() == null) {
            return mathSource;
        }
        return result.output();
    }

    private static void mergeDiagnostics(
            DiagnosticCollector collector,
            List<Diagnostic> diagnostics,
            int baseLine,
            int baseColumn) {
        for (Diagnostic diagnostic : diagnostics) {
            int line = baseLine + diagnostic.getLine() - 1;
            int column = diagnostic.getLine() == 1
                    ? baseColumn + diagnostic.getColumn() - 1
                    : diagnostic.getColumn();
            collector.add(new Diagnostic(
                    diagnostic.getSeverity(),
                    diagnostic.getCode(),
                    diagnostic.getMessage(),
                    line,
                    column,
                    diagnostic.getSuggestion(),
                    diagnostic.getCanonicalRewrite()));
        }
    }

    private static int findClosingInlineMath(String source, int start) {
        for (int i = start; i < source.length(); i++) {
            char current = source.charAt(i);
            if (current == '\n' || current == '\r') {
                return -1;
            }
            if (current == '$' && !isEscaped(source, i)) {
                return i;
            }
        }
        return -1;
    }

    private static int findClosingDisplayMath(String source, int start) {
        for (int i = start; i + 1 < source.length(); i++) {
            if (source.charAt(i) == '$' && source.charAt(i + 1) == '$' && !isEscaped(source, i)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean startsDisplayMath(String source, int index) {
        return index + 1 < source.length()
                && source.charAt(index) == '$'
                && source.charAt(index + 1) == '$'
                && !isEscaped(source, index);
    }

    private static boolean isEscaped(String source, int index) {
        int backslashes = 0;
        for (int i = index - 1; i >= 0 && source.charAt(i) == '\\'; i--) {
            backslashes++;
        }
        return backslashes % 2 == 1;
    }

    private static SourceLocation advance(SourceLocation location, String text) {
        SourceLocation current = location;
        for (int i = 0; i < text.length(); i++) {
            current = advance(current, text.charAt(i));
        }
        return current;
    }

    private static SourceLocation advance(SourceLocation location, char current) {
        if (current == '\n') {
            return new SourceLocation(location.line() + 1, 1);
        }
        return new SourceLocation(location.line(), location.column() + 1);
    }

    private enum Mode {
        TRANSPILE,
        CANONICALIZE
    }

    private record ProtectedRange(int start, int end) {
    }

    private record SourceLocation(int line, int column) {
    }

    private record DisplayMathSpan(String innerSource, int innerLine, int innerColumn, boolean blockStyle) {
    }
}
