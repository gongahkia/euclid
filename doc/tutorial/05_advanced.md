# Advanced Euclid Techniques

This tutorial covers complex expressions, best practices, debugging strategies, and advanced patterns for power users.

## Complex Nested Expressions

### Strategy: Build from Inside Out

When working with deeply nested expressions, build them incrementally:

**Euclid (Step by step):**
```euclid
# Step 1: Inner expression
inner = pow(x, 2) + 1

# Step 2: Intermediate expression  
middle = sqrt(inner)

# Step 3: Final expression
result = integral(middle, x, 0, 1)
```

**Euclid (All at once):**
```euclid
result = integral(sqrt(pow(x, 2) + 1), x, 0, 1)
```

**Rendered:**

$$\text{result} = \int_{0}^{1} \sqrt{x^{2} + 1} \, dx$$

### Using Parentheses for Clarity

**Without parentheses (ambiguous):**
```euclid
a + b * c + d
```

**With parentheses (clear):**
```euclid
(a + b) * (c + d)
```

**Rendered:**

$$(a + b) \cdot (c + d)$$

## Managing Long Expressions

### Break Across Multiple Lines with Comments

```euclid
# Quadratic formula: full derivation
# Start with ax^2 + bx + c = 0
pow(a * x, 2) + b * x + c = 0

# Subtract c from both sides
pow(a * x, 2) + b * x = -c

# Divide by a
pow(x, 2) + (b \\ a) * x = -c \\ a

# Complete the square
pow(x + b \\ (2 * a), 2) = (pow(b, 2) - 4 * a * c) \\ (4 * pow(a, 2))

# Take square root
x + b \\ (2 * a) = pm(sqrt((pow(b, 2) - 4 * a * c) \\ (4 * pow(a, 2))))

# Simplify
x = (-b pm sqrt(pow(b, 2) - 4 * a * c)) \\ (2 * a)
```

### Use Intermediate Variables

Instead of:

```euclid
f = integral(pow(E, -pow(x, 2)) * sqrt(2 * PI), x, -INF, INF) \\ integral(pow(E, -pow(x, 2)), x, -INF, INF)
```

Use:

```euclid
# Numerator: normal distribution
numerator = integral(pow(E, -pow(x, 2)) * sqrt(2 * PI), x, -INF, INF)

# Denominator: Gaussian integral
denominator = integral(pow(E, -pow(x, 2)), x, -INF, INF)

# Final result
f = numerator \\ denominator
```

## Operator Precedence

Euclid follows standard mathematical precedence:

1. **Function calls**: `pow(x, 2)`, `sin(x)`
2. **Multiplication/Division**: `*`, `\\`
3. **Addition/Subtraction**: `+`, `-`
4. **Comparison**: `lt`, `gt`, `leq`, `geq`, `=`, `neq`
5. **Logic**: `AND`, `OR`, `NOT`

### Examples

**Euclid:**
```euclid
2 + 3 * 4  # Equals 2 + (3 * 4) = 14
```

**Euclid:**
```euclid
pow(x, 2) + 2 * x + 1  # Equals (x^2) + (2x) + 1
```

**Euclid:**
```euclid
a * b \\ c * d  # Equals (a * b) / (c * d)
```

Always use parentheses when in doubt!

## Common Pitfalls and Solutions

### Pitfall 1: Division vs Fraction

**Wrong:**
```euclid
a / b  # Not supported!
```

**Right:**
```euclid
a \\ b  # Backslash-backslash
```

### Pitfall 2: Exponent Syntax

**Wrong:**
```euclid
x^2  # Not supported!
```

**Right:**
```euclid
pow(x, 2)
```

### Pitfall 3: Implicit Multiplication

**Wrong:**
```euclid
2x  # Not recognized!
```

**Right:**
```euclid
2 * x
```

### Pitfall 4: Function Argument Count

**Wrong:**
```euclid
pow(x)  # Missing argument!
```

**Right:**
```euclid
pow(x, 2)  # Two arguments required
```

### Pitfall 5: Mixing Operators

**Wrong:**
```euclid
x < y AND y < z  # Might not parse correctly
```

**Right:**
```euclid
lt(x, y) AND lt(y, z)  # Use function form for safety
```

## Debugging Strategies

### Strategy 1: Simplify the Expression

If you get an error, simplify to find the problem:

```euclid
# Original (error)
integral(sqrt(pow(sin(x), 2) + pow(cos(x), 2)), x, 0, PI)

# Simplify inner part
inner = pow(sin(x), 2) + pow(cos(x), 2)  # Test this first

# Then the sqrt
middle = sqrt(inner)  # Test this

# Finally the integral
result = integral(middle, x, 0, PI)  # Test this
```

### Strategy 2: Use the REPL

Test expressions interactively:

