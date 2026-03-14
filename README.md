[![](https://img.shields.io/badge/euclid_1.0-passing-light_green)](https://github.com/gongahkia/euclid/releases/tag/1.0)

# `Euclid`

Intuitive extended syntax for beautiful $\LaTeX$-style mathematical expressions in Markdown.

![](https://science4fun.info/wp-content/uploads/2021/06/Euclid.jpg)

## Motivation

Although [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) introduced [support](https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/writing-mathematical-expressions) for basic mathematical expressions in May 2022, the [MathJax](https://www.mathjax.org/) rendering library retains a confusing syntax. When combined with $\LaTeX$'s already bloated grammer, this obfuscates much of the logic of the expressions in a sea of dollarsigns, backslashes and curly braces.  

Ultimately, this reinforces the unnecessarily high barriers to entry for beginners seeking to integrate mathematical expressions into their markdown documentation.  

`Euclid` eliminates the friction associated with writing mathematical expressions in [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) by providing an intermediate `.ed` syntax with a straightforward ruleset for parsing mathematical expressions that mirrors the syntax of widespread entry-level programming languages like [Python](https://www.python.org/) and [Javascript](https://devdocs.io/javascript/).  

`.ed` script then transpiles to [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) and adheres to the [demands](https://en.wikibooks.org/wiki/LaTeX/Mathematics) of $\LaTeX$ and [MathJax](https://docs.mathjax.org/en/latest/). 

| Euclid Syntax (`.ed`) | Transpiled LaTeX Output | Rendered Result |
|---|---|---|
| `x = pm(-b, sqrt(pow(b, 2) - 4 * a * c)) \\ (2 * a)` | `$x = \frac{-b \pm \sqrt{b^{2} - 4 * a * c}}{2 * a}$` | $x = \frac{-b \pm \sqrt{b^{2} - 4 * a * c}}{2 * a}$ |

## Language Contract

`Euclid` now exposes a canonical language contract in code and uses it across the CLI and supporting tooling.

* canonical forms matter: `a / b` is inline division, while `a \\ b` is a fraction
* logical expressions support canonical infix syntax like `p AND q`, `p OR q`, and `NOT(p)`
* a small compatibility alias set is still accepted and can be canonicalized, including `INF -> INFINITY`, `choose -> binom`, and `proper_subset -> subset`, but alias usage now emits warnings
* pure CLI transpilation is strict Euclid; prose-heavy Markdown authoring is an explicit `--mixed` workflow
* CLI transpilation now prints structured warnings and refuses to write output files when the source still has errors
* capability metadata and canonical rewrites are available programmatically through the core transpiler API

## Usage

> [!IMPORTANT]  
> For a more detailed look at `Euclid`, check out the following documentation.
> * [Euclid Tutorial Series *(01-05)*](./tutorial)
> * [Euclid Language Syntax Specification](./tutorial/06_syntax.md)
> * [Euclid `.ed` Example Files](./example)

1. First run the below commands to install `Euclid` and build the standalone jars under `target/`.

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

3. Transpile `.ed` files to `.md` with $\LaTeX$ using `target/euclid-transpiler.jar`.

```console
$ java -jar target/euclid-transpiler.jar input.ed output.md
```

For prose-heavy Markdown documents with embedded Euclid expressions, use mixed mode:

```console
$ java -jar target/euclid-transpiler.jar --mixed input.ed output.md
```

Mixed mode is intentionally conservative: it only rewrites obvious Euclid candidates, leaves existing inline code and `$...$` spans untouched, and no longer rewrites bare constants such as `INF` inside prose.

4. Check or canonicalize Euclid source without transpiling it.

```console
$ java -jar target/euclid-transpiler.jar --check input.ed
$ java -jar target/euclid-transpiler.jar --canonicalize input.ed normalized.ed
```

5. Additionally launch the `Euclid` REPL with `target/euclid-repl.jar` to test `.ed` expressions.

```console
$ java -jar target/euclid-repl.jar
$ >>> x^2 + 2*x + 1
$ LaTeX: x^{2} + 2x + 1
$ >>> integral(x^2, x)
$ LaTeX: \int x^{2} \, dx
$ >>> :quit
```

## Real User Flows

`Euclid` is optimized for three concrete workflows.

* formula translation: turn readable Euclid into exact LaTeX without writing raw backslashes and braces
* Markdown authoring: write prose and obvious Euclid expressions together, then use mixed mode plus diagnostics to keep valid output without touching protected Markdown spans
* notation audit: inspect the capability manifest, canonical spellings, signatures, and aliases before publishing

## Verification

For a local core check that does not depend on Maven, run:

```console
$ bash scripts/verify_core.sh
```

This compiles the core Java sources with `java` source-file mode and runs a smoke suite against the canonical writer-facing flows.

## References

* [Symbolica](https://github.com/benruijl/symbolica) by [benruijl](https://github.com/benruijl)
