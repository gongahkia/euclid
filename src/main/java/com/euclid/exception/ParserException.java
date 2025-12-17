package com.euclid.exception;

/**
 * Exception thrown when the parser encounters an error during parsing.
 */
public class ParserException extends EuclidException {
    private final int line;
    private final int column;

    /**
     * Creates a new parser exception.
     *
     * @param message The error message
     * @param line    The line where the error occurred
     * @param column  The column where the error occurred
     */
    public ParserException(String message, int line, int column) {
        super(String.format("Parser error at line %d, column %d: %s", line, column, message));
        this.line = line;
        this.column = column;
    }

    /**
     * Creates a new parser exception with a cause.
     *
     * @param message The error message
     * @param line    The line where the error occurred
     * @param column  The column where the error occurred
     * @param cause   The underlying cause
     */
    public ParserException(String message, int line, int column, Throwable cause) {
        super(String.format("Parser error at line %d, column %d: %s", line, column, message), cause);
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
