package com.euclid.processor;

import com.euclid.Transpiler;
import com.euclid.exception.EuclidException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processes mixed markdown and Euclid math content.
 * Detects inline math expressions and transpiles them while preserving surrounding text.
 */
public class MixedContentProcessor {

    // Pattern to match potential math expressions
    // Matches: constants, functions, operators, and complete expressions
    private static final Pattern MATH_PATTERN = Pattern.compile(
        // Match complete mathematical expressions including:
        // - Function calls: pow(2, 3), sin(x), integral(f, x, a, b)
        "\\b(pow|abs|ceil|floor|mod|gcd|lcm|sin|cos|tan|csc|sec|cot|sinh|cosh|tanh|" +
        "log|ln|exp|sqrt|limit|diff|partial|integral|sum|prod|vector|matrix|" +
        "lt|gt|leq|geq|approx|neq|equiv|pm|times|div|cdot|ast|star|circ|bullet|" +
        "cap|cup|subset|supset|subseteq|supseteq|union|intersection|set_diff|" +
        "element_of|not_element_of|AND|OR|NOT|implies|iff|forall|exists)\\s*\\([^)]*\\)" +
        "|" +
        // - Constants: PI, E, I, GAMMA, PHI, INFINITY, Greek letters
        "\\b(PI|E|I|GAMMA|PHI|INFINITY|ALPHA|BETA|DELTA|EPSILON|ZETA|ETA|THETA|" +
        "KAPPA|LAMBDA|MU|NU|XI|OMICRON|RHO|SIGMA|TAU|UPSILON|CHI|PSI|OMEGA|emptyset)\\b" +
        "|" +
        // - Arithmetic expressions: 2 + 3, 4 * 5, etc.
        "\\d+\\s*[+\\-*/^]\\s*\\d+" +
        "|" +
        // - Fraction operator: a \\\\ b
        "\\w+\\s*\\\\\\\\\\s*\\w+" +
        "|" +
        // - Power operator: 2 ^ 3
        "\\w+\\s*\\^\\s*\\w+"
    );

    /**
     * Processes a line of mixed content, detecting and transpiling math expressions.
     *
     * @param line The input line with mixed content
     * @return The processed line with transpiled math wrapped in $
     * @throws EuclidException if transpilation fails
     */
    public static String processLine(String line) throws EuclidException {
        // Skip pure markdown elements
        if (line.trim().startsWith("#") || line.trim().isEmpty()) {
            return line;
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = MATH_PATTERN.matcher(line);
        int lastEnd = 0;

        while (matcher.find()) {
            // Append text before the math expression
            result.append(line, lastEnd, matcher.start());

            // Extract and transpile the math expression
            String mathExpr = matcher.group();
            try {
                String latex = Transpiler.transpile(mathExpr);
                result.append("$").append(latex).append("$");
            } catch (EuclidException e) {
                // If transpilation fails, keep the original text
                result.append(mathExpr);
            }

            lastEnd = matcher.end();
        }

        // Append remaining text
        result.append(line.substring(lastEnd));

        return result.toString();
    }

    /**
     * Processes an entire document with mixed content.
     *
     * @param content The input document
     * @return The processed document
     * @throws EuclidException if transpilation fails
     */
    public static String processDocument(String content) throws EuclidException {
        String[] lines = content.split("\n");
        List<String> processedLines = new ArrayList<>();

        for (String line : lines) {
            processedLines.add(processLine(line));
        }

        return String.join("\n", processedLines);
    }
}
