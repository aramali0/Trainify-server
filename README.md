# E-Learning Platform - Backend

## 📌 Overview

This repository contains the backend code for the e-learning platform, handling authentication, course management, and user interactions.

## 🚀 Technologies Used

- **Spring Boot**: Java framework for building web applications.
- **Spring Data**: Simplifies database interactions.
- **Spring Security**: Provides authentication and authorization.
- **Spring Web**: Implements RESTful APIs.
- **JUnit**: Unit testing framework.
- **Postman**: Used for API testing.

## 📂 Project Structure

```
/src/main/java/com/example/e-learning
  ├── controller   # API controllers
  ├── service      # Business logic
  ├── repository   # Database interactions
  ├── model        # Entity models
  ├── config       # Security and app configurations
```

## 📦 Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/your-org/e-learning-backend.git
   cd e-learning-backend
   ```
2. Configure the database in `application.properties`:

   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/e_learning
   spring.datasource.username=root
   spring.datasource.password=password
   ```

3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## 🛠️ Available Scripts

- `mvn clean install` → Build the project
- `mvn spring-boot:run` → Run the server
- `mvn test` → Run unit tests

## 📜 License

MIT License
