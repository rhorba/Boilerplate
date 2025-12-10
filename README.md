# Spring Boot Enterprise Boilerplate

A production-ready generic backend for SaaS applications, built with Spring Boot 3, Java 17, and Hexagonal Architecture.

## Features
- **Architecture**: Hexagonal (Ports & Adapters) -> Domain, Application, Infrastructure.
- **Security**: JWT Authentication, Refresh Token, Role-Based Access Control.
- **Database**: PostgreSQL (Dockerized).
- **Caching**: Redis (Dockerized).
- **Payment**: integrations for Stripe and PayPal.
- **Email**: Async Email Service with Mailhog for testing.
- **Frontend**: Angular 16 Admin Dashboard starter.
- **Documentation**: OpenAPI / Swagger UI.

## Prerequisites
- Java 17+
- Docker & Docker Compose
- Node.js & npm (for Frontend)

## Getting Started

### 1. Infrastructure (Docker)
Start the required databases and services:
```bash
docker-compose up -d
```
This starts:
- PostgreSQL (port 5432)
- Redis (port 6379)
- Mailhog (port 1025, UI at http://localhost:8025)

### 2. Backend
Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Frontend
Navigate to the frontend directory and start the Angular app:
```bash
cd frontend
npm install
npm start
```
Access the dashboard at `http://localhost:4200`.

## Default Configuration
- **Database**: `boilerplate_db` / `postgres` / `password`
- **Mailhog**: User `null`, Password `null` (captures all emails).
- **Stripe**: Configured with a placeholder test key in `application.yml`.

## Architecture Overview
The project follows a strict Hexagonal Architecture:
- `domain`: Pure business logic (Models, Ports). No dependencies on Spring or Frameworks.
- `application`: Service layer, Use Cases, DTOs. Orchestrates Domain logic.
- `infrastructure`: Adapters (Web Controllers, Persistence, External APIs like Stripe/Email) and Configuration.

## License
Private / Proprietary (as per "Product" description).
