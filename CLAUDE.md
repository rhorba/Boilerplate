## Project Overview
Enterprise-grade boilerplate featuring a decoupled Monorepo architecture. 
Backend: Spring Boot 3.4 (Java 21). Frontend: React 18/Angular 18.
Focus: Type-safety, automated migrations, and high-performance mapping.

## Rules
- Concise interactions. Priority: clarity over grammar.
- Architecture: Strict Layered Hexagonal. No business logic in Controllers/Entities.
- Mapping: MapStruct only. Manual mapping is prohibited.
- Migrations: Flyway for all schema changes. No hibernate ddl-auto=update.
- Commit messages: Follow Conventional Commits.
- **Line Endings**: Force LF for all `.sql` and `.java` files to prevent Flyway checksum errors on Windows.

## Tech Stack
- **Backend**: Java 21, Spring Boot 3.4, Spring Data JPA, Flyway, MapStruct, Lombok.
- **Frontend**: React 18+ (Vite) OR Angular 18+, Tailwind CSS, TanStack Query.
- **Database**: PostgreSQL.
- **Build**: Maven (Global), PNPM (Global).

## Development Commands (Windows Native)
- `mvn spring-boot:run` - Start Backend.
- `pnpm dev` - Start Frontend.
- `mvn clean verify` - Full Backend build & test.
- `pnpm lint` - Frontend linting and formatting.

## Windows Requirements
- **Java Version**: Ensure `%JAVA_HOME%` points to JDK 21.
- **Git Config**: Run `git config --global core.autocrlf input` to handle cross-platform line endings.
- **Database**: PostgreSQL should be running as a local service or via Docker.

---

## Implementation Status

### 🚀 Project Complete - 100%

All phases of the boilerplate implementation have been successfully completed. The project is production-ready with full authentication, authorization, and user management features.

### ✅ Completed Implementation

**Phase 1: Project Structure & Configuration**
- Monorepo structure (/backend, /frontend)
- .gitignore and .gitattributes (LF enforcement for cross-platform compatibility)
- Docker Compose configurations (dev with H2, prod with PostgreSQL)
- Environment configuration templates (.env.example)

**Phase 2: Backend Foundation**
- Maven pom.xml with all dependencies:
  - Spring Boot 3.4.1, Java 21
  - Spring Security, JWT (jjwt 0.12.6)
  - MapStruct 1.6.3, Lombok 1.18.34
  - Flyway, PostgreSQL, H2
  - SpringDoc OpenAPI 2.7.0
  - Testcontainers, JaCoCo, Checkstyle, SpotBugs
- Hexagonal architecture package structure
- Spring Boot application with profiles (dev/prod)
- Multi-stage Dockerfiles (prod & dev)

**Phase 3: Security & Authentication Layer**
- Domain entities with JPA Auditing:
  - BaseEntity (id, version, createdAt, updatedAt)
  - User, Role, Permission entities
- 9 Flyway migrations:
  - V1-V5: Table creation with indexes
  - V6: 13 permissions (USER_*, ROLE_*, PERMISSION_*, SYSTEM_MANAGE)
  - V7: 3 roles (ADMIN, USER, MODERATOR)
  - V8: Seed admin user (admin/admin123)
  - V9: Soft-delete support (deleted_at column on users)
- JPA repositories with JOIN FETCH queries to prevent N+1
- Request/Response DTOs with Bean Validation
- MapStruct mappers (User, Role, Permission)

**Phase 4: Security Configuration**
- JWT implementation:
  - 15-minute access tokens
  - 30-day refresh tokens (90 days with remember me)
  - Secret from JWT_SECRET environment variable
- UserDetailsService & UserPrincipal
- JwtAuthenticationFilter (Bearer token extraction)
- CustomPermissionEvaluator for @PreAuthorize
- SecurityConfig with stateless session management
- CorsConfig with configurable origins
- LoggingFilter for request/response audit
- Logback configuration (JSON for prod, console for dev)
- Checkstyle configuration for code quality

**Phase 5: Service Layer & Controllers**
- Exception handling:
  - ResourceNotFoundException (404)
  - DuplicateResourceException (409)
  - GlobalExceptionHandler for all error cases
- Services:
  - AuthService (login, refresh token, self-registration with auto-login)
  - UserService (CRUD, soft-delete, restore, purge, bulk operations, search/filter)
