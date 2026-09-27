## 1. Domain and persistence contract

- [x] 1.1 Add `ToolInvocationKey` with validation and canonical idempotency-key formatting.
- [x] 1.2 Extend the invocation repository contract with conditional status transitions and implement the MyBatis-Plus update predicate.
- [x] 1.3 Add domain/service result types for execute, replay, in-progress, and manual-reconciliation outcomes.

## 2. Idempotency service

- [x] 2.1 Implement intent creation with the required audit fields and request digest.
- [x] 2.2 Handle duplicate keys and concurrent unique-key races by reading and classifying the existing invocation.
- [x] 2.3 Implement guarded running/completion transitions and reject terminal-state overwrites; preserve unknown results.
- [x] 2.4 Make an already committed packet revision replayable by its immutable revision id.

## 3. Verification and task evidence

- [x] 3.1 Add unit tests for key formatting, duplicate replay, in-flight requests, unknown results, and invalid transitions.
- [x] 3.2 Add repository/schema contract assertions for conditional updates and existing uniqueness constraints.
- [x] 3.3 Run backend tests and static checks, update the task card and task index only after verification.
