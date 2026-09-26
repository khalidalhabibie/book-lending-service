# Book Lending Service

Simple book lending API built with Java, Spring Boot, and PostgreSQL.

## How to Run

Start PostgreSQL:

    docker compose up -d

Run the app:

    ./mvnw spring-boot:run

API: `http://localhost:8080`

## Auth

HTTP Basic Auth.

    Admin: admin / admin123
    User:  user / user123

## Rules

- Max 5 active loans
- Loan duration: 14 days
- Overdue loan blocks borrowing
- Unavailable books cannot be borrowed
- Rules can be changed in `application.properties`

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

## Test

    ./mvnw test

## Notes

- Database schema is managed with Flyway
- Borrow and return run inside database transactions
- Book availability is updated on borrow and return
- Book row is locked when updating availability
- Library members and authentication users are separate