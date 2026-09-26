## 1. Planning and migration boundary

- [x] 1.1 Complete the custom task metadata and OpenSpec artifacts
- [x] 1.2 Move the database bootstrap script out of the Flyway migration location without changing its SQL content

## 2. Audit migration

- [x] 2.1 Add V8 audit columns and user foreign keys to all 27 domain tables
- [x] 2.2 Update static migration validation to require V1–V8 and ignore platform bootstrap SQL
- [x] 2.3 Update migration documentation and domain model solution with audit-field semantics

## 3. Verification

- [x] 3.1 Add migration contract tests for audit columns and V0 exclusion
- [x] 3.2 Run static checks, backend compilation/tests, and OpenSpec validation
- [x] 3.3 Execute V8 in the local development database and verify all audit columns
