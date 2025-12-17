# Docker Guide for Euclid

This guide explains how to use Euclid with Docker for reproducible builds and consistent environments.

## Quick Start

### Build the Docker Image

```bash
docker build -t euclid .
```

Or using docker-compose:

```bash
docker-compose build
```

### Run the REPL

```bash
docker run -it euclid java -jar /app/euclid-repl.jar
```

Or using docker-compose:

```bash
docker-compose run --rm repl
```

### Transpile a File

To transpile a file from your current directory:

```bash
docker run -v $(pwd):/workspace euclid java -jar /app/euclid-transpiler.jar /workspace/input.ed /workspace/output.md
```

Or using docker-compose:

```bash
docker-compose run --rm transpiler java -jar /app/euclid-transpiler.jar input.ed output.md
```

## Common Use Cases

### 1. Interactive REPL Session

```bash
docker run -it euclid java -jar /app/euclid-repl.jar
```

Inside the REPL:
```
>>> PI + E
LaTeX: \pi + e

>>> integral(sin(x), x, 0, PI)
LaTeX: \int_{0}^{\pi} \sin(x) \, dx

>>> :quit
```

### 2. Transpile Files with Watch Mode

```bash
docker run -v $(pwd):/workspace euclid java -jar /app/euclid-transpiler.jar --watch /workspace/example.ed
```

### 3. Verbose/Debug Mode

```bash
docker run -v $(pwd):/workspace euclid java -jar /app/euclid-transpiler.jar --verbose /workspace/example.ed
```

### 4. Inline Math Mode

```bash
docker run -v $(pwd):/workspace euclid java -jar /app/euclid-transpiler.jar --inline /workspace/example.ed /workspace/output.md
```

### 5. Display Math Mode

```bash
docker run -v $(pwd):/workspace euclid java -jar /app/euclid-transpiler.jar --display /workspace/example.ed /workspace/output.md
```

## Docker Compose Examples

### Run Tests

```bash
docker-compose run --rm transpiler mvn test
```

### Build JARs

```bash
docker-compose run --rm transpiler mvn package
```

### Benchmark Performance

```bash
docker-compose run --rm transpiler mvn test -Dtest=PerformanceBenchmark
```

## Image Details

- **Base Image**: `eclipse-temurin:21-jre-alpine`
- **Size**: ~200MB (runtime image)
- **Build Time**: ~2-3 minutes (first build, with caching)
- **Java Version**: 21
- **Maven Version**: 3.9

## Multi-Stage Build

The Dockerfile uses a multi-stage build:

1. **Builder Stage** (`maven:3.9-eclipse-temurin-21-alpine`):
   - Compiles Java source code
   - Runs tests
   - Packages JARs

2. **Runtime Stage** (`eclipse-temurin:21-jre-alpine`):
   - Minimal JRE (no JDK or Maven)
   - Only includes compiled JARs
   - Smaller image size

## Volume Mounts

When transpiling files, mount your working directory:

```bash
# Linux/macOS
docker run -v $(pwd):/workspace euclid ...

# Windows (PowerShell)
docker run -v ${PWD}:/workspace euclid ...

# Windows (CMD)
docker run -v %cd%:/workspace euclid ...
```

## Environment Variables

You can pass Java options:

```bash
docker run -e JAVA_OPTS="-Xmx512m" euclid java $JAVA_OPTS -jar /app/euclid-transpiler.jar input.ed
```

## Troubleshooting

### Permission Issues

If you encounter permission issues with output files:

```bash
# Run as your user (Linux/macOS)
docker run --user $(id -u):$(id -g) -v $(pwd):/workspace euclid ...
```

### Build Failures

If the build fails, try building without cache:

```bash
docker build --no-cache -t euclid .
```

### Container Won't Start

Check Docker logs:

```bash
docker logs euclid-transpiler
```

## Production Deployment

For production use, consider:

1. **Tag images with versions**:
   ```bash
   docker build -t euclid:1.0.0 .
   docker tag euclid:1.0.0 euclid:latest
   ```

2. **Push to a registry**:
   ```bash
   docker tag euclid:1.0.0 your-registry/euclid:1.0.0
   docker push your-registry/euclid:1.0.0
   ```

3. **Use specific image digests** for reproducibility:
   ```bash
   docker pull euclid@sha256:...
   ```

## CI/CD Integration

### GitHub Actions

```yaml
- name: Build Docker image
  run: docker build -t euclid .

- name: Run tests in Docker
  run: docker run euclid mvn test

- name: Push to registry
  run: |
    docker tag euclid:latest ghcr.io/yourusername/euclid:latest
    docker push ghcr.io/yourusername/euclid:latest
```

### GitLab CI

```yaml
docker-build:
  image: docker:latest
  services:
    - docker:dind
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHORT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHORT_SHA
```

## See Also

- [README.md](README.md) - General Euclid documentation
- [DEVELOPER_GUIDE.md](doc/DEVELOPER_GUIDE.md) - For extending Euclid
- [Docker Documentation](https://docs.docker.com/)
