## ADDED Requirements

### Requirement: P0 evidence index is complete and status-aware

The repository SHALL provide a versioned evidence index covering DEV-QA-001 through DEV-QA-010, with repository-relative artifact links and explicit `VERIFIED_STATIC` or `PENDING_USER` status.

#### Scenario: Static evidence index validation

- **WHEN** the P0 evidence index check runs in its default mode
- **THEN** it validates ten task entries, existing artifact links, allowed statuses, and reports zero network and database calls.

### Requirement: Zero-tolerance metrics remain conditional until user evidence

The repository SHALL record three zero-tolerance metrics with value zero while preserving a `PENDING_USER` status until runtime, scan, and concurrency evidence is signed.

#### Scenario: Missing user evidence cannot pass

- **WHEN** the fixture has pending user evidence or an empty sign-off
- **THEN** the check reports a conditional conclusion and does not claim P0 pass.

### Requirement: P1 inputs and sign-off are explicit

The repository SHALL document P1 inputs, known limitations, manual verification commands, and a user sign-off template without including secrets or real payloads.

#### Scenario: User completes final sign-off

- **WHEN** the user has supplied runtime evidence and signed the record
- **THEN** the evidence index can be updated with signer, timestamp, conclusion, and linked evidence without changing the static script's offline behavior.
