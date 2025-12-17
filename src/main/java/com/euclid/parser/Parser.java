package com.euclid.parser;

import com.euclid.ast.*;
import com.euclid.token.Token;
import com.euclid.token.TokenType;
import com.euclid.exception.ParserException;
import com.euclid.util.ValidationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent parser for the Euclid language.
 * Converts a stream of tokens into an Abstract Syntax Tree (AST).
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    /**
     * Creates a new parser for the given tokens.
     *
     * @param tokens The tokens to parse
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the entire document.
     *
     * @return The root DocumentNode
     * @throws ParserException if a parsing error occurs
     */
    public DocumentNode parse() throws ParserException {
        // Validate balanced delimiters before parsing
        ValidationHelper.validateBalancedDelimiters(tokens);
        
        List<AstNode> nodes = new ArrayList<>();

        while (!isAtEnd()) {
            // Skip newlines
            if (match(TokenType.NEWLINE)) {
                continue;
            }
            nodes.add(expression());
        }

        return new DocumentNode(nodes);
    }

    /**
     * Parses an expression (top level).
     */
    private AstNode expression() throws ParserException {
        return addition();
    }

    /**
     * Parses addition and subtraction (lowest precedence).
     */
    private AstNode addition() throws ParserException {
        AstNode expr = multiplication();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            AstNode right = multiplication();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses multiplication, division, and modulo.
     */
    private AstNode multiplication() throws ParserException {
        AstNode expr = power();

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO, TokenType.BACKSLASH_BACKSLASH)) {
            Token operator = previous();
            AstNode right = power();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses exponentiation (right-associative).
     */
    private AstNode power() throws ParserException {
        AstNode expr = unary();

        if (match(TokenType.POWER)) {
            Token operator = previous();
            AstNode right = power(); // Right-associative
            return new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses unary expressions (-x, +x).
     */
    private AstNode unary() throws ParserException {
        if (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            AstNode right = unary();
            return new UnaryExpr(operator, right);
        }

        return call();
    }

    /**
     * Parses function calls and constants.
     */
    private AstNode call() throws ParserException {
        // Check for function calls
        if (isFunctionToken(peek())) {
            Token function = advance();

            // Some functions don't require parentheses (constants)
            if (isConstantToken(function)) {
                return new LiteralExpr(function.getType());
            }

            // Parse function arguments
            if (!check(TokenType.LPAREN)) {
                throw new ParserException(
                    "Expected '(' after function name '" + function.getLexeme() + "'",
                    function.getLine(),
                    function.getColumn()
                );
            }

            consume(TokenType.LPAREN, "Expected '('");
            List<AstNode> arguments = new ArrayList<>();

            if (!check(TokenType.RPAREN)) {
                do {
                    arguments.add(expression());
                } while (match(TokenType.COMMA));
            }

            consume(TokenType.RPAREN, "Expected ')' after function arguments");
            
            // Validate argument count
            ValidationHelper.validateArgumentCount(function, arguments.size());
            
            return new CallExpr(function, arguments);
        }

        return primary();
    }

    /**
     * Parses primary expressions (literals, identifiers, grouping).
     */
    private AstNode primary() throws ParserException {
        // Numbers
        if (match(TokenType.NUMBER)) {
            return new LiteralExpr(previous().getLiteral());
        }

        // Constants
        if (match(TokenType.PI, TokenType.E, TokenType.I, TokenType.GAMMA, TokenType.PHI,
                  TokenType.INFINITY, TokenType.EMPTYSET, TokenType.AND, TokenType.OR, TokenType.NOT)) {
            return new LiteralExpr(previous().getType());
        }

        // Greek letters (treated as identifiers)
        if (isGreekLetter(peek())) {
            Token greek = advance();
            return new IdentifierExpr(greek.getLexeme());
        }

        // Identifiers
        if (match(TokenType.IDENTIFIER)) {
            return new IdentifierExpr(previous().getLexeme());
        }

        // Grouped expressions
        if (match(TokenType.LPAREN)) {
            AstNode expr = expression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            return new GroupingExpr(expr);
        }

        // Text (plain markdown)
        if (match(TokenType.TEXT)) {
            return new TextExpr(previous().getLexeme());
        }

        // If we reach here, we have an unexpected token
        Token token = peek();
        throw new ParserException(
            "Unexpected token '" + token.getLexeme() + "'",
            token.getLine(),
            token.getColumn()
        );
    }

    /**
     * Checks if a token is a function token.
     */
    private boolean isFunctionToken(Token token) {
        TokenType type = token.getType();
        return type == TokenType.POW || type == TokenType.ABS || type == TokenType.CEIL ||
               type == TokenType.FLOOR || type == TokenType.MOD || type == TokenType.GCD ||
               type == TokenType.LCM || type == TokenType.LT || type == TokenType.GT ||
               type == TokenType.LEQ || type == TokenType.GEQ || type == TokenType.APPROX ||
               type == TokenType.NEQ || type == TokenType.EQUIV || type == TokenType.PM ||
               type == TokenType.TIMES || type == TokenType.DIV || type == TokenType.CDOT ||
               type == TokenType.AST || type == TokenType.STAR || type == TokenType.CIRC ||
               type == TokenType.BULLET || type == TokenType.CAP || type == TokenType.CUP ||
               type == TokenType.SIN || type == TokenType.COS || type == TokenType.TAN ||
               type == TokenType.CSC || type == TokenType.SEC || type == TokenType.COT ||
               type == TokenType.SINH || type == TokenType.COSH || type == TokenType.TANH ||
               type == TokenType.LOG || type == TokenType.LN || type == TokenType.EXP ||
               type == TokenType.SQRT || type == TokenType.PARTIAL || type == TokenType.LIMIT ||
               type == TokenType.DIFF || type == TokenType.INTEGRAL || type == TokenType.SUM ||
               type == TokenType.PROD || type == TokenType.VECTOR || type == TokenType.MATRIX ||
               type == TokenType.SUBSET || type == TokenType.SUPSET || type == TokenType.SUBSETEQ ||
               type == TokenType.SUPSETEQ || type == TokenType.UNION || type == TokenType.INTERSECTION ||
               type == TokenType.SET_DIFF || type == TokenType.ELEMENT_OF || type == TokenType.NOT_ELEMENT_OF ||
               type == TokenType.IMPLIES || type == TokenType.IFF || type == TokenType.FORALL ||
               type == TokenType.EXISTS || isConstantToken(token);
    }

    /**
     * Checks if a token is a constant (doesn't require parentheses).
     */
    private boolean isConstantToken(Token token) {
        TokenType type = token.getType();
        return type == TokenType.PI || type == TokenType.E || type == TokenType.I ||
               type == TokenType.GAMMA || type == TokenType.PHI || type == TokenType.INFINITY ||
               type == TokenType.EMPTYSET || type == TokenType.AND || type == TokenType.OR ||
               type == TokenType.NOT;
    }

    /**
     * Checks if a token is a Greek letter.
     */
    private boolean isGreekLetter(Token token) {
        TokenType type = token.getType();
        return type == TokenType.ALPHA || type == TokenType.BETA || type == TokenType.DELTA ||
               type == TokenType.EPSILON || type == TokenType.ZETA || type == TokenType.ETA ||
               type == TokenType.THETA || type == TokenType.KAPPA || type == TokenType.LAMBDA ||
               type == TokenType.MU || type == TokenType.NU || type == TokenType.XI ||
               type == TokenType.OMICRON || type == TokenType.RHO || type == TokenType.SIGMA ||
               type == TokenType.TAU || type == TokenType.UPSILON || type == TokenType.CHI ||
               type == TokenType.PSI || type == TokenType.OMEGA;
    }

    /**
     * Checks if the current token matches any of the given types.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the current token is of the given type.
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    /**
     * Consumes the current token if it matches the given type.
     */
    private Token consume(TokenType type, String message) throws ParserException {
        if (check(type)) return advance();

        Token token = peek();
        throw new ParserException(message, token.getLine(), token.getColumn());
    }

    /**
     * Advances to the next token.
     */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /**
     * Checks if we're at the end of the token stream.
     */
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    /**
     * Returns the current token without consuming it.
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Returns the previous token.
     */
    private Token previous() {
        return tokens.get(current - 1);
    }
}
