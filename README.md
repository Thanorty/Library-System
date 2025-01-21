# Library Management System API

A RESTful API service for managing a library system, built with Spring Boot. This system allows for book and borrower management, including features for book borrowing and returns.

## Features

- Borrower Management
  - Register new borrowers
  - View borrower details
  - List all borrowers

- Book Management
  - Register new books
  - List all books
  - Search books by ISBN
  - Track book availability

- Borrowing System
  - Borrow books
  - Return books
  - View borrowing history
  - Track overdue status

## Technology Stack

- Java 17
- Spring Boot
- Spring Data JPA
- RESTful API
- Gradle (package manager)
- PostgreSQL (database)
- Docker & Docker Compose
- GitHub Actions (CI/CD)

## API Endpoints

### Borrower Management

```
POST /api/borrowers
GET /api/borrowers
GET /api/borrowers/{id}
```

### Book Management

```
POST /api/books
GET /api/books
GET /api/books?isbn={isbn}
GET /api/books?isbn={isbn}&withBorrowHistory={boolean}
POST /api/books/{bookId}/borrow
POST /api/books/{bookId}/return
```

## Data Models

### Book
```json
{
  "id": "long",
  "isbn": "string",
  "title": "string",
  "author": "string",
  "available": "boolean",
  "borrowHistory": [
    {
      "borrowId": "long",
      "borrowerId": "long",
      "borrowerName": "string",
      "borrowerEmail": "string",
      "borrowDate": "datetime",
      "returnDate": "datetime"
    }
  ]
}
```

### Borrower
```json
{
  "id": "long",
  "name": "string",
  "email": "string"
}
```

## API Usage Examples

### Get Specific Book with History

```http
GET /api/books?isbn=978-0-7475-3269-9&withBorrowHistory=true
```

Response:
```json
{
  "id": "long",
  "isbn": "string",
  "title": "string",
  "author": "string",
  "available": "boolean",
  "borrowHistory": [
    {
      "borrowId": "long",
      "borrowerId": "long",
      "borrowerName": "string",
      "borrowerEmail": "string",
      "borrowDate": "datetime",
      "returnDate": "datetime"
    }
  ]
}
```

### Get Specific Book with History

```http
GET /api/books?isbn=978-0-7475-3269-9&withBorrowHistory=true
```

### Register a New Book

```http
POST /api/books
Content-Type: application/json

{
  "isbn": "978-0-7475-3269-9",
  "title": "Harry Potter and the Philosopher's Stone",
  "author": "J.K. Rowling"
}
```

### Register a New Borrower

```http
POST /api/borrowers
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

### Borrow a Book

```http
POST /api/books/1/borrow
Content-Type: application/json

{
  "borrowerId": 1
}
```

## Business Rules

1. ISBN Handling:
   - Multiple copies of books with the same ISBN are allowed
   - Books with the same ISBN must have identical title and author
   - Different books must have different ISBNs

2. Borrowing Rules:
   - A book can only be borrowed by one person at a time
   - Books have a standard loan period of 2 weeks
   - System tracks overdue books
   - Borrower must be registered to borrow books

## Error Handling

The API uses standard HTTP status codes and returns detailed error messages:

- 200: Successful operation
- 400: Bad request (invalid input)
- 404: Resource not found
- 409: Conflict (e.g., duplicate ISBN with different details)
- 500: Internal server error

Error Response Format:
```json
{
  "status": "ERROR",
  "message": "Error description",
  "errors": ["Detailed error message"]
}
```

## Setup and Installation

### Local Development Setup

1. Prerequisites:
   - Java 17 or higher
   - Maven/Gradle
   - Your preferred IDE
   - Docker and Docker Compose

2. Clone the repository:
   ```bash
   git clone https://github.com/Thanorty/Library-System.git
   ```

3. Configure database properties in `application.properties` for local development:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
   spring.datasource.username=library_user
   spring.datasource.password=library_password
   ```

4. Build the project:
   ```bash
   ./gradlew clean build
   ```

5. Run the application:
   ```bash
   ./gradlew bootRun
   ```

### Docker Setup

1. Build and run using Docker Compose:
   ```bash
   docker-compose up --build
   ```

This will start:
- PostgreSQL database container
- Spring Boot application container

The application will be available at `http://localhost:8080`

### Docker Services

```yaml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_USER: library_user
      POSTGRES_PASSWORD: library_password
      POSTGRES_DB: library_db
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
```

### Environment Variables

The following environment variables can be configured:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/library_db
SPRING_DATASOURCE_USERNAME=library_user
SPRING_DATASOURCE_PASSWORD=library_password
```

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration and deployment. The pipeline automates building, testing, and deployment processes.

### Workflow Stages

1. **Build**
   ```yaml
   - Checkout code
   - Setup JDK 17 (Temurin distribution)
   - Build with Gradle
   - Setup Docker environment
   - Start PostgreSQL and Spring Boot services
   - Run tests
   - Cleanup Docker services
   ```

2. **Deploy**
   - Automated deployment process (customizable based on deployment target)

### Workflow Trigger Events
- Push to main, dev-01 branch
- Pull requests to main, dev-01 branch

### Environment Setup
The CI/CD pipeline uses the following environment variables:
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/library_db
SPRING_DATASOURCE_USERNAME=library_user
SPRING_DATASOURCE_PASSWORD=library_password
```

