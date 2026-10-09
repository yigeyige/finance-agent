## MODIFIED Requirements

### Requirement: DeepSeek adapter uses the locked OpenAI-compatible contract

The system SHALL construct DeepSeek Chat Completions models with API URL, model name, encrypted API key and reasoning effort resolved from the single enabled persisted model connection. It SHALL use endpoint `/chat/completions`; YAML and environment variables MUST NOT provide runtime API URL, API key or model name. A deployment-only encryption master key MAY be supplied through the runtime environment and MUST NOT be used as a model connection value.

#### Scenario: Enabled persisted model configuration is used

- **WHEN** a live model call starts and one valid model connection is enabled
- **THEN** the adapter decrypts the Key only in memory, uses that connection's API URL, model name and reasoning effort, and never exposes the Key

#### Scenario: Live model construction requires an enabled connection

- **WHEN** a live model is requested without an enabled configuration, encrypted Key or available deployment encryption key
- **THEN** construction fails with a safe configuration error that does not contain any secret

#### Scenario: Application starts before a model connection is enabled

- **WHEN** production configuration supplies a valid deployment encryption master key but no model connection is enabled
- **THEN** the application starts and a subsequent live model call returns a safe instruction to enable a model connection
