## 1. Repository boundary

- [x] 1.1 Add user/session lookup, list and delete operations to the session repository port and MyBatis adapter.

## 2. AgentScope state adapter

- [x] 2.1 Add Jackson AgentState codec with schema and SHA-256 digest validation.
- [x] 2.2 Implement PostgreSQL AgentStateStore adapter with task/user/session and runtime-version checks.
- [x] 2.3 Add contract tests for restore, corruption, cross-user and stale runtime failures.

## 3. Verification

- [x] 3.1 Run targeted tests, Maven verify and strict OpenSpec validation; record that restart is user-verified.
