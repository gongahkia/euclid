package com.euclid;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Property-based tests for strict Euclid expressions and Markdown document mode.
 */
public class PropertyBasedTest {

    @Provide
    Arbitrary<String> strictExpressions() {
        Arbitrary<String> atoms = Arbitraries.of("x", "y", "z", "n", "k", "2", "3", "PI", "ALPHA");
        Arbitrary<String> unaryFunctions = Combinators.combine(
                Arbitraries.of("sin", "cos", "abs", "sqrt"),
                atoms).as((function, argument) -> function + "(" + argument + ")");
        Arbitrary<String> powers = Combinators.combine(atoms, atoms)
                .as((base, exponent) -> "pow(" + base + ", " + exponent + ")");
        Arbitrary<String> binaryFunctions = Combinators.combine(
                Arbitraries.of("subset", "geq", "given"),
                atoms,
                atoms).as((function, left, right) -> function + "(" + left + ", " + right + ")");
        Arbitrary<String> infix = Combinators.combine(
                atoms,
                Arbitraries.of("+", "-", "*", "/", "="),
                atoms).as((left, operator, right) -> left + " " + operator + " " + right);
        Arbitrary<String> implicit = Combinators.combine(
                Arbitraries.integers().between(2, 9).map(String::valueOf),
                Arbitraries.of("x", "y", "z"))
                .as((coefficient, variable) -> coefficient + variable);
        Arbitrary<String> aggregates = Arbitraries.of("sum(i, i, 1, n)", "prod(i, i, 1, n)");

        return Arbitraries.oneOf(atoms, unaryFunctions, powers, binaryFunctions, infix, implicit, aggregates);
    }

    @Provide
    Arbitrary<String> aliasExpressions() {
        return Arbitraries.of("INF", "choose(n, k)", "proper_subset(A, B)");
    }

    @Provide
    Arbitrary<String> mathSpanExpressions() {
        return Arbitraries.of("x^2 + y^2", "2x^2 + 1", "sin(PI / 2)", "sum(i, i, 1, n)", "subset(A, B)");
    }

    @Property(tries = 40)
    void strictTranspilationIsDeterministic(@ForAll("strictExpressions") String expression) throws Exception {
        assertEquals(Transpiler.transpile(expression), Transpiler.transpile(expression));
    }

    @Property(tries = 40)
    void strictOutputsKeepBracesBalanced(@ForAll("strictExpressions") String expression) throws Exception {
        String output = Transpiler.transpile(expression);
        assertEquals(count(output, '{'), count(output, '}'));
        assertEquals(count(output, '('), count(output, ')'));
    }

    @Property(tries = 25)
    void canonicalizeDocumentOnlyTouchesExplicitMath(@ForAll("aliasExpressions") String aliasExpression) {
        String source = "Prose " + aliasExpression + " and $" + aliasExpression + "$ and `" + aliasExpression + "`";
        String canonical = Transpiler.canonicalizeDocument(source);

        assertTrue(canonical.contains("Prose " + aliasExpression + " and $"));
        assertTrue(canonical.contains("$" + Transpiler.canonicalize(aliasExpression) + "$"));
        assertTrue(canonical.contains("`" + aliasExpression + "`"));
    }

    @Property(tries = 25)
    void protectedMarkdownRegionsSurviveDocumentTranspilation(@ForAll("mathSpanExpressions") String expression) throws Exception {
        String source = "Inline $" + expression + "$ with `code $" + expression + "$` and "
                + "[link $" + expression + "$](https://example.com?q=$INF$)";
        String rendered = Transpiler.transpileDocument(source);

        assertTrue(rendered.contains("Inline $" + Transpiler.transpile(expression) + "$"));
        assertTrue(rendered.contains("`code $" + expression + "$`"));
        assertTrue(rendered.contains("[link $" + expression + "$](https://example.com?q=$INF$)"));
    }

    private int count(String text, char needle) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }
}
