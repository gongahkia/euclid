package com.euclid;

import com.euclid.exception.EuclidException;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive REPL (Read-Eval-Print-Loop) for Euclid.
 * Allows users to test Euclid expressions interactively.
 */
public class Repl {
    private static final String PROMPT = ">>> ";
    private static final String CONTINUATION_PROMPT = "... ";
    private static final String VERSION = "1.0.0";
    private static boolean multilineMode = false;
    private static final List<String> inputBuffer = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // Set up JLine terminal and reader with history support
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();

            // Configure history file
            Path historyPath = Paths.get(System.getProperty("user.home"), ".euclid_history");

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .variable(LineReader.HISTORY_FILE, historyPath)
                    .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                    .build();

            printWelcome();

            boolean running = true;

            while (running) {
                String prompt = multilineMode ? CONTINUATION_PROMPT : PROMPT;
                String input;

                try {
                    input = reader.readLine(prompt).trim();
                } catch (UserInterruptException e) {
                    // Ctrl+C pressed
                    System.out.println();
                    continue;
                } catch (EndOfFileException e) {
                    // Ctrl+D pressed
                    break;
                }

            // Handle empty input
            if (input.isEmpty()) {
                if (multilineMode && !inputBuffer.isEmpty()) {
                    // Empty line in multiline mode - execute accumulated input
                    String fullInput = String.join("\n", inputBuffer);
                    inputBuffer.clear();
                    multilineMode = false;
                    executeInput(fullInput);
                }
                continue;
            }

            // Handle special commands
            if (input.startsWith(":")) {
                if (multilineMode) {
                    System.err.println("Error: Commands cannot be used in multiline mode. Press Enter on empty line to exit multiline mode.");
                    continue;
                }

                if (input.equals(":quit") || input.equals(":q") || input.equals(":exit")) {
                    running = false;
                    continue;
                }

                if (input.equals(":help") || input.equals(":h")) {
                    printHelp();
                    continue;
                }

                if (input.equals(":version") || input.equals(":v")) {
                    System.out.println("Euclid version " + VERSION);
                    continue;
                }

                if (input.equals(":clear") || input.equals(":c")) {
                    clearScreen();
                    continue;
                }

                if (input.equals(":multiline") || input.equals(":m")) {
                    multilineMode = true;
                    inputBuffer.clear();
                    System.out.println("Entering multiline mode. Press Enter on empty line to execute.");
                    continue;
                }

                if (input.startsWith(":load ") || input.startsWith(":l ")) {
                    String filename = input.substring(input.indexOf(' ') + 1).trim();
                    loadFile(filename);
                    continue;
                }

                if (input.equals(":history") || input.equals(":hist")) {
                    printHistory(reader);
                    continue;
                }

                System.err.println("Unknown command: " + input);
                System.err.println("Type ':help' for available commands.");
                continue;
            }

            // Check for unclosed delimiters to enter multiline mode automatically
            if (!multilineMode && hasUnclosedDelimiters(input)) {
                multilineMode = true;
                inputBuffer.add(input);
                continue;
            }

            // Handle multiline input
            if (multilineMode) {
                inputBuffer.add(input);
                continue;
            }

            // Execute single-line input
            executeInput(input);
        }

            System.out.println("Goodbye!");

