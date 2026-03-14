# `Euclid` syntax guide

This document is the canonical language contract for the current `Euclid` DSL.

## Introduction

`Euclid` source is usually saved with the `.ed` extension.

Pure Euclid parsing is strict by default. For prose-heavy Markdown documents, use mixed mode so normal text is preserved and obvious Euclid expressions are transpiled in place.

The language is case-sensitive:

* constants and named sets use uppercase tokens such as `PI`, `INFINITY`, and `REALS`
* most functions use lowercase tokens such as `sqrt`, `integral`, and `matrix`
* logical infix keywords are uppercase: `AND`, `OR`, `NOT`

## Canonical rules

Use these forms in new source:

* `/` is inline division, while `\\` creates a fraction
* `=` is infix equality
* `AND` and `OR` are infix logical operators, while `NOT(expr)` is the clearest unary negation form
* `sum(expr, var, lower, upper)` and `prod(expr, var, lower, upper)` are the canonical aggregate forms
* `vector([a, b, c])` and `matrix([[a, b], [c, d]])` are the preferred collection forms
* conditional probability is written as `prob(given(A, B))`
* unexpected characters are rejected in strict mode instead of being silently treated as text

## Precedence and associativity

`Euclid` parses operators in this order, from highest precedence to lowest:

1. function calls and postfix factorial: `sin(x)`, `n!`
2. powers and subscripts: `x^2`, `a_i`
3. multiplication family: implicit multiplication, `*`, `/`, `\\`, and infix `dot`
4. addition and subtraction: `+`, `-`
5. logical conjunction: `AND`
6. logical disjunction: `OR`
7. equality: `=`

`^`, `_`, and chained binary operators associate from left to right in the current parser implementation except where grouping forces a different structure. Use parentheses whenever intent could be ambiguous.

Comparison operators such as `<` and `>=` are written with helper functions like `lt(a, b)` and `geq(a, b)` rather than infix tokens.

## Constants and named sets

| Euclid | Output |
| :--- | :--- |
| `PI` | `\pi` |
| `E` | `e` |
| `I` | `i` |
| `INFINITY` | `\infty` |
| `emptyset` | `\emptyset` |
| `NATURALS` | `\mathbb{N}` |
| `INTEGERS` | `\mathbb{Z}` |
| `RATIONALS` | `\mathbb{Q}` |
| `REALS` | `\mathbb{R}` |
| `COMPLEXES` | `\mathbb{C}` |

## Greek letters

Uppercase token names transpile to the corresponding lowercase Greek letter.

| Euclid | Output |
| :--- | :--- |
| `ALPHA` | `\alpha` |
| `BETA` | `\beta` |
| `DELTA` | `\delta` |
| `EPSILON` | `\epsilon` |
| `GAMMA` | `\gamma` |
| `LAMBDA` | `\lambda` |
| `MU` | `\mu` |
| `PHI` | `\phi` |
| `SIGMA` | `\sigma` |
| `THETA` | `\theta` |
| `OMEGA` | `\omega` |

## Arithmetic and numeric helpers

| Euclid | Output |
| :--- | :--- |
| `a + b` | `a + b` |
| `a - b` | `a - b` |
| `a * b` | `a * b` |
| `a / b` | `a / b` |
| `a \\ b` | `\frac{a}{b}` |
| `pow(a, b)` or `a^b` | `a^{b}` |
| `abs(x)` | `|x|` |
| `ceil(x)` | `\lceil x \rceil` |
| `floor(x)` | `\lfloor x \rfloor` |
| `mod(a, b)` | `a \mod b` |
| `gcd(a, b)` | `\gcd(a, b)` |
| `lcm(a, b)` | `\text{lcm}(a, b)` |
| `n!` | `n!` |
| `binom(n, k)` | `\binom{n}{k}` |

Implicit multiplication is supported:

| Euclid | Output |
| :--- | :--- |
| `2x` | `2x` |
| `2(x + 1)` | `2(x + 1)` |
| `(a)(b)` | `(a)(b)` |

## Comparison and symbolic operators

These forms remain function-based:

| Euclid | Output |
| :--- | :--- |
| `lt(a, b)` | `a < b` |
| `gt(a, b)` | `a > b` |
| `leq(a, b)` | `a \leq b` |
| `geq(a, b)` | `a \geq b` |
| `approx(a, b)` | `a \approx b` |
| `neq(a, b)` | `a \neq b` |
| `equiv(a, b)` | `a \equiv b` |
| `pm(a, b)` | `a \pm b` |
| `times(a, b)` | `a \times b` |
| `div(a, b)` | `a \div b` |
| `cdot(a, b)` | `a \cdot b` |
| `ast(a, b)` | `a \ast b` |
| `star(a, b)` | `a \star b` |
| `circ(a, b)` | `a \circ b` |
| `bullet(a, b)` | `a \bullet b` |
| `cap(a, b)` | `a \cap b` |
| `cup(a, b)` | `a \cup b` |

