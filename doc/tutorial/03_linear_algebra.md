# Linear Algebra with Euclid

This tutorial covers vectors, matrices, and linear algebra operations in Euclid.

## Vectors

### Creating Vectors

Use the `vector()` function with a list of components:

**Euclid:**
```euclid
vector([1, 2, 3])
```

**Rendered:**

$$\begin{bmatrix} 1 \\ 2 \\ 3 \end{bmatrix}$$

### Vector Examples

#### 2D Vector

**Euclid:**
```euclid
v = vector([x, y])
```

**Rendered:**

$$v = \begin{bmatrix} x \\ y \end{bmatrix}$$

#### 3D Position Vector

**Euclid:**
```euclid
r = vector([x, y, z])
```

**Rendered:**

$$r = \begin{bmatrix} x \\ y \\ z \end{bmatrix}$$

#### Unit Vectors

**Euclid:**
```euclid
i = vector([1, 0, 0])
j = vector([0, 1, 0])
k = vector([0, 0, 1])
```

**Rendered:**

$$\hat{i} = \begin{bmatrix} 1 \\ 0 \\ 0 \end{bmatrix}$$
$$\hat{j} = \begin{bmatrix} 0 \\ 1 \\ 0 \end{bmatrix}$$
$$\hat{k} = \begin{bmatrix} 0 \\ 0 \\ 1 \end{bmatrix}$$

## Matrices

### Creating Matrices

Use the `matrix()` function with nested lists (rows):

**Euclid:**
```euclid
matrix([[a, b], [c, d]])
```

**Rendered:**

$$\begin{bmatrix} a & b \\ c & d \end{bmatrix}$$

### Matrix Examples

#### 2×2 Matrix

**Euclid:**
```euclid
A = matrix([[1, 2], [3, 4]])
```

**Rendered:**

$$A = \begin{bmatrix} 1 & 2 \\ 3 & 4 \end{bmatrix}$$

#### 3×3 Identity Matrix

**Euclid:**
```euclid
I = matrix([[1, 0, 0], [0, 1, 0], [0, 0, 1]])
```

**Rendered:**

$$I = \begin{bmatrix} 1 & 0 & 0 \\ 0 & 1 & 0 \\ 0 & 0 & 1 \end{bmatrix}$$

#### Rotation Matrix

**Euclid:**
```euclid
R = matrix([[cos(THETA), -sin(THETA)], [sin(THETA), cos(THETA)]])
```

**Rendered:**

$$R = \begin{bmatrix} \cos(\theta) & -\sin(\theta) \\ \sin(\theta) & \cos(\theta) \end{bmatrix}$$

#### Rectangular Matrix

**Euclid:**
```euclid
B = matrix([[1, 2, 3], [4, 5, 6]])
```

**Rendered:**

$$B = \begin{bmatrix} 1 & 2 & 3 \\ 4 & 5 & 6 \end{bmatrix}$$

## Vector Operations

### Vector Addition

**Euclid:**
```euclid
vector([a1, b1]) + vector([a2, b2]) = vector([a1 + a2, b1 + b2])
```

**Rendered:**

$$\begin{bmatrix} a_{1} \\ b_{1} \end{bmatrix} + \begin{bmatrix} a_{2} \\ b_{2} \end{bmatrix} = \begin{bmatrix} a_{1} + a_{2} \\ b_{1} + b_{2} \end{bmatrix}$$

### Scalar Multiplication

**Euclid:**
```euclid
c * vector([x, y, z]) = vector([c * x, c * y, c * z])
```

**Rendered:**

$$c \cdot \begin{bmatrix} x \\ y \\ z \end{bmatrix} = \begin{bmatrix} c \cdot x \\ c \cdot y \\ c \cdot z \end{bmatrix}$$

### Dot Product

The dot product isn't a built-in function, but you can write it explicitly:

**Euclid:**
```euclid
u dot v = u1 * v1 + u2 * v2 + u3 * v3
```

**Rendered:**

$$\vec{u} \cdot \vec{v} = u_{1} \cdot v_{1} + u_{2} \cdot v_{2} + u_{3} \cdot v_{3}$$

### Vector Magnitude

**Euclid:**
```euclid
abs(v) = sqrt(pow(v1, 2) + pow(v2, 2) + pow(v3, 2))
```

**Rendered:**

$$\lvert v \rvert = \sqrt{v_{1}^{2} + v_{2}^{2} + v_{3}^{2}}$$

### Unit Vector

**Euclid:**
```euclid
hat_v = v \\ abs(v)
```

