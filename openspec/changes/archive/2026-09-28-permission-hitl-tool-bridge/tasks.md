## 1. Permission boundary

- [x] 1.1 Add Harness permission middleware and fixed request contract.
- [x] 1.2 Ensure bypass modes never produce a business permit.

## 2. HITL bridge

- [x] 2.1 Add structured waiting/approved/rejected/expired events and short-lived pending requests.
- [x] 2.2 Delegate approved requests to the existing ToolGateway only.
- [x] 2.3 Add contract tests for two-layer authorization, rejection, bypass and no dynamic fallback.

## 3. Verification

- [x] 3.1 Run targeted tests, Maven verify and strict OpenSpec validation.
