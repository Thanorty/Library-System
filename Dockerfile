# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle wrapper and other necessary files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Run tests as part of the build process
RUN ./gradlew clean test

# Copy the build output (e.g., the jar file) into the container
COPY build/libs/book-borrowing-system*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
