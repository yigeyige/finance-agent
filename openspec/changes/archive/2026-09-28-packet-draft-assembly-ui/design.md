## Context

packet 领域已有草稿、复核和发布实体，但缺少组装入口。本变更只创建当前范围下的草稿，规范 JSON 经过 canonical 化后计算摘要，HTML 由同一字符串转义生成。

## Goals / Non-Goals
**Goals:**
- 覆盖问题/前提、范围、来源、当前/未来/未知事实、计算、专业判断、建议、待业务确认、验证准备和交接占位。
- 缺少必要章节或业务影响显式字段时阻止草稿落库。
- 输出 JSON 摘要与 HTML 快照一致并可人工审阅。

**Non-Goals:**
- 不生成 PDF，不发布或复核草稿。
- 不允许模型自由文本字段改写事实，也不推断实际业务影响。

## Decisions
- 使用 `PacketDocumentValidator` 固定章节白名单；`facts` 必须包含 `current`、`future`、`undetermined` 三类数组。
- `businessConfirmations.impactConfirmed` 必须为布尔值，`pendingItems` 必须为数组。
- API 要求当前范围修订、已发布运行版本、操作人和幂等键，已有当前草稿时拒绝覆盖。
- HTML 仅展示已 canonical 化 JSON 并通过框架 HTML 转义，内容摘要与 JSON digest 相同。

## Verification

- 后端执行 `mvn -q test`、`mvn -q -DskipTests compile`。
- 前端执行 `npm run typecheck`、`npm run build`。
- 用户启动环境后抽查 JSON/HTML 摘要一致、缺依据阻断、自由文本和业务影响边界。
