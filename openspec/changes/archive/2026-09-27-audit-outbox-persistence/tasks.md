## 1. Domain and digest contract

- [x] 1.1 Add canonical audit payload normalization, SHA-256 digest calculation, chained `AuditEvent`, and append-only integrity checks.
- [x] 1.2 Add `OutboxEvent` with idempotency, delivery-attempt and safe-payload validation.

## 2. Schema and MyBatis-Plus persistence

- [x] 2.1 Add V10 migration for outbox idempotency, audit immutability triggers, digest checks, and pending-delivery index.
- [x] 2.2 Add audit/outbox entities, mappers, explicit mappings, repository ports and isolated MyBatis-Plus implementations.
- [x] 2.3 Add transactional audit/outbox writer and register governance mappers.

## 3. Verification

- [x] 3.1 Add domain tests for canonical digest stability, chain verification and forbidden payload fields.
- [x] 3.2 Add repository, transaction and migration contract tests; run backend compile and test suite.
