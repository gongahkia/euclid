package com.euclid;

import com.euclid.exception.Diagnostic;
import com.euclid.lang.EuclidAliasHandling;
import com.euclid.lang.EuclidCanonicalizationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanonicalizationContractTest {

    @Test
    public void canonicalizationSkipsStringsAndComments() {
        String source = "mathtext(\"INF choose\") # proper_subset stays a comment\nINF // choose stays a comment";

        String canonical = Transpiler.canonicalize(source);

        assertEquals("mathtext(\"INF choose\") # proper_subset stays a comment\nINFINITY // choose stays a comment", canonical);
    }

    @Test
    public void canonicalizationReportsExactAliasLocations() {
        EuclidCanonicalizationResult result = Transpiler.canonicalizeWithMetadata("x = INF\nchoose(n, k)");

        assertEquals("x = INFINITY\nbinom(n, k)", result.canonicalSource());
        assertEquals(2, result.aliasOccurrences().size());
        assertEquals("INF", result.aliasOccurrences().get(0).alias());
        assertEquals(1, result.aliasOccurrences().get(0).line());
        assertEquals(5, result.aliasOccurrences().get(0).column());
        assertEquals("choose", result.aliasOccurrences().get(1).alias());
        assertEquals(2, result.aliasOccurrences().get(1).line());
        assertEquals(1, result.aliasOccurrences().get(1).column());
    }

    @Test
    public void strictAliasHandlingRejectsAliasesAtTheirExactLocations() {
        TranspileResult result = Transpiler.transpileWithDiagnostics(
                "x = INF\nchoose(n, k)",
                false,
                com.euclid.transpiler.MathMode.NONE,
                EuclidAliasHandling.ERROR);

        assertTrue(result.hasErrors());
        assertEquals(2, result.diagnostics().stream().filter(d -> "alias.noncanonical".equals(d.getCode())).count());
        Diagnostic first = result.diagnostics().stream()
                .filter(diagnostic -> "alias.noncanonical".equals(diagnostic.getCode()) && diagnostic.getLine() == 1)
                .findFirst()
                .orElseThrow();
        assertEquals(5, first.getColumn());
        assertEquals(Diagnostic.Severity.ERROR, first.getSeverity());
    }

    @Test
    public void precedenceKeepsImplicitMultiplyAndFractionsTighterThanAddition() throws Exception {
        assertEquals("2x^{2} + 1", Transpiler.transpile("2x^2 + 1"));
        assertEquals("\\frac{a}{b} + c", Transpiler.transpile("a \\\\ b + c"));
    }
}
