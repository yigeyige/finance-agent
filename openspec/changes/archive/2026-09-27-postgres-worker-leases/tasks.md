## 1. Lease and claim boundary

- [x] 1.1 Extend lease domain/repository contracts with owner-bound heartbeat, release, expiry and takeover operations.
- [x] 1.2 Implement the transactional claim/takeover service that composes `SKIP LOCKED`, task/step state updates and lease persistence.
- [x] 1.3 Add SQL/domain contract tests for one active lease, owner mismatch, expiry boundary and rollback behavior.

## 2. Worker runtime and configuration

- [x] 2.1 Add validated `finance-agent.worker` properties, local/test defaults, and API/Worker datasource pool routing.
- [x] 2.2 Implement an isolated bounded Worker executor, scheduler and `SmartLifecycle` graceful stop boundary.
- [x] 2.3 Add Worker lifecycle/configuration tests proving disabled-by-default behavior, no new claims after stop and bounded parameters.

## 3. Task and verification records

- [x] 3.1 Update the DEV-RUN-004 task record and task index after implementation.
- [x] 3.2 Run Maven tests/verification and OpenSpec strict validation; record user-owned concurrency verification steps without starting services.
