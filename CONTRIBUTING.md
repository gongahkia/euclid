# Contributing to Euclid

Thank you for your interest in contributing to Euclid! This document provides guidelines and instructions for contributing.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and collaborative environment.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates. When creating a bug report, include:

* **Clear descriptive title**
* **Exact steps to reproduce** the issue
* **Expected vs actual behavior**
* **Sample input** (`.ed` file or expression)
* **Environment details**: Java version, OS, Maven version
* **Error messages** and stack traces if applicable

**Example Bug Report:**
```
Title: Parser fails on nested function calls

Steps to reproduce:
1. Create file with: sin(cos(x))
2. Run transpiler
3. See ParserException

Expected: \sin(\cos(x))
Actual: ParserException: Unexpected token

Environment: Java 21, Ubuntu 22.04, Maven 3.9.5
```

### Suggesting Features

Feature suggestions are welcome! Please provide:

* **Clear description** of the feature
* **Use case** - why is this needed?
* **Example syntax** if proposing language changes
* **Expected behavior** and output

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Follow coding standards** (see below)
3. **Add tests** for new functionality
4. **Update documentation** if you change APIs or syntax
5. **Run tests** before submitting: `mvn test`
6. **Write clear commit messages** following conventional commits

**Branch naming:**
* `feature/add-complex-numbers` - new features
* `fix/parser-delimiter-bug` - bug fixes
* `docs/update-syntax-guide` - documentation
* `refactor/simplify-lexer` - code improvements

**Commit message format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:** `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

**Examples:**
```
feat(parser): add support for complex numbers

Implement complex number parsing with i notation.
Supports syntax: 3 + 4i, (1+2i) * (3-4i)

Closes #42

fix(lexer): handle Unicode Greek letters correctly

Previously failed on α, β, γ. Now properly tokenizes
all Greek alphabet characters.

docs(readme): add troubleshooting section

Added common installation and runtime issues with solutions.
```

## Development Setup

1. Clone the repository:
```bash
git clone https://github.com/gongahkia/euclid.git
cd euclid
```

2. Install dependencies:
```bash
mvn clean install
```

3. Run tests:
```bash
mvn test
```

4. Build project:
```bash
mvn clean package
```

## Project Structure

```
euclid/
├── src/
│   ├── main/java/com/euclid/
│   │   ├── lexer/          # Tokenization
│   │   ├── parser/         # AST generation
│   │   ├── ast/            # AST node definitions
│   │   ├── transpiler/     # LaTeX generation
│   │   ├── token/          # Token types
│   │   ├── exception/      # Custom exceptions
│   │   └── util/           # Helper utilities
│   └── test/java/com/euclid/  # Tests
├── example/                # Example .ed files
├── doc/                    # Documentation
└── pom.xml                 # Maven configuration
```

## Coding Standards

### Java Style

* **Indentation**: 4 spaces (no tabs)
* **Line length**: Max 120 characters
* **Naming conventions**:
  * Classes: `PascalCase`
  * Methods/variables: `camelCase`
  * Constants: `UPPER_SNAKE_CASE`
* **Braces**: Opening brace on same line (K&R style)
* **Imports**: Group standard library, then third-party, then project

**Example:**
```java
public class Parser {
    private static final int MAX_DEPTH = 100;
    private List<Token> tokens;
    
    public AstNode parse(List<Token> tokens) throws ParserException {
        if (tokens == null || tokens.isEmpty()) {
            throw new ParserException("Empty token list");
        }
        // Implementation
    }
}
```

### Documentation

* **All public methods** must have Javadoc
* **Complex algorithms** should have inline comments
* **Example usage** in class-level Javadoc

**Example:**
```java
/**
 * Parses a list of tokens into an Abstract Syntax Tree.
 * 
 * <p>Uses recursive descent parsing with operator precedence.
 * Supports nested expressions and function calls.
 * 
 * <p>Example:
 * <pre>{@code
 * List<Token> tokens = lexer.tokenize("x^2 + 1");
 * AstNode ast = parser.parse(tokens);
 * }</pre>
 * 
 * @param tokens list of tokens from lexer
 * @return root node of AST
 * @throws ParserException if syntax is invalid
 */
public AstNode parse(List<Token> tokens) throws ParserException {
    // Implementation
}
```

### Testing

* **Unit tests** for individual components
* **Integration tests** for full pipeline
* **Test naming**: `should<DoSomething>When<Condition>`
* **Arrange-Act-Assert** pattern

**Example:**
```java
@Test
void shouldParseBinaryExpressionWhenGivenAddition() throws Exception {
    // Arrange
    String input = "x + y";
    Lexer lexer = new Lexer(input);
    Parser parser = new Parser();
    
    // Act
    AstNode result = parser.parse(lexer.tokenize());
    
    // Assert
    assertInstanceOf(BinaryExpr.class, result);
    BinaryExpr expr = (BinaryExpr) result;
    assertEquals("+", expr.getOperator());
}
```

## Adding New Features

### Adding New Functions

1. **Add token type** in `TokenType.java`:
```java
SINH("sinh"),  // Hyperbolic sine
```

2. **Update lexer** in `Lexer.java` if needed (usually automatic for word-based functions)

3. **Add parsing logic** in `Parser.java`:
```java
case SINH:
    consume(TokenType.LPAREN);
    AstNode arg = parseExpression();
    consume(TokenType.RPAREN);
    return new CallExpr("sinh", List.of(arg));
```

4. **Add transpilation** in `LatexTranspiler.java`:
```java
case "sinh":
    return "\\sinh" + transpileArguments(node);
```

5. **Add tests**:
```java
@Test
void shouldTranspileHyperbolicSine() throws Exception {
    String result = transpile("sinh(x)");
    assertEquals("\\sinh(x)", result);
}
```

6. **Update documentation** in `doc/syntax.md`

### Adding New Operators

Follow similar process but also define **precedence** in parser.

## Questions?

* Open an issue for discussion
* Tag with `question` label
* Maintainers typically respond within 48 hours

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
