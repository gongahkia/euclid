package com.euclid.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;
import java.util.concurrent.CompletableFuture;

public class EuclidLanguageServer implements LanguageServer, LanguageClientAware {
    private final EuclidTextDocumentService textDocumentService;
    private final EuclidWorkspaceService workspaceService;
    private LanguageClient client;
    private int errorCode = 1;

    public EuclidLanguageServer() {
        this.textDocumentService = new EuclidTextDocumentService(this);
        this.workspaceService = new EuclidWorkspaceService();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        capabilities.setHoverProvider(true);
        CompletionOptions completionOptions = new CompletionOptions();
        completionOptions.setTriggerCharacters(java.util.List.of("(", ",", " "));
        capabilities.setCompletionProvider(completionOptions);
        SignatureHelpOptions sigHelpOptions = new SignatureHelpOptions();
        sigHelpOptions.setTriggerCharacters(java.util.List.of("(", ","));
        capabilities.setSignatureHelpProvider(sigHelpOptions);
        SemanticTokensWithRegistrationOptions semanticOptions = new SemanticTokensWithRegistrationOptions();
        semanticOptions.setLegend(new SemanticTokensLegend(
            java.util.List.of("function", "constant", "operator", "number", "variable", "keyword", "comment"),
            java.util.List.of()));
        semanticOptions.setFull(true);
        capabilities.setSemanticTokensProvider(semanticOptions);
        return CompletableFuture.completedFuture(new InitializeResult(capabilities));
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        errorCode = 0;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        System.exit(errorCode);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
        this.textDocumentService.connect(client);
    }

    public LanguageClient getClient() {
        return client;
    }
}
