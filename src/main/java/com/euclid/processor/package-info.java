/**
 * Document processing utilities for handling mixed markdown and Euclid content.
 *
 * <p>This package provides tools for processing documents that contain both
 * regular markdown text and Euclid mathematical expressions. The main component
 * is the MixedContentProcessor which conservatively detects obvious inline math
 * expressions while preserving surrounding prose and protected Markdown spans.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * String input = "Series: sum(i, i, 1, n)";
 * String output = MixedContentProcessor.processLine(input);
 * // Result: "Series: $\sum_{i=1}^{n} i$"
 * }</pre>
 *
 * @since 2.0
 */
package com.euclid.processor;
