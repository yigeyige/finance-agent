## 1. Task and baseline alignment

- [x] 1.1 Reopen DEV-BOOT-008, rename its task card to the separated deployment scope, and link this change.
- [x] 1.2 Update the affected `solutions/` baseline and production deployment documents from single-application packaging to independent frontend/backend artifacts.

## 2. Build boundaries

- [x] 2.1 Remove Maven frontend resource copying and update the artifact script to build/hash frontend and backend separately under one version.
- [x] 2.2 Replace the single application Dockerfile with independent backend and frontend Dockerfiles plus Nginx runtime configuration.
- [x] 2.3 Rewrite JAR/Docker startup documentation for two images and external API routing/configuration.

## 3. Verification and handoff

- [x] 3.1 Run frontend typecheck/build and backend tests/package; verify the JAR excludes frontend static files and both artifacts have hashes.
- [x] 3.2 Build both Docker images, inspect non-root/healthcheck/port metadata, and run context/socket/Compose negative checks without starting containers.
- [x] 3.3 Update DEV-BOOT-008 execution record and task index; leave the task in `02.done` for user manual validation.
