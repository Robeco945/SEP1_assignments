# ── Build & test stage ────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy POM first so dependency layer is cached separately from source changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source tree
COPY src ./src

# Default command: compile, run tests, and generate the JaCoCo HTML report
CMD ["mvn", "test", "-B"]
