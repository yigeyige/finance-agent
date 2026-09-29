## Context

DEV-QA-003 要求证明字段裁剪不仅在单元测试中成立，还不会通过持久化、日志、trace、workspace、checkpoint、SSE 或导出旁路泄漏。当前仓库已经有 `SensitiveFieldScanner`、`TrustedFieldProjector`、`ContextEvidence` 和 `ThinkingContentFilter`，因此采用契约测试和可审计扫描资产补齐证据，不复制一套运行时过滤器。

## Decisions

1. fixture 只包含合成身份、字段名、marker 和预期计数，不保存任何真实 payload；marker 用于跨文件/响应目录命中扫描。
2. PowerShell 脚本无 `-Execute` 时只校验 fixture、脚本声明和目标列表，不调用应用或数据库；显式 `-Execute` 时递归扫描用户指定的证据目录，并输出计数报告。
3. PostgreSQL SQL 仅使用 `SELECT`，对 JSONB/text 字段做聚合计数，禁止返回 payload、凭据或日志正文；数据库执行由用户在本地只读连接中完成。
4. 源写入门禁通过 `ConfigSourceAdapter` API 契约测试和可选写探针结果记录实现；不新增或调用写入接口。

## Boundary Rules

- 允许读取但禁止留存的字段只能存在 `TrustedProjection.readPayload`，不得出现在 retained/model projection。
- 禁止读取字段必须在 `SensitiveFieldScanner` 入口被拒绝。
- 允许读取但未批准送模字段不得进入 `ContextEvidence` 或模型请求。
- 任何命中 marker、禁止字段或源写入成功的扫描目标都使门禁失败。
