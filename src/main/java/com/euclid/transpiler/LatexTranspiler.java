package com.euclid.transpiler;

import com.euclid.ast.*;
import com.euclid.token.TokenType;
import com.euclid.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Transpiler that converts Euclid AST to LaTeX/MathJax.
 * Implements the Visitor pattern for AST traversal.
 */
public class LatexTranspiler implements AstVisitor<String> {
    private final MathMode mathMode;

    /**
     * Creates a new LatexTranspiler with default math mode (NONE).
     */
    public LatexTranspiler() {
        this(MathMode.NONE);
    }

    /**
     * Creates a new LatexTranspiler with the specified math mode.
     *
     * @param mathMode The math mode for wrapping LaTeX output
     */
    public LatexTranspiler(MathMode mathMode) {
        this.mathMode = mathMode;
    }

    @Override
    public String visitDocumentNode(DocumentNode node) {
        StringBuilder result = new StringBuilder();
        for (AstNode n : node.getNodes()) {
            String latex = n.accept(this);

            // Apply math mode wrapping for each expression
            if (n instanceof TextExpr) {
                // Don't wrap text expressions
                result.append(latex);
            } else {
                result.append(wrapMathMode(latex));
            }
        }
        return result.toString();
    }

    /**
     * Wraps the LaTeX code with appropriate math mode delimiters.
     *
     * @param latex The raw LaTeX code
     * @return The wrapped LaTeX code
     */
    private String wrapMathMode(String latex) {
        return switch (mathMode) {
            case INLINE -> "$" + latex + "$";
            case DISPLAY -> "$$" + latex + "$$";
            case NONE -> latex;
        };
    }

    @Override
    public String visitLiteralExpr(LiteralExpr expr) {
        Object value = expr.getValue();

        // Handle TokenType constants
        if (value instanceof TokenType type) {
            return switch (type) {
                // Mathematical constants
                case PI -> "\\pi";
                case E -> "e";
                case I -> "i";
                case GAMMA -> "\\gamma";
                case PHI -> "\\phi";
                case INFINITY -> "\\infty";

                // Greek letters
                case ALPHA -> "\\alpha";
                case BETA -> "\\beta";
                case DELTA -> "\\delta";
                case EPSILON -> "\\epsilon";
                case ZETA -> "\\zeta";
                case ETA -> "\\eta";
                case THETA -> "\\theta";
                case KAPPA -> "\\kappa";
                case LAMBDA -> "\\lambda";
                case MU -> "\\mu";
                case NU -> "\\nu";
                case XI -> "\\xi";
                case OMICRON -> "o";
                case RHO -> "\\rho";
                case SIGMA -> "\\sigma";
                case TAU -> "\\tau";
                case UPSILON -> "\\upsilon";
                case CHI -> "\\chi";
                case PSI -> "\\psi";
                case OMEGA -> "\\omega";

                // Set notation
                case EMPTYSET -> "\\emptyset";

                // Logic symbols
                case AND -> "\\land";
                case OR -> "\\lor";
                case NOT -> "\\neg";

                // Number sets
                case NATURALS -> "\\mathbb{N}";
                case INTEGERS -> "\\mathbb{Z}";
                case RATIONALS -> "\\mathbb{Q}";
                case REALS -> "\\mathbb{R}";
                case COMPLEXES -> "\\mathbb{C}";

                // Arrows
                case RIGHTARROW -> "\\rightarrow";
                case LEFTARROW -> "\\leftarrow";
                case LEFTRIGHTARROW -> "\\leftrightarrow";
                case MAPSTO -> "\\mapsto";
                case UPARROW -> "\\uparrow";
                case DOWNARROW -> "\\downarrow";
                case DARROW_RIGHT -> "\\Rightarrow";
                case DARROW_LEFT -> "\\Leftarrow";
                case DARROW_LEFTRIGHT -> "\\Leftrightarrow";

                // Dots
                case LDOTS -> "\\ldots";
                case CDOTS -> "\\cdots";
                case VDOTS -> "\\vdots";
                case DDOTS -> "\\ddots";

                // Proof
                case THEREFORE -> "\\therefore";
                case BECAUSE -> "\\because";
                case QED -> "\\blacksquare";

                // Geometry
                case PERP -> "\\perp";
                case PARALLEL -> "\\parallel";
                case ANGLE -> "\\angle";
                case TRIANGLE -> "\\triangle";
                case CONG -> "\\cong";
                case SIM -> "\\sim";
                case PROPTO -> "\\propto";

                // Physics
                case HBAR -> "\\hbar";
                case NABLA -> "\\nabla";
                case ELL -> "\\ell";

                default -> value.toString();
            };
        }

        // Handle numeric literals
        return value.toString();
    }

