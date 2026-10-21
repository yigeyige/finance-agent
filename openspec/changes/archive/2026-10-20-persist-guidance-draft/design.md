# Design

## Context

- `BusinessModelInteractionService.guideBusinessHandling` 返回 `BusinessModelResult<BusinessGuidanceSuggestion>`，其中 `draft()` 是 `BusinessHandlingDraft`（16 字段 + conclusion + evidence），当前不落库。
- `TaskDetailService.find` 只组装 premise/scope/step，不含草稿结论与证据。

## Goals / Non-Goals

**Goals:**
- 模型分析成功后自动持久化草稿全文，历史版本留痕。
- 任务详情展示最新草稿的检查结论与关联证据。

**Non-Goals:**
- 不暴露模型推理过程、原始提示词、连接凭据。
- 不改变现有 scope/premise/step 的权威写入流程。

## Decisions

### D1: 表结构
新增 `task.guidance_draft` 表：`guidance_draft_id UUID PK`、`task_id UUID FK → task.investigation_task`、`draft_json JSONB`（存 `BusinessHandlingDraft.toJson()` 输出）、`created_at TIMESTAMPTZ`、`created_by UUID`。索引 `(task_id, created_at DESC)`。

### D2: 保存时机
`BusinessModelInteractionService.guideBusinessHandling` 在模型返回成功后、返回结果前，调用 `guidanceDraftRepository.save(taskId, draft.toJson())`。修订场景同样追加新版本。

### D3: 详情返回
`TaskDetailService.find` 读取该任务最新一条草稿，返回 `conclusion`（文本）与 `evidence`（结构化数组）。无草稿时返回空。

### D4: 前端展示
`TaskDetail.vue` 在“对象与处理范围”卡片下新增“检查结论”与“关联证据表”两个只读区块，复用 `evidenceRowObjects` 思路渲染二维证据。

## Risks / Trade-offs

- [草稿 JSON 体积] → 单条草稿含证据行，限制在数 KB 内；JSONB 存储可接受。
- [历史版本膨胀] → 仅按任务追加，本次不做过期清理（后续可加保留策略）。

## Migration Plan

- 新增 Flyway 迁移 `V18__guidance_draft.sql`。无数据迁移（新表）。

## Open Questions

- 无。
