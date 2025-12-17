package com.euclid;

import com.euclid.exception.EuclidException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Performance benchmarks for the Euclid transpiler.
 * These tests measure transpilation speed and help identify bottlenecks.
 */
public class PerformanceBenchmark {

    private static final int WARMUP_ITERATIONS = 100;
    private static final int BENCHMARK_ITERATIONS = 1000;

    /**
     * Runs a benchmark and returns average time in nanoseconds.
     */
    private BenchmarkResult benchmark(String name, String euclidCode, int iterations) {
        List<Long> times = new ArrayList<>();

        // Warmup to let JIT compile
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            try {
                Transpiler.transpile(euclidCode);
            } catch (EuclidException e) {
                throw new RuntimeException("Benchmark failed during warmup: " + e.getMessage());
            }
        }

        // Actual benchmark
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            try {
                Transpiler.transpile(euclidCode);
            } catch (EuclidException e) {
                throw new RuntimeException("Benchmark failed: " + e.getMessage());
            }
            long end = System.nanoTime();
            times.add(end - start);
        }

        // Calculate statistics
        long sum = times.stream().mapToLong(Long::longValue).sum();
        double avg = (double) sum / iterations;
        long min = times.stream().mapToLong(Long::longValue).min().orElse(0);
        long max = times.stream().mapToLong(Long::longValue).max().orElse(0);

        // Calculate median
        times.sort(Long::compareTo);
        long median = times.get(times.size() / 2);

        return new BenchmarkResult(name, euclidCode, iterations, avg, min, max, median);
    }

    /**
     * Prints benchmark results in a formatted table.
     */
    private void printResults(List<BenchmarkResult> results) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       EUCLID PERFORMANCE BENCHMARKS                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.printf("%-30s %10s %10s %10s %10s%n",
                "Benchmark", "Avg (μs)", "Min (μs)", "Max (μs)", "Median (μs)");
        System.out.println("─".repeat(80));

        for (BenchmarkResult result : results) {
            System.out.printf("%-30s %10.2f %10.2f %10.2f %10.2f%n",
                    result.name,
                    result.avgNanos / 1000.0,
                    result.minNanos / 1000.0,
                    result.maxNanos / 1000.0,
                    result.medianNanos / 1000.0);
        }

        System.out.println("─".repeat(80));
        System.out.println();

        // Calculate throughput
        System.out.println("Throughput Analysis:");
        for (BenchmarkResult result : results) {
            double throughputPerSecond = 1_000_000_000.0 / result.avgNanos;
            System.out.printf("  %s: %.0f transpilations/second%n", result.name, throughputPerSecond);
        }
        System.out.println();
    }

    @Test
    public void runAllBenchmarks() {
        List<BenchmarkResult> results = new ArrayList<>();

        // Simple expressions
        results.add(benchmark("Simple constant", "PI", BENCHMARK_ITERATIONS));
        results.add(benchmark("Simple arithmetic", "2 + 2", BENCHMARK_ITERATIONS));
        results.add(benchmark("Simple power", "pow(x, 2)", BENCHMARK_ITERATIONS));

        // Medium complexity
        results.add(benchmark("Trig function", "sin(x) + cos(y)", BENCHMARK_ITERATIONS));
        results.add(benchmark("Fraction", "(x + 1) \\\\ (y - 1)", BENCHMARK_ITERATIONS));
        results.add(benchmark("Square root", "sqrt(2, 16)", BENCHMARK_ITERATIONS));

        // High complexity
        results.add(benchmark("Integral", "integral(sin(x), x, 0, PI)", BENCHMARK_ITERATIONS / 2));
        results.add(benchmark("Summation", "sum(pow(i, 2), i, 1, n)", BENCHMARK_ITERATIONS / 2));
        results.add(benchmark("Limit", "limit(sin(x) \\\\ x, x, 0)", BENCHMARK_ITERATIONS / 2));

        // Very complex
        String complexExpr = "integral(pow(x, 2) + sin(x), x, 0, PI) + sum(pow(i, 2), i, 1, n)";
        results.add(benchmark("Complex expression", complexExpr, BENCHMARK_ITERATIONS / 4));

        // Large expression (stress test)
        StringBuilder largeExpr = new StringBuilder("1");
        for (int i = 0; i < 50; i++) {
            largeExpr.append(" + ").append(i);
        }
        results.add(benchmark("Large chain (50 terms)", largeExpr.toString(), BENCHMARK_ITERATIONS / 4));

        // Nested expressions
        String nested = "pow(pow(pow(x, 2), 2), 2)";
        results.add(benchmark("Deeply nested", nested, BENCHMARK_ITERATIONS / 2));

        printResults(results);

        // Perform basic validation
        double avgSimple = results.get(0).avgNanos;
        double avgComplex = results.get(9).avgNanos;

        System.out.println("Performance Analysis:");
        System.out.printf("  Simple expressions: ~%.2f μs average%n", avgSimple / 1000.0);
        System.out.printf("  Complex expressions: ~%.2f μs average%n", avgComplex / 1000.0);
        System.out.printf("  Complexity overhead: %.2fx%n", avgComplex / avgSimple);
        System.out.println();

        if (avgSimple / 1000.0 > 100) {
            System.out.println("⚠ WARNING: Simple expressions taking > 100μs - consider optimization");
        } else {
            System.out.println("✓ Performance is acceptable for simple expressions");
        }
    }

    /**
     * Benchmark result container.
     */
    private static class BenchmarkResult {
        final String name;
        final String code;
        final int iterations;
        final double avgNanos;
        final long minNanos;
        final long maxNanos;
        final long medianNanos;

        BenchmarkResult(String name, String code, int iterations, double avgNanos,
                        long minNanos, long maxNanos, long medianNanos) {
            this.name = name;
            this.code = code;
            this.iterations = iterations;
            this.avgNanos = avgNanos;
            this.minNanos = minNanos;
            this.maxNanos = maxNanos;
            this.medianNanos = medianNanos;
        }
    }
}
