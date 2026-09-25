---
name: backend-coding-standards
description: >-
  Apply the finance_agent repository's backend Java coding standards when implementing,
  reviewing, or refactoring backend code. Use for layer boundaries, supported APIs,
  validation, transactions, logging, security, utility selection, and verification;
  do not use for frontend-only work or unrelated project-specific conventions.
---

# Backend Coding Standards

Use this skill for backend Java implementation and review in this repository. It is a
decision guide, not permission to broaden the requested change.

## Before changing code

1. Read the repository `AGENTS.md` and follow its workflow, safety, OpenSpec, and
   deployment constraints.
2. Read [`docs/backend-coding-standards.md`](../../../docs/backend-coding-standards.md)
   for the complete project standard.
3. Inspect the existing module and call chain before adding an abstraction. Reuse an
   existing capability or extract a focused component when that is compatible with the
   requested behavior.
4. Confirm the dependency baseline from the build configuration before using an API:
   Java 17, Spring Boot 4.0.4, Spring Framework 7.0.7, AgentScope Java 2.0.3 BOM,
   PostgreSQL JDBC 42.7.11, and pgvector Java SDK 0.1.6.

## Implementation rules

- Keep Controller/API code focused on protocol adaptation and boundary validation.
- Put use-case orchestration in application services, business invariants in the
  domain layer, and database/external calls behind infrastructure interfaces.
- Do not return persistence entities as public API contracts; use explicit DTO/VO or
  domain result types.
- Parameterize SQL and whitelist dynamic sort/filter fields. Define transaction scope
  around the use case and keep uncontrolled network calls outside long transactions.
- Preserve root causes when handling exceptions. Use the repository's unified error
  model instead of swallowing exceptions, returning `null`, or inventing a local
  response format.
- Use the repository logging facade with placeholder arguments. Never log secrets,
  tokens, credentials, full identifiers, or unmasked sensitive business data.
- Prefer the existing project utility/component, then a stable already-approved
  library, then Java 17 standard APIs. Do not add a dependency just to avoid a small,
  clear piece of code.
- Use `java.time` for new date/time code, explicit mapping for important DTO fields,
  and bounded/expiring locks or caches when concurrency or caching is required.
- Design idempotency for retries, message consumption, mutations, and external side
  effects.

## Verification

Match verification to risk. At minimum, run the relevant formatting/static checks,
compile check, and tests. For API, database, message, permission, or migration changes,
add the corresponding contract/integration/negative-path verification. If a check cannot
run, report the reason, uncovered scope, risk, and the user's required manual step.

Do not deploy, start the development environment, or perform runtime manual validation
unless the user explicitly requests it; this repository leaves those actions to the
user. Do not commit, merge, push, reset, or change branches unless explicitly authorized
by the repository instructions and the user.

## Review checklist

- Is the change inside the requested layer and dependency direction?
- Are input, permission, tenant/data, and failure boundaries explicit?
- Are SQL, logs, configuration, and secrets safe?
- Are retries/idempotency/transaction behavior defined where relevant?
- Are tests and static/compile verification proportional to the change?
- Did the change avoid unrelated project-specific rules from other repositories?
