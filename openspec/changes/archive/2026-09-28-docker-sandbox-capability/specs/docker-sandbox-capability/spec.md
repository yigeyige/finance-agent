## ADDED Requirements

### Requirement: JAR/local sandbox is bounded and synthetic

The system SHALL expose a JAR/local `DockerFilesystemSpec` with network disabled, no host mounts, a non-sensitive environment allowlist, and bounded CPU, memory and execution time.

#### Scenario: Safe JAR/local spec is created

- **WHEN** the JAR/local profile is selected
- **THEN** the spec is enabled with no network, no mounts, no Docker socket and positive resource limits

### Requirement: Application container does not depend on Docker socket

The system SHALL mark Docker sandboxing disabled for the application-container profile and SHALL not require a Docker socket, host path mount or Compose service.

#### Scenario: Application image capability is probed

- **WHEN** the application-container profile is probed
- **THEN** the result is NOT_APPLICABLE with a socket-free reason and no attempt to start Docker

### Requirement: Sandbox helper uses synthetic non-sensitive data

The system SHALL provide a deterministic synthetic calculation that has no filesystem, network, credential or database access.

#### Scenario: Synthetic calculation is stable

- **WHEN** the helper receives bounded synthetic input
- **THEN** it returns the same digest-bearing result for the same input
