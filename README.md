# Enterprise Boilerplate

Open-source enterprise-grade boilerplate featuring a decoupled monorepo architecture with Spring Boot 3.4 (Java 21) backend and Angular 18 frontend.

## Features

- **Backend**: Spring Boot 3.4, Java 21, PostgreSQL/H2, JWT Authentication
- **Frontend**: Angular 18, TanStack Query, Tailwind CSS, Signals
- **Security**: Role-Based Access Control (RBAC) with permission-level granularity
- **Authentication**: JWT with refresh tokens, self-registration with auto-login
- **User Management**: Soft-delete, server-side search/filtering, bulk operations, slide-out edit panel
- **User Profiles**: Extended profile management (bio, phone) linked to user accounts
- **Audit Logging**: comprehensive system activity tracking with IP address and metadata support
- **Architecture**: Hexagonal/Clean Architecture on backend, layer-based on frontend
- **Database**: Flyway migrations (V1-V11), seeded data
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Observability**: Logback JSON logging, Spring Actuator endpoints
- **Code Quality**: Checkstyle, SpotBugs, JaCoCo (70% coverage), ESLint, Prettier
- **Testing**: JUnit, Mockito, Testcontainers
- **Docker**: Multi-stage builds, Docker Compose for dev and prod

## Prerequisites

- **Java**: JDK 21 (set `JAVA_HOME` environment variable)
- **Node.js**: v20+ with pnpm installed globally
- **Git**: Configure `git config --global core.autocrlf input`
- **Docker**: (Optional) For containerized development

## Quick Start

### Development Mode (H2 Database)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Boilerplate
   ```

2. **Start Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   Backend runs at `http://localhost:8080`

3. **Start Frontend** (new terminal)
   ```bash
   cd frontend
   pnpm install
   pnpm dev
   ```
   Frontend runs at `http://localhost:4200`

4. **Login**
   - Username: `admin`
   - Password: `admin123`

### Docker Development Mode (H2 Database)

Run both backend and frontend in Docker with hot-reload enabled:

```bash
# Start all services
docker-compose -f docker-compose.dev.yml up --build

# Or run in detached mode
docker-compose -f docker-compose.dev.yml up --build -d

# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Stop all services
docker-compose -f docker-compose.dev.yml down
```

Access:
- Frontend: `http://localhost:4200`
- Backend API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:boilerplate`, username: `sa`, password: empty)

**Features**:
- Hot-reload for both frontend and backend
- H2 in-memory database (data resets on restart)
- Source code mounted as volumes for live development
- Automatic Flyway migrations and seed data on startup

### Production Mode (Docker Compose + PostgreSQL)

```bash
docker-compose -f docker-compose.prod.yml up --build
```

Access:
- Frontend: `http://localhost`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Project Structure

```
/backend                  # Spring Boot backend
├── src/main/java/com/boilerplate/
│   ├── domain/          # Entities, repositories, specifications
│   ├── application/     # DTOs, mappers, service implementations
│   ├── infrastructure/  # Security, persistence, configs
│   └── presentation/    # REST controllers, exception handlers
├── src/main/resources/
│   ├── db/migration/    # Flyway SQL migrations (V1-V9)
│   └── application*.yml # Configuration files
└── pom.xml

/frontend                # Angular 18 frontend
├── src/app/
│   ├── core/           # Services, guards, interceptors, models
│   ├── features/       # Feature modules (auth, users, profile, audit-logs, dashboard)
│   └── services/       # API communication services
└── package.json

/docs
└── plans/              # Implementation plans (archived)
```

## API Documentation

Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

## Default Credentials

| Username | Password   | Role      |
|----------|------------|-----------|
| admin    | admin123   | ADMIN     |

## Available Permissions

### User Management
- `USER_READ` - View users
- `USER_CREATE` - Create new users
- `USER_UPDATE` - Modify users
- `USER_DELETE` - Delete users (soft-delete)
- `USER_MANAGE` - Full user management (restore, purge, bulk operations)

### Role Management
- `ROLE_READ` - View roles
- `ROLE_CREATE` - Create roles
- `ROLE_UPDATE` - Modify roles
- `ROLE_DELETE` - Delete roles
- `ROLE_MANAGE` - Full role management

### Permission Management
- `PERMISSION_READ` - View permissions
- `PERMISSION_MANAGE` - Full permission management

### System
- `SYSTEM_MANAGE` - Full system administration

## Development Commands

### Backend
```bash
mvn clean install          # Build project
mvn spring-boot:run        # Run application
mvn test                   # Run tests
mvn verify                 # Run tests + quality checks
mvn flyway:migrate         # Run database migrations
```

### Frontend
```bash
pnpm dev                   # Start dev server
pnpm build                 # Build for production
pnpm lint                  # Run linter
pnpm lint:fix              # Fix linting issues
pnpm test                  # Run tests
```

## Environment Variables

### Backend
- `SPRING_PROFILES_ACTIVE` - Active profile (dev/prod)
- `DB_HOST` - Database host (prod only)
- `DB_PORT` - Database port (prod only)
- `DB_NAME` - Database name (prod only)
- `DB_USER` - Database username (prod only)
- `DB_PASSWORD` - Database password (prod only)
- `JWT_SECRET` - JWT signing secret (required for prod)
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins (prod only)

## Testing

### Backend Testing
```bash
# Unit tests
mvn test

# Integration tests with Testcontainers
mvn verify

# Coverage report (target/site/jacoco/index.html)
mvn jacoco:report
```

## Code Quality

### Backend
- **Checkstyle**: Code style enforcement (`/backend/checkstyle.xml`)
- **SpotBugs**: Bug detection
- **JaCoCo**: Code coverage (minimum 70%)

### Frontend
- **ESLint**: Code linting
- **Prettier**: Code formatting

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines.

## Architecture

### Backend - Hexagonal Architecture

**Layers:**
1. **Domain**: Business entities and repository interfaces
2. **Application**: Use cases, DTOs, mappers
3. **Infrastructure**: Security, database, external services
4. **Presentation**: REST API controllers

**Key Patterns:**
- Dependency inversion (domain doesn't depend on infrastructure)
- MapStruct for DTO mapping (no manual mapping)
- Flyway for database versioning
- Repository pattern with JPA
- JPA Specifications for dynamic search/filtering
- Soft-delete pattern with restore/purge

### Frontend - Layer-Based Architecture

**Structure:**
- **/core**: Authentication, guards, interceptors, shared models
- **/features**: Feature-specific components (auth, users, dashboard)
- **/services**: API communication services

**Key Patterns:**
- Standalone components (no NgModules)
- Signals for reactive state
- TanStack Query for server state
- Functional guards and interceptors
- Shared TypeScript model interfaces

## License

MIT License

## Support

For issues and questions, please open a GitHub issue.