```bash
$ java -jar euclid-repl.jar

euclid> pow(x, 2)
LaTeX: x^{2}

euclid> pow(x, 2) + 2 * x + 1
LaTeX: x^{2} + 2 \cdot x + 1

euclid> :quit
```

### Strategy 3: Check Balanced Delimiters

Make sure parentheses, brackets, and braces match:

```euclid
# Unbalanced (error)
pow(x, 2 + 1  # Missing closing paren

# Balanced (correct)
pow(x, 2) + 1
```

### Strategy 4: Watch Mode for Live Feedback

Use watch mode to see changes instantly:

```bash
java -jar euclid-transpiler.jar --watch myfile.ed
```

Edit `myfile.ed` and see immediate transpilation results!

## Advanced Patterns

### Pattern 1: Piecewise Functions (Manual)

While Euclid doesn't have built-in piecewise syntax, you can document it:

```euclid
# Absolute value function:
# f(x) = x   if x >= 0
# f(x) = -x  if x < 0

abs(x) = x  // for geq(x, 0)
abs(x) = -x  // for lt(x, 0)
```

### Pattern 2: Function Composition

```euclid
# Composition: (f ∘ g)(x) = f(g(x))
(f compose g)(x) = f(g(x))

# Example: sqrt(x^2)
f(x) = sqrt(x)
g(x) = pow(x, 2)
(f compose g)(x) = sqrt(pow(x, 2)) = abs(x)
```

### Pattern 3: Parametric Equations

```euclid
# Circle parametrization
x(t) = r * cos(t)
y(t) = r * sin(t)

# For t element_of [0, 2*PI]
```

### Pattern 4: Recursive Definitions

```euclid
# Fibonacci sequence
F(0) = 0
F(1) = 1
F(n) = F(n - 1) + F(n - 2)  # for n >= 2
```

### Pattern 5: Summation Bounds with Conditions

```euclid
# Sum over even numbers
sum(i, 2, n, i)  # where i is even, step by 2

# Notation: sum of 2, 4, 6, ..., n
S = 2 + 4 + 6 + ... + n
```

## Performance Tips

### Tip 1: Simplify Before Transpiling

Complex expressions take longer to process:

```euclid
# Instead of repeating computations
pow(sin(x), 2) + pow(sin(x), 2) + pow(sin(x), 2)

# Define intermediate variable
s = pow(sin(x), 2)
result = s + s + s  # or 3 * s
```

### Tip 2: Use Comments Wisely

Comments don't affect performance but improve readability:

```euclid
# Good: explain what the expression represents
E = m * pow(c, 2)  # Einstein's mass-energy equivalence

# Avoid: stating the obvious
x = x + 1  # Add 1 to x
```

### Tip 3: File Organization

For large documents, split into multiple files:

```
project/
  intro.ed        # Introduction and setup
  chapter1.ed     # Chapter 1 equations
  chapter2.ed     # Chapter 2 equations
  appendix.ed     # Appendix derivations
```

Transpile each separately:

```bash
java -jar euclid-transpiler.jar intro.ed
java -jar euclid-transpiler.jar chapter1.ed
# etc.
```

## Style Guidelines

### Naming Conventions

**Variables:**
- Use descriptive names: `velocity`, `acceleration`
- Use subscripts for indices: `x1`, `x2`, `x_initial`
- Use Greek letters for angles: `THETA`, `PHI`

**Constants:**
- Use uppercase: `PI`, `E`, `INF`
- Use descriptive names: `SPEED_OF_LIGHT`, `GRAVITY`

### Whitespace

**Good (readable):**
```euclid
a * b + c * d
```

**Also good (very clear):**
```euclid
(a * b) + (c * d)
```

**Poor (cramped):**
```euclid
a*b+c*d
```

### Comment Style

**Good:**
```euclid
# Calculate the discriminant of the quadratic equation
discriminant = pow(b, 2) - 4 * a * c
```

**Good:**
```euclid
pow(a, 2) + pow(b, 2) = pow(c, 2)  // Pythagorean theorem
```

**Avoid excessive comments:**
```euclid
# x squared
pow(x, 2)  # raise x to power 2
```

## Real-World Example: Physics Problem

Let's typeset a complete physics derivation:

```euclid
# Projectile Motion: Derive maximum height

# Initial conditions
# v0 = initial velocity
# THETA = launch angle
# g = gravitational acceleration

# Vertical component of initial velocity
v_y0 = v0 * sin(THETA)

# Horizontal component
v_x0 = v0 * cos(THETA)

# Vertical velocity at time t
v_y(t) = v_y0 - g * t

# At maximum height, v_y = 0
0 = v_y0 - g * t_max
t_max = v_y0 \\ g

# Maximum height using h = v0*t - (1/2)*g*t^2
h_max = v_y0 * t_max - (1 \\ 2) * g * pow(t_max, 2)

# Substitute t_max
h_max = v_y0 * (v_y0 \\ g) - (1 \\ 2) * g * pow(v_y0 \\ g, 2)

# Simplify
h_max = pow(v_y0, 2) \\ g - (1 \\ 2) * pow(v_y0, 2) \\ g

h_max = (1 \\ 2) * pow(v_y0, 2) \\ g

# Final formula
h_max = (pow(v0 * sin(THETA), 2)) \\ (2 * g)
```

