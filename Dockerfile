# Stage 1: Build the JAR
FROM gradle:8.10-jdk21 AS build
WORKDIR /app
COPY . .
# Note: Change 'buildFatJar' to 'bootJar' if using Spring Boot
# RUN chmod +x ./gradlew && ./gradlew buildFatJar --no-daemon
RUN gradle buildFatJar --no-daemon

# Stage 2: Run the JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copies the generated JAR (adjust path if your build folder differs)
COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080
# -Xmx350m prevents OutOfMemory crashes on Render's 512MB limit
ENTRYPOINT ["java", "-Xmx350m", "-jar", "app.jar"]
