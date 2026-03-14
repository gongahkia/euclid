import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public final class CompileCheck {
    private CompileCheck() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java CompileCheck.java <source-root> <output-dir>");
        }

        Path sourceRoot = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);
        Files.createDirectories(outputDir);

        List<Path> sources = new ArrayList<>();
        try (var stream = Files.walk(sourceRoot)) {
            stream.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.toString().contains("/lsp/"))
                    .filter(path -> !path.getFileName().toString().equals("Repl.java"))
                    .forEach(sources::add);
        }

        if (sources.isEmpty()) {
            throw new IllegalStateException("No Java sources found under " + sourceRoot);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("No system Java compiler is available.");
        }

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            var compilationUnits = fileManager.getJavaFileObjectsFromPaths(sources);
            List<String> options = List.of("-d", outputDir.toString());
            boolean success = compiler.getTask(null, fileManager, null, options, null, compilationUnits).call();
            if (!success) {
                throw new IllegalStateException("Core source compilation failed.");
            }
        }

        System.out.println("Compiled " + sources.size() + " source files into " + outputDir + ".");
    }
}
