package com.euclid.exception;

public class Diagnostic {
    public enum Severity { ERROR, WARNING, INFO }

    private final Severity severity;
    private final String message;
    private final int line;
    private final int column;
    private final String suggestion;

    public Diagnostic(Severity severity, String message, int line, int column, String suggestion) {
        this.severity = severity;
        this.message = message;
        this.line = line;
        this.column = column;
        this.suggestion = suggestion;
    }

    public Diagnostic(Severity severity, String message, int line, int column) {
        this(severity, message, line, column, null);
    }

    public Severity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getSuggestion() { return suggestion; }

    @Override
    public String toString() {
        String prefix = switch (severity) {
            case ERROR -> "ERROR";
            case WARNING -> "WARN";
            case INFO -> "INFO";
        };
        String loc = String.format("[%d:%d]", line, column);
        String result = prefix + " " + loc + " " + message;
        if (suggestion != null) result += " (suggestion: " + suggestion + ")";
        return result;
    }
}
