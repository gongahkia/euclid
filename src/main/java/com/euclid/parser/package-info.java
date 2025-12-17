/**
 * Parsing and syntax analysis for Euclid expressions.
 * <p>
 * The {@link com.euclid.parser.Parser} class converts a stream of tokens into an
 * Abstract Syntax Tree (AST) using recursive descent parsing. This is the second
 * stage of the transpilation pipeline.
 * </p>
 *
 * <h2>Parser Features</h2>
 * <ul>
 *   <li>Recursive descent parsing with operator precedence</li>
 *   <li>Supports nested expressions and function calls</li>
 *   <li>Handles grouping with parentheses, brackets, and braces</li>
 *   <li>Validates function argument counts</li>
 *   <li>Provides "did you mean?" suggestions for common errors</li>
 *   <li>Checks for balanced delimiters</li>
 *   <li>Detailed error messages with source context</li>
 * </ul>
 *
 * <h2>Grammar Overview</h2>
 * <p>
 * The parser implements the following grammar (simplified):
 * </p>
 * <pre>
 * document    → expression*
 * expression  → term ((PLUS | MINUS) term)*
 * term        → factor ((MULTIPLY | DIVIDE | MODULO) factor)*
 * factor      → unary (POWER unary)*
 * unary       → (MINUS | PLUS) unary | call
 * call        → primary (LPAREN arguments? RPAREN)?
 * primary     → NUMBER | IDENTIFIER | constant | grouping
 * grouping    → LPAREN expression RPAREN
 *             | LBRACKET expression RBRACKET
 *             | LBRACE expression RBRACE
 * arguments   → expression (COMMA expression)*
 * constant    → PI | E | I | GAMMA | PHI | INFINITY | greek_letter
 * </pre>
 *
 * <h2>Error Handling</h2>
 * <p>
 * The parser throws {@link com.euclid.exception.ParserException} when encountering
 * syntax errors, providing:
 * </p>
 * <ul>
 *   <li>Detailed error messages describing what went wrong</li>
 *   <li>Source context showing the problematic code</li>
 *   <li>Line and column numbers</li>
 *   <li>Helpful suggestions for fixing common mistakes</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * String source = "sin(PI / 4) + log(E, 2)";
 * Lexer lexer = new Lexer(source);
 * List<Token> tokens = lexer.tokenize();
 *
 * Parser parser = new Parser(tokens, source);
 * DocumentNode ast = parser.parse();
 *
 * // ast contains a tree of BinaryExpr, CallExpr, and LiteralExpr nodes
 * }</pre>
 *
 * @see com.euclid.parser.Parser
 * @see com.euclid.ast.AstNode
 * @see com.euclid.exception.ParserException
 * @see com.euclid.util.ValidationHelper
 */
package com.euclid.parser;
