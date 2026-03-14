package com.euclid.util;

import com.euclid.token.Token;
import com.euclid.token.TokenType;
import com.euclid.exception.ParserException;
import com.euclid.lang.EuclidLanguage;
import com.euclid.lang.EuclidSignature;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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
        FUNCTION_ARG_COUNTS.put(TokenType.CSCH, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.SECH, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.COTH, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.LN, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.EXP, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.ABS, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.FLOOR, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.CEIL, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.SQRT, -2); // 1 or 2 arguments
        
        // Binary functions (2 arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.POW, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.LOG, 2);
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
        FUNCTION_ARG_COUNTS.put(TokenType.CDOT, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.AST, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.STAR, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.CIRC, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.BULLET, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.CAP, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.CUP, 2);
        
        // Set operations (2 arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.SUBSET, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.SUPSET, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.SUBSETEQ, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.SUPSETEQ, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.UNION, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.INTERSECTION, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.SET_DIFF, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.ELEMENT_OF, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.NOT_ELEMENT_OF, 2);
        
        // Logical operators
        FUNCTION_ARG_COUNTS.put(TokenType.AND, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.OR, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.NOT, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.IMPLIES, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.IFF, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.FORALL, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.EXISTS, 2);
        
        // Calculus operations (variable arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.LIMIT, 3);
        FUNCTION_ARG_COUNTS.put(TokenType.DIFF, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.PARTIAL, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.INTEGRAL, -3); // 2 or 4 arguments
        FUNCTION_ARG_COUNTS.put(TokenType.SUM, -4); // 1, 3, or 4 arguments
        FUNCTION_ARG_COUNTS.put(TokenType.PROD, -4); // 1, 3, or 4 arguments
        
        // Matrix/vector operations (variable arguments)
        FUNCTION_ARG_COUNTS.put(TokenType.VECTOR, -1);
        FUNCTION_ARG_COUNTS.put(TokenType.MATRIX, -1);

        // Inverse trig (1 arg)
        FUNCTION_ARG_COUNTS.put(TokenType.ARCSIN, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.ARCCOS, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.ARCTAN, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.ARCCSC, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.ARCSEC, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.ARCCOT, 1);

        // Extrema (1 arg)
        FUNCTION_ARG_COUNTS.put(TokenType.MIN, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.MAX, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.SUP, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.INF, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.LIMSUP, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.LIMINF, 1);

        // Binomial (2 args)
        FUNCTION_ARG_COUNTS.put(TokenType.BINOM, 2);

        // Norm (1 or 2), inner (2)
        FUNCTION_ARG_COUNTS.put(TokenType.NORM, -2);
        FUNCTION_ARG_COUNTS.put(TokenType.INNER, 2);

        // Vector calculus (1 arg)
        FUNCTION_ARG_COUNTS.put(TokenType.GRAD, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.DIVERGENCE, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.CURL, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.LAPLACIAN, 1);

        // Accents and text (1 arg)
        FUNCTION_ARG_COUNTS.put(TokenType.HAT, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.TILDE, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.BAR, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.VEC, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.DOT, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.DDOT, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.OVERLINE, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.UNDERLINE, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.MATHTEXT, 1);

        // Probability
        FUNCTION_ARG_COUNTS.put(TokenType.PROB, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.EXPECT, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.VAR, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.COV, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.GIVEN, 2);

        // Linear algebra
        FUNCTION_ARG_COUNTS.put(TokenType.DET, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.TRACE, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.DIM, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.RANK, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.KER, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.TRANSPOSE, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.INVERSE, 1);

        // Visual decorations
        FUNCTION_ARG_COUNTS.put(TokenType.BOXED, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.CANCEL, 1);
        FUNCTION_ARG_COUNTS.put(TokenType.UNDERBRACE, 2);
        FUNCTION_ARG_COUNTS.put(TokenType.OVERBRACE, 2);

        // Structured layout
        FUNCTION_ARG_COUNTS.put(TokenType.PIECEWISE, -5); // even number of args, at least 2
        FUNCTION_ARG_COUNTS.put(TokenType.CASES, -5); // even number of args, at least 2
        FUNCTION_ARG_COUNTS.put(TokenType.ALIGN, -6); // at least 1 argument
        FUNCTION_ARG_COUNTS.put(TokenType.SYSTEM, -6); // at least 1 argument
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
            if ((function.getType() == TokenType.VECTOR || function.getType() == TokenType.MATRIX) && argumentCount < 1) {
                throw invalidArity(function, "at least 1", argumentCount);
            }
            return;
        }
        
        if (expected == -2) {
            // 1 or 2 arguments
            if (argumentCount != 1 && argumentCount != 2) {
                throw invalidArity(function, "1 or 2", argumentCount);
            }
            return;
        }

        if (expected == -3) {
            // 2 or 4 arguments
            if (argumentCount != 2 && argumentCount != 4) {
                throw invalidArity(function, "2 or 4", argumentCount);
            }
            return;
        }

        if (expected == -4) {
            // 1, 3, or 4 arguments
            if (argumentCount != 1 && argumentCount != 3 && argumentCount != 4) {
                throw invalidArity(function, "1, 3, or 4", argumentCount);
            }
            return;
        }

        if (expected == -5) {
            // Even number of arguments, at least 2
            if (argumentCount < 2 || argumentCount % 2 != 0) {
                throw invalidArity(function, "an even number and at least 2", argumentCount);
            }
            return;
        }

        if (expected == -6) {
            // At least 1 argument
            if (argumentCount < 1) {
                throw invalidArity(function, "at least 1", argumentCount);
            }
            return;
        }
        
        if (expected != argumentCount) {
            throw invalidArity(function, String.valueOf(expected), argumentCount);
        }
    }

    private static ParserException invalidArity(Token function, String expected, int actual) {
        EuclidSignature signature = EuclidLanguage.getSignature(function.getLexeme());
        String suggestion = signature != null
                ? "Use " + signature.label()
                : "Check the canonical function signature before retrying";
        String message = String.format("Function '%s' expects %s argument%s, but got %d",
                function.getLexeme(),
                expected,
                needsPluralSuffix(expected) ? "s" : "",
                actual);
        return new ParserException("parser.invalid-arity", message, function.getLine(), function.getColumn(), suggestion);
    }

    private static boolean needsPluralSuffix(String expected) {
        return !(expected.equals("1") || expected.equals("at least 1"));
    }

    /**
     * Suggests similar function names when an unknown identifier is used.
     * @param name The identifier name
     * @return A list of suggestions
     */
    public static List<String> suggestSimilarFunctions(String name) {
        LinkedHashSet<String> suggestions = new LinkedHashSet<>();
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
        
        // Check for simple typos using Levenshtein distance against all keywords
        for (String known : EuclidLanguage.getKeywordNames()) {
            if (levenshteinDistance(lowerName, known.toLowerCase()) <= 2) {
                suggestions.add(EuclidLanguage.getCanonicalName(known));
            }
        }

        return new ArrayList<>(suggestions);
    }

    /**
     * Calculates Levenshtein distance between two strings.
     */
    private static int levenshteinDistance(String a, String b) {
        if (a.length() > 100 || b.length() > 100 || Math.abs(a.length() - b.length()) > 3) {
            return Integer.MAX_VALUE;
        }
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
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
     * @param tokens The list of tokens to validate
     * @throws ParserException if delimiters are unbalanced
     */
    public static void validateBalancedDelimiters(List<Token> tokens) throws ParserException {
        ArrayDeque<Token> stack = new ArrayDeque<>();
        for (Token token : tokens) {
            switch (token.getType()) {
                case LPAREN, LBRACKET, LBRACE -> stack.push(token);
                case RPAREN -> {
                    if (stack.isEmpty() || stack.peek().getType() != TokenType.LPAREN) {
                        throw new ParserException(
                            stack.isEmpty() ? "Unmatched closing parenthesis ')'"
                                : "Mismatched delimiter: expected '" + closingFor(stack.peek()) + "' but found ')'",
                            token.getLine(), token.getColumn());
                    }
                    stack.pop();
                }
                case RBRACKET -> {
                    if (stack.isEmpty() || stack.peek().getType() != TokenType.LBRACKET) {
                        throw new ParserException(
                            stack.isEmpty() ? "Unmatched closing bracket ']'"
                                : "Mismatched delimiter: expected '" + closingFor(stack.peek()) + "' but found ']'",
                            token.getLine(), token.getColumn());
                    }
                    stack.pop();
                }
                case RBRACE -> {
                    if (stack.isEmpty() || stack.peek().getType() != TokenType.LBRACE) {
                        throw new ParserException(
                            stack.isEmpty() ? "Unmatched closing brace '}'"
                                : "Mismatched delimiter: expected '" + closingFor(stack.peek()) + "' but found '}'",
                            token.getLine(), token.getColumn());
                    }
                    stack.pop();
                }
                default -> {}
            }
        }
        if (!stack.isEmpty()) {
            Token unclosed = stack.peekLast(); // bottom-most unclosed
            throw new ParserException(
                "Unclosed '" + stack.peekLast().getLexeme() + "' - missing '" + closingFor(unclosed) + "'",
                unclosed.getLine(), unclosed.getColumn());
        }
    }

    private static String closingFor(Token open) {
        return switch (open.getType()) {
            case LPAREN -> ")";
            case LBRACKET -> "]";
            case LBRACE -> "}";
            default -> "?";
        };
    }
}
