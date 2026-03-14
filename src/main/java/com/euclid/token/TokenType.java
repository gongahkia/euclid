package com.euclid.token;

/**
 * Enum representing all possible token types in the Euclid language.
 * Tokens are the basic building blocks produced by the lexer.
 */
public enum TokenType {
    // Special tokens
    TEXT,           // Plain text that doesn't need transpilation
    EOF,            // End of file
    NEWLINE,        // Newline character

    // Delimiters
    LPAREN,         // (
    RPAREN,         // )
    LBRACKET,       // [
    RBRACKET,       // ]
    LBRACE,         // {
    RBRACE,         // }
    COMMA,          // ,

    // Literals
    NUMBER,         // Integer or floating-point number
    IDENTIFIER,     // Variable name
    STRING,         // String literal

    // Basic arithmetic operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    MODULO,         // %
    POWER,          // ^ (alternative to pow function)

    // Mathematical constants
    PI,             // π
    E,              // e (Euler's number)
    I,              // i (imaginary unit)
    GAMMA,          // γ (Euler-Mascheroni constant)
    PHI,            // φ (golden ratio)
    INFINITY,       // ∞

    // Greek letters (lowercase)
    ALPHA,          // α
    BETA,           // β
    DELTA,          // δ
    EPSILON,        // ε
    ZETA,           // ζ
    ETA,            // η
    THETA,          // θ
    KAPPA,          // κ
    LAMBDA,         // λ
    MU,             // μ
    NU,             // ν
    XI,             // ξ
    OMICRON,        // ο
    RHO,            // ρ
    SIGMA,          // σ
    TAU,            // τ
    UPSILON,        // υ
    CHI,            // χ
    PSI,            // ψ
    OMEGA,          // ω

    // Basic operations (functions)
    POW,            // pow(x, y) → x^y
    ABS,            // abs(x) → |x|
    CEIL,           // ceil(x) → ⌈x⌉
    FLOOR,          // floor(x) → ⌊x⌋
    MOD,            // mod(a, b) → a mod b
    GCD,            // gcd(a, b)
    LCM,            // lcm(a, b)

    // Comparison and relational symbols (functions)
    LT,             // lt(a, b) → a < b
    GT,             // gt(a, b) → a > b
    LEQ,            // leq(a, b) → a ≤ b
    GEQ,            // geq(a, b) → a ≥ b
    APPROX,         // approx(a, b) → a ≈ b
    NEQ,            // neq(a, b) → a ≠ b
    EQUIV,          // equiv(a, b) → a ≡ b

    // Binary operation symbols (functions)
    PM,             // pm(a, b) → a ± b
    TIMES,          // times(a, b) → a × b
    DIV,            // div(a, b) → a ÷ b
    CDOT,           // cdot(a, b) → a · b
    AST,            // ast(a, b) → a ∗ b
    STAR,           // star(a, b) → a ⋆ b
    CIRC,           // circ(a, b) → a ∘ b
    BULLET,         // bullet(a, b) → a • b
    CAP,            // cap(a, b) → a ∩ b (intersection)
    CUP,            // cup(a, b) → a ∪ b (union)

    // Trigonometric functions
    SIN,            // sin(x)
    COS,            // cos(x)
    TAN,            // tan(x)
    CSC,            // csc(x)
    SEC,            // sec(x)
    COT,            // cot(x)

    // Hyperbolic functions
    SINH,           // sinh(x)
    COSH,           // cosh(x)
    TANH,           // tanh(x)
    CSCH,           // csch(x)
    SECH,           // sech(x)
    COTH,           // coth(x)

    // Logarithmic and exponential functions
    LOG,            // log(x, base)
    LN,             // ln(x) - natural logarithm
    EXP,            // exp(x) → e^x

    // Roots and fractions
    SQRT,           // sqrt(n, x) → ⁿ√x
    BACKSLASH_BACKSLASH,  // \\ (fraction operator: a \\ b → a/b)
    PARTIAL,        // partial(f, x) → ∂f/∂x

    // Calculus operations
    LIMIT,          // limit(f(x), x, a) → lim(x→a) f(x)
    DIFF,           // diff(f(x), x) → df/dx
    INTEGRAL,       // integral(f(x), x, a, b) → ∫[a,b] f(x) dx