**Rendered:**

$$\hat{v} = \frac{v}{\lvert v \rvert}$$

## Matrix Operations

### Matrix Addition

**Euclid:**
```euclid
matrix([[a, b], [c, d]]) + matrix([[e, f], [g, h]]) = matrix([[a + e, b + f], [c + g, d + h]])
```

**Rendered:**

$$\begin{bmatrix} a & b \\ c & d \end{bmatrix} + \begin{bmatrix} e & f \\ g & h \end{bmatrix} = \begin{bmatrix} a + e & b + f \\ c + g & d + h \end{bmatrix}$$

### Scalar Multiplication

**Euclid:**
```euclid
k * matrix([[a, b], [c, d]]) = matrix([[k * a, k * b], [k * c, k * d]])
```

**Rendered:**

$$k \cdot \begin{bmatrix} a & b \\ c & d \end{bmatrix} = \begin{bmatrix} k \cdot a & k \cdot b \\ k \cdot c & k \cdot d \end{bmatrix}$$

### Matrix Multiplication

You can write out matrix multiplication explicitly:

**Euclid:**
```euclid
A * B = matrix([[a * e + b * g, a * f + b * h], [c * e + d * g, c * f + d * h]])
```

For clarity, show the matrices being multiplied:

**Euclid:**
```euclid
matrix([[a, b], [c, d]]) * matrix([[e, f], [g, h]])
```

**Rendered:**

$$\begin{bmatrix} a & b \\ c & d \end{bmatrix} \cdot \begin{bmatrix} e & f \\ g & h \end{bmatrix}$$

## Determinants

Write determinants using the `abs()` function notation or explicitly:

### 2×2 Determinant

**Euclid:**
```euclid
det(A) = a * d - b * c
```

Or show the matrix:

**Euclid:**
```euclid
abs(matrix([[a, b], [c, d]])) = a * d - b * c
```

**Rendered:**

$$\lvert \begin{bmatrix} a & b \\ c & d \end{bmatrix} \rvert = a \cdot d - b \cdot c$$

### 3×3 Determinant (Cofactor Expansion)

**Euclid:**
```euclid
det(A) = a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g)
```

**Rendered:**

$$\text{det}(A) = a \cdot (e \cdot i - f \cdot h) - b \cdot (d \cdot i - f \cdot g) + c \cdot (d \cdot h - e \cdot g)$$

## Systems of Linear Equations

### Augmented Matrix

**Euclid:**
```euclid
matrix([[2, 1, 5], [3, -1, 4]])
```

**Rendered:**

$$\begin{bmatrix} 2 & 1 & 5 \\ 3 & -1 & 4 \end{bmatrix}$$

This represents:
- 2x + y = 5
- 3x - y = 4

### Matrix Form

**Euclid:**
```euclid
A * x = b
```

Where:

**Euclid:**
```euclid
matrix([[a, b], [c, d]]) * vector([x, y]) = vector([e, f])
```

**Rendered:**

$$\begin{bmatrix} a & b \\ c & d \end{bmatrix} \cdot \begin{bmatrix} x \\ y \end{bmatrix} = \begin{bmatrix} e \\ f \end{bmatrix}$$

## Eigenvalues and Eigenvectors

### Eigenvalue Equation

**Euclid:**
```euclid
A * v = LAMBDA * v
```

**Rendered:**

$$A \cdot v = \lambda \cdot v$$

### Characteristic Polynomial

**Euclid:**
```euclid
det(A - LAMBDA * I) = 0
```

**Rendered:**

$$\text{det}(A - \lambda \cdot I) = 0$$

### Example: 2×2 Eigenvalue Problem

**Euclid:**
```euclid
matrix([[a - LAMBDA, b], [c, d - LAMBDA]])
```

**Rendered:**

$$\begin{bmatrix} a - \lambda & b \\ c & d - \lambda \end{bmatrix}$$

## Linear Transformations

### General Linear Transformation

**Euclid:**
```euclid
T(v) = A * v
```

**Rendered:**

$$T(v) = A \cdot v$$

### Rotation in 2D

**Euclid:**
```euclid
matrix([[cos(THETA), -sin(THETA)], [sin(THETA), cos(THETA)]]) * vector([x, y])
```

**Rendered:**

$$\begin{bmatrix} \cos(\theta) & -\sin(\theta) \\ \sin(\theta) & \cos(\theta) \end{bmatrix} \cdot \begin{bmatrix} x \\ y \end{bmatrix}$$

