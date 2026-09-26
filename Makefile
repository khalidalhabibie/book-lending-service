db-up:
	docker compose up -d

db-down:
	docker compose down

db-reset:
	docker compose down -v
	docker compose up -d

run:
	./mvnw spring-boot:run

test:
	./mvnw test

format:
	./mvnw spotless:apply

check:
	./mvnw spotless:check
	./mvnw test