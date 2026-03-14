package com.euclid;

import com.euclid.exception.EuclidException;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TranspilerFileContractTest {

    @Test
    public void warningsDoNotBlockFileOutput() throws Exception {
        Path input = Files.createTempFile("euclid-warning", ".ed");
        Path output = Files.createTempFile("euclid-warning", ".md");
        Files.writeString(input, "INF");

        Transpiler.transpileFile(input.toString(), output.toString(), false, com.euclid.transpiler.MathMode.NONE);

        assertEquals("\\infty", Files.readString(output));
    }

    @Test
    public void errorsPreventBrokenFileOutput() throws Exception {
        Path input = Files.createTempFile("euclid-error", ".ed");
        Path output = Files.createTempFile("euclid-error", ".md");
        Files.deleteIfExists(output);
        Files.writeString(input, "piecewise(x, geq(x, 0), -x)");

        assertThrows(EuclidException.class,
                () -> Transpiler.transpileFile(input.toString(), output.toString(), false, com.euclid.transpiler.MathMode.NONE));
        assertFalse(Files.exists(output));
    }
}
