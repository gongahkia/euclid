/**
 * Document processing utilities for handling mixed markdown and Euclid content.
 *
 * <p>This package provides tools for processing documents that contain both
 * regular markdown text and Euclid mathematical expressions. The main component
 * is the MixedContentProcessor which can automatically detect and transpile
 * inline math expressions while preserving surrounding prose.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * String input = "The value of PI is approximately 3.14";
 * String output = MixedContentProcessor.processLine(input);
 * // Result: "The value of $\pi$ is approximately 3.14"
 * }</pre>
 *
 * @since 2.0
 */
package com.euclid.processor;
