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

## Quick Start

Here's a simple example showing Euclid vs raw LaTeX:

**Euclid Syntax (`.ed`):**
```
The quadratic formula: x = (-b +- sqrt(b^2 - 4*a*c)) / (2*a)
```

**Transpiled LaTeX Output:**
```
The quadratic formula: $x = \frac{-b \pm \sqrt{b^{2} - 4ac}}{2a}$
```

**Rendered Result:**  
The quadratic formula: $x = \frac{-b \pm \sqrt{b^{2} - 4ac}}{2a}$

### Why Euclid?

| Feature | Raw LaTeX | Euclid |
|---------|-----------|--------|
| Fractions | `\frac{a}{b}` | `a/b` |
| Square root | `\sqrt{x}` | `sqrt(x)` |
| Exponents | `x^{2}` | `x^2` |
| Greek letters | `\alpha, \beta` | `alpha, beta` |
| Summation | `\sum_{i=1}^{n}` | `sum(i=1, n)` |

**Result:** Euclid reduces cognitive overhead and makes math expressions readable like code!

## Syntax Guide

* [Language specification](doc/syntax.md)
* [Examples](example/)

## Prerequisites

Before installing Euclid, ensure you have:

* **Java 21 or higher** - [Download](https://adoptium.net/)
* **Maven 3.8+** - [Installation guide](https://maven.apache.org/install.html)
* **Git** - [Download](https://git-scm.com/downloads)

Verify installation:
```console
$ java --version
$ mvn --version
$ git --version
```

## Installation

### Quick Install (Recommended)

```console
$ git clone https://github.com/gongahkia/euclid
$ cd euclid
$ mvn clean package
```

This creates a standalone JAR at `target/euclid-2.0-SNAPSHOT.jar`.

### Build from Source

```console
$ git clone https://github.com/gongahkia/euclid
$ cd euclid
$ mvn clean compile
```

## Usage 

### Transpiling Files

Convert `.ed` files to Markdown with LaTeX:

```console
$ java -jar target/euclid-2.0-SNAPSHOT.jar input.ed output.md
```

Or if building from source:

```console
$ mvn exec:java -Dexec.mainClass="com.euclid.Repl" -Dexec.args="input.ed output.md"
```

### Interactive REPL

Launch the REPL for testing expressions:

```console
$ java -jar target/euclid-2.0-SNAPSHOT.jar
```

Then type expressions and see LaTeX output:

```
euclid> x^2 + 2*x + 1
LaTeX: x^{2} + 2x + 1

euclid> integral(x^2, x)
LaTeX: \int x^{2} \, dx

euclid> exit
```

## Troubleshooting

### Common Issues

**Problem:** `java: command not found`  
**Solution:** Java is not installed or not in PATH. Install Java 21+ and add to PATH.

**Problem:** `mvn: command not found`  
**Solution:** Maven is not installed. Follow the [Maven installation guide](https://maven.apache.org/install.html).

**Problem:** Build fails with "unsupported class file version"  
**Solution:** You're using Java < 21. Update to Java 21 or higher.

**Problem:** `NoClassDefFoundError` when running JAR  
**Solution:** Use the shaded JAR: `target/euclid-2.0-SNAPSHOT.jar` which includes dependencies.

**Problem:** Parser errors with valid expressions  
**Solution:** Check [syntax documentation](doc/syntax.md) for operator precedence and function signatures.

**Problem:** Output file is empty  
**Solution:** Ensure input file has valid Euclid syntax. Check console for error messages.

### Getting Help

* Report bugs: [GitHub Issues](https://github.com/gongahkia/euclid/issues)
* Check [examples](example/) for syntax reference
* Read [syntax specification](doc/syntax.md) for detailed rules

## References

* [Symbolica](https://github.com/benruijl/symbolica) by [benruijl](https://github.com/benruijl)
