package com.euclid.lexer;

import com.euclid.token.Token;
import com.euclid.token.TokenType;
import com.euclid.exception.LexerException;
import com.euclid.lang.EuclidLanguage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lexer for the Euclid language.
 * Converts input text into a stream of tokens for the parser to consume.
 */
public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;      // Start of current lexeme
    private int current = 0;    // Current character
    private int line = 1;       // Current line (1-indexed)
    private int column = 1;     // Current column (1-indexed)
    private int tokenStartColumn = 1; // Column where current token started

    // Keywords and reserved words mapping
    private static final Map<String, TokenType> keywords = EuclidLanguage.getKeywordMap();

    public static Set<String> getKeywordNames() {
        return Collections.unmodifiableSet(keywords.keySet());
    }

    public static TokenType getKeywordType(String name) {
        return keywords.get(name);
    }

    /**
     * Creates a new lexer for the given source code.
     *
     * @param source The source code to tokenize
     */
    public Lexer(String source) {
        this.source = source;
    }

    /**
     * Tokenizes the entire source code.
     *
     * @return A list of tokens
     * @throws LexerException if a lexing error occurs
     */
    public List<Token> tokenize() throws LexerException {
        while (!isAtEnd()) {
            start = current;
            tokenStartColumn = column;
            scanToken();
        }

        // Add EOF token
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    /**
     * Scans a single token.
     */
    private void scanToken() throws LexerException {
        char c = advance();

        switch (c) {
            case '(' -> addToken(TokenType.LPAREN);
            case ')' -> addToken(TokenType.RPAREN);
            case '[' -> addToken(TokenType.LBRACKET);
            case ']' -> addToken(TokenType.RBRACKET);
            case '{' -> addToken(TokenType.LBRACE);
            case '}' -> addToken(TokenType.RBRACE);
            case ',' -> addToken(TokenType.COMMA);
            case '+' -> addToken(TokenType.PLUS);
            case '-' -> addToken(TokenType.MINUS);
            case '*' -> addToken(TokenType.MULTIPLY);
            case '/' -> {
                // Check for // comment
                if (match('/')) {
                    // Skip until end of line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.DIVIDE);
                }
            }
            case '%' -> addToken(TokenType.MODULO);
            case '^' -> addToken(TokenType.POWER);
            case '#' -> {
                // Python-style comment: skip until end of line
                while (peek() != '\n' && !isAtEnd()) advance();
            }
            case '!' -> addToken(TokenType.BANG);
            case '_' -> addToken(TokenType.UNDERSCORE);
            case '=' -> addToken(TokenType.EQUALS);
            case '\n' -> {
                addToken(TokenType.NEWLINE);
                line++;
                column = 1;
            }
            case '\r', '\t', ' ' -> { /* Skip whitespace but track columns */ }
            case '\\' -> {
                // Check for \\ (fraction operator)
                if (match('\\')) {
                    addToken(TokenType.BACKSLASH_BACKSLASH);
                } else {
                    // Single backslash could be part of LaTeX, treat as text
                    addToken(TokenType.TEXT);
                }
            }
            case '$' -> {
                // Check for $$ (display math)
                if (match('$')) {
                    addToken(TokenType.DOUBLE_DOLLAR);
                } else {
                    addToken(TokenType.DOLLAR);
                }
            }
            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    // Treat unknown characters as text (for markdown compatibility)
                    addToken(TokenType.TEXT);
                }
            }
        }
    }

    /**
     * Scans a number literal (integer or floating-point).
     */
    private void number() {
        while (isDigit(peek())) advance();

        // Look for decimal part
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // Consume the '.'
            while (isDigit(peek())) advance();
        }

        // Look for scientific notation
        if (peek() == 'e' || peek() == 'E') {
            advance();
            if (peek() == '+' || peek() == '-') advance();
            while (isDigit(peek())) advance();
        }

        String text = source.substring(start, current);
        if (text.contains(".") || text.contains("e") || text.contains("E")) {
            double value = Double.parseDouble(text);
            addToken(TokenType.NUMBER, value);
        } else {
            long value = Long.parseLong(text);
            addToken(TokenType.NUMBER, value);
        }
    }

    /**
     * Scans an identifier or keyword.
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        // speculatively consume underscore if the result is a keyword (set_diff, not_element_of)
        if (peek() == '_') {
            int saved = current;
            int savedCol = column;
            advance(); // consume _
            while (isAlphaNumeric(peek()) || peek() == '_') advance();
            String full = source.substring(start, current);
            if (!keywords.containsKey(full)) {
                current = saved; // backtrack: _ is a subscript, not part of keyword
                column = savedCol;
            }
        }

        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }

    /**
     * Checks if the current character matches the expected character.
     * If it matches, consumes it and returns true.
     *
     * @param expected The expected character
     * @return true if the character matches
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        column++;
        return true;
    }

    /**
     * Returns the current character without consuming it.
     *
     * @return The current character, or '\0' if at end
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Returns the next character without consuming it.
     *
     * @return The next character, or '\0' if at end
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * Checks if a character is a digit.
     *
     * @param c The character to check
     * @return true if the character is a digit
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Checks if a character is alphabetic.
     *
     * @param c The character to check
     * @return true if the character is alphabetic
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z');
    }

    /**
     * Checks if a character is alphanumeric.
     *
     * @param c The character to check
     * @return true if the character is alphanumeric
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Checks if we've reached the end of the source.
     *
     * @return true if at the end
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Consumes and returns the current character.
     *
     * @return The current character
     */
    private char advance() {
        column++;
        return source.charAt(current++);
    }

    /**
     * Adds a token without a literal value.
     *
     * @param type The token type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token with a literal value.
     *
     * @param type    The token type
     * @param literal The literal value
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, line, tokenStartColumn, literal));
    }
}
