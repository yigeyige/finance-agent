## ADDED Requirements

### Requirement: Skills are reviewed, immutable and permission-bounded

The system SHALL represent skills with versioned metadata, content and test evidence digests, lifecycle status, approver and declared tool/field permissions. Registration SHALL reject permissions outside the authoritative tool catalog or field-policy set.

#### Scenario: Unapproved or over-privileged skill is rejected

- **WHEN** a skill is registered with an unknown tool or field category
- **THEN** registration fails before it can be loaded or published

#### Scenario: Lifecycle approval is explicit

- **WHEN** a skill transitions from DRAFT to TESTED to PUBLISHED or RETIRED
- **THEN** the catalog requires the preceding status and records an approver-backed lifecycle event for publication/retirement

### Requirement: Tasks load only a digest-matching published skill version

The system SHALL load only PUBLISHED skills whose expected content digest matches, and SHALL freeze the selected skill version and digest against a task and runtime bundle for the task lifetime.

#### Scenario: Invalid skill cannot load

- **WHEN** a caller requests a DRAFT, RETIRED or digest-mismatched skill
- **THEN** loading fails without returning skill content

#### Scenario: Task skill version does not drift

- **WHEN** a task freezes a published skill and later requests a different version or runtime bundle
- **THEN** the catalog rejects the change

### Requirement: The example skill is synthetic

The system SHALL provide a deterministic synthetic investigation skill that does not access real data sources, credentials, network or filesystem content.

#### Scenario: Synthetic investigation returns stable output

- **WHEN** the approved synthetic scenario is executed
- **THEN** the result contains deterministic findings and evidence references only
