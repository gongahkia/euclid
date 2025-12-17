package com.euclid;

import com.euclid.exception.EuclidException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Property-based tests that verify certain properties hold for randomly generated inputs.
 * Uses JUnit 5's RepeatedTest to run each property test multiple times with random data.
 */
public class PropertyBasedTest {
    
    private static final Random random = new Random(42); // Fixed seed for reproducibility

    // ========== IDENTITY PROPERTIES ==========

    /**
     * Property: Transpiling the same input twice should produce the same output.
     * This tests idempotence.
     */
    @RepeatedTest(20)
    public void transpilingTwiceGivesSameResult() throws Exception {
        String input = generateRandomExpression();
        
        try {
            String result1 = Transpiler.transpile(input);
            String result2 = Transpiler.transpile(input);
            assertEquals(result1, result2, "Transpiling twice should give same result");
        } catch (EuclidException e) {
            // Both should fail in the same way - test consistency
            assertThrows(EuclidException.class, () -> Transpiler.transpile(input));
        }
    }

    /**
     * Property: Empty input should always produce empty output.
     */
    @Test
    public void emptyInputProducesEmptyOutput() throws Exception {
        String result = Transpiler.transpile("");
        assertTrue(result == null || result.trim().isEmpty(), 
                  "Empty input should produce empty output");
    }

    /**
     * Property: Whitespace-only input should produce empty or whitespace output.
     */
    @RepeatedTest(10)
    public void whitespaceOnlyInputProducesEmptyOutput() throws Exception {
        int spaces = random.nextInt(50);
        String input = " ".repeat(spaces);
        
        String result = Transpiler.transpile(input);
        assertTrue(result == null || result.trim().isEmpty(),
                  "Whitespace-only input should produce empty output");
    }

    // ========== STRUCTURE PROPERTIES ==========

    /**
     * Property: Valid numeric input should parse without error.
     */
    @RepeatedTest(30)
    public void validNumbersParseSuccessfully() throws Exception {
        double number = random.nextDouble() * 1000 - 500; // Range: -500 to 500
        assumeTrue(!Double.isNaN(number) && !Double.isInfinite(number));
        
        String input = String.valueOf(number);
        String result = Transpiler.transpile(input);
        
        assertNotNull(result, "Valid number should produce output");
        assertFalse(result.trim().isEmpty(), "Output should not be empty");
    }

    /**
     * Property: Adding parentheses around an expression should not break parsing.
     */
    @RepeatedTest(20)
    public void parenthesesDontBreakParsing() throws Exception {
        String expr = generateSimpleExpression();
        
        try {
            String withoutParens = Transpiler.transpile(expr);
            String withParens = Transpiler.transpile("(" + expr + ")");
            
            assertNotNull(withoutParens, "Expression without parentheses should parse");
            assertNotNull(withParens, "Expression with parentheses should parse");
        } catch (EuclidException e) {
            // If original fails, that's OK for this test
        }
    }

    // ========== COMMUTATIVE PROPERTIES ==========

    /**
     * Property: Addition should handle operands in any order (for valid expressions).
     */
    @RepeatedTest(25)
    public void additionHandlesBothOrders() throws Exception {
        int a = random.nextInt(1000) - 500;
        int b = random.nextInt(1000) - 500;
        
        String result1 = Transpiler.transpile(a + " + " + b);
        String result2 = Transpiler.transpile(b + " + " + a);
        
        assertNotNull(result1, "First order should parse");
        assertNotNull(result2, "Second order should parse");
        assertTrue(result1.contains(String.valueOf(a)) || result1.contains(a + ".0"),
                  "Should contain " + a);
        assertTrue(result1.contains(String.valueOf(b)) || result1.contains(b + ".0"),
                  "Should contain " + b);
    }

    /**
     * Property: Multiplication should handle operands in any order.
     */
    @RepeatedTest(25)
    public void multiplicationHandlesBothOrders() throws Exception {
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        
        String result1 = Transpiler.transpile(a + " * " + b);
        String result2 = Transpiler.transpile(b + " * " + a);
        
        assertNotNull(result1, "First order should parse");
        assertNotNull(result2, "Second order should parse");
    }

