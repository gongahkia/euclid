# `Euclid` syntax guide

## Table of Contents

1. [Introduction](#introduction)
2. [Constants](#constants)
3. [Basic operations](#basic-operations)
4. [Symbols](#symbols)
5. [Trigonometric and Hyperbolic functions](#trigonometric-and-hyperbolic-functions)
6. [Logarithmic and Exponential functions](#logarithmic-and-exponential-functions)
7. [Roots and Fractions](#roots-and-fractions)
8. [Limits, Derivatives and Integrals](#limits%2C-derivatives-and-integrals)
9. [Summation and Product notation](#summation-and-product-notation)
10. [Matrices and Vectors](#matrices-and-vectors)
11. [Set notation](#set-notation)
12. [Logic symbols](#logic-symbols)

## Introduction

* save your Euclid files with the `.ed` file extension 
* standard Markdown is read normally, only the extended syntax below is transpiled to the corresponding MathJax 
* for IDE syntax highlighting, it is more convenient to match the language syntax to Markdown

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
| `PHI` | $\phi$ |
| `CHI` | $\chi$ |
| `PSI` | $\psi$ |
| `OMEGA` | $\omega$ |

## Basic operations

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `+` | $+$ |
| `-` | $-$ |
| `/` | $/$ |
| `*` | $*$ |
| `pow(x, y)` | $x^y$ |
| `abs(x)` | $|x|$ | 
| `ceil(x)` | $\lceil x \rceil $ |
| `floor(x)` | $\lfloor x \rfloor$ |
| `mod(a, b)` | $a \mod b$ |
| `gcd(a, b)` | $\gcd(a, b)$ |
| `lcm(a, b)` | $\text{lcm}(a, b)$ |

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

## Trigonometric and Hyperbolic functions

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `sin()` | $sin()$ |
| `cos()` | $cos()$ |
| `tan()` | $tan()$ |
| `csc(x)` | $\csc(x)$ |
| `sec(x)` | $\sec(x)$ |
| `cot(x)` | $\cot(x)$ |
| `sinh(x)` | $\sinh(x)$ |
| `cosh(x)` | $\cosh(x)$ |
| `tanh(x)` | $\tanh(x)$ |

## Logarithmic and Exponential functions

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
| `sqrt(n, x)` | $\sqrt[n]{x}$ |

## Limits, Derivatives and Integrals

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `limit(f(x), x, a)` | $\lim_{{x \to a}} f(x)$ |
| `diff(f(x), x)` | $\frac{d}{dx} f(x)$ |
| `partial(f(x, y), x)` | $\frac{\partial}{\partial x} f(x, y)$ |
| `integral(f(x), x, a, b)` | $\int_{a}^{b} f(x) \, dx$ |

## Summation and Product notation

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `sum(from i=1 to n) f(i)` | $\sum_{i=1}^n f(i)$ |
| `prod(from i=1 to n) f(i)` | $\prod_{i=1}^n f(i)$ |

## Matrices and Vectors

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `vector([a, b, c])` | $\begin{pmatrix} a \\ b \\ c \end{pmatrix}$ |
| `matrix([[a, b], [c, d]])` | $\begin{matrix} a & b \\ c & d \end{matrix}$ |

## Set notation

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

## Logic symbols

| Euclid lang syntax | MathJax equivalent |
| :--- | :--- |
| `AND` | $\land$ |
| `OR` | $\lor$ |
| `NOT` | $\neg$ |
| `implies(a, b)` | $a \implies b$ |
| `iff(a, b)` | $a \iff b$ |
| `forall(x, P(x))` | $\forall x \, P(x)$|
| `exists(x, P(x))` | $\exists x \, P(x)$|
