# Getting Started with Euclid

Welcome to Euclid! This tutorial will help you write your first mathematical expressions using Euclid's intuitive syntax.

## What is Euclid?

Euclid is a readable mathematical notation language that transpiles to LaTeX. Instead of wrestling with LaTeX's verbose syntax, you can write natural mathematical expressions that are easy to read and maintain.

## Your First Expression

Let's start with a simple expression. In LaTeX, you'd write:

```latex
$$x^2 + 2x + 1$$
```

In Euclid, you write:

```euclid
pow(x, 2) + 2 * x + 1
```

That's it! Euclid handles the LaTeX formatting for you.

## Basic Arithmetic

Euclid supports all standard arithmetic operations:

| Operation | Euclid Syntax | LaTeX Output |
|-----------|---------------|--------------|
| Addition | `a + b` | `a + b` |
| Subtraction | `a - b` | `a - b` |
| Multiplication | `a * b` | `a \cdot b` |
| Division (fraction) | `a \\ b` | `\frac{a}{b}` |
| Exponentiation | `pow(a, b)` | `a^{b}` |
| Square root | `sqrt(x)` | `\sqrt{x}` |
| Nth root | `sqrt(3, x)` | `\sqrt[3]{x}` |

### Example: Quadratic Formula

**Euclid:**
```euclid
x = (-b + sqrt(pow(b, 2) - 4 * a * c)) \\ (2 * a)
```

**LaTeX Output:**
```latex
x = \frac{-b + \sqrt{b^{2} - 4 \cdot a \cdot c}}{2 \cdot a}
```

**Rendered:**

$$x = \frac{-b + \sqrt{b^{2} - 4 \cdot a \cdot c}}{2 \cdot a}$$

## Mathematical Constants

Euclid recognizes common mathematical constants:

| Constant | Euclid | LaTeX |
|----------|--------|-------|
| Pi | `PI` | `\pi` |
| Euler's number | `E` | `e` |
| Infinity | `INF` | `\infty` |
| Empty set | `emptyset` | `\emptyset` |

### Example: Circle Area

**Euclid:**
```euclid
A = PI * pow(r, 2)
```

**LaTeX Output:**
```latex
A = \pi \cdot r^{2}
```

**Rendered:**

$$A = \pi \cdot r^{2}$$

## Greek Letters

Use Greek letter names directly:

| Greek Letter | Euclid | LaTeX |
|--------------|--------|-------|
| Alpha | `ALPHA` | `\alpha` |
| Beta | `BETA` | `\beta` |
| Theta | `THETA` | `\theta` |
| Lambda | `LAMBDA` | `\lambda` |
| Sigma | `SIGMA` | `\sigma` |
| Omega | `OMEGA` | `\omega` |

### Example: Wavelength Formula

**Euclid:**
```euclid
LAMBDA = c \\ f
```

**LaTeX Output:**
```latex
\lambda = \frac{c}{f}
```

**Rendered:**

$$\lambda = \frac{c}{f}$$

## Comparison and Equality

| Operation | Euclid | LaTeX |
|-----------|--------|-------|
| Equals | `a = b` | `a = b` |
| Not equal | `neq(a, b)` | `a \neq b` |
| Less than | `lt(a, b)` | `a < b` |
| Greater than | `gt(a, b)` | `a > b` |
| Less or equal | `leq(a, b)` | `a \leq b` |
| Greater or equal | `geq(a, b)` | `a \geq b` |
| Approximately | `approx(a, b)` | `a \approx b` |

### Example: Inequality

**Euclid:**
```euclid
leq(0, x) AND lt(x, 1)
```

**LaTeX Output:**
```latex
0 \leq x \land x < 1
```

**Rendered:**

$$0 \leq x \land x < 1$$

## Absolute Value and Rounding

| Function | Euclid | LaTeX |
|----------|--------|-------|
| Absolute value | `abs(x)` | `\lvert x \rvert` |
| Floor | `floor(x)` | `\lfloor x \rfloor` |
| Ceiling | `ceil(x)` | `\lceil x \rceil` |

