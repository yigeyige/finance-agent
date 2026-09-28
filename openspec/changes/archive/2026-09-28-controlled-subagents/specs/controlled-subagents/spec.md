## ADDED Requirements

### Requirement: Subagents are registered with bounded roles

The system SHALL register only the evidence organizer, plan drafter and review assistant roles, each with explicit tool and field allowlists and no publication or scope-expansion authority.

#### Scenario: Unknown role is rejected

- **WHEN** a caller requests an unregistered subagent role
- **THEN** the bridge rejects the invocation before execution

### Requirement: Effective permissions are an intersection

The system SHALL compute effective subagent permissions as the intersection of task-approved tools/fields and the registered role allowlists, and SHALL reject requested permissions outside that intersection.

#### Scenario: Subagent requests extra authority

- **WHEN** a subagent request includes a tool or field outside the task approval or role allowlist
- **THEN** the invocation fails without executing the handler

#### Scenario: Subagent requests publication or scope expansion

- **WHEN** a subagent requests publication or a broader task scope
- **THEN** the bridge rejects the invocation

### Requirement: Results return to the owning task as public events

The system SHALL validate result task/user/invocation ownership and SHALL emit a minimal public completion event; subagent output SHALL not directly mutate task state or publish business results.

#### Scenario: Valid synthetic result is returned

- **WHEN** a registered role processes approved evidence references
- **THEN** the bridge returns a deterministic structured result and a `SUBAGENT_COMPLETED` event for the same task and invocation
