import com.euclid.TranspileResult;
import com.euclid.Transpiler;
import com.euclid.exception.Diagnostic;
import com.euclid.exception.LexerException;

public final class RunSmoke {
    private RunSmoke() {
    }

    public static void main(String[] args) throws Exception {
        assertEquals("x = y", Transpiler.transpile("x = y"), "equality");
        assertEquals("p \\land q", Transpiler.transpile("p AND q"), "infix logic");
        assertEquals("\\sum_{i=1}^{n} i", Transpiler.transpile("sum(i, i, 1, n)"), "summation");
        assertEquals("P(A \\mid B)", Transpiler.transpile("prob(given(A, B))"), "conditional probability");
        assertContains(Transpiler.transpile("piecewise(x, geq(x, 0), -x, lt(x, 0))"), "\\begin{cases}", "piecewise");
        assertEquals("INFINITY + subset(A, B) + binom(n, k)",
                Transpiler.canonicalize("INF + proper_subset(A, B) + choose(n, k)"),
                "canonicalization");
        assertThrowsLexer("x + @", "strict lexer");

        TranspileResult aliasResult = Transpiler.transpileWithDiagnostics("INF", false, com.euclid.transpiler.MathMode.NONE, false);
        assertTrue(!aliasResult.diagnostics().isEmpty(), "alias diagnostics");
        assertEquals(Diagnostic.Severity.WARNING.name(), aliasResult.diagnostics().get(0).getSeverity().name(), "alias severity");

        TranspileResult recoveryResult = Transpiler.transpileWithDiagnostics(
                "x = y\npiecewise(x, geq(x, 0), -x)\nz = w",
                false,
                com.euclid.transpiler.MathMode.NONE,
                false);
        assertTrue(recoveryResult.hasErrors(), "parser recovery errors");
        assertTrue(!recoveryResult.output().contains("[ERROR:"), "parser recovery output hygiene");

        System.out.println("Core Euclid smoke checks passed.");
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(label + " mismatch: expected '" + expected + "' but got '" + actual + "'");
        }
    }

    private static void assertContains(String actual, String expectedFragment, String label) {
        if (!actual.contains(expectedFragment)) {
            throw new IllegalStateException(label + " mismatch: expected fragment '" + expectedFragment + "' in '" + actual + "'");
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new IllegalStateException(label + " failed");
        }
    }

    private static void assertThrowsLexer(String input, String label) throws Exception {
        try {
            Transpiler.transpile(input);
            throw new IllegalStateException(label + " mismatch: expected LexerException");
        } catch (LexerException expected) {
            // expected
        }
    }
}