### Example: Distance Formula

**Euclid:**
```euclid
d = sqrt(pow(abs(x2 - x1), 2) + pow(abs(y2 - y1), 2))
```

**LaTeX Output:**
```latex
d = \sqrt{\lvert x_{2} - x_{1} \rvert^{2} + \lvert y_{2} - y_{1} \rvert^{2}}
```

**Rendered:**

$$d = \sqrt{\lvert x_{2} - x_{1} \rvert^{2} + \lvert y_{2} - y_{1} \rvert^{2}}$$

## Subscripts and Indices

Variables with numbers or underscores automatically become subscripts:

**Euclid:**
```euclid
x1 + x2 = x_sum
```

**LaTeX Output:**
```latex
x_{1} + x_{2} = x_{sum}
```

**Rendered:**

$$x_{1} + x_{2} = x_{sum}$$

## Using the Transpiler

### Command Line

Save your Euclid code to a file (e.g., `equation.ed`):

```euclid
# My first equation
E = m * pow(c, 2)
```

Then transpile it:

```bash
java -jar euclid-transpiler.jar equation.ed
```

This creates `equation_output.md` with your LaTeX code.

### REPL (Interactive Mode)

Start the REPL:

```bash
java -jar euclid-repl.jar
```

Type expressions and see immediate LaTeX output:

```
Euclid REPL v2.0 (Java)
Type :help for help, :quit to exit

euclid> pow(a, 2) + pow(b, 2) = pow(c, 2)
LaTeX: a^{2} + b^{2} = c^{2}

euclid> :quit
Goodbye!
```

### Watch Mode

Automatically retranspile when your file changes:

```bash
java -jar euclid-transpiler.jar --watch equation.ed
```

Edit `equation.ed` in your favorite editor and Euclid will retranspile automatically!

## Comments

Add comments to your Euclid code:

```euclid
# This is a comment
PI * pow(r, 2)  // Circle area

// Another comment style
E = m * pow(c, 2)
```

Comments are stripped during transpilation and don't appear in the output.

## Common Mistakes

### 1. Using `^` for exponents

**Wrong:**
```euclid
x^2  # This doesn't work!
```

**Right:**
```euclid
pow(x, 2)
```

### 2. Using `/` for fractions

**Wrong:**
```euclid
a / b  # This doesn't work!
```

**Right:**
```euclid
a \\ b
```

### 3. Forgetting parentheses in function calls

**Wrong:**
```euclid
sqrt x  # Missing parentheses
```

**Right:**
```euclid
sqrt(x)
```

## Practice Exercises

Try transpiling these expressions:

1. **Pythagorean theorem:**
   ```euclid
   pow(a, 2) + pow(b, 2) = pow(c, 2)
   ```

2. **Einstein's mass-energy equivalence:**
   ```euclid
   E = m * pow(c, 2)
   ```

3. **Distance between two points:**
   ```euclid
   d = sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2))
   ```

4. **Circumference of a circle:**
   ```euclid
   C = 2 * PI * r
   ```

5. **Quadratic formula (both solutions):**
   ```euclid
   x1 = (-b + sqrt(pow(b, 2) - 4 * a * c)) \\ (2 * a)
   x2 = (-b - sqrt(pow(b, 2) - 4 * a * c)) \\ (2 * a)
   ```

## Next Steps

Now that you know the basics, check out these tutorials:

- **[Calculus with Euclid](02_calculus.md)** - Learn derivatives, integrals, and limits
- **[Linear Algebra with Euclid](03_linear_algebra.md)** - Master vectors and matrices
- **[Typesetting Proofs](04_proofs.md)** - Use logic symbols and quantifiers
- **[Advanced Techniques](05_advanced.md)** - Complex expressions and best practices

## Need Help?

- Check the [syntax reference](../syntax.md) for a complete list of functions
- Look at the [examples directory](../../example/) for more code samples
- Read the [FAQ](../FAQ.md) for common questions
- Report bugs on [GitHub Issues](https://github.com/yourusername/euclid/issues)

Happy mathing! 🎓
