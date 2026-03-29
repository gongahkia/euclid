package com.euclid.transpiler;

import com.euclid.ast.AstNode;
import com.euclid.ast.AstVisitor;
import com.euclid.ast.BinaryExpr;
import com.euclid.ast.CallExpr;
import com.euclid.ast.DisplayMathExpr;
import com.euclid.ast.DocumentNode;
import com.euclid.ast.GroupingExpr;
import com.euclid.ast.IdentifierExpr;
import com.euclid.ast.InlineMathExpr;
import com.euclid.ast.LiteralExpr;
import com.euclid.ast.TextExpr;
import com.euclid.ast.UnaryExpr;
import com.euclid.token.TokenType;
import com.euclid.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Transpiler that converts Euclid AST to LaTeX/MathJax.
 */
public class LatexTranspiler implements AstVisitor<String> {
    private static final int PREC_EQUALITY = 10;
    private static final int PREC_OR = 20;
    private static final int PREC_AND = 30;
    private static final int PREC_ADDITIVE = 40;
    private static final int PREC_MULTIPLICATIVE = 50;
    private static final int PREC_POWER = 60;
    private static final int PREC_PREFIX = 70;
    private static final int PREC_POSTFIX = 80;
    private static final int PREC_PRIMARY = 90;

    private final MathMode mathMode;

    public LatexTranspiler() {
        this(MathMode.NONE);
    }

    public LatexTranspiler(MathMode mathMode) {
        this.mathMode = mathMode;
    }

