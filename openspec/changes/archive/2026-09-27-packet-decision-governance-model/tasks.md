## 1. Packet domain model

- [x] 1.1 Add packet status/decision enums and four domain records with audit fields and strict JSON/digest/version validation.
- [x] 1.2 Add review-to-draft consistency guard and published revision immutability boundary.

## 2. MyBatis-Plus persistence

- [x] 2.1 Add four packet persistence entities using `ASSIGN_UUID`, JSONB type handlers, and V5/V8 audit fields.
- [x] 2.2 Add four `BaseMapper` interfaces, application repository ports, and repository implementations including published-only revision query.
- [x] 2.3 Add explicit domain/entity mappings and register packet Mapper scanning without changing existing migrations.

## 3. Verification

- [x] 3.1 Add domain, mapping, repository, schema, and immutable-write contract tests.
- [x] 3.2 Run backend compile/tests and record evidence in the task file.
