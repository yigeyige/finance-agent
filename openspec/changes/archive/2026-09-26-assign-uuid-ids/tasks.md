## 1. Entity strategy

- [x] 1.1 Change all IAM, governance and task persistence Entity primary keys from `IdType.INPUT` to `IdType.ASSIGN_UUID`
- [x] 1.2 Add a shared persistence strategy check so UUID Entity mappings cannot regress to manual-only ID assignment

## 2. Verification

- [x] 2.1 Add tests for automatic UUID assignment and explicit UUID preservation at Mapper insert boundaries
- [x] 2.2 Run compile, repository tests, full tests, migration verification and strict OpenSpec validation
- [x] 2.3 Update task execution record and persistence documentation with the UUID strategy decision
