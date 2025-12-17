package com.euclid.ast;

/**
 * Represents a display math expression (content within $$ delimiters).
 * The expression will be transpiled and wrapped in $$ for display math mode.
 */
public class DisplayMathExpr implements AstNode {
    private final AstNode expression;

    public DisplayMathExpr(AstNode expression) {
        this.expression = expression;
    }

    public AstNode getExpression() {
        return expression;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitDisplayMathExpr(this);
    }

    @Override
    public String toString() {
        return "DisplayMath(" + expression.toString() + ")";
    }
}
