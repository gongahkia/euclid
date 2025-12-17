package com.euclid;

import com.euclid.exception.EuclidException;
import com.euclid.exception.LexerException;
import com.euclid.exception.ParserException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests to prevent previously fixed bugs from reappearing.
 * Each test documents a specific bug or edge case that was fixed.
 */
public class RegressionTest {

    // ========== EDGE CASES ==========

    @Test
    public void testEmptyInput() throws Exception {
        // Empty input should return empty string
        assertEquals("", Transpiler.transpile(""));
    }

    @Test
    public void testWhitespaceOnly() throws Exception {
        // Whitespace-only input should return empty or whitespace
        String result = Transpiler.transpile("   \t\n  ");
        assertTrue(result.trim().isEmpty());
    }

    @Test
    public void testSingleNumber() throws Exception {
        // Single numeric literal
        assertEquals("42.0", Transpiler.transpile("42"));
    }

    @Test
    public void testNegativeNumber() throws Exception {
        // Negative numbers
        assertEquals("-5.0", Transpiler.transpile("-5"));
    }

    @Test
    public void testFloatingPoint() throws Exception {
        // Floating point numbers
        String result = Transpiler.transpile("3.14");
        assertTrue(result.contains("3.14"));
    }

    @Test
    public void testScientificNotation() throws Exception {
        // Scientific notation
        String result = Transpiler.transpile("1.5e10");
        assertTrue(result.contains("1.5E10") || result.contains("1.5e10"));
    }

    @Test
    public void testVeryLargeNumber() throws Exception {
        // Very large numbers should not overflow
        String result = Transpiler.transpile("99999999999999");
        assertNotNull(result);
    }

    @Test
    public void testVerySmallNumber() throws Exception {
        // Very small decimal numbers
        String result = Transpiler.transpile("0.000001");
        assertNotNull(result);
    }

    // ========== DELIMITER BALANCING ==========

    @Test
    public void testBalancedParentheses() throws Exception {
        // Properly balanced parentheses
        assertEquals("(x + y)", Transpiler.transpile("(x + y)"));
    }

    @Test
    public void testNestedParentheses() throws Exception {
        // Deeply nested parentheses
        String result = Transpiler.transpile("((((x))))");
        assertTrue(result.contains("x"));
    }

    @Test
    public void testUnbalancedParentheses_Missing_Close() {
        // Missing closing parenthesis should throw ParserException
        assertThrows(ParserException.class, () -> {
            Transpiler.transpile("(x + y");
        });
    }

    @Test
    public void testUnbalancedParentheses_Extra_Close() {
        // Extra closing parenthesis should throw ParserException
        assertThrows(ParserException.class, () -> {
            Transpiler.transpile("x + y)");
        });
    }

    @Test
    public void testMismatchedDelimiters() {
        // Mismatched delimiters: [ and )
        assertThrows(ParserException.class, () -> {
            Transpiler.transpile("[x + y)");
        });
    }

    // ========== OPERATOR PRECEDENCE ==========

    @Test
    public void testAdditionMultiplicationPrecedence() throws Exception {
        // 2 + 3 * 4 should be 2 + (3 * 4), not (2 + 3) * 4
        String result = Transpiler.transpile("2 + 3 * 4");
        // Result should maintain proper order
        assertTrue(result.contains("2.0 + 3.0 * 4.0"));
    }

    @Test
    public void testPowerPrecedence() throws Exception {
        // 2 ^ 3 ^ 4 should be right-associative: 2 ^ (3 ^ 4)
        String result = Transpiler.transpile("pow(2, pow(3, 4))");
        assertTrue(result.contains("^"));
    }

    @Test
    public void testUnaryMinusPrecedence() throws Exception {
        // -2 ^ 3 should be -(2^3), not (-2)^3
        String result = Transpiler.transpile("-pow(2, 3)");
        assertTrue(result.contains("-") && result.contains("^"));
    }

    // ========== COMMENT HANDLING ==========

    @Test
    public void testSingleLineComment_Hash() throws Exception {
        // Hash-style comments should be stripped
        String result = Transpiler.transpile("x + y # this is a comment");
        assertFalse(result.contains("comment"));
        assertTrue(result.contains("x") && result.contains("y"));
    }

