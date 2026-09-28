## Context

PacketReview 领域对象已有摘要、范围和运行版本精确匹配校验，但缺少应用事务、专业确认门禁和可操作页面。

## Goals / Non-Goals
**Goals:**
- 复核请求必须精确绑定当前草稿，内容变更或范围/运行版本变化时失败。
- 批准前检查所有 HUMAN_CONFIRMED finding 均已完成专业确认并处于 CONFIRMED。
- 记录独立的复核审计事件，并明确展示摘要。

**Non-Goals:**
- 不实现发布、重查或交接流程。
- 不强制首期双人复核，只保留后续策略扩展点。

## Decisions
- 使用 `POST /api/v1/tasks/{taskId}/packet-drafts/{draftId}/reviews`，要求 `Idempotency-Key` 和操作人员身份。
- `PacketReview.requireMatches` 负责 draft 摘要、scope revision、runtime bundle 和 CURRENT 状态的一致性。
- 复核理由允许为空；摘要、范围和运行版本始终回显，便于人工核对。

## Verification

- 后端执行 `mvn -q test`、`mvn -q -DskipTests compile`。
- 前端执行 `npm run typecheck`、`npm run build`。
- 用户启动环境后抽查摘要不符、旧范围、旧运行版本、专业确认未完成、批准和退回路径。
