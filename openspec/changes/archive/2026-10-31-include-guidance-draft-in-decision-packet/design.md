## Context

`BusinessModelInteractionService` 每轮成功后将模型返回的 `BusinessHandlingDraft` 追加保存到 `task.guidance_draft`，其中已有 `conclusion` 和受限 `evidence`；同轮数据库查询证据另行累计。`BusinessDecisionPacketService.generate` 目前仅读任务原始信息、累计数据库证据和场景提示词，未注入 `GuidanceDraftRepository`。前端生成按钮只提交任务 ID，最新草稿由服务端读取即可。

## Goals / Non-Goals

**Goals:** 决策包模型使用最新已保存调查草稿及已有固定输入；保存本次草稿快照，使新旧决策包修订可区分且旧修订不随调查变化。

**Non-Goals:** 不扩充草稿结构，不改变调查模型或证据累积方式，不重新解析用户原附件，不新增表/迁移/页面，不改决策包五字段模型输出。

## Decisions

- 在 `BusinessDecisionPacketService.generate` 中用现有 `GuidanceDraftRepository.findLatestDraftJsonByTaskId` 读取最新草稿，并用现有 `BusinessHandlingDraft.fromJson` 校验。无草稿或草稿不可解析时中文拒绝，不调用模型。避免由前端提交易变化的草稿副本。
- `BusinessPacketAnalysisContext` 增加受限草稿对象；LIVE 与 Stub 模型输入均追加 `guidanceDraft`，保留 `title`、`question`、`decisionPacketPrompt` 和按现有规则去重的累计 `evidence`。草稿内的 `evidence` 表示最新调查结论所引用的证据，不再重复追加到顶层累计证据列表；保持顶层 50 条限制。
- 决策包 `businessContent` 新增 `guidanceDraft` 快照，沿用现有 canonical JSON 摘要与修订逻辑。草稿内容变化可产生新修订，历史包无需回填；前端既有五字段和证据展示保持不变。后续文件生成从指定包内容读取这份快照。
- 本任务只验证交接和固化，不将模型回复直接复制为正式包；仍由决策包模型分析产生五字段结果。

## Risks / Trade-offs

- **历史包没有草稿快照**：继续保持可读，后续文件生成需要用户重新调查并生成新包。
- **旧草稿 JSON 损坏**：明确拒绝并提示重新调查，不能退回仅用原始任务信息生成，以免误把旧行为当修复。
- **草稿和累计证据包含相似条目**：输入中分开表达草稿证据选择与跨轮数据库证据，顶层去重规则不变，不复制扩增顶层证据。

## Migration Plan

无数据库迁移。仅对新生成的包增加 JSON 字段；旧包保持原样。通过服务与模型输入契约测试、后端编译和 OpenSpec 严格校验；本地环境启动和手工验证由用户执行。

## Open Questions

无。流程结构化调查结论属于后续 `generate-process-artifacts` 切片，本任务仅传递当前 `BusinessHandlingDraft` 全文。
