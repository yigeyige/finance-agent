## Why

Long-term memory can improve planning but is unsafe if it captures configuration, personnel identifiers, credentials, task conclusions or business decisions. The Harness needs an approval, scope, withdrawal and sensitivity gate, and memory must remain a planning hint rather than evidence.

## What Changes

- Add a memory entry model with source task category, applicability scope, digest, approval and withdrawal lifecycle.
- Add a policy scanner rejecting sensitive content and prompt-injection instructions before proposal or approval.
- Add a gateway for propose/approve/search/withdraw, with exact category/scope matching and audit events.
- Return only planning hints; no evidence or task-state mutation API is exposed.

## Impact

- New `harness.application` memory policy, gateway, domain records and synthetic example.
- No new dependency, external memory store or database migration in this slice.
