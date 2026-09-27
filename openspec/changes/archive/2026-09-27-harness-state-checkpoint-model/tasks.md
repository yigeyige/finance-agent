## 1. Harness state domain

- [x] 1.1 Add `StateReference`, session/checkpoint/plan/workspace domain records with audit fields, digest and JSON validation.
- [x] 1.2 Add forbidden-field serializer policy and checkpoint compatibility guard for task/user/session/step/scope/approval bindings.

## 2. Schema and MyBatis-Plus persistence

- [x] 2.1 Add V9 migration for checkpoint user/scope bindings, plan revision, and workspace reference tables with constraints and audit fields.
- [x] 2.2 Add four MyBatis-Plus entities, mappers, application repository ports, and isolated repository implementations.
- [x] 2.3 Add explicit domain/entity mappings, serializer implementation, and register Harness Mapper scanning.

## 3. Verification

- [x] 3.1 Add domain, serializer, mapping, repository, migration, and forbidden-field contract tests.
- [x] 3.2 Run backend compile/tests and record evidence in the task file.
