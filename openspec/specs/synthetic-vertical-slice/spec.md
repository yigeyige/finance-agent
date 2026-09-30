# synthetic-vertical-slice Specification

## Purpose
TBD - created by archiving change core-synthetic-vertical-slice. Update Purpose after archive.
## Requirements
### Requirement: Versioned synthetic slice fixture

The repository SHALL provide a versioned, deterministic fixture that contains only synthetic identities and data and binds the task, scope, Stub model, source version, expected steps, recheck snapshots, and required evidence artifacts.

#### Scenario: Fixture is reproducible and safe

- **WHEN** the fixture is parsed twice with the same version and seed
- **THEN** the task identity, scope digest inputs, step sequence, source versions, and expected artifact list are identical and no credential, prompt, reasoning, or real business data appears

### Requirement: Explicit end-to-end runbook

The repository SHALL provide a script that validates the fixture without network access by default and only calls the local application when an explicit execution switch is supplied. The script MUST be directly parseable by Windows PowerShell 5.1 and PowerShell 7, preserve expected non-2xx local API response content for status assertions without printing response payloads to the console, and invoke the local synthetic evidence worker after a successful approved-step response before reading evidence.

#### Scenario: Static validation does not mutate runtime state

- **WHEN** the script runs without the execution switch
- **THEN** it validates required fixture fields, stage order, synthetic-only content, and output directory safety without calling the application or database

#### Scenario: Executed slice preserves stage evidence

- **WHEN** a user runs the script with the execution switch against a local application
- **THEN** it records responses for intake, premise, scope, proposal, approval, synthetic evidence execution, evidence, draft, review, recheck, publication, viewer view, handoff, and close, stopping with the last response preserved on failure

#### Scenario: Expected non-2xx response is portable across PowerShell hosts

- **WHEN** the stale publication request returns its expected non-2xx status under Windows PowerShell 5.1 or PowerShell 7
- **THEN** the script reads the response content through the host-compatible API and continues with the expected-status assertion

#### Scenario: Health check remains anonymous

- **WHEN** the script executes its initial local health check
- **THEN** it calls the actuator endpoint without synthetic identity or idempotency headers

### Requirement: Publication recheck and shared view evidence

The slice SHALL verify that a pre-publication scope addition invalidates the old review, and that a subsequent clean publication is visible as the same immutable view to OPERATOR and VIEWER.

#### Scenario: New reference invalidates review

- **WHEN** publication is attempted with a current snapshot containing the fixture's pre-publication added reference
- **THEN** publication is rejected as stale, no packet revision is returned, and the current draft is marked stale for reassembly and review

#### Scenario: Operator and viewer receive the same revision

- **WHEN** a clean recheck is published and both local identities request the published packet
- **THEN** both responses contain the same packet revision ID, content digest, canonical JSON, and HTML snapshot digest

### Requirement: Read-only evidence result package

The repository SHALL provide a read-only query template that can collect the task's state, approval, evidence, packet, audit, outbox, and handoff rows without exposing secrets or source raw payloads.

#### Scenario: Evidence query is bounded to one task

- **WHEN** the query is executed with a task identifier
- **THEN** every result is filtered by that task or its direct foreign-key revision and returns only IDs, statuses, versions, digests, timestamps, and safe summaries
