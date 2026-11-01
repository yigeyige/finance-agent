## Why

业务页面会直接展示 `guidanceDraft.processPlan`、`subjectKey`、`recommendedGoal` 等字段名，业务用户难以理解。字段说明应集中在前端展示层维护，不应让模型在 JSON 字段名中拼接中文注释。

## What Changes

- 新增前端字段字典，维护字段路径的中文名称和说明。
- 业务处理向导：流程事实、调查结论、决策包字段补充中文注释。
- 任务详情：决策包卡片字段补充中文注释。
- 未命中字典时回退为原字段名。
- 不改提示词、不改后端契约。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-handling-guidance`: 前端展示模型字段时提供统一中文说明。

## Impact

- 前端：新增 `frontend/src/constants/field-labels.ts`；修改 `WorkbenchHome.vue`、`TaskDetail.vue`。
- 验证：`npm --prefix frontend run typecheck`、`build`。