    @Override
    public String visitIdentifierExpr(IdentifierExpr expr) {
        return expr.getName();
    }

    @Override
    public String visitBinaryExpr(BinaryExpr expr) {
        String left = expr.getLeft().accept(this);
        String right = expr.getRight().accept(this);
        TokenType op = expr.getOperator().getType();

        return switch (op) {
            case PLUS -> left + " + " + right;
            case MINUS -> left + " - " + right;
            case MULTIPLY -> left + " * " + right;
            case DIVIDE -> left + " / " + right;
            case EQUALS -> left + " = " + right;
            case AND -> left + " \\land " + right;
            case OR -> left + " \\lor " + right;
            case DOT -> left + " \\cdot " + right;
            case POWER -> left + "^{" + right + "}";
            case UNDERSCORE -> left + "_{" + right + "}";
            case IMPLICIT_MULTIPLY -> left + right;
            case BACKSLASH_BACKSLASH -> "\\frac{" + left + "}{" + right + "}";
            default -> left + " " + expr.getOperator().getLexeme() + " " + right;
        };
    }

    @Override
    public String visitUnaryExpr(UnaryExpr expr) {
        String operand = expr.getOperand().accept(this);
        TokenType op = expr.getOperator().getType();

        return switch (op) {
            case MINUS -> "-" + operand;
            case PLUS -> "+" + operand;
            case NOT -> "\\neg " + operand;
            case BANG -> operand + "!";
            default -> expr.getOperator().getLexeme() + operand;
        };
    }

