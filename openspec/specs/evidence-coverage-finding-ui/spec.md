# evidence-coverage-finding-ui Specification

## Purpose
TBD - created by archiving change evidence-coverage-finding-ui. Update Purpose after archive.
## Requirements
### Requirement: Evidence overview query is redacted and role protected
The system SHALL provide `GET /api/v1/tasks/{taskId}/evidence` for an operator and SHALL reject a pre-publication viewer request with HTTP 403. The response MUST omit field-policy allowlists and other non-public field values.

#### Scenario: Operator reads evidence overview
- **WHEN** an authenticated operator requests a task evidence overview
- **THEN** the response contains snapshots, coverages, findings, and optional computation witnesses using the approved view fields

#### Scenario: Viewer reads before publication
- **WHEN** a viewer requests the same endpoint before publication
- **THEN** the service returns HTTP 403 and no evidence payload

### Requirement: Temporal and coverage uncertainty is explicit
The UI SHALL display current, future, and undetermined effective classes, and SHALL display COMPLETE, PARTIAL, and UNKNOWN coverage states without interpreting PARTIAL or UNKNOWN as a full-range no-match result.

#### Scenario: Partial coverage is present
- **WHEN** a coverage record is PARTIAL or UNKNOWN
- **THEN** the UI shows that state and any uncovered paths or failures

### Requirement: Deterministic findings expose computation witness
The UI SHALL show a finding's statement and status together with its computation witness when present, including algorithm/rule versions, input/output digests, source snapshot identifiers, and recompute status.

#### Scenario: Finding has a witness
- **WHEN** a deterministic finding is returned with a witness
- **THEN** the UI renders the witness metadata and does not expose raw model output

