package com.euclid.lsp;

import com.euclid.TranspileResult;
import com.euclid.Transpiler;
import com.euclid.exception.Diagnostic;
import com.euclid.exception.EuclidException;
import com.euclid.lexer.Lexer;
import com.euclid.token.Token;
import com.euclid.token.TokenType;
import com.euclid.transpiler.MathMode;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EuclidTextDocumentService implements TextDocumentService {
    private final EuclidLanguageServer server;
    private LanguageClient client;
    private final Map<String, String> documents = new HashMap<>();

    public EuclidTextDocumentService(EuclidLanguageServer server) {
        this.server = server;
    }

    public void connect(LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        documents.put(uri, text);
        publishDiagnostics(uri, text);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getContentChanges().get(0).getText();
        documents.put(uri, text);
        publishDiagnostics(uri, text);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        documents.remove(uri);
        if (client != null) {
            client.publishDiagnostics(new PublishDiagnosticsParams(uri, List.of()));
        }
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {}

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        String uri = params.getTextDocument().getUri();
        String content = documents.get(uri);
        if (content == null) return CompletableFuture.completedFuture(null);
        String[] lines = content.split("\\r?\\n");
        int lineIdx = params.getPosition().getLine();
        if (lineIdx >= lines.length) return CompletableFuture.completedFuture(null);
        String line = lines[lineIdx];
        int col = params.getPosition().getCharacter();
        String expr = extractExpressionAround(line, col);
        if (expr == null || expr.isBlank()) return CompletableFuture.completedFuture(null);
        try {
            String result = Transpiler.transpile(expr);
            MarkupContent mc = new MarkupContent(MarkupKind.MARKDOWN, "**LaTeX:** `" + result + "`");
            return CompletableFuture.completedFuture(new Hover(mc));
        } catch (EuclidException e) {
            MarkupContent mc = new MarkupContent(MarkupKind.MARKDOWN, "**Error:** " + e.getMessage());
            return CompletableFuture.completedFuture(new Hover(mc));
        }
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        List<CompletionItem> items = new ArrayList<>();
        Set<String> keywords = Lexer.getKeywordNames();
        for (String kw : keywords) {
            CompletionItem item = new CompletionItem(kw);
            TokenType type = Lexer.getKeywordType(kw);
            if (isConstantType(type)) {
                item.setKind(CompletionItemKind.Constant);
                item.setInsertText(kw);
            } else {
                item.setKind(CompletionItemKind.Function);
                item.setInsertText(kw + "($1)");
                item.setInsertTextFormat(InsertTextFormat.Snippet);
            }
            items.add(item);
        }
        return CompletableFuture.completedFuture(Either.forLeft(items));
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
        String uri = params.getTextDocument().getUri();
        String content = documents.get(uri);
        if (content == null) return CompletableFuture.completedFuture(null);
        String[] lines = content.split("\\r?\\n");
        int lineIdx = params.getPosition().getLine();
        if (lineIdx >= lines.length) return CompletableFuture.completedFuture(null);
        String line = lines[lineIdx];
        int col = Math.min(params.getPosition().getCharacter(), line.length());
        String before = line.substring(0, col);
        int parenDepth = 0;
        int funcEnd = -1;
        int commaCount = 0;
        for (int i = before.length() - 1; i >= 0; i--) {
            char c = before.charAt(i);
            if (c == ')') parenDepth++;
            else if (c == '(') {
                if (parenDepth == 0) { funcEnd = i; break; }
                parenDepth--;
            } else if (c == ',' && parenDepth == 0) commaCount++;
        }
        if (funcEnd <= 0) return CompletableFuture.completedFuture(null);
        int funcStart = funcEnd - 1;
        while (funcStart >= 0 && Character.isLetterOrDigit(before.charAt(funcStart))) funcStart--;
        funcStart++;
        String funcName = before.substring(funcStart, funcEnd);
        SignatureInformation sigInfo = SIGNATURES.get(funcName);
        if (sigInfo == null) return CompletableFuture.completedFuture(null);
        SignatureHelp help = new SignatureHelp();
        help.setSignatures(List.of(sigInfo));
        help.setActiveSignature(0);
        help.setActiveParameter(commaCount);
        return CompletableFuture.completedFuture(help);
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        String uri = params.getTextDocument().getUri();
        String content = documents.get(uri);
        if (content == null) return CompletableFuture.completedFuture(new SemanticTokens(List.of()));
        try {
            List<Token> tokens = new Lexer(content).tokenize();
            List<Integer> data = new ArrayList<>();
            int prevLine = 0, prevCol = 0;
            for (Token token : tokens) {
                if (token.getType() == TokenType.EOF || token.getType() == TokenType.NEWLINE) continue;
                int tokenTypeIdx = semanticTypeIndex(token.getType());
                if (tokenTypeIdx < 0) continue;
                int line = token.getLine() - 1; // 0-indexed
                int col = token.getColumn() - 1;
                int deltaLine = line - prevLine;
                int deltaCol = deltaLine == 0 ? col - prevCol : col;
                int length = token.getLexeme().length();
                if (length == 0) continue;
                data.add(deltaLine);
                data.add(deltaCol);
                data.add(length);
                data.add(tokenTypeIdx);
                data.add(0); // no modifiers
                prevLine = line;
                prevCol = col;
            }
            return CompletableFuture.completedFuture(new SemanticTokens(data));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new SemanticTokens(List.of()));
        }
    }

    private void publishDiagnostics(String uri, String text) {
        if (client == null) return;
        TranspileResult result = Transpiler.transpileWithDiagnostics(text, false, MathMode.NONE, false);
        List<org.eclipse.lsp4j.Diagnostic> lspDiags = new ArrayList<>();
        for (Diagnostic d : result.diagnostics()) {
            org.eclipse.lsp4j.Diagnostic lspD = new org.eclipse.lsp4j.Diagnostic();
            int line = Math.max(0, d.getLine() - 1);
            int col = Math.max(0, d.getColumn() - 1);
            lspD.setRange(new Range(new Position(line, col), new Position(line, col + 1)));
            lspD.setMessage(d.getMessage());
            lspD.setSeverity(switch (d.getSeverity()) {
                case ERROR -> DiagnosticSeverity.Error;
                case WARNING -> DiagnosticSeverity.Warning;
                case INFO -> DiagnosticSeverity.Information;
            });
            lspD.setSource("euclid");
            lspDiags.add(lspD);
        }
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, lspDiags));
    }

    private String extractExpressionAround(String line, int col) {
        if (col >= line.length()) col = line.length() - 1;
        if (col < 0) return null;
        int start = col, end = col;
        while (start > 0 && !Character.isWhitespace(line.charAt(start - 1))) start--;
        while (end < line.length() - 1 && !Character.isWhitespace(line.charAt(end + 1))) end++;
        return line.substring(start, end + 1);
    }

    private boolean isConstantType(TokenType type) {
        return type == TokenType.PI || type == TokenType.E || type == TokenType.I ||
               type == TokenType.GAMMA || type == TokenType.PHI || type == TokenType.INFINITY ||
               type == TokenType.EMPTYSET || type == TokenType.NATURALS || type == TokenType.INTEGERS ||
               type == TokenType.RATIONALS || type == TokenType.REALS || type == TokenType.COMPLEXES ||
               type == TokenType.THEREFORE || type == TokenType.BECAUSE || type == TokenType.QED ||
               type == TokenType.HBAR || type == TokenType.NABLA || type == TokenType.ELL ||
               type == TokenType.LDOTS || type == TokenType.CDOTS || type == TokenType.VDOTS || type == TokenType.DDOTS ||
               type == TokenType.RIGHTARROW || type == TokenType.LEFTARROW || type == TokenType.MAPSTO;
    }

    private int semanticTypeIndex(TokenType type) {
        if (isFunctionType(type)) return 0;
        if (isConstantType(type) || isGreekType(type)) return 1;
        if (isOperatorType(type)) return 2;
        if (type == TokenType.NUMBER) return 3;
        if (type == TokenType.IDENTIFIER) return 4;
        if (type == TokenType.FROM || type == TokenType.TO) return 5;
        return -1;
    }

    private boolean isFunctionType(TokenType t) {
        return t == TokenType.SIN || t == TokenType.COS || t == TokenType.TAN ||
               t == TokenType.POW || t == TokenType.SQRT || t == TokenType.LOG || t == TokenType.LN ||
               t == TokenType.ABS || t == TokenType.EXP || t == TokenType.LIMIT || t == TokenType.DIFF ||
               t == TokenType.INTEGRAL || t == TokenType.SUM || t == TokenType.PROD ||
               t == TokenType.BINOM || t == TokenType.NORM || t == TokenType.INNER ||
               t == TokenType.GRAD || t == TokenType.DET || t == TokenType.PROB ||
               t == TokenType.ARCSIN || t == TokenType.ARCCOS || t == TokenType.ARCTAN ||
               t == TokenType.MIN || t == TokenType.MAX || t == TokenType.VECTOR || t == TokenType.MATRIX;
    }

    private boolean isGreekType(TokenType t) {
        return t == TokenType.ALPHA || t == TokenType.BETA || t == TokenType.DELTA ||
               t == TokenType.EPSILON || t == TokenType.THETA || t == TokenType.LAMBDA ||
               t == TokenType.MU || t == TokenType.SIGMA || t == TokenType.OMEGA;
    }

    private boolean isOperatorType(TokenType t) {
        return t == TokenType.PLUS || t == TokenType.MINUS || t == TokenType.MULTIPLY ||
               t == TokenType.DIVIDE || t == TokenType.POWER || t == TokenType.EQUALS ||
               t == TokenType.MODULO;
    }

    private static final Map<String, SignatureInformation> SIGNATURES = new HashMap<>();
    static {
        SIGNATURES.put("sin", sig("sin(x)", "x: angle"));
        SIGNATURES.put("cos", sig("cos(x)", "x: angle"));
        SIGNATURES.put("tan", sig("tan(x)", "x: angle"));
        SIGNATURES.put("pow", sig("pow(base, exponent)", "base: base value", "exponent: power"));
        SIGNATURES.put("sqrt", sig("sqrt(x) or sqrt(n, x)", "x: radicand", "n: root degree (optional)"));
        SIGNATURES.put("log", sig("log(x, base)", "x: value", "base: logarithm base"));
        SIGNATURES.put("ln", sig("ln(x)", "x: value"));
        SIGNATURES.put("exp", sig("exp(x)", "x: exponent"));
        SIGNATURES.put("abs", sig("abs(x)", "x: value"));
        SIGNATURES.put("limit", sig("limit(expr, var, approach)", "expr: expression", "var: variable", "approach: limit point"));
        SIGNATURES.put("diff", sig("diff(expr, var)", "expr: expression", "var: variable"));
        SIGNATURES.put("integral", sig("integral(expr, var, lower, upper)", "expr: integrand", "var: variable", "lower: lower bound", "upper: upper bound"));
        SIGNATURES.put("sum", sig("sum(expr, var, lower, upper)", "expr: summand", "var: index variable", "lower: lower bound", "upper: upper bound"));
        SIGNATURES.put("prod", sig("prod(expr, var, lower, upper)", "expr: multiplicand", "var: index variable", "lower: lower bound", "upper: upper bound"));
        SIGNATURES.put("binom", sig("binom(n, k)", "n: total", "k: choose"));
        SIGNATURES.put("norm", sig("norm(x) or norm(x, p)", "x: vector/value", "p: norm type (optional)"));
        SIGNATURES.put("inner", sig("inner(x, y)", "x: first vector", "y: second vector"));
        SIGNATURES.put("grad", sig("grad(f)", "f: scalar field"));
        SIGNATURES.put("det", sig("det(A)", "A: matrix"));
        SIGNATURES.put("prob", sig("prob(A)", "A: event"));
        SIGNATURES.put("expect", sig("expect(X)", "X: random variable"));
        SIGNATURES.put("var", sig("var(X)", "X: random variable"));
        SIGNATURES.put("cov", sig("cov(X, Y)", "X: first variable", "Y: second variable"));
        SIGNATURES.put("vector", sig("vector(a, b, c, ...)", "elements: vector components"));
        SIGNATURES.put("matrix", sig("matrix([a,b], [c,d], ...)", "rows: matrix rows"));
    }

    private static SignatureInformation sig(String label, String... paramLabels) {
        SignatureInformation si = new SignatureInformation(label);
        List<ParameterInformation> params = new ArrayList<>();
        for (String p : paramLabels) params.add(new ParameterInformation(p));
        si.setParameters(params);
        return si;
    }
}
