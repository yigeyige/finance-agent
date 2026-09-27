## 1. Mapper SQL and failure mapping

- [x] 1.1 Add parameterized task row-lock and ready-step `FOR UPDATE SKIP LOCKED` Mapper methods.
- [x] 1.2 Add repository infrastructure exceptions and PostgreSQL/MyBatis exception mapping without exposing SQL details.
- [x] 1.3 Make optimistic task update and active lease acquisition report explicit conflict outcomes.

## 2. Transaction services

- [x] 2.1 Add transactional task execution boundary for task lock and lease acquisition.
- [x] 2.2 Add atomic publication transaction service covering immutable revision, audit, outbox and task transition.
- [x] 2.3 Add read-only transaction facade for task/lease/published revision queries.

## 3. Verification and records

- [x] 3.1 Add SQL and transaction contract tests for locks, ordering, status guards, rollback ordering and exception mapping.
- [x] 3.2 Run backend compile/tests and update the task record and task index with evidence.
