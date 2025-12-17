# Euclid vs LaTeX: Comparison Guide

This guide compares Euclid syntax with raw LaTeX, demonstrating when and why you might prefer Euclid for writing mathematical expressions in Markdown.

## Philosophy

**LaTeX** is powerful and standard, but requires remembering many backslash commands and braces.
**Euclid** aims to be more intuitive, using function-call syntax familiar from programming languages like Python, JavaScript, and MATLAB.

---

## Table of Contents

1. [Basic Constants](#basic-constants)
2. [Basic Operations](#basic-operations)
3. [Trigonometric Functions](#trigonometric-functions)
4. [Calculus](#calculus)
5. [Matrices and Vectors](#matrices-and-vectors)
6. [Set Theory and Logic](#set-theory-and-logic)
7. [Complex Expressions](#complex-expressions)
8. [When to Use Euclid](#when-to-use-euclid)
9. [When to Stick with LaTeX](#when-to-stick-with-latex)

---

## Basic Constants

| Description | Euclid | LaTeX |
|------------|--------|-------|
| Pi | `PI` | `\pi` |
| Euler's number | `E` | `e` |
| Imaginary unit | `I` | `i` |
| Golden ratio | `PHI` | `\phi` |
| Euler-Mascheroni constant | `GAMMA` | `\gamma` |
| Infinity | `INFINITY` | `\infty` |

**Why Euclid?** Constants use memorable ALL_CAPS names instead of backslash commands.

---

## Basic Operations

### Exponents

| Operation | Euclid | LaTeX |
|-----------|--------|-------|
| x squared | `pow(x, 2)` | `x^{2}` |
| x cubed | `pow(x, 3)` | `x^{3}` |
| x to the nth | `pow(x, n)` | `x^{n}` |
| 2 to the 10th | `pow(2, 10)` | `2^{10}` |

**Euclid advantage:** Consistent function call syntax. No need to remember when braces are required.

**LaTeX advantage:** More concise for simple exponents like `x^2`.

### Roots

| Operation | Euclid | LaTeX |
|-----------|--------|-------|
| Square root of x | `sqrt(2, x)` or `sqrt(x)` | `\sqrt{x}` |
| Cube root of x | `sqrt(3, x)` | `\sqrt[3]{x}` |
| nth root of x | `sqrt(n, x)` | `\sqrt[n]{x}` |

**Euclid advantage:** Uniform syntax with explicit root degree as first argument.

### Fractions

| Operation | Euclid | LaTeX |
|-----------|--------|-------|
| a over b | `a \\ b` | `\frac{a}{b}` |
| Complex fraction | `(x + 1) \\ (y - 1)` | `\frac{x + 1}{y - 1}` |

**Euclid advantage:** The `\\` operator is visual (division line) and doesn't require `\frac{}{}` syntax.

---

## Trigonometric Functions

| Function | Euclid | LaTeX |
|----------|--------|-------|
| Sine of x | `sin(x)` | `\sin(x)` |
| Cosine of x | `cos(x)` | `\cos(x)` |
| Tangent of x | `tan(x)` | `\tan(x)` |
| Sine inverse | `sin(x)` (same) | `\sin^{-1}(x)` or `\arcsin(x)` |
| Hyperbolic sine | `sinh(x)` | `\sinh(x)` |
| Hyperbolic cosine | `cosh(x)` | `\cosh(x)` |

**Euclid advantage:** No backslashes needed. Reads like code.

**LaTeX advantage:** Standard mathematical notation widely recognized.

---

## Calculus

### Derivatives

| Expression | Euclid | LaTeX |
|------------|--------|-------|
| Derivative of f(x) | `diff(f(x), x)` | `\frac{d}{dx} f(x)` |
| Partial derivative | `partial(f(x,y), x)` | `\frac{\partial f}{\partial x}` |

### Integrals

| Expression | Euclid | LaTeX |
|------------|--------|-------|
| Indefinite integral | `integral(f(x), x)` | `\int f(x) \, dx` |
| Definite integral (0 to 1) | `integral(f(x), x, 0, 1)` | `\int_{0}^{1} f(x) \, dx` |
| Double integral | `integral(integral(f(x,y), y), x)` | `\int \int f(x,y) \, dy \, dx` |

**Euclid advantage:** Clear function-call semantics with named parameters. Limits are explicit arguments.

**LaTeX advantage:** Traditional mathematical notation that mathematicians are trained to read.

### Limits

| Expression | Euclid | LaTeX |
|------------|--------|-------|
| Limit as x→0 | `limit(f(x), x, 0)` | `\lim_{x \to 0} f(x)` |
| Limit as x→∞ | `limit(f(x), x, INFINITY)` | `\lim_{x \to \infty} f(x)` |

### Summation and Products

| Expression | Euclid | LaTeX |
|------------|--------|-------|
| Sum from 1 to n | `sum(f(i), i, 1, n)` | `\sum_{i=1}^{n} f(i)` |
| Product from 1 to n | `prod(f(i), i, 1, n)` | `\prod_{i=1}^{n} f(i)` |
| Infinite sum | `sum(f(i), i, 1, INFINITY)` | `\sum_{i=1}^{\infty} f(i)` |

**Euclid advantage:** Consistent function syntax across sum, prod, integral, limit.

---

## Matrices and Vectors

### Vectors

| Description | Euclid | LaTeX |
|-------------|--------|-------|
| Column vector | `vector([1, 2, 3])` | `\begin{pmatrix} 1 \\ 2 \\ 3 \end{pmatrix}` |

### Matrices

| Description | Euclid | LaTeX |
|-------------|--------|-------|
| 2×2 matrix | `matrix([[1, 2], [3, 4]])` | `\begin{pmatrix} 1 & 2 \\ 3 & 4 \end{pmatrix}` |
| 3×3 matrix | `matrix([[1, 2, 3], [4, 5, 6], [7, 8, 9]])` | `\begin{pmatrix} 1 & 2 & 3 \\ 4 & 5 & 6 \\ 7 & 8 & 9 \end{pmatrix}` |

**Euclid advantage:** Uses familiar array/list syntax from programming. Easier for those coming from MATLAB, NumPy, etc.

**LaTeX advantage:** More control over matrix environments (pmatrix, bmatrix, vmatrix, etc.)

---

## Set Theory and Logic

### Sets

| Symbol | Euclid | LaTeX |
|--------|--------|-------|
| Empty set | `emptyset` | `\emptyset` or `\varnothing` |
| Element of | `element_of` | `\in` |
| Subset | `subset` | `\subset` or `\subseteq` |
| Union | `union` | `\cup` |
| Intersection | `intersection` | `\cap` |

### Logic

| Symbol | Euclid | LaTeX |
|--------|--------|-------|
| AND | `AND` | `\land` or `\wedge` |
| OR | `OR` | `\lor` or `\vee` |
| NOT | `NOT` | `\neg` or `\lnot` |
| Implies | `implies` | `\implies` or `\Rightarrow` |
| If and only if | `iff` | `\iff` or `\Leftrightarrow` |
| For all | `forall` | `\forall` |
| Exists | `exists` | `\exists` |

**Euclid advantage:** English-like keywords (`AND`, `OR`, `forall`, `exists`) are more readable for non-mathematicians.

---

## Complex Expressions

### Example 1: Quadratic Formula

**Euclid:**
```
x = (neg(b) pm sqrt(pow(b, 2) - 4 * a * c)) \\ (2 * a)
```

**LaTeX:**
```
x = \frac{-b \pm \sqrt{b^{2} - 4ac}}{2a}
```

**Comparison:**
- Euclid is more verbose but self-documenting
- LaTeX is more concise once you learn the syntax

### Example 2: Definite Integral of Sin

**Euclid:**
```
integral(sin(x), x, 0, PI)
```

**LaTeX:**
```
\int_{0}^{\pi} \sin(x) \, dx
```

**Comparison:**
- Euclid clearly shows the function call structure
- LaTeX is closer to traditional blackboard notation

### Example 3: Summation with Complex Terms

**Euclid:**
```
sum(pow(neg(1), i) \\ (2 * i + 1), i, 0, INFINITY)
```

**LaTeX:**
```
\sum_{i=0}^{\infty} \frac{(-1)^{i}}{2i + 1}
```

**Comparison:**
- Euclid makes nesting and operator precedence clearer
- LaTeX requires fewer characters for mathematicians who think in that notation

---

## When to Use Euclid

✅ **Choose Euclid when:**

1. **You're familiar with programming** (Python, JavaScript, MATLAB)
   - Euclid's function-call syntax will feel natural

2. **You're writing Markdown documents** with lots of inline math
   - Faster to type function names than backslash commands

3. **You want readability for non-mathematicians**
   - `integral(f(x), x, 0, 1)` is more self-explanatory than `\int_{0}^{1} f(x) \, dx`

4. **You're teaching or learning**
   - Function call syntax makes structure explicit
   - Easier to understand nesting and precedence

5. **You want consistency**
   - All operations follow `operation(arguments)` pattern
   - No need to remember different LaTeX command patterns

6. **You're using the REPL for quick math exploration**
   - Interactive testing of expressions
   - Immediate feedback on syntax

---

## When to Stick with LaTeX

✅ **Choose LaTeX when:**

1. **You're already fluent in LaTeX**
   - Muscle memory makes it faster than learning new syntax

2. **You need advanced typesetting features**
   - Precise alignment, custom environments
   - Specialized packages (TikZ, pgfplots, etc.)

3. **You're collaborating with mathematicians**
   - LaTeX is the lingua franca of academic mathematics
   - Everyone knows `\frac`, `\int`, `\sum`

4. **You need minimal output size**
   - LaTeX is often more concise: `x^2` vs `pow(x, 2)`

5. **You're writing a research paper**
   - Publishers expect LaTeX
   - Reference formatting, bibliography integration

6. **You need fine control over spacing and formatting**
   - `\,`, `\quad`, `\!` for manual spacing
   - Custom macros and command definitions

---

## Migration Tips

If you're moving from LaTeX to Euclid:

1. **Start with simple expressions** to get comfortable with function syntax
2. **Use the REPL** (`:help` for commands) to experiment
3. **Run with `--verbose`** to see how Euclid transpiles to LaTeX
4. **Keep a cheat sheet** of common translations handy
5. **Mix and match** - you can still use raw LaTeX when needed

---

## Summary Table: Quick Reference

| Category | Euclid Example | LaTeX Example |
|----------|----------------|---------------|
| Constant | `PI` | `\pi` |
| Power | `pow(x, 2)` | `x^{2}` |
| Fraction | `a \\ b` | `\frac{a}{b}` |
| Root | `sqrt(3, x)` | `\sqrt[3]{x}` |
| Trig | `sin(x)` | `\sin(x)` |
| Integral | `integral(f(x), x, 0, 1)` | `\int_{0}^{1} f(x) \, dx` |
| Sum | `sum(f(i), i, 1, n)` | `\sum_{i=1}^{n} f(i)` |
| Limit | `limit(f(x), x, 0)` | `\lim_{x \to 0} f(x)` |
| Matrix | `matrix([[1, 2], [3, 4]])` | `\begin{pmatrix} 1 & 2 \\ 3 & 4 \end{pmatrix}` |
| Logic AND | `AND` | `\land` |
| Set union | `union` | `\cup` |

---

## Try It Yourself

The best way to see if Euclid works for you is to try it:

```bash
# Install and try the REPL
git clone https://github.com/gongahkia/euclid.git
cd euclid
mvn package
java -jar target/euclid-repl.jar
```

Then type some expressions:

```
>>> PI + E
LaTeX: \pi + e

>>> integral(sin(x), x, 0, PI)
LaTeX: \int_{0}^{\pi} \sin(x) \, dx

>>> matrix([[1, 2], [3, 4]])
LaTeX: \begin{pmatrix} 1 & 2 \\ 3 & 4 \end{pmatrix}
```

---

## See Also

- [Syntax Reference](syntax.md) - Complete Euclid syntax guide
- [Tutorial Series](tutorial/) - Step-by-step tutorials
- [Developer Guide](DEVELOPER_GUIDE.md) - For extending Euclid
- [Examples](../example/) - Real-world examples

---

**Bottom Line:** Euclid doesn't replace LaTeX—it complements it by offering a more programming-friendly syntax for mathematical expressions in Markdown. Use whichever feels more natural for your workflow!
