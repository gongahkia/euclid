# Euclid Transpiler - Docker Container for Reproducible Builds
# Build: docker build -t euclid .
# Run REPL: docker run -it euclid repl
# Transpile file: docker run -v $(pwd):/workspace euclid transpile /workspace/input.ed /workspace/output.md

FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Set working directory
WORKDIR /build

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the project (with tests)
RUN mvn clean package -DskipTests

# Create runtime image (smaller)
FROM eclipse-temurin:21-jre-alpine

# Install bash for better shell experience
RUN apk add --no-cache bash

# Create app directory
WORKDIR /app

# Copy built JARs from builder
COPY --from=builder /build/target/euclid-transpiler.jar ./euclid-transpiler.jar
COPY --from=builder /build/target/euclid-repl.jar ./euclid-repl.jar

# Create workspace directory for mounting user files
WORKDIR /workspace

# Default command shows usage
ENTRYPOINT ["java"]
CMD ["-jar", "/app/euclid-transpiler.jar"]

# Add labels
LABEL org.opencontainers.image.title="Euclid Transpiler"
LABEL org.opencontainers.image.description="Intuitive syntax for beautiful LaTeX-style mathematical expressions in Markdown"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.authors="Gabriel Ong"
LABEL org.opencontainers.image.url="https://github.com/gongahkia/euclid"
LABEL org.opencontainers.image.source="https://github.com/gongahkia/euclid"
LABEL org.opencontainers.image.licenses="MIT"
