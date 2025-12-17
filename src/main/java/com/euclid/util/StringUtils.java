package com.euclid.util;

import java.util.Objects;

/**
 * String utility functions for text processing and formatting.
 */
public final class StringUtils {

    private StringUtils() {
        // Prevent instantiation
    }

    /**
     * Removes surrounding quotes from a string if present.
     *
     * @param str The input string
     * @return The string without quotes
     */
    public static String removeQuotes(String str) {
        Objects.requireNonNull(str, "String cannot be null");

        if (str.length() >= 2 && str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * Escapes special LaTeX characters in a string.
     *
     * @param text The input text
     * @return The escaped text
     */
    public static String escapeLaTeX(String text) {
        Objects.requireNonNull(text, "Text cannot be null");

        return text
            .replace("\\", "\\textbackslash{}")
            .replace("&", "\\&")
            .replace("%", "\\%")
            .replace("$", "\\$")
            .replace("#", "\\#")
            .replace("_", "\\_")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("~", "\\textasciitilde{}")
            .replace("^", "\\textasciicircum{}");
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param str The string to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Checks if a string is null, empty, or contains only whitespace.
     *
     * @param str The string to check
     * @return true if null, empty, or whitespace
     */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Truncates a string to a maximum length, adding ellipsis if needed.
     *
     * @param str The input string
     * @param maxLength The maximum length
     * @return The truncated string
     */
    public static String truncate(String str, int maxLength) {
        Objects.requireNonNull(str, "String cannot be null");

        if (maxLength < 0) {
            throw new IllegalArgumentException("Max length cannot be negative");
        }

        if (str.length() <= maxLength) {
            return str;
        }

        return str.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    /**
     * Repeats a string n times.
     *
     * @param str The string to repeat
     * @param times The number of times to repeat
     * @return The repeated string
     */
    public static String repeat(String str, int times) {
        Objects.requireNonNull(str, "String cannot be null");

        if (times < 0) {
            throw new IllegalArgumentException("Times cannot be negative");
        }

        return str.repeat(times);
    }
}
