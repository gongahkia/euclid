package com.euclid.token;

import java.util.Objects;

/**
 * Represents a single token in the Euclid language.
 * A token consists of a type, the original text (lexeme), and position information.
 */
public class Token {
    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;
    private final Object literal; // For storing parsed values (numbers, strings, etc.)

    /**
     * Creates a new token.
     *
     * @param type    The type of the token
     * @param lexeme  The original text of the token
     * @param line    The line number where the token appears (1-indexed)
     * @param column  The column number where the token appears (1-indexed)
     * @param literal The literal value (for numbers, strings, etc.), or null
     */
    public Token(TokenType type, String lexeme, int line, int column, Object literal) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.literal = literal;
    }

    /**
     * Creates a new token without a literal value.
     *
     * @param type   The type of the token
     * @param lexeme The original text of the token
     * @param line   The line number where the token appears (1-indexed)
     * @param column The column number where the token appears (1-indexed)
     */
    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, line, column, null);
    }

    /**
     * Gets the type of this token.
     *
     * @return The token type
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Gets the original text (lexeme) of this token.
     *
     * @return The lexeme
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * Gets the line number where this token appears.
     *
     * @return The line number (1-indexed)
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column number where this token appears.
     *
     * @return The column number (1-indexed)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the literal value of this token.
     *
     * @return The literal value, or null if not applicable
     */
    public Object getLiteral() {
        return literal;
    }

    /**
     * Checks if this token is of the specified type.
     *
     * @param type The token type to check
     * @return true if this token is of the specified type
     */
    public boolean is(TokenType type) {
        return this.type == type;
    }

    /**
     * Checks if this token is one of the specified types.
     *
     * @param types The token types to check
     * @return true if this token is one of the specified types
     */
    public boolean isOneOf(TokenType... types) {
        for (TokenType t : types) {
            if (this.type == t) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Token token = (Token) obj;
        return line == token.line &&
               column == token.column &&
               type == token.type &&
               Objects.equals(lexeme, token.lexeme) &&
               Objects.equals(literal, token.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lexeme, line, column, literal);
    }

    @Override
    public String toString() {
        return String.format("Token{type=%s, lexeme='%s', line=%d, column=%d, literal=%s}",
                type, lexeme, line, column, literal);
    }

    /**
     * Creates a string representation suitable for debugging.
     *
     * @return A debug string
     */
    public String toDebugString() {
        if (literal != null) {
            return String.format("%s '%s' (%s) at %d:%d", type, lexeme, literal, line, column);
        } else {
            return String.format("%s '%s' at %d:%d", type, lexeme, line, column);
        }
    }
}
