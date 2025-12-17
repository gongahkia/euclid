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
 * ValidationHelper.validateFunctionArguments(functionToken, arguments);
 *
 * // Get suggestions for typos
 * String typo = "cosine";
 * String suggestion = ValidationHelper.getSuggestion(typo);
 * // Returns: "cos" (using Levenshtein distance to find closest match)
 *
 * // Check balanced delimiters
 * String source = "sin(PI / 4)";
 * ValidationHelper.checkBalancedDelimiters(source);
 * // No exception thrown - delimiters are balanced
 * }</pre>
 *
 * @see com.euclid.util.ValidationHelper
 */
package com.euclid.util;
