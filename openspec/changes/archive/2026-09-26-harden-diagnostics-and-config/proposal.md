## Why

The minimal diagnostics implementation currently exposes component status without an authorization boundary, silently classifies probe failures, and accepts unsafe production configuration values. These gaps conflict with the backend security, traceability, and configuration requirements.

## What Changes

- Disable the custom diagnostics API by default until the scheduled IAM capability can authorize it, while retaining health probes for controlled operational use.
- Prevent public health output from exposing named internal components.
- Add bounded, non-sensitive failure telemetry for diagnostic probes.
- Validate runtime configuration invariants, use production allow-lists, and reject source stubs in production.
- Centralize database diagnostic timeouts in configuration instead of mutating JDBC URLs at runtime.
- Move the compile-only AgentScope API probe out of production sources.

## Capabilities

### New Capabilities

- `diagnostic-security-hardening`: Safe diagnostic exposure, probe failure traceability, and bounded database probe behavior.
- `runtime-configuration-validation`: Validated diagnostic runtime configuration and production-safe mode validation.

### Modified Capabilities

- `minimal-diagnostics`: Restrict custom diagnostics exposure and health component disclosure while preserving safe internal dependency checks.

## Impact

- Affected code: diagnostics, configuration properties, production validation, AgentScope compatibility probe, and their tests.
- **BREAKING**: `GET /api/v1/diagnostics` is disabled by default and health responses no longer expose individual component names to public callers.
- No database migration, external service call, or runtime deployment is included.
