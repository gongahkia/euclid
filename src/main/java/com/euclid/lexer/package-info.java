/**
 * Lexical analysis (tokenization) for Euclid source code.
 * <p>
 * The {@link com.euclid.lexer.Lexer} class converts raw Euclid source text into
 * a stream of tokens that can be consumed by the parser. This is the first stage
 * of the transpilation pipeline.
 * </p>
 *
 * <h2>Lexer Capabilities</h2>
 * <ul>
 *   <li>Recognizes all mathematical constants (PI, E, Greek letters)</li>
 *   <li>Tokenizes function names (sin, cos, log, integral, etc.)</li>
 *   <li>Handles numeric literals (integers, floats, scientific notation)</li>
 *   <li>Supports operators (+, -, *, /, ^, %, \\)</li>
 *   <li>Recognizes delimiters (parentheses, brackets, braces, commas)</li>
 *   <li>Strips inline comments (# and // styles)</li>
 *   <li>Tracks line and column numbers for error reporting</li>
 *   <li>Handles backslash-backslash fraction operator: {@code a \\ b}</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <p>
 * The lexer throws {@link com.euclid.exception.LexerException} when encountering
 * invalid input, providing detailed error messages with source context and position
 * information.
 * </p>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * String source = "sin(PI / 4) + log(E, 2)";
 * Lexer lexer = new Lexer(source);
 * List<Token> tokens = lexer.tokenize();
 *
 * // tokens contains:
 * // SIN, LPAREN, PI, DIVIDE, NUMBER(4), RPAREN, PLUS, LOG, LPAREN, E, COMMA, NUMBER(2), RPAREN, EOF
 * }</pre>
 *
 * @see com.euclid.lexer.Lexer
 * @see com.euclid.token.Token
 * @see com.euclid.exception.LexerException
 */
package com.euclid.lexer;
