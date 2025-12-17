package com.euclid.ast;

/**
 * Represents a grouped expression (e.g., (a + b)).
 */
public class GroupingExpr implements AstNode {
    private final AstNode expression;

    public GroupingExpr(AstNode expression) {
        this.expression = expression;
    }

    public AstNode getExpression() {
        return expression;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitGroupingExpr(this);
    }

    @Override
    public String toString() {
        return "Grouping(" + expression + ")";
    }
}
