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
    public void testDivisionAndFractionSemanticsStayDistinct() throws Exception {
        assertEquals("a / b", Transpiler.transpile("a / b"));
        assertEquals("\\frac{a}{b}", Transpiler.transpile("a \\\\ b"));
    }

    @Test
    public void testSetRelationsRemainExplicit() throws Exception {
        assertEquals("A \\subset B", Transpiler.transpile("subset(A, B)"));
        assertEquals("A \\subseteq B", Transpiler.transpile("subseteq(A, B)"));
        assertEquals("x \\notin A", Transpiler.transpile("not_element_of(x, A)"));
    }

    @Test
    public void testInfixDotNotationIsSupported() throws Exception {
        assertEquals("u \\cdot v", Transpiler.transpile("u dot v"));
    }

    @Test
    public void testAggregateOrderAndPrecedenceMatchTheContract() throws Exception {
        assertEquals("\\sum_{i=1}^{n} i", Transpiler.transpile("sum(i, i, 1, n)"));
        assertEquals("\\prod_{i=1}^{n} i", Transpiler.transpile("prod(i, i, 1, n)"));
        assertEquals(Transpiler.transpile("(p AND q) = r"), Transpiler.transpile("p AND q = r"));
        assertEquals(Transpiler.transpile("p = (q OR r)"), Transpiler.transpile("p = q OR r"));
    }

    @Test
    public void testStructuredLayoutHelpersAreDocumentedAndWorking() throws Exception {
        assertTrue(Transpiler.transpile("piecewise(x, geq(x, 0), -x, lt(x, 0))").contains("\\begin{cases}"));
        assertTrue(Transpiler.transpile("align(x = y, y = z)").contains("\\begin{align*}"));
        assertTrue(Transpiler.transpile("system(x = y, y = z)").contains("\\begin{cases}"));
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

        Optional<EuclidCapability> vector = manifest.capabilities().stream()
                .filter(capability -> capability.name().equals("vector"))
                .findFirst();
        assertTrue(vector.isPresent());
        assertTrue(vector.get().signature().display().contains("vector([a, b, c])"));
    }

    @Test
    public void testDiagnosticsSurfaceCanonicalRewrite() {
        TranspileResult result = Transpiler.transpileWithDiagnostics("INF", false, com.euclid.transpiler.MathMode.NONE, false);
        assertFalse(result.diagnostics().isEmpty());
        assertEquals("canonical.rewrite", result.diagnostics().get(0).getCode());
        assertEquals("INFINITY", result.diagnostics().get(0).getCanonicalRewrite());
    }
}
