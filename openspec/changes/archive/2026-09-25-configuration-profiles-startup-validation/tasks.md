## 1. OpenSpec and task setup

- [x] 1.1 Complete proposal, design, delta specs, and task linkage for DEV-BOOT-004.

## 2. Typed configuration and profiles

- [x] 2.1 Add `FinanceAgentProperties` with nested database, DeepSeek, workspace, budget, identity, storage, and field-policy settings.
- [x] 2.2 Enable configuration-properties scanning from the Spring Boot application entry point.
- [x] 2.3 Add local, test, and prod profile files with environment-variable overrides and safe defaults.
- [x] 2.4 Add root `.env.example` and configuration documentation without real secrets.

## 3. Production startup validation

- [x] 3.1 Add a profile-aware startup validator with secret-safe error messages.
- [x] 3.2 Reject missing production required values and unsafe Stub/test/local/loose-policy combinations.

## 4. Verification

- [x] 4.1 Add configuration binding and production failure contract tests.
- [x] 4.2 Run targeted tests, full Maven tests, package compilation, and strict OpenSpec validation.
- [x] 4.3 Update the task record and task index with completion evidence.
