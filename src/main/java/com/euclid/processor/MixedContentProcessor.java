package com.euclid.processor;

import com.euclid.TranspileResult;
import com.euclid.Transpiler;
import com.euclid.exception.Diagnostic;
import com.euclid.exception.DiagnosticCollector;
import com.euclid.exception.EuclidException;
import com.euclid.transpiler.MathMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processes mixed markdown and Euclid math content.
 * Detects inline math expressions and transpiles them while preserving surrounding text.
 */
public class MixedContentProcessor {

    // Pattern to match potential math expressions with word boundaries to prevent
    // false matches (e.g., "sin" inside "singing")
    private static final Pattern MATH_PATTERN = Pattern.compile(
        // Function calls: require word boundary before AND '(' after to avoid partial word matches
        "(?<![a-zA-Z])(pow|abs|ceil|floor|mod|gcd|lcm|sin|cos|tan|csc|sec|cot|sinh|cosh|tanh|csch|sech|coth|" +
        "log|ln|exp|sqrt|limit|diff|partial|integral|sum|prod|vector|matrix|" +
        "lt|gt|leq|geq|approx|neq|equiv|pm|times|div|cdot|ast|star|circ|bullet|" +
        "cap|cup|subset|supset|subseteq|supseteq|union|intersection|set_diff|" +
        "element_of|not_element_of|AND|OR|NOT|implies|iff|forall|exists|given)(?![a-zA-Z])\\s*\\([^)]*\\)" +
        "|" +
        // Constants: strict word boundaries
        "(?<![a-zA-Z])(PI|GAMMA|PHI|INFINITY|INF|ALPHA|BETA|DELTA|EPSILON|ZETA|ETA|THETA|" +
        "KAPPA|LAMBDA|MU|NU|XI|OMICRON|RHO|SIGMA|TAU|UPSILON|CHI|PSI|OMEGA|emptyset)(?![a-zA-Z])" +
        "|" +
        // Arithmetic: require at least one operator between numbers
        "\\d+\\s*[+\\-*/^]\\s*\\d+" +
        "|" +
        // Fraction operator: a \\\\ b
        "\\w+\\s*\\\\\\\\\\s*\\w+" +
        "|" +
        // Power operator: 2 ^ 3
        "\\w+\\s*\\^\\s*\\w+"
    );

    /**
     * Processes a line of mixed content, detecting and transpiling math expressions.
     *
     * @param line The input line with mixed content
     * @return The processed line with transpiled math wrapped in $
     * @throws EuclidException if transpilation fails
     */
    public static String processLine(String line) throws EuclidException {
        return processLine(line, 1, null);
    }

    public static String processLine(String line, int lineNumber, DiagnosticCollector diagnostics) throws EuclidException {
        // Skip pure markdown elements (headers, blank lines)
        if (line.matches("^\\s*#+\\s.*") || line.trim().isEmpty()) {
            return line;
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = MATH_PATTERN.matcher(line);
        int lastEnd = 0;

        while (matcher.find()) {
            // Append text before the math expression
            result.append(line, lastEnd, matcher.start());

            // Extract and transpile the math expression
            String mathExpr = matcher.group();
            TranspileResult transpileResult = Transpiler.transpileWithDiagnostics(mathExpr, false, MathMode.NONE, false);
            mergeDiagnostics(diagnostics, transpileResult.diagnostics(), lineNumber, matcher.start() + 1);

            if (!transpileResult.hasErrors() && transpileResult.output() != null) {
                result.append("$").append(transpileResult.output()).append("$");
            } else {
                result.append(mathExpr);
            }

            lastEnd = matcher.end();
        }

        // Append remaining text
        result.append(line.substring(lastEnd));

        return result.toString();
    }

    /**
     * Processes an entire document with mixed content.
     *
     * @param content The input document
     * @return The processed document
     * @throws EuclidException if transpilation fails
     */
    public static String processDocument(String content) throws EuclidException {
        return processDocument(content, null);
    }

    public static String processDocument(String content, DiagnosticCollector diagnostics) throws EuclidException {
        // pre-pass: join lines with unclosed delimiters so multiline expressions stay together
        String[] rawLines = content.split("\n");
        List<String> joined = new ArrayList<>();
        List<Integer> joinedStartLines = new ArrayList<>();
        StringBuilder pending = new StringBuilder();
        int depth = 0;
        int lineNumber = 1;
        int pendingStartLine = 1;
        for (String line : rawLines) {
            if (pending.length() == 0) {
                pendingStartLine = lineNumber;
            }
            if (pending.length() > 0) pending.append(" ");
            pending.append(line);
            for (char c : line.toCharArray()) {
                if (c == '(' || c == '[') depth++;
                else if (c == ')' || c == ']') depth--;
            }
            if (depth <= 0) {
                joined.add(pending.toString());
                joinedStartLines.add(pendingStartLine);
                pending.setLength(0);
                depth = 0;
            }
            lineNumber++;
        }
        if (pending.length() > 0) {
            joined.add(pending.toString());
            joinedStartLines.add(pendingStartLine);
        }

        List<String> processedLines = new ArrayList<>();
        for (int i = 0; i < joined.size(); i++) {
            processedLines.add(processLine(joined.get(i), joinedStartLines.get(i), diagnostics));
        }
        return String.join("\n", processedLines);
    }

    private static void mergeDiagnostics(
            DiagnosticCollector collector,
            List<Diagnostic> diagnostics,
            int baseLine,
            int baseColumn) {
        if (collector == null) {
            return;
        }

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
}
