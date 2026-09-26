## 1. Strict JSON validation

- [x] 1.1 Replace `VersionedJson` bracket-only validation with Jackson parsing and explicit trailing-token rejection.
- [x] 1.2 Preserve object/array semantics, `requireObject()` behavior, and existing exception boundary.

## 2. Tests and coding guidance

- [x] 2.1 Add unit tests for valid object/array JSON, malformed JSON, trailing content, and object-only validation.
- [x] 2.2 Update `backend-coding-standards` Skill with the framework/component reuse rule and user-confirmation requirement for new components.
- [x] 2.3 Run backend tests and static checks, then record verification evidence in the task file.
