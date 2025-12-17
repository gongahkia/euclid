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
 * Comprehensive tests for mathematical functions and constants.
 */
public class MathFunctionsTest {

    private String transpile(String input) throws LexerException, ParserException {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens, input);
        DocumentNode ast = parser.parse();
        LatexTranspiler transpiler = new LatexTranspiler();
        return ast.accept(transpiler);
    }

    // Trigonometric Functions
    @Test
    public void testSineFunction() throws LexerException, ParserException {
        String result = transpile("sin(x)");
        assertTrue(result.contains("\\sin"));
    }

    @Test
    public void testCosineFunction() throws LexerException, ParserException {
        String result = transpile("cos(theta)");
        assertTrue(result.contains("\\cos"));
    }

    @Test
    public void testTangentFunction() throws LexerException, ParserException {
        String result = transpile("tan(x)");
        assertTrue(result.contains("\\tan"));
    }

    @Test
    public void testCosecantFunction() throws LexerException, ParserException {
        String result = transpile("csc(x)");
        assertTrue(result.contains("\\csc"));
    }

    @Test
    public void testSecantFunction() throws LexerException, ParserException {
        String result = transpile("sec(x)");
        assertTrue(result.contains("\\sec"));
    }

    @Test
    public void testCotangentFunction() throws LexerException, ParserException {
        String result = transpile("cot(x)");
        assertTrue(result.contains("\\cot"));
    }

    // Hyperbolic Functions
    @Test
    public void testHyperbolicSine() throws LexerException, ParserException {
        String result = transpile("sinh(x)");
        assertTrue(result.contains("\\sinh"));
    }

    @Test
    public void testHyperbolicCosine() throws LexerException, ParserException {
        String result = transpile("cosh(x)");
        assertTrue(result.contains("\\cosh"));
    }

    @Test
    public void testHyperbolicTangent() throws LexerException, ParserException {
        String result = transpile("tanh(x)");
        assertTrue(result.contains("\\tanh"));
    }

    // Logarithmic and Exponential
    @Test
    public void testNaturalLog() throws LexerException, ParserException {
        String result = transpile("ln(x)");
        assertTrue(result.contains("\\ln"));
    }

    @Test
    public void testLogarithm() throws LexerException, ParserException {
        String result = transpile("log(x, 10)");
        assertTrue(result.contains("\\log"));
    }

    @Test
    public void testExponential() throws LexerException, ParserException {
        String result = transpile("exp(x)");
        assertTrue(result.contains("\\exp") || result.contains("e^"));
    }

    // Mathematical Constants
    @Test
    public void testPiConstant() throws LexerException, ParserException {
        String result = transpile("PI");
        assertEquals("\\pi", result);
    }

    @Test
    public void testEulerConstant() throws LexerException, ParserException {
        String result = transpile("E");
        assertEquals("e", result);
    }

    @Test
    public void testImaginaryUnit() throws LexerException, ParserException {
        String result = transpile("I");
        assertEquals("i", result);
    }

    // Greek Letters
    @Test
    public void testAlpha() throws LexerException, ParserException {
        String result = transpile("ALPHA");
        assertEquals("\\alpha", result);
    }

    @Test
    public void testBeta() throws LexerException, ParserException {
        String result = transpile("BETA");
        assertEquals("\\beta", result);
    }

    @Test
    public void testGamma() throws LexerException, ParserException {
        String result = transpile("GAMMA");
        assertEquals("\\gamma", result);
    }

    @Test
    public void testDelta() throws LexerException, ParserException {
        String result = transpile("DELTA");
        assertEquals("\\delta", result);
    }

    @Test
    public void testTheta() throws LexerException, ParserException {
        String result = transpile("THETA");
        assertEquals("\\theta", result);
    }

    @Test
    public void testLambda() throws LexerException, ParserException {
        String result = transpile("LAMBDA");
        assertEquals("\\lambda", result);
    }

    @Test
    public void testOmega() throws LexerException, ParserException {
        String result = transpile("OMEGA");
        assertEquals("\\omega", result);
    }

    // Square Root
    @Test
    public void testSquareRoot() throws LexerException, ParserException {
        String result = transpile("sqrt(x)");
        assertTrue(result.contains("\\sqrt"));
    }

    @Test
    public void testSquareRootWithNumber() throws LexerException, ParserException {
        String result = transpile("sqrt(16)");
        assertTrue(result.contains("\\sqrt") && result.contains("16"));
    }

    // Absolute Value
    @Test
    public void testAbsoluteValue() throws LexerException, ParserException {
        String result = transpile("abs(x)");
        assertTrue(result.contains("abs") || result.contains("|"));
    }

    // Floor and Ceiling
    @Test
    public void testFloorFunction() throws LexerException, ParserException {
        String result = transpile("floor(x)");
        assertTrue(result.contains("floor") || result.contains("\\lfloor"));
    }

    @Test
    public void testCeilingFunction() throws LexerException, ParserException {
        String result = transpile("ceil(x)");
        assertTrue(result.contains("ceil") || result.contains("\\lceil"));
    }

    // Complex Expressions
    @Test
    public void testNestedFunctions() throws LexerException, ParserException {
        String result = transpile("sin(cos(x))");
        assertTrue(result.contains("\\sin") && result.contains("\\cos"));
    }

    @Test
    public void testMixedOperationsWithFunctions() throws LexerException, ParserException {
        String result = transpile("2 * sin(x) + cos(y)");
        assertTrue(result.contains("\\sin") && result.contains("\\cos"));
        assertTrue(result.contains("2") && result.contains("x") && result.contains("y"));
    }

    @Test
    public void testExponentiationWithFunctions() throws LexerException, ParserException {
        String result = transpile("sin(x)^2");
        assertTrue(result.contains("\\sin") && result.contains("^{2"));
    }

    @Test
    public void testFractionsWithFunctions() throws LexerException, ParserException {
        String result = transpile("sin(x) / cos(x)");
        assertTrue(result.contains("\\frac"));
        assertTrue(result.contains("\\sin") && result.contains("\\cos"));
    }
}
