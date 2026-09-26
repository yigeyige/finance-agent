## 1. OpenSpec and dependency setup

- [x] 1.1 Update the DEV-DATA-001 task metadata and execution record with the new change and current owner
- [x] 1.2 Add Flyway core and PostgreSQL database support dependencies without enabling Spring Boot startup migration

## 2. Migration implementation

- [x] 2.1 Add the explicit Java Flyway migration runner with migrate and validate-only modes
- [x] 2.2 Add V1 schema migration without role or extension creation
- [x] 2.3 Add V2–V7 domain migrations from attachment A, including vector(3) knowledge storage
- [x] 2.4 Add PowerShell migration and static verification scripts

## 3. Verification and documentation

- [x] 3.1 Add static migration resource contract tests and run backend compilation/tests
- [x] 3.2 Document platform preflight, commands, permissions, history queries, repeat-run and negative checks
- [x] 3.3 Run OpenSpec validation and record evidence in the task card
