package com.euclid.ast;

/**
 * Represents an identifier (variable name like x, y, theta).
 */
public class IdentifierExpr implements AstNode {
    private final String name;

    public IdentifierExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitIdentifierExpr(this);
    }

    @Override
    public String toString() {
        return "Identifier(" + name + ")";
    }
}
