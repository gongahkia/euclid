package com.euclid.lsp;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import java.io.InputStream;
import java.io.OutputStream;

public class EuclidLspLauncher {
    public static void main(String[] args) throws Exception {
        EuclidLanguageServer server = new EuclidLanguageServer();
        InputStream in = System.in;
        OutputStream out = System.out;
        Launcher<LanguageClient> launcher = Launcher.createLauncher(server, LanguageClient.class, in, out);
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);
        launcher.startListening().get();
    }
}
