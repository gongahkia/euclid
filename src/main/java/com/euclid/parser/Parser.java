package com.euclid.parser;

import com.euclid.ast.*;
import com.euclid.token.Token;
import com.euclid.token.TokenType;
import com.euclid.exception.DiagnosticCollector;
import com.euclid.exception.ParserException;
import com.euclid.lang.EuclidLanguage;
import com.euclid.util.ValidationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent parser for the Euclid language.
 * Converts a stream of tokens into an Abstract Syntax Tree (AST).
 */
public class Parser {
    private final List<Token> tokens;
    private final String source;
    private final DiagnosticCollector diagnostics;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this(tokens, null, null);
    }

    public Parser(List<Token> tokens, String source) {
        this(tokens, source, null);
    }

    public Parser(List<Token> tokens, String source, DiagnosticCollector diagnostics) {
        this.tokens = tokens;
        this.source = source;
        this.diagnostics = diagnostics;
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
            if (match(TokenType.NEWLINE)) {
                continue;
            }
            if (diagnostics != null) {
                try {
                    nodes.add(expression());
                } catch (ParserException e) {
                    diagnostics.addError(
                            e.getCode(),
                            e.getMessage(),
                            e.getLine(),
                            e.getColumn(),
                            e.getSuggestion(),
                            e.getCanonicalRewrite());
                    nodes.add(new TextExpr("[ERROR: " + e.getMessage() + "]"));
                    while (!isAtEnd() && !check(TokenType.NEWLINE) && !check(TokenType.EOF)) {
                        advance();
                    }
                }
            } else {
                nodes.add(expression());
            }
        }

        return new DocumentNode(nodes);
    }

    /**
     * Parses an expression (top level).
     */
    private AstNode expression() throws ParserException {
        return equality();
    }

    /**
     * Parses equality chains.
     */
    private AstNode equality() throws ParserException {
        AstNode expr = logicalOr();

        while (match(TokenType.EQUALS)) {
            Token operator = previous();
            AstNode right = logicalOr();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses logical disjunction.
     */
    private AstNode logicalOr() throws ParserException {
        AstNode expr = logicalAnd();

        while (match(TokenType.OR)) {
            Token operator = previous();
            AstNode right = logicalAnd();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses logical conjunction.
     */
    private AstNode logicalAnd() throws ParserException {
        AstNode expr = addition();

        while (match(TokenType.AND)) {
            Token operator = previous();
            AstNode right = addition();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
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

        while (true) {
            if (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO, TokenType.BACKSLASH_BACKSLASH, TokenType.DOT)) {
                Token operator = previous();
                AstNode right = power();
                expr = new BinaryExpr(expr, operator, right);
            } else if (isImplicitMultiplyStart(peek())) {
                Token synth = new Token(TokenType.IMPLICIT_MULTIPLY, "", peek().getLine(), peek().getColumn());
                AstNode right = power();
                expr = new BinaryExpr(expr, synth, right);
            } else {
                break;
            }
        }

        return expr;
    }

    private boolean isImplicitMultiplyStart(Token token) {
        TokenType t = token.getType();
        return t == TokenType.NUMBER || t == TokenType.IDENTIFIER || t == TokenType.LPAREN ||
               EuclidLanguage.isFunctionType(t) || isGreekLetter(token) || isConstantToken(token);
    }

    /**
     * Parses exponentiation (right-associative).
     */
    private AstNode power() throws ParserException {
        AstNode expr = unary();

        while (match(TokenType.POWER, TokenType.UNDERSCORE)) {
            Token operator = previous();
            AstNode right = unary(); // right operand (allow chaining x_1^2)
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    /**
     * Parses unary expressions (-x, +x).
     */
    private AstNode unary() throws ParserException {
        if (match(TokenType.MINUS, TokenType.PLUS, TokenType.NOT)) {
            Token operator = previous();
            AstNode right = unary();
            return new UnaryExpr(operator, right);
        }

        return postfix();
    }

    /**
     * Parses postfix operators (n!, n!!).
     */
    private AstNode postfix() throws ParserException {
        AstNode expr = call();
        while (match(TokenType.BANG)) {
            expr = new UnaryExpr(previous(), expr);
        }
        return expr;
    }

    /**
     * Parses function calls and constants.
     */
    private AstNode call() throws ParserException {
        if (isLogicalFunctionAlias(peek())) {
            Token function = advance();
            return parseFunctionCall(function);
        }

        // Check for function calls
        if (isFunctionToken(peek())) {
            Token function = advance();

            // Some functions don't require parentheses (constants)
            if (isConstantToken(function)) {
                return new LiteralExpr(function.getType());
            }

            // if no '(' follows, treat as plain identifier (e.g. var in x_var)
            if (!check(TokenType.LPAREN)) {
                return new IdentifierExpr(function.getLexeme());
            }

            return parseFunctionCall(function);
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

        // String literals
        if (match(TokenType.STRING)) {
            return new LiteralExpr(previous().getLiteral());
        }

        // Standalone logical symbols
        if (match(TokenType.AND, TokenType.OR, TokenType.NOT)) {
            return new LiteralExpr(previous().getType());
        }

        // Constants
        if (isConstantToken(peek()) && !isFunctionToken(peek())) {
            Token constant = advance();
            return new LiteralExpr(constant.getType());
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

        // Bracket groups [a, b, c] — used for matrix rows
        if (match(TokenType.LBRACKET)) {
            Token bracket = previous();
            List<AstNode> elements = new ArrayList<>();
            if (!check(TokenType.RBRACKET)) {
                do {
                    elements.add(expression());
                } while (match(TokenType.COMMA));
            }
            consume(TokenType.RBRACKET, "Expected ']' after bracket group");
            Token rowToken = new Token(TokenType.LBRACKET, "[]", bracket.getLine(), bracket.getColumn());
            return new CallExpr(rowToken, elements);
        }

        // Grouped expressions
        if (match(TokenType.LPAREN)) {
            AstNode expr = expression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            return new GroupingExpr(expr);
        }

        // Inline math mode ($ ... $)
        if (match(TokenType.DOLLAR)) {
            AstNode expr = expression();
            consume(TokenType.DOLLAR, "Expected '$' to close inline math");
            return new InlineMathExpr(expr);
        }

        // Display math mode ($$ ... $$)
        if (match(TokenType.DOUBLE_DOLLAR)) {
            AstNode expr = expression();
            consume(TokenType.DOUBLE_DOLLAR, "Expected '$$' to close display math");
            return new DisplayMathExpr(expr);
        }

        // Text (plain markdown)
        if (match(TokenType.TEXT)) {
            return new TextExpr(previous().getLexeme());
        }

        // If we reach here, we have an unexpected token
        Token token = peek();
        String suggestion = "Try using a number, identifier, or function call here";
        if (source != null) {
            throw new ParserException(
                "parser.unexpected-token",
                "Unexpected token '" + token.getLexeme() + "'",
                token.getLine(),
                token.getColumn(),
                source,
                suggestion
            );
        } else {
            throw new ParserException(
                "Unexpected token '" + token.getLexeme() + "'",
                token.getLine(),
                token.getColumn()
            );
        }
    }

    /**
     * Checks if a token is a function token.
     */
    private boolean isFunctionToken(Token token) {
        return EuclidLanguage.isFunctionType(token.getType()) || isConstantToken(token);
    }

    /**
     * Checks if a token is a constant (doesn't require parentheses).
     */
    private boolean isConstantToken(Token token) {
        return EuclidLanguage.isConstantType(token.getType());
    }

    /**
     * Checks if a token is a Greek letter.
     */
    private boolean isGreekLetter(Token token) {
        return EuclidLanguage.isGreekType(token.getType());
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
        String suggestion = null;
        
        // Provide helpful suggestions based on context
        if (type == TokenType.RPAREN) {
            suggestion = "Check for unbalanced parentheses - every '(' needs a matching ')'";
        } else if (type == TokenType.RBRACKET) {
            suggestion = "Check for unbalanced brackets - every '[' needs a matching ']'";
        } else if (type == TokenType.COMMA) {
            suggestion = "Function arguments should be separated by commas";
        }
        
        if (source != null && suggestion != null) {
            throw new ParserException("parser.consume", message, token.getLine(), token.getColumn(), source, suggestion);
        } else {
            throw new ParserException(message, token.getLine(), token.getColumn());
        }
    }

    private AstNode parseFunctionCall(Token function) throws ParserException {
        consume(TokenType.LPAREN, "Expected '('");
        List<AstNode> arguments = new ArrayList<>();

        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RPAREN, "Expected ')' after function arguments");
        ValidationHelper.validateArgumentCount(function, arguments.size());
        return new CallExpr(function, arguments);
    }

    private boolean isLogicalFunctionAlias(Token token) {
        return (token.getType() == TokenType.AND || token.getType() == TokenType.OR || token.getType() == TokenType.NOT)
                && checkNext(TokenType.LPAREN);
    }

    private boolean checkNext(TokenType type) {
        if (current + 1 >= tokens.size()) {
            return false;
        }
        return tokens.get(current + 1).getType() == type;
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
