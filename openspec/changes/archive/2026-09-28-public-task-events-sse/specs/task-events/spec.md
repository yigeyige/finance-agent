## MODIFIED Requirements

### Requirement: Safe task event stream

系统 SHALL 为每个 task 生成单调事件序号，并提供按游标重放的事件查询；事件 SHALL 仅包含状态、审批需求、工具进度、公开理由和结果引用。

#### Scenario: Cursor replay is ordered

- **WHEN** 客户端按合法 task 和非负游标查询事件
- **THEN** 返回严格递增序号且只包含该游标之后的事件

#### Scenario: Sensitive content is filtered

- **WHEN** 事件尝试写入 prompt、reasoning、源原文、凭证或 Token
- **THEN** 事件服务拒绝或过滤敏感字段

### Requirement: Authorized REST and SSE access

系统 SHALL 校验 task/user 边界；VIEWER 只能读取发布后允许的公开事件，SSE 与 REST 使用相同过滤规则。

#### Scenario: Cross-task cursor is rejected

- **WHEN** 使用其他任务的游标或负 `Last-Event-ID`
- **THEN** 请求被拒绝
