## Context

专业确认表和领域对象已经存在，但缺少应用事务边界、finding 状态更新和前端操作入口。本变更复用现有 MyBatis-Plus、身份上下文、任务审计摘要链和证据总览。

## Goals / Non-Goals
**Goals:**
- 每次请求只处理一个 finding，理由和证据引用均为必填。
- 校验 finding 属于任务且仍处于当前范围修订，拒绝确定性 finding 的人工篡改。
- 将确认人、时间、决定、理由和证据引用回显为审计记录。

**Non-Goals:**
- 不提供批量确认，不覆盖既有确认。
- 不改变确定性规则、计算见证或原始模型输出。

## Decisions
- `POST /api/v1/tasks/{taskId}/findings/{findingId}/professional-confirmations` 作为单条写入契约，要求 `Idempotency-Key`。
- `CONFIRM`、`REJECT`、`NEEDS_MORE_EVIDENCE` 分别映射到 `CONFIRMED`、`WITHDRAWN`、`DRAFT`。
- 证据引用使用非空 JSON 数组存储；审计事件仅记录理由和引用摘要。

## Verification

- 后端执行 `mvn -q test`、`mvn -q -DskipTests compile`。
- 前端执行 `npm run typecheck`、`npm run build`。
- 用户启动环境后抽查三种决定、空理由、旧范围、跨任务、重复确认和 VIEWER 拒绝路径。
