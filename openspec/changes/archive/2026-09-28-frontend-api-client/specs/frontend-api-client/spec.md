## ADDED Requirements

### Requirement: Consistent REST request metadata
REST 客户端 SHALL 为请求生成请求标识，并支持写请求携带 `Idempotency-Key` 与 `If-Match` 行版本条件。

#### Scenario: Write request carries concurrency metadata
- **WHEN** 页面通过服务层提交幂等写请求
- **THEN** 请求包含请求标识、幂等键和行版本条件头

### Requirement: Stable Chinese error model
客户端 SHALL 将网络错误、401/403、409、412 和服务端错误映射为可识别的错误对象及明确中文提示。

#### Scenario: Version conflict
- **WHEN** 服务端返回 409 或 412
- **THEN** 客户端分别提示数据冲突或版本已过期，并保留 traceId
