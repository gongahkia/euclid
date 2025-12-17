/**
 * Transpilation from Euclid AST to LaTeX/MathJax output.
 * <p>
 * The {@link com.euclid.transpiler.LatexTranspiler} class converts an Abstract Syntax Tree
 * into LaTeX mathematical notation that can be embedded in Markdown documents and rendered
 * by MathJax or similar renderers.
 * </p>
 *
 * <h2>Transpiler Features</h2>
 * <ul>
 *   <li>Converts all mathematical constants to LaTeX symbols</li>
 *   <li>Transpiles basic arithmetic operations (+, -, *, /, ^)</li>
 *   <li>Handles trigonometric functions (sin, cos, tan, csc, sec, cot)</li>
 *   <li>Supports hyperbolic functions (sinh, cosh, tanh)</li>
 *   <li>Converts logarithmic and exponential functions (log, ln, exp)</li>
 *   <li>Generates fractions, roots, and powers</li>
 *   <li>Supports calculus operations (limit, derivative, integral)</li>
 *   <li>Handles summation and product notation</li>
 *   <li>Transpiles matrices and vectors</li>
 *   <li>Converts set notation and logical operators</li>
 *   <li>Preserves plain text nodes without modification</li>
 * </ul>
 *
 * <h2>Implementation Pattern</h2>
 * <p>
 * The transpiler implements the {@link com.euclid.ast.AstVisitor} interface,
 * using the Visitor pattern to traverse the AST and generate LaTeX output.
 * Each AST node type has a corresponding visit method that returns the
 * LaTeX string representation.
 * </p>
 *
 * <h2>Example Transpilations</h2>
 * <pre>
 * Euclid Input           LaTeX Output
 * ─────────────────      ─────────────────
 * PI                  →  \pi
 * sin(x)              →  \sin(x)
 * sqrt(2)             →  \sqrt{2}
 * log(x, 10)          →  \log_{10}(x)
 * integral(f(x), x)   →  \int f(x) \, dx
 * a \\ b              →  \frac{a}{b}
 * limit(f(x), x, 0)   →  \lim_{x \to 0} f(x)
 * ALPHA + BETA        →  \alpha + \beta
 * </pre>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // Parse the expression
 * String source = "sin(PI / 4)";
 * Lexer lexer = new Lexer(source);
 * Parser parser = new Parser(lexer.tokenize(), source);
 * DocumentNode ast = parser.parse();
 *
 * // Transpile to LaTeX
 * LatexTranspiler transpiler = new LatexTranspiler();
 * String latex = ast.accept(transpiler);
 * // Result: "\sin(\frac{\pi}{4})"
 * }</pre>
 *
 * @see com.euclid.transpiler.LatexTranspiler
 * @see com.euclid.ast.AstVisitor
 * @see com.euclid.ast.AstNode
 */
package com.euclid.transpiler;