    @Override
    public String visitCallExpr(CallExpr expr) {
        TokenType function = expr.getFunction().getType();
        List<AstNode> args = expr.getArguments();

        return switch (function) {
            // Basic operations
            case POW -> transpilePow(args);
            case ABS -> transpileAbs(args);
            case CEIL -> transpileCeil(args);
            case FLOOR -> transpileFloor(args);
            case MOD -> transpileMod(args);
            case GCD -> transpileGcd(args);
            case LCM -> transpileLcm(args);

            // Comparison operators
            case LT -> transpileBinarySymbol(args, "<");
            case GT -> transpileBinarySymbol(args, ">");
            case LEQ -> transpileBinarySymbol(args, "\\leq");
            case GEQ -> transpileBinarySymbol(args, "\\geq");
            case APPROX -> transpileBinarySymbol(args, "\\approx");
            case NEQ -> transpileBinarySymbol(args, "\\neq");
            case EQUIV -> transpileBinarySymbol(args, "\\equiv");

            // Binary operation symbols
            case PM -> transpileBinarySymbol(args, "\\pm");
            case TIMES -> transpileBinarySymbol(args, "\\times");
            case DIV -> transpileBinarySymbol(args, "\\div");
            case CDOT -> transpileBinarySymbol(args, "\\cdot");
            case AST -> transpileBinarySymbol(args, "\\ast");
            case STAR -> transpileBinarySymbol(args, "\\star");
            case CIRC -> transpileBinarySymbol(args, "\\circ");
            case BULLET -> transpileBinarySymbol(args, "\\bullet");
            case CAP -> transpileBinarySymbol(args, "\\cap");
            case CUP -> transpileBinarySymbol(args, "\\cup");

            // Trigonometric functions
            case SIN -> transpileTrigFunction("\\sin", args);
            case COS -> transpileTrigFunction("\\cos", args);
            case TAN -> transpileTrigFunction("\\tan", args);
            case CSC -> transpileTrigFunction("\\csc", args);
            case SEC -> transpileTrigFunction("\\sec", args);
            case COT -> transpileTrigFunction("\\cot", args);

            // Hyperbolic functions
            case SINH -> transpileTrigFunction("\\sinh", args);
            case COSH -> transpileTrigFunction("\\cosh", args);
            case TANH -> transpileTrigFunction("\\tanh", args);
            case CSCH -> transpileTrigFunction("\\operatorname{csch}", args);
            case SECH -> transpileTrigFunction("\\operatorname{sech}", args);
            case COTH -> transpileTrigFunction("\\coth", args);

            // Logarithmic and exponential
            case LOG -> transpileLog(args);
            case LN -> transpileLn(args);
            case EXP -> transpileExp(args);

            // Roots and fractions
            case SQRT -> transpileSqrt(args);
            case PARTIAL -> transpilePartial(args);

            // Calculus
            case LIMIT -> transpileLimit(args);
            case DIFF -> transpileDiff(args);
            case INTEGRAL -> transpileIntegral(args);

            // Summation and product
            case SUM -> transpileSum(args);
            case PROD -> transpileProd(args);

            // Matrices and vectors
            case VECTOR -> transpileVector(args);
            case MATRIX -> transpileMatrix(args);
            case LBRACKET -> args.stream().map(a -> a.accept(this)).collect(Collectors.joining(", "));

            // Set operations
            case SUBSET -> transpileBinarySymbol(args, "\\subset");
            case SUPSET -> transpileBinarySymbol(args, "\\supset");
            case SUBSETEQ -> transpileBinarySymbol(args, "\\subseteq");
            case SUPSETEQ -> transpileBinarySymbol(args, "\\supseteq");
            case UNION -> transpileBinarySymbol(args, "\\cup");
            case INTERSECTION -> transpileBinarySymbol(args, "\\cap");
            case SET_DIFF -> transpileBinarySymbol(args, "\\setminus");
            case ELEMENT_OF -> transpileBinarySymbol(args, "\\in");
            case NOT_ELEMENT_OF -> transpileBinarySymbol(args, "\\notin");

            // Logic symbols
            case IMPLIES -> transpileBinarySymbol(args, "\\implies");
            case IFF -> transpileBinarySymbol(args, "\\iff");
            case AND -> transpileBinarySymbol(args, "\\land");
            case OR -> transpileBinarySymbol(args, "\\lor");
            case NOT -> transpileNot(args);
            case FORALL -> transpileForall(args);
            case EXISTS -> transpileExists(args);

            // Accents and decorations
            case HAT -> transpileAccent("\\hat", args);
            case TILDE -> transpileAccent("\\tilde", args);
            case BAR -> transpileAccent("\\bar", args);
            case VEC -> transpileAccent("\\vec", args);
            case DOT -> transpileAccent("\\dot", args);
            case DDOT -> transpileAccent("\\ddot", args);
            case OVERLINE -> transpileAccent("\\overline", args);
            case UNDERLINE -> transpileAccent("\\underline", args);

            // Text in math mode
            case MATHTEXT -> transpileMathtext(args);

            // Piecewise and cases
            case PIECEWISE -> transpilePiecewise(args);
            case CASES -> transpileCases(args);

            // Aligned equations
            case ALIGN -> transpileAlign(args);
            case SYSTEM -> transpileSystem(args);

            // Inverse trig
            case ARCSIN -> transpileTrigFunction("\\arcsin", args);
            case ARCCOS -> transpileTrigFunction("\\arccos", args);
            case ARCTAN -> transpileTrigFunction("\\arctan", args);
            case ARCCSC -> transpileTrigFunction("\\operatorname{arccsc}", args);
            case ARCSEC -> transpileTrigFunction("\\operatorname{arcsec}", args);
            case ARCCOT -> transpileTrigFunction("\\operatorname{arccot}", args);

            // Extrema
            case MIN -> transpileTrigFunction("\\min", args);
            case MAX -> transpileTrigFunction("\\max", args);
            case SUP -> transpileTrigFunction("\\sup", args);
            case INF -> transpileTrigFunction("\\inf", args);
            case LIMSUP -> transpileTrigFunction("\\limsup", args);
            case LIMINF -> transpileTrigFunction("\\liminf", args);

            // Binomial
            case BINOM -> transpileBinom(args);

            // Norm / inner product
            case NORM -> transpileNorm(args);
            case INNER -> transpileInner(args);

            // Vector calculus
            case GRAD -> "\\nabla " + args.get(0).accept(this);
            case DIVERGENCE -> "\\nabla \\cdot " + args.get(0).accept(this);
            case CURL -> "\\nabla \\times " + args.get(0).accept(this);
            case LAPLACIAN -> "\\nabla^{2} " + args.get(0).accept(this);

            // Probability
            case PROB -> "P(" + args.get(0).accept(this) + ")";
            case EXPECT -> "\\mathbb{E}[" + args.get(0).accept(this) + "]";
            case VAR -> "\\text{Var}(" + args.get(0).accept(this) + ")";
            case COV -> "\\text{Cov}(" + args.get(0).accept(this) + ", " + args.get(1).accept(this) + ")";
            case GIVEN -> transpileBinarySymbol(args, "\\mid");

            // Linear algebra
            case DET -> transpileTrigFunction("\\det", args);
            case TRACE -> transpileTrigFunction("\\text{tr}", args);
            case DIM -> transpileTrigFunction("\\dim", args);
            case RANK -> transpileTrigFunction("\\text{rank}", args);
            case KER -> transpileTrigFunction("\\ker", args);
            case TRANSPOSE -> args.get(0).accept(this) + "^{T}";
            case INVERSE -> args.get(0).accept(this) + "^{-1}";

            // Visual decorations
            case BOXED -> transpileAccent("\\boxed", args);
            case CANCEL -> transpileAccent("\\cancel", args);
            case UNDERBRACE -> transpileUnderbrace(args);
            case OVERBRACE -> transpileOverbrace(args);

            default -> "UNKNOWN_FUNCTION(" + function + ")";
        };
    }

