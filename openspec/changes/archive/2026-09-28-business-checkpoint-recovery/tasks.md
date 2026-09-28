## 1. Runtime binding and checkpoint persistence

- [x] 1.1 Add checkpoint runtime bundle binding to the domain/entity/mapping and create the V10 migration.
- [x] 1.2 Add checkpoint safe-point enum, record command and service that validates current task/session binding before append.

## 2. Recovery decision

- [x] 2.1 Add recovery request, decision/result types and a service that revalidates task, session, step, scope, approval and runtime bundle.
- [x] 2.2 Integrate lease and invocation status classification into RESUME, REPLAY_RESULT, NEW_APPROVAL_REQUIRED and MANUAL_RECONCILIATION decisions.
- [x] 2.3 Keep recovery side-effect free so Worker/API callers can reuse the decision without bypassing human approval or acquiring a lease.

## 3. Verification and task evidence

- [x] 3.1 Add recovery matrix tests for compatible, stale, runtime-drift, lease-conflict and invocation-state cases.
- [x] 3.2 Add migration/repository contract assertions for runtime binding and safe checkpoint types.
- [x] 3.3 Run backend tests and strict OpenSpec validation, then update the task card and index before archive/commit.
