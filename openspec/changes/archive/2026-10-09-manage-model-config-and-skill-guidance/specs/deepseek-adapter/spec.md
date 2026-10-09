## MODIFIED Requirements

### Requirement: DeepSeek adapter uses the locked OpenAI-compatible contract

The system SHALL construct DeepSeek Chat Completions models with API URL, model name, encrypted API key and reasoning effort resolved from the single enabled persisted model connection. It SHALL use endpoint `/chat/completions`; YAML and environment variables MUST NOT provide runtime API URL, API key or model name. A deployment-only encryption master key MAY be supplied through the runtime environment and MUST NOT be used as a model connection value.

#### Scenario: Enabled persisted model configuration is used

- **WHEN** a live model call starts and one valid model connection is enabled
- **THEN** the adapter decrypts the Key only in memory, uses that connection's API URL, model name and reasoning effort, and never exposes the Key

#### Scenario: Live model construction requires an enabled connection

- **WHEN** a live model is requested without an enabled configuration, encrypted Key or available deployment encryption key
- **THEN** construction fails with a safe configuration error that does not contain any secret

### Requirement: Model calls preserve bounded execution and response metadata

The adapter SHALL pass tool schemas, structured response configuration, the enabled connection's reasoning effort, and bounded execution timeout to AgentScope, SHALL support Reactor cancellation, and SHALL surface model errors without automatic model switching.

#### Scenario: HTTP stub receives a Chat Completions request

- **WHEN** the adapter is pointed at a local HTTP stub through an enabled persisted configuration with a valid Key
- **THEN** it sends a Chat Completions request to `/chat/completions`, carries the configured reasoning option only in the provider request, and exposes model response metadata and usage without exposing the Key or reasoning content

#### Scenario: Model failure pauses the current call

- **WHEN** the provider returns an error or rate-limit response
- **THEN** the adapter propagates the error, clears transient reasoning, and does not select another model