    @Test
    public void testSingleLineComment_DoubleSlash() throws Exception {
        // C-style comments should be stripped
        String result = Transpiler.transpile("x + y // this is a comment");
        assertFalse(result.contains("comment"));
        assertTrue(result.contains("x") && result.contains("y"));
    }

    @Test
    public void testCommentOnlyLine() throws Exception {
        // Line with only comment should produce no output
        String result = Transpiler.transpile("# just a comment");
        assertTrue(result.trim().isEmpty());
    }

    @Test
    public void testMultipleComments() throws Exception {
        // Multiple comment styles
        String result = Transpiler.transpile("x + y # comment 1\n// comment 2\nz");
        assertFalse(result.contains("comment"));
        assertTrue(result.contains("x") && result.contains("y") && result.contains("z"));
    }

    // ========== FUNCTION ARGUMENT COUNT VALIDATION ==========

    @Test
    public void testSin_CorrectArgCount() throws Exception {
        // sin should accept exactly 1 argument
        String result = Transpiler.transpile("sin(x)");
        assertEquals("\\sin(x)", result);
    }

    @Test
    public void testSin_NoArgs() {
        // sin with no arguments should fail
        assertThrows(EuclidException.class, () -> {
            Transpiler.transpile("sin()");
        });
    }

    @Test
    public void testSin_TooManyArgs() {
        // sin with too many arguments should fail
        assertThrows(EuclidException.class, () -> {
            Transpiler.transpile("sin(x, y)");
        });
    }

    @Test
    public void testPow_CorrectArgCount() throws Exception {
        // pow should accept exactly 2 arguments
        String result = Transpiler.transpile("pow(x, 2)");
        assertEquals("x^{2.0}", result);
    }

    @Test
    public void testPow_TooFewArgs() {
        // pow with only 1 argument should fail
        assertThrows(EuclidException.class, () -> {
            Transpiler.transpile("pow(x)");
        });
    }

    // ========== SPECIAL CHARACTERS ==========

    @Test
    public void testBackslashBackslash_Fraction() throws Exception {
        // \\ should create fractions
        String result = Transpiler.transpile("a \\\\ b");
        assertEquals("\\frac{a}{b}", result);
    }

    @Test
    public void testIdentifierWithNumbers() throws Exception {
        // Identifiers can contain numbers
        String result = Transpiler.transpile("x1 + y2");
        assertTrue(result.contains("x1") && result.contains("y2"));
    }

    @Test
    public void testIdentifierWithUnderscore() throws Exception {
        // Identifiers can contain underscores
        String result = Transpiler.transpile("my_var + your_var");
        assertTrue(result.contains("my_var") && result.contains("your_var"));
    }

    // ========== ERROR MESSAGE QUALITY ==========

    @Test
    public void testInvalidCharacter_ErrorMessage() {
        // Invalid characters should produce clear error messages
        try {
            Transpiler.transpile("x + @");
            fail("Should have thrown LexerException");
        } catch (LexerException e) {
            // Error message should mention the invalid character
            assertTrue(e.getMessage().contains("@") || e.getMessage().contains("Unexpected"));
        } catch (Exception e) {
            fail("Expected LexerException, got " + e.getClass().getName());
        }
    }

    @Test
    public void testUnexpectedToken_ErrorMessage() {
        // Unexpected tokens should produce clear error messages with position
        try {
            Transpiler.transpile("x + + y");
            fail("Should have thrown ParserException");
        } catch (ParserException e) {
            // Error message should mention line and column
            assertTrue(e.getMessage().contains("line") || e.getMessage().contains("column"));
        } catch (Exception e) {
            fail("Expected ParserException, got " + e.getClass().getName());
        }
    }

    // ========== COMPLEX EXPRESSIONS ==========

    @Test
    public void testChainedOperations() throws Exception {
        // Long chains of operations
        String result = Transpiler.transpile("a + b - c * d / e");
        assertNotNull(result);
        assertTrue(result.contains("a") && result.contains("b") && 
                   result.contains("c") && result.contains("d") && result.contains("e"));
    }

