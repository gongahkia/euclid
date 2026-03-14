package com.euclid;

import com.euclid.lang.EuclidCapability;
import com.euclid.lang.EuclidCapabilityManifest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageContractTest {

    @Test
    public void testEqualityIsParsedAsDocumented() throws Exception {
        assertEquals("x = y", Transpiler.transpile("x = y"));
    }

    @Test
    public void testInfixLogicalOperatorsAreParsed() throws Exception {
        assertEquals("p \\land q", Transpiler.transpile("p AND q"));
        assertEquals("p \\lor q", Transpiler.transpile("p OR q"));
    }

    @Test
    public void testLogicalFunctionAliasesStillWork() throws Exception {
        assertEquals("p \\land q", Transpiler.transpile("AND(p, q)"));
        assertEquals("\\neg (p)", Transpiler.transpile("NOT(p)"));
    }

    @Test
    public void testCanonicalizeRewritesKnownAliases() {
        assertEquals("INFINITY + subset(A, B) + binom(n, k)",
                Transpiler.canonicalize("INF + proper_subset(A, B) + choose(n, k)"));
    }

    @Test
    public void testCapabilitiesExposeCanonicalEntriesAndAliases() {
        EuclidCapabilityManifest manifest = Transpiler.capabilityManifest();

        Optional<EuclidCapability> infinity = manifest.capabilities().stream()
                .filter(capability -> capability.name().equals("INFINITY"))
                .findFirst();
        assertTrue(infinity.isPresent());
        assertTrue(infinity.get().aliases().contains("INF"));

        Optional<EuclidCapability> subset = manifest.capabilities().stream()
                .filter(capability -> capability.name().equals("subset"))
                .findFirst();
        assertTrue(subset.isPresent());
        assertTrue(subset.get().aliases().contains("proper_subset"));
    }

    @Test
    public void testDiagnosticsSurfaceCanonicalRewrite() {
        TranspileResult result = Transpiler.transpileWithDiagnostics("INF", false, com.euclid.transpiler.MathMode.NONE, false);
        assertFalse(result.diagnostics().isEmpty());
        assertEquals("canonical.rewrite", result.diagnostics().get(0).getCode());
        assertEquals("INFINITY", result.diagnostics().get(0).getCanonicalRewrite());
    }
}
