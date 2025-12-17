# Typesetting Proofs with Euclid

This tutorial covers logic symbols, quantifiers, set theory notation, and proof techniques in Euclid.

## Logic Symbols

### Basic Logic Operators

| Operation | Euclid | LaTeX |
|-----------|--------|-------|
| And (conjunction) | `AND(p, q)` or `p AND q` | `p \land q` |
| Or (disjunction) | `OR(p, q)` or `p OR q` | `p \lor q` |
| Not (negation) | `NOT(p)` | `\lnot p` |
| Implies | `implies(p, q)` | `p \implies q` |
| If and only if | `iff(p, q)` | `p \iff q` |

### Example: De Morgan's Laws

**Euclid:**
```euclid
NOT(p AND q) = NOT(p) OR NOT(q)
NOT(p OR q) = NOT(p) AND NOT(q)
```

**Rendered:**

$$\lnot (p \land q) = \lnot p \lor \lnot q$$
$$\lnot (p \lor q) = \lnot p \land \lnot q$$

### Example: Implication

**Euclid:**
```euclid
implies(p, q) = OR(NOT(p), q)
```

**Rendered:**

$$p \implies q = \lnot p \lor q$$

### Example: Logical Equivalence

**Euclid:**
```euclid
iff(p, q) = AND(implies(p, q), implies(q, p))
```

**Rendered:**

$$p \iff q = (p \implies q) \land (q \implies p)$$

## Quantifiers

### Universal Quantifier (For All)

**Euclid:**
```euclid
forall(x, P(x))
```

**Rendered:**

$$\forall x \, P(x)$$

### Existential Quantifier (There Exists)

**Euclid:**
```euclid
exists(x, P(x))
```

**Rendered:**

$$\exists x \, P(x)$$

### Example: Even Number Definition

**Euclid:**
```euclid
forall(n, exists(k, n = 2 * k))
```

**Rendered:**

$$\forall n \, \exists k \, (n = 2 \cdot k)$$

### Example: Unique Existence

**Euclid:**
```euclid
exists(x, P(x) AND forall(y, implies(P(y), y = x)))
```

**Rendered:**

$$\exists x \, (P(x) \land \forall y \, (P(y) \implies y = x))$$

### Example: Negation of Universal

**Euclid:**
```euclid
NOT(forall(x, P(x))) = exists(x, NOT(P(x)))
```

**Rendered:**

$$\lnot \forall x \, P(x) = \exists x \, \lnot P(x)$$

## Set Theory

### Basic Set Notation

| Operation | Euclid | LaTeX |
|-----------|--------|-------|
| Empty set | `emptyset` | `\emptyset` |
| Element of | `element_of(x, A)` | `x \in A` |
| Not element of | `NOT(element_of(x, A))` | `x \notin A` |
| Subset | `subset(A, B)` | `A \subseteq B` |
| Proper subset | `proper_subset(A, B)` | `A \subset B` |
| Union | `union(A, B)` | `A \cup B` |
| Intersection | `intersection(A, B)` | `A \cap B` |

### Example: Set Membership

**Euclid:**
```euclid
element_of(x, A)
```

**Rendered:**

$$x \in A$$

### Example: Subset Definition

**Euclid:**
```euclid
subset(A, B) = forall(x, implies(element_of(x, A), element_of(x, B)))
```

**Rendered:**

$$A \subseteq B = \forall x \, (x \in A \implies x \in B)$$

### Example: Set Equality

**Euclid:**
```euclid
A = B = AND(subset(A, B), subset(B, A))
```

**Rendered:**

$$A = B = (A \subseteq B) \land (B \subseteq A)$$

### Example: Union Definition

**Euclid:**
```euclid
element_of(x, union(A, B)) = OR(element_of(x, A), element_of(x, B))
```

**Rendered:**

$$x \in A \cup B = (x \in A) \lor (x \in B)$$

### Example: Intersection Definition

**Euclid:**
```euclid
element_of(x, intersection(A, B)) = AND(element_of(x, A), element_of(x, B))
```

**Rendered:**

$$x \in A \cap B = (x \in A) \land (x \in B)$$

## Comparison and Inequality

### Comparison Operators

