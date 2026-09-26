## 1. Dependency and configuration

- [x] 1.1 Add MyBatis-Plus 3.5.17 Boot 4 starter and replace JdbcTemplate configuration with DataSource/Mapper scan
- [x] 1.2 Add persistence Entity base mappings and PostgreSQL JSONB TypeHandler

## 2. Repository migration

- [x] 2.1 Migrate IAM and governance repositories to MyBatis-Plus Mapper implementations
- [x] 2.2 Migrate task repositories and preserve optimistic/lease/idempotency SQL behavior
- [x] 2.3 Remove JdbcTemplate repositories, imports and direct dependency; update tests

## 3. Verification

- [x] 3.1 Add Mapper/entity contract tests and context bean checks
- [x] 3.2 Update persistence documentation and baseline references
- [x] 3.3 Run compile/tests and strict OpenSpec validation
