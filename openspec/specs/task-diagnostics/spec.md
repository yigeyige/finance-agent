# task-diagnostics Specification

## Purpose
TBD - created by archiving change task-diagnostics-ui. Update Purpose after archive.
## Requirements
### Requirement: Safe task diagnostic summary
The system SHALL provide a read-only task diagnostic summary containing status, current step, lease, approval validity, pause reason, last error code and recovery actions without exposing raw failure details.

#### Scenario: Distinguish paused and manual reconciliation states
- **WHEN** a task is model-paused, source-paused, budget-paused, recoverably failed, or has an UNKNOWN tool invocation
- **THEN** the diagnostic response identifies the stable state and presents a bounded human-readable action label

#### Scenario: Failure details remain redacted
- **WHEN** a stored error value contains SQL, credentials, prohibited fields or model reasoning content
- **THEN** the response returns a safe code or redacted marker and never returns the original value

### Requirement: Read-only diagnostic page
The system SHALL expose the diagnostic summary to authorized OPERATOR/VIEWER users through a page that does not execute task mutations.

#### Scenario: Authorized user views diagnostics
- **WHEN** an authorized user opens the task diagnostic route
- **THEN** the page shows the status cards and bounded recovery actions

