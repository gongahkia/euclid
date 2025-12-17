package com.euclid.transpiler;

/**
 * Represents the LaTeX math mode for transpiled output.
 */
public enum MathMode {
    /**
     * Inline math mode: wraps output in $...$
     */
    INLINE,

    /**
     * Display math mode: wraps output in $$...$$
     */
    DISPLAY,

    /**
     * No wrapping: returns raw LaTeX code
     */
    NONE
}
