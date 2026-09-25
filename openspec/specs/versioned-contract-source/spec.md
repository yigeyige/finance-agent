# versioned-contract-source Specification

## Purpose
TBD - created by archiving change contract-directory-generation-boundary. Update Purpose after archive.
## Requirements
### Requirement: Contract sources are versioned and closed by default
The system SHALL store an OpenAPI 3.1 document under `contracts/openapi` and independent Draft 2020-12 JSON Schemas under `contracts/json-schema`, and every schema SHALL include a semantic-versioned `$id` and explicitly set `additionalProperties` for object shapes.

#### Scenario: Contract inventory is discoverable
- **WHEN** a developer inspects the contracts directory
- **THEN** OpenAPI, JSON Schema, examples, and generation-boundary documentation are present in their designated directories

#### Scenario: Schema identity is stable
- **WHEN** a JSON Schema is loaded
- **THEN** it declares Draft 2020-12, a `schema://` `$id` ending in a semantic version, and explicit object closure behavior

### Requirement: One source feeds Java and TypeScript boundaries
The system SHALL document `contracts/` as the only source of API/domain contract truth, with Java generation or validation on the backend side and TypeScript output reserved for `frontend/src/api/generated`; handwritten duplicate DTOs SHALL NOT be treated as source contracts.

#### Scenario: Generated output destinations are explicit
- **WHEN** a later type-generation task is implemented
- **THEN** it can locate the backend and frontend output boundaries without inventing a second source directory

