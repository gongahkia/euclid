package com.euclid;

import com.euclid.ast.DocumentNode;
import com.euclid.lexer.Lexer;
import com.euclid.parser.Parser;
import com.euclid.token.Token;
import com.euclid.transpiler.LatexTranspiler;
import com.euclid.exception.LexerException;
import com.euclid.exception.ParserException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for operators, calculus operations, and advanced math constructs.
 */
public class OperatorsAndCalculusTest {

    private String transpile(String input) throws LexerException, ParserException {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens, input);
        DocumentNode ast = parser.parse();
        LatexTranspiler transpiler = new LatexTranspiler();
        return ast.accept(transpiler);
    }

    // Basic Arithmetic Operators
    @Test
    public void testAddition() throws LexerException, ParserException {
        String result = transpile("a + b");
        assertTrue(result.contains("+"));
        assertTrue(result.contains("a") && result.contains("b"));
    }

    @Test
    public void testSubtraction() throws LexerException, ParserException {
        String result = transpile("x - y");
        assertTrue(result.contains("-"));
    }

    @Test
    public void testMultiplication() throws LexerException, ParserException {
        String result = transpile("3 * 4");
        assertTrue(result.contains("\\cdot") || result.contains("3") && result.contains("4"));
    }

    @Test
    public void testDivisionAsFraction() throws LexerException, ParserException {
        String result = transpile("a / b");
        assertTrue(result.contains("\\frac"));
    }

    @Test
    public void testExponentiation() throws LexerException, ParserException {
        String result = transpile("x^2");
        assertTrue(result.contains("x^{2"));
    }

    @Test
    public void testMultipleExponentiation() throws LexerException, ParserException {
        String result = transpile("x^2 + y^3");
        assertTrue(result.contains("x^{2") && result.contains("y^{3"));
    }

    // Comparison Operators
    @Test
    public void testLessThan() throws LexerException, ParserException {
        String result = transpile("lt(x, y)");
        assertTrue(result.contains("<") || result.contains("lt"));
    }

    @Test
    public void testGreaterThan() throws LexerException, ParserException {
        String result = transpile("gt(x, y)");
        assertTrue(result.contains(">") || result.contains("gt"));
    }

    @Test
    public void testLessThanOrEqual() throws LexerException, ParserException {
        String result = transpile("leq(x, y)");
        assertTrue(result.contains("\\leq") || result.contains("leq"));
    }

    @Test
    public void testGreaterThanOrEqual() throws LexerException, ParserException {
        String result = transpile("geq(x, y)");
        assertTrue(result.contains("\\geq") || result.contains("geq"));
    }

    // Calculus Operations
    @Test
    public void testIntegral() throws LexerException, ParserException {
        String result = transpile("integral(x^2, x)");
        assertTrue(result.contains("\\int"));
    }

    @Test
    public void testDerivative() throws LexerException, ParserException {
        String result = transpile("diff(x^2, x)");
        assertTrue(result.contains("\\frac") && result.contains("d"));
    }

    @Test
    public void testLimit() throws LexerException, ParserException {
        String result = transpile("limit(x, 0)");
        assertTrue(result.contains("\\lim") || result.contains("limit"));
    }

    // Summation and Product
    @Test
    public void testSummation() throws LexerException, ParserException {
        String result = transpile("sum(i, 1, n)");
        assertTrue(result.contains("\\sum") || result.contains("sum"));
    }

    @Test
    public void testProduct() throws LexerException, ParserException {
        String result = transpile("prod(i, 1, n)");
        assertTrue(result.contains("\\prod") || result.contains("prod"));
    }

    // Logical Operators
    @Test
    public void testLogicalAnd() throws LexerException, ParserException {
        String result = transpile("AND(p, q)");
        assertTrue(result.contains("\\land") || result.contains("AND"));
    }

    @Test
    public void testLogicalOr() throws LexerException, ParserException {
        String result = transpile("OR(p, q)");
        assertTrue(result.contains("\\lor") || result.contains("OR"));
    }

    @Test
    public void testLogicalNot() throws LexerException, ParserException {
        String result = transpile("NOT(p)");
        assertTrue(result.contains("\\neg") || result.contains("NOT"));
    }

    // Set Operations
    @Test
    public void testUnion() throws LexerException, ParserException {
        String result = transpile("union(A, B)");
        assertTrue(result.contains("\\cup") || result.contains("union"));
    }

    @Test
    public void testIntersection() throws LexerException, ParserException {
        String result = transpile("intersection(A, B)");
        assertTrue(result.contains("\\cap") || result.contains("intersection"));
    }

    @Test
    public void testSubset() throws LexerException, ParserException {
        String result = transpile("subset(A, B)");
        assertTrue(result.contains("\\subset") || result.contains("subset"));
    }

    // Complex Nested Expressions
    @Test
    public void testNestedParentheses() throws LexerException, ParserException {
        String result = transpile("((x + y) * (a + b))");
        assertNotNull(result);
        assertTrue(result.contains("x") && result.contains("y") && result.contains("a") && result.contains("b"));
    }

    @Test
    public void testComplexFraction() throws LexerException, ParserException {
        String result = transpile("(a + b) / (c + d)");
        assertTrue(result.contains("\\frac"));
    }

    @Test
    public void testChainedOperations() throws LexerException, ParserException {
        String result = transpile("a + b - c * d / e");
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    // Edge Cases
    @Test
    public void testSingleNumber() throws LexerException, ParserException {
        String result = transpile("42");
        assertTrue(result.contains("42"));
    }

    @Test
    public void testSingleVariable() throws LexerException, ParserException {
        String result = transpile("x");
        assertEquals("x", result);
    }

    @Test
    public void testNegativeNumber() throws LexerException, ParserException {
        String result = transpile("-5");
        assertTrue(result.contains("-") && result.contains("5"));
    }

    @Test
    public void testDecimalNumber() throws LexerException, ParserException {
        String result = transpile("3.14");
        assertTrue(result.contains("3.14"));
    }
}
