package com.euclid;

import com.euclid.exception.Diagnostic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MixedContentProcessorTest {

    @Test
    public void validInlineCandidateIsTranspiledInMixedMode() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "Value: sum(i, i, 1, n)",
                false,
                com.euclid.transpiler.MathMode.NONE,
                true);

        assertEquals("Value: $\\sum_{i=1}^{n} i$", result.output());
        assertFalse(result.hasErrors());
    }

    @Test
    public void invalidInlineCandidateIsPreservedAndReportedInMixedMode() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "Value: piecewise(x, geq(x, 0), -x)",
                false,
                com.euclid.transpiler.MathMode.NONE,
                true);

        assertEquals("Value: piecewise(x, geq(x, 0), -x)", result.output());
        assertTrue(result.hasErrors());
        assertTrue(result.diagnostics().stream().anyMatch(d -> "parser.invalid-arity".equals(d.getCode())));
        assertTrue(result.diagnostics().stream().anyMatch(d -> d.getLine() == 1 && d.getColumn() > 1));
    }

    @Test
    public void aliasWarningsAreOffsetInsideMixedMode() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "Infinity: INF",
                false,
                com.euclid.transpiler.MathMode.NONE,
                true);

        assertEquals("Infinity: $\\infty$", result.output());
        assertTrue(result.diagnostics().stream().anyMatch(d ->
                d.getSeverity() == Diagnostic.Severity.WARNING
                        && "canonical.rewrite".equals(d.getCode())
                        && d.getColumn() > 1));
    }
}
