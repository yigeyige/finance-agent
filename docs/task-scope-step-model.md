# 任务、范围、前提和步骤模型验证

DEV-DATA-003 复用 V3/V8 迁移中的 task 表，通过 MyBatis-Plus 3.5.17 Mapper 仓储访问。范围修订只允许追加，任务更新携带 `row_version`，活动租约由部分唯一索引保护，工具调用由 `idempotency_key` 唯一约束保护。

任务持久化 Entity 的 UUID 主键统一使用 MyBatis-Plus `IdType.ASSIGN_UUID`；Entity 主键以 `String` 接收生成结果，映射层与领域 `UUID` 转换，数据库迁移和 UUID 列类型不变。

## 约束核对

```sql
SELECT indexname, indexdef
FROM pg_indexes
WHERE schemaname = 'task'
  AND indexname = 'ux_one_active_lease_per_task';

SELECT conname, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE connamespace = 'task'::regnamespace
  AND conrelid IN ('task.scope_revision'::regclass,
                   'task.step_approval'::regclass,
                   'task.tool_invocation'::regclass);
```

## 负向用例

- 使用相同 `task_id` 和活动状态插入第二条租约，应被 `ux_one_active_lease_per_task` 拒绝。
- 使用相同 `step_id`、`revision_no` 插入第二条审批，应被唯一约束拒绝。
- 使用相同 `idempotency_key` 插入第二条工具调用，应被唯一约束拒绝。
- 以不存在的 `scope_revision_id` 插入审批，或以不存在的步骤/审批插入工具调用，应被外键拒绝。
- 用旧 `row_version` 更新任务，仓储返回 `false` 且不改变任务行。
