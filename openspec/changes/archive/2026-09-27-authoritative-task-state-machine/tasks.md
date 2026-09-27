## 1. Domain state machine

- [x] 1.1 Add task transition value objects, explicit transition table and guard exceptions for normal, stale, pause/resume and cancellation paths.
- [x] 1.2 Add pure state matrix tests covering accepted and rejected transitions and required reasons/targets.

## 2. Transactional application boundary

- [x] 2.1 Add TaskStateTransitionService using task row lock, optimistic `row_version` update and transactional ordering.
- [x] 2.2 Add TaskStateEvent port/adapter that records `TASK_STATE_CHANGED` through the existing audit chain.
- [x] 2.3 Add service tests for no-side-effect rejection, version conflict, event failure and successful event ordering.

## 3. Verification and records

- [x] 3.1 Add architecture/contract checks preventing Controller/Harness direct state persistence bypass.
- [x] 3.2 Run backend compile/tests and update task/index evidence.
