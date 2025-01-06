# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the build output (e.g., the jar file) into the container
COPY build/libs/book-borrowing-system*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
