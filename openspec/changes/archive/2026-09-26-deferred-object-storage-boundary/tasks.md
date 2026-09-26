## 1. Contract and configuration

- [x] 1.1 Define storage domain reference, gateway port, typed failures, and package boundaries.
- [x] 1.2 Add adapter ID, temporary root, and max-size configuration plus production guard.

## 2. Controlled implementations

- [x] 2.1 Implement local/test temporary gateway with streaming digest, size limit, safe logical keys, and cleanup.
- [x] 2.2 Implement prod disabled gateway and profile-based bean selection without local fallback.
- [x] 2.3 Add positive/negative tests for references, path traversal, size limits, missing objects, and production validation.

## 3. Documentation and verification

- [x] 3.1 Update configuration/build docs and solution baseline to record PostgreSQL decision-package storage and deferred object storage boundary.
- [x] 3.2 Run Maven tests/package and OpenSpec validation; update task/index and move the task to `02.done/`.
