## ADDED Requirements

### Requirement: HTTP and SSE access is centralized
The system SHALL expose typed HTTP and SSE client modules under a shared service boundary rather than issuing requests directly from Vue templates or pages.

#### Scenario: HTTP client normalizes failures
- **WHEN** a service request fails with a network or API error
- **THEN** the client rejects with a user-safe Chinese error message and does not include credentials or raw response secrets

#### Scenario: SSE client reports lifecycle events
- **WHEN** a caller opens or closes an SSE stream
- **THEN** the service exposes typed open, message, error, and close callbacks without requiring page components to manage the native connection directly

### Requirement: Runtime secrets stay outside frontend assets
The system SHALL not embed API keys, database passwords, development identity tokens, or user-specific data in source files or production bundles.

#### Scenario: Production bundle contains no development credentials
- **WHEN** the frontend is built with default environment values
- **THEN** the emitted assets contain no API key, database password, or development identity token
