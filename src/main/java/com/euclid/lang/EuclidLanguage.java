package com.euclid.lang;

import com.euclid.token.TokenType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Central language registry for Euclid keywords, aliases, signatures, and
 * editor-facing capability metadata.
 */
public final class EuclidLanguage {
    private static final Map<String, TokenType> KEYWORDS;
    private static final Map<String, String> ALIAS_TO_CANONICAL;
    private static final Map<String, List<String>> ALIASES_BY_CANONICAL;
    private static final Map<String, EuclidSignature> SIGNATURES;

    private static final Set<TokenType> FUNCTION_TYPES = EnumSet.of(
            TokenType.POW, TokenType.ABS, TokenType.CEIL, TokenType.FLOOR, TokenType.MOD, TokenType.GCD,
            TokenType.LCM, TokenType.LT, TokenType.GT, TokenType.LEQ, TokenType.GEQ, TokenType.APPROX,
            TokenType.NEQ, TokenType.EQUIV, TokenType.PM, TokenType.TIMES, TokenType.DIV, TokenType.CDOT,
            TokenType.AST, TokenType.STAR, TokenType.CIRC, TokenType.BULLET, TokenType.CAP, TokenType.CUP,
            TokenType.SIN, TokenType.COS, TokenType.TAN, TokenType.CSC, TokenType.SEC, TokenType.COT,
            TokenType.SINH, TokenType.COSH, TokenType.TANH, TokenType.CSCH, TokenType.SECH, TokenType.COTH,
            TokenType.LOG, TokenType.LN, TokenType.EXP, TokenType.SQRT, TokenType.PARTIAL, TokenType.LIMIT,
            TokenType.DIFF, TokenType.INTEGRAL, TokenType.SUM, TokenType.PROD, TokenType.VECTOR,
            TokenType.MATRIX, TokenType.SUBSET, TokenType.SUPSET, TokenType.SUBSETEQ, TokenType.SUPSETEQ,
            TokenType.UNION, TokenType.INTERSECTION, TokenType.SET_DIFF, TokenType.ELEMENT_OF,
            TokenType.NOT_ELEMENT_OF, TokenType.IMPLIES, TokenType.IFF, TokenType.FORALL, TokenType.EXISTS,
            TokenType.HAT, TokenType.TILDE, TokenType.BAR, TokenType.VEC, TokenType.DOT, TokenType.DDOT,
            TokenType.OVERLINE, TokenType.UNDERLINE, TokenType.MATHTEXT, TokenType.PIECEWISE,
            TokenType.CASES, TokenType.ALIGN, TokenType.SYSTEM, TokenType.ARCSIN, TokenType.ARCCOS,
            TokenType.ARCTAN, TokenType.ARCCSC, TokenType.ARCSEC, TokenType.ARCCOT, TokenType.MIN,
            TokenType.MAX, TokenType.SUP, TokenType.INF, TokenType.LIMSUP, TokenType.LIMINF,
            TokenType.BINOM, TokenType.NORM, TokenType.INNER, TokenType.GRAD, TokenType.DIVERGENCE,
            TokenType.CURL, TokenType.LAPLACIAN, TokenType.PROB, TokenType.EXPECT, TokenType.VAR,
            TokenType.COV, TokenType.GIVEN, TokenType.DET, TokenType.TRACE, TokenType.DIM, TokenType.RANK, TokenType.KER,
            TokenType.TRANSPOSE, TokenType.INVERSE, TokenType.BOXED, TokenType.CANCEL,
            TokenType.UNDERBRACE, TokenType.OVERBRACE
    );

    private static final Set<TokenType> CONSTANT_TYPES = EnumSet.of(
            TokenType.PI, TokenType.E, TokenType.I, TokenType.GAMMA, TokenType.PHI, TokenType.INFINITY,
            TokenType.EMPTYSET, TokenType.NATURALS, TokenType.INTEGERS, TokenType.RATIONALS, TokenType.REALS,
            TokenType.COMPLEXES, TokenType.RIGHTARROW, TokenType.LEFTARROW, TokenType.LEFTRIGHTARROW,
            TokenType.MAPSTO, TokenType.UPARROW, TokenType.DOWNARROW, TokenType.DARROW_RIGHT,
            TokenType.DARROW_LEFT, TokenType.DARROW_LEFTRIGHT, TokenType.LDOTS, TokenType.CDOTS,
            TokenType.VDOTS, TokenType.DDOTS, TokenType.THEREFORE, TokenType.BECAUSE, TokenType.QED,
            TokenType.PERP, TokenType.PARALLEL, TokenType.ANGLE, TokenType.TRIANGLE, TokenType.CONG,
            TokenType.SIM, TokenType.PROPTO, TokenType.HBAR, TokenType.NABLA, TokenType.ELL
    );

