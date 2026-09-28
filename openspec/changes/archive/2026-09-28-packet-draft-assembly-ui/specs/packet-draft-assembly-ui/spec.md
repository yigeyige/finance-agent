## ADDED Requirements

### Requirement: Packet draft is assembled from bounded structured sections
The system SHALL assemble a packet draft from problem, premises, scope, sources, current/future/undetermined facts, computations, professional judgments, recommendations, business confirmations, validation preparation, and handoff sections. The canonical JSON SHALL be authoritative and SHALL not contain an unbounded model narrative field that can override fact sections.

#### Scenario: Complete structured input is submitted
- **WHEN** an operator submits all required sections for the task's current scope and a published runtime bundle
- **THEN** the service stores one current draft with canonical JSON and a 64-character content digest

#### Scenario: Required evidence section is missing
- **WHEN** a required section or one of the three fact-class arrays is missing or has the wrong JSON type
- **THEN** validation fails before the draft is stored

### Requirement: Business impact and HTML snapshot are explicit and bound
The validator SHALL require `businessConfirmations.impactConfirmed` as an explicit boolean and `pendingItems` as an array. The HTML snapshot SHALL be generated from the canonical JSON and SHALL expose the same digest; PDF generation is out of scope.

#### Scenario: Business impact is not explicitly confirmed
- **WHEN** the business confirmation object omits `impactConfirmed` or supplies a non-boolean value
- **THEN** the draft is rejected rather than inferring impact from configuration

#### Scenario: HTML preview is requested
- **WHEN** a valid draft is returned
- **THEN** the response includes canonical JSON, its digest, and an escaped HTML snapshot carrying that digest

### Requirement: Draft API protects scope and overwrite boundaries
The API SHALL require an operator identity and idempotency key, verify the task/current scope and published runtime bundle, and reject overwriting an existing current draft.

#### Scenario: Existing current draft is submitted again
- **WHEN** an operator submits another draft for the same task while a current draft exists
- **THEN** the service returns a state conflict and leaves the existing draft unchanged
