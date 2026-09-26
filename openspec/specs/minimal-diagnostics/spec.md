# Minimal diagnostics Specification

## Purpose

为最小 Spring Boot 骨架提供安全、可定位且不泄露敏感内容的健康、请求关联、依赖状态和结构化日志诊断能力，支持人工启动验收与故障定位。

## Requirements

### Requirement: Health readiness distinguishes core dependencies

The system SHALL expose liveness and readiness health groups, with readiness distinguishing the database, model stub, and source stub without exposing credentials or raw exception messages.

#### Scenario: Healthy configured dependencies

- **WHEN** the database probe succeeds and model/source stubs are enabled
- **THEN** readiness reports UP and exposes only the named component statuses

#### Scenario: Dependency failure is distinguishable

- **WHEN** a database configuration is missing or the read-only probe fails
- **THEN** the database component reports DOWN while the response contains no URL, username, password, or exception text

### Requirement: Diagnostic API is read-only and non-sensitive

The system SHALL expose `GET /api/v1/diagnostics` with overall status, component statuses, and correlation IDs, and SHALL NOT return configuration values, credentials, prompt/response content, reasoning, or stack traces.

#### Scenario: Request context is returned safely

- **WHEN** a request supplies valid correlation headers
- **THEN** the response returns the accepted IDs and echoes trace/invocation headers without changing their values

#### Scenario: Missing context is generated

- **WHEN** trace or invocation headers are absent
- **THEN** the service generates valid IDs, returns them in the response, and makes them available to logs

### Requirement: Structured logs carry bounded correlation context

The system SHALL emit structured JSON console logs containing only bounded `traceId`, `taskId`, `stepId`, and `invocationId` context fields plus approved diagnostic fields.

#### Scenario: Allowed fields are logged

- **WHEN** a diagnostic event contains approved status fields
- **THEN** the JSON log contains those fields and the current correlation IDs

#### Scenario: Sensitive fields are blocked

- **WHEN** an event contains a secret, token, password, forbidden field, full prompt/response, or reasoning text
- **THEN** the sanitizer rejects or replaces the value with `[REDACTED]` and the raw value is absent from output

### Requirement: Verification is self-contained

The system SHALL provide unit/contract tests for positive and negative diagnostic behavior without requiring application startup, a database, a model, or a source system.

#### Scenario: Static verification passes

- **WHEN** the documented Maven test and compile commands run
- **THEN** the tests verify health, context, API safety, and log filtering deterministically
