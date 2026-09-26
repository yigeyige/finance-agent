## Context

The minimal diagnostics slice currently provides unauthenticated component-level output because IAM is not yet implemented. Probe failures are intentionally converted to safe statuses, but the conversion is not observable. Configuration is bound without semantic constraints, and the compile-only AgentScope compatibility check is present in production sources.

## Goals / Non-Goals

**Goals:**

- Make the default externally visible diagnostic surface non-sensitive and fail closed.
- Preserve internal readiness probing without exposing component names publicly.
- Make safe operational failure classification observable through structured logs.
- Reject invalid or development-only production values at binding or startup validation time.
- Keep database diagnostic timeouts explicit and configuration-driven.

**Non-Goals:**

- Implementing IAM, a permanent diagnostic authorization policy, database pooling, or real model/source probes.
- Starting the application, contacting a database, or changing deployment topology.

## Decisions

### Default-off custom diagnostic API

Use a `finance-agent.diagnostics.api-enabled` property with a default of `false` and conditionally register the controller. This fails closed without inventing temporary authentication before the planned IAM contract exists. An operator can explicitly enable it only in a controlled deployment.

### Public health hides components

Set public health component visibility to `never` while preserving internal component indicators and readiness configuration. This keeps low-detail liveness/readiness available without publishing dependency inventory.

### Safe, structured probe-failure logging

Catch probe exceptions only to map them to a safe status, then emit a bounded structured event containing component, outcome, exception class, optional SQL state, and the MDC correlation context. Raw exception messages, stack traces, connection information, and secrets are not logged from this path.

### Typed configuration plus production allow-lists

Add Bean Validation to budgets and timeouts, represent identity and storage modes as enums, and require explicit production modes rather than excluding only known development strings. Production validation also rejects a source stub. This avoids permissive unknown configurations.

### Database probe reads timeout properties

Pass PostgreSQL connection properties to `DriverManager` rather than appending query parameters to a JDBC URL. `Connection.isValid` receives the configured validation timeout. This has no global JVM timeout side effect and leaves connection strings unchanged.

### Test-only AgentScope API compilation probe

The existing probe is a dependency compatibility check, not runtime bootstrap behavior. Move it from `src/main` to `src/test` so runtime code contains no hardcoded vendor endpoint, model name, or direct environment lookup.

## Risks / Trade-offs

- [Existing diagnostic consumers receive 404 by default] → This is intentional fail-closed behavior; deployments that need the endpoint explicitly enable it until IAM is available.
- [Operational logs contain reduced error information] → The classification remains enough for correlation and remediation while protecting JDBC and provider details.
- [Enum binding rejects legacy unknown strings] → Production startup already rejects unsafe modes; explicit accepted values make the contract deterministic.
- [Driver-specific timeout properties] → PostgreSQL JDBC is the project baseline, so passing its documented properties is scoped and testable.

## Migration Plan

1. Deploy with the default-disabled diagnostics API and hidden health components.
2. If a controlled environment requires the custom API before IAM, set `FINANCE_AGENT_DIAGNOSTICS_API_ENABLED=true` temporarily and restrict it at the ingress layer.
3. Roll back by restoring the previous application properties and code version; no data migration is involved.

## Open Questions

- The later IAM task will define the final authorization rule and whether the custom endpoint remains necessary alongside an authenticated management surface.
