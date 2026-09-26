## Context

当前 V1–V7 已在本地 `finance_agent` 数据库建立 27 张领域表，但审计字段不一致。部分表已有 `created_at`/`updated_at`，部分表只有业务时间或完全没有统一审计时间；没有统一的创建人和更新人列。仓库中另有已提交的 `V0_create_database.sql`，它是平台建库/授权脚本，不应进入 Flyway 业务迁移扫描目录。

## Goals / Non-Goals

**Goals:**

- 通过 V8 一次性为 V1–V7 的 27 张表补齐四个统一审计字段。
- 保证字段类型、默认时间和人员外键规则一致，并兼容系统生成记录。
- 保留 V1–V7 原文不变，提供静态检查和本地数据库核对。
- 将 V0 平台准备脚本从 Flyway 迁移目录隔离。

**Non-Goals:**

- 不回写历史记录的真实创建人/更新人；无法推断的人员字段保持 NULL。
- 不在数据库中自动维护 `updated_by`，应用仓储或事务边界负责传入审计上下文。
- 不删除表、不重建数据库、不修改现有业务约束或数据。

## Decisions

1. **使用 V8 前向迁移。** 已安装的 V1–V7 不可修改；V8 对每张表执行 `ADD COLUMN IF NOT EXISTS`，便于本地直接验证并由 Flyway history 控制正式环境只执行一次。
2. **人员字段可空、带外键。** `created_by` 和 `updated_by` 使用 `uuid REFERENCES iam.agent_user(user_id)`，允许系统任务、迁移历史和无人值守 outbox/audit 记录为空；一旦提供值，必须引用已知用户。将人员列设为 NOT NULL 会阻断既有数据回填和系统生成事件。
3. **时间字段非空且默认当前时间。** 所有四个时间列统一为 `timestamptz NOT NULL DEFAULT now()`；已有时间列保留原语义，仅为缺失列补齐并为更新列设置默认值。
4. **隔离 V0。** 将 `V0_create_database.sql` 移到 `scripts/postgres-bootstrap.sql`，作为平台准备脚本；Flyway 只扫描 `classpath:db/migration` 中的 V1–V8，避免在业务库连接中执行 `CREATE DATABASE` 和广泛授权。
5. **不自动触发更新时间。** 本任务不添加触发器，避免在仓储事务尚未统一前改变更新语义；后续仓储实现必须在更新语句中设置 `updated_by` 和 `updated_at`。

## Risks / Trade-offs

- [Risk] 既有记录没有真实人员信息 → 人员列保持 NULL，并在应用审计上下文完善后对新写入强制填充。
- [Risk] 表结构已被本地直接 SQL 建立但没有 Flyway history → 本地验证记录为直接执行结果；正式环境必须使用 Flyway 基线/迁移流程，不手工伪造 history。
- [Risk] V0 已被其他脚本引用 → 移动后保留脚本内容，并在迁移说明中更新平台准备路径。
- [Risk] `updated_at` 仅有默认值不能自动追踪 UPDATE → 明确由后续仓储层负责写入，当前不引入隐式触发器。

## Migration Plan

1. 执行静态校验，确认 V0 不在 Flyway 迁移文件集合中且 V1–V8 连续。
2. 平台准备 pgvector 和数据库后，用迁移账号执行 Flyway V8；本地已直接执行 V1–V7 的库可先做对象核对再执行 V8 SQL。
3. 查询七个 schema 的全部表，确认四列、类型、默认值和外键均存在。
4. 若 V8 失败，保留事务回滚结果，修复权限/对象后重试；不修改 V1–V7 或删除业务表。

## Open Questions

- 后续仓储任务是否将四个审计字段抽象为统一基类/SQL 片段，待 `DEV-DATA-009` 实施时决定。
