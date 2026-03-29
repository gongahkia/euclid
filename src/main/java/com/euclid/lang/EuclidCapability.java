package com.euclid.lang;

import com.euclid.token.TokenType;
import java.util.List;

public record EuclidCapability(
        String name,
        String preferredSpelling,
        EuclidCapabilityKind kind,
        EuclidFixity fixity,
        EuclidArity arity,
        TokenType tokenType,
        EuclidAliasPolicy aliasPolicy,
        List<String> aliases,
        EuclidSignature signature) {
}
