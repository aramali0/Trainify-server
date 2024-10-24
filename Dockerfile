# First stage: Build the application
FROM maven:3.8.6-openjdk-17-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies first (to cache dependencies)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy the rest of the application source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Second stage: Run the application
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the first stage
COPY --from=build /app/target/Experts-Human-Capita-0.0.1-SNAPSHOT.jar /app/Experts-Human-Capita.jar

# Expose the application port
EXPOSE 8087

# Run the application
CMD ["java", "-jar", "Experts-Human-Capita.jar"]
