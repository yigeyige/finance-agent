## Context

The existing field scanner protects JSON payload boundaries, but long-term memory is text and needs semantic deny rules. The first slice is an in-memory gateway with explicit lifecycle records; a later persistence adapter can preserve the same policy boundary.

## Decisions

1. **Approval lifecycle.** A proposed entry starts `PENDING_APPROVAL`; only an explicit approver can make it `ACTIVE`; active entries can be `WITHDRAWN` and are never searchable afterward.
2. **Text policy.** Reject configuration/settings, credentials/tokens, personnel identifiers, task conclusions/business decisions, and prompt-injection instructions. Store only abstract experience text with a content digest.
3. **Exact scope retrieval.** Search requires the same source task category and applicability scope. Cross-category or cross-scope entries return no results, preventing accidental reuse.
4. **Planning-only contract.** Search returns `PlanningMemoryHint` with entry metadata and abstract text. The gateway has no evidence or state transition method, and the hint explicitly reports `evidence=false`.
5. **Synthetic example.** Provide one fixed non-sensitive hint about checking logical relationships; it contains no source values or business decisions.

## Verification

- Safe proposal/approval/search and withdrawal tests.
- Sensitive content and prompt-injection negative tests.
- Exact scope/category isolation and planning-only result tests.
