![](https://github.com/gongahkia/euclid/actions/workflows/ci.yml/badge.svg)

# `Euclid`

Readable Euclid math inside Markdown, transpiled to MathJax-ready LaTeX.

![](https://science4fun.info/wp-content/uploads/2021/06/Euclid.jpg)

## Motivation

Although [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) introduced [support](https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/writing-mathematical-expressions) for basic mathematical expressions in May 2022, the [MathJax](https://www.mathjax.org/) rendering library retains a confusing syntax. When combined with $\LaTeX$'s already bloated grammer, this obfuscates much of the logic of the expressions in a sea of dollarsigns, backslashes and curly braces.  

Ultimately, this reinforces the unnecessarily high barriers to entry for beginners seeking to integrate mathematical expressions into their markdown documentation.  

`Euclid` reduces that friction by letting you keep Markdown as the outer document format while writing math spans in a cleaner, programming-language-inspired notation. Euclid then transpiles only those explicit math spans to MathJax-compatible LaTeX.

## An example

| Markdown + Euclid (`.ed`) | Transpiled Output | Rendered Result |
|---|---|---|
| ``The quadratic formula is `$x = (-b + sqrt(b^2 - 4*a*c)) \\ (2*a)$`.`` | ``The quadratic formula is `$x = \frac{-b + \sqrt{b^{2} - 4ac}}{2a}$`.`` | The quadratic formula is $x = \frac{-b + \sqrt{b^{2} - 4ac}}{2a}$. |

## Usage

> [!IMPORTANT]  
> For a more detailed look at `Euclid`, check out the following documentation.
> * [Euclid Tutorial Series *(01-05)*](./tutorial)
> * [Euclid Language Syntax Specification](./tutorial/06_syntax.md)
> * [Euclid `.ed` Example Files](./example)

1. Build the standalone jars.

```console
$ git clone https://github.com/gongahkia/euclid && cd euclid
$ mvn clean package
```

2. Optionally build `Euclid` from source with the following commands.

```console
$ git clone https://github.com/gongahkia/euclid
$ cd euclid
$ mvn clean compile
```

3. Transpile Markdown documents that contain explicit Euclid math spans.

```console
$ java -jar target/euclid-transpiler.jar input.ed output.md
```

4. Use `--strict` when the entire file is pure Euclid rather than Markdown.

```console
$ java -jar target/euclid-transpiler.jar --strict formula.ed formula.md
$ java -jar target/euclid-transpiler.jar --strict --inline formula.ed
```

5. Check or canonicalize source without writing output.

```console
$ java -jar target/euclid-transpiler.jar --check input.ed
$ java -jar target/euclid-transpiler.jar --check --json input.ed
$ java -jar target/euclid-transpiler.jar --check --strict-aliases input.ed
$ java -jar target/euclid-transpiler.jar --strict --check formula.ed
$ java -jar target/euclid-transpiler.jar --canonicalize input.ed normalized.ed
$ java -jar target/euclid-transpiler.jar --strict --canonicalize formula.ed normalized.ed
$ java -jar target/euclid-transpiler.jar --manifest --json
```

6. Launch the strict-expression REPL when you want to experiment with raw Euclid expressions.

```console
$ java -jar target/euclid-repl.jar
$ >>> x^2 + 2*x + 1
$ LaTeX: x^{2} + 2x + 1
$ >>> integral(x^2, x)
$ LaTeX: \int x^{2} \, dx
$ >>> :quit
```

## Mode Summary

- Default CLI mode is Markdown-first: only explicit `$...$` and `$$...$$` regions are parsed as Euclid.
- `--strict` treats the whole file as pure Euclid and is the right choice for formulas, CI checks on expression corpora, and REPL-like workflows.
- Library entry points follow the same split: `transpile(...)` is strict, while `transpileDocument(...)` processes Markdown documents conservatively.

## References

* [Symbolica](https://github.com/benruijl/symbolica) by [benruijl](https://github.com/benruijl)
