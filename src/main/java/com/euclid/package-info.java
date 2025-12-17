/**
 * Euclid - A human-readable transpiler for mathematical notation in Markdown.
 * <p>
 * Euclid simplifies writing mathematical expressions in Markdown by providing
 * a Python-like syntax that transpiles to LaTeX/MathJax. Instead of writing
 * verbose LaTeX commands, users can write intuitive expressions like
 * {@code sin(x) + integral(f(x), x, 0, 1)}.
 * </p>
 *
 * <h2>Main Entry Points</h2>
 * <ul>
 *   <li>{@link com.euclid.Transpiler} - Main transpilation API for converting .ed files to Markdown</li>
 *   <li>{@link com.euclid.Repl} - Interactive REPL for testing expressions</li>
 * </ul>
 *
 * <h2>Architecture</h2>
 * <p>
 * The transpilation pipeline consists of four main stages:
 * </p>
 * <ol>
 *   <li><strong>Lexical Analysis</strong> - {@link com.euclid.lexer.Lexer} converts source text into tokens</li>
 *   <li><strong>Parsing</strong> - {@link com.euclid.parser.Parser} builds an Abstract Syntax Tree (AST)</li>
 *   <li><strong>Validation</strong> - {@link com.euclid.util.ValidationHelper} checks for semantic errors</li>
 *   <li><strong>Transpilation</strong> - {@link com.euclid.transpiler.LatexTranspiler} generates LaTeX output</li>
 * </ol>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // Transpile a string
 * String source = "sin(PI / 4) + log(E, 2)";
 * String latex = Transpiler.transpile(source);
 * System.out.println(latex);
 * // Output: \sin(\frac{\pi}{4}) + \log_{2}(e)
 *
 * // Transpile a file
 * Transpiler.transpileFile("input.ed", "output.md");
 * }</pre>
 *
 * @see com.euclid.Transpiler
 * @see com.euclid.Repl
 * @see <a href="https://github.com/gongahkia/euclid">Euclid GitHub Repository</a>
 */
package com.euclid;
