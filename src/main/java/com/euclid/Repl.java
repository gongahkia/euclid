package com.euclid;

import com.euclid.exception.EuclidException;

import java.util.Scanner;

/**
 * Interactive REPL (Read-Eval-Print-Loop) for Euclid.
 * Allows users to test Euclid expressions interactively.
 */
public class Repl {
    private static final String PROMPT = ">>> ";
    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        printWelcome();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print(PROMPT);

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();

            // Handle special commands
            if (input.isEmpty()) {
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

            // Transpile the input
            try {
                String result = Transpiler.transpile(input);
                System.out.println("LaTeX: " + result);
                System.out.println();
            } catch (EuclidException e) {
                System.err.println("Error: " + e.getMessage());
                System.out.println();
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
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
        System.out.println("  :help, :h       - Show this help message");
        System.out.println("  :quit, :q, :exit- Exit the REPL");
        System.out.println("  :version, :v    - Show version information");
        System.out.println("  :clear, :c      - Clear the screen");
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
        System.out.println("For full syntax reference, see: doc/syntax.md");
        System.out.println();
    }

    private static void clearScreen() {
        // ANSI escape code to clear screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
        printWelcome();
    }
}
