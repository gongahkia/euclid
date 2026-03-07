package com.euclid;

import com.euclid.exception.Diagnostic;
import java.util.List;

public record TranspileResult(String output, List<Diagnostic> diagnostics) {
    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(d -> d.getSeverity() == Diagnostic.Severity.ERROR);
    }
}
