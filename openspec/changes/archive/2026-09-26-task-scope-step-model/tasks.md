## 1. Foundation

- [x] 1.1 Update DEV-DATA-003 metadata and define task domain enums/records
- [x] 1.2 Define JSONB scope value object with schema-version and object validation

## 2. Repositories

- [x] 2.1 Implement parameterized JDBC repositories for tasks, scope revisions and premises
- [x] 2.2 Implement step/approval repositories with scope and strategy-version bindings
- [x] 2.3 Implement optimistic task update and single-active-lease repository operations
- [x] 2.4 Implement idempotent tool-invocation repository and status updates

## 3. Verification and documentation

- [x] 3.1 Add domain and repository contract tests for append-only scope, approvals, conflicts and idempotency
- [x] 3.2 Add SQL constraint verification/manual database instructions without changing migrations
- [x] 3.3 Run compile/tests, migration verification and strict OpenSpec validation
