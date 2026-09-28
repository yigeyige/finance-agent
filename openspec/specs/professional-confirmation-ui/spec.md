# professional-confirmation-ui Specification

## Purpose
TBD - created by archiving change professional-confirmation-ui. Update Purpose after archive.
## Requirements
### Requirement: Professional confirmation is per finding and audited
The system SHALL accept one professional decision per finding only when the request supplies the current scope revision, a non-empty rationale, a non-empty JSON evidence-reference array, and an idempotency key. The response SHALL include the decision, actor, timestamp, scope, and resulting finding status, and the service SHALL append an audit event using digests for free-text fields.

#### Scenario: Confirm a human finding
- **WHEN** an operator submits `CONFIRM` for a finding belonging to the task and current scope
- **THEN** the service stores the confirmation, changes the finding to `CONFIRMED`, and returns confirmation metadata

#### Scenario: Request more evidence
- **WHEN** an operator submits `NEEDS_MORE_EVIDENCE` with a reason and evidence references
- **THEN** the finding remains `DRAFT` and the decision is auditable as pending professional confirmation

### Requirement: Deterministic findings and stale requests are protected
The system SHALL reject deterministic findings, cross-task findings, stale scope revisions, duplicate confirmations, blank reasons, and invalid evidence references without changing the finding.

#### Scenario: Deterministic finding is submitted
- **WHEN** an operator attempts any professional decision on a deterministic finding
- **THEN** the service returns a conflict and does not update the finding

#### Scenario: Scope or task does not match
- **WHEN** the finding does not belong to the task or the supplied scope revision differs
- **THEN** the service returns a contract/state error and does not create a confirmation

### Requirement: The evidence page supports explicit individual decisions
The UI SHALL render a separate confirmation form for each non-deterministic finding, require a decision, rationale, and evidence reference, and display existing confirmation audit metadata. It MUST NOT offer a batch or reasonless confirmation action.

#### Scenario: Existing confirmation is loaded
- **WHEN** the evidence overview contains a professional confirmation
- **THEN** the UI displays the decision, actor/time metadata, rationale, and evidence references instead of another submit form

