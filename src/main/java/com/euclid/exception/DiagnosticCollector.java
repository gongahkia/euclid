package com.euclid.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiagnosticCollector {
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public void addError(String message, int line, int column) {
        diagnostics.add(new Diagnostic(Diagnostic.Severity.ERROR, message, line, column));
    }

    public void addError(String message, int line, int column, String suggestion) {
        diagnostics.add(new Diagnostic(Diagnostic.Severity.ERROR, message, line, column, suggestion));
    }

    public void addError(String code, String message, int line, int column, String suggestion, String canonicalRewrite) {
        diagnostics.add(new Diagnostic(Diagnostic.Severity.ERROR, code, message, line, column, suggestion, canonicalRewrite));
    }

    public void addWarning(String message, int line, int column) {
        diagnostics.add(new Diagnostic(Diagnostic.Severity.WARNING, message, line, column));
    }

    public void addInfo(String message, int line, int column) {
        diagnostics.add(new Diagnostic(Diagnostic.Severity.INFO, message, line, column));
    }

    public void addInfo(String code, String message, int line, int column, String suggestion, String canonicalRewrite) {
        diagnostics.add(new Diagnostic(Diagnostic.Severity.INFO, code, message, line, column, suggestion, canonicalRewrite));
    }

    public List<Diagnostic> getErrors() {
        return diagnostics.stream()
            .filter(d -> d.getSeverity() == Diagnostic.Severity.ERROR)
            .toList();
    }

    public List<Diagnostic> getWarnings() {
        return diagnostics.stream()
            .filter(d -> d.getSeverity() == Diagnostic.Severity.WARNING)
            .toList();
    }

    public List<Diagnostic> getAll() {
        return Collections.unmodifiableList(diagnostics);
    }

    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(d -> d.getSeverity() == Diagnostic.Severity.ERROR);
    }

    public void clear() {
        diagnostics.clear();
    }
}