            try {
                terminal.close();
            } catch (IOException e) {
                // Ignore close errors
            }
        } catch (IOException e) {
            System.err.println("Error initializing terminal: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printWelcome() {
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    Euclid Interactive REPL                         ║");
        System.out.println("║                         Version " + VERSION + "                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Intuitive syntax for beautiful LaTeX mathematical expressions.");
        System.out.println("Type ':help' for available commands, or ':quit' to exit.");
        System.out.println();
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("Available Commands:");
        System.out.println("  :help, :h         - Show this help message");
        System.out.println("  :quit, :q, :exit  - Exit the REPL");
        System.out.println("  :version, :v      - Show version information");
        System.out.println("  :clear, :c        - Clear the screen");
        System.out.println("  :multiline, :m    - Enter multiline mode for complex expressions");
        System.out.println("  :load <file>, :l  - Load and execute a .ed file");
        System.out.println("  :history, :hist   - Show command history");
        System.out.println();
        System.out.println("Multiline Input:");
        System.out.println("  - Unclosed delimiters automatically enter multiline mode");
        System.out.println("  - Press Enter on an empty line to execute multiline input");
        System.out.println("  - Use :multiline command to manually enter multiline mode");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  >>> PI");
        System.out.println("  LaTeX: \\pi");
        System.out.println();
        System.out.println("  >>> pow(x, 2) + 3");
        System.out.println("  LaTeX: x^{2} + 3");
        System.out.println();
        System.out.println("  >>> sqrt(2, 16)");
        System.out.println("  LaTeX: \\sqrt[2]{16}");
        System.out.println();
        System.out.println("  >>> integral(sin(x), x, 0, PI)");
        System.out.println("  LaTeX: \\int_{0}^{\\pi} \\sin(x) \\, dx");
        System.out.println();
        System.out.println("  >>> vector([1,");
        System.out.println("  ... 2, 3])");
        System.out.println("  LaTeX: \\begin{pmatrix} 1 \\\\ 2 \\\\ 3 \\end{pmatrix}");
        System.out.println();
        System.out.println("For full syntax reference, see: doc/syntax.md");
        System.out.println();
    }

    private static void clearScreen() {
        // ANSI escape code to clear screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
        printWelcome();
    }

    /**
     * Executes the given Euclid expression and prints the result.
     *
     * @param input The Euclid expression to execute
     */
    private static void executeInput(String input) {
        try {
            String result = Transpiler.transpile(input);
            System.out.println("LaTeX: " + result);
            System.out.println();
        } catch (EuclidException e) {
            System.err.println("Error: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Checks if the input has unclosed delimiters (for automatic multiline mode).
     *
     * @param input The input string to check
     * @return true if there are unclosed delimiters
     */
    private static boolean hasUnclosedDelimiters(String input) {
        int parenCount = 0;
        int bracketCount = 0;
        int braceCount = 0;

        for (char c : input.toCharArray()) {
            switch (c) {
                case '(' -> parenCount++;
                case ')' -> parenCount--;
                case '[' -> bracketCount++;
                case ']' -> bracketCount--;
                case '{' -> braceCount++;
                case '}' -> braceCount--;
            }
        }

        return parenCount > 0 || bracketCount > 0 || braceCount > 0;
    }

    /**
     * Loads and executes a Euclid file.
     *
     * @param filename The path to the file to load
     */
    private static void loadFile(String filename) {
        try {
            Path path = Paths.get(filename);
            if (!Files.exists(path)) {
                System.err.println("Error: File not found: " + filename);
                return;
            }

            String content = Files.readString(path);
            System.out.println("Loading file: " + filename);
            System.out.println();

            String[] lines = content.split("\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("//")) {
                    continue; // Skip empty lines and comments
                }

                System.out.println(PROMPT + trimmed);
                executeInput(trimmed);
            }

            System.out.println("File loaded successfully.");
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.out.println();
        }
    }

    /**
     * Prints the command history.
     *
     * @param reader The LineReader instance with history
     */
    private static void printHistory(LineReader reader) {
        History history = reader.getHistory();

        if (history.isEmpty()) {
            System.out.println("No history available.");
            System.out.println();
            return;
        }

        System.out.println();
        System.out.println("Command History:");
        System.out.println("─────────────────────────────────────────────────────────────────");

        int count = 1;
        for (History.Entry entry : history) {
            System.out.printf("%4d  %s%n", count++, entry.line());
        }

        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("Total: " + history.size() + " commands");
        System.out.println();
        System.out.println("Tip: Use Up/Down arrow keys to navigate history");
        System.out.println();
    }
}
