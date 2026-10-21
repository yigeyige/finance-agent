# Proposal

## Why

“开始模型分析”生成的业务处理草稿（对象与处理范围、检查结论、关联证据）当前只保存在前端组件内存中，未持久化到数据库。管理员从任务列表重新进入任务详情时，看不到之前的对象范围、检查结论与关联证据。

## What Changes

- 新建 `task.guidance_draft` 表，每次模型分析成功后自动保存草稿全文（16 字段 + 检查结论 + 关联证据），历史多版本留痕。
- 任务详情接口返回最新草稿的检查结论与关联证据。
- 前端任务详情页新增“检查结论”与“关联证据表”两个只读展示区。

## Capabilities

### New Capabilities

- `guidance-draft-persistence`: 业务处理草稿的自动持久化与详情只读展示。

## Impact

- 后端：新增 `GuidanceDraftEntity`/`GuidanceDraftRepository`、Flyway 迁移、`BusinessModelInteractionService` 保存逻辑、`TaskDetailService` 返回扩展。
- 前端：`TaskDetail` 类型扩展、`TaskDetail.vue` 展示区。
- 不引入新依赖。
