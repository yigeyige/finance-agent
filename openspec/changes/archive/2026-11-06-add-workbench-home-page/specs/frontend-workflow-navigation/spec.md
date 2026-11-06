## MODIFIED Requirements

### Requirement: Task child pages use deterministic return routes

任务子页 SHALL 使用显式目标路由返回业务处理页 `/handling` 或同任务的上一个流程页面，不得依赖未知的浏览器历史来决定业务返回目标。业务处理页 MUST 使用独立路由 `/handling`，根路径 `/` MUST 重定向到首页 `/home`；子页“返回任务”MUST NOT 使用根路径作为返回目标。

#### Scenario: Packet draft returns to evidence

- **WHEN** 操作员在任务的决策包草稿页点击返回证据
- **THEN** 前端导航到同一 `taskId` 的证据与覆盖度页面

#### Scenario: Child page returns to business handling

- **WHEN** 操作员在证据与覆盖度、决策包草稿、已发布决策包或任务诊断页点击“返回任务”
- **THEN** 前端导航到 `/handling`，不触发草稿重置，业务处理页仍展示当前任务的处理进度
