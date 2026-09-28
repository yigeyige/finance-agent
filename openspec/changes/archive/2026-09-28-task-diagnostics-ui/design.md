# Design

## Backend

- TaskDiagnosticService 在只读事务中读取任务、当前步骤、当前租约、当前步骤审批和最新工具调用。
- API 只返回枚举化状态、步骤摘要、租约时间、审批是否有效、脱敏错误码和动作标签；不返回原始异常、SQL、凭证、禁止字段或模型响应。
- UNKNOWN 工具调用映射为 MANUAL_RECONCILIATION_REQUIRED，暂停状态映射为稳定暂停原因和恢复提示。
- GET /api/v1/tasks/{taskId}/diagnostics 只读开放给 OPERATOR/VIEWER，最终权限仍由服务端拦截器决定。

## Frontend

- 新增诊断 API 类型与解析、/tasks/:taskId/diagnostics 页面。
- 页面用卡片和表格区分任务状态、步骤、租约、审批、暂停/错误和建议动作，并提供返回工作台入口。
- 页面不提供任何改变任务状态的按钮。
