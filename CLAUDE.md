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