Infix `dot` is also supported:

| Euclid | Output |
| :--- | :--- |
| `u dot v` | `u \cdot v` |

## Trigonometric, logarithmic, and root functions

| Euclid | Output |
| :--- | :--- |
| `sin(x)` | `\sin(x)` |
| `cos(x)` | `\cos(x)` |
| `tan(x)` | `\tan(x)` |
| `csc(x)` | `\csc(x)` |
| `sec(x)` | `\sec(x)` |
| `cot(x)` | `\cot(x)` |
| `sinh(x)` | `\sinh(x)` |
| `cosh(x)` | `\cosh(x)` |
| `tanh(x)` | `\tanh(x)` |
| `csch(x)` | `\operatorname{csch}(x)` |
| `sech(x)` | `\operatorname{sech}(x)` |
| `coth(x)` | `\coth(x)` |
| `arcsin(x)` | `\arcsin(x)` |
| `arccos(x)` | `\arccos(x)` |
| `arctan(x)` | `\arctan(x)` |
| `arccsc(x)` | `\operatorname{arccsc}(x)` |
| `arcsec(x)` | `\operatorname{arcsec}(x)` |
| `arccot(x)` | `\operatorname{arccot}(x)` |
| `log(x, base)` | `\log_{base}(x)` |
| `ln(x)` | `\ln(x)` |
| `exp(x)` | `e^{x}` |
| `sqrt(x)` | `\sqrt{x}` |
| `sqrt(n, x)` | `\sqrt[n]{x}` |

## Calculus

| Euclid | Output |
| :--- | :--- |
| `limit(expr, var, point)` | `\lim_{var \to point} expr` |
| `diff(expr, var)` | `\frac{d}{dvar} expr` |
| `partial(expr, var)` | `\frac{\partial}{\partial var} expr` |
| `integral(expr, var)` | `\int expr \, dvar` |
| `integral(expr, var, lower, upper)` | `\int_{lower}^{upper} expr \, dvar` |
| `sum(expr, var, lower, upper)` | `\sum_{var=lower}^{upper} expr` |
| `prod(expr, var, lower, upper)` | `\prod_{var=lower}^{upper} expr` |

Extrema and limit-family tokens are unary in the current implementation:

| Euclid | Output |
| :--- | :--- |
| `min(S)` | `\min(S)` |
| `max(S)` | `\max(S)` |
| `sup(S)` | `\sup(S)` |
| `inf(S)` | `\inf(S)` |
| `limsup(a_n)` | `\limsup(a_n)` |
| `liminf(a_n)` | `\liminf(a_n)` |

## Vectors, matrices, and linear algebra

Preferred collection forms:

| Euclid | Output |
| :--- | :--- |
| `vector([a, b, c])` | column vector |
| `matrix([[a, b], [c, d]])` | matrix with explicit row list |
| `matrix([a, b], [c, d])` | compatibility row form |

Other linear algebra helpers:

| Euclid | Output |
| :--- | :--- |
| `norm(x)` | `\|x\|` |
| `norm(x, p)` | `\|x\|_{p}` |
| `inner(x, y)` | `\langle x, y \rangle` |
| `det(A)` | `\det(A)` |
| `trace(A)` | `\text{tr}(A)` |
| `dim(V)` | `\dim(V)` |
| `rank(A)` | `\text{rank}(A)` |
| `ker(A)` | `\ker(A)` |
| `transpose(A)` | `A^{T}` |
| `inverse(A)` | `A^{-1}` |
| `grad(f)` | `\nabla f` |
| `divergence(F)` | `\nabla \cdot F` |
| `curl(F)` | `\nabla \times F` |
| `laplacian(f)` | `\nabla^{2} f` |

## Sets and logic

Set helpers:

| Euclid | Output |
| :--- | :--- |
| `subset(A, B)` | `A \subset B` |
| `supset(A, B)` | `A \supset B` |
| `subseteq(A, B)` | `A \subseteq B` |
| `supseteq(A, B)` | `A \supseteq B` |
| `union(A, B)` | `A \cup B` |
| `intersection(A, B)` | `A \cap B` |
| `set_diff(A, B)` | `A \setminus B` |
| `element_of(x, A)` | `x \in A` |
| `not_element_of(x, A)` | `x \notin A` |

Logical helpers:

