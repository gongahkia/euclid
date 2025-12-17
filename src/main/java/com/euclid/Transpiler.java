package com.euclid;

import com.euclid.ast.AstNode;
import com.euclid.ast.DocumentNode;
import com.euclid.exception.EuclidException;
import com.euclid.lexer.Lexer;
import com.euclid.parser.Parser;
import com.euclid.processor.MixedContentProcessor;
import com.euclid.token.Token;
import com.euclid.transpiler.LatexTranspiler;
import com.euclid.transpiler.MathMode;

import java.io.IOException;
import java.nio.file.*;
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
        return transpile(source, verbose, mathMode, false);
    }

    /**
     * Transpiles Euclid source code to LaTeX/Markdown with full options.
     *
     * @param source    The Euclid source code
     * @param verbose   Whether to print debug information
     * @param mathMode  The math mode for wrapping output
     * @param mixedMode Whether to use mixed content mode (auto-detect inline math)
     * @return The transpiled LaTeX/Markdown
     * @throws EuclidException if an error occurs during transpilation
     */
    public static String transpile(String source, boolean verbose, MathMode mathMode, boolean mixedMode) throws EuclidException {
        // If mixed mode, use the mixed content processor
        if (mixedMode) {
            return MixedContentProcessor.processDocument(source);
        }

        // Otherwise, use regular transpilation
        // Lex: Convert source to tokens
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
        transpileFile(inputPath, outputPath, verbose, mathMode, false);
    }

    /**
     * Transpiles a file from .ed to .md format with full options.
     *
     * @param inputPath  The input .ed file path
     * @param outputPath The output .md file path
     * @param verbose    Whether to print debug information
     * @param mathMode   The math mode for wrapping output
     * @param mixedMode  Whether to use mixed content mode
     * @throws IOException     if an I/O error occurs
     * @throws EuclidException if a transpilation error occurs
     */
    public static void transpileFile(String inputPath, String outputPath, boolean verbose, MathMode mathMode, boolean mixedMode) throws IOException, EuclidException {
        // Read input file
        String source = Files.readString(Paths.get(inputPath));

        if (verbose) {
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("INPUT FILE: " + inputPath);
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println(source);
            System.out.println();
        }

        // Transpile
        String result = transpile(source, verbose, mathMode, mixedMode);

        if (verbose) {
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("TRANSPILED OUTPUT");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println(result);
            System.out.println();
        }

        // Write output file
        Files.writeString(Paths.get(outputPath), result);
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
        watchFile(inputPath, outputPath, verbose, mathMode, false);
    }

    /**
     * Watches a file for changes and automatically retranspiles with full options.
     *
     * @param inputPath  The input .ed file path to watch
     * @param outputPath The output .md file path
     * @param verbose    Whether to print debug information
     * @param mathMode   The math mode for wrapping output
     * @param mixedMode  Whether to use mixed content mode
     * @throws IOException     if an I/O error occurs
     * @throws EuclidException if a transpilation error occurs
     */
    public static void watchFile(String inputPath, String outputPath, boolean verbose, MathMode mathMode, boolean mixedMode) throws IOException, EuclidException {
        Path path = Paths.get(inputPath);
        Path dir = path.getParent();
        String fileName = path.getFileName().toString();

        if (dir == null) {
            dir = Paths.get(".");
        }

        // Initial transpilation
        System.out.println("Initial transpilation of " + inputPath + " to " + outputPath + "...");
        transpileFile(inputPath, outputPath, verbose, mathMode, mixedMode);
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
                            transpileFile(inputPath, outputPath, verbose, mathMode, mixedMode);
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
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        // Check for flags
        boolean watchMode = false;
        boolean verboseMode = false;
        boolean mixedMode = false;
        MathMode mathMode = MathMode.NONE;
        String inputFile = null;
        String outputFile = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--watch") || args[i].equals("-w")) {
                watchMode = true;
            } else if (args[i].equals("--verbose") || args[i].equals("-v") ||
                       args[i].equals("--debug") || args[i].equals("-d")) {
                verboseMode = true;
            } else if (args[i].equals("--mixed") || args[i].equals("-m")) {
                mixedMode = true;
            } else if (args[i].equals("--inline") || args[i].equals("-i")) {
                mathMode = MathMode.INLINE;
            } else if (args[i].equals("--display") || args[i].equals("-D")) {
                mathMode = MathMode.DISPLAY;
            } else if (inputFile == null) {
                inputFile = args[i];
            } else if (outputFile == null) {
                outputFile = args[i];
            }
        }

        if (inputFile == null) {
            printUsage();
            System.exit(1);
        }

        // Determine output file
        if (outputFile == null) {
            // Default: replace .ed with .md
            if (inputFile.endsWith(".ed")) {
                outputFile = inputFile.substring(0, inputFile.length() - 3) + ".md";
            } else {
                outputFile = inputFile + ".md";
            }
        }

        try {
            if (watchMode) {
                watchFile(inputFile, outputFile, verboseMode, mathMode, mixedMode);
            } else {
                if (!verboseMode) {
                    System.out.println("Transpiling " + inputFile + " to " + outputFile + "...");
                }
                transpileFile(inputFile, outputFile, verboseMode, mathMode, mixedMode);
                if (!verboseMode) {
                    System.out.println("Transpilation complete!");
                }
            }
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            System.exit(1);
        } catch (EuclidException e) {
            System.err.println("Transpilation Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Euclid Transpiler - Convert .ed files to Markdown with LaTeX");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar euclid-transpiler.jar [options] <input.ed> [output.md]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --watch, -w      Watch mode: automatically retranspile when file changes");
        System.out.println("  --verbose, -v    Verbose mode: show tokenization output and AST");
        System.out.println("  --debug, -d      Debug mode: same as --verbose");
        System.out.println("  --mixed, -m      Mixed mode: auto-detect and transpile inline math in prose");
        System.out.println("  --inline, -i     Wrap output in inline math mode ($...$)");
        System.out.println("  --display, -D    Wrap output in display math mode ($$...$$)");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  input.ed         Input Euclid file");
        System.out.println("  output.md        Output Markdown file (optional, defaults to input.md)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar euclid-transpiler.jar example.ed");
        System.out.println("  java -jar euclid-transpiler.jar example.ed output.md");
        System.out.println("  java -jar euclid-transpiler.jar --watch example.ed");
        System.out.println("  java -jar euclid-transpiler.jar -v example.ed");
        System.out.println("  java -jar euclid-transpiler.jar --inline example.ed");
        System.out.println("  java -jar euclid-transpiler.jar --display example.ed");
        System.out.println("  java -jar euclid-transpiler.jar --watch --inline --verbose example.ed");
    }
}
