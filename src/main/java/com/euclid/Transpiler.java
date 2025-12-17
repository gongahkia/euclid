package com.euclid;

import com.euclid.ast.DocumentNode;
import com.euclid.exception.EuclidException;
import com.euclid.lexer.Lexer;
import com.euclid.parser.Parser;
import com.euclid.token.Token;
import com.euclid.transpiler.LatexTranspiler;

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
        // Lex: Convert source to tokens
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Parse: Convert tokens to AST
        Parser parser = new Parser(tokens);
        DocumentNode ast = parser.parse();

        // Transpile: Convert AST to LaTeX
        LatexTranspiler transpiler = new LatexTranspiler();
        return ast.accept(transpiler);
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
        // Read input file
        String source = Files.readString(Paths.get(inputPath));

        // Transpile
        String result = transpile(source);

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
        Path path = Paths.get(inputPath);
        Path dir = path.getParent();
        String fileName = path.getFileName().toString();

        if (dir == null) {
            dir = Paths.get(".");
        }

        // Initial transpilation
        System.out.println("Initial transpilation of " + inputPath + " to " + outputPath + "...");
        transpileFile(inputPath, outputPath);
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
                            transpileFile(inputPath, outputPath);
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

        // Check for --watch flag
        boolean watchMode = false;
        String inputFile = null;
        String outputFile = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--watch") || args[i].equals("-w")) {
                watchMode = true;
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
                watchFile(inputFile, outputFile);
            } else {
                System.out.println("Transpiling " + inputFile + " to " + outputFile + "...");
                transpileFile(inputFile, outputFile);
                System.out.println("Transpilation complete!");
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
        System.out.println("  java -jar euclid-transpiler.jar [--watch] <input.ed> [output.md]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --watch, -w  Watch mode: automatically retranspile when file changes");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  input.ed     Input Euclid file");
        System.out.println("  output.md    Output Markdown file (optional, defaults to input.md)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar euclid-transpiler.jar example.ed");
        System.out.println("  java -jar euclid-transpiler.jar example.ed output.md");
        System.out.println("  java -jar euclid-transpiler.jar --watch example.ed");
        System.out.println("  java -jar euclid-transpiler.jar -w example.ed output.md");
    }
}
