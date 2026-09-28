## ADDED Requirements

### Requirement: Review is bound to the exact current draft
The review API SHALL require draft ID, content digest, scope revision, runtime bundle, decision, reviewer identity, and idempotency key. The service SHALL reject a non-current draft or any mismatch before inserting a review.

#### Scenario: Exact draft bindings are supplied
- **WHEN** an operator submits a review whose digest, scope revision, and runtime bundle match the current draft
- **THEN** the service stores the review and returns all bound values for human verification

#### Scenario: Digest or scope is stale
- **WHEN** the submitted digest, scope revision, runtime bundle, or draft status does not match
- **THEN** the service returns a conflict and does not append a review

### Requirement: Approval requires completed professional judgment
The service SHALL block `APPROVE` while any HUMAN_CONFIRMED finding for the task lacks a professional confirmation or is not `CONFIRMED`. Deterministic findings remain protected by their existing witness path.

#### Scenario: Professional confirmation is incomplete
- **WHEN** an operator attempts to approve a draft with an unconfirmed human finding
- **THEN** the service returns a conflict and leaves the draft and review history unchanged

### Requirement: Review is separately audited and visibly presented
The system SHALL append a `PACKET_DRAFT_REVIEWED` audit event and the UI SHALL show the exact content digest, scope revision, runtime bundle, decision, reviewer, time, and comment.

#### Scenario: Review is submitted from the page
- **WHEN** the user reviews the displayed draft
- **THEN** the page submits one review and renders the returned audit metadata without changing canonical JSON
