# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Install Maven
RUN apk add --no-cache maven

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build file and source code
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Expose the application port
EXPOSE 8087

# Run the application
CMD ["java", "-jar", "target/Experts-Human-Capita-0.0.1-SNAPSHOT.jar"]
