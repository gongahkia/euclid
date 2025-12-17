package com.euclid.ast;

/**
 * Represents an inline math expression (content within $ delimiters).
 * The expression will be transpiled and wrapped in $ for inline math mode.
 */
public class InlineMathExpr implements AstNode {
    private final AstNode expression;

    public InlineMathExpr(AstNode expression) {
        this.expression = expression;
    }

    public AstNode getExpression() {
        return expression;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitInlineMathExpr(this);
    }

    @Override
    public String toString() {
        return "InlineMath(" + expression.toString() + ")";
    }
}
