package com.euclid;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Specification tests that verify the implementation matches doc/syntax.md.
 * Each test corresponds to a documented feature in the syntax guide.
 */
public class SyntaxSpecificationTest {

    // ========== CONSTANTS (from doc/syntax.md) ==========

    @Test
    public void testConstant_PI() throws Exception {
        assertEquals("\\pi", Transpiler.transpile("PI"));
    }

    @Test
    public void testConstant_E() throws Exception {
        assertEquals("e", Transpiler.transpile("E"));
    }

    @Test
    public void testConstant_I() throws Exception {
        assertEquals("i", Transpiler.transpile("I"));
    }

    @Test
    public void testConstant_GAMMA() throws Exception {
        assertEquals("\\gamma", Transpiler.transpile("GAMMA"));
    }

    @Test
    public void testConstant_PHI() throws Exception {
        assertEquals("\\phi", Transpiler.transpile("PHI"));
    }

    @Test
    public void testConstant_INFINITY() throws Exception {
        assertEquals("\\infty", Transpiler.transpile("INFINITY"));
    }

    @Test
    public void testConstant_ALPHA() throws Exception {
        assertEquals("\\alpha", Transpiler.transpile("ALPHA"));
    }

    @Test
    public void testConstant_BETA() throws Exception {
        assertEquals("\\beta", Transpiler.transpile("BETA"));
    }

    @Test
    public void testConstant_DELTA() throws Exception {
        assertEquals("\\delta", Transpiler.transpile("DELTA"));
    }

    @Test
    public void testConstant_EPSILON() throws Exception {
        assertEquals("\\epsilon", Transpiler.transpile("EPSILON"));
    }

    @Test
    public void testConstant_ZETA() throws Exception {
        assertEquals("\\zeta", Transpiler.transpile("ZETA"));
    }

    @Test
    public void testConstant_ETA() throws Exception {
        assertEquals("\\eta", Transpiler.transpile("ETA"));
    }

    @Test
    public void testConstant_THETA() throws Exception {
        assertEquals("\\theta", Transpiler.transpile("THETA"));
    }

    @Test
    public void testConstant_KAPPA() throws Exception {
        assertEquals("\\kappa", Transpiler.transpile("KAPPA"));
    }

    @Test
    public void testConstant_LAMBDA() throws Exception {
        assertEquals("\\lambda", Transpiler.transpile("LAMBDA"));
    }

    @Test
    public void testConstant_MU() throws Exception {
        assertEquals("\\mu", Transpiler.transpile("MU"));
    }

    @Test
    public void testConstant_NU() throws Exception {
        assertEquals("\\nu", Transpiler.transpile("NU"));
    }

    @Test
    public void testConstant_XI() throws Exception {
        assertEquals("\\xi", Transpiler.transpile("XI"));
    }

    @Test
    public void testConstant_OMICRON() throws Exception {
        assertEquals("o", Transpiler.transpile("OMICRON"));
    }

    @Test
    public void testConstant_RHO() throws Exception {
        assertEquals("\\rho", Transpiler.transpile("RHO"));
    }

    @Test
    public void testConstant_SIGMA() throws Exception {
        assertEquals("\\sigma", Transpiler.transpile("SIGMA"));
    }

    @Test
    public void testConstant_TAU() throws Exception {
        assertEquals("\\tau", Transpiler.transpile("TAU"));
    }

    @Test
    public void testConstant_UPSILON() throws Exception {
        assertEquals("\\upsilon", Transpiler.transpile("UPSILON"));
    }

    @Test
    public void testConstant_CHI() throws Exception {
        assertEquals("\\chi", Transpiler.transpile("CHI"));
    }

    @Test
    public void testConstant_PSI() throws Exception {
        assertEquals("\\psi", Transpiler.transpile("PSI"));
    }

    @Test
    public void testConstant_OMEGA() throws Exception {
        assertEquals("\\omega", Transpiler.transpile("OMEGA"));
    }

    // ========== BASIC OPERATIONS (from doc/syntax.md) ==========

    @Test
    public void testBasicOperation_Addition() throws Exception {
        assertEquals("a + b", Transpiler.transpile("a + b"));
    }

    @Test
    public void testBasicOperation_Subtraction() throws Exception {
        assertEquals("a - b", Transpiler.transpile("a - b"));
    }

    @Test
    public void testBasicOperation_Division() throws Exception {
        assertEquals("a / b", Transpiler.transpile("a / b"));
    }

    @Test
    public void testBasicOperation_Multiplication() throws Exception {
        assertEquals("a * b", Transpiler.transpile("a * b"));
    }

    @Test
    public void testBasicOperation_Power() throws Exception {
        assertEquals("x^{y}", Transpiler.transpile("pow(x, y)"));
    }

    @Test
    public void testBasicOperation_Abs() throws Exception {
        assertEquals("|x|", Transpiler.transpile("abs(x)"));
    }

    @Test
    public void testBasicOperation_Ceil() throws Exception {
        assertEquals("\\lceil x \\rceil", Transpiler.transpile("ceil(x)"));
    }

