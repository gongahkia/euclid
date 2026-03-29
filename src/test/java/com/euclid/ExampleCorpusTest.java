package com.euclid;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExampleCorpusTest {

    @Test
    public void inputExampleMatchesGoldenOutput() throws Exception {
        Path input = Path.of("example", "input.ed");
        Path output = Path.of("example", "output.md");

        assertEquals(
                Files.readString(output),
                Transpiler.transpileDocument(Files.readString(input)));
    }

    @Test
    public void shippedExamplesTranspileInDocumentMode() throws Exception {
        for (Path path : exampleFiles()) {
            String rendered = Transpiler.transpileDocument(Files.readString(path));
            assertNotNull(rendered, path.toString());
            assertFalse(rendered.isBlank(), path.toString());
        }
    }

    @Test
    public void shippedExamplesCheckWithoutErrorsInDocumentMode() throws Exception {
        for (Path path : exampleFiles()) {
            TranspileResult result = Transpiler.checkDocument(Files.readString(path), false, com.euclid.lang.EuclidAliasHandling.WARN);
            assertFalse(result.hasErrors(), path.toString());
        }
    }

    private List<Path> exampleFiles() throws IOException {
        try (var files = Files.list(Path.of("example"))) {
            return files
                    .filter(path -> path.getFileName().toString().endsWith(".ed"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
        }
    }
}
