package com.euclid.exception;

/**
 * Exception thrown when the lexer encounters an error during tokenization.
 */
public class LexerException extends EuclidException {
    private final int line;
    private final int column;
    private final String sourceContext;

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
        this.sourceContext = null;
    }

    /**
     * Creates a new lexer exception with source context.
     *
     * @param message The error message
     * @param line    The line where the error occurred
     * @param column  The column where the error occurred
     * @param source  The source code
     */
    public LexerException(String message, int line, int column, String source) {
        super(buildErrorMessage(message, line, column, source));
        this.line = line;
        this.column = column;
        this.sourceContext = extractSourceContext(source, line, column);
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
        this.sourceContext = null;
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

    /**
     * Gets the source context around the error.
     *
     * @return The source context or null if not available
     */
    public String getSourceContext() {
        return sourceContext;
    }

    /**
     * Builds an error message with source context.
     */
    private static String buildErrorMessage(String message, int line, int column, String source) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║ Lexer Error at line %d, column %d                     \n", line, column));
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ %s\n", message));
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        
        String[] lines = source.split("\n");
        if (line > 0 && line <= lines.length) {
            String errorLine = lines[line - 1];
            sb.append(String.format("║ %s\n", errorLine));
            sb.append("║ ");
            for (int i = 0; i < column - 1 && i < errorLine.length(); i++) {
                sb.append(" ");
            }
            sb.append("^\n");
        }
        sb.append("╚══════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    /**
     * Extracts source context around the error.
     */
    private static String extractSourceContext(String source, int line, int column) {
        String[] lines = source.split("\n");
        if (line > 0 && line <= lines.length) {
            return lines[line - 1];
        }
        return null;
    }
}
