package com.euclid.ast;

import com.euclid.token.Token;

/**
 * Represents a binary expression (e.g., a + b, a * b).
 */
public class BinaryExpr implements AstNode {
    private final AstNode left;
    private final Token operator;
    private final AstNode right;

    public BinaryExpr(AstNode left, Token operator, AstNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public AstNode getLeft() {
        return left;
    }

    public Token getOperator() {
        return operator;
    }

    public AstNode getRight() {
        return right;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitBinaryExpr(this);
    }

    @Override
    public String toString() {
        return "Binary(" + left + " " + operator.getLexeme() + " " + right + ")";
    }
}
