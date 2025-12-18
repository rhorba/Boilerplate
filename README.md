# Spring Boot Enterprise Boilerplate

A production-ready generic backend for SaaS applications, built with **Spring Boot 3**, **Java 17**, and strict **Hexagonal Architecture**.
Frontend included: **Angular 16** Admin Dashboard starter.

## Features
- **Architecture**: Hexagonal (Ports & Adapters) -> Domain, Application, Infrastructure.
- **Security**: 
    - JWT Authentication (Access + Refresh Token).
    - Decoupled `TokenProviderPort` for security implementation independence.
    - Role-Based Access Control (RBAC).
- **Database**: PostgreSQL (Dockerized).
- **Caching**: Redis (Dockerized).
- **Quality**: 
    - Global Exception Handling (ProblemDetails-style).
    - Custom Domain Exceptions.
    - Extensive Unit Testing.
- **Integration**:
    - **Payment**: integrations for Stripe and PayPal (Prepared).
    - **Email**: Async Email Service with Mailhog for testing.
- **Documentation**: OpenAPI / Swagger UI.

## Prerequisites
- **Java 17+**
- **Docker & Docker Compose**
- **Node.js & npm** (for Frontend)

## Getting Started

### 1. Infrastructure (Docker)
Start the required databases and services:
```bash
docker-compose up -d
```
This starts:
- **PostgreSQL** (port 5432)
- **Redis** (port 6379)
- **Mailhog** (port 1025, UI at http://localhost:8025)

### 2. Backend
Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`.
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

### 3. Frontend
Navigate to the frontend directory and start the Angular app:
```bash
cd frontend
npm install
npm start
```
Access the dashboard at `http://localhost:4200`.

## Architecture Overview
The project follows a strict Hexagonal Architecture:

### 1. Domain Layer (`com.boilerplate.domain`)
Pure business logic. **No dependencies** on Spring, Hibernate, or other frameworks.
- **Models**: `User`, `Role` (POJOs).
- **Ports**: Interfaces defining what the domain needs (Repositories) or what functionality it exposes/uses.

### 2. Application Layer (`com.boilerplate.application`)
Orchestrates the domain logic.
- **Services**: `AuthService`, `UserService`.
- **Ports (Out)**: `TokenProviderPort` (decouples Security lib), `UserRepository`.

### 3. Infrastructure Layer (`com.boilerplate.infrastructure`)
Adapters that implement the ports.
- **Persistence**: JPA Entities, Repositories.
- **Web**: REST Controllers, Global Exception Handler.
- **Config**: Spring Security `JwtService` (implements `TokenProviderPort`).

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and development process.

## License
Private / Proprietary.
