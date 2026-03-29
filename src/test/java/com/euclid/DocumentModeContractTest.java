package com.euclid;

import com.euclid.exception.Diagnostic;
import com.euclid.lang.EuclidAliasHandling;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentModeContractTest {

    @Test
    public void transpileDocumentPreservesProtectedMarkdownAndProse() throws Exception {
        String source = "Paragraph with $x^2 + y^2$ and `code $PI$`.\n"
                + "[link $INF$](https://example.com?q=$INF$)\n"
                + "<div>$INF$</div>\n\n"
                + "```euclid\n"
                + "$PI$\n"
                + "```\n";

        String result = Transpiler.transpileDocument(source);

        assertTrue(result.contains("Paragraph with $x^{2} + y^{2}$ and `code $PI$`."));
        assertTrue(result.contains("[link $INF$](https://example.com?q=$INF$)"));
        assertTrue(result.contains("<div>$INF$</div>"));
        assertTrue(result.contains("```euclid\n$PI$\n```"));
    }

    @Test
    public void canonicalizeDocumentOnlyTouchesExplicitMathSpans() {
        String source = "Prose INF and $INF + choose(n, k)$ and `INF`";

        assertEquals(
                "Prose INF and $INFINITY + binom(n, k)$ and `INF`",
                Transpiler.canonicalizeDocument(source));
    }

    @Test
    public void bareProseAliasesRemainUntouchedInDocumentMode() {
        TranspileResult result = Transpiler.checkDocument("The prose says INF.", false, EuclidAliasHandling.WARN);

        assertFalse(result.hasErrors());
        assertTrue(result.diagnostics().isEmpty());
        assertEquals("The prose says INF.", result.output());
    }

    @Test
    public void checkDocumentMapsDiagnosticsToDocumentCoordinates() {
        String source = "Intro $INF$ then $piecewise(x, geq(x, 0), -x)$";
        TranspileResult result = Transpiler.checkDocument(source, false, EuclidAliasHandling.WARN);

        Diagnostic alias = result.diagnostics().stream()
                .filter(diagnostic -> "canonical.rewrite".equals(diagnostic.getCode()))
                .findFirst()
                .orElseThrow();
        Diagnostic arity = result.diagnostics().stream()
                .filter(diagnostic -> "parser.invalid-arity".equals(diagnostic.getCode()))
                .findFirst()
                .orElseThrow();

        assertEquals(1, alias.getLine());
        assertEquals(8, alias.getColumn());
        assertEquals(1, arity.getLine());
        assertEquals(19, arity.getColumn());
    }

    @Test
    public void multiLineRawHtmlBlocksRemainProtected() throws Exception {
        String source = "<div>\n$PI$\n</div>\n";

        assertEquals(source, Transpiler.transpileDocument(source));
    }

    @Test
    public void linksWithParenthesizedDestinationsRemainProtected() throws Exception {
        String source = "[calc $PI$](https://example.com/f(x)) and $x^2$";
        String result = Transpiler.transpileDocument(source);

        assertTrue(result.contains("[calc $PI$](https://example.com/f(x))"));
        assertTrue(result.contains("$x^{2}$"));
    }
}
