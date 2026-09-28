## Why

Subagents are useful for bounded evidence organization, plan drafting and review assistance, but unconstrained spawning could expand task scope, fields, tools or publication authority. The Harness currently disables dynamic subagents without a business-side registry and result contract.

## What Changes

- Add a fixed catalog for three synthetic subagent roles: evidence organizer, plan drafter and review assistant.
- Compute effective permissions as the intersection of task-approved tools/fields and the registered subagent allowlist.
- Add a bridge that rejects extra tool/field requests, scope expansion and publication requests, validates result ownership, and emits a public completion event.
- Add deterministic synthetic handlers and result schemas with no real-data access.

## Impact

- New `harness.application` subagent catalog, permission model, bridge, result/event records and synthetic handlers.
- No new dependency, dynamic AgentScope spawn or database migration.