    @Test
    public void testBasicOperation_Floor() throws Exception {
        assertEquals("\\lfloor x \\rfloor", Transpiler.transpile("floor(x)"));
    }

    @Test
    public void testBasicOperation_Mod() throws Exception {
        assertEquals("a \\mod b", Transpiler.transpile("mod(a, b)"));
    }

    @Test
    public void testBasicOperation_Gcd() throws Exception {
        assertEquals("\\gcd(a, b)", Transpiler.transpile("gcd(a, b)"));
    }

    @Test
    public void testBasicOperation_Lcm() throws Exception {
        assertEquals("\\text{lcm}(a, b)", Transpiler.transpile("lcm(a, b)"));
    }

    // ========== SYMBOLS (from doc/syntax.md) ==========

    @Test
    public void testSymbol_LessThan() throws Exception {
        assertEquals("a < b", Transpiler.transpile("lt(a, b)"));
    }

    @Test
    public void testSymbol_GreaterThan() throws Exception {
        assertEquals("a > b", Transpiler.transpile("gt(a, b)"));
    }

    @Test
    public void testSymbol_LessOrEqual() throws Exception {
        assertEquals("a \\leq b", Transpiler.transpile("leq(a, b)"));
    }

    @Test
    public void testSymbol_GreaterOrEqual() throws Exception {
        assertEquals("a \\geq b", Transpiler.transpile("geq(a, b)"));
    }

    @Test
    public void testSymbol_Approx() throws Exception {
        assertEquals("a \\approx b", Transpiler.transpile("approx(a, b)"));
    }

    @Test
    public void testSymbol_NotEqual() throws Exception {
        assertEquals("a \\neq b", Transpiler.transpile("neq(a, b)"));
    }

    @Test
    public void testSymbol_Equiv() throws Exception {
        assertEquals("a \\equiv b", Transpiler.transpile("equiv(a, b)"));
    }

    @Test
    public void testSymbol_PlusMinus() throws Exception {
        assertEquals("a \\pm b", Transpiler.transpile("pm(a, b)"));
    }

    @Test
    public void testSymbol_Times() throws Exception {
        assertEquals("a \\times b", Transpiler.transpile("times(a, b)"));
    }

    @Test
    public void testSymbol_Div() throws Exception {
        assertEquals("a \\div b", Transpiler.transpile("div(a, b)"));
    }

    @Test
    public void testSymbol_Cdot() throws Exception {
        assertEquals("a \\cdot b", Transpiler.transpile("cdot(a, b)"));
    }

    @Test
    public void testSymbol_Ast() throws Exception {
        assertEquals("a \\ast b", Transpiler.transpile("ast(a, b)"));
    }

    @Test
    public void testSymbol_Star() throws Exception {
        assertEquals("a \\star b", Transpiler.transpile("star(a, b)"));
    }

    @Test
    public void testSymbol_Circ() throws Exception {
        assertEquals("a \\circ b", Transpiler.transpile("circ(a, b)"));
    }

    @Test
    public void testSymbol_Bullet() throws Exception {
        assertEquals("a \\bullet b", Transpiler.transpile("bullet(a, b)"));
    }

    @Test
    public void testSymbol_Cap() throws Exception {
        assertEquals("a \\cap b", Transpiler.transpile("cap(a, b)"));
    }

    @Test
    public void testSymbol_Cup() throws Exception {
        assertEquals("a \\cup b", Transpiler.transpile("cup(a, b)"));
    }

    // ========== TRIGONOMETRIC AND HYPERBOLIC FUNCTIONS (from doc/syntax.md) ==========

    @Test
    public void testTrig_Sin() throws Exception {
        assertEquals("\\sin(x)", Transpiler.transpile("sin(x)"));
    }

    @Test
    public void testTrig_Cos() throws Exception {
        assertEquals("\\cos(x)", Transpiler.transpile("cos(x)"));
    }

    @Test
    public void testTrig_Tan() throws Exception {
        assertEquals("\\tan(x)", Transpiler.transpile("tan(x)"));
    }

    @Test
    public void testTrig_Csc() throws Exception {
        assertEquals("\\csc(x)", Transpiler.transpile("csc(x)"));
    }

    @Test
    public void testTrig_Sec() throws Exception {
        assertEquals("\\sec(x)", Transpiler.transpile("sec(x)"));
    }

    @Test
    public void testTrig_Cot() throws Exception {
        assertEquals("\\cot(x)", Transpiler.transpile("cot(x)"));
    }

    @Test
    public void testHyperbolic_Sinh() throws Exception {
        assertEquals("\\sinh(x)", Transpiler.transpile("sinh(x)"));
    }

    @Test
    public void testHyperbolic_Cosh() throws Exception {
        assertEquals("\\cosh(x)", Transpiler.transpile("cosh(x)"));
    }

    @Test
    public void testHyperbolic_Tanh() throws Exception {
        assertEquals("\\tanh(x)", Transpiler.transpile("tanh(x)"));
    }

    // ========== LOGARITHMIC AND EXPONENTIAL FUNCTIONS (from doc/syntax.md) ==========

