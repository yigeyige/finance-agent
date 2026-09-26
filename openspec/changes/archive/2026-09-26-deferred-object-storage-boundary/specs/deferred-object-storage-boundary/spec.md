## ADDED Requirements

### Requirement: Object storage uses a path-free reference contract

The system SHALL expose a domain-neutral `ObjectStorageGateway` whose object references contain only a relative logical key, SHA-256 content digest, byte size, and media type, and SHALL NOT expose host absolute paths or credentials.

#### Scenario: Stored content returns a safe reference

- **WHEN** an allowed stream is stored with a logical key and media type
- **THEN** the gateway returns a reference containing the logical key, computed SHA-256 digest, byte size, and media type without a host path

#### Scenario: Core decision package storage remains PostgreSQL-bound

- **WHEN** the P0 application persists decision package JSON or HTML
- **THEN** the authoritative content remains in PostgreSQL and the object storage gateway is not required

### Requirement: Local and test storage is controlled and bounded

The system SHALL provide a local/test-only temporary-directory implementation that enforces relative logical keys, root isolation, a configured byte limit, and streaming digest calculation.

#### Scenario: Path traversal is rejected

- **WHEN** a caller supplies an absolute key, drive-qualified key, backslash, `.` segment, or `..` segment
- **THEN** the gateway rejects the operation with a path violation error and writes no object outside the configured root

#### Scenario: Oversized content is rejected

- **WHEN** the input stream exceeds the configured maximum byte size
- **THEN** the gateway stops or rejects the write with a size-limit error and removes any temporary partial file

#### Scenario: Valid content is stored and reopened

- **WHEN** a valid stream is written in local or test profile
- **THEN** the gateway stores it below the configured root and can reopen it by logical key without returning an absolute path

### Requirement: Production never silently falls back to local files

The system SHALL register a disabled object storage implementation in prod until a formal adapter is configured, and SHALL fail production startup when the adapter identifier is missing or set to `none`.

#### Scenario: Missing formal adapter fails closed

- **WHEN** prod configuration uses `OBJECT_STORE` with an empty or `none` adapter identifier
- **THEN** startup validation fails with a configuration key only and no local directory is selected

#### Scenario: Disabled gateway reports explicit unavailability

- **WHEN** code calls the object storage gateway in prod without a formal adapter
- **THEN** it returns a deterministic not-configured error rather than writing to a local or memory store

### Requirement: Storage failure contracts are explicit

The system SHALL distinguish unconfigured storage, path violations, size-limit violations, missing objects, and I/O failures with typed runtime errors and SHALL NOT use null or host paths as fallback values.

#### Scenario: Large or unavailable operations are diagnosable

- **WHEN** an operation cannot be completed because storage is unavailable or content exceeds policy
- **THEN** the caller receives the corresponding typed error category without credentials, raw local paths, or source exception text
