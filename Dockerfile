# Use a base image with OpenJDK 17
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY build/libs/bank-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8081

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
