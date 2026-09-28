## Why

Skills are a versioned Harness extension point, but the project has no catalog or lifecycle gate. Loading an unreviewed or modified skill could widen tool or field access and could let a running task drift to a different version.

## What Changes

- Add immutable skill package metadata with DRAFT/TESTED/PUBLISHED/RETIRED lifecycle, content and test evidence digests, approver and declared tool/field permissions.
- Add a catalog service that validates permissions against the authoritative tool catalog and field policy, loads only published and digest-matching packages, and freezes a published version for a task/runtime bundle.
- Add a synthetic investigation skill that returns deterministic, non-production evidence without reading real systems.
- Record lifecycle approval events for audit and add negative contract tests.

## Impact

- New `harness.application` skill catalog, loader, freeze model and synthetic skill.
- No new dependency, database migration or dynamic code execution.
