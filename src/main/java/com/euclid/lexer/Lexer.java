package com.euclid.lexer;

import com.euclid.token.Token;
import com.euclid.token.TokenType;
import com.euclid.exception.LexerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lexer for the Euclid language.
 * Converts input text into a stream of tokens for the parser to consume.
 */
public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;      // Start of current lexeme
    private int current = 0;    // Current character
    private int line = 1;       // Current line (1-indexed)
    private int column = 1;     // Current column (1-indexed)
    private int tokenStartColumn = 1; // Column where current token started

    // Keywords and reserved words mapping
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        // Mathematical constants
        keywords.put("PI", TokenType.PI);
        keywords.put("E", TokenType.E);
        keywords.put("I", TokenType.I);
        keywords.put("GAMMA", TokenType.GAMMA);
        keywords.put("PHI", TokenType.PHI);
        keywords.put("INFINITY", TokenType.INFINITY);

        // Greek letters
        keywords.put("ALPHA", TokenType.ALPHA);
        keywords.put("BETA", TokenType.BETA);
        keywords.put("DELTA", TokenType.DELTA);
        keywords.put("EPSILON", TokenType.EPSILON);
        keywords.put("ZETA", TokenType.ZETA);
        keywords.put("ETA", TokenType.ETA);
        keywords.put("THETA", TokenType.THETA);
        keywords.put("KAPPA", TokenType.KAPPA);
        keywords.put("LAMBDA", TokenType.LAMBDA);
        keywords.put("MU", TokenType.MU);
        keywords.put("NU", TokenType.NU);
        keywords.put("XI", TokenType.XI);
        keywords.put("OMICRON", TokenType.OMICRON);
        keywords.put("RHO", TokenType.RHO);
        keywords.put("SIGMA", TokenType.SIGMA);
        keywords.put("TAU", TokenType.TAU);
        keywords.put("UPSILON", TokenType.UPSILON);
        keywords.put("CHI", TokenType.CHI);
        keywords.put("PSI", TokenType.PSI);
        keywords.put("OMEGA", TokenType.OMEGA);

        // Basic operations
        keywords.put("pow", TokenType.POW);
        keywords.put("abs", TokenType.ABS);
        keywords.put("ceil", TokenType.CEIL);
        keywords.put("floor", TokenType.FLOOR);
        keywords.put("mod", TokenType.MOD);
        keywords.put("gcd", TokenType.GCD);
        keywords.put("lcm", TokenType.LCM);

        // Comparison operators
        keywords.put("lt", TokenType.LT);
        keywords.put("gt", TokenType.GT);
        keywords.put("leq", TokenType.LEQ);
        keywords.put("geq", TokenType.GEQ);
        keywords.put("approx", TokenType.APPROX);
        keywords.put("neq", TokenType.NEQ);
        keywords.put("equiv", TokenType.EQUIV);

        // Binary operation symbols
        keywords.put("pm", TokenType.PM);
        keywords.put("times", TokenType.TIMES);
        keywords.put("div", TokenType.DIV);
        keywords.put("cdot", TokenType.CDOT);
        keywords.put("ast", TokenType.AST);
        keywords.put("star", TokenType.STAR);
        keywords.put("circ", TokenType.CIRC);
        keywords.put("bullet", TokenType.BULLET);
        keywords.put("cap", TokenType.CAP);
        keywords.put("cup", TokenType.CUP);

        // Trigonometric functions
        keywords.put("sin", TokenType.SIN);
        keywords.put("cos", TokenType.COS);
        keywords.put("tan", TokenType.TAN);
        keywords.put("csc", TokenType.CSC);
        keywords.put("sec", TokenType.SEC);
        keywords.put("cot", TokenType.COT);

        // Hyperbolic functions
        keywords.put("sinh", TokenType.SINH);
        keywords.put("cosh", TokenType.COSH);
        keywords.put("tanh", TokenType.TANH);

        // Logarithmic and exponential
        keywords.put("log", TokenType.LOG);
        keywords.put("ln", TokenType.LN);
        keywords.put("exp", TokenType.EXP);

        // Roots and fractions
        keywords.put("sqrt", TokenType.SQRT);
        keywords.put("partial", TokenType.PARTIAL);

        // Calculus
        keywords.put("limit", TokenType.LIMIT);
        keywords.put("diff", TokenType.DIFF);
        keywords.put("integral", TokenType.INTEGRAL);

        // Summation and product
        keywords.put("sum", TokenType.SUM);
        keywords.put("prod", TokenType.PROD);

        // Matrices and vectors
        keywords.put("vector", TokenType.VECTOR);
        keywords.put("matrix", TokenType.MATRIX);

        // Set notation constants
        keywords.put("emptyset", TokenType.EMPTYSET);

        // Set operations
        keywords.put("subset", TokenType.SUBSET);
        keywords.put("supset", TokenType.SUPSET);
        keywords.put("subseteq", TokenType.SUBSETEQ);
        keywords.put("supseteq", TokenType.SUPSETEQ);
        keywords.put("union", TokenType.UNION);
        keywords.put("intersection", TokenType.INTERSECTION);
        keywords.put("set_diff", TokenType.SET_DIFF);
        keywords.put("element_of", TokenType.ELEMENT_OF);
        keywords.put("not_element_of", TokenType.NOT_ELEMENT_OF);

        // Logic symbols
        keywords.put("AND", TokenType.AND);
        keywords.put("OR", TokenType.OR);
        keywords.put("NOT", TokenType.NOT);
        keywords.put("implies", TokenType.IMPLIES);
        keywords.put("iff", TokenType.IFF);
        keywords.put("forall", TokenType.FORALL);
        keywords.put("exists", TokenType.EXISTS);

        // Keywords for structured expressions
        keywords.put("from", TokenType.FROM);
        keywords.put("to", TokenType.TO);

        // Accents and decorations
        keywords.put("hat", TokenType.HAT);
        keywords.put("tilde", TokenType.TILDE);
        keywords.put("bar", TokenType.BAR);
        keywords.put("vec", TokenType.VEC);
        keywords.put("dot", TokenType.DOT);
        keywords.put("ddot", TokenType.DDOT);
        keywords.put("overline", TokenType.OVERLINE);
        keywords.put("underline", TokenType.UNDERLINE);

        // Text in math mode
        keywords.put("mathtext", TokenType.MATHTEXT);

        // Piecewise and cases
        keywords.put("piecewise", TokenType.PIECEWISE);
        keywords.put("cases", TokenType.CASES);

        // Aligned equations
        keywords.put("align", TokenType.ALIGN);
        keywords.put("system", TokenType.SYSTEM);

        // Number sets
        keywords.put("NATURALS", TokenType.NATURALS);
        keywords.put("INTEGERS", TokenType.INTEGERS);
        keywords.put("RATIONALS", TokenType.RATIONALS);
        keywords.put("REALS", TokenType.REALS);
        keywords.put("COMPLEXES", TokenType.COMPLEXES);

        // Arrow symbols
        keywords.put("rightarrow", TokenType.RIGHTARROW);
        keywords.put("leftarrow", TokenType.LEFTARROW);
        keywords.put("leftrightarrow", TokenType.LEFTRIGHTARROW);
        keywords.put("mapsto", TokenType.MAPSTO);
        keywords.put("uparrow", TokenType.UPARROW);
        keywords.put("downarrow", TokenType.DOWNARROW);
        keywords.put("Rightarrow", TokenType.DARROW_RIGHT);
        keywords.put("Leftarrow", TokenType.DARROW_LEFT);
        keywords.put("Leftrightarrow", TokenType.DARROW_LEFTRIGHT);

        // Dot sequences
        keywords.put("ldots", TokenType.LDOTS);
        keywords.put("cdots", TokenType.CDOTS);
        keywords.put("vdots", TokenType.VDOTS);
        keywords.put("ddots", TokenType.DDOTS);

        // Proof notation
        keywords.put("therefore", TokenType.THEREFORE);
        keywords.put("because", TokenType.BECAUSE);
        keywords.put("qed", TokenType.QED);

        // Geometry symbols
        keywords.put("perp", TokenType.PERP);
        keywords.put("parallel", TokenType.PARALLEL);
        keywords.put("angle", TokenType.ANGLE);
        keywords.put("triangle", TokenType.TRIANGLE);
        keywords.put("cong", TokenType.CONG);
        keywords.put("sim", TokenType.SIM);
        keywords.put("propto", TokenType.PROPTO);

        // Physics constants
        keywords.put("hbar", TokenType.HBAR);
        keywords.put("nabla", TokenType.NABLA);
        keywords.put("ell", TokenType.ELL);

        // Inverse trig
        keywords.put("arcsin", TokenType.ARCSIN);
        keywords.put("arccos", TokenType.ARCCOS);
        keywords.put("arctan", TokenType.ARCTAN);
        keywords.put("arccsc", TokenType.ARCCSC);
        keywords.put("arcsec", TokenType.ARCSEC);
        keywords.put("arccot", TokenType.ARCCOT);

        // Extrema and bounds
        keywords.put("min", TokenType.MIN);
        keywords.put("max", TokenType.MAX);
        keywords.put("sup", TokenType.SUP);
        keywords.put("inf", TokenType.INF);
        keywords.put("limsup", TokenType.LIMSUP);
        keywords.put("liminf", TokenType.LIMINF);

        // Binomial coefficient
        keywords.put("binom", TokenType.BINOM);
        keywords.put("choose", TokenType.BINOM);

        // Norm and inner product
        keywords.put("norm", TokenType.NORM);
        keywords.put("inner", TokenType.INNER);

        // Vector calculus
        keywords.put("grad", TokenType.GRAD);
        keywords.put("divergence", TokenType.DIVERGENCE);
        keywords.put("curl", TokenType.CURL);
        keywords.put("laplacian", TokenType.LAPLACIAN);

        // Probability and statistics
        keywords.put("prob", TokenType.PROB);
        keywords.put("expect", TokenType.EXPECT);
        keywords.put("var", TokenType.VAR);
        keywords.put("cov", TokenType.COV);

        // Linear algebra extended
        keywords.put("det", TokenType.DET);
        keywords.put("trace", TokenType.TRACE);
        keywords.put("dim", TokenType.DIM);
        keywords.put("rank", TokenType.RANK);
        keywords.put("ker", TokenType.KER);
        keywords.put("transpose", TokenType.TRANSPOSE);
        keywords.put("inverse", TokenType.INVERSE);

        // Visual decorations
        keywords.put("boxed", TokenType.BOXED);
        keywords.put("cancel", TokenType.CANCEL);
        keywords.put("underbrace", TokenType.UNDERBRACE);
        keywords.put("overbrace", TokenType.OVERBRACE);
    }

    public static Set<String> getKeywordNames() {
        return Collections.unmodifiableSet(keywords.keySet());
    }

    public static TokenType getKeywordType(String name) {
        return keywords.get(name);
    }

    /**
     * Creates a new lexer for the given source code.
     *
     * @param source The source code to tokenize
     */
    public Lexer(String source) {
        this.source = source;
    }

    /**
     * Tokenizes the entire source code.
     *
     * @return A list of tokens
     * @throws LexerException if a lexing error occurs
     */
    public List<Token> tokenize() throws LexerException {
        while (!isAtEnd()) {
            start = current;
            tokenStartColumn = column;
            scanToken();
        }

        // Add EOF token
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    /**
     * Scans a single token.
     */
    private void scanToken() throws LexerException {
        char c = advance();

        switch (c) {
            case '(' -> addToken(TokenType.LPAREN);
            case ')' -> addToken(TokenType.RPAREN);
            case '[' -> addToken(TokenType.LBRACKET);
            case ']' -> addToken(TokenType.RBRACKET);
            case '{' -> addToken(TokenType.LBRACE);
            case '}' -> addToken(TokenType.RBRACE);
            case ',' -> addToken(TokenType.COMMA);
            case '+' -> addToken(TokenType.PLUS);
            case '-' -> addToken(TokenType.MINUS);
            case '*' -> addToken(TokenType.MULTIPLY);
            case '/' -> {
                // Check for // comment
                if (match('/')) {
                    // Skip until end of line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.DIVIDE);
                }
            }
            case '%' -> addToken(TokenType.MODULO);
            case '^' -> addToken(TokenType.POWER);
            case '#' -> {
                // Python-style comment: skip until end of line
                while (peek() != '\n' && !isAtEnd()) advance();
            }
            case '!' -> addToken(TokenType.BANG);
            case '_' -> addToken(TokenType.UNDERSCORE);
            case '=' -> addToken(TokenType.EQUALS);
            case '\n' -> {
                addToken(TokenType.NEWLINE);
                line++;
                column = 1;
            }
            case '\r', '\t', ' ' -> { /* Skip whitespace but track columns */ }
            case '\\' -> {
                // Check for \\ (fraction operator)
                if (match('\\')) {
                    addToken(TokenType.BACKSLASH_BACKSLASH);
                } else {
                    // Single backslash could be part of LaTeX, treat as text
                    addToken(TokenType.TEXT);
                }
            }
            case '$' -> {
                // Check for $$ (display math)
                if (match('$')) {
                    addToken(TokenType.DOUBLE_DOLLAR);
                } else {
                    addToken(TokenType.DOLLAR);
                }
            }
            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    // Treat unknown characters as text (for markdown compatibility)
                    addToken(TokenType.TEXT);
                }
            }
        }
    }

    /**
     * Scans a number literal (integer or floating-point).
     */
    private void number() {
        while (isDigit(peek())) advance();

        // Look for decimal part
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // Consume the '.'
            while (isDigit(peek())) advance();
        }

        // Look for scientific notation
        if (peek() == 'e' || peek() == 'E') {
            advance();
            if (peek() == '+' || peek() == '-') advance();
            while (isDigit(peek())) advance();
        }

        String text = source.substring(start, current);
        if (text.contains(".") || text.contains("e") || text.contains("E")) {
            double value = Double.parseDouble(text);
            addToken(TokenType.NUMBER, value);
        } else {
            long value = Long.parseLong(text);
            addToken(TokenType.NUMBER, value);
        }
    }

    /**
     * Scans an identifier or keyword.
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        // speculatively consume underscore if the result is a keyword (set_diff, not_element_of)
        if (peek() == '_') {
            int saved = current;
            int savedCol = column;
            advance(); // consume _
            while (isAlphaNumeric(peek()) || peek() == '_') advance();
            String full = source.substring(start, current);
            if (!keywords.containsKey(full)) {
                current = saved; // backtrack: _ is a subscript, not part of keyword
                column = savedCol;
            }
        }

        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }

    /**
     * Checks if the current character matches the expected character.
     * If it matches, consumes it and returns true.
     *
     * @param expected The expected character
     * @return true if the character matches
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        column++;
        return true;
    }

    /**
     * Returns the current character without consuming it.
     *
     * @return The current character, or '\0' if at end
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Returns the next character without consuming it.
     *
     * @return The next character, or '\0' if at end
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * Checks if a character is a digit.
     *
     * @param c The character to check
     * @return true if the character is a digit
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Checks if a character is alphabetic.
     *
     * @param c The character to check
     * @return true if the character is alphabetic
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z');
    }

    /**
     * Checks if a character is alphanumeric.
     *
     * @param c The character to check
     * @return true if the character is alphanumeric
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Checks if we've reached the end of the source.
     *
     * @return true if at the end
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Consumes and returns the current character.
     *
     * @return The current character
     */
    private char advance() {
        column++;
        return source.charAt(current++);
    }

    /**
     * Adds a token without a literal value.
     *
     * @param type The token type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token with a literal value.
     *
     * @param type    The token type
     * @param literal The literal value
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, line, tokenStartColumn, literal));
    }
}
