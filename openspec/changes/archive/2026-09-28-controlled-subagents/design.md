## Context

AgentScope dynamic subagents are disabled in the current Harness factory. The first implementation therefore stays at the business boundary: a fixed catalog and a synchronous deterministic bridge model the authorization and result contracts without enabling arbitrary runtime agents.

## Decisions

1. **Fixed roles.** Register exactly three roles with explicit allowlists: evidence organizer, plan drafter and review assistant. Each descriptor is non-publishing and non-scope-expanding.
2. **Permission intersection.** A request supplies the task's approved tools/fields and requested tools/fields. The bridge computes `approved ∩ descriptor allowlist`; requested permissions must be contained in that intersection. It never grants descriptor permissions outside task approval.
3. **Result ownership and public event.** Results carry the originating task/user/invocation IDs, structured references and a digest. The bridge rejects mismatched IDs, publication or scope-expansion flags, and emits a minimal `SUBAGENT_COMPLETED` event.
4. **Synthetic handlers only.** Handlers operate on caller-provided evidence references and return deterministic summaries. They have no filesystem, network, database or credential access.

## Verification

- Catalog role and allowlist tests.
- Permission intersection and negative extra-tool/field/publish/scope tests.
- Result ownership and public event tests.
