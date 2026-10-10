## MODIFIED Requirements

### Requirement: Complete business schema comments
系统 SHALL 通过前向 Flyway 迁移，为 `iam`、`task`、`evidence`、`packet`、`harness`、`knowledge`、`governance` schema 中的每张业务表及其每个字段写入中文 PostgreSQL 注释。新增业务表的迁移 MUST 同时写入其表和字段注释；迁移 MUST 不改写既有迁移文件，也 MUST 不修改既有业务数据。

#### Scenario: Login credential table is introduced
- **WHEN** 数据库执行创建 IAM 密码凭据表的迁移
- **THEN** 该迁移同时写入密码凭据表及其全部字段的中文注释，且不修改既有用户或角色数据
