package com.euclid.ast;

/**
 * Represents plain text (markdown that doesn't need transpilation).
 */
public class TextExpr implements AstNode {
    private final String text;

    public TextExpr(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitTextExpr(this);
    }

    @Override
    public String toString() {
        return "Text(\"" + text + "\")";
    }
}
