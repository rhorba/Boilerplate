# GEMINI.md - Master Project Instructions & Skill System

## 1. Project Overview
Enterprise-grade boilerplate featuring a decoupled Monorepo architecture.
- **Backend**: Spring Boot 3.4.1 (Java 21).
- **Frontend**: Angular 18.2.0 Standalone.
- **Architecture**: Strict Layered Hexagonal.
- **Status**: 100% Complete with JWT auth, RBAC, and soft-delete user management.

## 2. Strict Engineering Rules
- **Mapping**: MapStruct only. Manual mapping is prohibited.
- **Database**: All schema changes MUST use Flyway migrations in `backend/src/main/resources/db/migration/`.
- **Logic Placement**: No business logic in Controllers or Entities. Entities use JPA Auditing.
- **Type Safety**: Maintain strict TypeScript interfaces in the frontend to match Backend DTOs.
- **Line Endings**: Force **LF** for all `.sql` and `.java` files to prevent Flyway checksum errors on Windows.

---

## 3. Workflow Skill Protocols

### 🧠 Phase 1: Brainstorming into Designs
**Trigger**: When starting a new feature or design refinement.
1. **Context First**: Analyze existing entities in `domain/model` and services in `application/service` before proposing.
2. **Socratic Iteration**: Ask **exactly one question at a time** to refine the data flow and UI (e.g., "Is this a new table or state change?").
3. **Approach Options**: Propose 2-3 approaches (Basic vs. Robust). Always recommend the most "YAGNI" (You Ain't Gonna Need It) option.
4. **Validation**: Present designs in 200-300 word sections and wait for user approval after each.

### 📝 Phase 2: Writing Implementation Plans
**Trigger**: Once a design is approved and tasks are needed.
1. **Granularity**: Every task must be bite-sized (2-5 minutes).
2. **Full-Stack Sync**: Every Backend change MUST include a corresponding task for Frontend models/services.
3. **Structure**: List specific file paths for "Create" or "Modify" for every single step.
4. **Output**: Save the plan to `docs/plans/YYYY-MM-DD-<feature-name>/plan.md`.

### 🚀 Phase 3: Executing Plans
**Trigger**: When implementing an existing `plan.md`.
1. **Batching**: Execute exactly **3 tasks** per turn.
2. **Verification**:
    - **Backend**: Run `mvn clean compile` to check MapStruct/Lombok.
    - **Frontend**: Run `pnpm lint` and verify Signal/Observable patterns.
3. **Reporting**: Show exactly what was implemented and provide a sync check before the next batch.
4. **Blockers**: STOP if Flyway migrations fail or if API signatures mismatch.

---

## 4. Automation Commands
- **Backend**: `mvn spring-boot:run` (Dev mode), `mvn clean verify` (Full Build/Test).
- **Frontend**: `pnpm dev` (Angular start), `pnpm lint` && `pnpm test` (Quality).
- **Database**: PostgreSQL (local/Docker).

## 5. Key Directory Reference
- `backend/src/main/java/com/boilerplate/`
    - `domain/`: Entities & Repositories.
    - `application/`: DTOs, Mappers, Services.
    - `infrastructure/`: Security, JWT, Config.
    - `presentation/`: Controllers & Global Exceptions.
- `frontend/src/app/`
    - `core/`: Global Services, Guards, Interceptors.
    - `features/`: Module-based UI Components.