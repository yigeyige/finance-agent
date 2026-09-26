# separated-container-runtime Specification

## Purpose

定义前端 Nginx 静态镜像与后端 Java 镜像的独立运行、安全元数据和启动边界。

## Requirements

### Requirement: Frontend and backend run as separate hardened images
The repository SHALL provide independent frontend and backend Dockerfiles. The backend image SHALL run the executable JAR as a dedicated non-root user with `/actuator/health`; the frontend image SHALL serve the built SPA from a non-root Nginx process on port 8080.

#### Scenario: Backend image metadata is hardened
- **WHEN** the backend image is inspected
- **THEN** its user is not root, its working directory is `/opt/finance-agent`, its entrypoint starts the JAR, and an HTTP health check targets `/actuator/health`

#### Scenario: Frontend image serves the SPA
- **WHEN** the frontend image is inspected
- **THEN** it contains the compiled `index.html`, listens on port 8080, uses a non-root runtime, and defines a root-page health check

### Requirement: Separated startup does not require host Docker control
The startup documentation SHALL describe independent frontend and backend commands using the same version tag, read-only filesystems, bounded temporary storage, and external configuration; it SHALL NOT require Compose or a Docker socket.

#### Scenario: Independent startup commands are available
- **WHEN** a developer follows the documentation
- **THEN** the backend and frontend can be started as separate containers without mounting `/var/run/docker.sock` or relying on a Compose file

#### Scenario: Build context excludes sensitive and generated material
- **WHEN** either Docker build runs
- **THEN** `.env`, secret files, dependency caches, task/solution planning files, and local build outputs are excluded from the context and final runtime image
