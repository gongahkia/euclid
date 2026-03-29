package com.euclid.parser;

import com.euclid.ast.AstNode;
import com.euclid.ast.BinaryExpr;
import com.euclid.ast.CallExpr;
import com.euclid.ast.DisplayMathExpr;
import com.euclid.ast.DocumentNode;
import com.euclid.ast.GroupingExpr;
import com.euclid.ast.IdentifierExpr;
import com.euclid.ast.InlineMathExpr;
import com.euclid.ast.LiteralExpr;
import com.euclid.ast.UnaryExpr;
import com.euclid.exception.DiagnosticCollector;
import com.euclid.exception.ParserException;
import com.euclid.lang.EuclidLanguage;
import com.euclid.token.Token;
import com.euclid.token.TokenType;
import com.euclid.util.ValidationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Handwritten Pratt parser for strict Euclid expressions.
 */
public class Parser {
    private static final int PREC_EQUALITY = 10;
    private static final int PREC_OR = 20;
    private static final int PREC_AND = 30;
    private static final int PREC_ADDITIVE = 40;
    private static final int PREC_MULTIPLICATIVE = 50;
    private static final int PREC_POWER = 60;
    private static final int PREC_PREFIX = 70;
    private static final int PREC_POSTFIX = 80;

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
        ValidationHelper.validateBalancedDelimiters(tokens);

        List<AstNode> nodes = new ArrayList<>();
        while (!isAtEnd()) {
            skipNewlines();
            if (isAtEnd()) {
                break;
            }

            if (diagnostics != null) {
                try {
                    nodes.add(parseTopLevelExpression());
                } catch (ParserException exception) {
                    diagnostics.addError(
                            exception.getCode(),
                            exception.getMessage(),
                            exception.getLine(),
                            exception.getColumn(),
                            exception.getSuggestion(),
                            exception.getCanonicalRewrite());
                    synchronize();
                }
            } else {
                nodes.add(parseTopLevelExpression());
            }
        }

