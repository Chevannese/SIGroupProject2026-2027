# Stage 1: Build the application distribution
FROM gradle:8.14-jdk21 AS build
WORKDIR /app
COPY . .

# Note: Change 'buildFatJar' to 'bootJar' if using Spring Boot
RUN chmod +x ./gradlew && ./gradlew buildFatJar --no-daemon


# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the generated binaries and libraries from installDist
COPY --from=build /app/build/install/* /app/

EXPOSE 8080

# -Xmx350m prevents OutOfMemory crashes on Render's 512MB limit
ENTRYPOINT ["java", "-Xmx350m", "-jar", "app.jar"]