| Euclid | Output |
| :--- | :--- |
| `p AND q` | `p \land q` |
| `p OR q` | `p \lor q` |
| `NOT(p)` | `\neg (p)` |
| `implies(p, q)` | `p \implies q` |
| `iff(p, q)` | `p \iff q` |
| `forall(x, P(x))` | `\forall x \, P(x)` |
| `exists(x, P(x))` | `\exists x \, P(x)` |

Use parentheses around complex quantified predicates for readability:

```euclid
forall(x, implies(element_of(x, A), element_of(x, B)))
exists(y, P(y) AND Q(y))
```

`AND(p, q)`, `OR(p, q)`, and `NOT(p)` remain accepted as compatibility aliases for the infix logical operators.

## Probability and statistics

| Euclid | Output |
| :--- | :--- |
| `prob(A)` | `P(A)` |
| `prob(given(A, B))` | `P(A \mid B)` |
| `expect(X)` | `\mathbb{E}[X]` |
| `var(X)` | `\text{Var}(X)` |
| `cov(X, Y)` | `\text{Cov}(X, Y)` |

## Layout, text, and visual notation

Decorations:

| Euclid | Output |
| :--- | :--- |
| `hat(x)` | `\hat{x}` |
| `tilde(x)` | `\tilde{x}` |
| `bar(x)` | `\bar{x}` |
| `vec(x)` | `\vec{x}` |
| `dot(x)` | `\dot{x}` |
| `ddot(x)` | `\ddot{x}` |
| `overline(x)` | `\overline{x}` |
| `underline(x)` | `\underline{x}` |
| `boxed(x)` | `\boxed{x}` |
| `cancel(x)` | `\cancel{x}` |

Text-aware helpers:

| Euclid | Output |
| :--- | :--- |
| `mathtext("sample mean")` | `\text{sample mean}` |
| `underbrace(x, "n terms")` | labeled underbrace |
| `overbrace(x, "n terms")` | labeled overbrace |

Strings use double quotes.

Structured layout helpers:

| Euclid | Output |
| :--- | :--- |
| `piecewise(expr1, cond1, expr2, cond2, ...)` | `cases` environment |
| `cases(expr1, cond1, expr2, cond2, ...)` | `cases` environment |
| `align(eq1, eq2, ...)` | `align*` environment |
| `system(eq1, eq2, ...)` | aligned system |

## Arrows, dots, proof, and geometry tokens

Standalone tokens transpile directly:

| Euclid | Output |
| :--- | :--- |
| `rightarrow` | `\rightarrow` |
| `leftarrow` | `\leftarrow` |
| `leftrightarrow` | `\leftrightarrow` |
| `mapsto` | `\mapsto` |
| `Rightarrow` | `\Rightarrow` |
| `Leftarrow` | `\Leftarrow` |
| `Leftrightarrow` | `\Leftrightarrow` |
| `ldots` | `\ldots` |
| `cdots` | `\cdots` |
| `vdots` | `\vdots` |
| `ddots` | `\ddots` |
| `therefore` | `\therefore` |
| `because` | `\because` |
| `qed` | `\blacksquare` |
| `perp` | `\perp` |
| `parallel` | `\parallel` |
| `angle` | `\angle` |
| `triangle` | `\triangle` |
| `cong` | `\cong` |
| `sim` | `\sim` |
| `propto` | `\propto` |
| `hbar` | `\hbar` |
| `nabla` | `\nabla` |
| `ell` | `\ell` |

## Mixed-content mode

When mixed mode is enabled, normal prose is preserved and obvious Euclid-like expressions are transpiled in place. This is intended for Markdown authoring workflows where text and math live in the same document, but it is not the core DSL contract.

For best results:

* keep complex expressions on their own line
* use canonical spellings so diagnostics and rewrite suggestions stay accurate
* prefer explicit grouping for long logic, fraction, and aggregate expressions
* keep existing inline code and `$...$` LaTeX spans as-is; mixed mode intentionally does not rewrite inside those protected regions
* do not rely on bare constants in prose being auto-detected; write a real Euclid expression such as `PI / 2` or use strict `.ed` input instead

## Compatibility aliases

The transpiler still accepts a small compatibility surface and can rewrite it to canonical form. Using these aliases emits a warning-level diagnostic so they do not silently become part of the preferred DSL:

| Alias | Canonical form |
| :--- | :--- |
| `INF` | `INFINITY` |
| `choose(n, k)` | `binom(n, k)` |
| `proper_subset(A, B)` | `subset(A, B)` |
| `AND(p, q)` | `p AND q` |
| `OR(p, q)` | `p OR q` |

If you want stable docs, editor support, and predictable canonicalization, prefer the canonical forms shown throughout this guide.
