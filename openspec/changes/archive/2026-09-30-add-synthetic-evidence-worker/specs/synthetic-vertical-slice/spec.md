## MODIFIED Requirements

### Requirement: Explicit end-to-end runbook

The repository SHALL provide a script that validates the fixture without network access by default and only calls the local application when an explicit execution switch is supplied. In execution mode, after a successful approved-step response, the script SHALL explicitly invoke the local synthetic evidence worker with the fixture task and identity before reading evidence.

#### Scenario: Static validation does not mutate runtime state

- **WHEN** the script runs without the execution switch
- **THEN** it validates required fixture fields, stage order, synthetic-only content, and output directory safety without calling the application or database

#### Scenario: Executed slice preserves stage evidence

- **WHEN** a user runs the script with the execution switch against a local application
- **THEN** it records responses for intake, premise, scope, proposal, approval, synthetic evidence execution, evidence, draft, review, recheck, publication, viewer view, handoff, and close, stopping with the last response preserved on failure