| Operation | Euclid | LaTeX |
|-----------|--------|-------|
| Equals | `a = b` | `a = b` |
| Not equal | `neq(a, b)` | `a \neq b` |
| Less than | `lt(a, b)` | `a < b` |
| Greater than | `gt(a, b)` | `a > b` |
| Less or equal | `leq(a, b)` | `a \leq b` |
| Greater or equal | `geq(a, b)` | `a \geq b` |
| Approximately equal | `approx(a, b)` | `a \approx b` |
| Congruent | `equiv(a, b)` | `a \equiv b` |

### Example: Triangle Inequality

**Euclid:**
```euclid
leq(abs(a + b), abs(a) + abs(b))
```

**Rendered:**

$$\lvert a + b \rvert \leq \lvert a \rvert + \lvert b \rvert$$

### Example: Transitive Property

**Euclid:**
```euclid
AND(lt(a, b), lt(b, c)) implies lt(a, c)
```

**Rendered:**

$$(a < b \land b < c) \implies a < c$$

## Proof Structures

### Direct Proof Template

```euclid
# Claim: forall(x, implies(P(x), Q(x)))

# Assume P(x) for arbitrary x
# ... proof steps ...
# Therefore Q(x)
```

### Proof by Contradiction Template

```euclid
# Claim: P

# Assume NOT(P)
# ... derive contradiction ...
# Therefore P
```

### Proof by Cases Template

```euclid
# Claim: P

# Case 1: Assume A
# ... prove P ...

# Case 2: Assume NOT(A)  
# ... prove P ...

# In both cases P holds, therefore P
```

## Mathematical Proofs Examples

### Example: Square Root of 2 is Irrational (Setup)

**Euclid:**
```euclid
# Claim: NOT(exists(p, exists(q, AND(sqrt(2) = p \\ q, gcd(p, q) = 1))))

# Assume sqrt(2) = p \\ q where gcd(p, q) = 1
pow(sqrt(2), 2) = pow(p \\ q, 2)
2 = pow(p, 2) \\ pow(q, 2)
pow(p, 2) = 2 * pow(q, 2)

# Therefore p^2 is even, so p is even
# Let p = 2k for some k
pow(2 * k, 2) = 2 * pow(q, 2)
4 * pow(k, 2) = 2 * pow(q, 2)
pow(q, 2) = 2 * pow(k, 2)

# Therefore q^2 is even, so q is even
# But this contradicts gcd(p, q) = 1
# Therefore sqrt(2) is irrational
```

### Example: Sum of Angles in Triangle

**Euclid:**
```euclid
# Claim: forall(triangle, ALPHA + BETA + GAMMA = 180)
ALPHA + BETA + GAMMA = PI
```

**Rendered:**

$$\alpha + \beta + \gamma = \pi$$

### Example: Pythagorean Theorem

**Euclid:**
```euclid
# For right triangle with legs a, b and hypotenuse c:
pow(a, 2) + pow(b, 2) = pow(c, 2)
```

**Rendered:**

$$a^{2} + b^{2} = c^{2}$$

### Example: Fundamental Theorem of Arithmetic

**Euclid:**
```euclid
forall(n, gt(n, 1) implies exists(p1, exists(p2, n = prod(i, 1, k, pi))))
```

**Rendered:**

$$\forall n \, (n > 1 \implies \exists p_{1}, p_{2}, \ldots \, (n = \prod_{i=1}^{k} p_{i}))$$

## Number Theory

### Divisibility

**Euclid:**
```euclid
# a divides b
exists(k, b = k * a)
```

**Rendered:**

$$\exists k \, (b = k \cdot a)$$

### Modular Arithmetic

**Euclid:**
```euclid
equiv(a, b) mod n
mod(a - b, n) = 0
```

**Rendered:**

$$a \equiv b \pmod{n}$$
$$\text{mod}(a - b, n) = 0$$

### GCD Property

**Euclid:**
```euclid
gcd(a, b) * lcm(a, b) = a * b
```

**Rendered:**

$$\gcd(a, b) \cdot \text{lcm}(a, b) = a \cdot b$$

## Real Analysis

### Limit Definition (Epsilon-Delta)

**Euclid:**
```euclid
limit(f(x), x, a) = L = forall(EPSILON, gt(EPSILON, 0) implies exists(DELTA, gt(DELTA, 0) AND forall(x, AND(gt(abs(x - a), 0), lt(abs(x - a), DELTA)) implies lt(abs(f(x) - L), EPSILON))))
```

