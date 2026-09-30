## Context

`qa-core-synthetic-vertical-slice.ps1` contains Chinese strings in a no-BOM UTF-8 file. Windows PowerShell 5.1 parses `-File` input through its legacy default encoding and fails before execution. Its HTTP exception handler also assumes a `HttpWebResponse`; PowerShell 7 instead exposes `HttpResponseMessage`.

## Goals / Non-Goals

**Goals:**

- Run the same script unchanged from Windows PowerShell 5.1 and PowerShell 7.
- Preserve a non-2xx JSON response for expected-status assertions without logging credentials or full sensitive payloads.
- Keep default static execution offline.

**Non-Goals:**

- Change the local API contract, fixture sequence, response artifacts, or add a runtime/worker launcher.
- Change database configuration or expose response bodies in console output.

## Decisions

1. Save the script as UTF-8 with BOM. Windows PowerShell 5.1 recognizes the BOM while PowerShell 7 continues to parse the file as UTF-8. Resolve default fixture and output paths in the script body through `$MyInvocation.MyCommand.Path`, because Windows PowerShell 5.1 does not reliably populate `$PSScriptRoot` while evaluating parameter defaults. Read the no-BOM UTF-8 fixture with an explicit UTF-8 encoding so its Chinese JSON strings remain valid under Windows PowerShell 5.1.
2. PowerShell 7 uses `-SkipHttpErrorCheck`, then decodes its byte-array response content before evaluating expected statuses; Windows PowerShell 5.1 retains the exception branch and reads its response stream. A fallback helper first uses `ErrorDetails.Message`, then branches by response type: `HttpResponseMessage.Content.ReadAsStringAsync()` for PowerShell 7 and `GetResponseStream()` for Windows PowerShell. This keeps the existing expected-status semantics.
3. The actuator health check is an anonymous request, because test-identity and idempotency headers belong to business API calls and can alter health-observation behavior.
4. Add a focused static contract test that checks BOM presence, helper branches, response normalization, anonymous health invocation, and expected non-2xx parsing code without requiring a running application.
5. `IdType.ASSIGN_UUID` generates and stores String primary-key values; therefore repositories translate a domain `UUID` through the existing `PersistenceUuid.toStorage` before every `BaseMapper.selectById` call. This keeps domain types unchanged and avoids treating the scalar UUID as an entity parameter object.
6. Non-primary UUID columns remain domain `UUID` fields. A global PostgreSQL UUID TypeHandler is registered through the existing MyBatis-Plus `ConfigurationCustomizer`: it binds UUID values as JDBC `OTHER` and converts returned UUID text or objects back to `UUID`. This is a focused database adapter, does not add a dependency, and avoids manually converting every foreign-key and audit field to String.
7. The global API exception handler keeps the response contract safe but reuses the request traceId and emits an `ERROR` structured event containing only a bounded event name, component, exception class, and SQLSTATE classification. It deliberately omits exception message, stack trace, SQL, request payload, credentials and other untrusted values so the console can correlate a failure without leaking internals.
8. Flyway versions must be unique. The pre-existing `V10__checkpoint_runtime_binding.sql` conflicts with the audit/outbox contract migration that supplies `canonicalization_version`; it is therefore renamed to `V12__checkpoint_runtime_binding.sql`, after the existing V11 migration. A resource-list contract test prevents a duplicate version from being reintroduced.
9. PostgreSQL persists `timestamptz` values at microsecond precision. Audit event creation therefore truncates `occurredAt` to microseconds before computing its digest, so a persisted-and-reloaded event remains a valid digest-chain predecessor.
10. Spring Boot 4 MVC binds HTTP JSON through Jackson 3 (`tools.jackson`). Web request DTOs carrying nested JSON use its `JsonNode`; where the application still has a Jackson 2 domain contract, the controller converts only the serialized JSON at the boundary and delegates strict parsing to the existing canonical JSON component. Diagnostic telemetry reports a fixed root-cause category, not a raw exception message, request body, SQL, or stack trace.

## Risks / Trade-offs

- [PowerShell host-specific response implementation] → Test parseability in both installed hosts and keep the helper limited to response-content extraction.
- [Response content is absent or not JSON] → Keep the existing empty-content handling and fail with the saved response record instead of silently passing.
