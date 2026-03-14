package com.euclid.lsp;

import com.euclid.TranspileResult;
import com.euclid.Transpiler;
import com.euclid.exception.Diagnostic;
import com.euclid.lang.EuclidCapability;
import com.euclid.lang.EuclidCapabilityKind;
import com.euclid.lang.EuclidLanguage;
import com.euclid.lang.EuclidSignature;
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
        for (EuclidCapability capability : EuclidLanguage.capabilityManifest().capabilities()) {
            CompletionItem item = new CompletionItem(capability.name());
            EuclidCapabilityKind kind = capability.kind();
            if (kind == EuclidCapabilityKind.FUNCTION) {
                item.setKind(CompletionItemKind.Function);
                item.setInsertText(capability.name() + "($1)");
                item.setInsertTextFormat(InsertTextFormat.Snippet);
            } else if (kind == EuclidCapabilityKind.CONSTANT) {
                item.setKind(CompletionItemKind.Constant);
                item.setInsertText(capability.name());
            } else {
                item.setKind(CompletionItemKind.Keyword);
                item.setInsertText(capability.name());
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
        while (funcStart >= 0 && (Character.isLetterOrDigit(before.charAt(funcStart)) || before.charAt(funcStart) == '_')) funcStart--;
        funcStart++;
        String funcName = before.substring(funcStart, funcEnd);
        SignatureInformation sigInfo = toSignature(EuclidLanguage.getSignature(funcName));
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
            if (d.getCode() != null) {
                lspD.setCode(Either.forLeft(d.getCode()));
            }
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
        return EuclidLanguage.isConstantType(type);
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
        return EuclidLanguage.isFunctionType(t);
    }

    private boolean isGreekType(TokenType t) {
        return EuclidLanguage.isGreekType(t);
    }

    private boolean isOperatorType(TokenType t) {
        return t == TokenType.PLUS || t == TokenType.MINUS || t == TokenType.MULTIPLY ||
               t == TokenType.DIVIDE || t == TokenType.POWER || t == TokenType.EQUALS ||
               t == TokenType.MODULO || t == TokenType.AND || t == TokenType.OR || t == TokenType.NOT;
    }

    private static SignatureInformation toSignature(EuclidSignature signature) {
        if (signature == null) {
            return null;
        }
        SignatureInformation si = new SignatureInformation(signature.label());
        List<ParameterInformation> params = new ArrayList<>();
        for (String p : signature.parameters()) params.add(new ParameterInformation(p));
        si.setParameters(params);
        return si;
    }
}
