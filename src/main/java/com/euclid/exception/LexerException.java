package com.euclid.exception;

/**
 * Exception thrown when the lexer encounters an error during tokenization.
 */
public class LexerException extends EuclidException {
    private final int line;
    private final int column;

    /**
     * Creates a new lexer exception.
     *
     * @param message The error message
     * @param line    The line where the error occurred
     * @param column  The column where the error occurred
     */
    public LexerException(String message, int line, int column) {
        super(String.format("Lexer error at line %d, column %d: %s", line, column, message));
        this.line = line;
        this.column = column;
    }

    /**
     * Creates a new lexer exception with a cause.
     *
     * @param message The error message
     * @param line    The line where the error occurred
     * @param column  The column where the error occurred
     * @param cause   The underlying cause
     */
    public LexerException(String message, int line, int column, Throwable cause) {
        super(String.format("Lexer error at line %d, column %d: %s", line, column, message), cause);
        this.line = line;
        this.column = column;
    }

    /**
     * Gets the line where the error occurred.
     *
     * @return The line number
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column where the error occurred.
     *
     * @return The column number
     */
    public int getColumn() {
        return column;
    }
}
