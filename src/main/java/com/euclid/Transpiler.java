package com.euclid;

import com.euclid.ast.DocumentNode;
import com.euclid.exception.EuclidException;
import com.euclid.lexer.Lexer;
import com.euclid.parser.Parser;
import com.euclid.token.Token;
import com.euclid.transpiler.LatexTranspiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
     * Main entry point for the transpiler CLI.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        try {
            String inputFile = args[0];
            String outputFile;

            if (args.length >= 2) {
                outputFile = args[1];
            } else {
                // Default: replace .ed with .md
                if (inputFile.endsWith(".ed")) {
                    outputFile = inputFile.substring(0, inputFile.length() - 3) + ".md";
                } else {
                    outputFile = inputFile + ".md";
                }
            }

            System.out.println("Transpiling " + inputFile + " to " + outputFile + "...");
            transpileFile(inputFile, outputFile);
            System.out.println("Transpilation complete!");

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
        System.out.println("  java -jar euclid-transpiler.jar <input.ed> [output.md]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  input.ed   - Input Euclid file");
        System.out.println("  output.md  - Output Markdown file (optional, defaults to input.md)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar euclid-transpiler.jar example.ed");
        System.out.println("  java -jar euclid-transpiler.jar example.ed output.md");
    }
}
