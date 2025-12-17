package com.euclid.util;

import com.euclid.token.Token;
import com.euclid.token.TokenType;
import com.euclid.exception.ParserException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for validating function calls and arguments in the Euclid parser.
 */
public class ValidationHelper {

    /**
     * Map of function names to their expected argument counts.
     * -1 means variable arguments, -2 means 1 or 2 arguments, etc.
     */
    private static final Map<TokenType, Integer> FUNCTION_ARG_COUNTS = new HashMap<>();

    static {
        // Unary functions (1 argument)
        FUNCTION_ARG_COUNTS.put(TokenType.SIN, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.COS, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.TAN, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.CSC, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.SEC, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.COT, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.SINH, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.COSH, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.TANH, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.LN, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.EXP, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.ABS, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.FLOOR, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.CEIL, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.SQRT, -2); // 1 or 2 arguments
        
        // Binary functions (2 arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.POW, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.LOG, -2); // 1 or 2 arguments
        FUNCTION_ARG_COUNTS.put(TokenType.MOD, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.GCD, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.LCM, 2);
        
        // Comparison operators (2 arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.LT, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.GT, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.LEQ, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.GEQ, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.APPROX, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.NEQ, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.EQUIV, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.PM, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.TIMES, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.DIV, 2);
        
        // Set operations (2 arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.SUBSET, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.UNION, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.INTERSECTION, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.ELEMENT_OF, 2);
        
        // Logical operators
        FUNCTION_ARG_COUNTS.put(TokenType.AND, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.OR, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.NOT, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.IMPLIES, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.IFF, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.FORALL, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.EXISTS, 1);
        
        // Calculus operations (variable arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.LIMIT, -1);
        FUNCTION_ARG_COUNTS.put(TokenType.DIFF, -1);
        FUNCTION_ARG_COUNTS.put(TokenType.PARTIAL, -1);
        FUNCTION_ARG_COUNTS.put(TokenType.INTEGRAL, -1);
        FUNCTION_ARG_COUNTS.put(TokenType.SUM, -1);
        FUNCTION_ARG_COUNTS.put(TokenType.PROD, -1);
        
        // Matrix/vector operations (variable arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.VECTOR, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.MATRIX, 1);
    }

    /**
     * Validates function argument count.
     * @param function The function token
     * @param argumentCount The number of arguments provided
     * @throws ParserException if the argument count is invalid
     */
    public static void validateArgumentCount(Token function, int argumentCount) throws ParserException {
        Integer expected = FUNCTION_ARG_COUNTS.get(function.getType());
        
        if (expected == null) {
            // Unknown function, skip validation
            return;
        }
        
        if (expected == -1) {
            // Variable arguments, any count is valid
            return;
        }
        
        if (expected == -2) {
            // 1 or 2 arguments
            if (argumentCount != 1 && argumentCount != 2) {
                throw new ParserException(
                    String.format("Function '%s' expects 1 or 2 arguments, but got %d", 
                                  function.getLexeme(), argumentCount),
                    function.getLine(),
                    function.getColumn()
                );
            }
            return;
        }
        
        if (expected != argumentCount) {
            String plural = expected == 1 ? "" : "s";
            throw new ParserException(
                String.format("Function '%s' expects %d argument%s, but got %d", 
                              function.getLexeme(), expected, plural, argumentCount),
                function.getLine(),
                function.getColumn()
            );
        }
    }

    /**
     * Suggests similar function names when an unknown identifier is used.
     * @param name The identifier name
     * @return A list of suggestions
     */
    public static List<String> suggestSimilarFunctions(String name) {
        List<String> suggestions = new ArrayList<>();
        String lowerName = name.toLowerCase();
        
        // Common typos and similar names
        Map<String, String> commonMistakes = new HashMap<>();
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
        
        // Check for simple typos using Levenshtein distance
        String[] knownFunctions = {"sin", "cos", "tan", "log", "ln", "sqrt", "abs", "pow", "exp", 
                                    "PI", "E", "ALPHA", "BETA", "GAMMA", "DELTA", "THETA"};
        
        for (String known : knownFunctions) {
            if (levenshteinDistance(lowerName, known.toLowerCase()) <= 2) {
                suggestions.add(known);
            }
        }
        
        return suggestions;
    }

    /**
     * Calculates Levenshtein distance between two strings.
     */
    private static int levenshteinDistance(String a, String b) {
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
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1),     // insertion
                    dp[i - 1][j - 1] + cost // substitution
                );
            }
        }
        
        return dp[a.length()][b.length()];
    }

    /**
     * Validates that delimiters are balanced in the input.
     * @param tokens The list of tokens to validate
     * @throws ParserException if delimiters are unbalanced
     */
    public static void validateBalancedDelimiters(List<Token> tokens) throws ParserException {
        int parenCount = 0;
        int bracketCount = 0;
        int braceCount = 0;
        
        Token lastOpenParen = null;
        Token lastOpenBracket = null;
        Token lastOpenBrace = null;
        
        for (Token token : tokens) {
            switch (token.getType()) {
                case LPAREN:
                    parenCount++;
                    if (lastOpenParen == null) lastOpenParen = token;
                    break;
                case RPAREN:
                    parenCount--;
                    if (parenCount < 0) {
                        throw new ParserException(
                            "Unmatched closing parenthesis ')'",
                            token.getLine(),
                            token.getColumn()
                        );
                    }
                    break;
                case LBRACKET:
                    bracketCount++;
                    if (lastOpenBracket == null) lastOpenBracket = token;
                    break;
                case RBRACKET:
                    bracketCount--;
                    if (bracketCount < 0) {
                        throw new ParserException(
                            "Unmatched closing bracket ']'",
                            token.getLine(),
                            token.getColumn()
                        );
                    }
                    break;
                case LBRACE:
                    braceCount++;
                    if (lastOpenBrace == null) lastOpenBrace = token;
                    break;
                case RBRACE:
                    braceCount--;
                    if (braceCount < 0) {
                        throw new ParserException(
                            "Unmatched closing brace '}'",
                            token.getLine(),
                            token.getColumn()
                        );
                    }
                    break;
                default:
                    break;
            }
        }
        
        if (parenCount > 0) {
            throw new ParserException(
                "Unclosed parenthesis '(' - missing closing ')'",
                lastOpenParen.getLine(),
                lastOpenParen.getColumn()
            );
        }
        
        if (bracketCount > 0) {
            throw new ParserException(
                "Unclosed bracket '[' - missing closing ']'",
                lastOpenBracket.getLine(),
                lastOpenBracket.getColumn()
            );
        }
        
        if (braceCount > 0) {
            throw new ParserException(
                "Unclosed brace '{' - missing closing '}'",
                lastOpenBrace.getLine(),
                lastOpenBrace.getColumn()
            );
        }
    }
}
