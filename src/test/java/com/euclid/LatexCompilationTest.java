package com.euclid;

import com.euclid.exception.EuclidException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that verify transpiled LaTeX actually compiles with a real LaTeX processor.
 * These tests require pdflatex to be installed on the system.
 */
@EnabledIf("isLatexAvailable")
public class LatexCompilationTest {

    private static boolean latexAvailable = false;

    /**
     * Check if pdflatex is available on the system.
     */
    @BeforeAll
    public static void checkLatexAvailability() {
        try {
            Process process = new ProcessBuilder("pdflatex", "--version")
                    .redirectErrorStream(true)
                    .start();

            int exitCode = process.waitFor();
            latexAvailable = (exitCode == 0);

            if (latexAvailable) {
                System.out.println("✓ pdflatex found - LaTeX compilation tests enabled");
            } else {
                System.out.println("⚠ pdflatex not found - LaTeX compilation tests skipped");
            }
        } catch (IOException | InterruptedException e) {
            latexAvailable = false;
            System.out.println("⚠ pdflatex not available - LaTeX compilation tests skipped");
        }
    }

    /**
     * JUnit condition method to enable tests only if LaTeX is available.
     */
    public static boolean isLatexAvailable() {
        return latexAvailable;
    }

    /**
     * Compiles a LaTeX document and returns true if successful.
     */
    private boolean compileLatex(String latexContent, String testName) throws IOException, InterruptedException {
        // Create temporary directory for LaTeX compilation
        Path tempDir = Files.createTempDirectory("euclid-latex-test-");
        Path texFile = tempDir.resolve(testName + ".tex");

        try {
            // Write LaTeX document
            Files.writeString(texFile, latexContent);

            // Run pdflatex
            ProcessBuilder pb = new ProcessBuilder(
                    "pdflatex",
                    "-interaction=nonstopmode",
                    "-output-directory=" + tempDir.toString(),
                    texFile.toString()
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Capture output for debugging
            List<String> output = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.err.println("LaTeX compilation failed for test: " + testName);
                System.err.println("Output:");
                output.forEach(System.err::println);
                return false;
            }

            // Check if PDF was created
            Path pdfFile = tempDir.resolve(testName + ".pdf");
            boolean success = Files.exists(pdfFile);

            if (!success) {
                System.err.println("PDF file not created for test: " + testName);
            }

            return success;

        } finally {
            // Clean up temporary files
            deleteDirectory(tempDir);
        }
    }

    /**
     * Recursively deletes a directory and its contents.
     */
    private void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Reverse order to delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Ignore deletion errors in tests
                        }
                    });
        }
    }

    /**
     * Creates a minimal LaTeX document with the given math content.
     */
    private String createLatexDocument(String mathContent) {
        return """
                \\documentclass{article}
                \\usepackage{amsmath}
                \\usepackage{amssymb}
                \\begin{document}

                """ + mathContent + """

                \\end{document}
                """;
    }

    /**
     * Helper to transpile Euclid and wrap in LaTeX document.
     */
    private String transpileAndWrap(String euclidCode) throws EuclidException {
        String latex = Transpiler.transpile(euclidCode);
        return createLatexDocument("$" + latex + "$");
    }

    @Test
    public void testBasicArithmetic() throws Exception {
        String euclidCode = "2 + 2";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "basic-arithmetic"),
                "Basic arithmetic should compile successfully");
    }

    @Test
    public void testConstants() throws Exception {
        String euclidCode = "PI + E + I";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "constants"),
                "Mathematical constants should compile successfully");
    }

    @Test
    public void testPowerFunction() throws Exception {
        String euclidCode = "pow(x, 2) + pow(y, 3)";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "power-function"),
                "Power functions should compile successfully");
    }

    @Test
    public void testTrigFunctions() throws Exception {
        String euclidCode = "sin(x) + cos(y) + tan(z)";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "trig-functions"),
                "Trigonometric functions should compile successfully");
    }

    @Test
    public void testIntegral() throws Exception {
        String euclidCode = "integral(sin(x), x, 0, PI)";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "integral"),
                "Integrals should compile successfully");
    }

    @Test
    public void testSummation() throws Exception {
        String euclidCode = "sum(pow(i, 2), i, 1, n)";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "summation"),
                "Summations should compile successfully");
    }

    @Test
    public void testFraction() throws Exception {
        String euclidCode = "(x + 1) \\\\ (y - 1)";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "fraction"),
                "Fractions should compile successfully");
    }

    @Test
    public void testSquareRoot() throws Exception {
        String euclidCode = "sqrt(2, 16)";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "square-root"),
                "Square roots should compile successfully");
    }

    @Test
    public void testMatrix() throws Exception {
        String euclidCode = "matrix([[1, 2], [3, 4]])";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "matrix"),
                "Matrices should compile successfully");
    }

    @Test
    public void testVector() throws Exception {
        String euclidCode = "vector([1, 2, 3])";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "vector"),
                "Vectors should compile successfully");
    }

    @Test
    public void testComplexExpression() throws Exception {
        String euclidCode = "integral(pow(x, 2) + sin(x), x, 0, PI) + sum(pow(i, 2), i, 1, n)";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "complex-expression"),
                "Complex expressions should compile successfully");
    }

    @Test
    public void testGreekLetters() throws Exception {
        String euclidCode = "ALPHA + BETA + GAMMA + DELTA + THETA";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "greek-letters"),
                "Greek letters should compile successfully");
    }

    @Test
    public void testLogicSymbols() throws Exception {
        String euclidCode = "forall AND exists OR NOT";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "logic-symbols"),
                "Logic symbols should compile successfully");
    }

    @Test
    public void testSetOperations() throws Exception {
        String euclidCode = "union AND intersection AND subset";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "set-operations"),
                "Set operations should compile successfully");
    }

    @Test
    public void testLimit() throws Exception {
        String euclidCode = "limit(sin(x) \\\\ x, x, 0)";
        String latexDoc = transpileAndWrap(euclidCode);
        assertTrue(compileLatex(latexDoc, "limit"),
                "Limits should compile successfully");
    }
}
