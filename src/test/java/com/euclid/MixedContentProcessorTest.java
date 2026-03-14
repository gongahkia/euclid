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
                "Choose: choose(n, k)",
                false,
                com.euclid.transpiler.MathMode.NONE,
                true);

        assertEquals("Choose: $\\binom{n}{k}$", result.output());
        assertTrue(result.diagnostics().stream().anyMatch(d ->
                d.getSeverity() == Diagnostic.Severity.WARNING
                        && "canonical.rewrite".equals(d.getCode())
                        && d.getColumn() > 1));
    }

    @Test
    public void standaloneConstantsRemainPlainTextInMixedMode() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "Acronym: INF",
                false,
                com.euclid.transpiler.MathMode.NONE,
                true);

        assertEquals("Acronym: INF", result.output());
        assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    public void existingInlineCodeAndLatexAreLeftUntouchedInMixedMode() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "Code `sum(i, i, 1, n)` and math $choose(n, k)$ stay literal.",
                false,
                com.euclid.transpiler.MathMode.NONE,
                true);

        assertEquals("Code `sum(i, i, 1, n)` and math $choose(n, k)$ stay literal.", result.output());
        assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    public void plainWordsContainingFunctionNamesDoNotTriggerMixedMode() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "We are singing about cosine similarity in plain prose.",
                false,
                com.euclid.transpiler.MathMode.NONE,
                true);

        assertEquals("We are singing about cosine similarity in plain prose.", result.output());
        assertTrue(result.diagnostics().isEmpty());
    }
}
