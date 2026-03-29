package com.euclid;

import com.euclid.exception.Diagnostic;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TranspilerCliModeTest {

    @Test
    public void checkFileSurfacesWarningsWithoutProducingErrors() throws Exception {
        Path input = Files.createTempFile("euclid-check", ".ed");
        Files.writeString(input, "INF");

        TranspileResult result = Transpiler.checkFile(input.toString(), false);

        assertFalse(result.hasErrors());
        assertTrue(result.diagnostics().stream().anyMatch(d ->
                d.getSeverity() == Diagnostic.Severity.WARNING
                        && "canonical.rewrite".equals(d.getCode())));
    }

    @Test
    public void canonicalizeFileRewritesCompatibilityAliases() throws Exception {
        Path input = Files.createTempFile("euclid-canonicalize", ".ed");
        Path output = Files.createTempFile("euclid-canonicalize", ".out");
        Files.writeString(input, "INF + choose(n, k) + proper_subset(A, B)");

        Transpiler.canonicalizeFile(input.toString(), output.toString());

        assertEquals("INFINITY + binom(n, k) + subset(A, B)", Files.readString(output));
    }

    @Test
    public void checkModeRejectsOutputPaths() throws Exception {
        Path input = Files.createTempFile("euclid-check-usage", ".ed");
        Path output = Files.createTempFile("euclid-check-usage", ".md");
        Files.writeString(input, "x = y");

        int exitCode = runCliQuietly("--check", input.toString(), output.toString());

        assertEquals(1, exitCode);
    }

    @Test
    public void removedMixedModeFlagIsRejected() throws Exception {
        Path input = Files.createTempFile("euclid-canonicalize-usage", ".ed");
        Files.writeString(input, "sum(i, i, 1, n)");

        CliInvocationResult result = runCli("--mixed", input.toString());

        assertEquals(1, result.exitCode());
        assertTrue(result.stderr().contains("Unknown option"));
    }

    @Test
    public void checkModeCanEmitMachineReadableJson() throws Exception {
        Path input = Files.createTempFile("euclid-check-json", ".ed");
        Files.writeString(input, "The limit is $INF$.");

        CliInvocationResult result = runCli("--check", "--json", input.toString());

        assertEquals(0, result.exitCode());
        assertTrue(result.stdout().contains("\"ok\":true"));
        assertTrue(result.stdout().contains("\"mode\":\"document\""));
        assertTrue(result.stdout().contains("\"code\":\"canonical.rewrite\""));
    }

    @Test
    public void manifestModeCanEmitMachineReadableJson() {
        CliInvocationResult result = runCli("--manifest", "--json");

        assertEquals(0, result.exitCode());
        assertTrue(result.stdout().contains("\"capabilities\""));
        assertTrue(result.stdout().contains("\"name\":\"INFINITY\""));
    }

    @Test
    public void strictAliasesMakeCheckModeFail() throws Exception {
        Path input = Files.createTempFile("euclid-check-strict-alias", ".ed");
        Files.writeString(input, "$INF$");

        CliInvocationResult result = runCli("--check", "--json", "--strict-aliases", input.toString());

        assertEquals(1, result.exitCode());
        assertTrue(result.stdout().contains("\"code\":\"alias.noncanonical\""));
        assertTrue(result.stdout().contains("\"severity\":\"ERROR\""));
        assertTrue(result.stdout().contains("\"mode\":\"document\""));
    }

    private static CliInvocationResult runCli(String... args) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outBuffer));
            System.setErr(new PrintStream(errBuffer));
            return new CliInvocationResult(
                    Transpiler.runCli(args),
                    outBuffer.toString(),
                    errBuffer.toString());
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    private static int runCliQuietly(String... args) {
        return runCli(args).exitCode();
    }

    private record CliInvocationResult(int exitCode, String stdout, String stderr) {
    }
}
