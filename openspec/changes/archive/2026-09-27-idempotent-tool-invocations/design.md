## Context

`task.tool_invocation` 已有审批绑定、请求/响应摘要、状态和 `idempotency_key` 唯一约束，`StepExecutionGuard` 也能生成执行许可，但两者尚未连接。仓储的无条件状态更新还可能覆盖终态。设计必须兼容现有 MyBatis-Plus 持久层、PostgreSQL 唯一约束和现有 `InvocationStatus` 枚举，并且不启动本地运行环境。

## Goals / Non-Goals

**Goals:**

- 生成稳定、可审计且可重建的调用幂等键。
- 通过服务边界实现“意图记录 → 运行 → 终态”的条件状态转换。
- 对已完成调用返回持久化结果，对 `UNKNOWN` 明确要求人工核对，对并发重复请求返回原记录。
- 保持现有表结构和依赖组合不变。

**Non-Goals:**

- 本变更不接入具体工具网关或真实外部系统。
- 不修改发布修订表结构；发布事务在写入前按不可变 `packetRevisionId` 查询已提交修订，已提交时直接重放，新的发布幂等键协议仍留给后续发布能力任务。
- 不自动重试 `UNKNOWN`，也不把明确失败复用为同一 invocation。

## Decisions

1. **规范化键使用结构化记录。** `ToolInvocationKey` 校验 UUID、正整数 `approvalRevision` 和 `invocationNo`，以固定 `taskId:stepId:approvalRevision:invocationNo` 字符串作为数据库键。相比调用方自由拼接，结构化对象可集中校验并保证不同入口生成同一键。
2. **服务先查后插并处理唯一键竞争。** `startIntent` 先查询已有记录；不存在时插入 `INTENT_RECORDED`。并发插入遇到唯一键冲突后重新读取记录并按同一规则返回，避免把数据库竞争暴露成重复执行。
3. **条件状态转换。** 仓储新增 `updateStatusIfCurrent`，SQL 同时匹配幂等键和期望旧状态。服务只允许 `INTENT_RECORDED → RUNNING → terminal`，并允许意图直接落为明确终态；终态不可被覆盖。
4. **结果分类。** `SUCCEEDED`/`PARTIAL`/`FAILED` 作为可重放或可按新 invocation 重试的确定结果；`UNKNOWN` 返回人工核对动作，调用方不得使用相同键再次执行。
5. **不新增框架。** 使用现有 Spring `@Service`、事务注解、MyBatis-Plus 和异常映射能力，不引入新的幂等组件，避免重复造轮子。

## Risks / Trade-offs

- [并发请求在插入后、外部调用前中断] → 保留 `INTENT_RECORDED`，重试只返回 `IN_PROGRESS`，由恢复任务/人工核对决定是否继续。
- [外部调用已产生副作用但结果未落库] → 标记 `UNKNOWN`，禁止盲重试，并保留错误码和时间供人工核对。
- [旧数据状态不完整] → 服务对终态和摘要做边界校验；仓储更新失败时读取当前记录并返回确定冲突。

## Migration Plan

1. 部署应用代码与测试；无需数据库迁移。
2. 新调用入口改用 `ToolInvocationService` 写意图和完成状态。
3. 回滚时保留现有仓储和表结构；移除服务调用不会删除历史调用记录。

## Open Questions

- 具体工具网关如何把外部响应摘要映射为 `SUCCEEDED`、`PARTIAL` 或 `UNKNOWN`，留给后续工具网关任务定义。