### Test Environment
- Uses PostgreSQL in Docker container
- Runs integration tests against containerized services
- Ensures consistent test environment across CI/CD pipeline


## Testing Strategy

The application implements a comprehensive testing strategy using JUnit 5, MockMvc, and Mockito. Tests are organized at multiple levels to ensure code quality and functionality.

### Test Structure

1. **Controller Tests**
   - Uses `@SpringBootTest` and `@AutoConfigureMockMvc`
   - Tests HTTP endpoints and responses
   - Validates JSON responses
   - Checks HTTP status codes
   - Tests business rule validations

2. **Service Tests**
   - Uses `@ExtendWith(MockitoExtension.class)`
   - Mocks repository layer
   - Tests business logic
   - Validates error conditions
   - Ensures data integrity

### Example Test Cases

#### Book Controller Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {
    @Test
    @Transactional
    void registerBook_Success() {
        // Tests successful book registration
    }

    @Test
    @Transactional
    void registerBook_DuplicateISBNDifferentTitle() {
        // Tests ISBN uniqueness constraints
    }

    @Test
    @Transactional
    void registerBook_MultipleCopies() {
        // Tests multiple copies of same book
    }
}
```

#### Service Layer Tests
```java
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Test
    void borrowBook_Success() {
        // Tests successful book borrowing
    }

    @Test
    void borrowBook_BookNotAvailable_ThrowsException() {
        // Tests borrowing unavailable book
    }

    @Test
    void returnBook_Success() {
        // Tests successful book return
    }
}
```

### Key Test Scenarios

1. **Book Management**
   - Register new book
   - Handle duplicate ISBNs
   - Multiple copies of same book
   - Book availability tracking

2. **Borrower Management**
   - Register new borrower
   - Handle duplicate emails
   - Validate required fields
   - Borrower lookup

3. **Borrowing Operations**
   - Borrow available book
   - Handle unavailable book
   - Return borrowed book
   - Track borrow history

### Test Coverage Areas

1. **Input Validation**
   - Required fields
   - Data format validation
   - Business rule validation

2. **Error Handling**
   - Resource not found
   - Duplicate resources
   - Invalid operations
   - Constraint violations

3. **Transaction Management**
   - Data consistency
   - Transaction rollback
   - Concurrent operations

### Running Tests

1. **Local Environment**
   ```bash
   ./gradlew test
   ```

2. **Docker Environment**
   ```bash
   docker-compose up -d postgres
   ./gradlew test
   ```

3. **CI/CD Pipeline**
   - Tests run automatically on push/PR
   - Uses containerized PostgreSQL
   - Ensures consistent test environment

### Testing Best Practices

1. **Test Isolation**
   - Use `@Transactional` for database tests
   - Mock external dependencies
   - Reset state between tests

2. **Meaningful Assertions**
   - Test specific outcomes
   - Verify error messages
   - Check state changes

3. **Test Data Management**
   - Use `@BeforeEach` for setup
   - Clear test data after execution
   - Use meaningful test data

4. **Test Organization**
   - Grouped by functionality
   - Clear test names
   - Documented test purposes

## Project Structure

```
├── src
│   ├── main
│   │   ├── java/com/library
│   │   │   ├── controller
│   │   │   ├── service
│   │   │   ├── repository
│   │   │   ├── model
│   │   │   ├── dto
│   │   │   └── exception
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java/com/library
├── docker-compose.yml
├── Dockerfile
├── build.gradle
└── .github
    └── workflows
        └── ci-cd.yml
```

## 12-Factor App Compliance

This application follows the 12-Factor methodology:

1. **Codebase**
   - Single codebase tracked in Git
   - Multiple deploys from the same codebase

2. **Dependencies**
   - Explicitly declared in build.gradle
   - Isolated dependencies using Gradle

3. **Config**
   - Environment variables for configuration
   - Different configs for development/production

4. **Backing Services**
   - Database treated as attached resource
   - Configuration via environment variables

5. **Build, Release, Run**
   - Strict separation using CI/CD pipeline
   - Immutable releases with Docker

6. **Processes**
   - Stateless application
   - Data persistence in PostgreSQL

7. **Port Binding**
   - Self-contained with embedded Tomcat
   - Port configuration via environment

8. **Concurrency**
   - Horizontal scalability support
   - Stateless design

9. **Disposability**
   - Fast startup and graceful shutdown
   - Container-based deployment

10. **Dev/Prod Parity**
    - Docker ensures environment consistency
    - Same backing services in all environments

11. **Logs**
    - Treated as event streams
    - Standard output logging

12. **Admin Processes**
    - One-off admin processes as tasks
    - Same environment as regular processes


## Design Decisions

1. Database Choice:
   - PostgreSQL chosen for:
     - ACID compliance
     - Complex relationships between entities
     - Data integrity requirements
     - Strong consistency needs
     - Container-friendly deployment

2. Architecture:
   - Controller-Service-Repository pattern
   - DTO pattern for request/response handling
   - Separation of concerns
   - Transaction management
   - Containerized microservices architecture

3. Security Considerations:
   - Input validation
   - Error handling
   - Data sanitization
   - Transaction isolation
   - Container isolation and networking

4. Container Orchestration:
   - Docker Compose for local development and testing
   - Isolated network for services
   - Volume persistence for database