- Controllers with OpenAPI annotations:
  - AuthController (/api/auth/*) with registration endpoint
  - UserController (/api/users/*) with permission-based access, bulk ops, soft-delete
  - RoleController (/api/roles/*) for role listing
- Unit tests:
  - UserServiceTest with Mockito and AssertJ (soft-delete-aware)
  - AuthServiceTest with registration tests
- Documentation:
  - README.md (setup, architecture, features)
  - CONTRIBUTING.md (code standards, workflow)

**Phase 6: Frontend (Angular 18)**
- Project configuration:
  - package.json with Angular 18.2.0, @tanstack/angular-query-experimental
  - Tailwind CSS 3.4.13 with PostCSS
  - ESLint & Prettier with strict rules
  - TypeScript 5.5.2 with strict mode
- Core services:
  - TokenService (localStorage management)
  - AuthService with signals (currentUser, isAuthenticated, self-registration)
  - UserService (full CRUD, search/filter, bulk operations, soft-delete)
- HTTP interceptors:
  - authInterceptor (Bearer token injection)
  - errorInterceptor (401 logout, error logging)
- Guards:
  - authGuard (route protection with returnUrl)
- Shared models:
  - User, Role, Permission TypeScript interfaces
- Components:
  - LoginComponent (reactive form with rememberMe)
  - DashboardComponent (user info, role badges)
  - UserListComponent (search, role/status filters, sortable columns, bulk actions, pagination)
  - UserEditPanelComponent (slide-out form with validation and keyboard support)
- Routing:
  - Lazy-loaded routes with guards
  - Default redirect to dashboard
- Docker configuration:
  - Dockerfile (multi-stage: node build → nginx serve)
  - Dockerfile.dev (pnpm dev server)
  - nginx.conf (SPA routing, /api proxy to backend)

### 📋 Key Features Implemented

**Authentication & Authorization:**
- JWT-based stateless authentication
- Remember me functionality (90-day tokens)
- Self-registration with auto-login and rate limiting
- Role-based access control (RBAC)
- Permission-based authorization at endpoint level
- Automatic token refresh mechanism

**User Management:**
- Complete CRUD operations with validation
- Username/email uniqueness checks
- BCrypt password hashing
- Role assignment and management
- Pagination support
- Soft-delete with restore and permanent purge
- Server-side search and filtering (JPA Specifications)
- Bulk operations (delete, enable, disable)
- Slide-out edit panel with form validation

**Security Features:**
- CORS configuration
- CSRF protection
- SQL injection prevention (JPA)
- XSS protection (Spring Security defaults)
- Request/response logging for audit

**Code Quality:**
- Checkstyle enforcement
- SpotBugs static analysis
- JaCoCo code coverage (70% threshold)
- Prettier & ESLint for frontend
- Strict TypeScript compilation

**Database:**
- Flyway migrations with version control
- Seed data for development
- H2 for local development
- PostgreSQL for production
- Optimized queries with JOIN FETCH

**API Documentation:**
- SpringDoc OpenAPI integration
- Swagger UI at /swagger-ui.html
- Bearer token authentication in Swagger

**Monitoring:**
- Spring Boot Actuator endpoints
- Health checks for Docker Compose
- Prometheus-compatible metrics

### 🎯 Quick Start

**Development Mode:**
```bash
# Backend (H2 database)
cd backend
mvn spring-boot:run

# Frontend (separate terminal)
cd frontend
pnpm install
pnpm dev
```

**Production Mode:**
```bash
# Docker Compose with PostgreSQL
docker-compose -f docker-compose.prod.yml up --build
```

**Access Points:**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console (dev): http://localhost:8080/h2-console

**Default Credentials:**
- Username: `admin`
- Password: `admin123`

### 📊 Default Permissions

The system includes 13 pre-configured permissions:
- USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE, USER_MANAGE
- ROLE_READ, ROLE_CREATE, ROLE_UPDATE, ROLE_DELETE, ROLE_MANAGE
- PERMISSION_READ, PERMISSION_MANAGE
- SYSTEM_MANAGE

**Role Assignments:**
- ADMIN: All permissions
- MODERATOR: USER_* + ROLE_READ + PERMISSION_READ
- USER: USER_READ only

### 🔧 Environment Variables

Required for production:
```bash
# Database
POSTGRES_USER=boilerplate
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=boilerplate

# JWT
JWT_SECRET=your_256_bit_secret_key_here

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://yourdomain.com
```

### 📦 Project Structure

```
Boilerplate/
├── backend/                           # Spring Boot 3.4 + Java 21
│   ├── src/main/java/com/boilerplate/
│   │   ├── domain/                    # Entities & Repositories
│   │   ├── application/               # DTOs, Mappers, Services
│   │   ├── infrastructure/            # Security, Config
│   │   └── presentation/              # Controllers, Exception Handling
│   ├── src/main/resources/
│   │   ├── db/migration/              # Flyway migrations
│   │   └── application*.yml           # Configuration
│   └── pom.xml
├── frontend/                          # Angular 18 Standalone
│   ├── src/app/
│   │   ├── core/                      # Services, Guards, Interceptors
│   │   └── features/                  # Components (auth, dashboard, users)
│   ├── angular.json
│   ├── tailwind.config.js
│   └── package.json
├── docs/
│   └── plans/                         # Archived implementation plans
├── docker-compose.dev.yml             # H2 development setup
├── docker-compose.prod.yml            # PostgreSQL production setup
├── .gitattributes                     # LF enforcement
├── README.md                          # Full documentation
└── CONTRIBUTING.md                    # Contribution guidelines