    @Override
    public String visitGroupingExpr(GroupingExpr expr) {
        return "(" + expr.getExpression().accept(this) + ")";
    }

    @Override
    public String visitTextExpr(TextExpr expr) {
        return StringUtils.escapeLaTeX(expr.getText());
    }

    @Override
    public String visitInlineMathExpr(InlineMathExpr expr) {
        // Transpile the inner expression and wrap in $ delimiters
        String latex = expr.getExpression().accept(this);
        return "$" + latex + "$";
    }

    @Override
    public String visitDisplayMathExpr(DisplayMathExpr expr) {
        // Transpile the inner expression and wrap in $$ delimiters
        String latex = expr.getExpression().accept(this);
        return "$$" + latex + "$$";
    }

    // Helper methods for specific function transpilation

    private String transpilePow(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return args.get(0).accept(this) + "^{" + args.get(1).accept(this) + "}";
    }

    private String transpileAbs(List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        return "|" + args.get(0).accept(this) + "|";
    }

    private String transpileCeil(List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        return "\\lceil " + args.get(0).accept(this) + " \\rceil";
    }

    private String transpileFloor(List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        return "\\lfloor " + args.get(0).accept(this) + " \\rfloor";
    }

    private String transpileMod(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return args.get(0).accept(this) + " \\mod " + args.get(1).accept(this);
    }

    private String transpileGcd(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\gcd(" + args.get(0).accept(this) + ", " + args.get(1).accept(this) + ")";
    }

    private String transpileLcm(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\text{lcm}(" + args.get(0).accept(this) + ", " + args.get(1).accept(this) + ")";
    }

    private String transpileBinarySymbol(List<AstNode> args, String symbol) {
        if (args.size() != 2) return "ERROR";
        return args.get(0).accept(this) + " " + symbol + " " + args.get(1).accept(this);
    }

    private String transpileTrigFunction(String funcName, List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        return funcName + "(" + args.get(0).accept(this) + ")";
    }

    private String transpileLog(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\log_{" + args.get(1).accept(this) + "}(" + args.get(0).accept(this) + ")";
    }

