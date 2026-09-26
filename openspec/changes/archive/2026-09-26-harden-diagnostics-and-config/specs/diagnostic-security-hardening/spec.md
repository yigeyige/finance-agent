## ADDED Requirements

### Requirement: Diagnostic exposure fails closed by default
The system SHALL not register the custom diagnostics API unless `finance-agent.diagnostics.api-enabled` is explicitly enabled, and public health output SHALL not reveal named internal component statuses.

#### Scenario: Default runtime configuration
- **WHEN** the diagnostics API enablement property is absent or false
- **THEN** `GET /api/v1/diagnostics` returns 404 and public health output does not contain database, model, or source component names

#### Scenario: Controlled diagnostic enablement
- **WHEN** the diagnostics API enablement property is explicitly true
- **THEN** the read-only diagnostics API is registered and returns only its defined non-sensitive status contract

### Requirement: Probe failures are safely traceable
The system SHALL classify diagnostic probe failures as safe statuses and SHALL emit bounded structured telemetry containing the component and failure classification without raw exception messages, stack traces, credentials, URLs, prompts, or SQL text.

#### Scenario: Database probe connection failure
- **WHEN** the database connection attempt throws an SQL exception
- **THEN** the component status is DOWN with a safe reason and telemetry contains only approved safe fields

### Requirement: Database probe timeout is configuration-driven
The system SHALL take database connect, socket, and validation timeouts from validated runtime configuration and SHALL not alter the configured JDBC URL to apply timeouts.

#### Scenario: Configured timeout is used
- **WHEN** a database diagnostic probe runs with configured timeout values
- **THEN** it passes those values as driver connection properties and uses the configured validation timeout
