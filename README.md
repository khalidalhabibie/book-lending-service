# Book Lending Service

Simple book lending service built with Java, Spring Boot, and PostgreSQL.

## How to Run

Start PostgreSQL:

    docker compose up -d

Run:

    ./mvnw spring-boot:run

API: `http://localhost:8080`

## Auth

HTTP Basic Auth.

    Admin: admin / admin123
    User:  user / user123

Admin can manage books and members. Both users can access lending APIs.

## Config

Borrowing rules are configurable in `application.properties`:

    library.max-active-loans=5
    library.loan-duration-days=14

## Rules

- Maximum active loans per member
- Overdue loan blocks new borrowing
- Book must have an available copy
- Due date follows the configured loan duration

## API

    GET    /api/books
    POST   /api/books
    GET    /api/books/{id}
    PUT    /api/books/{id}

    GET    /api/members
    POST   /api/members
    GET    /api/members/{id}
    PUT    /api/members/{id}

    POST   /api/loans
    POST   /api/loans/{id}/return

Swagger: `http://localhost:8080/swagger-ui/index.html`

Health: `http://localhost:8080/actuator/health`

## Database

PostgreSQL with Flyway migrations.

    src/main/resources/db/migration

## Test

    ./mvnw test

## Notes

- Members and authentication users are separate
- Book availability is updated on borrow and return
- Borrow and return are transactional
- Book row is locked when updating availability