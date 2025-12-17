# Writing Calculus in Euclid

This tutorial covers calculus operations in Euclid, including derivatives, integrals, limits, and trigonometric functions.

## Trigonometric Functions

Euclid supports all standard trigonometric functions:

### Basic Trig Functions

| Function | Euclid | LaTeX |
|----------|--------|-------|
| Sine | `sin(x)` | `\sin(x)` |
| Cosine | `cos(x)` | `\cos(x)` |
| Tangent | `tan(x)` | `\tan(x)` |
| Cosecant | `csc(x)` | `\csc(x)` |
| Secant | `sec(x)` | `\sec(x)` |
| Cotangent | `cot(x)` | `\cot(x)` |

### Example: Pythagorean Identity

**Euclid:**
```euclid
pow(sin(THETA), 2) + pow(cos(THETA), 2) = 1
```

**Rendered:**

$$\sin^{2}(\theta) + \cos^{2}(\theta) = 1$$

### Hyperbolic Functions

| Function | Euclid | LaTeX |
|----------|--------|-------|
| Hyperbolic sine | `sinh(x)` | `\sinh(x)` |
| Hyperbolic cosine | `cosh(x)` | `\cosh(x)` |
| Hyperbolic tangent | `tanh(x)` | `\tanh(x)` |
| Hyperbolic cosecant | `csch(x)` | `\text{csch}(x)` |
| Hyperbolic secant | `sech(x)` | `\text{sech}(x)` |
| Hyperbolic cotangent | `coth(x)` | `\coth(x)` |

### Example: Hyperbolic Identity

**Euclid:**
```euclid
pow(cosh(x), 2) - pow(sinh(x), 2) = 1
```

**Rendered:**

$$\cosh^{2}(x) - \sinh^{2}(x) = 1$$

## Logarithms and Exponentials

### Natural Logarithm

**Euclid:**
```euclid
ln(x)
```

**Rendered:**

$$\ln(x)$$

### Logarithm with Base

**Euclid:**
```euclid
log(x, 10)  # log base 10 of x
```

**Rendered:**

$$\log_{10}(x)$$

### Exponential Function

**Euclid:**
```euclid
exp(x)
```

**Rendered:**

$$e^{x}$$

Or equivalently:

**Euclid:**
```euclid
pow(E, x)
```

**Rendered:**

$$e^{x}$$

### Example: Logarithm Properties

**Euclid:**
```euclid
ln(a * b) = ln(a) + ln(b)
ln(a \\ b) = ln(a) - ln(b)
ln(pow(a, n)) = n * ln(a)
```

**Rendered:**

$$\ln(a \cdot b) = \ln(a) + \ln(b)$$
$$\ln(\frac{a}{b}) = \ln(a) - \ln(b)$$
$$\ln(a^{n}) = n \cdot \ln(a)$$

## Derivatives

### Basic Derivative Notation

**Euclid:**
```euclid
diff(f(x), x)
```

**Rendered:**

$$\frac{d}{dx} f(x)$$

### Partial Derivatives

**Euclid:**
```euclid
partial(f(x, y), x)
```

**Rendered:**

$$\frac{\partial}{\partial x} f(x, y)$$

### Example: Power Rule

**Euclid:**
```euclid
diff(pow(x, n), x) = n * pow(x, n - 1)
```

**Rendered:**

$$\frac{d}{dx} x^{n} = n \cdot x^{n - 1}$$

### Example: Product Rule

**Euclid:**
```euclid
diff(u(x) * v(x), x) = u(x) * diff(v(x), x) + v(x) * diff(u(x), x)
```

**Rendered:**

$$\frac{d}{dx} (u(x) \cdot v(x)) = u(x) \cdot \frac{d}{dx} v(x) + v(x) \cdot \frac{d}{dx} u(x)$$

### Example: Chain Rule

**Euclid:**
```euclid
diff(f(g(x)), x) = diff(f(u), u) * diff(g(x), x)
```

**Rendered:**

$$\frac{d}{dx} f(g(x)) = \frac{d}{du} f(u) \cdot \frac{d}{dx} g(x)$$

### Example: Derivative of Sine

**Euclid:**
```euclid
diff(sin(x), x) = cos(x)
```

**Rendered:**

$$\frac{d}{dx} \sin(x) = \cos(x)$$

## Integrals

### Indefinite Integral

**Euclid:**
```euclid
integral(f(x), x)
```

**Rendered:**

$$\int f(x) \, dx$$

### Definite Integral

**Euclid:**
```euclid
integral(f(x), x, a, b)
```

**Rendered:**

$$\int_{a}^{b} f(x) \, dx$$

### Example: Power Rule for Integration

**Euclid:**
```euclid
integral(pow(x, n), x) = (pow(x, n + 1)) \\ (n + 1) + C
```

**Rendered:**

$$\int x^{n} \, dx = \frac{x^{n + 1}}{n + 1} + C$$

### Example: Definite Integral

**Euclid:**
```euclid
integral(pow(x, 2), x, 0, 1) = 1 \\ 3
```

**Rendered:**

$$\int_{0}^{1} x^{2} \, dx = \frac{1}{3}$$

### Example: Area Under Sine Curve

**Euclid:**
```euclid
A = integral(sin(x), x, 0, PI)
```

**Rendered:**

$$A = \int_{0}^{\pi} \sin(x) \, dx$$

## Limits

### Basic Limit

**Euclid:**
```euclid
limit(f(x), x, a)
```

**Rendered:**

$$\lim_{x \to a} f(x)$$

### Limit at Infinity

**Euclid:**
```euclid
limit(f(x), x, INF)
```

**Rendered:**

$$\lim_{x \to \infty} f(x)$$

