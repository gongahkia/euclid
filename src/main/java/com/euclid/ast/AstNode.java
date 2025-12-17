package com.euclid.ast;

/**
 * Base interface for all AST nodes.
 * Uses the Visitor pattern for traversal and transpilation.
 */
public interface AstNode {
    /**
     * Accepts a visitor to process this node.
     *
     * @param visitor The visitor
     * @param <R>     The return type
     * @return The result of the visit
     */
    <R> R accept(AstVisitor<R> visitor);
}
