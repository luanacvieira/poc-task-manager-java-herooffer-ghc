# Copilot Instructions for Java Task Manager Monolith

## Project Overview
- **Monolithic Java app** using Spring Boot, Vaadin (UI), and JPA/Hibernate (H2 in-memory DB).
- Replicates a Node/React Task Manager, with plans for modularization/microservices.
- All business logic, REST API, and UI are in a single codebase.

## Key Components
- `domain/`: Core entities (`Task`, `Priority`, `Category`).
- `repository/`: Spring Data JPA repository (`TaskRepository`).
- `service/`: Business logic (`TaskService`).
- `web/rest/`: REST API (`TaskController`, `TaskDto`, `TaskMapper`).
- `web/ui/`: Vaadin UI (`MainLayout`, `TaskView`).
- `resources/application.properties`: App config (DB, server, etc).

## Developer Workflows
- **Build/Run:** `mvn spring-boot:run` (requires JDK 17+)
- **Test:** `mvn test` (unit tests in `TaskServiceTests.java`)
- **UI:** http://localhost:8080/tasks
- **API:** http://localhost:8080/api/tasks
- **H2 Console:** http://localhost:8080/h2-console (JDBC: `jdbc:h2:mem:taskdb`)

## Patterns & Conventions
- **DTO/Mapper:** REST API uses `TaskDto` and `TaskMapper` for data transfer.
- **Enum fields:** `Priority` and `Category` are enums, mapped in both API and UI.
- **Validation:** Uses Jakarta Validation annotations on entities and DTOs.
- **No authentication** (future phase: JWT/multi-user).
- **Stateless REST API**: `/api/tasks` for CRUD, `/api/tasks/stats` for stats.
- **Vaadin UI**: 100% Java, no frontend JS/TS code.
- **Tests:** Only basic service test exists; expand as needed.

## Integration Points
- **No external services** (all in-memory/dev only).
- **Planned evolution:** Modularization, external DB, separate frontend, CQRS, JWT, observability.

## Examples
- **Add a task (cURL):**
  ```bash
  curl -X POST http://localhost:8080/api/tasks \
    -H "Content-Type: application/json" \
    -d '{"title": "Revisar arquitetura", "priority": "HIGH", ...}'
  ```
- **Entity pattern:** See `domain/Task.java` for field conventions.
- **REST pattern:** See `web/rest/TaskController.java` for endpoint structure.

## Special Notes
- **Keep code modular**: anticipate future extraction of modules/services.
- **Follow existing naming and layering** (domain, repository, service, web/rest, web/ui).
- **Document new endpoints and workflows in README.md.**

---
For more details, see `README.md` and source files in each package.