    @Test
    public void testLog_WithBase() throws Exception {
        assertEquals("\\log_{base}(x)", Transpiler.transpile("log(x, base)"));
    }

    @Test
    public void testLog_Ln() throws Exception {
        assertEquals("\\ln(x)", Transpiler.transpile("ln(x)"));
    }

    @Test
    public void testLog_Exp() throws Exception {
        assertEquals("e^{x}", Transpiler.transpile("exp(x)"));
    }

    // ========== ROOTS AND FRACTIONS (from doc/syntax.md) ==========

    @Test
    public void testFraction_Backslash() throws Exception {
        assertEquals("\\frac{a}{b}", Transpiler.transpile("a \\\\ b"));
    }

    @Test
    public void testRoot_Sqrt() throws Exception {
        assertEquals("\\sqrt[n]{x}", Transpiler.transpile("sqrt(n, x)"));
    }

    @Test
    public void testPartial_Derivative() throws Exception {
        // Note: syntax.md shows: partial(f(x, y), x) but f(x,y) needs special handling
        // For now, test with simple variable
        assertTrue(Transpiler.transpile("partial(f, x)").contains("\\frac{\\partial}{\\partial x}"));
    }

    // ========== LIMITS, DERIVATIVES AND INTEGRALS (from doc/syntax.md) ==========

    @Test
    public void testLimit() throws Exception {
        String result = Transpiler.transpile("limit(f, x, a)");
        assertTrue(result.contains("\\lim_{x \\to a}"));
    }

    @Test
    public void testDerivative() throws Exception {
        String result = Transpiler.transpile("diff(f, x)");
        assertTrue(result.contains("\\frac{d}{dx}"));
    }

    @Test
    public void testIntegral_WithBounds() throws Exception {
        String result = Transpiler.transpile("integral(f, x, a, b)");
        assertTrue(result.contains("\\int_{a}^{b}") && result.contains("dx"));
    }

    // ========== SUMMATION AND PRODUCT NOTATION (from doc/syntax.md) ==========

    @Test
    public void testSummation() throws Exception {
        String result = Transpiler.transpile("sum(i, 1, n, f)");
        assertTrue(result.contains("\\sum_{i=1}^{n}"));
    }

    @Test
    public void testProduct() throws Exception {
        String result = Transpiler.transpile("prod(i, 1, n, f)");
        assertTrue(result.contains("\\prod_{i=1}^{n}"));
    }

    // ========== MATRICES AND VECTORS (from doc/syntax.md) ==========

    @Test
    public void testVector() throws Exception {
        String result = Transpiler.transpile("vector([a, b, c])");
        assertTrue(result.contains("\\begin{pmatrix}") && result.contains("\\end{pmatrix}"));
    }

    @Test
    public void testMatrix() throws Exception {
        String result = Transpiler.transpile("matrix([[a, b], [c, d]])");
        assertTrue(result.contains("\\begin{matrix}") && result.contains("\\end{matrix}"));
    }

    // ========== SET NOTATION (from doc/syntax.md) ==========

    @Test
    public void testSet_EmptySet() throws Exception {
        assertEquals("\\emptyset", Transpiler.transpile("emptyset"));
    }

    @Test
    public void testSet_Subset() throws Exception {
        assertEquals("a \\subset b", Transpiler.transpile("subset(a, b)"));
    }

    @Test
    public void testSet_Union() throws Exception {
        assertEquals("a \\cup b", Transpiler.transpile("union(a, b)"));
    }

    @Test
    public void testSet_Intersection() throws Exception {
        assertEquals("a \\cap b", Transpiler.transpile("intersection(a, b)"));
    }

    @Test
    public void testSet_ElementOf() throws Exception {
        assertEquals("a \\in b", Transpiler.transpile("element_of(a, b)"));
    }

    // ========== LOGIC SYMBOLS (from doc/syntax.md) ==========

    @Test
    public void testLogic_AND() throws Exception {
        assertEquals("\\land", Transpiler.transpile("AND"));
    }

    @Test
    public void testLogic_OR() throws Exception {
        assertEquals("\\lor", Transpiler.transpile("OR"));
    }

    @Test
    public void testLogic_NOT() throws Exception {
        assertEquals("\\neg", Transpiler.transpile("NOT"));
    }

    @Test
    public void testLogic_Implies() throws Exception {
        String result = Transpiler.transpile("implies(a, b)");
        assertTrue(result.contains("\\implies") || result.contains("\\Rightarrow"));
    }

    @Test
    public void testLogic_Iff() throws Exception {
        String result = Transpiler.transpile("iff(a, b)");
        assertTrue(result.contains("\\iff") || result.contains("\\Leftrightarrow"));
    }

    @Test
    public void testLogic_Forall() throws Exception {
        String result = Transpiler.transpile("forall(x)");
        assertTrue(result.contains("\\forall"));
    }

    @Test
    public void testLogic_Exists() throws Exception {
        String result = Transpiler.transpile("exists(x)");
        assertTrue(result.contains("\\exists"));
    }
}
