package com.euclid.ast;

import com.euclid.token.Token;

/**
 * Represents a unary expression (e.g., -x, +x).
 */
public class UnaryExpr implements AstNode {
    private final Token operator;
    private final AstNode operand;

    public UnaryExpr(Token operator, AstNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public Token getOperator() {
        return operator;
    }

    public AstNode getOperand() {
        return operand;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitUnaryExpr(this);
    }

    @Override
    public String toString() {
        return "Unary(" + operator.getLexeme() + " " + operand + ")";
    }
}
