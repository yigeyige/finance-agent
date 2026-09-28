## Context

仓库已有范围重查比较器和 revision/audit/outbox 事务发布器。本变更补充上层编排：先核对批准草稿，再执行重查，差异时标记草稿 stale，成功时将任务置为 READY_TO_PUBLISH 后调用既有原子发布器。

## Goals / Non-Goals
**Goals:**
- 发布命令触发基线与当前范围快照比较，捕获新增、删除、字段、有效期、来源版本、规则版本和模板版本变化。
- 无变化时在一个事务内追加不可变 revision、`PACKET_PUBLISHED` 审计、outbox 和 PUBLISHED 任务状态。
- 使用 publication ID 幂等返回原 revision。

**Non-Goals:**
- 不实现真实外部系统枚举；当前接口接收已完成的基线/当前快照，用户负责运行环境验证。
- 不实现 PDF、交接和发布查看。

## Decisions
- 发布请求绑定 draft ID、content digest、scope revision、runtime bundle、canonical JSON 和 HTML digest。
- 仅 APPROVE review 可发布；HUMAN_CONFIRMED finding 的确认门禁由 review service 承担。
- 重查差异时响应 409、任务回到 `DRAFT_PACKET`、草稿标记 `STALE`，不写发布 revision。
- 成功发布的审计 payload 只包含版本、摘要和范围 ID，outbox 使用 publication ID 作为幂等键。

## Verification

- 后端执行 `mvn -q test`、`mvn -q -DskipTests compile`。
- 前端执行 `npm run typecheck`、`npm run build`。
- 用户启动环境后抽查变化阻断、无变化发布、重复 publication ID、旧 review 和摘要不一致。
