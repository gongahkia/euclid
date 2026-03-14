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

        TranspileResult result = Transpiler.checkFile(input.toString(), false, false);

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
    public void canonicalizeModeRejectsMixedMode() throws Exception {
        Path input = Files.createTempFile("euclid-canonicalize-usage", ".ed");
        Files.writeString(input, "sum(i, i, 1, n)");

        int exitCode = runCliQuietly("--canonicalize", "--mixed", input.toString());

        assertEquals(1, exitCode);
    }

    private static int runCliQuietly(String... args) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outBuffer));
            System.setErr(new PrintStream(errBuffer));
            return Transpiler.runCli(args);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
