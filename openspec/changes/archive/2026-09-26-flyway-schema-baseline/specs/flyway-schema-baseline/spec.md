## ADDED Requirements

### Requirement: Versioned schema migrations
系统 SHALL 提供按附件 A 依赖顺序排列的 Flyway V1–V7 迁移，并将业务对象放入 `iam`、`task`、`evidence`、`packet`、`harness`、`knowledge`、`governance` 七个 schema。

#### Scenario: Empty database migration
- **WHEN** 具有迁移权限的账号在已准备好数据库和 pgvector 扩展的空库执行迁移
- **THEN** V1–V7 按顺序成功执行，七个 schema、领域表、约束和索引均可查询

#### Scenario: Repeat migration
- **WHEN** 对已经成功迁移的同一数据库再次执行迁移
- **THEN** Flyway 使用 history 跳过已安装版本，不重复创建对象且命令成功返回

### Requirement: Migration history and strict version validation
迁移 SHALL 在 `public.finance_agent_schema_history` 记录每个版本，并 SHALL 在版本低于已安装基线、乱序或 checksum 漂移时拒绝继续。

#### Scenario: History is complete
- **WHEN** V1–V7 执行完成后查询 schema history
- **THEN** 查询结果包含 V1 至 V7 的成功记录、安装顺序、描述和校验状态

#### Scenario: Older or out-of-order version
- **WHEN** 迁移目录包含低于已安装版本的新增版本，或显式启用乱序迁移
- **THEN** Flyway validate/migrate 失败，并且不修改已安装业务对象

### Requirement: Platform preparation and least privilege boundary
迁移 SHALL 不创建数据库、角色、密码或 pgvector 扩展；应用账号 SHALL 不承担 DDL，扩展和迁移账号由平台准备。

#### Scenario: Extension preflight
- **WHEN** 目标库未安装 pgvector 扩展
- **THEN** V7 在引用 `vector(3)` 时失败并保留可诊断错误，迁移不会尝试提升权限或创建扩展

#### Scenario: Application role negative check
- **WHEN** 使用无 DDL 权限的应用账号执行迁移脚本或直接执行 CREATE SCHEMA
- **THEN** 数据库拒绝该操作，应用账号的现有业务权限不被提升

### Requirement: Explicit migration and validation commands
系统 SHALL 提供显式迁移命令、validate-only 命令、静态校验脚本、迁移说明和只读验收查询，且命令不得输出数据库密码或完整连接凭据。

#### Scenario: Static contract check
- **WHEN** 在不连接数据库的环境运行迁移静态校验
- **THEN** 校验七个迁移文件、版本命名、必需 schema、`vector(3)` 和禁止语句，并在不满足时返回非零退出码

#### Scenario: Validate-only
- **WHEN** 使用迁移账号执行 validate-only 命令且脚本与 history 一致
- **THEN** 命令报告校验成功且不执行新的 DDL
