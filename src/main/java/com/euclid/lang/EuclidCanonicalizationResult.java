package com.euclid.lang;

import java.util.List;

public record EuclidCanonicalizationResult(
        String canonicalSource,
        List<EuclidAliasOccurrence> aliasOccurrences) {
}
