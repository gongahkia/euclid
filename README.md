[![](https://img.shields.io/badge/euclid_1.0-passing-light_green)](https://github.com/gongahkia/euclid/releases/tag/1.0)
[![](https://img.shields.io/badge/euclid_2.0-passing-green)](https://github.com/gongahkia/euclid/releases/tag/2.0)

# `Euclid`

Intuitive extended syntax for beautiful $\LaTeX$-style mathematical expressions in Markdown.

![](https://science4fun.info/wp-content/uploads/2021/06/Euclid.jpg)

## Motivation

Although [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) introduced [support](https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/writing-mathematical-expressions) for basic mathematical expressions in May 2022, the [MathJax](https://www.mathjax.org/) rendering library retains a confusing syntax. When combined with $\LaTeX$'s already bloated grammer, this obfuscates much of the logic of the expressions in a sea of dollarsigns, backslashes and curly braces.  

Ultimately, this reinforces the unnecessarily high barriers to entry for beginners seeking to integrate mathematical expressions into their markdown documentation.  

`Euclid` eliminates the friction associated with writing mathematical expressions in [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) by providing an intermediate syntax (`.ed` script) identical to [Markdown](https://www.markdownguide.org/) with a straightforward ruleset for parsing mathematical expressions that mirrors the syntax of widespread entry-level programming languages like [Python](https://www.python.org/) and [Javascript](https://devdocs.io/javascript/).  

`.ed` script then transpiles to [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) and adheres to the [demands](https://en.wikibooks.org/wiki/LaTeX/Mathematics) of $\LaTeX$ and [MathJax](https://docs.mathjax.org/en/latest/). 

| Euclid Syntax (`.ed`) | Transpiled LaTeX Output | Rendered Result |
|---|---|---|
| `x = (-b +- sqrt(b^2 - 4*a*c)) / (2*a)` | `$x = \frac{-b \pm \sqrt{b^{2} - 4ac}}{2a}$` | $x = \frac{-b \pm \sqrt{b^{2} - 4ac}}{2a}$ |

## Usage

> [!IMPORTANT]  
> For a more detailed look at `Euclid`, check out the following documentation.
> * [Euclid Tutorial Series *(01-05)*](./tutorial)
> * [Euclid Language Syntax Specification](./tutorial/06_syntax.md)
> * [Euclid `.ed` Example Files](./example)

1. First run the below commands to install `Euclid` and create a standalone JAR at `target/euclid-2.0-SNAPSHOT.jar`.

```console
$ git clone https://github.com/gongahkia/euclid
$ cd euclid
$ mvn clean package
```

2. Optionally build `Euclid` from source with the following commands.

```console
$ git clone https://github.com/gongahkia/euclid
$ cd euclid
$ mvn clean compile
```

3. Transpile `.ed` files to `.md` with $\LaTeX$.

```console
$ java -jar target/euclid-2.0-SNAPSHOT.jar input.ed output.md
```

4. Additionally launch the `Euclid` REPL to test `.ed` expressions.

```console
$ java -jar target/euclid-2.0-SNAPSHOT.jar
$ euclid> x^2 + 2*x + 1
$ LaTeX: x^{2} + 2x + 1
$ euclid> integral(x^2, x)
$ LaTeX: \int x^{2} \, dx
$ euclid> exit
```

## What's New

### Subscripts & Factorial

Use `_` for subscripts and `!` for factorials — no LaTeX boilerplate needed.

| Euclid Syntax | LaTeX Output |
|---|---|
| `x_1 + x_2` | `x_{1} + x_{2}` |
| `a_i^2` | `a_{i}^{2}` |
| `n!` | `n!` |
| `binom(n, k)` | `\binom{n}{k}` |

### Implicit Multiplication

Euclid now infers multiplication from context, just like mathematical notation.

| Euclid Syntax | LaTeX Output |
|---|---|
| `2x` | `2x` |
| `2(x + 1)` | `2(x + 1)` |
| `(a)(b)` | `(a)(b)` |

### Expanded Vocabulary

60+ new tokens across several categories:

| Category | Examples |
|---|---|
| Number sets | `NATURALS`, `INTEGERS`, `RATIONALS`, `REALS`, `COMPLEXES` |
| Greek letters | `ALPHA` through `OMEGA` (all 19 lowercase) |
| Arrows | `rightarrow`, `leftarrow`, `mapsto`, `Rightarrow` |
| Dot sequences | `ldots`, `cdots`, `vdots`, `ddots` |
| Proof notation | `therefore`, `because`, `qed` |
| Geometry | `perp`, `parallel`, `angle`, `triangle`, `cong`, `sim` |
| Inverse trig | `arcsin`, `arccos`, `arctan`, `arccsc`, `arcsec`, `arccot` |
| Extrema | `min`, `max`, `sup`, `inf`, `limsup`, `liminf` |
| Norms | `norm(x)`, `inner(x, y)` |
| Vector calculus | `grad`, `divergence`, `curl`, `laplacian` |
| Probability | `prob(A)`, `expect(X)`, `var(X)`, `cov(X, Y)` |
| Linear algebra | `det`, `trace`, `dim`, `rank`, `ker`, `transpose` |
| Visual | `boxed(x)`, `cancel(x)`, `underbrace(x, label)`, `overbrace(x, label)` |

### Matrix Syntax

Matrices now support bracket-row notation with proper column separators.

```
matrix([1, 2], [3, 4])
```

Transpiles to:

```latex
\begin{pmatrix} 1 & 2 \\ 3 & 4 \end{pmatrix}
```

### LSP Server

Euclid ships with a built-in [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) server for editor integration.

```console
$ java -jar target/euclid-lsp.jar
```

**Features:**

* **Diagnostics** — red squiggles for syntax errors as you type
* **Hover** — see the LaTeX output for the expression under your cursor
* **Completion** — auto-complete function names, constants, and Greek letters
* **Signature help** — parameter hints for functions like `integral(expr, var, lower, upper)`
* **Semantic tokens** — syntax highlighting for `.ed` files (functions, constants, operators, numbers, variables)

Connect any LSP-compatible editor (VS Code, Neovim, Emacs, etc.) to the server on stdin/stdout.

### Error Recovery

The transpiler no longer aborts on the first error. Valid expressions still transpile while errors are reported as diagnostics with graduated severity (error, warning, info).

## References

* [Symbolica](https://github.com/benruijl/symbolica) by [benruijl](https://github.com/benruijl)