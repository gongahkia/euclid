package com.euclid.ast;

/**
 * Visitor interface for traversing and processing AST nodes.
 *
 * @param <R> The return type of visit methods
 */
public interface AstVisitor<R> {
    R visitLiteralExpr(LiteralExpr expr);
    R visitIdentifierExpr(IdentifierExpr expr);
    R visitBinaryExpr(BinaryExpr expr);
    R visitUnaryExpr(UnaryExpr expr);
    R visitCallExpr(CallExpr expr);
    R visitGroupingExpr(GroupingExpr expr);
    R visitTextExpr(TextExpr expr);
    R visitDocumentNode(DocumentNode node);
}
