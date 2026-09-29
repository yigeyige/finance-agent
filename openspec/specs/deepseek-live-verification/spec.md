# deepseek-live-verification Specification

## Purpose
TBD - created by archiving change deepseek-live-verification. Update Purpose after archive.
## Requirements
### Requirement: Offline-first DeepSeek live test

The repository SHALL provide a single command that performs no network call by default and calls the DeepSeek OpenAI-compatible endpoint only with an explicit execution switch and a user-provided environment API key.

#### Scenario: Default command is safe

- **WHEN** the script runs without `-Execute`
- **THEN** it reports static readiness, makes zero network calls, and does not read or print the API key

#### Scenario: Live call records safe metadata

- **WHEN** a user runs the script with `-Execute` and `DEEPSEEK_API_KEY`
- **THEN** it sends the minimal synthetic structured request and records only model, response ID, token counts, finish reason, and schema status

### Requirement: Sensitive content is not recorded

The live-test command SHALL NOT print or persist prompt, response body, reasoning content, authorization headers, or API keys.

#### Scenario: Provider response contains reasoning

- **WHEN** the response contains reasoning content
- **THEN** the result remains metadata-only and the reasoning content is not emitted

