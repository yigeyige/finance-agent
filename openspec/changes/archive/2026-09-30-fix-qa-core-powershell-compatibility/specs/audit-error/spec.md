## MODIFIED Requirements

### Requirement: Stable safe error model

系统 SHALL 使用稳定领域错误码和 `retryClass` 表达可重试、新审批、人工对账或不可重试；HTTP 响应不得包含 SQL、路径、凭证、Token 或异常堆栈。未知 API 异常 SHALL 使用请求上下文 traceId 关联安全响应和最小结构化遥测。

#### Scenario: Sensitive exception text is not exposed

- **WHEN** 底层异常包含 SQL、文件路径或凭证片段
- **THEN** HTTP 响应只返回安全目录消息

#### Scenario: Unknown API exception is safely traceable

- **WHEN** API 请求发生未映射异常
- **THEN** 响应返回当前请求 traceId，控制台仅输出该关联上下文及异常类别、SQLSTATE 分类等受限字段，不输出异常正文、堆栈、SQL 或请求内容

#### Scenario: Nested conversion failure is safely classified

- **WHEN** HTTP 消息转换异常包装更深层的转换原因
- **THEN** 控制台输出外层异常类型和固定根因分类，且不输出异常正文、请求内容或堆栈

### Requirement: Persisted audit chain is reproducible

系统 SHALL 在计算审计事件摘要前将事件时间规范化为 PostgreSQL `timestamptz` 可持久化的微秒精度，以保证持久化重读后的事件仍可通过摘要链完整性校验。

#### Scenario: Nanosecond event timestamp is reloaded from PostgreSQL

- **WHEN** 事件创建时间包含纳秒且随后按 PostgreSQL 微秒精度重读
- **THEN** 重读事件保持相同摘要并通过完整性校验
