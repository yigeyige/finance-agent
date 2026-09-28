## Context

The existing tool catalog already provides the authoritative tool code set and digest. Harness sessions already carry task and runtime bundle identities. Skills should be immutable descriptors selected by those boundaries, not executable scripts that can mutate state-machine or field-policy rules.

## Decisions

1. **Immutable package metadata.** A `SkillPackage` contains identity, task types, schema references, content, content digest, test evidence digest, allowed tools, allowed field categories, lifecycle status, approver and timestamps. The constructor verifies the content digest.
2. **Catalog-owned lifecycle.** `SkillCatalogService` is the only transition point. Legal transitions are DRAFT → TESTED → PUBLISHED → RETIRED. Publishing requires an approver and tested evidence; retired packages cannot be loaded.
3. **Permission intersection.** Registration rejects tool codes not present in the fixed `ToolCatalog` and field categories not present in the configured authoritative field-policy set. The skill model has no state-machine override or arbitrary policy fields.
4. **Digest and runtime freeze.** Loading requires PUBLISHED status and an expected content digest. `freezeForTask` stores task ID, runtime bundle ID, skill identity, version and digest; a second freeze cannot change the selected version or runtime bundle.
5. **Synthetic-only example.** `SyntheticInvestigationSkill` returns fixed findings and evidence references for a named scenario. It accepts no data source or filesystem client, so tests prove it does not access real data.

## Verification

- Lifecycle and approval transition tests.
- Unpublished/retired/digest-tampered/permission-superset negative tests.
- Task version freeze and synthetic skill deterministic output tests.
