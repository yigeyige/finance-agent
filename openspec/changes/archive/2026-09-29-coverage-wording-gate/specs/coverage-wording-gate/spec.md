## ADDED Requirements

### Requirement: Coverage-aware packet wording

The packet validation boundary SHALL distinguish COMPLETE, PARTIAL, and UNKNOWN coverage and SHALL require limitations for incomplete coverage.

#### Scenario: Incomplete coverage cannot claim full scope

- **WHEN** coverage is PARTIAL or UNKNOWN and the packet contains an absolute full-scope claim
- **THEN** validation fails and the packet remains unavailable for publication

### Requirement: Fact source classification

The packet validation boundary SHALL prevent RAG, memory, and model hints from being represented as current facts.

#### Scenario: Non-authoritative hint is not a current fact

- **WHEN** a current fact is marked with source kind RAG, MEMORY, or MODEL
- **THEN** validation fails and the content must be moved to undetermined or limitations

### Requirement: Business impact qualification

When business impact is not confirmed, the packet SHALL require pending items and SHALL reject deterministic impact wording.

#### Scenario: Unconfirmed impact remains qualified

- **WHEN** business impact confirmation is false and recommendations contain a deterministic impact claim
- **THEN** validation fails

### Requirement: Offline scenario verification

The repository SHALL provide a versioned fixture, a script that validates it without network access by default, and an audit-friendly result summary for manual review.

#### Scenario: Static fixture check is side-effect free

- **WHEN** the validation script runs without an execution switch
- **THEN** it checks all required scenarios and reports zero network and database calls
