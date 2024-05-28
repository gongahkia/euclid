![](https://img.shields.io/badge/euclid_1.0-WIP-orange)

# `Euclid`

Intuitive extended syntax for beautiful $\LaTeX$-style mathematical expressions in Markdown.

![](https://science4fun.info/wp-content/uploads/2021/06/Euclid.jpg)

## Motivation

Although [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) introduced [support](https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/writing-mathematical-expressions) for basic mathematical expressions in May 2022, the [MathJax](https://www.mathjax.org/) rendering library retains a confusing syntax. When combined with $\LaTeX$'s already bloated grammer, this obfuscates much of the logic of the expressions in a sea of dollarsigns, backslashes and curly braces.  

Ultimately, this reinforces the unnecessarily high barriers to entry for beginners seeking to integrate mathematical expressions into their markdown documentation.  

`Euclid` eliminates the friction associated with writing mathematical expressions in [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) by providing an intermediate syntax (`.e` script) identical to [Markdown](https://www.markdownguide.org/) with a straightforward ruleset for parsing mathematical expressions that mirrors the syntax of widespread entry-level programming languages like [Python](https://www.python.org/) and [Javascript](https://devdocs.io/javascript/).  

`.e` script then transpiles to [GFM](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) and adheres to the [demands](https://en.wikibooks.org/wiki/LaTeX/Mathematics) of $\LaTeX$ and [MathJax](https://docs.mathjax.org/en/latest/). 

## Purpose

> [!WARNING]
> OI continue add here as per [here](https://github.com/gongahkia/judgeman)

## Installation

```console
$ git clone https://github.com/gongahkia/euclid
$ cd euclid
$ make config
```

## Usage 

```console
$ make 
```

## Syntax guide

> [!WARNING]
> OI continue adding here