    private String transpileLn(List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        return "\\ln(" + args.get(0).accept(this) + ")";
    }

    private String transpileExp(List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        return "e^{" + args.get(0).accept(this) + "}";
    }

    private String transpileSqrt(List<AstNode> args) {
        if (args.size() == 1) {
            return "\\sqrt{" + args.get(0).accept(this) + "}";
        } else if (args.size() == 2) {
            return "\\sqrt[" + args.get(0).accept(this) + "]{" + args.get(1).accept(this) + "}";
        }
        return "ERROR";
    }

    private String transpilePartial(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\frac{\\partial}{\\partial " + args.get(1).accept(this) + "} " + args.get(0).accept(this);
    }

    private String transpileLimit(List<AstNode> args) {
        if (args.size() != 3) return "ERROR";
        return "\\lim_{{" + args.get(1).accept(this) + " \\to " + args.get(2).accept(this) + "}} " + args.get(0).accept(this);
    }

    private String transpileDiff(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\frac{d}{d" + args.get(1).accept(this) + "} " + args.get(0).accept(this);
    }

    private String transpileIntegral(List<AstNode> args) {
        if (args.size() == 2) {
            return "\\int " + args.get(0).accept(this) + " \\, d" + args.get(1).accept(this);
        } else if (args.size() == 4) {
            return "\\int_{" + args.get(2).accept(this) + "}^{" + args.get(3).accept(this) + "} " +
                   args.get(0).accept(this) + " \\, d" + args.get(1).accept(this);
        }
        return "ERROR";
    }

    private String transpileSum(List<AstNode> args) {
        if (args.isEmpty()) return "ERROR";
        if (args.size() == 1) return "\\sum " + args.get(0).accept(this);
        if (args.size() == 3) return "\\sum_{" + args.get(1).accept(this) + "}^{" + args.get(2).accept(this) + "} " + args.get(0).accept(this);
        if (args.size() == 4) return "\\sum_{" + args.get(1).accept(this) + "=" + args.get(2).accept(this) + "}^{" + args.get(3).accept(this) + "} " + args.get(0).accept(this);
        return "ERROR";
    }

    private String transpileProd(List<AstNode> args) {
        if (args.isEmpty()) return "ERROR";
        if (args.size() == 1) return "\\prod " + args.get(0).accept(this);
        if (args.size() == 3) return "\\prod_{" + args.get(1).accept(this) + "}^{" + args.get(2).accept(this) + "} " + args.get(0).accept(this);
        if (args.size() == 4) return "\\prod_{" + args.get(1).accept(this) + "=" + args.get(2).accept(this) + "}^{" + args.get(3).accept(this) + "} " + args.get(0).accept(this);
        return "ERROR";
    }

    private String transpileVector(List<AstNode> args) {
        List<AstNode> elements = args;
        if (args.size() == 1 && args.get(0) instanceof CallExpr row && row.getFunction().getType() == TokenType.LBRACKET) {
            elements = row.getArguments();
        }

        String content = elements.stream()
                .map(arg -> arg.accept(this))
                .collect(Collectors.joining(" \\\\ "));
        return "\\begin{pmatrix} " + content + " \\end{pmatrix}";
    }

    private String transpileMatrix(List<AstNode> args) {
        List<AstNode> rows = args;
        if (args.size() == 1 && args.get(0) instanceof CallExpr outerRow && outerRow.getFunction().getType() == TokenType.LBRACKET) {
            rows = outerRow.getArguments();
        }

        StringBuilder sb = new StringBuilder("\\begin{pmatrix} ");
        for (int i = 0; i < rows.size(); i++) {
            AstNode arg = rows.get(i);
            if (arg instanceof CallExpr row && row.getFunction().getType() == TokenType.LBRACKET) {
                // row node: join elements with &
                sb.append(row.getArguments().stream()
                    .map(e -> e.accept(this))
                    .collect(Collectors.joining(" & ")));
            } else {
                sb.append(arg.accept(this));
            }
            if (i < rows.size() - 1) sb.append(" \\\\ ");
        }
        sb.append(" \\end{pmatrix}");
        return sb.toString();
    }

