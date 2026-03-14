package com.euclid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserRecoveryTest {

    @Test
    public void invalidLineDoesNotLeakErrorTextIntoRecoveredOutput() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "x = y\npiecewise(x, geq(x, 0), -x)\nz = w",
                false,
                com.euclid.transpiler.MathMode.NONE);

        assertTrue(result.hasErrors());
        assertFalse(result.output().contains("[ERROR:"));
        assertTrue(result.output().contains("x = y"));
        assertTrue(result.output().contains("z = w"));
    }

    @Test
    public void parserRecoveryPreservesStableDiagnosticCodes() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "x = y\npiecewise(x, geq(x, 0), -x)\nz = w",
                false,
                com.euclid.transpiler.MathMode.NONE);

        assertTrue(result.diagnostics().stream().anyMatch(d -> "parser.invalid-arity".equals(d.getCode())));
    }
}