### Scaling Transformation

**Euclid:**
```euclid
matrix([[sx, 0], [0, sy]]) * vector([x, y]) = vector([sx * x, sy * y])
```

**Rendered:**

$$\begin{bmatrix} s_{x} & 0 \\ 0 & s_{y} \end{bmatrix} \cdot \begin{bmatrix} x \\ y \end{bmatrix} = \begin{bmatrix} s_{x} \cdot x \\ s_{y} \cdot y \end{bmatrix}$$

### Reflection Across x-axis

**Euclid:**
```euclid
matrix([[1, 0], [0, -1]]) * vector([x, y]) = vector([x, -y])
```

**Rendered:**

$$\begin{bmatrix} 1 & 0 \\ 0 & -1 \end{bmatrix} \cdot \begin{bmatrix} x \\ y \end{bmatrix} = \begin{bmatrix} x \\ -y \end{bmatrix}$$

## Practical Examples

### Example: Distance Between Points

**Euclid:**
```euclid
d = abs(vector([x2, y2]) - vector([x1, y1]))
d = sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2))
```

**Rendered:**

$$d = \lvert \begin{bmatrix} x_{2} \\ y_{2} \end{bmatrix} - \begin{bmatrix} x_{1} \\ y_{1} \end{bmatrix} \rvert$$
$$d = \sqrt{(x_{2} - x_{1})^{2} + (y_{2} - y_{1})^{2}}$$

### Example: Projection of u onto v

**Euclid:**
```euclid
proj_v(u) = ((u dot v) \\ (v dot v)) * v
```

**Rendered:**

$$\text{proj}_{v}(u) = \frac{u \cdot v}{v \cdot v} \cdot v$$

### Example: Cross Product Magnitude

**Euclid:**
```euclid
abs(u times v) = abs(u) * abs(v) * sin(THETA)
```

**Rendered:**

$$\lvert u \times v \rvert = \lvert u \rvert \cdot \lvert v \rvert \cdot \sin(\theta)$$

### Example: Gram-Schmidt Orthogonalization

**Euclid:**
```euclid
u1 = v1
u2 = v2 - ((v2 dot u1) \\ (u1 dot u1)) * u1
```

**Rendered:**

$$u_{1} = v_{1}$$
$$u_{2} = v_{2} - \frac{v_{2} \cdot u_{1}}{u_{1} \cdot u_{1}} \cdot u_{1}$$

## Practice Exercises

Try transpiling these linear algebra expressions:

1. **Vector addition:**
   ```euclid
   vector([1, 2]) + vector([3, 4]) = vector([4, 6])
   ```

2. **Dot product:**
   ```euclid
   vector([a, b]) dot vector([c, d]) = a * c + b * d
   ```

3. **Matrix-vector multiplication:**
   ```euclid
   matrix([[1, 2], [3, 4]]) * vector([x, y]) = vector([x + 2 * y, 3 * x + 4 * y])
   ```

4. **Identity property:**
   ```euclid
   I * v = v
   ```

5. **Determinant of 2×2:**
   ```euclid
   det(matrix([[a, b], [c, d]])) = a * d - b * c
   ```

6. **Eigenvalue equation:**
   ```euclid
   (A - LAMBDA * I) * v = 0
   ```

7. **Magnitude of 3D vector:**
   ```euclid
   abs(vector([x, y, z])) = sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2))
   ```

## Tips for Linear Algebra

1. **Use meaningful variable names** - Use subscripts like `v1`, `v2` for clarity
2. **Break complex operations** - Write intermediate steps for matrix multiplication
3. **Show dimensions** - Comment on matrix/vector sizes when not obvious
4. **Use zero/identity matrices** - Clearly show special matrices
5. **Label transformations** - Name transformation matrices (R for rotation, etc.)

## Common Patterns

### Column Vector

```euclid
v = vector([v1, v2, v3])
```

### Row Vector

Write as a 1×n matrix:

```euclid
v_transpose = matrix([[v1, v2, v3]])
```

### Matrix-Vector Product

Always write matrix first, then vector:

```euclid
A * v
```

### Diagonal Matrix

```euclid
D = matrix([[d1, 0, 0], [0, d2, 0], [0, 0, d3]])
```

## Next Steps

- **[Typesetting Proofs](04_proofs.md)** - Learn logic symbols and quantifiers
- **[Advanced Techniques](05_advanced.md)** - Complex expressions and best practices

## Reference

For a complete list of functions, see the [syntax reference](../syntax.md).
