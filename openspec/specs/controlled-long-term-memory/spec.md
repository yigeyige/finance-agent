# controlled-long-term-memory Specification

## Purpose
TBD - created by archiving change controlled-long-term-memory. Update Purpose after archive.
## Requirements
### Requirement: Memory accepts only approved non-sensitive experience summaries

The system SHALL validate memory text against sensitive-content and prompt-injection policy before accepting a proposal, SHALL require explicit human approval before activation, and SHALL record content digest and approval metadata.

#### Scenario: Sensitive or injected memory is rejected

- **WHEN** a proposal contains configuration, personnel identifiers, credentials, business conclusions or prompt-injection instructions
- **THEN** the gateway rejects it without creating an active entry

#### Scenario: Approval is explicit

- **WHEN** an approver approves a safe pending entry
- **THEN** the entry becomes ACTIVE and an approval event records the approver and digest

### Requirement: Memory retrieval is scoped and withdrawal-aware

The system SHALL return only ACTIVE entries matching the requested task category and applicability scope, and SHALL never return WITHDRAWN or cross-scope entries.

#### Scenario: Cross-scope or withdrawn entry is unavailable

- **WHEN** a caller searches another category/scope or after withdrawal
- **THEN** no memory hint is returned

### Requirement: Memory affects planning only

The system SHALL return memory as an explicit planning hint that is not evidence and SHALL NOT expose an API that mutates task state or publishes business decisions from memory.

#### Scenario: Safe memory produces a planning hint

- **WHEN** an active safe entry matches the task category and scope
- **THEN** the result contains the abstract hint and `evidence=false` without source data

