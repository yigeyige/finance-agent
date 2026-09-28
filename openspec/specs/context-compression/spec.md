# context-compression Specification

## Purpose
TBD - created by archiving change context-compression. Update Purpose after archive.
## Requirements
### Requirement: Compression output is a bounded, reference-only summary

The system SHALL produce a structured compression summary containing only whitelisted task/scope/premise/evidence/finding/current-step references, short summaries for existing references, and an explicit set of unknown items; the summary SHALL NOT contain raw snapshots or new evidence claims.

#### Scenario: Valid compression preserves references and unknowns

- **WHEN** a compression summary contains registered references, matching digests, bounded summaries and the complete expected unknown-item set
- **THEN** the summary validates and remains distinguishable from evidence records

#### Scenario: Summary cannot upgrade itself to evidence

- **WHEN** a summary adds an unregistered evidence ID or labels a free-text summary as a new finding
- **THEN** validation fails and no compressed context is returned

### Requirement: Compression validates authoritative reference integrity

The system SHALL reject fabricated reference IDs, digest mismatches, unsupported reference types, overlong summaries and missing unknown items; failures SHALL be safe and direct the caller to manual scope reduction.

#### Scenario: Snapshot or digest is forged

- **WHEN** a reference ID is absent from the authoritative catalog or its digest differs
- **THEN** validation fails with a compression error and does not continue model execution

#### Scenario: Unknown item is dropped

- **WHEN** the expected unknown-item set contains an item omitted from the compression summary
- **THEN** validation fails rather than silently treating the compressed context as complete