### Example: Derivative Definition

**Euclid:**
```euclid
diff(f(x), x) = limit((f(x + h) - f(x)) \\ h, h, 0)
```

**Rendered:**

$$\frac{d}{dx} f(x) = \lim_{h \to 0} \frac{f(x + h) - f(x)}{h}$$

### Example: L'Hôpital's Rule

**Euclid:**
```euclid
limit(f(x) \\ g(x), x, a) = limit(diff(f(x), x) \\ diff(g(x), x), x, a)
```

**Rendered:**

$$\lim_{x \to a} \frac{f(x)}{g(x)} = \lim_{x \to a} \frac{\frac{d}{dx} f(x)}{\frac{d}{dx} g(x)}$$

### Example: Exponential Limit

**Euclid:**
```euclid
E = limit(pow(1 + 1 \\ n, n), n, INF)
```

**Rendered:**

$$e = \lim_{n \to \infty} (1 + \frac{1}{n})^{n}$$

## Summation and Product Notation

### Summation

**Euclid:**
```euclid
sum(i, 1, n, f(i))
```

**Rendered:**

$$\sum_{i=1}^{n} f(i)$$

### Example: Sum of First n Natural Numbers

**Euclid:**
```euclid
sum(i, 1, n, i) = (n * (n + 1)) \\ 2
```

**Rendered:**

$$\sum_{i=1}^{n} i = \frac{n \cdot (n + 1)}{2}$$

### Example: Infinite Series

**Euclid:**
```euclid
sum(n, 0, INF, 1 \\ pow(2, n)) = 2
```

**Rendered:**

$$\sum_{n=0}^{\infty} \frac{1}{2^{n}} = 2$$

### Product Notation

**Euclid:**
```euclid
prod(i, 1, n, f(i))
```

**Rendered:**

$$\prod_{i=1}^{n} f(i)$$

### Example: Factorial

**Euclid:**
```euclid
n! = prod(i, 1, n, i)
```

**Rendered:**

$$n! = \prod_{i=1}^{n} i$$

## Complex Calculus Examples

### Example: Taylor Series for Exponential

**Euclid:**
```euclid
exp(x) = sum(n, 0, INF, (pow(x, n)) \\ prod(k, 1, n, k))
```

**Rendered:**

$$e^{x} = \sum_{n=0}^{\infty} \frac{x^{n}}{\prod_{k=1}^{n} k}$$

### Example: Fundamental Theorem of Calculus

**Euclid:**
```euclid
diff(integral(f(t), t, a, x), x) = f(x)
```

**Rendered:**

$$\frac{d}{dx} \int_{a}^{x} f(t) \, dt = f(x)$$

### Example: Integration by Parts

**Euclid:**
```euclid
integral(u * diff(v, x), x) = u * v - integral(v * diff(u, x), x)
```

**Rendered:**

$$\int u \cdot \frac{d}{dx} v \, dx = u \cdot v - \int v \cdot \frac{d}{dx} u \, dx$$

### Example: Gaussian Integral

**Euclid:**
```euclid
integral(exp(-pow(x, 2)), x, -INF, INF) = sqrt(PI)
```

**Rendered:**

$$\int_{-\infty}^{\infty} e^{-x^{2}} \, dx = \sqrt{\pi}$$

### Example: Wave Equation

**Euclid:**
```euclid
partial(partial(u, t), t) = pow(c, 2) * partial(partial(u, x), x)
```

**Rendered:**

$$\frac{\partial}{\partial t} \frac{\partial}{\partial t} u = c^{2} \cdot \frac{\partial}{\partial x} \frac{\partial}{\partial x} u$$

## Practice Exercises

Try transpiling these calculus expressions:

1. **Derivative of cosine:**
   ```euclid
   diff(cos(x), x) = -sin(x)
   ```

2. **Integral of exponential:**
   ```euclid
   integral(exp(x), x) = exp(x) + C
   ```

3. **Limit definition of e:**
   ```euclid
   E = limit(pow(1 + 1 \\ n, n), n, INF)
   ```

4. **Riemann sum:**
   ```euclid
   integral(f(x), x, a, b) = limit(sum(i, 1, n, f(xi) * DELTA_x), n, INF)
   ```

5. **Partial derivative:**
   ```euclid
   partial(pow(x, 2) * pow(y, 3), x) = 2 * x * pow(y, 3)
   ```

6. **Geometric series:**
   ```euclid
   sum(n, 0, INF, pow(r, n)) = 1 \\ (1 - r)
   ```

7. **Euler's formula (as limit):**
   ```euclid
   exp(i * THETA) = cos(THETA) + i * sin(THETA)
   ```

## Common Patterns

### Chain Rule Pattern

When differentiating composite functions, use this pattern:

```euclid
diff(f(g(x)), x) = diff(f(u), u) * diff(g(x), x)
```

### Integration Bounds

Always specify bounds in order: lower bound, then upper bound:

```euclid
integral(expression, variable, lower, upper)
```

### Summation/Product Index

Format: `sum(index, start, end, expression)`

The index variable should appear in the expression.

## Tips and Tricks

1. **Use parentheses liberally** - They make complex expressions clearer
2. **Break long expressions into multiple lines** - Use intermediate variables
3. **Comment your math** - Explain what each expression represents
4. **Check operator precedence** - Use parentheses if unsure

## Next Steps

- **[Linear Algebra with Euclid](03_linear_algebra.md)** - Learn vectors and matrices
- **[Typesetting Proofs](04_proofs.md)** - Master logic symbols
- **[Advanced Techniques](05_advanced.md)** - Complex expressions and best practices

## Reference

For a complete list of functions, see the [syntax reference](../syntax.md).
