## 1. Domain and persistence boundaries

- [x] 1.1 Add commands/results for bounded step proposal batches and approval creation/revocation.
- [x] 1.2 Add internal `ExecutionPermit`, execution request and guard validation for task/step/scope/approval bindings, expiry, versions and call limits.
- [x] 1.3 Extend step/approval repositories and MyBatis mappings only where the existing schema supports the new use cases; preserve append-only approval revisions.

## 2. Application services and audit

- [x] 2.1 Implement transactional step proposal application service with one recommendation, at most two alternatives, current-scope checks and state transition to `WAITING_STEP_APPROVAL`.
- [x] 2.2 Implement transactional approval application service with digest calculation, revision reuse for identical bindings, new revision creation for binding drift, and minimal audit events.
- [x] 2.3 Ensure permit issuance is internal-only and never serialized or included in audit payloads.

## 3. REST and contracts

- [x] 3.1 Add request/response DTOs and controllers for step proposals and step approvals with actor and idempotency headers, strict unknown-field rejection and bounded validation.
- [x] 3.2 Update OpenAPI and versioned JSON Schemas/examples for proposal, approval and minimal response contracts.

## 4. Verification and task records

- [x] 4.1 Add unit tests covering proposal cardinality, current-scope binding, approval revision immutability/reuse, and all permit rejection cases.
- [x] 4.2 Run Maven compile/tests, contract verification, OpenSpec strict validation, update the task index and execution record, and document manual verification handoff.
