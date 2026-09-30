## MODIFIED Requirements

### Requirement: Explicit end-to-end runbook

The repository SHALL provide a script that validates the fixture without network access by default and only calls the local application when an explicit execution switch is supplied. The script MUST be directly parseable by Windows PowerShell 5.1 and PowerShell 7, and MUST preserve expected non-2xx local API response content for status assertions without printing response payloads to the console.

#### Scenario: Static validation does not mutate runtime state

- **WHEN** the script runs without the execution switch
- **THEN** it validates required fixture fields, stage order, synthetic-only content, and output directory safety without calling the application or database

#### Scenario: Executed slice preserves stage evidence

- **WHEN** a user runs the script with the execution switch against a local application
- **THEN** it records responses for intake, premise, scope, proposal, approval, evidence, draft, review, recheck, publication, viewer view, handoff, and close, stopping with the last response preserved on failure

#### Scenario: Expected non-2xx response is portable across PowerShell hosts

- **WHEN** the stale publication request returns its expected non-2xx status under Windows PowerShell 5.1 or PowerShell 7
- **THEN** the script reads the response content through the host-compatible API and continues with the expected-status assertion

#### Scenario: Health check remains anonymous

- **WHEN** the script executes its initial local health check
- **THEN** it calls the actuator endpoint without synthetic identity or idempotency headers
