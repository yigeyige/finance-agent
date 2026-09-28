# packet-publication-recheck-ui Specification

## Purpose
TBD - created by archiving change packet-publication-recheck-ui. Update Purpose after archive.
## Requirements
### Requirement: Publication triggers a full-scope recheck
The publication API SHALL compare the approved baseline with the current full-scope snapshots before writing a revision. Added, removed, field, effective-class, source-version, rule-version, or template-version changes SHALL make the draft stale and block publication.

#### Scenario: Scope changed before publication
- **WHEN** the current snapshot list differs from the approved baseline
- **THEN** the service marks the current draft `STALE`, moves the task to `DRAFT_PACKET`, returns the deterministic diff, and writes no published revision

#### Scenario: No scope change is found
- **WHEN** baseline and current snapshots are equivalent
- **THEN** the service proceeds to the atomic publication transaction

### Requirement: Revision, audit, outbox, and task state are atomic
The successful publication SHALL write an immutable packet revision, `PACKET_PUBLISHED` audit event, outbox event, and PUBLISHED task state in one transaction. A failure in any write SHALL roll back the complete publication.

#### Scenario: Publication succeeds
- **WHEN** draft, approved review, digest, scope, runtime, and recheck all match
- **THEN** the response contains the revision and digest, and the task is PUBLISHED

### Requirement: Publication is idempotent and immutable
The API SHALL use publication ID as an idempotency key and return the existing revision for a repeated request. It SHALL reject stale drafts, old reviews, mismatched canonical/HTML digests, and attempts to overwrite an existing published revision.

#### Scenario: Same publication ID is retried
- **WHEN** the same publication ID is submitted after a successful publication
- **THEN** the service returns the original immutable revision without another write

