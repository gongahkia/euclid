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
 * Integration tests for the Euclid transpiler.
 * Tests the complete pipeline: Lexer → Parser → Transpiler
 */
public class EuclidIntegrationTest {

    private String transpile(String input) throws LexerException, ParserException {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens, input);  // Pass source for better error messages
        DocumentNode ast = parser.parse();
        LatexTranspiler transpiler = new LatexTranspiler();
        return ast.accept(transpiler);
    }

    @Test
    public void testSimpleNumber() throws LexerException, ParserException {
        assertEquals("42.0", transpile("42"));
    }

    @Test
    public void testSimpleAddition() throws LexerException, ParserException {
        assertEquals("2.0 + 3.0", transpile("2 + 3"));
    }

    @Test
    public void testPiConstant() throws LexerException, ParserException {
        assertEquals("\\pi", transpile("PI"));
    }

    @Test
    public void testSinFunction() throws LexerException, ParserException {
        String result = transpile("sin(x)");
        assertTrue(result.contains("\\sin"));
    }

    @Test
    public void testComplexExpression() throws LexerException, ParserException {
        String result = transpile("sin(PI / 2) + cos(0)");
        assertTrue(result.contains("\\sin"));
        assertTrue(result.contains("\\pi"));
        assertTrue(result.contains("\\cos"));
    }

    @Test
    public void testFraction() throws LexerException, ParserException {
        String result = transpile("a / b");
        assertTrue(result.contains("\\frac"));
    }

    @Test
    public void testExponentiation() throws LexerException, ParserException {
        String result = transpile("2 ^ 3");
        assertTrue(result.contains("2^{3}"));
    }

    @Test
    public void testGreekLetters() throws LexerException, ParserException {
        assertEquals("\\alpha", transpile("ALPHA"));
        assertEquals("\\beta", transpile("BETA"));
        assertEquals("\\gamma", transpile("GAMMA"));
    }

    @Test
    public void testMultiplication() throws LexerException, ParserException {
        String result = transpile("4 * 6");
        assertTrue(result.contains("\\cdot"));
    }

    @Test
    public void testTextPassthrough() throws LexerException, ParserException {
        String result = transpile("This is plain text");
        assertEquals("This is plain text", result);
    }

    @Test
    public void testHashComments() throws LexerException, ParserException {
        // Comments should be stripped out
        String result = transpile("x + y # this is a comment");
        assertFalse(result.contains("#"));
        assertFalse(result.contains("comment"));
        assertTrue(result.contains("x"));
        assertTrue(result.contains("y"));
    }

    @Test
    public void testDoubleSlashComments() throws LexerException, ParserException {
        // Comments should be stripped out
        String result = transpile("sin(x) // calculate sine of x");
        assertFalse(result.contains("//"));
        assertFalse(result.contains("calculate"));
        assertTrue(result.contains("\\sin"));
    }

    @Test
    public void testMixedCommentsAndExpressions() throws LexerException, ParserException {
        String input = """
                # This is a header comment
                x^2 + 2*x + 1  // quadratic expression
                # Another comment
                """;
        String result = transpile(input);
        assertFalse(result.contains("#"));
        assertFalse(result.contains("//"));
        assertTrue(result.contains("x^{2}"));
    }

    @Test
    public void testImprovedErrorMessagesForUnbalancedParentheses() {
        String input = "sin(x + y";  // Missing closing parenthesis
        
        ParserException exception = assertThrows(ParserException.class, () -> {
            transpile(input);
        });
        
        // Error message should contain helpful context
        String message = exception.getMessage();
        assertTrue(message.contains("Parser") || message.contains("Expected"), 
            "Should be a parser error: " + message);
        assertTrue(message.contains("parenthes") || message.contains("')'") || message.contains(")"), 
            "Should mention parentheses issue: " + message);
    }

    @Test
    public void testErrorMessageShowsSourceContext() {
        String input = "x^2 + ";  // Incomplete expression
        
        ParserException exception = assertThrows(ParserException.class, () -> {
            transpile(input);
        });
        
        // Error message should show the problematic line
        String message = exception.getMessage();
        assertTrue(message.contains("x^2 +") || message.contains("Unexpected"), 
            "Should show source context or unexpected token message");
    }
}
