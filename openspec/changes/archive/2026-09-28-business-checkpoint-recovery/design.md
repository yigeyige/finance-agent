## Context

`Checkpoint` 已绑定 task、user、session、step、scope、approval，并通过 `StateReference` 拒绝敏感字段；`ToolInvocationService` 已提供调用状态分类，`TaskLeaseService` 已提供租约生命周期。当前缺少把这些状态组合为一个可审计恢复判定的应用边界，且 checkpoint 尚未保存创建时的 runtime bundle。

## Goals / Non-Goals

**Goals:**

- 只在六类业务安全点追加 checkpoint，并保存最小安全状态引用及 runtime bundle 绑定。
- 恢复时重读权威业务状态，确保不会跨 task/user/session、步骤、scope、approval 或 runtime bundle 恢复。
- 将租约和工具调用状态映射为四类确定决策，供 Worker/API 复用。
- 对未知或可能已产生副作用的状态默认转人工核对。

**Non-Goals:**

- 不实现具体工具调用、模型调用、证据或发布业务。
- 不在恢复服务中获取新租约或执行外部副作用；Worker 仍负责过期租约接管。
- 不启动本地服务、不做真实故障注入；用户负责运行时手动验证。

## Decisions

1. **Checkpoint 只追加并记录运行版本。** 增加 `runtime_bundle_id` 列；旧历史记录为空时恢复直接要求重新审批，避免猜测旧版本。
2. **安全点白名单。** 应用命令只接受 `APPROVAL_GRANTED`、`INVOCATION_INTENT_RECORDED`、`RESULT_TRIMMED`、`DETERMINISTIC_COMPUTATION_COMPLETED`、`DRAFT_CREATED`、`PUBLICATION_REVIEWED` 六类类型，模型流中间状态不落 checkpoint。
3. **恢复先校验边界再分类副作用。** 先读取 checkpoint、task、session、step、scope、approval；任一绑定过期或运行版本不一致返回 `NEW_APPROVAL_REQUIRED`。当前租约由任务租约仓储判断，其他未过期 Worker 持有时转 `MANUAL_RECONCILIATION`。
4. **工具状态决策。** `SUCCEEDED`/`PARTIAL` 返回 `REPLAY_RESULT`；`INTENT_RECORDED` 或确定失败允许回到 `RESUME`；`RUNNING` 或 `UNKNOWN` 返回 `MANUAL_RECONCILIATION`，不自动重试可能已产生副作用的调用。
5. **复用现有端口。** 使用已有 Java record、Spring `@Service`、MyBatis-Plus 仓储和事务能力，不添加依赖或重复实现 JSON/摘要校验。

## Risks / Trade-offs

- [旧 checkpoint 没有 runtime bundle] → V10 保持历史可读但恢复视为 `NEW_APPROVAL_REQUIRED`。
- [租约在判定后过期] → 恢复服务只给决策，不执行调用；Worker 必须在执行前重新获取/确认租约。
- [调用状态与 checkpoint 写入不在同一事务] → 以工具调用持久化状态为权威，未知状态始终人工核对。

## Migration Plan

1. 执行 V10，为 checkpoint 增加可空 `runtime_bundle_id`，新服务写入时要求非空。
2. 部署应用代码后由 Worker/API 调用 checkpoint 记录与恢复判定服务。
3. 回滚应用代码不删除历史 checkpoint；迁移列保留以兼容已写入记录。

## Open Questions

- 具体 API 路由和 Worker 消息格式由后续 Harness/工作台任务定义，本变更先提供应用服务契约。
