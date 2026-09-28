# published-packet-lifecycle Specification

## Purpose
TBD - created by archiving change published-packet-handoff-close-ui. Update Purpose after archive.
## Requirements
### Requirement: Shared published view
The system SHALL return only the latest published packet revision for a task to OPERATOR and VIEWER, including checked time, revision number, source versions, coverage summary, limitations and handoff status.

#### Scenario: Viewer reads published revision
- **WHEN** a VIEWER requests the published packet view for a task with a published revision
- **THEN** the response contains that immutable revision and no draft-only data

#### Scenario: Viewer reads before publication
- **WHEN** a VIEWER requests a task without a published revision
- **THEN** the system rejects the request and does not expose the draft

### Requirement: Handoff
The system SHALL allow only OPERATOR to append one handoff for the published revision and move the task from PUBLISHED to HANDED_OFF without changing the revision.

#### Scenario: Valid handoff
- **WHEN** an OPERATOR submits owner, external process and downstream status references
- **THEN** the handoff is audited, the task becomes HANDED_OFF, and the published revision remains byte-for-byte unchanged

### Requirement: Closure is not remediation completion
The system SHALL require an owner reference before closing a handed-off task and SHALL return an explicit remediation-not-confirmed state.

#### Scenario: Close handed-off task
- **WHEN** an OPERATOR closes a task with an existing handoff
- **THEN** the task becomes CLOSED, the revision remains unchanged, and `remediationComplete` is false

#### Scenario: Close without handoff
- **WHEN** an OPERATOR attempts to close without an owner reference
- **THEN** the system rejects the request and leaves the task state unchanged

