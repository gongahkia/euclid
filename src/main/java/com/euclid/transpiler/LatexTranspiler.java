package com.euclid.transpiler;

import com.euclid.ast.*;
import com.euclid.token.TokenType;

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
            case POWER -> left + "^{" + right + "}";
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
            case FORALL -> transpileForall(args);
            case EXISTS -> transpileExists(args);

            default -> "UNKNOWN_FUNCTION(" + function + ")";
        };
    }

    @Override
    public String visitGroupingExpr(GroupingExpr expr) {
        return "(" + expr.getExpression().accept(this) + ")";
    }

    @Override
    public String visitTextExpr(TextExpr expr) {
        return expr.getText();
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
        // Simplified version - full implementation would parse "from i=1 to n" syntax
        if (args.isEmpty()) return "ERROR";
        return "\\sum " + args.get(0).accept(this);
    }

    private String transpileProd(List<AstNode> args) {
        // Simplified version - full implementation would parse "from i=1 to n" syntax
        if (args.isEmpty()) return "ERROR";
        return "\\prod " + args.get(0).accept(this);
    }

    private String transpileVector(List<AstNode> args) {
        String elements = args.stream()
                .map(arg -> arg.accept(this))
                .collect(Collectors.joining(" \\\\ "));
        return "\\begin{pmatrix} " + elements + " \\end{pmatrix}";
    }

    private String transpileMatrix(List<AstNode> args) {
        // Simplified version - assumes each arg is a row
        String rows = args.stream()
                .map(arg -> arg.accept(this))
                .collect(Collectors.joining(" \\\\ "));
        return "\\begin{matrix} " + rows + " \\end{matrix}";
    }

    private String transpileForall(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\forall " + args.get(0).accept(this) + " \\, " + args.get(1).accept(this);
    }

    private String transpileExists(List<AstNode> args) {
        if (args.size() != 2) return "ERROR";
        return "\\exists " + args.get(0).accept(this) + " \\, " + args.get(1).accept(this);
    }
}
