package com.euclid.exception;

/**
 * Exception thrown when the parser encounters an error during parsing.
 */
public class ParserException extends EuclidException {
    private final int line;
    private final int column;
    private final String sourceContext;
    private final String suggestion;

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
        this.sourceContext = null;
        this.suggestion = null;
    }

    /**
     * Creates a new parser exception with source context and suggestion.
     *
     * @param message    The error message
     * @param line       The line where the error occurred
     * @param column     The column where the error occurred
     * @param source     The source code
     * @param suggestion Suggested fix (optional)
     */
    public ParserException(String message, int line, int column, String source, String suggestion) {
        super(buildErrorMessage(message, line, column, source, suggestion));
        this.line = line;
        this.column = column;
        this.sourceContext = extractSourceContext(source, line, column);
        this.suggestion = suggestion;
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
        this.sourceContext = null;
        this.suggestion = null;
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
     * Gets the suggestion for fixing the error.
     *
     * @return The suggestion or null if not available
     */
    public String getSuggestion() {
        return suggestion;
    }

    /**
     * Builds an error message with source context and suggestion.
     */
    private static String buildErrorMessage(String message, int line, int column, String source, String suggestion) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║ Parser Error at line %d, column %d                    \n", line, column));
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
        
        if (suggestion != null && !suggestion.isEmpty()) {
            sb.append("╠══════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ 💡 Suggestion: %s\n", suggestion));
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
