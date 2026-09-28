## MODIFIED Requirements

### Requirement: DeepSeek adapter uses the locked OpenAI-compatible contract

The system SHALL construct DeepSeek Chat Completions models with the default base URL `https://api.deepseek.com`, default model `deepseek-flash`, endpoint `/chat/completions`, an API key supplied only through environment-backed configuration, and thinking effort set to high by default.

#### Scenario: Default model configuration is offline-safe

- **WHEN** the local or test profile is loaded without a key
- **THEN** the default model settings are available, Stub mode remains enabled, thinking options are not sent to a provider, and no HTTP request is made

#### Scenario: Live model construction requires a key

- **WHEN** a live model is requested without an API key
- **THEN** construction fails with a safe configuration error that does not contain the missing or configured secret

### Requirement: Model calls preserve bounded execution and response metadata

The adapter SHALL pass tool schemas, structured response configuration, thinking-high, and bounded execution timeout to AgentScope, SHALL support Reactor cancellation, and SHALL surface model errors without automatic model switching.

#### Scenario: HTTP stub receives a Chat Completions request

- **WHEN** the adapter is pointed at a local HTTP stub with a valid key
- **THEN** it sends a Chat Completions request to `/chat/completions`, carries the thinking-high option only in the provider request, and exposes model response metadata and usage without exposing the key or reasoning content

#### Scenario: Model failure pauses the current call

- **WHEN** the provider returns an error or rate-limit response
- **THEN** the adapter propagates the error, clears transient reasoning, and does not select another model
