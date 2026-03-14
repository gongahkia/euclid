package com.euclid.web;

import com.euclid.TranspileResult;
import com.euclid.Transpiler;
import com.euclid.exception.Diagnostic;
import com.euclid.lang.EuclidCapability;
import com.euclid.lang.EuclidCapabilityManifest;
import com.euclid.lang.EuclidSignature;
import com.euclid.transpiler.MathMode;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Minimal built-in web server for the Euclid playground.
 */
public final class EuclidWebServer {
    private static final String INDEX_RESOURCE = "/web/index.html";
    private static final String APP_RESOURCE = "/web/app.js";
    private static final String STYLES_RESOURCE = "/web/styles.css";

    private EuclidWebServer() {
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticHandler(INDEX_RESOURCE, "text/html; charset=utf-8"));
        server.createContext("/app.js", new StaticHandler(APP_RESOURCE, "application/javascript; charset=utf-8"));
        server.createContext("/styles.css", new StaticHandler(STYLES_RESOURCE, "text/css; charset=utf-8"));
        server.createContext("/api/capabilities", exchange -> handleCapabilities(exchange));
        server.createContext("/api/canonicalize", exchange -> handleCanonicalize(exchange));
        server.createContext("/api/transpile", exchange -> handleTranspile(exchange));
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Euclid web playground running at http://localhost:" + port);
    }

    private static void handleCapabilities(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            writeMethodNotAllowed(exchange);
            return;
        }

        EuclidCapabilityManifest manifest = Transpiler.capabilityManifest();
        StringBuilder json = new StringBuilder();
        json.append("{\"capabilities\":[");
        List<EuclidCapability> capabilities = manifest.capabilities();
        for (int i = 0; i < capabilities.size(); i++) {
            EuclidCapability capability = capabilities.get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append("{")
                    .append("\"name\":\"").append(escapeJson(capability.name())).append("\",")
                    .append("\"kind\":\"").append(capability.kind()).append("\",")
                    .append("\"tokenType\":\"").append(capability.tokenType()).append("\",")
                    .append("\"aliases\":").append(toJsonArray(capability.aliases())).append(",")
                    .append("\"signature\":").append(toJsonSignature(capability.signature()))
                    .append("}");
        }
        json.append("]}");
        writeJson(exchange, 200, json.toString());
    }

    private static void handleCanonicalize(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            writeMethodNotAllowed(exchange);
            return;
        }

        Map<String, String> form = parseForm(exchange);
        String source = form.getOrDefault("source", "");
        String canonical = Transpiler.canonicalize(source);
        writeJson(exchange, 200, "{\"canonicalSource\":\"" + escapeJson(canonical) + "\"}");
    }

    private static void handleTranspile(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            writeMethodNotAllowed(exchange);
            return;
        }

        Map<String, String> form = parseForm(exchange);
        String source = form.getOrDefault("source", "");
        boolean mixedMode = Boolean.parseBoolean(form.getOrDefault("mixedMode", "false"));
        MathMode mathMode = parseMathMode(form.get("mathMode"));
        String canonical = Transpiler.canonicalize(source);
        TranspileResult result = Transpiler.transpileWithDiagnostics(source, false, mathMode, mixedMode);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"output\":").append(toJsonString(result.output())).append(",")
                .append("\"canonicalSource\":\"").append(escapeJson(canonical)).append("\",")
                .append("\"hasErrors\":").append(result.hasErrors()).append(",")
                .append("\"diagnostics\":[");

        List<Diagnostic> diagnostics = result.diagnostics();
        for (int i = 0; i < diagnostics.size(); i++) {
            Diagnostic diagnostic = diagnostics.get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append("{")
                    .append("\"severity\":\"").append(diagnostic.getSeverity()).append("\",")
                    .append("\"code\":").append(toJsonString(diagnostic.getCode())).append(",")
                    .append("\"message\":\"").append(escapeJson(diagnostic.getMessage())).append("\",")
                    .append("\"line\":").append(diagnostic.getLine()).append(",")
                    .append("\"column\":").append(diagnostic.getColumn()).append(",")
                    .append("\"suggestion\":").append(toJsonString(diagnostic.getSuggestion())).append(",")
                    .append("\"canonicalRewrite\":").append(toJsonString(diagnostic.getCanonicalRewrite()))
                    .append("}");
        }
        json.append("]}");

        writeJson(exchange, 200, json.toString());
    }

    private static MathMode parseMathMode(String rawMathMode) {
        if (rawMathMode == null || rawMathMode.isBlank()) {
            return MathMode.NONE;
        }

        try {
            return MathMode.valueOf(rawMathMode.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return MathMode.NONE;
        }
    }

    private static Map<String, String> parseForm(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> form = new LinkedHashMap<>();
        if (body.isBlank()) {
            return form;
        }

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    : "";
            form.put(key, value);
        }
        return form;
    }

    private static void writeMethodNotAllowed(HttpExchange exchange) throws IOException {
        writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
    }

    private static void writeJson(HttpExchange exchange, int statusCode, String payload) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Cache-Control", "no-store");
        writeBytes(exchange, statusCode, payload.getBytes(StandardCharsets.UTF_8));
    }

    private static void writeText(HttpExchange exchange, int statusCode, String contentType, byte[] payload) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentType);
        writeBytes(exchange, statusCode, payload);
    }

    private static void writeBytes(HttpExchange exchange, int statusCode, byte[] payload) throws IOException {
        exchange.sendResponseHeaders(statusCode, payload.length);
        exchange.getResponseBody().write(payload);
        exchange.close();
    }

    private static String toJsonArray(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append('"').append(escapeJson(values.get(i))).append('"');
        }
        json.append(']');
        return json.toString();
    }

    private static String toJsonSignature(EuclidSignature signature) {
        if (signature == null) {
            return "null";
        }
        return "{"
                + "\"label\":\"" + escapeJson(signature.label()) + "\","
                + "\"parameters\":" + toJsonArray(signature.parameters())
                + "}";
    }

    private static String toJsonString(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + escapeJson(value) + "\"";
    }

    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> escaped.append(c);
            }
        }
        return escaped.toString();
    }

    private static final class StaticHandler implements HttpHandler {
        private final String resourcePath;
        private final String contentType;

        private StaticHandler(String resourcePath, String contentType) {
            this.resourcePath = resourcePath;
            this.contentType = contentType;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeMethodNotAllowed(exchange);
                return;
            }
            try {
                byte[] content = readResource(resourcePath);
                writeText(exchange, 200, contentType, content);
            } catch (UncheckedIOException exception) {
                writeJson(exchange, 404, "{\"error\":\"Resource not found\"}");
            }
        }

        private static byte[] readResource(String path) {
            try (InputStream inputStream = EuclidWebServer.class.getResourceAsStream(path)) {
                if (inputStream == null) {
                    throw new UncheckedIOException(new IOException("Missing resource " + path));
                }
                return inputStream.readAllBytes();
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }
    }
}
