## MODIFIED Requirements

### Requirement: Health readiness distinguishes core dependencies
The system SHALL expose liveness and readiness health groups, with readiness internally distinguishing the database, model stub, and source stub without exposing credentials, raw exception messages, or named internal components in public health output.

#### Scenario: Healthy configured dependencies
- **WHEN** the database probe succeeds and model/source stubs are enabled
- **THEN** readiness reports UP without exposing named component statuses to public callers

#### Scenario: Dependency failure is distinguishable
- **WHEN** a database configuration is missing or the read-only probe fails
- **THEN** the database component reports DOWN internally while public health output contains no URL, username, password, exception text, or named internal component status

### Requirement: Diagnostic API is read-only and non-sensitive
The system SHALL expose `GET /api/v1/diagnostics` only when the explicit diagnostics API enablement property is true; when enabled it SHALL return overall status, component statuses, and correlation IDs, and SHALL NOT return configuration values, credentials, prompt/response content, reasoning, or stack traces.

#### Scenario: Default diagnostic API exposure is disabled
- **WHEN** diagnostics API enablement is absent or false
- **THEN** `GET /api/v1/diagnostics` returns 404

#### Scenario: Request context is returned safely when enabled
- **WHEN** diagnostics API enablement is true and a request supplies valid correlation headers
- **THEN** the response returns the accepted IDs and echoes trace/invocation headers without changing their values

#### Scenario: Missing context is generated when enabled
- **WHEN** diagnostics API enablement is true and trace or invocation headers are absent
- **THEN** the service generates valid IDs, returns them in the response, and makes them available to logs
