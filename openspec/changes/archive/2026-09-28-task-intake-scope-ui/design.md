## Context

后端已提供 `/api/v1/tasks`、`/{taskId}/premises` 和 `/{taskId}/scope-confirmations`，响应包含任务状态、row version 和 scope revision。前端已有 Axios、身份上下文和严格类型边界，但尚未提供领域表单。

## Goals / Non-Goals

**Goals:**

- 用 Element UI 表单完成任务、前提和范围三步操作。
- 将 JSON 字段权限和路径作为可验证输入，提交前阻止空范围和非法 JSON。
- 展示当前任务状态、范围修订号、row version 和并发冲突提示。

**Non-Goals:**

- 不实现任务分页查询、运行控制或决策包页面。
- 不在前端复制后端状态机或权限判断。

## Decisions

- 使用现有 `postJson` 服务层并为每次写入生成幂等键；row version 通过 `If-Match` 传递。
- 表单只保留当前任务详情和本地最近创建列表，后续任务再接入分页查询。
- 三类字段权限和排除范围均以 JSON 文本编辑，使用 `JSON.parse` 做输入校验，不手写后端业务规则。

## Risks / Trade-offs

- [后端暂无任务列表接口] → 当前页面展示本次会话创建的任务详情，明确不伪造全量列表。
- [旧版本并发] → 将 409/412 转换为中文 stale 提示并要求重新读取。

## Migration Plan

新增 API 服务和工作台视图即可，无数据库迁移；用户启动前后端后手动验证创建、补充前提和多次范围确认。

## Open Questions

- 后续任务列表分页和筛选字段由任务查询 API 任务确定。