    @Override
    public String visitDocumentNode(DocumentNode node) {
        return node.getNodes().stream()
                .map(this::renderTopLevel)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String visitLiteralExpr(LiteralExpr expr) {
        return render(expr).latex();
    }

    @Override
    public String visitIdentifierExpr(IdentifierExpr expr) {
        return render(expr).latex();
    }

    @Override
    public String visitBinaryExpr(BinaryExpr expr) {
        return render(expr).latex();
    }

    @Override
    public String visitUnaryExpr(UnaryExpr expr) {
        return render(expr).latex();
    }

    @Override
    public String visitCallExpr(CallExpr expr) {
        return render(expr).latex();
    }

    @Override
    public String visitGroupingExpr(GroupingExpr expr) {
        return render(expr).latex();
    }

    @Override
    public String visitTextExpr(TextExpr expr) {
        return render(expr).latex();
    }

    @Override
    public String visitInlineMathExpr(InlineMathExpr expr) {
        return render(expr).latex();
    }

    @Override
    public String visitDisplayMathExpr(DisplayMathExpr expr) {
        return render(expr).latex();
    }

    private String renderTopLevel(AstNode node) {
        String latex = render(node).latex();
        if (node instanceof TextExpr) {
            return latex;
        }
        return wrapMathMode(latex);
    }

    private String wrapMathMode(String latex) {
        return switch (mathMode) {
            case INLINE -> "$" + latex + "$";
            case DISPLAY -> "$$" + latex + "$$";
            case NONE -> latex;
        };
    }

    private Rendered render(AstNode node) {
        if (node instanceof LiteralExpr expr) {
            return renderLiteral(expr);
        }
        if (node instanceof IdentifierExpr expr) {
            return new Rendered(expr.getName(), PREC_PRIMARY, true);
        }
        if (node instanceof BinaryExpr expr) {
            return renderBinary(expr);
        }
        if (node instanceof UnaryExpr expr) {
            return renderUnary(expr);
        }
        if (node instanceof CallExpr expr) {
            return renderCall(expr);
        }
        if (node instanceof GroupingExpr expr) {
            return new Rendered("(" + render(expr.getExpression()).latex() + ")", PREC_PRIMARY, true);
        }
        if (node instanceof TextExpr expr) {
            return new Rendered(StringUtils.escapeLaTeX(expr.getText()), PREC_PRIMARY, true);
        }
        if (node instanceof InlineMathExpr expr) {
            return new Rendered("$" + render(expr.getExpression()).latex() + "$", PREC_PRIMARY, true);
        }
        if (node instanceof DisplayMathExpr expr) {
            return new Rendered("$$" + render(expr.getExpression()).latex() + "$$", PREC_PRIMARY, true);
        }
        if (node instanceof DocumentNode expr) {
            return new Rendered(visitDocumentNode(expr), PREC_PRIMARY, false);
        }
        throw new IllegalStateException("Unsupported AST node: " + node.getClass().getName());
    }

    private Rendered renderLiteral(LiteralExpr expr) {
        Object value = expr.getValue();
        if (value instanceof TokenType type) {
            return new Rendered(renderTokenLiteral(type), PREC_PRIMARY, true);
        }
        if (value instanceof String text) {
            return new Rendered(StringUtils.escapeLaTeX(text), PREC_PRIMARY, true);
        }
        return new Rendered(value.toString(), PREC_PRIMARY, true);
    }

    private String renderTokenLiteral(TokenType type) {
        return switch (type) {
            case PI -> "\\pi";
            case E -> "e";
            case I -> "i";
            case GAMMA -> "\\gamma";
            case PHI -> "\\phi";
            case INFINITY -> "\\infty";
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
            case EMPTYSET -> "\\emptyset";
            case AND -> "\\land";
            case OR -> "\\lor";
            case NOT -> "\\neg";
            case NATURALS -> "\\mathbb{N}";
            case INTEGERS -> "\\mathbb{Z}";
            case RATIONALS -> "\\mathbb{Q}";
            case REALS -> "\\mathbb{R}";
            case COMPLEXES -> "\\mathbb{C}";
            case RIGHTARROW -> "\\rightarrow";
            case LEFTARROW -> "\\leftarrow";
            case LEFTRIGHTARROW -> "\\leftrightarrow";
            case MAPSTO -> "\\mapsto";
            case UPARROW -> "\\uparrow";
            case DOWNARROW -> "\\downarrow";
            case DARROW_RIGHT -> "\\Rightarrow";
            case DARROW_LEFT -> "\\Leftarrow";
            case DARROW_LEFTRIGHT -> "\\Leftrightarrow";
            case LDOTS -> "\\ldots";
            case CDOTS -> "\\cdots";
            case VDOTS -> "\\vdots";
            case DDOTS -> "\\ddots";
            case THEREFORE -> "\\therefore";
            case BECAUSE -> "\\because";
            case QED -> "\\blacksquare";
            case PERP -> "\\perp";
            case PARALLEL -> "\\parallel";
            case ANGLE -> "\\angle";
            case TRIANGLE -> "\\triangle";
            case CONG -> "\\cong";
            case SIM -> "\\sim";
            case PROPTO -> "\\propto";
            case HBAR -> "\\hbar";
            case NABLA -> "\\nabla";
            case ELL -> "\\ell";
            default -> type.name();
        };
    }

    private Rendered renderBinary(BinaryExpr expr) {
        TokenType operator = expr.getOperator().getType();
        return switch (operator) {
            case PLUS -> renderInfix(expr, " + ", PREC_ADDITIVE, Associativity.LEFT);
            case MINUS -> renderInfix(expr, " - ", PREC_ADDITIVE, Associativity.LEFT);
            case EQUALS -> renderInfix(expr, " = ", PREC_EQUALITY, Associativity.LEFT);
            case AND -> renderInfix(expr, " \\land ", PREC_AND, Associativity.LEFT);
            case OR -> renderInfix(expr, " \\lor ", PREC_OR, Associativity.LEFT);
            case DIVIDE -> renderInfix(expr, " / ", PREC_MULTIPLICATIVE, Associativity.LEFT);
            case MODULO -> renderInfix(expr, " \\bmod ", PREC_MULTIPLICATIVE, Associativity.LEFT);
            case DOT -> renderInfix(expr, " \\cdot ", PREC_MULTIPLICATIVE, Associativity.LEFT);
            case BACKSLASH_BACKSLASH -> renderFraction(expr);
            case POWER -> renderPower(expr);
            case UNDERSCORE -> renderSubscript(expr);
            case MULTIPLY, IMPLICIT_MULTIPLY -> renderMultiply(expr, operator == TokenType.IMPLICIT_MULTIPLY);
            default -> renderInfix(expr, " " + expr.getOperator().getLexeme() + " ", PREC_ADDITIVE, Associativity.LEFT);
        };
    }

    private Rendered renderUnary(UnaryExpr expr) {
        TokenType operator = expr.getOperator().getType();
        Rendered operand = render(expr.getOperand());
        String latex = switch (operator) {
            case MINUS -> "-" + parenthesizeIfNeeded(operand, PREC_PREFIX, Associativity.RIGHT);
            case NOT -> "\\neg " + parenthesizeIfNeeded(operand, PREC_PREFIX, Associativity.RIGHT);
            case BANG -> parenthesizeIfNeeded(operand, PREC_POSTFIX, Associativity.LEFT) + "!";
            default -> expr.getOperator().getLexeme() + parenthesizeIfNeeded(operand, PREC_PREFIX, Associativity.RIGHT);
        };
        int precedence = operator == TokenType.BANG ? PREC_POSTFIX : PREC_PREFIX;
        return new Rendered(latex, precedence, false);
    }

    private Rendered renderCall(CallExpr expr) {
        TokenType function = expr.getFunction().getType();
        List<AstNode> args = expr.getArguments();

        return switch (function) {
            case LBRACKET -> new Rendered(
                    args.stream().map(arg -> render(arg).latex()).collect(Collectors.joining(", ")),
                    PREC_PRIMARY,
                    false);
            case POW -> renderFunctionStylePower(args);
            case ABS -> unaryFunction(args, "abs", value -> "|" + value + "|");
            case CEIL -> unaryFunction(args, "ceil", value -> "\\lceil " + value + " \\rceil");
            case FLOOR -> unaryFunction(args, "floor", value -> "\\lfloor " + value + " \\rfloor");
            case MOD -> binarySymbol(args, "mod", " \\bmod ");
            case GCD -> naryNamedFunction("\\gcd", "gcd", args);
            case LCM -> naryNamedFunction("\\operatorname{lcm}", "lcm", args);
            case LT -> binarySymbol(args, "lt", " < ");
            case GT -> binarySymbol(args, "gt", " > ");
            case LEQ -> binarySymbol(args, "leq", " \\leq ");
            case GEQ -> binarySymbol(args, "geq", " \\geq ");
            case APPROX -> binarySymbol(args, "approx", " \\approx ");
            case NEQ -> binarySymbol(args, "neq", " \\neq ");
            case EQUIV -> binarySymbol(args, "equiv", " \\equiv ");
            case PM -> binarySymbol(args, "pm", " \\pm ");
            case TIMES -> binarySymbol(args, "times", " \\times ");
            case DIV -> binarySymbol(args, "div", " \\div ");
            case CDOT -> binarySymbol(args, "cdot", " \\cdot ");
            case AST -> binarySymbol(args, "ast", " \\ast ");
            case STAR -> binarySymbol(args, "star", " \\star ");
            case CIRC -> binarySymbol(args, "circ", " \\circ ");
            case BULLET -> binarySymbol(args, "bullet", " \\bullet ");
            case CAP -> binarySymbol(args, "cap", " \\cap ");
            case CUP -> binarySymbol(args, "cup", " \\cup ");
            case SIN -> namedFunction("\\sin", args);
            case COS -> namedFunction("\\cos", args);
            case TAN -> namedFunction("\\tan", args);
            case CSC -> namedFunction("\\csc", args);
            case SEC -> namedFunction("\\sec", args);
            case COT -> namedFunction("\\cot", args);
            case SINH -> namedFunction("\\sinh", args);
            case COSH -> namedFunction("\\cosh", args);
            case TANH -> namedFunction("\\tanh", args);
            case CSCH -> namedFunction("\\operatorname{csch}", args);
            case SECH -> namedFunction("\\operatorname{sech}", args);
            case COTH -> namedFunction("\\coth", args);
            case LOG -> renderLog(args);
            case LN -> namedFunction("\\ln", args);
            case EXP -> unaryFunction(args, "exp", value -> "e^{" + value + "}");
            case SQRT -> renderSqrt(args);
            case PARTIAL -> renderPartial(args);
            case LIMIT -> renderLimit(args);
            case DIFF -> renderDiff(args);
            case INTEGRAL -> renderIntegral(args);
            case SUM -> renderAggregate("\\sum", "sum", args);
            case PROD -> renderAggregate("\\prod", "prod", args);
            case VECTOR -> renderVector(args);
            case MATRIX -> renderMatrix(args);
            case SUBSET -> binarySymbol(args, "subset", " \\subset ");
            case SUPSET -> binarySymbol(args, "supset", " \\supset ");
            case SUBSETEQ -> binarySymbol(args, "subseteq", " \\subseteq ");
            case SUPSETEQ -> binarySymbol(args, "supseteq", " \\supseteq ");
            case UNION -> binarySymbol(args, "union", " \\cup ");
            case INTERSECTION -> binarySymbol(args, "intersection", " \\cap ");
            case SET_DIFF -> binarySymbol(args, "set_diff", " \\setminus ");
            case ELEMENT_OF -> binarySymbol(args, "element_of", " \\in ");
            case NOT_ELEMENT_OF -> binarySymbol(args, "not_element_of", " \\notin ");
            case IMPLIES -> binarySymbol(args, "implies", " \\implies ");
            case IFF -> binarySymbol(args, "iff", " \\iff ");
            case AND -> binarySymbol(args, "AND", " \\land ");
            case OR -> binarySymbol(args, "OR", " \\lor ");
            case HAT -> accent("\\hat", args);
            case TILDE -> accent("\\tilde", args);
            case BAR -> accent("\\bar", args);
            case VEC -> accent("\\vec", args);
            case DOT -> renderDotFunction(args);
            case DDOT -> accent("\\ddot", args);
            case OVERLINE -> accent("\\overline", args);
            case UNDERLINE -> accent("\\underline", args);
            case MATHTEXT -> renderMathText(args);
            case PIECEWISE, CASES -> renderPiecewise(args);
            case ALIGN -> renderAlignment(args);
            case SYSTEM -> renderSystem(args);
            case ARCSIN -> namedFunction("\\arcsin", args);
            case ARCCOS -> namedFunction("\\arccos", args);
            case ARCTAN -> namedFunction("\\arctan", args);
            case ARCCSC -> namedFunction("\\operatorname{arccsc}", args);
            case ARCSEC -> namedFunction("\\operatorname{arcsec}", args);
            case ARCCOT -> namedFunction("\\operatorname{arccot}", args);
            case MIN -> namedFunction("\\min", args);
            case MAX -> namedFunction("\\max", args);
            case SUP -> namedFunction("\\sup", args);
            case INF -> namedFunction("\\inf", args);
            case LIMSUP -> namedFunction("\\limsup", args);
            case LIMINF -> namedFunction("\\liminf", args);
            case BINOM -> renderBinom(args);
            case NORM -> renderNorm(args);
            case INNER -> renderInner(args);
            case FORALL -> renderQuantifier("\\forall", args);
            case EXISTS -> renderQuantifier("\\exists", args);
            case GRAD -> unaryFunction(args, "grad", value -> "\\nabla " + value);
            case DIVERGENCE -> unaryFunction(args, "divergence", value -> "\\nabla \\cdot " + value);
            case CURL -> unaryFunction(args, "curl", value -> "\\nabla \\times " + value);
            case LAPLACIAN -> unaryFunction(args, "laplacian", value -> "\\nabla^{2} " + value);
            case PROB -> unaryFunction(args, "prob", value -> "P(" + value + ")");
            case EXPECT -> unaryFunction(args, "expect", value -> "\\mathbb{E}[" + value + "]");
            case VAR -> unaryFunction(args, "var", value -> "\\operatorname{Var}(" + value + ")");
            case COV -> binaryFunction("\\operatorname{Cov}", "cov", args);
            case GIVEN -> binarySymbol(args, "given", " \\mid ");
            case DET -> namedFunction("\\det", args);
            case TRACE -> namedFunction("\\operatorname{tr}", args);
            case DIM -> namedFunction("\\dim", args);
            case RANK -> namedFunction("\\operatorname{rank}", args);
            case KER -> namedFunction("\\ker", args);
            case TRANSPOSE -> unaryFunction(args, "transpose", value -> value + "^{T}");
            case INVERSE -> unaryFunction(args, "inverse", value -> value + "^{-1}");
            case BOXED -> accent("\\boxed", args);
            case CANCEL -> accent("\\cancel", args);
            case UNDERBRACE -> renderBraceAnnotation("\\underbrace", "_", args);
            case OVERBRACE -> renderBraceAnnotation("\\overbrace", "^", args);
            case IDENTIFIER -> renderGenericCall(expr.getFunction().getLexeme(), args);
            default -> renderGenericCall(expr.getFunction().getLexeme(), args);
        };
    }

    private Rendered renderInfix(BinaryExpr expr, String operatorLatex, int precedence, Associativity associativity) {
        Rendered left = render(expr.getLeft());
        Rendered right = render(expr.getRight());
        String latex = parenthesizeIfNeeded(left, precedence, Associativity.LEFT)
                + operatorLatex
                + parenthesizeIfNeeded(right, precedence, associativity == Associativity.RIGHT
                        ? Associativity.RIGHT
                        : Associativity.LEFT_CHILD_OF_LEFT_ASSOC);
        if (expr.getOperator().getType() == TokenType.EQUALS) {
            String leftLatex = left.latex();
            String rightLatex = parenthesizeIfNeeded(right, precedence, Associativity.LEFT_CHILD_OF_LEFT_ASSOC);
            if (expr.getLeft() instanceof BinaryExpr binaryLeft && isLogicalBinary(binaryLeft.getOperator().getType())) {
                leftLatex = "(" + leftLatex + ")";
            }
            if (expr.getRight() instanceof BinaryExpr binaryRight && isLogicalBinary(binaryRight.getOperator().getType())) {
                rightLatex = "(" + right.latex() + ")";
            }
            latex = leftLatex + operatorLatex + rightLatex;
        }
        return new Rendered(latex, precedence, false);
    }

    private Rendered renderFraction(BinaryExpr expr) {
        String left = renderFractionOperand(expr.getLeft());
        String right = renderFractionOperand(expr.getRight());
        return new Rendered("\\frac{" + left + "}{" + right + "}", PREC_MULTIPLICATIVE, false);
    }

    private Rendered renderPower(BinaryExpr expr) {
        Rendered left = render(expr.getLeft());
        Rendered right = render(expr.getRight());
        String latex = parenthesizeIfNeeded(left, PREC_POWER, Associativity.RIGHT) + "^{" + right.latex() + "}";
        return new Rendered(latex, PREC_POWER, false);
    }

    private Rendered renderSubscript(BinaryExpr expr) {
        Rendered left = render(expr.getLeft());
        Rendered right = render(expr.getRight());
        String latex = parenthesizeIfNeeded(left, PREC_POWER, Associativity.RIGHT) + "_{" + right.latex() + "}";
        return new Rendered(latex, PREC_POWER, false);
    }

    private Rendered renderMultiply(BinaryExpr expr, boolean implicit) {
        Rendered left = render(expr.getLeft());
        Rendered right = render(expr.getRight());
        String leftLatex = parenthesizeIfNeeded(left, PREC_MULTIPLICATIVE, Associativity.LEFT);
        String rightLatex = parenthesizeIfNeeded(right, PREC_MULTIPLICATIVE, Associativity.LEFT_CHILD_OF_LEFT_ASSOC);
        String joiner = implicit ? "" : multiplicationJoiner(leftLatex, rightLatex);
        return new Rendered(leftLatex + joiner + rightLatex, PREC_MULTIPLICATIVE, false);
    }

    private String multiplicationJoiner(String left, String right) {
        if (startsWithDigit(left) && startsWithDigit(right)) {
            return " \\cdot ";
        }
        if (startsWithDigit(left) && startsWithDigitOrLetter(right)) {
            return "";
        }
        if (endsWithBracket(left) || startsWithBracket(right)) {
            return "";
        }
        if (left.startsWith("\\") || right.startsWith("\\")) {
            return "";
        }
        if (startsWithLetter(left) && startsWithLetter(right)) {
            return "";
        }
        return " \\cdot ";
    }

    private Rendered renderGenericCall(String functionName, List<AstNode> args) {
        String arguments = args.stream()
                .map(arg -> render(arg).latex())
                .collect(Collectors.joining(", "));
        return new Rendered(functionName + "(" + arguments + ")", PREC_PRIMARY, true);
    }

    private Rendered renderFunctionStylePower(List<AstNode> args) {
        ensureArity("pow", args, 2);
        return new Rendered(
                parenthesizeIfNeeded(render(args.get(0)), PREC_POWER, Associativity.RIGHT) + "^{" + render(args.get(1)).latex() + "}",
                PREC_POWER,
                false);
    }

    private Rendered unaryFunction(List<AstNode> args, String name, java.util.function.Function<String, String> formatter) {
        ensureArity(name, args, 1);
        return new Rendered(formatter.apply(render(args.get(0)).latex()), PREC_PRIMARY, true);
    }

    private Rendered namedFunction(String functionName, List<AstNode> args) {
        ensureArity(functionName, args, 1);
        return new Rendered(functionName + "(" + render(args.get(0)).latex() + ")", PREC_PRIMARY, true);
    }

    private Rendered binaryFunction(String functionName, String name, List<AstNode> args) {
        ensureArity(name, args, 2);
        return new Rendered(functionName + "(" + render(args.get(0)).latex() + ", " + render(args.get(1)).latex() + ")",
                PREC_PRIMARY,
                true);
    }

    private Rendered naryNamedFunction(String functionName, String name, List<AstNode> args) {
        if (args.isEmpty()) {
            throw invalidArguments(name, args.size(), "at least 1");
        }
        String arguments = args.stream()
                .map(arg -> render(arg).latex())
                .collect(Collectors.joining(", "));
        return new Rendered(functionName + "(" + arguments + ")", PREC_PRIMARY, true);
    }

    private Rendered binarySymbol(List<AstNode> args, String name, String symbol) {
        ensureArity(name, args, 2);
        return new Rendered(render(args.get(0)).latex() + symbol + render(args.get(1)).latex(), PREC_PRIMARY, false);
    }

    private Rendered renderLog(List<AstNode> args) {
        ensureArity("log", args, 2);
        return new Rendered("\\log_{" + render(args.get(1)).latex() + "}(" + render(args.get(0)).latex() + ")",
                PREC_PRIMARY,
                true);
    }

    private Rendered renderSqrt(List<AstNode> args) {
        if (args.size() == 1) {
            return new Rendered("\\sqrt{" + render(args.get(0)).latex() + "}", PREC_PRIMARY, true);
        }
        if (args.size() == 2) {
            return new Rendered("\\sqrt[" + render(args.get(0)).latex() + "]{" + render(args.get(1)).latex() + "}",
                    PREC_PRIMARY,
                    true);
        }
        throw invalidArguments("sqrt", args.size(), "1 or 2");
    }

    private Rendered renderPartial(List<AstNode> args) {
        ensureArity("partial", args, 2);
        return new Rendered("\\frac{\\partial}{\\partial " + render(args.get(1)).latex() + "} " + render(args.get(0)).latex(),
                PREC_PRIMARY,
                false);
    }

    private Rendered renderLimit(List<AstNode> args) {
        ensureArity("limit", args, 3);
        return new Rendered("\\lim_{" + render(args.get(1)).latex() + " \\to " + render(args.get(2)).latex() + "} "
                + render(args.get(0)).latex(), PREC_PRIMARY, false);
    }

    private Rendered renderDiff(List<AstNode> args) {
        ensureArity("diff", args, 2);
        return new Rendered("\\frac{d}{d" + render(args.get(1)).latex() + "} " + render(args.get(0)).latex(), PREC_PRIMARY, false);
    }

    private Rendered renderIntegral(List<AstNode> args) {
        if (args.size() == 2) {
            return new Rendered("\\int " + render(args.get(0)).latex() + " \\, d" + render(args.get(1)).latex(),
                    PREC_PRIMARY,
                    false);
        }
        if (args.size() == 4) {
            return new Rendered("\\int_{" + render(args.get(2)).latex() + "}^{" + render(args.get(3)).latex() + "} "
                    + render(args.get(0)).latex() + " \\, d" + render(args.get(1)).latex(), PREC_PRIMARY, false);
        }
        throw invalidArguments("integral", args.size(), "2 or 4");
    }

    private Rendered renderAggregate(String operator, String name, List<AstNode> args) {
        if (args.isEmpty()) {
            throw invalidArguments(name, args.size(), "1, 3, or 4");
        }
        if (args.size() == 1) {
            return new Rendered(operator + " " + render(args.get(0)).latex(), PREC_PRIMARY, false);
        }
        if (args.size() == 3) {
            return new Rendered(operator + "_{" + render(args.get(1)).latex() + "}^{" + render(args.get(2)).latex() + "} "
                    + render(args.get(0)).latex(), PREC_PRIMARY, false);
        }
        if (args.size() == 4) {
            return new Rendered(operator + "_{" + render(args.get(1)).latex() + "=" + render(args.get(2)).latex() + "}^{"
                    + render(args.get(3)).latex() + "} " + render(args.get(0)).latex(), PREC_PRIMARY, false);
        }
        throw invalidArguments(name, args.size(), "1, 3, or 4");
    }

    private Rendered renderVector(List<AstNode> args) {
        List<AstNode> elements = flattenSingleBracketGroup(args);
        String content = elements.stream().map(arg -> render(arg).latex()).collect(Collectors.joining(" \\\\ "));
        return new Rendered("\\begin{pmatrix} " + content + " \\end{pmatrix}", PREC_PRIMARY, false);
    }

    private Rendered renderMatrix(List<AstNode> args) {
        List<AstNode> rows = flattenSingleBracketGroup(args);
        StringBuilder builder = new StringBuilder("\\begin{pmatrix} ");
        for (int i = 0; i < rows.size(); i++) {
            AstNode row = rows.get(i);
            if (row instanceof CallExpr call && call.getFunction().getType() == TokenType.LBRACKET) {
                builder.append(call.getArguments().stream()
                        .map(arg -> render(arg).latex())
                        .collect(Collectors.joining(" & ")));
            } else {
                builder.append(render(row).latex());
            }
            if (i < rows.size() - 1) {
                builder.append(" \\\\ ");
            }
        }
        builder.append(" \\end{pmatrix}");
        return new Rendered(builder.toString(), PREC_PRIMARY, false);
    }

    private List<AstNode> flattenSingleBracketGroup(List<AstNode> args) {
        if (args.size() == 1 && args.get(0) instanceof CallExpr call && call.getFunction().getType() == TokenType.LBRACKET) {
            return call.getArguments();
        }
        return args;
    }

    private Rendered accent(String command, List<AstNode> args) {
        ensureArity(command, args, 1);
        return new Rendered(command + "{" + render(args.get(0)).latex() + "}", PREC_PRIMARY, true);
    }

    private Rendered renderDotFunction(List<AstNode> args) {
        if (args.size() == 1) {
            return new Rendered("\\dot{" + render(args.get(0)).latex() + "}", PREC_PRIMARY, true);
        }
        if (args.size() == 2) {
            return new Rendered(render(args.get(0)).latex() + " \\cdot " + render(args.get(1)).latex(), PREC_PRIMARY, false);
        }
        throw invalidArguments("dot", args.size(), "1 or 2");
    }

    private Rendered renderMathText(List<AstNode> args) {
        ensureArity("mathtext", args, 1);
        return new Rendered("\\text{" + render(args.get(0)).latex() + "}", PREC_PRIMARY, true);
    }

    private Rendered renderPiecewise(List<AstNode> args) {
        if (args.size() < 2 || args.size() % 2 != 0) {
            throw invalidArguments("piecewise", args.size(), "an even number and at least 2");
        }
        StringBuilder result = new StringBuilder("\\begin{cases}\n");
        for (int i = 0; i < args.size(); i += 2) {
            result.append(render(args.get(i)).latex())
                    .append(" & \\text{if } ")
                    .append(render(args.get(i + 1)).latex());
            if (i + 2 < args.size()) {
                result.append(" \\\\");
            }
            result.append('\n');
        }
        result.append("\\end{cases}");
        return new Rendered(result.toString(), PREC_PRIMARY, false);
    }

    private Rendered renderAlignment(List<AstNode> args) {
        if (args.isEmpty()) {
            throw invalidArguments("align", args.size(), "at least 1");
        }
        String body = args.stream().map(arg -> render(arg).latex()).collect(Collectors.joining(" \\\\\n"));
        return new Rendered("\\begin{align*}\n" + body + "\n\\end{align*}", PREC_PRIMARY, false);
    }

    private Rendered renderSystem(List<AstNode> args) {
        if (args.isEmpty()) {
            throw invalidArguments("system", args.size(), "at least 1");
        }
        String body = args.stream().map(arg -> render(arg).latex()).collect(Collectors.joining(" \\\\\n"));
        return new Rendered("\\begin{cases}\n" + body + "\n\\end{cases}", PREC_PRIMARY, false);
    }

    private Rendered renderBinom(List<AstNode> args) {
        ensureArity("binom", args, 2);
        return new Rendered("\\binom{" + render(args.get(0)).latex() + "}{" + render(args.get(1)).latex() + "}",
                PREC_PRIMARY,
                true);
    }

    private Rendered renderNorm(List<AstNode> args) {
        if (args.size() == 1) {
            return new Rendered("\\|" + render(args.get(0)).latex() + "\\|", PREC_PRIMARY, true);
        }
        if (args.size() == 2) {
            return new Rendered("\\|" + render(args.get(0)).latex() + "\\|_{" + render(args.get(1)).latex() + "}",
                    PREC_PRIMARY,
                    true);
        }
        throw invalidArguments("norm", args.size(), "1 or 2");
    }

    private Rendered renderInner(List<AstNode> args) {
        ensureArity("inner", args, 2);
        return new Rendered("\\langle " + render(args.get(0)).latex() + ", " + render(args.get(1)).latex() + " \\rangle",
                PREC_PRIMARY,
                true);
    }

    private Rendered renderQuantifier(String symbol, List<AstNode> args) {
        ensureArity(symbol, args, 2);
        return new Rendered(symbol + " " + render(args.get(0)).latex() + " \\, " + render(args.get(1)).latex(),
                PREC_PRIMARY,
                false);
    }

    private Rendered renderBraceAnnotation(String command, String direction, List<AstNode> args) {
        ensureArity(command, args, 2);
        String annotation = args.get(1) instanceof LiteralExpr literal && literal.getValue() instanceof String
                ? "\\text{" + render(literal).latex() + "}"
                : render(args.get(1)).latex();
        return new Rendered(command + "{" + render(args.get(0)).latex() + "}" + direction + "{" + annotation + "}",
                PREC_PRIMARY,
                true);
    }

    private String parenthesizeIfNeeded(Rendered child, int parentPrecedence, Associativity associativity) {
        if (needsParentheses(child, parentPrecedence, associativity)) {
            return "(" + child.latex() + ")";
        }
        return child.latex();
    }

    private boolean needsParentheses(Rendered child, int parentPrecedence, Associativity associativity) {
        if (child.precedence() < parentPrecedence) {
            return true;
        }
        if (child.precedence() > parentPrecedence) {
            return false;
        }
        return associativity == Associativity.RIGHT || associativity == Associativity.LEFT_CHILD_OF_LEFT_ASSOC;
    }

    private void ensureArity(String function, List<AstNode> args, int expected) {
        if (args.size() != expected) {
            throw invalidArguments(function, args.size(), String.valueOf(expected));
        }
    }

    private IllegalStateException invalidArguments(String function, int actual, String expected) {
        return new IllegalStateException(
                "Internal Euclid contract error: function '" + function + "' received "
                        + actual + " arguments; expected " + expected + ".");
    }

    private boolean startsWithDigit(String value) {
        return !value.isEmpty() && Character.isDigit(value.charAt(0));
    }

    private boolean startsWithLetter(String value) {
        return !value.isEmpty() && Character.isLetter(value.charAt(0));
    }

    private boolean startsWithDigitOrLetter(String value) {
        return !value.isEmpty() && Character.isLetterOrDigit(value.charAt(0));
    }

    private boolean startsWithBracket(String value) {
        return value.startsWith("(") || value.startsWith("[") || value.startsWith("{");
    }

    private boolean endsWithBracket(String value) {
        return value.endsWith(")") || value.endsWith("]") || value.endsWith("}");
    }

    private boolean isLogicalBinary(TokenType tokenType) {
        return tokenType == TokenType.AND || tokenType == TokenType.OR;
    }

    private String renderFractionOperand(AstNode node) {
        if (node instanceof GroupingExpr groupingExpr) {
            return render(groupingExpr.getExpression()).latex();
        }
        return render(node).latex();
    }

    private enum Associativity {
        LEFT,
        LEFT_CHILD_OF_LEFT_ASSOC,
        RIGHT
    }

    private record Rendered(String latex, int precedence, boolean simple) {
    }
}
