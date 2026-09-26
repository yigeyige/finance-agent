## ADDED Requirements

### Requirement: Single application image runs as non-root with health check
The Docker image SHALL run the Spring Boot application as a dedicated non-root user, set an explicit working directory, expose the application port, and define an HTTP health check for `/actuator/health`.

#### Scenario: Container metadata is hardened
- **WHEN** a developer inspects the built image metadata
- **THEN** the configured user is not root, the work directory is `/opt/finance-agent`, and a health check is present

#### Scenario: Read-only runtime is documented
- **WHEN** a developer follows the container startup instructions
- **THEN** the command uses a read-only root filesystem with a bounded temporary filesystem and does not mount Docker socket

### Requirement: Deployment remains a single application without Compose
The repository SHALL provide one Dockerfile and SHALL NOT require Docker Compose, sidecar containers, or Docker socket access to run the application image.

#### Scenario: Single image startup is sufficient
- **WHEN** the user runs the documented Docker command with external configuration
- **THEN** one application container starts without Compose files or host Docker control access