    private static final Set<TokenType> GREEK_TYPES = EnumSet.of(
            TokenType.ALPHA, TokenType.BETA, TokenType.DELTA, TokenType.EPSILON, TokenType.ZETA,
            TokenType.ETA, TokenType.THETA, TokenType.KAPPA, TokenType.LAMBDA, TokenType.MU,
            TokenType.NU, TokenType.XI, TokenType.OMICRON, TokenType.RHO, TokenType.SIGMA,
            TokenType.TAU, TokenType.UPSILON, TokenType.CHI, TokenType.PSI, TokenType.OMEGA
    );

    static {
        LinkedHashMap<String, TokenType> keywords = new LinkedHashMap<>();
        LinkedHashMap<String, String> aliasToCanonical = new LinkedHashMap<>();
        LinkedHashMap<String, EuclidSignature> signatures = new LinkedHashMap<>();

        registerKeyword(keywords, "PI", TokenType.PI);
        registerKeyword(keywords, "E", TokenType.E);
        registerKeyword(keywords, "I", TokenType.I);
        registerKeyword(keywords, "GAMMA", TokenType.GAMMA);
        registerKeyword(keywords, "PHI", TokenType.PHI);
        registerKeyword(keywords, "INFINITY", TokenType.INFINITY);
        registerAlias(keywords, aliasToCanonical, "INF", "INFINITY", TokenType.INFINITY);

        registerKeyword(keywords, "ALPHA", TokenType.ALPHA);
        registerKeyword(keywords, "BETA", TokenType.BETA);
        registerKeyword(keywords, "DELTA", TokenType.DELTA);
        registerKeyword(keywords, "EPSILON", TokenType.EPSILON);
        registerKeyword(keywords, "ZETA", TokenType.ZETA);
        registerKeyword(keywords, "ETA", TokenType.ETA);
        registerKeyword(keywords, "THETA", TokenType.THETA);
        registerKeyword(keywords, "KAPPA", TokenType.KAPPA);
        registerKeyword(keywords, "LAMBDA", TokenType.LAMBDA);
        registerKeyword(keywords, "MU", TokenType.MU);
        registerKeyword(keywords, "NU", TokenType.NU);
        registerKeyword(keywords, "XI", TokenType.XI);
        registerKeyword(keywords, "OMICRON", TokenType.OMICRON);
        registerKeyword(keywords, "RHO", TokenType.RHO);
        registerKeyword(keywords, "SIGMA", TokenType.SIGMA);
        registerKeyword(keywords, "TAU", TokenType.TAU);
        registerKeyword(keywords, "UPSILON", TokenType.UPSILON);
        registerKeyword(keywords, "CHI", TokenType.CHI);
        registerKeyword(keywords, "PSI", TokenType.PSI);
        registerKeyword(keywords, "OMEGA", TokenType.OMEGA);

        registerKeyword(keywords, "pow", TokenType.POW);
        registerKeyword(keywords, "abs", TokenType.ABS);
        registerKeyword(keywords, "ceil", TokenType.CEIL);
        registerKeyword(keywords, "floor", TokenType.FLOOR);
        registerKeyword(keywords, "mod", TokenType.MOD);
        registerKeyword(keywords, "gcd", TokenType.GCD);
        registerKeyword(keywords, "lcm", TokenType.LCM);

        registerKeyword(keywords, "lt", TokenType.LT);
        registerKeyword(keywords, "gt", TokenType.GT);
        registerKeyword(keywords, "leq", TokenType.LEQ);
        registerKeyword(keywords, "geq", TokenType.GEQ);
        registerKeyword(keywords, "approx", TokenType.APPROX);
        registerKeyword(keywords, "neq", TokenType.NEQ);
        registerKeyword(keywords, "equiv", TokenType.EQUIV);

        registerKeyword(keywords, "pm", TokenType.PM);
        registerKeyword(keywords, "times", TokenType.TIMES);
        registerKeyword(keywords, "div", TokenType.DIV);
        registerKeyword(keywords, "cdot", TokenType.CDOT);
        registerKeyword(keywords, "ast", TokenType.AST);
        registerKeyword(keywords, "star", TokenType.STAR);
        registerKeyword(keywords, "circ", TokenType.CIRC);
        registerKeyword(keywords, "bullet", TokenType.BULLET);
        registerKeyword(keywords, "cap", TokenType.CAP);
        registerKeyword(keywords, "cup", TokenType.CUP);

        registerKeyword(keywords, "sin", TokenType.SIN);
        registerKeyword(keywords, "cos", TokenType.COS);
        registerKeyword(keywords, "tan", TokenType.TAN);
        registerKeyword(keywords, "csc", TokenType.CSC);
        registerKeyword(keywords, "sec", TokenType.SEC);
        registerKeyword(keywords, "cot", TokenType.COT);
        registerKeyword(keywords, "sinh", TokenType.SINH);
        registerKeyword(keywords, "cosh", TokenType.COSH);
        registerKeyword(keywords, "tanh", TokenType.TANH);
        registerKeyword(keywords, "csch", TokenType.CSCH);
        registerKeyword(keywords, "sech", TokenType.SECH);
        registerKeyword(keywords, "coth", TokenType.COTH);

        registerKeyword(keywords, "log", TokenType.LOG);
        registerKeyword(keywords, "ln", TokenType.LN);
        registerKeyword(keywords, "exp", TokenType.EXP);
        registerKeyword(keywords, "sqrt", TokenType.SQRT);
        registerKeyword(keywords, "partial", TokenType.PARTIAL);
        registerKeyword(keywords, "limit", TokenType.LIMIT);
        registerKeyword(keywords, "diff", TokenType.DIFF);
        registerKeyword(keywords, "integral", TokenType.INTEGRAL);
        registerKeyword(keywords, "sum", TokenType.SUM);
        registerKeyword(keywords, "prod", TokenType.PROD);
        registerKeyword(keywords, "vector", TokenType.VECTOR);
        registerKeyword(keywords, "matrix", TokenType.MATRIX);

        registerKeyword(keywords, "emptyset", TokenType.EMPTYSET);
        registerKeyword(keywords, "subset", TokenType.SUBSET);
        registerAlias(keywords, aliasToCanonical, "proper_subset", "subset", TokenType.SUBSET);
        registerKeyword(keywords, "supset", TokenType.SUPSET);
        registerKeyword(keywords, "subseteq", TokenType.SUBSETEQ);
        registerKeyword(keywords, "supseteq", TokenType.SUPSETEQ);
        registerKeyword(keywords, "union", TokenType.UNION);
        registerKeyword(keywords, "intersection", TokenType.INTERSECTION);
        registerKeyword(keywords, "set_diff", TokenType.SET_DIFF);
        registerKeyword(keywords, "element_of", TokenType.ELEMENT_OF);
        registerKeyword(keywords, "not_element_of", TokenType.NOT_ELEMENT_OF);

        registerKeyword(keywords, "AND", TokenType.AND);
        registerKeyword(keywords, "OR", TokenType.OR);
        registerKeyword(keywords, "NOT", TokenType.NOT);
        registerKeyword(keywords, "implies", TokenType.IMPLIES);
        registerKeyword(keywords, "iff", TokenType.IFF);
        registerKeyword(keywords, "forall", TokenType.FORALL);
        registerKeyword(keywords, "exists", TokenType.EXISTS);
        registerKeyword(keywords, "from", TokenType.FROM);
        registerKeyword(keywords, "to", TokenType.TO);

        registerKeyword(keywords, "hat", TokenType.HAT);
        registerKeyword(keywords, "tilde", TokenType.TILDE);
        registerKeyword(keywords, "bar", TokenType.BAR);
        registerKeyword(keywords, "vec", TokenType.VEC);
        registerKeyword(keywords, "dot", TokenType.DOT);
        registerKeyword(keywords, "ddot", TokenType.DDOT);
        registerKeyword(keywords, "overline", TokenType.OVERLINE);
        registerKeyword(keywords, "underline", TokenType.UNDERLINE);
        registerKeyword(keywords, "mathtext", TokenType.MATHTEXT);
        registerKeyword(keywords, "piecewise", TokenType.PIECEWISE);
        registerKeyword(keywords, "cases", TokenType.CASES);
        registerKeyword(keywords, "align", TokenType.ALIGN);
        registerKeyword(keywords, "system", TokenType.SYSTEM);

        registerKeyword(keywords, "NATURALS", TokenType.NATURALS);
        registerKeyword(keywords, "INTEGERS", TokenType.INTEGERS);
        registerKeyword(keywords, "RATIONALS", TokenType.RATIONALS);
        registerKeyword(keywords, "REALS", TokenType.REALS);
        registerKeyword(keywords, "COMPLEXES", TokenType.COMPLEXES);

        registerKeyword(keywords, "rightarrow", TokenType.RIGHTARROW);
        registerKeyword(keywords, "leftarrow", TokenType.LEFTARROW);
        registerKeyword(keywords, "leftrightarrow", TokenType.LEFTRIGHTARROW);
        registerKeyword(keywords, "mapsto", TokenType.MAPSTO);
        registerKeyword(keywords, "uparrow", TokenType.UPARROW);
        registerKeyword(keywords, "downarrow", TokenType.DOWNARROW);
        registerKeyword(keywords, "Rightarrow", TokenType.DARROW_RIGHT);
        registerKeyword(keywords, "Leftarrow", TokenType.DARROW_LEFT);
        registerKeyword(keywords, "Leftrightarrow", TokenType.DARROW_LEFTRIGHT);

        registerKeyword(keywords, "ldots", TokenType.LDOTS);
        registerKeyword(keywords, "cdots", TokenType.CDOTS);
        registerKeyword(keywords, "vdots", TokenType.VDOTS);
        registerKeyword(keywords, "ddots", TokenType.DDOTS);

        registerKeyword(keywords, "therefore", TokenType.THEREFORE);
        registerKeyword(keywords, "because", TokenType.BECAUSE);
        registerKeyword(keywords, "qed", TokenType.QED);
        registerKeyword(keywords, "perp", TokenType.PERP);
        registerKeyword(keywords, "parallel", TokenType.PARALLEL);
        registerKeyword(keywords, "angle", TokenType.ANGLE);
        registerKeyword(keywords, "triangle", TokenType.TRIANGLE);
        registerKeyword(keywords, "cong", TokenType.CONG);
        registerKeyword(keywords, "sim", TokenType.SIM);
        registerKeyword(keywords, "propto", TokenType.PROPTO);
        registerKeyword(keywords, "hbar", TokenType.HBAR);
        registerKeyword(keywords, "nabla", TokenType.NABLA);
        registerKeyword(keywords, "ell", TokenType.ELL);

        registerKeyword(keywords, "arcsin", TokenType.ARCSIN);
        registerKeyword(keywords, "arccos", TokenType.ARCCOS);
        registerKeyword(keywords, "arctan", TokenType.ARCTAN);
        registerKeyword(keywords, "arccsc", TokenType.ARCCSC);
        registerKeyword(keywords, "arcsec", TokenType.ARCSEC);
        registerKeyword(keywords, "arccot", TokenType.ARCCOT);

        registerKeyword(keywords, "min", TokenType.MIN);
        registerKeyword(keywords, "max", TokenType.MAX);
        registerKeyword(keywords, "sup", TokenType.SUP);
        registerKeyword(keywords, "inf", TokenType.INF);
        registerKeyword(keywords, "limsup", TokenType.LIMSUP);
        registerKeyword(keywords, "liminf", TokenType.LIMINF);

        registerKeyword(keywords, "binom", TokenType.BINOM);
        registerAlias(keywords, aliasToCanonical, "choose", "binom", TokenType.BINOM);
        registerKeyword(keywords, "norm", TokenType.NORM);
        registerKeyword(keywords, "inner", TokenType.INNER);
        registerKeyword(keywords, "grad", TokenType.GRAD);
        registerKeyword(keywords, "divergence", TokenType.DIVERGENCE);
        registerKeyword(keywords, "curl", TokenType.CURL);
        registerKeyword(keywords, "laplacian", TokenType.LAPLACIAN);
        registerKeyword(keywords, "prob", TokenType.PROB);
        registerKeyword(keywords, "expect", TokenType.EXPECT);
        registerKeyword(keywords, "var", TokenType.VAR);
        registerKeyword(keywords, "cov", TokenType.COV);
        registerKeyword(keywords, "given", TokenType.GIVEN);
        registerKeyword(keywords, "det", TokenType.DET);
        registerKeyword(keywords, "trace", TokenType.TRACE);
        registerKeyword(keywords, "dim", TokenType.DIM);
        registerKeyword(keywords, "rank", TokenType.RANK);
        registerKeyword(keywords, "ker", TokenType.KER);
        registerKeyword(keywords, "transpose", TokenType.TRANSPOSE);
        registerKeyword(keywords, "inverse", TokenType.INVERSE);
        registerKeyword(keywords, "boxed", TokenType.BOXED);
        registerKeyword(keywords, "cancel", TokenType.CANCEL);
        registerKeyword(keywords, "underbrace", TokenType.UNDERBRACE);
        registerKeyword(keywords, "overbrace", TokenType.OVERBRACE);

        registerSignature(signatures, "pow", "pow(base, exponent)", "base: base value", "exponent: power");
        registerSignature(signatures, "abs", "abs(x)", "x: value");
        registerSignature(signatures, "ceil", "ceil(x)", "x: value");
        registerSignature(signatures, "floor", "floor(x)", "x: value");
        registerSignature(signatures, "sqrt", "sqrt(x) or sqrt(n, x)", "x: radicand", "n: root degree");
        registerSignature(signatures, "log", "log(x, base)", "x: value", "base: logarithm base");
        registerSignature(signatures, "ln", "ln(x)", "x: value");
        registerSignature(signatures, "exp", "exp(x)", "x: exponent");
        registerSignature(signatures, "sin", "sin(x)", "x: angle");
        registerSignature(signatures, "cos", "cos(x)", "x: angle");
        registerSignature(signatures, "tan", "tan(x)", "x: angle");
        registerSignature(signatures, "csc", "csc(x)", "x: angle");
        registerSignature(signatures, "sec", "sec(x)", "x: angle");
        registerSignature(signatures, "cot", "cot(x)", "x: angle");
        registerSignature(signatures, "sinh", "sinh(x)", "x: value");
        registerSignature(signatures, "cosh", "cosh(x)", "x: value");
        registerSignature(signatures, "tanh", "tanh(x)", "x: value");
        registerSignature(signatures, "csch", "csch(x)", "x: value");
        registerSignature(signatures, "sech", "sech(x)", "x: value");
        registerSignature(signatures, "coth", "coth(x)", "x: value");
        registerSignature(signatures, "limit", "limit(expr, var, approach)", "expr: expression", "var: variable", "approach: limit point");
        registerSignature(signatures, "diff", "diff(expr, var)", "expr: expression", "var: differentiation variable");
        registerSignature(signatures, "partial", "partial(expr, var)", "expr: expression", "var: partial variable");
        registerSignature(signatures, "integral", "integral(expr, var) or integral(expr, var, lower, upper)", "expr: integrand", "var: variable", "lower: lower bound", "upper: upper bound");
        registerSignature(signatures, "sum", "sum(expr, var, lower, upper)", "expr: summand", "var: index variable", "lower: lower bound", "upper: upper bound");
        registerSignature(signatures, "prod", "prod(expr, var, lower, upper)", "expr: multiplicand", "var: index variable", "lower: lower bound", "upper: upper bound");
        registerSignature(signatures, "forall", "forall(var, predicate)", "var: quantified variable", "predicate: body");
        registerSignature(signatures, "exists", "exists(var, predicate)", "var: quantified variable", "predicate: body");
        registerSignature(signatures, "implies", "implies(left, right)", "left: premise", "right: consequence");
        registerSignature(signatures, "iff", "iff(left, right)", "left: proposition", "right: proposition");
        registerSignature(signatures, "matrix", "matrix([[a, b], [c, d]]) or matrix([a, b], [c, d])", "rowN: bracketed row");
        registerSignature(signatures, "vector", "vector([a, b, c]) or vector(a, b, c)", "a..n: vector elements");
        registerSignature(signatures, "piecewise", "piecewise(expr1, cond1, expr2, cond2, ...)", "exprN: branch result", "condN: branch condition");
        registerSignature(signatures, "align", "align(eq1, eq2, ...)", "eqN: aligned expression");
        registerSignature(signatures, "system", "system(eq1, eq2, ...)", "eqN: system equation");
        registerSignature(signatures, "binom", "binom(n, k)", "n: total", "k: chosen");
        registerSignature(signatures, "norm", "norm(x) or norm(x, p)", "x: expression", "p: norm order");
        registerSignature(signatures, "inner", "inner(x, y)", "x: left vector", "y: right vector");
        registerSignature(signatures, "prob", "prob(event)", "event: event expression");
        registerSignature(signatures, "expect", "expect(expr)", "expr: random variable or expression");
        registerSignature(signatures, "var", "var(expr)", "expr: random variable or expression");
        registerSignature(signatures, "cov", "cov(x, y)", "x: first variable", "y: second variable");
        registerSignature(signatures, "given", "given(event, condition)", "event: left-hand event", "condition: right-hand event");

        KEYWORDS = Collections.unmodifiableMap(keywords);
        ALIAS_TO_CANONICAL = Collections.unmodifiableMap(aliasToCanonical);
        ALIASES_BY_CANONICAL = Collections.unmodifiableMap(reverseAliases(aliasToCanonical));
        SIGNATURES = Collections.unmodifiableMap(signatures);
    }

