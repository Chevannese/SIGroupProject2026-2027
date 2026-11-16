# Stage 1: build the distribution with the project's Gradle wrapper and JDK 25 (matches jvmToolchain(25))
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew installDist --no-daemon

# Stage 2: run the app on a small JRE
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/build/install/FlowServer/ ./

# Render's free tier has 512 MB of RAM
ENV JAVA_OPTS="-Xmx350m"

# Documentation only: the server reads $PORT (set by Render) and falls back to 9000
EXPOSE 9000

CMD ["./bin/FlowServer"]
