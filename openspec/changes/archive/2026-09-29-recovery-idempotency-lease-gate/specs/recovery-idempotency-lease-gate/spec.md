## ADDED Requirements

### Requirement: Versioned recovery fault matrix

The repository SHALL provide a deterministic matrix for interruption after approval, invocation intent, result submission, computation, draft assembly, and publication transaction, plus lease, duplicate request, unknown result, model pause, and budget exhaustion cases.

#### Scenario: Recovery outcome is explicit

- **WHEN** the matrix is parsed
- **THEN** every case declares its input state, expected recovery decision, and zero/one side-effect expectations without real identifiers or payloads

### Requirement: Recovery safety invariants

The recovery gate SHALL assert zero double execution, zero duplicate packet revisions, zero approval bypass, and manual reconciliation for unknown invocation results.

#### Scenario: Unknown result is not replayed

- **WHEN** invocation status is UNKNOWN
- **THEN** recovery decision is `MANUAL_RECONCILIATION` and no automatic tool execution is allowed

### Requirement: Offline-first verification

The repository SHALL provide a script and read-only SQL template that validate the matrix and aggregate evidence without starting workers or returning raw payloads.

#### Scenario: Static check has no side effects

- **WHEN** the script runs without `-Execute`
- **THEN** it performs zero network/database calls and reports all required fault points
