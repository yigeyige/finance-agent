## 1. Diagnostic exposure and traceability

- [x] 1.1 Add a default-disabled configuration switch for the custom diagnostics API and hide public health component names.
- [x] 1.2 Emit bounded, sanitized structured telemetry when diagnostic probes fail.
- [x] 1.3 Bind and use validated database diagnostic timeout settings without rewriting JDBC URLs.

## 2. Safe runtime configuration

- [x] 2.1 Add semantic binding constraints and typed identity/storage modes.
- [x] 2.2 Apply production allow-list validation, including rejection of source stubs.

## 3. Dependency boundary and verification

- [x] 3.1 Move the compile-only AgentScope compatibility probe from production to test sources.
- [x] 3.2 Update and add deterministic unit, MVC, and configuration tests for the hardened behavior.
- [x] 3.3 Run OpenSpec validation and the backend Maven test suite.
