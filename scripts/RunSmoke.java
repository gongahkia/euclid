import com.euclid.Transpiler;

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
}
