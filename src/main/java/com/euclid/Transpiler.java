package com.euclid;

import com.euclid.ast.AstNode;
import com.euclid.ast.DocumentNode;
import com.euclid.exception.Diagnostic;
import com.euclid.exception.DiagnosticCollector;
import com.euclid.exception.EuclidException;
import com.euclid.lang.EuclidAliasHandling;
import com.euclid.lang.EuclidAliasOccurrence;
import com.euclid.lang.EuclidCapabilityManifest;
import com.euclid.lang.EuclidCanonicalizationResult;
import com.euclid.lang.EuclidLanguage;
import com.euclid.lexer.Lexer;
import com.euclid.parser.Parser;
import com.euclid.token.Token;
import com.euclid.transpiler.LatexTranspiler;
import com.euclid.transpiler.MathMode;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Main Transpiler class that converts Euclid (.ed) files to Markdown with LaTeX.
 */
public class Transpiler {

    /**
     * Transpiles Euclid source code to LaTeX/Markdown.
     *
     * @param source The Euclid source code
     * @return The transpiled LaTeX/Markdown
     * @throws EuclidException if an error occurs during transpilation
     */
    public static String transpile(String source) throws EuclidException {
        return transpile(source, false, MathMode.NONE);
    }

    /**
     * Transpiles Euclid source code to LaTeX/Markdown with optional verbose output.
     *
     * @param source  The Euclid source code
     * @param verbose Whether to print debug information
     * @return The transpiled LaTeX/Markdown
     * @throws EuclidException if an error occurs during transpilation
     */
    public static String transpile(String source, boolean verbose) throws EuclidException {
        return transpile(source, verbose, MathMode.NONE);
    }

    /**
     * Transpiles Euclid source code to LaTeX/Markdown with optional verbose output and math mode.
     *
     * @param source   The Euclid source code
     * @param verbose  Whether to print debug information
     * @param mathMode The math mode for wrapping output
     * @return The transpiled LaTeX/Markdown
     * @throws EuclidException if an error occurs during transpilation
     */
    public static String transpile(String source, boolean verbose, MathMode mathMode) throws EuclidException {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        if (verbose) {
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TOKENIZATION OUTPUT");
            System.out.println("═══════════════════════════════════════════════════════════");
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                System.out.printf("[%3d] %s%n", i, token.toDebugString());
            }
            System.out.println("Total tokens: " + tokens.size());
            System.out.println();
        }

        // Parse: Convert tokens to AST
        Parser parser = new Parser(tokens);
        DocumentNode ast = parser.parse();

        if (verbose) {
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("ABSTRACT SYNTAX TREE (AST)");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println(prettyPrintAst(ast, 0));
            System.out.println();
        }

