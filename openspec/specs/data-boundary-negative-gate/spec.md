# data-boundary-negative-gate Specification

## Purpose
TBD - created by archiving change data-boundary-negative-gate. Update Purpose after archive.
## Requirements
### Requirement: Versioned negative boundary fixture

The repository SHALL provide a deterministic fixture containing only synthetic identities, field names, a unique marker, scan targets, and expected zero-hit outcomes for forbidden persistence/model transmission and source writes.

#### Scenario: Fixture is safe and reproducible

- **WHEN** the fixture is parsed twice
- **THEN** its version, marker, field policy, target list, and expected outcomes are identical and no real payload, credential, prompt, or reasoning content is present

### Requirement: Explicit boundary scan

The repository SHALL provide a script that performs only static fixture validation by default and scans user-provided evidence files only when an explicit execution switch is supplied.

#### Scenario: Offline validation does not mutate runtime state

- **WHEN** the script runs without `-Execute`
- **THEN** it does not call the application or database and validates the fixture, required targets, forbidden keys, and zero-hit expectations

#### Scenario: Evidence scan blocks leakage

- **WHEN** the script runs with `-Execute` against a local evidence directory
- **THEN** it reports marker/forbidden-key hits per target and exits non-zero when any forbidden persistence, model transmission, or source write evidence is found

### Requirement: Read-only source and database gate

The source adapter and SQL evidence template SHALL expose only read operations and SHALL aggregate scan counts without returning raw payloads.

#### Scenario: Source write is rejected

- **WHEN** the source adapter contract is inspected or an optional write probe is recorded
- **THEN** no write method is available and a successful source write makes the gate fail

