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
        Parser parser = new Parser(tokens);
        DocumentNode ast = parser.parse();
        LatexTranspiler transpiler = new LatexTranspiler();
        return ast.accept(transpiler);
    }

    @Test
    public void testSimpleNumber() throws LexerException, ParserException {
        String result = transpile("42");
        assertNotNull(result);
        assertTrue(result.contains("42"));
    }

    @Test
    public void testSimpleAddition() throws LexerException, ParserException {
        String result = transpile("2 + 3");
        assertNotNull(result);
        assertTrue(result.contains("+"));
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
        assertNotNull(result);
    }

    @Test
    public void testExponentiation() throws LexerException, ParserException {
        String result = transpile("2 ^ 3");
        assertNotNull(result);
    }

    @Test
    public void testGreekConstant() throws LexerException, ParserException {
        String alpha = transpile("ALPHA");
        assertNotNull(alpha);
    }

    @Test
    public void testMultiplication() throws LexerException, ParserException {
        String result = transpile("4 * 6");
        assertNotNull(result);
    }

    @Test
    public void testTextMode() throws LexerException, ParserException {
        String result = transpile("This is plain text");
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }
}
