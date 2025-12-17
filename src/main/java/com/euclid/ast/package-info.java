/**
 * Abstract Syntax Tree (AST) representations for Euclid expressions.
 * <p>
 * This package contains the AST node types used to represent parsed Euclid expressions.
 * All AST nodes implement the {@link com.euclid.ast.AstNode} interface and support
 * the Visitor pattern for traversal and transformation.
 * </p>
 *
 * <h2>AST Node Types</h2>
 * <ul>
 *   <li>{@link com.euclid.ast.LiteralExpr} - Numeric literals and constants (e.g., {@code 42}, {@code PI})</li>
 *   <li>{@link com.euclid.ast.IdentifierExpr} - Variable names (e.g., {@code x}, {@code myVar})</li>
 *   <li>{@link com.euclid.ast.BinaryExpr} - Binary operations (e.g., {@code a + b}, {@code x * y})</li>
 *   <li>{@link com.euclid.ast.UnaryExpr} - Unary operations (e.g., {@code -x})</li>
 *   <li>{@link com.euclid.ast.CallExpr} - Function calls (e.g., {@code sin(x)}, {@code pow(2, 3)})</li>
 *   <li>{@link com.euclid.ast.GroupingExpr} - Parenthesized expressions (e.g., {@code (a + b)})</li>
 *   <li>{@link com.euclid.ast.TextExpr} - Plain text nodes that don't require transpilation</li>
 *   <li>{@link com.euclid.ast.DocumentNode} - Root node containing a sequence of expressions</li>
 * </ul>
 *
 * <h2>Visitor Pattern</h2>
 * <p>
 * AST nodes can be traversed using the {@link com.euclid.ast.AstVisitor} interface.
 * This enables separation of tree traversal logic from node definitions, making it
 * easy to implement different operations (transpilation, optimization, analysis) without
 * modifying the AST node classes.
 * </p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * // Create an AST for "sin(PI)"
 * CallExpr expr = new CallExpr(
 *     new Token(TokenType.SIN, "sin", 1, 1),
 *     Arrays.asList(
 *         new LiteralExpr(TokenType.PI)
 *     )
 * );
 *
 * // Transpile using a visitor
 * LatexTranspiler transpiler = new LatexTranspiler();
 * String latex = expr.accept(transpiler);  // Returns: "\sin(\pi)"
 * }</pre>
 *
 * @see com.euclid.ast.AstNode
 * @see com.euclid.ast.AstVisitor
 * @see com.euclid.transpiler.LatexTranspiler
 */
package com.euclid.ast;
