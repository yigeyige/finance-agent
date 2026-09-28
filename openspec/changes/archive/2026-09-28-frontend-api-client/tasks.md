## 1. Contract boundary

- [x] 1.1 Define task, public event, error and enum types in the generated boundary.
- [x] 1.2 Add strict unknown-enum and extra-field runtime validators with Chinese errors.

## 2. REST client

- [x] 2.1 Extend Axios client with request ID, idempotency key and row-version headers.
- [x] 2.2 Add typed ApiError and status-specific Chinese error mapping while preserving compatibility helpers.

## 3. SSE client

- [x] 3.1 Add cursor-aware URL construction, event parsing and last-event tracking.
- [x] 3.2 Expose explicit resume/close behavior and reject malformed or unknown events.

## 4. Verification

- [x] 4.1 Run frontend typecheck and production build.
- [x] 4.2 Record contract/typecheck evidence and update the task index.
