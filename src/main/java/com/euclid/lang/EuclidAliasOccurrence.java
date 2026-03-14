package com.euclid.lang;

public record EuclidAliasOccurrence(
        String alias,
        String canonical,
        int line,
        int column) {
}
