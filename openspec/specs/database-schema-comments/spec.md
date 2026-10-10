## Purpose

为 finance-agent 业务数据库提供可迁移、完整且可维护的中文表和字段注释。

## Requirements

### Requirement: Complete business schema comments

系统 SHALL 通过一个高于当前最新版本的前向 Flyway 迁移，为 `iam`、`task`、`evidence`、`packet`、`harness`、`knowledge`、`governance` schema 中的每张业务表及其每个字段写入中文 PostgreSQL 注释。迁移 MUST 不改写既有迁移文件，也 MUST 不修改业务数据。

#### Scenario: Apply comments after existing schema migrations
- **WHEN** 已执行现有建表迁移的数据库执行该注释迁移
- **THEN** 每张业务表和字段均可从 PostgreSQL 元数据读取非空中文注释，且表结构和业务数据保持不变

### Requirement: Fail closed for undocumented objects

注释迁移 SHALL 对受管业务 schema 的每张表和字段要求存在受控说明映射；遇到未登记对象时 MUST 失败，不得静默跳过。

#### Scenario: A new field lacks a registered description
- **WHEN** 注释迁移执行时发现业务 schema 中存在未在说明映射登记的字段
- **THEN** 迁移失败并指出缺失说明的对象标识，且不继续忽略该字段

### Requirement: Static comment coverage validation

仓库 SHALL 提供静态校验，验证注释迁移文件版本、受管 schema 范围及其表和字段说明映射与建表迁移定义一致，且校验过程不得连接数据库或输出连接凭据。

#### Scenario: Validate the comment migration statically
- **WHEN** 开发者在未连接数据库的环境运行迁移静态校验
- **THEN** 校验确认所有业务表和字段均有说明映射，并在漏项、重复项或未知对象存在时返回非零状态
