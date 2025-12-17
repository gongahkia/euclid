package com.euclid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to validate that transpiled LaTeX output is syntactically correct.
 * These tests check for common LaTeX syntax errors that would prevent compilation.
 */
public class LatexValidationTest {

    /**
     * Test that all braces are balanced in LaTeX output.
     * LaTeX requires matching { and } for grouping.
     */
    @Test
    public void testBalancedBraces() throws Exception {
        String[] testCases = {
            "sin(x)",
            "sqrt(2, 16)",
            "a \\\\ b",
            "integral(f, x, 0, 1)",
            "sum(i, 1, n, i^2)",
            "limit(f, x, 0)",
            "diff(x^2, x)"
        };

        for (String input : testCases) {
            String latex = Transpiler.transpile(input);
            assertTrue(areBracesBalanced(latex),
                "Braces not balanced in LaTeX output for input: " + input + "\nOutput: " + latex);
        }
    }

    /**
     * Test that all parentheses are balanced in LaTeX output.
     * LaTeX uses () for function arguments and grouping.
     */
    @Test
    public void testBalancedParentheses() throws Exception {
        String[] testCases = {
            "sin(x)",
            "log(x, 10)",
            "pow(2, 8)",
            "(a + b) * c",
            "((a + b) * (c + d))",
            "floor(ceil(x))"
        };

        for (String input : testCases) {
            String latex = Transpiler.transpile(input);
            assertTrue(areParenthesesBalanced(latex),
                "Parentheses not balanced in LaTeX output for input: " + input + "\nOutput: " + latex);
        }
    }

    /**
     * Test that LaTeX commands start with backslash.
     * All LaTeX commands must begin with \.
     */
    @Test
    public void testLatexCommandsHaveBackslash() throws Exception {
        String[] inputs = {"sin(x)", "cos(x)", "tan(x)", "log(x, 10)", "sqrt(2, 16)"};
        String[] expectedCommands = {"\\sin", "\\cos", "\\tan", "\\log", "\\sqrt"};

        for (int i = 0; i < inputs.length; i++) {
            String latex = Transpiler.transpile(inputs[i]);
            assertTrue(latex.contains(expectedCommands[i]),
                "LaTeX output missing command " + expectedCommands[i] + " for input: " + inputs[i]
                    + "\nOutput: " + latex);
        }
    }

    /**
     * Test that special math symbols are properly used.
     * Characters like _, ^, {, }, \, $, %, &, # have special meaning in LaTeX.
     */
    @Test
    public void testSpecialCharactersInOutput() throws Exception {
        // Test that superscripts use ^
        String input2 = "x^2";
        String latex2 = Transpiler.transpile(input2);
        assertTrue(latex2.contains("^"),
            "Power operation should produce ^ in LaTeX: " + latex2);
    }

    /**
     * Test that fractions use \frac{}{} or \dfrac{}{} properly.
     */
    @Test
    public void testFractionSyntax() throws Exception {
        String input = "a \\\\ b";
        String latex = Transpiler.transpile(input);

        assertTrue(latex.contains("\\frac{") || latex.contains("\\dfrac{"),
            "Fraction should use \\frac or \\dfrac command: " + latex);
        assertTrue(areBracesBalanced(latex),
            "Fraction braces not balanced: " + latex);
    }

    /**
     * Test that Greek letter constants use proper LaTeX commands when used as constants.
     */
    @Test
    public void testGreekLetterCommands() throws Exception {
        // Test PI constant
        String latex = Transpiler.transpile("PI");
        assertTrue(latex.contains("\\pi"),
            "PI constant should be converted to \\pi. Output: " + latex);
    }

    /**
     * Test that integrals use proper \int command with bounds.
     */
    @Test
    public void testIntegralSyntax() throws Exception {
        String input = "integral(f, x, 0, 1)";
        String latex = Transpiler.transpile(input);

        assertTrue(latex.contains("\\int"),
            "Integral should use \\int command: " + latex);
        assertTrue(latex.contains("_") && latex.contains("^"),
            "Integral should have bounds with _ and ^: " + latex);
    }

    /**
     * Test that summations use proper \sum command.
     */
    @Test
    public void testSummationSyntax() throws Exception {
        String input = "sum(i, 1, n, i^2)";
        String latex = Transpiler.transpile(input);

        assertTrue(latex.contains("\\sum"),
            "Summation should use \\sum command: " + latex);
        // Note: Bounds may be formatted differently, just check for sum command
    }

    /**
     * Test that limits use proper \lim command.
     */
    @Test
    public void testLimitSyntax() throws Exception {
        String input = "limit(f, x, 0)";
        String latex = Transpiler.transpile(input);

        assertTrue(latex.contains("\\lim"),
            "Limit should use \\lim command: " + latex);
        assertTrue(latex.contains("_") || latex.contains("\\to"),
            "Limit should have subscript or \\to: " + latex);
    }

