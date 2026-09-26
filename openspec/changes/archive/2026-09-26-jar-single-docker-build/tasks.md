## 1. OpenSpec and task setup

- [x] 1.1 Link DEV-BOOT-008 to this change and complete task-card metadata.

## 2. JAR and frontend packaging

- [x] 2.1 Add Web/Actuator dependencies and Maven resource copy from `frontend/dist` to JAR static resources.
- [x] 2.2 Add reproducible artifact build script and JAR/Docker startup documentation.

## 3. Container build

- [x] 3.1 Add multi-stage Dockerfile and `.dockerignore` with non-root user, health check, and no socket/Compose.
- [x] 3.2 Run frontend build, Maven tests/package, inspect JAR contents, and perform Dockerfile static negative checks.
- [x] 3.3 Update task record and index with artifact hashes, image metadata/static checks, and manual validation handoff.
