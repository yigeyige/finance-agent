## 1. Domain and application boundary

- [x] 1.1 Extend the authoritative state transition command/service to bind a confirmed scope revision and allow only the documented scope-confirmation states.
- [x] 1.2 Add a reusable task audit appender for minimal chained events and refactor state-event persistence to use it.
- [x] 1.3 Add task-intake commands, responses and transactional application service for draft creation, premise append and immutable scope confirmation.

## 2. REST and contract source

- [x] 2.1 Add validated REST DTOs and Controller endpoints for task creation, premise append and scope confirmation with explicit actor/idempotency headers.
- [x] 2.2 Add versioned JSON Schemas, OpenAPI paths and valid/invalid contract fixtures; keep forbidden prompt/reasoning fields absent.

## 3. Verification and records

- [x] 3.1 Add service, controller, domain and contract tests covering provenance, strict JSON, no unconfirmed scope execution and audit minimization.
- [x] 3.2 Run Maven compile/tests and contract verification; update the task record and task index with evidence.
