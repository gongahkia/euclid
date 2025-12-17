package com.euclid.ast;

import com.euclid.token.Token;
import java.util.List;

/**
 * Represents a function call expression (e.g., pow(x, y), sin(x)).
 */
public class CallExpr implements AstNode {
    private final Token function;
    private final List<AstNode> arguments;

    public CallExpr(Token function, List<AstNode> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public Token getFunction() {
        return function;
    }

    public List<AstNode> getArguments() {
        return arguments;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitCallExpr(this);
    }

    @Override
    public String toString() {
        return "Call(" + function.getLexeme() + ", " + arguments + ")";
    }
}
