## 1. Shared backend foundation

- [x] 1.1 Add canonical JSON value/codec and SHA-256 digest utility with numeric, null, Unicode and array rules.
- [x] 1.2 Add shared UUID generator/converter and injectable UTC clock/time formatting types.
- [x] 1.3 Add Jackson ISO-8601 UTC configuration and application UTC properties.

## 2. Reuse and frontend boundary

- [x] 2.1 Refactor audit digest implementation to delegate canonical JSON and SHA-256 to shared foundation.
- [x] 2.2 Add Vue 2/TypeScript Asia/Shanghai time display utility without adding dependencies.

## 3. Verification

- [x] 3.1 Add fixed canonical JSON, digest, UUID, clock and serialization contract tests.
- [x] 3.2 Run backend compile/tests and frontend typecheck/build; record evidence in the task file.
