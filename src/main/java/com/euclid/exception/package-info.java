/**
 * Exception types for Euclid error handling.
 * <p>
 * This package contains specialized exception classes for different stages
 * of the transpilation pipeline. All exceptions extend {@link com.euclid.exception.EuclidException}
 * and provide detailed error information including source context and position.
 * </p>
 *
 * <h2>Exception Hierarchy</h2>
 * <ul>
 *   <li>{@link com.euclid.exception.EuclidException} - Base exception for all Euclid errors</li>
 *   <li>{@link com.euclid.exception.LexerException} - Errors during tokenization (invalid characters, malformed numbers)</li>
 *   <li>{@link com.euclid.exception.ParserException} - Syntax errors during parsing (unexpected tokens, unbalanced delimiters)</li>
 * </ul>
 *
 * <h2>Error Formatting</h2>
 * <p>
 * Both {@link com.euclid.exception.LexerException} and {@link com.euclid.exception.ParserException}
 * provide enhanced error messages that include:
 * </p>
 * <ul>
 *   <li>A clear description of the error</li>
 *   <li>Line and column numbers</li>
 *   <li>Source code context with the problematic line</li>
 *   <li>A visual indicator (caret ^) pointing to the error location</li>
 *   <li>Helpful suggestions for fixing common mistakes (parser only)</li>
 *   <li>Box-drawing characters for professional error display</li>
 * </ul>
 *
 * <h2>Example Error Output</h2>
 * <pre>
 * ┌─────────────────────────────────────────────────
 * │ Parser error at line 1, column 10:
 * │ Unexpected token: Expected ')' but found 'EOF'
 * │
 * │   1 | sin(PI / 4
 * │                   ^
 * │
 * │ 💡 Suggestion: Check for unbalanced parentheses
 * └─────────────────────────────────────────────────
 * </pre>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * try {
 *     Lexer lexer = new Lexer("sin(invalid@char)");
 *     List<Token> tokens = lexer.tokenize();
 * } catch (LexerException e) {
 *     System.err.println(e.getMessage());
 *     // Prints error with source context and position
 * }
 *
 * try {
 *     Parser parser = new Parser(tokens, source);
 *     DocumentNode ast = parser.parse();
 * } catch (ParserException e) {
 *     System.err.println(e.getMessage());
 *     // Prints error with suggestion if available
 * }
 * }</pre>
 *
 * @see com.euclid.exception.EuclidException
 * @see com.euclid.exception.LexerException
 * @see com.euclid.exception.ParserException
 */
package com.euclid.exception;
