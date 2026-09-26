# Database Audit Fields

## Purpose

为财务共享智能运维 Agent 的全部领域表提供统一的创建人与更新时间审计字段。

## Requirements

### Requirement: Uniform audit columns on every domain table

七个业务 schema 下的每张领域表 SHALL 具备 `created_by`、`created_at`、`updated_by`、`updated_at` 四个审计字段；人员字段 SHALL 使用 UUID 并在非空时引用 `iam.agent_user(user_id)`，时间字段 SHALL 使用 `timestamptz`。

#### Scenario: All tables expose audit columns

- **WHEN** 查询 `iam`、`task`、`evidence`、`packet`、`harness`、`knowledge`、`governance` 下的业务表字段
- **THEN** 每张表均返回四个指定审计列，且列类型符合约定

#### Scenario: New row receives audit timestamps

- **WHEN** 插入一条未显式提供时间的领域记录
- **THEN** `created_at` 和 `updated_at` 使用当前时间默认值，人员列可由应用审计上下文提供

#### Scenario: Audit user referential integrity

- **WHEN** 写入非空的 `created_by` 或 `updated_by`
- **THEN** 数据库要求对应 UUID 已存在于 `iam.agent_user.user_id`，不存在时拒绝写入

### Requirement: Forward-only audit migration

审计字段 SHALL 通过新的 V8 前向迁移加入，且不得修改已安装的 V1–V7 文件；迁移 SHALL 不创建数据库、角色或扩展。

#### Scenario: Existing baseline is preserved

- **WHEN** 检查迁移目录和 Flyway history
- **THEN** V1–V7 内容保持不变，V8 是唯一新增业务迁移，并且迁移顺序连续

#### Scenario: Platform bootstrap is excluded

- **WHEN** Flyway runner 扫描业务迁移目录
- **THEN** 不加载包含 `CREATE DATABASE` 或广泛授权的 V0 平台准备脚本
