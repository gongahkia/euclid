package com.euclid.ast;

import java.util.List;

/**
 * Represents the root document node containing multiple expressions.
 */
public class DocumentNode implements AstNode {
    private final List<AstNode> nodes;

    public DocumentNode(List<AstNode> nodes) {
        this.nodes = nodes;
    }

    public List<AstNode> getNodes() {
        return nodes;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitDocumentNode(this);
    }

    @Override
    public String toString() {
        return "Document(" + nodes + ")";
    }
}
