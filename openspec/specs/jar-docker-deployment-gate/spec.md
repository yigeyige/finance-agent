# jar-docker-deployment-gate Specification

## Purpose
TBD - created by archiving change jar-docker-deployment-gate. Update Purpose after archive.
## Requirements
### Requirement: Independent JAR and image deployment

The repository SHALL provide deployment checks for an independently deployable backend JAR, backend image, and frontend image using external PostgreSQL configuration and no Compose or Docker socket dependency.

#### Scenario: Deployment contract is explicit

- **WHEN** the static deployment check runs
- **THEN** it verifies artifact naming, work directory, port, external database variables, health/readiness endpoints, non-root users, and absence of Compose/socket mounts

### Requirement: Manual startup and stop evidence

The repository SHALL provide a checklist for user-run startup, health/readiness verification, and graceful stop for JAR and Docker modes.

#### Scenario: AI does not start runtime

- **WHEN** the static check runs without a build/start switch
- **THEN** it performs no container or external database operation and leaves runtime verification to the user

