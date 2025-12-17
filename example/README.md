# Euclid Examples

This directory contains example `.ed` files demonstrating the Euclid language syntax.

## Examples

- **input.ed / output.md** - Original basic example with constants and operations
- **01_arithmetic.ed** - Basic arithmetic, algebra, constants, and Greek letters
- **02_calculus.ed** - Trigonometric functions, logarithms, derivatives, integrals, sums
- **03_linear_algebra.ed** - Vectors, matrices, and matrix operations
- **04_set_theory_logic.ed** - Set notation, logical operators, and quantifiers
- **05_real_world.ed** - Real-world applications in physics, statistics, geometry, finance

## Usage

To transpile any example:
```bash
# Using Maven
mvn clean package
java -jar target/euclid-1.0.0-transpiler.jar example/01_arithmetic.ed -o example/01_arithmetic.md

# Or using the Transpiler class directly
java -cp target/euclid-1.0.0-transpiler.jar com.euclid.Transpiler example/01_arithmetic.ed example/01_arithmetic.md
```

## Expected Output

Each `.ed` file will be transpiled to LaTeX-formatted mathematical expressions embedded in Markdown, which can then be rendered by any Markdown viewer that supports LaTeX (like GitHub, Obsidian, or with MathJax).
