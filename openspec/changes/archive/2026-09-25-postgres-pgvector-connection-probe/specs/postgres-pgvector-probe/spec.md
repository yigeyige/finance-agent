## ADDED Requirements

### Requirement: Environment-driven PostgreSQL and pgvector probe
The system SHALL provide a standalone Java probe that reads `FINANCE_AGENT_DB_URL`, `FINANCE_AGENT_DB_USERNAME`, and `FINANCE_AGENT_DB_PASSWORD` from the process environment, without defaulting missing credentials, and performs only read-only PostgreSQL/pgvector checks.

#### Scenario: Missing credentials fail closed
- **WHEN** the probe starts without a non-blank required database environment variable
- **THEN** it exits with a configuration error and identifies the missing key without attempting a default password or printing a secret

#### Scenario: Healthy database and extension are reported
- **WHEN** the configured database accepts a read-only connection and exposes the expected PostgreSQL and pgvector versions
- **THEN** the probe reports an OK status, the observed versions, and a successful vector type operation

#### Scenario: Dependency failures are distinguishable
- **WHEN** the database is unreachable, the PostgreSQL version mismatches, or the vector extension is missing/mismatched
- **THEN** the probe returns a deterministic non-zero status for the corresponding failure class without creating database objects

### Requirement: Database preparation remains outside application startup
The system SHALL document `finance_agent` database and application-role preparation as an external administrative step, and SHALL NOT create databases, roles, extensions, schemas, or passwords during application startup or probe execution.

#### Scenario: Preparation guidance is safe to reuse
- **WHEN** a developer reads the database preparation documentation
- **THEN** it contains placeholder-only administrative examples, environment-variable wiring, and an explicit boundary that the application performs no administrative DDL
