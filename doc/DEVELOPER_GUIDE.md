# Euclid Developer Guide

Welcome to the Euclid developer documentation! This guide explains the internal architecture and how to extend the project.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Pipeline Flow](#pipeline-flow)
3. [Module Documentation](#module-documentation)
4. [Adding New Features](#adding-new-features)
5. [Development Workflow](#development-workflow)
6. [Testing Strategy](#testing-strategy)

## Architecture Overview

Euclid follows a classic compiler pipeline architecture:

```
.ed Source → Lexer → Tokens → Parser → AST → Transpiler → LaTeX Markdown
```

### Key Design Principles

- **Separation of concerns**: Each stage has a single responsibility
- **Visitor pattern**: AST traversal uses visitor pattern for extensibility
- **Exception hierarchy**: Custom exceptions provide context-specific errors
- **Validation**: Input validation happens at both lexer and parser levels

## Pipeline Flow

### 1. Lexical Analysis (Lexer)

**Location**: `src/main/java/com/euclid/lexer/Lexer.java`

The lexer scans the input string character by character and produces a stream of tokens.

**Input**: Raw `.ed` file content (String)  
**Output**: List of Token objects

**Example**:
```
Input:  "x^2 + 3*y"
Output: [IDENTIFIER(x), POWER, NUMBER(2), PLUS, NUMBER(3), MULTIPLY, IDENTIFIER(y), EOF]
```

**Key methods**:
- `tokenize()`: Main entry point, returns List<Token>
- `scanToken()`: Processes one token at a time
- `number()`: Handles numeric literals
- `identifier()`: Handles keywords and identifiers

**Token types** defined in `TokenType.java`:
- Operators: `PLUS`, `MINUS`, `MULTIPLY`, `DIVIDE`, `POWER`, `FACTORIAL`
- Delimiters: `LPAREN`, `RPAREN`, `LBRACKET`, `RBRACKET`, `COMMA`
- Functions: `SQRT`, `SIN`, `COS`, `LOG`, `INTEGRAL`, `SUM`, `PROD`, etc.
- Literals: `NUMBER`, `IDENTIFIER`
- Special: `EOF`, `NEWLINE`

### 2. Syntax Analysis (Parser)

**Location**: `src/main/java/com/euclid/parser/Parser.java`

The parser uses recursive descent to build an Abstract Syntax Tree (AST) from tokens.

**Input**: List of Token objects  
**Output**: AST (Abstract Syntax Tree)

**Operator precedence** (highest to lowest):
1. Function calls: `sin(x)`, `sqrt(x)`
2. Factorial: `n!`
3. Exponentiation: `x^2` (right-associative)
4. Unary operators: `-x`, `+x`
5. Multiplication/Division: `*`, `/`
6. Addition/Subtraction: `+`, `-`

**Key methods**:
- `parse()`: Entry point, returns root AST node
- `parseExpression()`: Handles addition/subtraction
- `parseTerm()`: Handles multiplication/division
- `parseFactor()`: Handles exponentiation
- `parseUnary()`: Handles unary operators
- `parsePrimary()`: Handles literals, identifiers, grouping, functions

**Example AST** for `x^2 + 1`:
```
BinaryExpr(+)
├── BinaryExpr(^)
│   ├── IdentifierExpr(x)
│   └── LiteralExpr(2)
└── LiteralExpr(1)
```

### 3. Code Generation (Transpiler)

**Location**: `src/main/java/com/euclid/transpiler/LatexTranspiler.java`

The transpiler walks the AST using the visitor pattern and generates LaTeX code.

**Input**: AST root node  
**Output**: LaTeX string

**Key methods**:
- `transpile(AstNode)`: Main entry point
- `visit(BinaryExpr)`: Handles binary operators
- `visit(UnaryExpr)`: Handles unary operators
- `visit(CallExpr)`: Handles function calls
- `visit(LiteralExpr)`: Handles numbers
- `visit(IdentifierExpr)`: Handles variables

**LaTeX mappings**:
- `/` → `\frac{a}{b}`
- `^` → `a^{b}`
- `sqrt(x)` → `\sqrt{x}`
- `sin(x)` → `\sin(x)`
- `integral(f, x)` → `\int f \, dx`
- `sum(i=1, n)` → `\sum_{i=1}^{n}`

## Module Documentation

### Token (`token/Token.java`, `token/TokenType.java`)

Represents a lexical unit with:
- `type`: TokenType enum
- `lexeme`: Original text
- `literal`: Parsed value (for numbers)
- `line`: Source line number

### AST Nodes (`ast/`)

All AST nodes implement `AstNode` interface and accept visitors:

- **BinaryExpr**: Binary operations (`+`, `-`, `*`, `/`, `^`)
- **UnaryExpr**: Unary operations (`-`, `+`)
- **CallExpr**: Function calls (`sin`, `cos`, `sqrt`, etc.)
- **LiteralExpr**: Numeric literals
- **IdentifierExpr**: Variable names
- **GroupingExpr**: Parenthesized expressions
- **TextExpr**: Plain text (Markdown pass-through)
- **DocumentNode**: Root node containing multiple expressions

### Exception Hierarchy (`exception/`)

```
EuclidException (base)
├── LexerException (tokenization errors)
└── ParserException (syntax errors)
```

All exceptions include:
- Error message
- Line number
- Problematic input snippet (future enhancement)

### Validation (`util/ValidationHelper.java`)

Provides input validation:
- `validateArgumentCount()`: Checks function argument counts
- `validateBalancedDelimiters()`: Ensures matched parentheses/brackets
- `suggestSimilarFunctions()`: Levenshtein distance for typo suggestions

## Adding New Features

### Adding a New Function

Let's add `cosh` (hyperbolic cosine) as an example.

#### 1. Add Token Type

**File**: `src/main/java/com/euclid/token/TokenType.java`

```java
// Add to the enum
COSH("cosh"),
```

#### 2. Update Lexer (Usually Automatic)

The lexer automatically recognizes keywords from `TokenType`. No changes needed unless special handling is required.

#### 3. Add Parser Support

**File**: `src/main/java/com/euclid/parser/Parser.java`

```java
private AstNode parsePrimary() throws ParserException {
    // ... existing code ...
    
    case COSH:
        return parseFunctionCall("cosh", 1);
    
    // ... rest of code ...
}
```

#### 4. Add Transpiler Support

**File**: `src/main/java/com/euclid/transpiler/LatexTranspiler.java`

```java
@Override
public String visit(CallExpr node) {
    switch (node.getFunctionName()) {
        case "cosh":
            return "\\cosh(" + transpile(node.getArguments().get(0)) + ")";
        
        // ... existing cases ...
    }
}
```

#### 5. Add Tests

**File**: `src/test/java/com/euclid/EuclidIntegrationTest.java`

```java
@Test
void shouldTranspileHyperbolicCosine() throws Exception {
    String result = transpile("cosh(x)");
    assertEquals("\\cosh(x)", result);
}
```

#### 6. Update Documentation

**File**: `doc/syntax.md`

Add `cosh(x)` to the function reference section.

### Adding a New Operator

Let's add `%` (modulo) as an example.

#### 1. Add Token Type

```java
MODULO("%"),
```

#### 2. Update Lexer

```java
case '%':
    addToken(TokenType.MODULO);
    break;
```

#### 3. Define Precedence in Parser

Modulo has same precedence as multiplication/division:

```java
private AstNode parseTerm() throws ParserException {
    AstNode expr = parseFactor();
    
    while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
        Token operator = previous();
        AstNode right = parseFactor();
        expr = new BinaryExpr(expr, operator.getLexeme(), right);
    }
    
    return expr;
}
```

#### 4. Add Transpiler Support

```java
case "%":
    return transpile(node.getLeft()) + " \\bmod " + transpile(node.getRight());
```

### Adding Complex Features

For features requiring multiple tokens (like `sum(i=1, n, f)`), you'll need to:

1. Parse the complex syntax in `parseFunctionCall()`
2. Store additional data in AST nodes (may need new node types)
3. Handle special transpilation logic

Example for summation bounds:
```java
case SUM:
    // Parse sum(variable=start, end, expression)
    consume(TokenType.LPAREN);
    AstNode variable = parseExpression();
    consume(TokenType.EQUALS);
    AstNode start = parseExpression();
    consume(TokenType.COMMA);
    AstNode end = parseExpression();
    consume(TokenType.COMMA);
    AstNode expr = parseExpression();
    consume(TokenType.RPAREN);
    
    return new SumExpr(variable, start, end, expr);
```

## Development Workflow

### Setting Up Development Environment

```bash
# Clone repository
git clone https://github.com/gongahkia/euclid.git
cd euclid

# Build project
mvn clean install

# Run tests
mvn test

# Run with coverage
mvn test jacoco:report

# Check code quality
mvn checkstyle:check
mvn spotbugs:check
```

### Running Locally

```bash
# Compile
mvn compile

# Run transpiler
mvn exec:java -Dexec.mainClass="com.euclid.Transpiler" \
  -Dexec.args="input.ed output.md"

# Run REPL
mvn exec:java -Dexec.mainClass="com.euclid.Repl"

# Create JAR
mvn package
java -jar target/euclid-transpiler.jar input.ed output.md
```

### Debugging

**Enable detailed logging** in tests:
```java
System.out.println("Tokens: " + lexer.tokenize());
System.out.println("AST: " + parser.parse(tokens));
```

**Common issues**:
- **Token mismatches**: Check `TokenType.java` and lexer
- **Parsing errors**: Verify operator precedence and grammar rules
- **Transpilation issues**: Debug AST structure first, then visitor logic

### Code Style

Follow the Google Java Style Guide (enforced by Checkstyle):

- Indentation: 2 spaces (in Google style) or 4 spaces
- Line length: 100 characters max
- Naming: `camelCase` for methods/variables, `PascalCase` for classes
- Javadoc: Required for all public methods

**Pre-commit checklist**:
```bash
mvn clean test                    # All tests pass
mvn checkstyle:check              # Style compliance
mvn spotbugs:check                # No bugs detected
git status                        # Only intended files staged
git diff --staged                 # Review changes
```

## Testing Strategy

### Test Pyramid

1. **Unit Tests**: Test individual methods in isolation
2. **Integration Tests**: Test full pipeline (Lexer → Parser → Transpiler)
3. **Regression Tests**: Ensure fixed bugs stay fixed

### Current Test Coverage

- **EuclidIntegrationTest.java**: 10 end-to-end tests
- Coverage: ~50% (target: 70%+)

### Writing Good Tests

```java
@Test
void shouldDescribeWhatIsBeingTested() throws Exception {
    // Arrange: Set up test data
    String input = "x^2 + 1";
    
    // Act: Execute the code under test
    String result = transpile(input);
    
    // Assert: Verify expected behavior
    assertEquals("x^{2} + 1", result);
}
```

### Test Naming Convention

`should<Action>When<Condition>`

Examples:
- `shouldTranspileFractionsWhenGivenDivision`
- `shouldThrowExceptionWhenUnbalancedParentheses`
- `shouldHandleNestedFunctionCalls`

### Running Specific Tests

```bash
# Run single test class
mvn test -Dtest=EuclidIntegrationTest

# Run single test method
mvn test -Dtest=EuclidIntegrationTest#shouldTranspileAddition

# Run tests matching pattern
mvn test -Dtest="*Integration*"
```

## Performance Considerations

### Optimization Tips

1. **Token reuse**: Reuse Token objects when possible
2. **String concatenation**: Use `StringBuilder` for complex LaTeX generation
3. **AST caching**: Cache parsed ASTs for repeated expressions
4. **Lazy evaluation**: Only parse what's needed

### Benchmarking

```java
long start = System.nanoTime();
// ... code to benchmark ...
long duration = System.nanoTime() - start;
System.out.println("Time: " + duration / 1_000_000.0 + " ms");
```

## Common Pitfalls

### 1. Right-Associative Operators

Exponentiation is right-associative: `2^3^4 = 2^(3^4) = 2^81`

```java
// Correct implementation
private AstNode parseFactor() throws ParserException {
    AstNode expr = parseUnary();
    
    if (match(TokenType.POWER)) {
        Token operator = previous();
        AstNode right = parseFactor();  // Recursive call for right-associativity
        return new BinaryExpr(expr, operator.getLexeme(), right);
    }
    
    return expr;
}
```

### 2. Operator Precedence

Always test complex expressions:
```java
"x^2*3" → "(x^2) * 3" not "x^(2*3)"
"2*3^4" → "2 * (3^4)" not "(2*3)^4"
```

### 3. Token Look-Ahead

Sometimes you need to peek ahead without consuming:

```java
if (peek().getType() == TokenType.EQUALS) {
    // It's an assignment
} else {
    // It's a comparison
}
```

### 4. Error Recovery

Don't just throw exceptions—provide context:

```java
throw new ParserException(
    "Expected ')' but got '" + peek().getLexeme() + "'",
    peek().getLine()
);
```

## Contributing

See [CONTRIBUTING.md](../CONTRIBUTING.md) for:
- Pull request process
- Coding standards
- Git workflow
- Code review guidelines

## Resources

- [LaTeX Math Guide](https://en.wikibooks.org/wiki/LaTeX/Mathematics)
- [Recursive Descent Parsing](https://en.wikipedia.org/wiki/Recursive_descent_parser)
- [Visitor Pattern](https://en.wikipedia.org/wiki/Visitor_pattern)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

## Getting Help

- **Issues**: [GitHub Issues](https://github.com/gongahkia/euclid/issues)
- **Discussions**: Use GitHub Discussions for questions
- **Slack/Discord**: (if available)

---

Happy coding! 🚀
