/**
 * Utility classes for validation and helper functions.
 * <p>
 * This package contains utility classes that support the parser and other
 * components with validation logic, string distance calculations, and
 * other helper functionality.
 * </p>
 *
 * <h2>Validation Helper</h2>
 * <p>
 * The {@link com.euclid.util.ValidationHelper} class provides:
 * </p>
 * <ul>
 *   <li>Function argument count validation</li>
 *   <li>"Did you mean?" suggestions using Levenshtein distance</li>
 *   <li>Balanced delimiter checking</li>
 *   <li>Common error detection and correction suggestions</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // Validate function argument count
 * Token functionToken = new Token(TokenType.SIN, "sin", 1, 1);
 * List<AstNode> arguments = Arrays.asList(arg1);
 * ValidationHelper.validateArgumentCount(functionToken, arguments.size());
 *
 * // Get suggestions for typos
 * String typo = "cosine";
 * List<String> suggestions = ValidationHelper.suggestSimilarFunctions(typo);
 * // Returns a list containing "cos" as a close canonical match
 *
 * // Check balanced delimiters
 * List<Token> tokens = List.of(
 *     new Token(TokenType.LPAREN, "(", 1, 1),
 *     new Token(TokenType.RPAREN, ")", 1, 2)
 * );
 * ValidationHelper.validateBalancedDelimiters(tokens);
 * // No exception thrown - delimiters are balanced
 * }</pre>
 *
 * @see com.euclid.util.ValidationHelper
 */
package com.euclid.util;