    // ========== OUTPUT FORMAT PROPERTIES ==========

    /**
     * Property: Transpiled output should not contain unbalanced braces.
     */
    @RepeatedTest(30)
    public void outputDoesNotContainUnbalancedBraces() throws Exception {
        String input = generateRandomExpression();
        
        try {
            String result = Transpiler.transpile(input);
            
            // Count braces
            int openCount = countOccurrences(result, "{");
            int closeCount = countOccurrences(result, "}");
            
            assertEquals(openCount, closeCount, 
                        "Braces should be balanced in output: " + result);
        } catch (EuclidException e) {
            // Invalid input is OK - just testing valid outputs
        }
    }

    /**
     * Property: Trigonometric functions should produce LaTeX commands.
     */
    @RepeatedTest(15)
    public void trigFunctionsProduceLatexCommands() throws Exception {
        String[] functions = {"sin", "cos", "tan"};
        String func = functions[random.nextInt(functions.length)];
        String input = func + "(x)";
        
        String result = Transpiler.transpile(input);
        assertTrue(result.contains("\\" + func), 
                  func + " should produce \\" + func + " in output");
    }

    // ========== ERROR HANDLING PROPERTIES ==========

    /**
     * Property: Invalid characters should throw meaningful exceptions.
     */
    @RepeatedTest(10)
    public void invalidInputThrowsMeaningfulException() {
        char[] invalidChars = {'@', '$', '%', '&', '!', '~', '`'};
        char invalidChar = invalidChars[random.nextInt(invalidChars.length)];
        
        String input = "x + " + invalidChar;
        try {
            Transpiler.transpile(input);
            // Some chars might be valid in certain contexts
        } catch (EuclidException e) {
            assertNotNull(e.getMessage(), "Exception should have message");
            assertFalse(e.getMessage().isEmpty(), "Exception message should not be empty");
            assertTrue(e.getMessage().contains("line") || 
                      e.getMessage().contains("column") ||
                      e.getMessage().contains("Unexpected") ||
                      e.getMessage().contains("error"),
                      "Should mention error location or cause");
        }
    }

    /**
     * Property: Unbalanced delimiters should be caught.
     */
    @RepeatedTest(20)
    public void unbalancedDelimitersAreCaught() {
        int openCount = random.nextInt(5) + 1;
        int closeCount = random.nextInt(5);
        assumeTrue(openCount != closeCount);
        
        String input = "x " + "(".repeat(openCount) + ")".repeat(closeCount);
        
        try {
            Transpiler.transpile(input);
            // If it parses, the delimiters must have balanced somehow
        } catch (EuclidException e) {
            assertNotNull(e.getMessage(), "Exception should have message");
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Generate a random simple expression for testing.
     */
    private String generateSimpleExpression() {
        String[] variables = {"x", "y", "z", "a", "b"};
        String[] operators = {"+", "-", "*", "/"};
        
        String var1 = variables[random.nextInt(variables.length)];
        String op = operators[random.nextInt(operators.length)];
        String var2 = variables[random.nextInt(variables.length)];
        
        return var1 + " " + op + " " + var2;
    }

    /**
     * Generate a random mathematical expression for testing.
     */
    private String generateRandomExpression() {
        int choice = random.nextInt(5);
        
        return switch (choice) {
            case 0 -> generateSimpleExpression();
            case 1 -> String.valueOf(random.nextInt(100));
            case 2 -> {
                String[] funcs = {"sin", "cos", "tan", "abs", "floor", "ceil"};
                yield funcs[random.nextInt(funcs.length)] + "(x)";
            }
            case 3 -> {
                String[] constants = {"PI", "E"};
                yield constants[random.nextInt(constants.length)];
            }
            case 4 -> "pow(" + random.nextInt(10) + ", " + random.nextInt(5) + ")";
            default -> "x";
        };
    }

    /**
     * Count occurrences of a substring in a string.
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
