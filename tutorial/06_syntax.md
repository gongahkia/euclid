# `Euclid` syntax guide

## Table of Contents

1. [Introduction](#introduction)
2. [Constants](#constants)
3. [Number Sets](#number-sets)
4. [Basic Operations](#basic-operations)
5. [Subscripts and Superscripts](#subscripts-and-superscripts)
6. [Factorial and Binomial](#factorial-and-binomial)
7. [Implicit Multiplication](#implicit-multiplication)
8. [Symbols](#symbols)
9. [Arrows](#arrows)
10. [Dot Sequences](#dot-sequences)
11. [Trigonometric and Hyperbolic Functions](#trigonometric-and-hyperbolic-functions)
12. [Inverse Trigonometric Functions](#inverse-trigonometric-functions)
13. [Logarithmic and Exponential Functions](#logarithmic-and-exponential-functions)
14. [Roots and Fractions](#roots-and-fractions)
15. [Limits, Derivatives and Integrals](#limits-derivatives-and-integrals)
16. [Extrema](#extrema)
17. [Summation and Product Notation](#summation-and-product-notation)
18. [Matrices and Vectors](#matrices-and-vectors)
19. [Norms and Inner Products](#norms-and-inner-products)
20. [Linear Algebra](#linear-algebra)
21. [Vector Calculus](#vector-calculus)
22. [Set Notation](#set-notation)
23. [Logic Symbols](#logic-symbols)
24. [Proof Notation](#proof-notation)
25. [Geometry](#geometry)
26. [Probability and Statistics](#probability-and-statistics)
27. [Visual Notation](#visual-notation)

## Introduction

* save your Euclid files with the `.ed` file extension
* standard Markdown is read normally, only the extended syntax below is transpiled to the corresponding MathJax
* for IDE syntax highlighting, it is more convenient to match the language syntax to Markdown
* Euclid ships with a built-in LSP server for editor integration (`java -jar target/euclid-lsp.jar`)
* the transpiler supports error recovery — valid expressions still transpile while errors are reported as diagnostics

## Constants

Commonly used constants are specified in Uppercase by default.

| Euclid representation | MathJax equivalent |
| :--- | :--- |
| `PI` | $\pi$ |
| `E` | $e$ |
| `I` | $i$ |
| `GAMMA` | $\gamma$ |
| `PHI` | $\phi$ |
| `INFINITY` | $\infty$ |
| `ALPHA` | $\alpha$ |
| `BETA` | $\beta$ |
| `DELTA` | $\delta$ |
| `EPSILON` | $\epsilon$ |
| `ZETA` | $\zeta$ |
| `ETA` | $\eta$ |
| `THETA` | $\theta$ |
| `KAPPA` | $\kappa$ |
| `LAMBDA` | $\lambda$ |
| `MU` | $\mu$ |
| `NU` | $\nu$ |
| `XI` | $\xi$ |
| `OMICRON` | $o$ |
| `RHO` | $\rho$ |
| `SIGMA` | $\sigma$ |
| `TAU` | $\tau$ |
| `UPSILON` | $\upsilon$ |
| `CHI` | $\chi$ |
| `PSI` | $\psi$ |
| `OMEGA` | $\omega$ |

## Number Sets

| Euclid representation | MathJax equivalent |
| :--- | :--- |
| `NATURALS` | $\mathbb{N}$ |
| `INTEGERS` | $\mathbb{Z}$ |
| `RATIONALS` | $\mathbb{Q}$ |
| `REALS` | $\mathbb{R}$ |
| `COMPLEXES` | $\mathbb{C}$ |

## Basic Operations

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `+` | $+$ |
| `-` | $-$ |
| `/` | $/$ |
| `*` | $*$ |
| `pow(x, y)` | $x^y$ |
| `x^y` | $x^{y}$ |
| `abs(x)` | $|x|$ |
| `ceil(x)` | $\lceil x \rceil $ |
| `floor(x)` | $\lfloor x \rfloor$ |
| `mod(a, b)` | $a \mod b$ |
| `gcd(a, b)` | $\gcd(a, b)$ |
| `lcm(a, b)` | $\text{lcm}(a, b)$ |

## Subscripts and Superscripts

Use `_` for subscripts and `^` for superscripts directly.

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `x_1` | $x_{1}$ |
| `x_i` | $x_{i}$ |
| `a_i^2` | $a_{i}^{2}$ |
| `x^2` | $x^{2}$ |

## Factorial and Binomial

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `n!` | $n!$ |
| `binom(n, k)` | $\binom{n}{k}$ |

## Implicit Multiplication

Euclid infers multiplication from context, just like mathematical notation.

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `2x` | $2x$ |
| `2(x + 1)` | $2(x + 1)$ |
| `(a)(b)` | $(a)(b)$ |

## Symbols

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `lt(a, b)` | $a < b$ |
| `gt(a, b)` | $a > b$ |
| `leq(a, b)` | $a \leq b$ |
| `geq(a, b)` | $a \geq b$ |
| `approx(a, b)` | $a \approx b$ |
| `neq(a, b)` | $a \neq b$ |
| `equiv(a, b)` | $a \equiv b$ |
| `pm(a, b)` | $a \pm b$ |
| `times(a, b)` | $a \times b$ |
| `div(a, b)` | $a \div b$ |
| `cdot(a, b)` | $a \cdot b$ |
| `ast(a, b)` | $a \ast b$ |
| `star(a, b)` | $a \star b$ |
| `circ(a, b)` | $a \circ b$ |
| `bullet(a, b)` | $a \bullet b$ |
| `cap(a, b)` | $a \cap b$ |
| `cup(a, b)` | $a \cup b$ |

## Arrows

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `rightarrow` | $\rightarrow$ |
| `leftarrow` | $\leftarrow$ |
| `Rightarrow` | $\Rightarrow$ |
| `mapsto` | $\mapsto$ |

## Dot Sequences

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `ldots` | $\ldots$ |
| `cdots` | $\cdots$ |
| `vdots` | $\vdots$ |
| `ddots` | $\ddots$ |

## Trigonometric and Hyperbolic Functions

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `sin(x)` | $\sin(x)$ |
| `cos(x)` | $\cos(x)$ |
| `tan(x)` | $\tan(x)$ |
| `csc(x)` | $\csc(x)$ |
| `sec(x)` | $\sec(x)$ |
| `cot(x)` | $\cot(x)$ |
| `sinh(x)` | $\sinh(x)$ |
| `cosh(x)` | $\cosh(x)$ |
| `tanh(x)` | $\tanh(x)$ |

## Inverse Trigonometric Functions

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `arcsin(x)` | $\arcsin(x)$ |
| `arccos(x)` | $\arccos(x)$ |
| `arctan(x)` | $\arctan(x)$ |
| `arccsc(x)` | $\text{arccsc}(x)$ |
| `arcsec(x)` | $\text{arcsec}(x)$ |
| `arccot(x)` | $\text{arccot}(x)$ |

## Logarithmic and Exponential Functions

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `log(x, base)` | $\log_{base}(x)$ |
| `ln(x)` | $\ln(x)$ |
| `exp(x)` | $e^x$ |

## Roots and Fractions

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `a \\ b` | $\frac{a}{b}$ |
| `partial(f(x, y), x)` | $\frac{\partial}{\partial x} f(x, y)$ |
| `sqrt(x)` | $\sqrt{x}$ |
| `sqrt(n, x)` | $\sqrt[n]{x}$ |

## Limits, Derivatives and Integrals

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `limit(f(x), x, a)` | $\lim_{{x \to a}} f(x)$ |
| `diff(f(x), x)` | $\frac{d}{dx} f(x)$ |
| `partial(f(x, y), x)` | $\frac{\partial}{\partial x} f(x, y)$ |
| `integral(f(x), x)` | $\int f(x) \, dx$ |
| `integral(f(x), x, a, b)` | $\int_{a}^{b} f(x) \, dx$ |

## Extrema

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `min` | $\min$ |
| `max` | $\max$ |
| `sup` | $\sup$ |
| `inf` | $\inf$ |
| `limsup` | $\limsup$ |
| `liminf` | $\liminf$ |

## Summation and Product Notation

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `sum(i, 1, n, f(i))` | $\sum_{i=1}^n f(i)$ |
| `prod(i, 1, n, f(i))` | $\prod_{i=1}^n f(i)$ |

## Matrices and Vectors

Matrices use bracket-row notation with each row as a bracket group.

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `vector([a, b, c])` | $\begin{pmatrix} a \\ b \\ c \end{pmatrix}$ |
| `matrix([a, b], [c, d])` | $\begin{pmatrix} a & b \\ c & d \end{pmatrix}$ |

## Norms and Inner Products

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `norm(x)` | $\lVert x \rVert$ |
| `inner(x, y)` | $\langle x, y \rangle$ |

## Linear Algebra

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `det` | $\det$ |
| `trace` | $\text{trace}$ |
| `dim` | $\dim$ |
| `rank` | $\text{rank}$ |
| `ker` | $\ker$ |
| `transpose` | $^\top$ |

## Vector Calculus

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `grad` | $\nabla$ |
| `divergence` | $\nabla \cdot$ |
| `curl` | $\nabla \times$ |
| `laplacian` | $\nabla^2$ |

## Set Notation

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `emptyset` | $\emptyset$ |
| `subset(a, b)` | $a \subset b$ |
| `supset(a, b)` | $a \supset b$ |
| `subseteq(a, b)` | $a \subseteq b$ |
| `supseteq(a, b)` | $a \supseteq b$ |
| `union(a, b)` | $a \cup b$ |
| `intersection(a, b)` | $a \cap b$ |
| `set_diff(a, b)` | $a \setminus b$ |
| `element_of(a, b)` | $a \in b$ |
| `not_element_of(a, b)` | $a \notin b$ |

## Logic Symbols

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `AND` | $\land$ |
| `OR` | $\lor$ |
| `NOT` | $\neg$ |
| `implies(a, b)` | $a \implies b$ |
| `iff(a, b)` | $a \iff b$ |
| `forall(x, P(x))` | $\forall x \, P(x)$|
| `exists(x, P(x))` | $\exists x \, P(x)$|

## Proof Notation

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `therefore` | $\therefore$ |
| `because` | $\because$ |
| `qed` | $\blacksquare$ |

## Geometry

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `perp` | $\perp$ |
| `parallel` | $\parallel$ |
| `angle` | $\angle$ |
| `triangle` | $\triangle$ |
| `cong` | $\cong$ |
| `sim` | $\sim$ |

## Probability and Statistics

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `prob(A)` | $P(A)$ |
| `expect(X)` | $E[X]$ |
| `var(X)` | $\text{Var}(X)$ |
| `cov(X, Y)` | $\text{Cov}(X, Y)$ |

## Visual Notation

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `boxed(x)` | $\boxed{x}$ |
| `cancel(x)` | $\cancel{x}$ |
| `underbrace(x, label)` | $\underbrace{x}_{\text{label}}$ |
| `overbrace(x, label)` | $\overbrace{x}^{\text{label}}$ |