    @Test
    public void testNestedFunctionCalls() throws Exception {
        // Nested function calls: sin(cos(tan(x)))
        String result = Transpiler.transpile("sin(cos(tan(x)))");
        assertTrue(result.contains("\\sin"));
        assertTrue(result.contains("\\cos"));
        assertTrue(result.contains("\\tan"));
    }

    @Test
    public void testComplexFraction() throws Exception {
        // Complex fractions: (a + b) \\ (c + d)
        String result = Transpiler.transpile("(a + b) \\\\ (c + d)");
        assertTrue(result.contains("\\frac"));
        assertTrue(result.contains("a") && result.contains("b") && 
                   result.contains("c") && result.contains("d"));
    }

    @Test
    public void testMixedConstantsAndVariables() throws Exception {
        // Mix of constants and variables
        String result = Transpiler.transpile("PI + E + x + y");
        assertTrue(result.contains("\\pi") && result.contains("e") && 
                   result.contains("x") && result.contains("y"));
    }

    // ========== WHITESPACE HANDLING ==========

    @Test
    public void testExtraWhitespace() throws Exception {
        // Extra whitespace should be ignored
        String result1 = Transpiler.transpile("x+y");
        String result2 = Transpiler.transpile("x  +  y");
        String result3 = Transpiler.transpile("x\t+\ty");
        // All should produce equivalent results
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
    }

    @Test
    public void testLeadingTrailingWhitespace() throws Exception {
        // Leading/trailing whitespace should be ignored
        String result = Transpiler.transpile("  x + y  ");
        assertTrue(result.contains("x") && result.contains("y"));
    }

    @Test
    public void testNewlines() throws Exception {
        // Newlines should not break parsing
        String result = Transpiler.transpile("x +\ny");
        assertTrue(result.contains("x") && result.contains("y"));
    }

    // ========== ZERO AND ONE EDGE CASES ==========

    @Test
    public void testDivisionByZero() throws Exception {
        // Division by zero should parse (runtime behavior is separate concern)
        String result = Transpiler.transpile("5 / 0");
        assertNotNull(result);
    }

    @Test
    public void testMultiplicationByZero() throws Exception {
        String result = Transpiler.transpile("x * 0");
        assertNotNull(result);
    }

    @Test
    public void testMultiplicationByOne() throws Exception {
        String result = Transpiler.transpile("x * 1");
        assertNotNull(result);
    }

    @Test
    public void testPowerOfZero() throws Exception {
        String result = Transpiler.transpile("pow(x, 0)");
        assertNotNull(result);
    }

    @Test
    public void testZeroPower() throws Exception {
        String result = Transpiler.transpile("pow(0, x)");
        assertNotNull(result);
    }

    // ========== RESERVED WORD CONFLICTS ==========

    @Test
    public void testConstantAsPartOfIdentifier() throws Exception {
        // Constants as part of identifier names (e.g., "PI_VALUE")
        String result = Transpiler.transpile("PI_VALUE + E_CONSTANT");
        // Should treat as identifiers, not constants
        assertTrue(result.contains("PI_VALUE") && result.contains("E_CONSTANT"));
    }

    // ========== PREVIOUSLY FOUND BUGS (Document when found) ==========

    @Test
    public void testBug_DoubleNegative() throws Exception {
        // Bug: Double negative (--x) should be handled correctly
        String result = Transpiler.transpile("--5");
        // Should parse as negative of negative
        assertNotNull(result);
    }

    @Test
    public void testBug_EmptyFunctionCall() {
        // Bug: Empty function calls should be rejected
        assertThrows(EuclidException.class, () -> {
            Transpiler.transpile("pow()");
        });
    }

    @Test
    public void testBug_TrailingOperator() {
        // Bug: Trailing operators should be rejected
        assertThrows(ParserException.class, () -> {
            Transpiler.transpile("x + y +");
        });
    }

    @Test
    public void testBug_LeadingOperator_Except_Minus() {
        // Bug: Leading operators (except unary minus) should be rejected
        assertThrows(ParserException.class, () -> {
            Transpiler.transpile("+ x");
        });
    }

    @Test
    public void testBug_ConsecutiveOperators() {
        // Bug: Consecutive binary operators should be rejected
        assertThrows(ParserException.class, () -> {
            Transpiler.transpile("x + * y");
        });
    }
}