    // Summation and product notation
    SUM,            // sum(expr, i, 1, n)
    PROD,           // prod(expr, i, 1, n)

    // Matrices and vectors
    VECTOR,         // vector([a, b, c])
    MATRIX,         // matrix([[a, b], [c, d]])

    // Set notation
    EMPTYSET,       // ∅
    SUBSET,         // subset(a, b) → a ⊂ b
    SUPSET,         // supset(a, b) → a ⊃ b
    SUBSETEQ,       // subseteq(a, b) → a ⊆ b
    SUPSETEQ,       // supseteq(a, b) → a ⊇ b
    UNION,          // union(a, b) → a ∪ b
    INTERSECTION,   // intersection(a, b) → a ∩ b
    SET_DIFF,       // set_diff(a, b) → a \ b
    ELEMENT_OF,     // element_of(a, b) → a ∈ b
    NOT_ELEMENT_OF, // not_element_of(a, b) → a ∉ b

    // Logic symbols
    AND,            // ∧
    OR,             // ∨
    NOT,            // ¬
    IMPLIES,        // implies(a, b) → a ⇒ b
    IFF,            // iff(a, b) → a ⇔ b
    FORALL,         // forall(x, P(x)) → ∀x P(x)
    EXISTS,         // exists(x, P(x)) → ∃x P(x)

    // Reserved keywords retained for compatibility and diagnostics
    FROM,           // Reserved for legacy sum/prod notation
    TO,             // Reserved for legacy sum/prod notation
    EQUALS,         // = (equality operator and structured bounds)

    // Accents and decorations
    HAT,            // hat(x) → \\hat{x}
    TILDE,          // tilde(x) → \\tilde{x}
    BAR,            // bar(x) → \\bar{x}
    VEC,            // vec(x) → \\vec{x}
    DOT,            // dot(x) → \\dot{x} or infix a dot b → a \\cdot b
    DDOT,           // ddot(x) → \\ddot{x}
    OVERLINE,       // overline(x) → \\overline{x}
    UNDERLINE,      // underline(x) → \\underline{x}

    // Text in math mode
    MATHTEXT,       // mathtext("label") → \\text{label}

    // Piecewise and cases
    PIECEWISE,      // piecewise() for piecewise functions
    CASES,          // cases() for case environments

    // Aligned equations
    ALIGN,          // align() for aligned equations
    SYSTEM,         // system() for systems of equations

    // Number sets
    NATURALS,       // N
    INTEGERS,       // Z
    RATIONALS,      // Q
    REALS,          // R
    COMPLEXES,      // C

    // Arrow symbols
    RIGHTARROW, LEFTARROW, LEFTRIGHTARROW, MAPSTO,
    UPARROW, DOWNARROW,
    DARROW_RIGHT, DARROW_LEFT, DARROW_LEFTRIGHT,

    // Dot sequences
    LDOTS, CDOTS, VDOTS, DDOTS,

    // Proof notation
    THEREFORE, BECAUSE, QED,

    // Geometry symbols
    PERP, PARALLEL, ANGLE, TRIANGLE, CONG, SIM, PROPTO,

    // Physics constants
    HBAR, NABLA, ELL,

    // Inverse trig functions
    ARCSIN, ARCCOS, ARCTAN, ARCCSC, ARCSEC, ARCCOT,

    // Extrema and bounds
    MIN, MAX, SUP, INF, LIMSUP, LIMINF,

    // Binomial coefficient
    BINOM,

    // Norm and inner product
    NORM, INNER,

    // Vector calculus operators
    GRAD, DIVERGENCE, CURL, LAPLACIAN,

    // Probability and statistics
    PROB, EXPECT, VAR, COV, GIVEN,

    // Linear algebra extended
    DET, TRACE, DIM, RANK, KER, TRANSPOSE, INVERSE,

    // Visual decorations
    BOXED, CANCEL, UNDERBRACE, OVERBRACE,

    // Factorial
    BANG,           // !

    // Implicit multiplication
    IMPLICIT_MULTIPLY,

    // Subscript
    UNDERSCORE,     // _

    // Math mode delimiters
    DOLLAR,         // $ (inline math)
    DOUBLE_DOLLAR   // $$ (display math)
}
