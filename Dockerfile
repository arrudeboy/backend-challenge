# Use an official JDK 21 image as the base
FROM eclipse-temurin:21-jdk-alpine as builder

# Set a working directory inside the container
WORKDIR /app

# Copy Maven wrapper files if you use Maven wrapper
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Copy the application source code
COPY src src

# Build the application using Maven (or Gradle)
RUN ./mvnw clean package -DskipTests

# Use a smaller runtime image for the application
FROM eclipse-temurin:21-jre-alpine

# Set a working directory
WORKDIR /app

# Copy the Spring Boot JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Default command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