        // Transpile: Convert AST to LaTeX
        LatexTranspiler transpiler = new LatexTranspiler(mathMode);
        return ast.accept(transpiler);
    }

    /**
     * Returns the shared capability manifest for editor and CLI-adjacent tooling.
     */
    public static EuclidCapabilityManifest capabilityManifest() {
        return EuclidLanguage.capabilityManifest();
    }

    /**
     * Canonicalizes lexical aliases to their preferred spellings.
     */
    public static String canonicalize(String source) {
        return EuclidLanguage.canonicalizeSource(source);
    }

    /**
     * Canonicalizes lexical aliases and returns exact alias occurrence metadata.
     */
    public static EuclidCanonicalizationResult canonicalizeWithMetadata(String source) {
        return EuclidLanguage.canonicalizeSourceWithMetadata(source);
    }

    /**
     * Transpiles with diagnostic collection instead of throwing exceptions.
     */
    public static TranspileResult transpileWithDiagnostics(String source, boolean verbose, MathMode mathMode) {
        return transpileWithDiagnostics(source, verbose, mathMode, EuclidAliasHandling.WARN);
    }

    /**
     * Transpiles with diagnostic collection and explicit alias handling.
     */
    public static TranspileResult transpileWithDiagnostics(
            String source,
            boolean verbose,
            MathMode mathMode,
            EuclidAliasHandling aliasHandling) {
        DiagnosticCollector collector = new DiagnosticCollector();
        EuclidCanonicalizationResult canonicalization = canonicalizeWithMetadata(source);
        String canonicalSource = canonicalization.canonicalSource();
        for (EuclidAliasOccurrence occurrence : canonicalization.aliasOccurrences()) {
            if (aliasHandling == EuclidAliasHandling.ERROR) {
                collector.addError(
                        "alias.noncanonical",
                        "Compatibility alias '" + occurrence.alias() + "' is not allowed in strict alias mode",
                        occurrence.line(),
                        occurrence.column(),
                        "Use '" + occurrence.canonical() + "' or run --canonicalize first",
                        canonicalSource);
            } else {
                collector.addWarning(
                        "canonical.rewrite",
                        "Compatibility alias '" + occurrence.alias() + "' should be written as '" + occurrence.canonical() + "'",
                        occurrence.line(),
                        occurrence.column(),
                        "Use canonical Euclid spellings or run --canonicalize first",
                        canonicalSource);
            }
        }
        try {
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens, source, collector);
            DocumentNode ast = parser.parse();
            LatexTranspiler transpiler = new LatexTranspiler(mathMode);
            String output = ast.accept(transpiler);
            return new TranspileResult(output, collector.getAll());
        } catch (EuclidException e) {
            collector.addError("transpile.failure", e.getMessage(), 1, 1, null, canonicalSource.equals(source) ? null : canonicalSource);
            return new TranspileResult(null, collector.getAll());
        } catch (RuntimeException e) {
            collector.addError("transpile.internal", e.getMessage(), 1, 1,
                    "This indicates a compiler contract failure rather than invalid Euclid source",
                    canonicalSource.equals(source) ? null : canonicalSource);
            return new TranspileResult(null, collector.getAll());
        }
    }

    /**
     * Pretty prints the AST with indentation.
     *
     * @param node   The AST node to print
     * @param indent The current indentation level
     * @return The formatted AST string
     */
    private static String prettyPrintAst(AstNode node, int indent) {
        String indentation = "  ".repeat(indent);
        StringBuilder sb = new StringBuilder();

        String nodeStr = node.toString();
        sb.append(indentation).append(nodeStr);

        return sb.toString();
    }

    /**
     * Transpiles a file from .ed to .md format.
     *
     * @param inputPath  The input .ed file path
     * @param outputPath The output .md file path
     * @throws IOException     if an I/O error occurs
     * @throws EuclidException if a transpilation error occurs
     */
    public static void transpileFile(String inputPath, String outputPath) throws IOException, EuclidException {
        transpileFile(inputPath, outputPath, false, MathMode.NONE);
    }

    /**
     * Transpiles a file from .ed to .md format with optional verbose output.
     *
     * @param inputPath  The input .ed file path
     * @param outputPath The output .md file path
     * @param verbose    Whether to print debug information
     * @throws IOException     if an I/O error occurs
     * @throws EuclidException if a transpilation error occurs
     */
    public static void transpileFile(String inputPath, String outputPath, boolean verbose) throws IOException, EuclidException {
        transpileFile(inputPath, outputPath, verbose, MathMode.NONE);
    }

    /**
     * Transpiles a file from .ed to .md format with optional verbose output and math mode.
     *
     * @param inputPath  The input .ed file path
     * @param outputPath The output .md file path
     * @param verbose    Whether to print debug information
     * @param mathMode   The math mode for wrapping output
     * @throws IOException     if an I/O error occurs
     * @throws EuclidException if a transpilation error occurs
     */
    public static void transpileFile(String inputPath, String outputPath, boolean verbose, MathMode mathMode) throws IOException, EuclidException {
        transpileFile(inputPath, outputPath, verbose, mathMode, EuclidAliasHandling.WARN);
    }

    /**
     * Transpiles a file with explicit alias handling.
     */
    public static void transpileFile(
            String inputPath,
            String outputPath,
            boolean verbose,
            MathMode mathMode,
            EuclidAliasHandling aliasHandling) throws IOException, EuclidException {
        // Read input file
        String source = Files.readString(Paths.get(inputPath));

        if (verbose) {
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("INPUT FILE: " + inputPath);
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println(source);
            System.out.println();
        }

        TranspileResult result = transpileWithDiagnostics(source, verbose, mathMode, aliasHandling);
        printDiagnostics(result.diagnostics(), verbose);

        if (result.hasErrors() || result.output() == null) {
            throw new EuclidException(summarizeFailure(result.diagnostics()));
        }

        if (verbose) {
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TRANSPILED OUTPUT");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println(result.output());
            System.out.println();
        }

        // Write output file
        Files.writeString(Paths.get(outputPath), result.output());
    }

    /**
     * Checks a file for Euclid diagnostics without writing output.
     */
    public static TranspileResult checkFile(String inputPath, boolean verbose) throws IOException {
        return checkFile(inputPath, verbose, EuclidAliasHandling.WARN);
    }

    /**
     * Checks a file for Euclid diagnostics with explicit alias handling.
     */
    public static TranspileResult checkFile(String inputPath, boolean verbose, EuclidAliasHandling aliasHandling) throws IOException {
        String source = Files.readString(Paths.get(inputPath));
        return transpileWithDiagnostics(source, verbose, MathMode.NONE, aliasHandling);
    }

    /**
     * Canonicalizes a file's Euclid source without transpiling it.
     */
    public static String canonicalizeFile(String inputPath) throws IOException {
        return canonicalize(Files.readString(Paths.get(inputPath)));
    }

    /**
     * Canonicalizes a file's Euclid source and writes it to a destination path.
     */
    public static void canonicalizeFile(String inputPath, String outputPath) throws IOException {
        Files.writeString(Paths.get(outputPath), canonicalizeFile(inputPath));
    }

    /**
     * Watches a file for changes and automatically retranspiles.
     *
     * @param inputPath  The input .ed file path to watch
     * @param outputPath The output .md file path
     * @throws IOException     if an I/O error occurs
     * @throws EuclidException if a transpilation error occurs
     */
    public static void watchFile(String inputPath, String outputPath) throws IOException, EuclidException {
        watchFile(inputPath, outputPath, false, MathMode.NONE);
    }

    /**
     * Watches a file for changes and automatically retranspiles with optional verbose output.
     *
     * @param inputPath  The input .ed file path to watch
     * @param outputPath The output .md file path
     * @param verbose    Whether to print debug information
     * @throws IOException     if an I/O error occurs
     * @throws EuclidException if a transpilation error occurs
     */
    public static void watchFile(String inputPath, String outputPath, boolean verbose) throws IOException, EuclidException {
        watchFile(inputPath, outputPath, verbose, MathMode.NONE);
    }

    /**
     * Watches a file for changes and automatically retranspiles with optional verbose output and math mode.
     *
     * @param inputPath  The input .ed file path to watch
     * @param outputPath The output .md file path
     * @param verbose    Whether to print debug information
     * @param mathMode   The math mode for wrapping output
     * @throws IOException     if an I/O error occurs
     * @throws EuclidException if a transpilation error occurs
     */
    public static void watchFile(String inputPath, String outputPath, boolean verbose, MathMode mathMode) throws IOException, EuclidException {
        watchFile(inputPath, outputPath, verbose, mathMode, EuclidAliasHandling.WARN);
    }

    /**
     * Watches a file for changes and automatically retranspiles with explicit alias handling.
     */
    public static void watchFile(
            String inputPath,
            String outputPath,
            boolean verbose,
            MathMode mathMode,
            EuclidAliasHandling aliasHandling) throws IOException, EuclidException {
        Path path = Paths.get(inputPath);
        Path dir = path.getParent();
        String fileName = path.getFileName().toString();

        if (dir == null) {
            dir = Paths.get(".");
        }

        // Initial transpilation
        System.out.println("Initial transpilation of " + inputPath + " to " + outputPath + "...");
        transpileFile(inputPath, outputPath, verbose, mathMode, aliasHandling);
        System.out.println("✓ Transpilation complete!");
        System.out.println();
        System.out.println("Watching " + inputPath + " for changes... (Press Ctrl+C to stop)");

        // Set up file watcher
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            dir.register(watchService, ENTRY_MODIFY, ENTRY_CREATE);

            while (true) {
                WatchKey key;
                try {
                    // Wait for file changes
                    key = watchService.take();
                } catch (InterruptedException e) {
                    System.out.println("\nWatch service interrupted. Exiting...");
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == OVERFLOW) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path changedFile = ev.context();

                    // Only retranspile if the watched file changed
                    if (changedFile.toString().equals(fileName)) {
                        System.out.println("\n[" + java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "] File changed, retranspiling...");
                        
                        try {
                            // Small delay to ensure file write is complete
                            Thread.sleep(100);
                            transpileFile(inputPath, outputPath, verbose, mathMode, aliasHandling);
                            System.out.println("✓ Retranspilation complete!");
                        } catch (EuclidException e) {
                            System.err.println("✗ Transpilation Error: " + e.getMessage());
                        } catch (InterruptedException e) {
                            System.out.println("\nWatch service interrupted. Exiting...");
                            return;
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }

    /**
     * Main entry point for the transpiler CLI.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.exit(runCli(args));
    }

    static int runCli(String[] args) {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        // Check for flags
        boolean watchMode = false;
        boolean verboseMode = false;
        boolean checkMode = false;
        boolean canonicalizeMode = false;
        boolean manifestMode = false;
        boolean jsonMode = false;
        EuclidAliasHandling aliasHandling = EuclidAliasHandling.WARN;
        MathMode mathMode = MathMode.NONE;
        String inputFile = null;
        String outputFile = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--watch") || args[i].equals("-w")) {
                watchMode = true;
            } else if (args[i].equals("--verbose") || args[i].equals("-v") ||
                       args[i].equals("--debug") || args[i].equals("-d")) {
                verboseMode = true;
            } else if (args[i].equals("--check") || args[i].equals("-c")) {
                checkMode = true;
            } else if (args[i].equals("--canonicalize")) {
                canonicalizeMode = true;
            } else if (args[i].equals("--manifest")) {
                manifestMode = true;
            } else if (args[i].equals("--json")) {
                jsonMode = true;
            } else if (args[i].equals("--strict-aliases")) {
                aliasHandling = EuclidAliasHandling.ERROR;
            } else if (args[i].equals("--inline") || args[i].equals("-i")) {
                mathMode = MathMode.INLINE;
            } else if (args[i].equals("--display") || args[i].equals("-D")) {
                mathMode = MathMode.DISPLAY;
            } else if (args[i].startsWith("-")) {
                throw new IllegalArgumentException("Unknown option: " + args[i]);
            } else if (inputFile == null) {
                inputFile = args[i];
            } else if (outputFile == null) {
                outputFile = args[i];
            }
        }

        if (!manifestMode && inputFile == null) {
            printUsage();
            return 1;
        }

        try {
            validateCliOptions(watchMode, checkMode, canonicalizeMode, manifestMode, jsonMode, aliasHandling, mathMode, inputFile, outputFile);

            if (manifestMode) {
                if (jsonMode) {
                    System.out.println(serializeManifestJson(capabilityManifest()));
                } else {
                    System.out.print(formatManifest(capabilityManifest()));
                }
                return 0;
            }

            if (checkMode) {
                if (!verboseMode && !jsonMode) {
                    System.out.println("Checking " + inputFile + "...");
                }
                TranspileResult result = checkFile(inputFile, verboseMode, aliasHandling);
                if (jsonMode) {
                    System.out.println(serializeCheckResultJson(result));
                } else {
                    printDiagnostics(result.diagnostics(), verboseMode);
                }
                if (result.hasErrors()) {
                    if (!verboseMode && !jsonMode) {
                        System.err.println("Check failed.");
                    }
                    return 1;
                }
                if (!verboseMode && !jsonMode) {
                    System.out.println("Check passed.");
                }
                return 0;
            }

            if (canonicalizeMode) {
                String canonicalSource = canonicalizeFile(inputFile);
                if (outputFile == null) {
                    System.out.print(canonicalSource);
                } else {
                    Files.writeString(Paths.get(outputFile), canonicalSource);
                    if (!verboseMode) {
                        System.out.println("Canonicalized " + inputFile + " to " + outputFile + ".");
                    }
                }
                return 0;
            }

            if (outputFile == null) {
                outputFile = defaultOutputPath(inputFile);
            }

            if (watchMode) {
                watchFile(inputFile, outputFile, verboseMode, mathMode, aliasHandling);
            } else {
                if (!verboseMode) {
                    System.out.println("Transpiling " + inputFile + " to " + outputFile + "...");
                }
                transpileFile(inputFile, outputFile, verboseMode, mathMode, aliasHandling);
                if (!verboseMode) {
                    System.out.println("Transpilation complete!");
                }
            }
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            return 1;
        } catch (IllegalArgumentException e) {
            System.err.println("Usage Error: " + e.getMessage());
            printUsage();
            return 1;
        } catch (EuclidException e) {
            System.err.println("Transpilation Error: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    private static void printUsage() {
        System.out.println("Euclid Transpiler - Convert .ed files to Markdown with LaTeX");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar target/euclid-transpiler.jar [options] <input.ed> [output.md]");
        System.out.println("  java -jar target/euclid-transpiler.jar --check <input.ed>");
        System.out.println("  java -jar target/euclid-transpiler.jar --canonicalize <input.ed> [output.ed]");
        System.out.println("  java -jar target/euclid-transpiler.jar --manifest [--json]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --watch, -w      Watch mode: automatically retranspile when file changes");
        System.out.println("  --verbose, -v    Verbose mode: show tokenization output and AST");
        System.out.println("  --debug, -d      Debug mode: same as --verbose");
        System.out.println("  --check, -c      Check source and print diagnostics without writing output");
        System.out.println("  --canonicalize   Rewrite compatibility aliases to canonical Euclid spellings");
        System.out.println("  --manifest       Print the Euclid capability manifest");
        System.out.println("  --json           Emit machine-readable JSON for --check or --manifest");
        System.out.println("  --strict-aliases Reject compatibility aliases instead of warning on them");
        System.out.println("  --inline, -i     Wrap output in inline math mode ($...$)");
        System.out.println("  --display, -D    Wrap output in display math mode ($$...$$)");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  input.ed         Input Euclid file");
        System.out.println("  output.md        Output Markdown file (optional, defaults to input.md)");
        System.out.println("  output.ed        Canonicalized Euclid file (optional, defaults to stdout)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar target/euclid-transpiler.jar example.ed");
        System.out.println("  java -jar target/euclid-transpiler.jar example.ed output.md");
        System.out.println("  java -jar target/euclid-transpiler.jar --check example.ed");
        System.out.println("  java -jar target/euclid-transpiler.jar --check --json example.ed");
        System.out.println("  java -jar target/euclid-transpiler.jar --canonicalize example.ed normalized.ed");
        System.out.println("  java -jar target/euclid-transpiler.jar --manifest --json");
        System.out.println("  java -jar target/euclid-transpiler.jar --watch example.ed");
        System.out.println("  java -jar target/euclid-transpiler.jar -v example.ed");
        System.out.println("  java -jar target/euclid-transpiler.jar --inline example.ed");
        System.out.println("  java -jar target/euclid-transpiler.jar --display example.ed");
        System.out.println("  java -jar target/euclid-transpiler.jar --watch --inline --verbose example.ed");
    }

    private static void validateCliOptions(
            boolean watchMode,
            boolean checkMode,
            boolean canonicalizeMode,
            boolean manifestMode,
            boolean jsonMode,
            EuclidAliasHandling aliasHandling,
            MathMode mathMode,
            String inputFile,
            String outputFile) {
        if (checkMode && canonicalizeMode) {
            throw new IllegalArgumentException("--check and --canonicalize are mutually exclusive");
        }
        if (manifestMode && (checkMode || canonicalizeMode || watchMode || inputFile != null || outputFile != null)) {
            throw new IllegalArgumentException("--manifest does not accept input files, output files, or other execution modes");
        }
        if ((checkMode || canonicalizeMode) && watchMode) {
            throw new IllegalArgumentException("--watch only applies to transpilation mode");
        }
        if (checkMode && outputFile != null) {
            throw new IllegalArgumentException("--check does not accept an output file");
        }
        if (checkMode && mathMode != MathMode.NONE) {
            throw new IllegalArgumentException("--check does not support --inline or --display");
        }
        if (jsonMode && !(checkMode || manifestMode)) {
            throw new IllegalArgumentException("--json only applies to --check or --manifest");
        }
        if (canonicalizeMode && aliasHandling == EuclidAliasHandling.ERROR) {
            throw new IllegalArgumentException("--strict-aliases does not apply to --canonicalize");
        }
        if (manifestMode && aliasHandling == EuclidAliasHandling.ERROR) {
            throw new IllegalArgumentException("--strict-aliases does not apply to --manifest");
        }
        if (canonicalizeMode && mathMode != MathMode.NONE) {
            throw new IllegalArgumentException("--canonicalize does not support --inline or --display");
        }
        if (manifestMode && mathMode != MathMode.NONE) {
            throw new IllegalArgumentException("--manifest does not support --inline or --display");
        }
    }

    private static String defaultOutputPath(String inputFile) {
        if (inputFile.endsWith(".ed")) {
            return inputFile.substring(0, inputFile.length() - 3) + ".md";
        }
        return inputFile + ".md";
    }

    private static void printDiagnostics(List<Diagnostic> diagnostics, boolean verbose) {
        for (Diagnostic diagnostic : diagnostics) {
            if (diagnostic.getSeverity() == Diagnostic.Severity.INFO && !verbose) {
                continue;
            }
            System.err.println(diagnostic);
        }
    }

    private static String summarizeFailure(List<Diagnostic> diagnostics) {
        return diagnostics.stream()
                .filter(diagnostic -> diagnostic.getSeverity() == Diagnostic.Severity.ERROR)
                .min(Comparator.comparingInt(Diagnostic::getLine).thenComparingInt(Diagnostic::getColumn))
                .map(diagnostic -> diagnostic.getMessage() + " at " + diagnostic.getLine() + ":" + diagnostic.getColumn())
                .orElse("Compilation failed without a stable diagnostic.");
    }

    private static String formatManifest(EuclidCapabilityManifest manifest) {
        StringBuilder output = new StringBuilder();
        output.append("Euclid Capability Manifest").append(System.lineSeparator());
        output.append("Capabilities: ").append(manifest.capabilities().size()).append(System.lineSeparator()).append(System.lineSeparator());

        for (var capability : manifest.capabilities()) {
            output.append(capability.kind()).append(" ").append(capability.name());
            output.append(" token=").append(capability.tokenType());
            output.append(" alias_policy=").append(capability.aliasPolicy());
            if (!capability.aliases().isEmpty()) {
                output.append(" aliases=").append(String.join(", ", capability.aliases()));
            }
            if (capability.signature() != null) {
                output.append(" signature=").append(capability.signature().label());
            }
            output.append(System.lineSeparator());
        }

        return output.toString();
    }

    private static String serializeCheckResultJson(TranspileResult result) {
        StringBuilder output = new StringBuilder();
        output.append("{");
        output.append("\"ok\":").append(!result.hasErrors()).append(",");
        output.append("\"output\":").append(result.output() == null ? "null" : jsonString(result.output())).append(",");
        output.append("\"diagnostics\":[");
        for (int i = 0; i < result.diagnostics().size(); i++) {
            if (i > 0) {
                output.append(",");
            }
            output.append(serializeDiagnosticJson(result.diagnostics().get(i)));
        }
        output.append("]}");
        return output.toString();
    }

    private static String serializeManifestJson(EuclidCapabilityManifest manifest) {
        StringBuilder output = new StringBuilder();
        output.append("{\"capabilities\":[");
        for (int i = 0; i < manifest.capabilities().size(); i++) {
            if (i > 0) {
                output.append(",");
            }
            var capability = manifest.capabilities().get(i);
            output.append("{");
            output.append("\"name\":").append(jsonString(capability.name())).append(",");
            output.append("\"kind\":").append(jsonString(capability.kind().name())).append(",");
            output.append("\"tokenType\":").append(jsonString(capability.tokenType().name())).append(",");
            output.append("\"aliasPolicy\":").append(jsonString(capability.aliasPolicy().name())).append(",");
            output.append("\"aliases\":").append(serializeStringArrayJson(capability.aliases())).append(",");
            output.append("\"signature\":");
            if (capability.signature() == null) {
                output.append("null");
            } else {
                output.append("{");
                output.append("\"label\":").append(jsonString(capability.signature().label())).append(",");
                output.append("\"parameters\":").append(serializeStringArrayJson(capability.signature().parameters()));
                output.append("}");
            }
            output.append("}");
        }
        output.append("]}");
        return output.toString();
    }

    private static String serializeDiagnosticJson(Diagnostic diagnostic) {
        StringBuilder output = new StringBuilder();
        output.append("{");
        output.append("\"severity\":").append(jsonString(diagnostic.getSeverity().name())).append(",");
        output.append("\"code\":").append(diagnostic.getCode() == null ? "null" : jsonString(diagnostic.getCode())).append(",");
        output.append("\"message\":").append(jsonString(diagnostic.getMessage())).append(",");
        output.append("\"line\":").append(diagnostic.getLine()).append(",");
        output.append("\"column\":").append(diagnostic.getColumn()).append(",");
        output.append("\"suggestion\":").append(diagnostic.getSuggestion() == null ? "null" : jsonString(diagnostic.getSuggestion())).append(",");
        output.append("\"canonicalRewrite\":").append(diagnostic.getCanonicalRewrite() == null ? "null" : jsonString(diagnostic.getCanonicalRewrite()));
        output.append("}");
        return output.toString();
    }

    private static String serializeStringArrayJson(List<String> values) {
        StringBuilder output = new StringBuilder();
        output.append("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                output.append(",");
            }
            output.append(jsonString(values.get(i)));
        }
        output.append("]");
        return output.toString();
    }

    private static String jsonString(String value) {
        StringBuilder output = new StringBuilder();
        output.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> output.append("\\\\");
                case '"' -> output.append("\\\"");
                case '\n' -> output.append("\\n");
                case '\r' -> output.append("\\r");
                case '\t' -> output.append("\\t");
                default -> output.append(c);
            }
        }
        output.append('"');
        return output.toString();
    }
}
