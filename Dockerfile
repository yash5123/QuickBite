# ── Stage 1: Build & Compile Java Sources ──
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy dependency libraries and Java source code
COPY libs/ libs/
COPY src/ src/

# Create output directory and compile all Java source files
RUN mkdir -p out/production/QuickBite && \
    find src -name "*.java" > sources.txt && \
    javac -encoding UTF-8 -cp "libs/*" -d out/production/QuickBite @sources.txt

# ── Stage 2: Minimal Production Runtime ──
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy compiled classes, native sqlite libs, and frontend assets
COPY --from=builder /app/out/production/QuickBite out/production/QuickBite
COPY libs/ libs/
COPY public/ public/

# Default fallback port (Render overrides this with $PORT dynamically)
ENV PORT=8080
EXPOSE 8080

# Execute QuickBiteServer entry point
CMD ["sh", "-c", "java -cp out/production/QuickBite:libs/* QuickBiteServer"]