        return new DocumentNode(nodes);
    }

    private AstNode parseTopLevelExpression() throws ParserException {
        AstNode expression = parseExpression(0, true);
        if (!check(TokenType.EOF) && !check(TokenType.NEWLINE)) {
            Token token = peek();
            throw parserError(
                    "parser.same-line-expression",
                    token,
                    "Unexpected token '" + token.getLexeme() + "'",
                    "Top-level Euclid expressions must be separated by newlines or connected with an operator");
        }
        return expression;
    }

    private AstNode parseExpression(int minBindingPower, boolean stopAtNewline) throws ParserException {
        if (!stopAtNewline) {
            skipNewlines();
        }
        if (shouldStop(stopAtNewline)) {
            Token token = peek();
            throw parserError(
                    "parser.missing-expression",
                    token,
                    "Expected an expression",
                    "Provide an operand, literal, or grouped Euclid expression here");
        }

        Token token = advance();
        AstNode left = nud(token);

        while (true) {
            if (!stopAtNewline && check(TokenType.NEWLINE)) {
                if (!shouldContinueAcrossNewline()) {
                    break;
                }
                skipNewlines();
            }
            if (shouldStop(stopAtNewline)) {
                break;
            }

            Operator operator = infixOperator(peek(), previous(), left);
            if (operator == null || operator.leftBindingPower() < minBindingPower) {
                break;
            }

            if (!operator.implicit()) {
                advance();
            }

            if (operator.postfix()) {
                left = new UnaryExpr(operator.token(), left);
                continue;
            }

            AstNode right = parseExpression(operator.rightBindingPower(), false);
            left = new BinaryExpr(left, operator.token(), right);
        }

        return left;
    }

    private AstNode nud(Token token) throws ParserException {
        TokenType type = token.getType();
        switch (type) {
            case NUMBER:
                return new LiteralExpr(token.getLiteral());
            case STRING:
                return new LiteralExpr(token.getLiteral());
            case IDENTIFIER:
                if (check(TokenType.LPAREN)) {
                    return parseCallArguments(token, false);
                }
                return new IdentifierExpr(token.getLexeme());
            case MINUS:
                return new UnaryExpr(token, parseExpression(PREC_PREFIX, false));
            case PLUS:
                throw parserError(
                        "parser.unexpected-prefix-plus",
                        token,
                        "Leading '+' is not valid in strict Euclid",
                        "Remove the leading '+' or use '-' for unary negation");
            case NOT:
                return new UnaryExpr(token, parseExpression(PREC_PREFIX, false));
            case LPAREN: {
                AstNode expression = parseExpression(0, false);
                consume(TokenType.RPAREN, "Expected ')' after expression");
                return new GroupingExpr(expression);
            }
            case LBRACKET:
                return parseBracketGroup(token);
            case DOLLAR: {
                AstNode expression = parseExpression(0, false);
                consume(TokenType.DOLLAR, "Expected '$' to close inline math");
                return new InlineMathExpr(expression);
            }
            case DOUBLE_DOLLAR: {
                AstNode expression = parseExpression(0, false);
                consume(TokenType.DOUBLE_DOLLAR, "Expected '$$' to close display math");
                return new DisplayMathExpr(expression);
            }
            default:
                break;
        }

        if (EuclidLanguage.isConstantType(type) || EuclidLanguage.isGreekType(type)) {
            return new LiteralExpr(type);
        }

        if (type == TokenType.AND || type == TokenType.OR) {
            if (check(TokenType.LPAREN)) {
                return parseFunctionCall(token);
            }
            throw parserError(
                    "parser.unexpected-token",
                    token,
                    "Unexpected token '" + token.getLexeme() + "'",
                    "Use infix logical syntax like 'p " + token.getLexeme() + " q'");
        }

        if (EuclidLanguage.isFunctionType(type)) {
            if (check(TokenType.LPAREN)) {
                return parseFunctionCall(token);
            }
            return new IdentifierExpr(token.getLexeme());
        }

        throw parserError(
                "parser.unexpected-token",
                token,
                "Unexpected token '" + token.getLexeme() + "'",
                "Try using a number, identifier, grouping, or supported Euclid function here");
    }

    private AstNode parseBracketGroup(Token bracket) throws ParserException {
        List<AstNode> elements = new ArrayList<>();
        if (!check(TokenType.RBRACKET)) {
            do {
                elements.add(parseExpression(0, false));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RBRACKET, "Expected ']' after bracket group");
        return new CallExpr(new Token(TokenType.LBRACKET, "[]", bracket.getLine(), bracket.getColumn()), elements);
    }

    private AstNode parseFunctionCall(Token function) throws ParserException {
        return parseCallArguments(function, true);
    }

    private AstNode parseCallArguments(Token function, boolean validateArity) throws ParserException {
        consume(TokenType.LPAREN, "Expected '(' after function name");
        List<AstNode> arguments = new ArrayList<>();

        if (!check(TokenType.RPAREN)) {
            do {
                arguments.add(parseExpression(0, false));
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RPAREN, "Expected ')' after function arguments");
        if (validateArity) {
            ValidationHelper.validateArgumentCount(function, arguments.size());
        }
        return new CallExpr(function, arguments);
    }

    private Operator infixOperator(Token next, Token previousToken, AstNode left) {
        TokenType type = next.getType();
        return switch (type) {
            case BANG -> new Operator(new Token(TokenType.BANG, "!", next.getLine(), next.getColumn()),
                    PREC_POSTFIX, PREC_POSTFIX, false, false);
            case POWER, UNDERSCORE -> new Operator(next, PREC_POWER, PREC_POWER, false, false);
            case MULTIPLY, DIVIDE, MODULO, BACKSLASH_BACKSLASH, DOT ->
                    new Operator(next, PREC_MULTIPLICATIVE, PREC_MULTIPLICATIVE + 1, false, false);
            case PLUS, MINUS -> new Operator(next, PREC_ADDITIVE, PREC_ADDITIVE + 1, false, false);
            case AND -> new Operator(next, PREC_AND, PREC_AND + 1, false, false);
            case OR -> new Operator(next, PREC_OR, PREC_OR + 1, false, false);
            case EQUALS -> new Operator(next, PREC_EQUALITY, PREC_EQUALITY + 1, false, false);
            default -> implicitMultiplyOperator(previousToken, left, next);
        };
    }

    private Operator implicitMultiplyOperator(Token previousToken, AstNode left, Token next) {
        if (!startsImplicitMultiplication(previousToken, left, next)) {
            return null;
        }
        return new Operator(
                new Token(TokenType.IMPLICIT_MULTIPLY, "", next.getLine(), next.getColumn()),
                PREC_MULTIPLICATIVE,
                PREC_MULTIPLICATIVE + 1,
                true,
                false);
    }

    private boolean startsImplicitMultiplication(Token previousToken, AstNode left, Token next) {
        TokenType nextType = next.getType();
        if (!(nextType == TokenType.LPAREN
                || nextType == TokenType.NUMBER
                || nextType == TokenType.IDENTIFIER
                || EuclidLanguage.isConstantType(nextType)
                || EuclidLanguage.isGreekType(nextType)
                || EuclidLanguage.isFunctionType(nextType))) {
            return false;
        }

        TokenType previousType = previousToken.getType();
        if (nextType == TokenType.IDENTIFIER) {
            return previousType == TokenType.NUMBER
                    || EuclidLanguage.isConstantType(previousType)
                    || EuclidLanguage.isGreekType(previousType)
                    || previousType == TokenType.RPAREN
                    || previousType == TokenType.RBRACKET
                    || previousType == TokenType.BANG
                    || left instanceof CallExpr;
        }

        if (nextType == TokenType.NUMBER) {
            return previousType == TokenType.RPAREN
                    || previousType == TokenType.RBRACKET
                    || previousType == TokenType.BANG
                    || previousType == TokenType.IDENTIFIER
                    || EuclidLanguage.isConstantType(previousType)
                    || EuclidLanguage.isGreekType(previousType)
                    || left instanceof CallExpr;
        }

        return previousType != TokenType.IDENTIFIER || nextType == TokenType.LPAREN || nextType == TokenType.NUMBER;
    }

    private boolean shouldContinueAcrossNewline() {
        int lookahead = current;
        while (lookahead < tokens.size() && tokens.get(lookahead).getType() == TokenType.NEWLINE) {
            lookahead++;
        }
        if (lookahead >= tokens.size()) {
            return false;
        }
        return switch (tokens.get(lookahead).getType()) {
            case BANG, POWER, UNDERSCORE, MULTIPLY, DIVIDE, MODULO, BACKSLASH_BACKSLASH, DOT,
                    PLUS, MINUS, AND, OR, EQUALS -> true;
            default -> false;
        };
    }

    private boolean shouldStop(boolean stopAtNewline) {
        if (check(TokenType.EOF)
                || check(TokenType.RPAREN)
                || check(TokenType.RBRACKET)
                || check(TokenType.RBRACE)
                || check(TokenType.COMMA)
                || check(TokenType.DOLLAR)
                || check(TokenType.DOUBLE_DOLLAR)) {
            return true;
        }
        return stopAtNewline && check(TokenType.NEWLINE);
    }

    private ParserException parserError(String code, Token token, String message, String suggestion) {
        if (source == null) {
            return new ParserException(code, message, token.getLine(), token.getColumn(), suggestion);
        }
        return new ParserException(code, message, token.getLine(), token.getColumn(), source, suggestion);
    }

    private void skipNewlines() {
        while (match(TokenType.NEWLINE)) {
            // keep consuming
        }
    }

    private void synchronize() {
        while (!isAtEnd()) {
            if (match(TokenType.NEWLINE)) {
                return;
            }
            advance();
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return type == TokenType.EOF;
        }
        return peek().getType() == type;
    }

    private Token consume(TokenType type, String message) throws ParserException {
        if (check(type)) {
            return advance();
        }

        Token token = peek();
        String suggestion = null;
        if (type == TokenType.RPAREN) {
            suggestion = "Check for unbalanced parentheses - every '(' needs a matching ')'";
        } else if (type == TokenType.RBRACKET) {
            suggestion = "Check for unbalanced brackets - every '[' needs a matching ']'";
        } else if (type == TokenType.COMMA) {
            suggestion = "Function arguments should be separated by commas";
        }
        throw parserError("parser.consume", token, message, suggestion);
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private record Operator(
            Token token,
            int leftBindingPower,
            int rightBindingPower,
            boolean implicit,
            boolean postfix) {
    }
}