## Real-World Example: Statistics

```euclid
# Normal Distribution

# Probability density function
f(x) = (1 \\ sqrt(2 * PI * pow(SIGMA, 2))) * exp(-(pow(x - MU, 2) \\ (2 * pow(SIGMA, 2))))

# Standard normal (MU = 0, SIGMA = 1)
PHI(x) = (1 \\ sqrt(2 * PI)) * exp(-(pow(x, 2) \\ 2))

# Cumulative distribution function (integral)
CAPITAL_PHI(x) = integral(PHI(t), t, -INF, x)

# 68-95-99.7 rule
# P(MU - SIGMA <= X <= MU + SIGMA) = 0.68
# P(MU - 2*SIGMA <= X <= MU + 2*SIGMA) = 0.95
# P(MU - 3*SIGMA <= X <= MU + 3*SIGMA) = 0.997
```

## Workflow Best Practices

### Development Workflow

1. **Draft in REPL** - Test expressions interactively
2. **Save to file** - Once working, save to `.ed` file
3. **Use watch mode** - Edit with live feedback
4. **Version control** - Track changes with git
5. **Document** - Add comments explaining context

### Collaboration Tips

1. **Use meaningful filenames** - `project_calculus.ed`, not `file1.ed`
2. **Add header comments** - Explain document purpose
3. **Consistent style** - Agree on naming and formatting
4. **Share conventions** - Document project-specific notation

### Integration with LaTeX

Euclid generates LaTeX snippets. To use in a full document:

**document.tex:**
```latex
\documentclass{article}
\usepackage{amsmath}
\usepackage{amssymb}

\begin{document}

\section{My Equation}

% Paste transpiled LaTeX here
$$x^{2} + 2 \cdot x + 1 = 0$$

\end{document}
```

## Troubleshooting Checklist

When you encounter errors:

- [ ] Are all parentheses balanced?
- [ ] Are you using `\\` for fractions (not `/`)?
- [ ] Are you using `pow(x, 2)` for exponents (not `x^2`)?
- [ ] Do all functions have the correct number of arguments?
- [ ] Are function names spelled correctly?
- [ ] Are you using `*` for multiplication (not implicit)?
- [ ] Did you test in the REPL first?
- [ ] Does a simpler version work?

## Practice Exercises

Try these advanced problems:

1. **Derive Euler's formula using Taylor series:**
   ```euclid
   exp(i * THETA) = sum(n, 0, INF, (pow(i * THETA, n)) \\ prod(k, 1, n, k))
   ```

2. **Fourier transform definition:**
   ```euclid
   F(OMEGA) = integral(f(t) * exp(-i * OMEGA * t), t, -INF, INF)
   ```

3. **Chain rule with multiple variables:**
   ```euclid
   partial(f(u(x, y), v(x, y)), x) = partial(f, u) * partial(u, x) + partial(f, v) * partial(v, x)
   ```

4. **Lagrange multiplier method:**
   ```euclid
   nabla f = LAMBDA * nabla g
   ```

5. **Green's theorem:**
   ```euclid
   integral(integral(partial(Q, x) - partial(P, y), x, y), D) = integral(P * dx + Q * dy, C)
   ```

## Additional Resources

- **[Getting Started](01_getting_started.md)** - Basics and fundamentals
- **[Calculus](02_calculus.md)** - Derivatives, integrals, limits
- **[Linear Algebra](03_linear_algebra.md)** - Vectors and matrices
- **[Proofs](04_proofs.md)** - Logic and set theory
- **[Syntax Reference](../syntax.md)** - Complete function reference
- **[Examples](../../example/)** - More code samples

## Tips for Power Users

1. **Memorize common patterns** - Speed up your workflow
2. **Create templates** - Save boilerplate for common tasks
3. **Use shell scripts** - Automate repetitive transpilation
4. **Integrate with editors** - Set up syntax highlighting if possible
5. **Learn LaTeX** - Understanding output helps debug issues
6. **Read source code** - Check how functions are implemented

## Final Thoughts

Euclid is designed to make mathematical typesetting accessible and readable. The best way to master it is through practice. Start with simple expressions and gradually build complexity.

Remember:
- **Clarity over brevity** - Readable code is maintainable code
- **Test incrementally** - Build complex expressions step by step
- **Comment generously** - Your future self will thank you
- **Use the REPL** - Interactive testing catches errors early
- **Don't be afraid to experiment** - Try new patterns and techniques

Happy mathing with Euclid! 🎓✨
