## ADDED Requirements

### Requirement: Thinking content is transient and filtered at every boundary

The system SHALL keep DeepSeek thinking/reasoning content only in volatile memory for the active model/tool loop, SHALL clear it on completion, error, or cancellation, and SHALL omit it from events, exceptions, logs, checkpoints, database payloads, and SSE output.

#### Scenario: Reasoning marker is not persisted or emitted

- **WHEN** a provider response contains a unique `reasoning_content` marker
- **THEN** the final response and every mapped boundary payload omit the marker and the reasoning field

#### Scenario: Cancellation clears transient reasoning

- **WHEN** the active model stream is cancelled or fails
- **THEN** the ephemeral reasoning context is closed and cannot be read or restored by a later call

### Requirement: DeepSeek thinking protocol uses high effort without exposing reasoning

The system SHALL send the locked DeepSeek thinking-high option and preserve `reasoning_content` only while constructing the next tool-loop request; the user-facing response SHALL contain final content, tool calls, and non-sensitive metadata only.

#### Scenario: Tool loop forwards reasoning only in-memory

- **WHEN** a response contains a tool call and reasoning content
- **THEN** the next provider request may contain the reasoning field, while mapped events and persisted payloads do not

#### Scenario: Default offline invocation does not call a provider

- **WHEN** local/test Stub mode is active without a key
- **THEN** no DeepSeek HTTP request is made and no reasoning content is created
