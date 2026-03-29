package com.euclid.util;

import com.euclid.exception.ParserException;
import com.euclid.lang.EuclidArity;
import com.euclid.lang.EuclidLanguage;
import com.euclid.lang.EuclidSignature;
import com.euclid.token.Token;
import com.euclid.token.TokenType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Utility class for validating function calls and delimiters in the Euclid parser.
 */
public final class ValidationHelper {

    private ValidationHelper() {
    }

    /**
     * Validates function argument count.
     *
     * @param function The function token
     * @param argumentCount The number of arguments provided
     * @throws ParserException if the argument count is invalid
     */
    public static void validateArgumentCount(Token function, int argumentCount) throws ParserException {
        EuclidArity arity = EuclidLanguage.getArity(function.getLexeme());
        if (arity == null || arity.accepts(argumentCount)) {
            return;
        }

        EuclidSignature signature = EuclidLanguage.getSignature(function.getLexeme());
        String suggestion = signature != null
                ? "Use " + signature.label()
                : "Check the canonical function signature before retrying";
        String message = String.format("Function '%s' expects %s argument%s, but got %d",
                function.getLexeme(),
                arity.label(),
                needsPluralSuffix(arity.label()) ? "s" : "",
                argumentCount);
        throw new ParserException("parser.invalid-arity", message, function.getLine(), function.getColumn(), suggestion);
    }

    private static boolean needsPluralSuffix(String expected) {
        return !(expected.equals("1") || expected.equals("at least 1"));
    }

    /**
     * Suggests similar function names when an unknown identifier is used.
     *
     * @param name The identifier name
     * @return A list of suggestions
     */
    public static List<String> suggestSimilarFunctions(String name) {
        LinkedHashSet<String> suggestions = new LinkedHashSet<>();
        String lowerName = name.toLowerCase();

        java.util.Map<String, String> commonMistakes = new java.util.HashMap<>();
        commonMistakes.put("sine", "sin");
        commonMistakes.put("cosine", "cos");
        commonMistakes.put("tangent", "tan");
        commonMistakes.put("logarithm", "log");
        commonMistakes.put("square", "sqrt");
        commonMistakes.put("squareroot", "sqrt");
        commonMistakes.put("absolute", "abs");
        commonMistakes.put("power", "pow");
        commonMistakes.put("exponential", "exp");
        commonMistakes.put("pi", "PI");
        commonMistakes.put("euler", "E");
        commonMistakes.put("infinity", "INFINITY");

        if (commonMistakes.containsKey(lowerName)) {
            suggestions.add(commonMistakes.get(lowerName));
        }

        for (String known : EuclidLanguage.getKeywordNames()) {
            if (levenshteinDistance(lowerName, known.toLowerCase()) <= 2) {
                suggestions.add(EuclidLanguage.getCanonicalName(known));
            }
        }

        return new ArrayList<>(suggestions);
    }

    private static int levenshteinDistance(String a, String b) {
        if (a.length() > 100 || b.length() > 100 || Math.abs(a.length() - b.length()) > 3) {
            return Integer.MAX_VALUE;
        }
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }

    /**
     * Validates that delimiters are balanced in the input.
     *
     * @param tokens The list of tokens to validate
     * @throws ParserException if delimiters are unbalanced
     */
    public static void validateBalancedDelimiters(List<Token> tokens) throws ParserException {
        ArrayDeque<Token> stack = new ArrayDeque<>();
        for (Token token : tokens) {
            switch (token.getType()) {
                case LPAREN, LBRACKET, LBRACE -> stack.push(token);
                case RPAREN -> closeDelimiter(stack, token, TokenType.LPAREN, ")");
                case RBRACKET -> closeDelimiter(stack, token, TokenType.LBRACKET, "]");
                case RBRACE -> closeDelimiter(stack, token, TokenType.LBRACE, "}");
                default -> {
                }
            }
        }
        if (!stack.isEmpty()) {
            Token unclosed = stack.peekLast();
            throw new ParserException(
                    "Unclosed '" + unclosed.getLexeme() + "' - missing '" + closingFor(unclosed) + "'",
                    unclosed.getLine(),
                    unclosed.getColumn());
        }
    }

    private static void closeDelimiter(
            ArrayDeque<Token> stack,
            Token token,
            TokenType expectedOpen,
            String closing) throws ParserException {
        if (stack.isEmpty() || stack.peek().getType() != expectedOpen) {
            String message = stack.isEmpty()
                    ? "Unmatched closing " + delimiterName(closing) + " '" + closing + "'"
                    : "Mismatched delimiter: expected '" + closingFor(stack.peek()) + "' but found '" + closing + "'";
            throw new ParserException(message, token.getLine(), token.getColumn());
        }
        stack.pop();
    }

    private static String closingFor(Token open) {
        return switch (open.getType()) {
            case LPAREN -> ")";
            case LBRACKET -> "]";
            case LBRACE -> "}";
            default -> "?";
        };
    }

    private static String delimiterName(String closing) {
        return switch (closing) {
            case ")" -> "parenthesis";
            case "]" -> "bracket";
            case "}" -> "brace";
            default -> "delimiter";
        };
    }
}
