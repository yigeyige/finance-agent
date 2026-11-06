# frontend-workflow-navigation Specification

## Purpose
规定任务工作台与各子页面之间的上下文保留、确定性返回和路由参数刷新行为，确保用户在同一会话中切换任务页面时不会丢失已建立的任务进度，也不会继续展示上一任务的数据。

## Requirements

### Requirement: Workbench context survives in-app task navigation
前端 SHALL 在同一 SPA 会话中从工作台进入任务子页后保留当前任务、范围、计划和审批视图状态。

#### Scenario: Return from diagnostics
- **WHEN** 操作员从有当前任务的工作台进入诊断页并返回
- **THEN** 工作台仍展示同一任务及其已完成的范围和审批状态

### Requirement: Task child pages use deterministic return routes

任务子页 SHALL 使用显式目标路由返回业务处理页 `/handling` 或同任务的上一个流程页面，不得依赖未知的浏览器历史来决定业务返回目标。业务处理页 MUST 使用独立路由 `/handling`，根路径 `/` MUST 重定向到首页 `/home`；子页“返回任务”MUST NOT 使用根路径作为返回目标。

#### Scenario: Packet draft returns to evidence

- **WHEN** 操作员在任务的决策包草稿页点击返回证据
- **THEN** 前端导航到同一 `taskId` 的证据与覆盖度页面

#### Scenario: Child page returns to business handling

- **WHEN** 操作员在证据与覆盖度、决策包草稿、已发布决策包或任务诊断页点击“返回任务”
- **THEN** 前端导航到 `/handling`，不触发草稿重置，业务处理页仍展示当前任务的处理进度

### Requirement: Route parameter changes reload task data
任务详情页 SHALL 在路由 `taskId` 发生变化时丢弃上一任务的页面数据并重新查询。

#### Scenario: Evidence route changes task
- **WHEN** 用户在证据页从任务 A 的 URL 导航到任务 B 的 URL
- **THEN** 页面展示任务 B 的证据，不保留任务 A 的空状态或数据
