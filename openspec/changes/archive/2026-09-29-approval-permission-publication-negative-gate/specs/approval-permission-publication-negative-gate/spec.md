## ADDED Requirements

### Requirement: Versioned negative request cases

The repository SHALL provide a deterministic fixture with negative request samples for unconfirmed scope, stale/expired approvals, binding drift, cross-task permits, missing professional confirmation, stale review, concurrent publication, and VIEWER privilege escalation.

#### Scenario: Every rejection is auditable

- **WHEN** the fixture is parsed
- **THEN** every case has a synthetic request, expected rejection status/decision, stable audit event name, and no credential or real business value

### Requirement: Role and publication boundary contract

The implementation SHALL preserve the server-side rule that VIEWER can read a published packet but cannot approve, review, publish, hand off, or close; publication SHALL require current draft/review bindings and remain idempotent for a replayed publication identity.

#### Scenario: Unauthorized role is rejected

- **WHEN** a VIEWER submits a mutating task or packet request
- **THEN** the request is rejected with HTTP 403 and no mutation is allowed

#### Scenario: Stale publication cannot publish

- **WHEN** draft, review, scope, field/template binding, or recheck is stale
- **THEN** publication is rejected or returns the conflict decision and no new revision is created

### Requirement: Offline-first request verification

The repository SHALL provide a script that validates the fixture without network calls by default and only scans user-provided response/audit evidence when explicitly requested.

#### Scenario: Static validation is side-effect free

- **WHEN** the script runs without `-Execute`
- **THEN** it performs no HTTP or database operation and reports the required case count and status
