## ADDED Requirements

### Requirement: Profile configuration is environment-overridable
The system SHALL provide `local`, `test`, and `prod` Spring profiles, and SHALL expose environment-variable overrides for the server port, database connection, DeepSeek model settings, workspace root, execution budget, identity mode, storage mode, and strict field policy.

#### Scenario: Local profile loads safe development defaults
- **WHEN** the application runs with the `local` profile and no optional environment variables are set
- **THEN** the configuration binds successfully with Stub model support, local workspace/storage defaults, and a development port

#### Scenario: Production profile accepts environment injection
- **WHEN** the application runs with the `prod` profile and the documented environment variables are supplied
- **THEN** the typed configuration object contains the injected values without reading a `.env` file

### Requirement: Configuration binding is typed and secret-safe
The system SHALL bind the `finance-agent` namespace to a typed configuration object covering database, DeepSeek, workspace, budget, identity, storage, and field-policy settings, and SHALL NOT expose secret values through its string representation or validation errors.

#### Scenario: Configuration fields bind to their declared types
- **WHEN** numeric budget and boolean strict-policy environment values are supplied
- **THEN** they are bound as numeric and boolean fields rather than retained as raw strings

#### Scenario: Secret-safe diagnostics
- **WHEN** configuration validation reports an error
- **THEN** the message contains property names and does not contain API keys, database passwords, or complete connection strings
