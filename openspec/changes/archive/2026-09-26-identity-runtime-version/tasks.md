## 1. Foundation

- [x] 1.1 Update DEV-DATA-002 metadata and add Spring JDBC dependency/data source configuration
- [x] 1.2 Add identity and runtime-version domain records and status enums

## 2. Repositories and seed boundary

- [x] 2.1 Implement parameterized JDBC repositories for users, role bindings and runtime bundles
- [x] 2.2 Add published-only runtime bundle lookup and audit actor propagation
- [x] 2.3 Add local/test-only, explicitly enabled synthetic identity seed configuration

## 3. Verification

- [x] 3.1 Add repository contract tests for uniqueness, role revocation and published-state filtering
- [x] 3.2 Add production negative tests for synthetic seed registration and run compile/tests
- [x] 3.3 Validate OpenSpec and record manual database verification instructions
