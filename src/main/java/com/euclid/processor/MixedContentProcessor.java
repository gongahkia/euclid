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

    // Pattern to match potential math expressions with word boundaries to prevent
    // false matches (e.g., "sin" inside "singing")
    private static final Pattern MATH_PATTERN = Pattern.compile(
        // Function calls: require word boundary before AND '(' after to avoid partial word matches
        "(?<![a-zA-Z])(pow|abs|ceil|floor|mod|gcd|lcm|sin|cos|tan|csc|sec|cot|sinh|cosh|tanh|csch|sech|coth|" +
        "log|ln|exp|sqrt|limit|diff|partial|integral|sum|prod|vector|matrix|" +
        "lt|gt|leq|geq|approx|neq|equiv|pm|times|div|cdot|ast|star|circ|bullet|" +
        "cap|cup|subset|supset|subseteq|supseteq|union|intersection|set_diff|" +
        "element_of|not_element_of|AND|OR|NOT|implies|iff|forall|exists|given)(?![a-zA-Z])\\s*\\([^)]*\\)" +
        "|" +
        // Constants: strict word boundaries
        "(?<![a-zA-Z])(PI|GAMMA|PHI|INFINITY|INF|ALPHA|BETA|DELTA|EPSILON|ZETA|ETA|THETA|" +
        "KAPPA|LAMBDA|MU|NU|XI|OMICRON|RHO|SIGMA|TAU|UPSILON|CHI|PSI|OMEGA|emptyset)(?![a-zA-Z])" +
        "|" +
        // Arithmetic: require at least one operator between numbers
        "\\d+\\s*[+\\-*/^]\\s*\\d+" +
        "|" +
        // Fraction operator: a \\\\ b
        "\\w+\\s*\\\\\\\\\\s*\\w+" +
        "|" +
        // Power operator: 2 ^ 3
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
        // Skip pure markdown elements (headers, blank lines)
        if (line.matches("^\\s*#+\\s.*") || line.trim().isEmpty()) {
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
        // pre-pass: join lines with unclosed delimiters so multiline expressions stay together
        String[] rawLines = content.split("\n");
        List<String> joined = new ArrayList<>();
        StringBuilder pending = new StringBuilder();
        int depth = 0;
        for (String line : rawLines) {
            if (pending.length() > 0) pending.append(" ");
            pending.append(line);
            for (char c : line.toCharArray()) {
                if (c == '(' || c == '[') depth++;
                else if (c == ')' || c == ']') depth--;
            }
            if (depth <= 0) {
                joined.add(pending.toString());
                pending.setLength(0);
                depth = 0;
            }
        }
        if (pending.length() > 0) joined.add(pending.toString());

        List<String> processedLines = new ArrayList<>();
        for (String line : joined) {
            processedLines.add(processLine(line));
        }
        return String.join("\n", processedLines);
    }
}