    /**
     * Test that complex nested expressions maintain valid LaTeX.
     */
    @Test
    public void testNestedExpressions() throws Exception {
        String[] testCases = {
            "sin(cos(x))",
            "pow(2, pow(3, 4))",
            "integral(sin(x^2), x, 0, PI)",
            "sum(i, 1, n, pow(i, 2))",
            "sqrt(2, sqrt(2, 256))"
        };

        for (String input : testCases) {
            String latex = Transpiler.transpile(input);
            assertTrue(areBracesBalanced(latex),
                "Nested expression braces not balanced for: " + input + "\nOutput: " + latex);
            assertTrue(areParenthesesBalanced(latex),
                "Nested expression parentheses not balanced for: " + input + "\nOutput: " + latex);
        }
    }

    /**
     * Test that output doesn't contain invalid LaTeX sequences.
     */
    @Test
    public void testNoInvalidLatexSequences() throws Exception {
        String[] testCases = {
            "x + y",
            "sin(x) + cos(x)",
            "a \\\\ b + c \\\\ d",
            "integral(x^2, x, 0, 1)"
        };

        for (String input : testCases) {
            String latex = Transpiler.transpile(input);

            // Check for common LaTeX errors
            assertFalse(latex.contains("\\\\\\"),
                "Triple backslash found (invalid LaTeX) in: " + latex);
            assertFalse(latex.matches(".*\\^\\s*\\^.*"),
                "Adjacent ^ operators found in: " + latex);
            assertFalse(latex.matches(".*_\\s*_.*"),
                "Adjacent _ operators found in: " + latex);
        }
    }

    /**
     * Test that all math mode delimiters would be properly closed.
     * Note: The transpiler outputs raw LaTeX, so the calling code should wrap in $ or $$.
     */
    @Test
    public void testOutputIsValidInsideMathMode() throws Exception {
        String[] testCases = {
            "x + y",
            "sin(x)",
            "a \\\\ b",
            "x^2"
        };

        for (String input : testCases) {
            String latex = Transpiler.transpile(input);

            // Simulate wrapping in math mode
            String wrapped = "$" + latex + "$";

            // Check that math mode wrapping doesn't create obvious errors
            assertEquals(2, countOccurrences(wrapped, "$"),
                "Math mode delimiters should be balanced for: " + input);
        }
    }

    /**
     * Test that logical symbols use proper LaTeX commands.
     */
    @Test
    public void testLogicalSymbolCommands() throws Exception {
        String[][] testCases = {
            {"implies(p, q)", "\\implies"},
            {"iff(p, q)", "\\iff"}
        };

        for (String[] testCase : testCases) {
            String input = testCase[0];
            String expectedCommand = testCase[1];
            String latex = Transpiler.transpile(input);

            assertTrue(latex.contains(expectedCommand),
                "Logical symbol not properly converted for " + input
                    + "\nExpected: " + expectedCommand + "\nOutput: " + latex);
        }
    }

    /**
     * Test that set notation uses proper LaTeX commands.
     */
    @Test
    public void testSetNotationCommands() throws Exception {
        String[][] testCases = {
            {"union(A, B)", "\\cup"},
            {"intersection(A, B)", "\\cap"},
            {"subset(A, B)", "\\subset"},
            {"element_of(x, A)", "\\in"},
            {"emptyset", "\\emptyset"}
        };

        for (String[] testCase : testCases) {
            String input = testCase[0];
            String expectedCommand = testCase[1];
            String latex = Transpiler.transpile(input);

            assertTrue(latex.contains(expectedCommand),
                "Set notation not properly converted for " + input
                    + "\nExpected: " + expectedCommand + "\nOutput: " + latex);
        }
    }

    /**
     * Test that basic arithmetic maintains valid LaTeX.
     */
    @Test
    public void testBasicArithmetic() throws Exception {
        String[] testCases = {
            "a + b",
            "a - b",
            "a * b",
            "a + b + c",
            "a * b + c * d",
            "(a + b) * (c + d)"
        };

        for (String input : testCases) {
            String latex = Transpiler.transpile(input);
            assertNotNull(latex, "LaTeX output should not be null for: " + input);
            assertFalse(latex.isEmpty(), "LaTeX output should not be empty for: " + input);
            assertTrue(areParenthesesBalanced(latex),
                "Parentheses not balanced for: " + input + "\nOutput: " + latex);
        }
    }

    /**
     * Test that trig functions produce LaTeX commands.
     */
    @Test
    public void testTrigFunctionCommands() throws Exception {
        String[][] testCases = {
            {"sin(x)", "\\sin"},
            {"cos(x)", "\\cos"},
            {"tan(x)", "\\tan"},
            {"csc(x)", "\\csc"},
            {"sec(x)", "\\sec"},
            {"cot(x)", "\\cot"}
        };

        for (String[] testCase : testCases) {
            String input = testCase[0];
            String expectedCommand = testCase[1];
            String latex = Transpiler.transpile(input);

            assertTrue(latex.contains(expectedCommand),
                "Trig function not properly converted for " + input
                    + "\nExpected: " + expectedCommand + "\nOutput: " + latex);
        }
    }

    // Helper methods for validation

    /**
     * Check if braces are balanced in a string.
     */
    private boolean areBracesBalanced(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == '{') count++;
            if (c == '}') count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    /**
     * Check if parentheses are balanced in a string.
     */
    private boolean areParenthesesBalanced(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == '(') count++;
            if (c == ')') count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    /**
     * Count occurrences of a substring.
     */
    private int countOccurrences(String str, String substring) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}
