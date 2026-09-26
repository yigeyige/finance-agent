## Why

V1–V7 建立的领域表中只有部分表具备时间字段，缺少统一的创建人、更新人审计信息，无法满足本地验证和后续生产追溯要求。需要用前向迁移补齐全部业务表，同时不修改已经执行的基线版本。

## What Changes

- 新增 V8 前向迁移，为七个业务 schema 的全部业务表补充 `created_by`、`created_at`、`updated_by`、`updated_at`。
- 时间字段统一使用 `timestamptz` 并默认取 `now()`；人员字段使用 UUID，并在非空时关联 `iam.agent_user`。
- 更新迁移静态检查、数据库说明和领域模型方案，明确系统生成记录允许人员字段为空但应用写入必须传递审计上下文。
- 将数据库创建/授权准备脚本与 Flyway 业务迁移隔离，避免 `V0_create_database.sql` 被 runner 误识别为业务迁移。

## Capabilities

### New Capabilities

- `database-audit-fields`: 为所有领域表提供统一的创建人与更新时间审计字段。

### Modified Capabilities

- 无。

## Impact

- 影响 `backend/src/main/resources/db/migration/`、Flyway 静态校验脚本、迁移说明和领域模型方案。
- 新增 V8 迁移会改变所有业务表的列结构；已有数据通过可空人员字段和时间默认值保持兼容。
- 需要在本地开发库执行 V8；生产环境需按前向迁移流程发布，不能编辑 V1–V7。
