package com.euclid.lang;

import java.util.List;

public record EuclidArity(
        String label,
        int min,
        int max,
        List<Integer> exactAccepted,
        boolean evenOnly) {

    public static EuclidArity exact(int count) {
        return new EuclidArity(String.valueOf(count), count, count, List.of(count), false);
    }

    public static EuclidArity oneOf(String label, int... counts) {
        java.util.List<Integer> accepted = new java.util.ArrayList<>();
        for (int count : counts) {
            accepted.add(count);
        }
        int min = accepted.stream().mapToInt(Integer::intValue).min().orElse(0);
        int max = accepted.stream().mapToInt(Integer::intValue).max().orElse(0);
        return new EuclidArity(label, min, max, List.copyOf(accepted), false);
    }

    public static EuclidArity range(String label, int min, int max) {
        return new EuclidArity(label, min, max, List.of(), false);
    }

    public static EuclidArity evenAtLeast(int min) {
        return new EuclidArity("an even number and at least " + min, min, Integer.MAX_VALUE, List.of(), true);
    }

    public boolean accepts(int count) {
        if (!exactAccepted.isEmpty()) {
            return exactAccepted.contains(count);
        }
        if (count < min || count > max) {
            return false;
        }
        return !evenOnly || count % 2 == 0;
    }
}