**Rendered:**

$$\lim_{x \to a} f(x) = L = \forall \epsilon \, (\epsilon > 0 \implies \exists \delta \, (\delta > 0 \land \forall x \, (0 < \lvert x - a \rvert < \delta \implies \lvert f(x) - L \rvert < \epsilon)))$$

### Continuity Definition

**Euclid:**
```euclid
# f is continuous at a:
forall(EPSILON, gt(EPSILON, 0) implies exists(DELTA, gt(DELTA, 0) AND forall(x, lt(abs(x - a), DELTA) implies lt(abs(f(x) - f(a)), EPSILON))))
```

### Convergence of Sequence

**Euclid:**
```euclid
# Sequence {a_n} converges to L:
forall(EPSILON, gt(EPSILON, 0) implies exists(N, forall(n, geq(n, N) implies lt(abs(a_n - L), EPSILON))))
```

## Complex Proofs Examples

### Example: Infinitude of Primes (Euclid's Proof)

```euclid
# Assume there are finitely many primes: p1, p2, ..., pk
# Let N = prod(i, 1, k, pi) + 1
N = p1 * p2 * ... * pk + 1

# N is not divisible by any pi
# Therefore N is prime or has a prime factor not in our list
# Contradiction - there are infinitely many primes
```

### Example: Cantor's Diagonalization

```euclid
# The set of real numbers in [0,1] is uncountable
# Assume countable: r1, r2, r3, ...
# Construct x differing from rn at nth decimal
# x NOT element_of {r1, r2, ...}
# Contradiction
```

### Example: Binomial Theorem

**Euclid:**
```euclid
pow(a + b, n) = sum(k, 0, n, binom(n, k) * pow(a, n - k) * pow(b, k))
```

Where `binom(n, k)` represents the binomial coefficient:

**Euclid:**
```euclid
binom(n, k) = (prod(i, 1, n, i)) \\ (prod(i, 1, k, i) * prod(i, 1, n - k, i))
```

## Practice Exercises

Try typesetting these proofs:

1. **Contrapositive:**
   ```euclid
   implies(p, q) = implies(NOT(q), NOT(p))
   ```

2. **Set difference:**
   ```euclid
   element_of(x, A - B) = AND(element_of(x, A), NOT(element_of(x, B)))
   ```

3. **Intermediate Value Theorem setup:**
   ```euclid
   AND(lt(f(a), 0), gt(f(b), 0)) implies exists(c, AND(lt(a, c), AND(lt(c, b), f(c) = 0)))
   ```

4. **Sum of first n integers:**
   ```euclid
   sum(i, 1, n, i) = (n * (n + 1)) \\ 2
   ```

5. **Rational number definition:**
   ```euclid
   element_of(x, Q) = exists(p, exists(q, AND(x = p \\ q, neq(q, 0))))
   ```

6. **Function composition:**
   ```euclid
   (f compose g)(x) = f(g(x))
   ```

## Tips for Proof Typesetting

1. **Use comments liberally** - Explain proof strategy and steps
2. **Break into cases** - Clearly mark different proof cases
3. **Show intermediate steps** - Don't skip algebra
4. **State assumptions** - Make it clear what you're assuming
5. **Mark conclusions** - Use "Therefore" or similar markers
6. **Indent nested quantifiers** - Improve readability
7. **Use meaningful variable names** - Choose names that reflect meaning

## Common Proof Patterns

### Universal Instantiation

```euclid
# From: forall(x, P(x))
# We can derive: P(a) for any specific a
```

### Existential Generalization

```euclid
# From: P(a) for some specific a
# We can derive: exists(x, P(x))
```

### Modus Ponens

```euclid
# From: p AND implies(p, q)
# We can derive: q
```

### Universal Modus Ponens

```euclid
# From: forall(x, implies(P(x), Q(x))) AND P(a)
# We can derive: Q(a)
```

## Next Steps

- **[Advanced Techniques](05_advanced.md)** - Complex expressions and best practices
- **[Getting Started](01_getting_started.md)** - Review basics
- **[Syntax Reference](../syntax.md)** - Complete function list

## Reference

For more logic and set theory functions, see the [syntax reference](../syntax.md).
