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
    SUM,            // sum(from i=1 to n) f(i)
    PROD,           // prod(from i=1 to n) f(i)

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

    // Keywords for structured expressions
    FROM,           // Used in sum/prod: from i=1
    TO,             // Used in sum/prod: to n
    EQUALS,         // = (for equations in sum/prod bounds)

    // Math mode delimiters
    DOLLAR,         // $ (inline math)
    DOUBLE_DOLLAR   // $$ (display math)
}