    private String transpileForall(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\forall " + args.get(0).accept(this) + " \\, " + args.get(1).accept(this);
    }

    private String transpileExists(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\exists " + args.get(0).accept(this) + " \\, " + args.get(1).accept(this);
    }

    private String transpileNot(List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        return "\\neg " + args.get(0).accept(this);
    }

    // Accents and decorations
    private String transpileAccent(String command, List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        return command + "{" + args.get(0).accept(this) + "}";
    }

    // Text in math mode
    private String transpileMathtext(List<AstNode> args) {
        if (args.size() != 1) return "ERROR";
        // The argument should be a string literal
        String text = args.get(0).accept(this);
        // Remove quotes if present using StringUtils
        text = StringUtils.removeQuotes(text);
        return "\\text{" + text + "}";
    }

    // Piecewise functions
    private String transpilePiecewise(List<AstNode> args) {
        // piecewise(expr1, cond1, expr2, cond2, ...)
        // Generates: \begin{cases} expr1 & cond1 \\ expr2 & cond2 \\ ... \end{cases}
        if (args.size() % 2 != 0) return "ERROR: piecewise requires even number of arguments";

        StringBuilder result = new StringBuilder("\\begin{cases}\n");
        for (int i = 0; i < args.size(); i += 2) {
            String expr = args.get(i).accept(this);
            String cond = args.get(i + 1).accept(this);
            result.append(expr).append(" & \\text{if } ").append(cond);
            if (i + 2 < args.size()) {
                result.append(" \\\\");
            }
            result.append("\n");
        }
        result.append("\\end{cases}");
        return result.toString();
    }

    // Cases environment
    private String transpileCases(List<AstNode> args) {
        // Similar to piecewise but more flexible
        // cases(expr1, cond1, expr2, cond2, ...)
        return transpilePiecewise(args);
    }

    // Aligned equations
    private String transpileAlign(List<AstNode> args) {
        // align(eq1, eq2, eq3, ...)
        // Generates: \begin{align*} eq1 \\ eq2 \\ eq3 \\ ... \end{align*}
        if (args.isEmpty()) return "ERROR: align requires at least one equation";

        StringBuilder result = new StringBuilder("\\begin{align*}\n");
        for (int i = 0; i < args.size(); i++) {
            result.append(args.get(i).accept(this));
            if (i < args.size() - 1) {
                result.append(" \\\\");
            }
            result.append("\n");
        }
        result.append("\\end{align*}");
        return result.toString();
    }

    private String transpileBinom(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\binom{" + args.get(0).accept(this) + "}{" + args.get(1).accept(this) + "}";
    }

    private String transpileNorm(List<AstNode> args) {
        if (args.size() == 1) return "\\|" + args.get(0).accept(this) + "\\|";
        if (args.size() == 2) return "\\|" + args.get(0).accept(this) + "\\|_{" + args.get(1).accept(this) + "}";
        return "ERROR";
    }

    private String transpileInner(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\langle " + args.get(0).accept(this) + ", " + args.get(1).accept(this) + " \\rangle";
    }

    private String transpileUnderbrace(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\underbrace{" + args.get(0).accept(this) + "}_{" + args.get(1).accept(this) + "}";
    }

    private String transpileOverbrace(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\overbrace{" + args.get(0).accept(this) + "}^{" + args.get(1).accept(this) + "}";
    }

    // Systems of equations
    private String transpileSystem(List<AstNode> args) {
        // system(eq1, eq2, eq3, ...)
        // Generates: \begin{cases} eq1 \\ eq2 \\ eq3 \\ ... \end{cases}
        if (args.isEmpty()) return "ERROR: system requires at least one equation";

        StringBuilder result = new StringBuilder("\\begin{cases}\n");
        for (int i = 0; i < args.size(); i++) {
            result.append(args.get(i).accept(this));
            if (i < args.size() - 1) {
                result.append(" \\\\");
            }
            result.append("\n");
        }
        result.append("\\end{cases}");
        return result.toString();
    }
}
