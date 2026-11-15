# Stage 1: Build the application distribution
FROM gradle:8.14-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle installDist --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the generated binaries and libraries from installDist
COPY --from=build /app/build/install/* /app/

EXPOSE 8080
# Set JVM memory limit for Render's 512MB free tier and run the startup script
ENV JAVA_OPTS="-Xmx350m"
CMD ["sh", "-c", "./bin/*"]