    private EuclidLanguage() {
    }

    public static Map<String, TokenType> getKeywordMap() {
        return KEYWORDS;
    }

    public static Set<String> getKeywordNames() {
        return KEYWORDS.keySet();
    }

    public static TokenType getKeywordType(String name) {
        return KEYWORDS.get(name);
    }

    public static String getCanonicalName(String name) {
        return ALIAS_TO_CANONICAL.getOrDefault(name, name);
    }

    public static Set<String> getCanonicalKeywordNames() {
        return KEYWORDS.keySet().stream()
                .filter(name -> getCanonicalName(name).equals(name))
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    public static EuclidSignature getSignature(String name) {
        return SIGNATURES.get(getCanonicalName(name));
    }

    public static Map<String, List<String>> getAliasesByCanonical() {
        return ALIASES_BY_CANONICAL;
    }

    public static boolean isFunctionType(TokenType type) {
        return FUNCTION_TYPES.contains(type);
    }

    public static boolean isConstantType(TokenType type) {
        return CONSTANT_TYPES.contains(type);
    }

    public static boolean isGreekType(TokenType type) {
        return GREEK_TYPES.contains(type);
    }

    public static EuclidCapabilityManifest capabilityManifest() {
        List<EuclidCapability> capabilities = new ArrayList<>();
        for (String name : getCanonicalKeywordNames()) {
            TokenType type = KEYWORDS.get(name);
            capabilities.add(new EuclidCapability(
                    name,
                    classify(name, type),
                    type,
                    aliasPolicy(name),
                    ALIASES_BY_CANONICAL.getOrDefault(name, List.of()),
                    SIGNATURES.get(name)
            ));
        }
        return new EuclidCapabilityManifest(List.copyOf(capabilities));
    }

    public static String canonicalizeSource(String source) {
        return canonicalizeSourceWithMetadata(source).canonicalSource();
    }

    public static EuclidCanonicalizationResult canonicalizeSourceWithMetadata(String source) {
        StringBuilder canonicalized = new StringBuilder(source.length());
        List<EuclidAliasOccurrence> occurrences = new ArrayList<>();

        int index = 0;
        int line = 1;
        int column = 1;

        while (index < source.length()) {
            char current = source.charAt(index);

            if (current == '/' && index + 1 < source.length() && source.charAt(index + 1) == '/') {
                int commentEnd = advanceUntilNewline(source, index);
                canonicalized.append(source, index, commentEnd);
                column += commentEnd - index;
                index = commentEnd;
                continue;
            }

            if (current == '#') {
                int commentEnd = advanceUntilNewline(source, index);
                canonicalized.append(source, index, commentEnd);
                column += commentEnd - index;
                index = commentEnd;
                continue;
            }

            if (current == '"') {
                int stringEnd = appendStringLiteral(source, canonicalized, index);
                for (int i = index; i < stringEnd; i++) {
                    if (source.charAt(i) == '\n') {
                        line++;
                        column = 1;
                    } else {
                        column++;
                    }
                }
                index = stringEnd;
                continue;
            }

            if (isIdentifierStart(current)) {
                int tokenStartLine = line;
                int tokenStartColumn = column;
                int tokenEnd = advanceIdentifier(source, index);
                String identifier = source.substring(index, tokenEnd);
                String canonical = ALIAS_TO_CANONICAL.get(identifier);
                if (canonical != null) {
                    canonicalized.append(canonical);
                    occurrences.add(new EuclidAliasOccurrence(identifier, canonical, tokenStartLine, tokenStartColumn));
                } else {
                    canonicalized.append(identifier);
                }
                column += tokenEnd - index;
                index = tokenEnd;
                continue;
            }

            canonicalized.append(current);
            if (current == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            index++;
        }

        return new EuclidCanonicalizationResult(canonicalized.toString(), List.copyOf(occurrences));
    }

    private static EuclidCapabilityKind classify(String name, TokenType type) {
        if (isFunctionType(type)) {
            return EuclidCapabilityKind.FUNCTION;
        }
        if (type == TokenType.AND || type == TokenType.OR || type == TokenType.NOT) {
            return EuclidCapabilityKind.OPERATOR;
        }
        if (isConstantType(type) || isGreekType(type)) {
            return EuclidCapabilityKind.CONSTANT;
        }
        if (type == TokenType.FROM || type == TokenType.TO) {
            return EuclidCapabilityKind.KEYWORD;
        }
        return Character.isUpperCase(name.charAt(0)) ? EuclidCapabilityKind.CONSTANT : EuclidCapabilityKind.KEYWORD;
    }

    private static EuclidAliasPolicy aliasPolicy(String name) {
        return ALIASES_BY_CANONICAL.containsKey(name)
                ? EuclidAliasPolicy.WARN
                : EuclidAliasPolicy.NONE;
    }

    private static boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isIdentifierPart(char c) {
        return isIdentifierStart(c) || (c >= '0' && c <= '9') || c == '_';
    }

    private static int advanceIdentifier(String source, int start) {
        int index = start + 1;
        while (index < source.length() && isIdentifierPart(source.charAt(index))) {
            index++;
        }
        return index;
    }

    private static int advanceUntilNewline(String source, int start) {
        int index = start;
        while (index < source.length() && source.charAt(index) != '\n') {
            index++;
        }
        return index;
    }

    private static int appendStringLiteral(String source, StringBuilder output, int start) {
        int index = start;
        boolean escaping = false;

        while (index < source.length()) {
            char c = source.charAt(index);
            output.append(c);
            index++;

            if (escaping) {
                escaping = false;
                continue;
            }

            if (c == '\\') {
                escaping = true;
                continue;
            }

            if (c == '"') {
                break;
            }
        }

        return index;
    }

    private static Map<String, List<String>> reverseAliases(Map<String, String> aliasToCanonical) {
        LinkedHashMap<String, List<String>> reversed = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : aliasToCanonical.entrySet()) {
            reversed.computeIfAbsent(entry.getValue(), ignored -> new ArrayList<>()).add(entry.getKey());
        }
        LinkedHashMap<String, List<String>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : reversed.entrySet()) {
            result.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return result;
    }

    private static void registerKeyword(Map<String, TokenType> keywords, String name, TokenType type) {
        keywords.put(name, type);
    }

    private static void registerAlias(Map<String, TokenType> keywords, Map<String, String> aliases, String alias, String canonical, TokenType type) {
        keywords.put(alias, type);
        aliases.put(alias, canonical);
    }

    private static void registerSignature(Map<String, EuclidSignature> signatures, String name, String label, String... parameters) {
        signatures.put(name, new EuclidSignature(label, List.of(parameters)));
    }
}
