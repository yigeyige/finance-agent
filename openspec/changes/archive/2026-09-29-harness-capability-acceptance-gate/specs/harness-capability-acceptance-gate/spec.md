## ADDED Requirements

### Requirement: Six-capability acceptance matrix

The repository SHALL provide a versioned acceptance matrix for Context Compression, controlled Workspace, controlled Skills, controlled Subagents, Long-term Memory, and Docker Sandbox, including functional, security, failure, evidence, and manual verification fields.

#### Scenario: Capability record is complete

- **WHEN** the matrix is parsed
- **THEN** all six capabilities have a version, at least one check in each category, and an explicit manual-required flag

### Requirement: Harness security boundaries

The acceptance gate SHALL preserve workspace path isolation, permission/field/tool allowlists, approval-only memory activation, and a Docker socket-free sandbox profile.

#### Scenario: Production boundary remains allowlisted

- **WHEN** the acceptance script checks capability metadata and deployment files
- **THEN** no capability declares unrestricted tools, scope expansion, Docker socket mounts, or automatic memory evidence authority

### Requirement: Offline-first verification

The repository SHALL provide a script that validates the acceptance matrix without starting applications or containers by default.

#### Scenario: Static check has no side effects

- **WHEN** the script runs without `-Execute`
- **THEN** it reports zero network/container calls and preserves the need for manual runtime verification
