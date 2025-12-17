/**
 * Token types and token representations for the Euclid lexer.
 * <p>
 * This package contains the token system used by the lexer to represent
 * the lexical units of Euclid source code. Each token has a type
 * ({@link com.euclid.token.TokenType}), lexeme (original text), and position information.
 * </p>
 *
 * <h2>Token Types</h2>
 * <p>
 * The {@link com.euclid.token.TokenType} enum defines all possible token types:
 * </p>
 * <ul>
 *   <li><strong>Literals</strong> - Numbers, identifiers, strings</li>
 *   <li><strong>Operators</strong> - Arithmetic (+, -, *, /), power (^), modulo (%)</li>
 *   <li><strong>Delimiters</strong> - Parentheses, brackets, braces, commas</li>
 *   <li><strong>Constants</strong> - PI, E, I, Greek letters (ALPHA through OMEGA)</li>
 *   <li><strong>Functions</strong> - Mathematical functions (sin, cos, log, integral, etc.)</li>
 *   <li><strong>Comparison</strong> - lt, gt, leq, geq, approx, neq, equiv</li>
 *   <li><strong>Logic</strong> - AND, OR, NOT, implies, iff, forall, exists</li>
 *   <li><strong>Set Operations</strong> - union, intersection, subset, element_of</li>
 * </ul>
 *
 * <h2>Token Class</h2>
 * <p>
 * The {@link com.euclid.token.Token} class represents a single token with:
 * </p>
 * <ul>
 *   <li>Type ({@link com.euclid.token.TokenType})</li>
 *   <li>Lexeme (original text from source)</li>
 *   <li>Line and column numbers for error reporting</li>
 *   <li>Optional literal value (for numbers and strings)</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * // Create a token for the number 42 at line 1, column 5
 * Token token = new Token(TokenType.NUMBER, "42", 1, 5, 42.0);
 *
 * // Create a token for the PI constant
 * Token piToken = new Token(TokenType.PI, "PI", 1, 1);
 *
 * // Create a token for a function name
 * Token sinToken = new Token(TokenType.SIN, "sin", 1, 10);
 * }</pre>
 *
 * @see com.euclid.token.Token
 * @see com.euclid.token.TokenType
 * @see com.euclid.lexer.Lexer
 */
package com.euclid.token;
