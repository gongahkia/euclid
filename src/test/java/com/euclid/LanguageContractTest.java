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
    public void testBracketCompatibleVectorsAndMatricesStillRender() throws Exception {
        assertTrue(Transpiler.transpile("vector([a, b, c])").contains("\\begin{pmatrix}"));
        assertTrue(Transpiler.transpile("matrix([[a, b], [c, d]])").contains("a & b"));
    }

    @Test
    public void testConditionalProbabilityAndMathTextAreSupported() throws Exception {
        assertEquals("P(A \\mid B)", Transpiler.transpile("prob(given(A, B))"));
        assertEquals("\\text{sample mean}", Transpiler.transpile("mathtext(\"sample mean\")"));
    }

    @Test
    public void testInfixDotNotationIsSupported() throws Exception {
        assertEquals("u \\cdot v", Transpiler.transpile("u dot v"));
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
