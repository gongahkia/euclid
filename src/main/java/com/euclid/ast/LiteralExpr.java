package com.euclid.ast;

/**
 * Represents a literal value (number or constant like PI, E).
 */
public class LiteralExpr implements AstNode {
    private final Object value;

    public LiteralExpr(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }

    @Override
    public String toString() {
        return "Literal(" + value + ")";
    }
}
