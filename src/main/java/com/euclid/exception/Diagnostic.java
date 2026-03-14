package com.euclid.exception;

public class Diagnostic {
    public enum Severity { ERROR, WARNING, INFO }

    private final Severity severity;
    private final String code;
    private final String message;
    private final int line;
    private final int column;
    private final String suggestion;
    private final String canonicalRewrite;

    public Diagnostic(
            Severity severity,
            String code,
            String message,
            int line,
            int column,
            String suggestion,
            String canonicalRewrite) {
        this.severity = severity;
        this.code = code;
        this.message = message;
        this.line = line;
        this.column = column;
        this.suggestion = suggestion;
        this.canonicalRewrite = canonicalRewrite;
    }

    public Diagnostic(Severity severity, String message, int line, int column, String suggestion) {
        this(severity, null, message, line, column, suggestion, null);
    }

    public Diagnostic(Severity severity, String code, String message, int line, int column, String suggestion) {
        this(severity, code, message, line, column, suggestion, null);
    }

    public Diagnostic(Severity severity, String message, int line, int column) {
        this(severity, null, message, line, column, null, null);
    }

    public Severity getSeverity() { return severity; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getSuggestion() { return suggestion; }
    public String getCanonicalRewrite() { return canonicalRewrite; }

    @Override
    public String toString() {
        String prefix = switch (severity) {
            case ERROR -> "ERROR";
            case WARNING -> "WARN";
            case INFO -> "INFO";
        };
        String loc = String.format("[%d:%d]", line, column);
        String result = prefix + " " + loc + " " + message;
        if (code != null) result += " (" + code + ")";
        if (suggestion != null) result += " (suggestion: " + suggestion + ")";
        if (canonicalRewrite != null) result += " (canonical: " + canonicalRewrite + ")";
        return result;
    }
}
