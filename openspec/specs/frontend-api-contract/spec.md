# frontend-api-contract Specification

## Purpose
TBD - created by archiving change frontend-api-client. Update Purpose after archive.
## Requirements
### Requirement: Versioned frontend domain types
前端 SHALL 在生成边界集中声明任务响应、公开任务事件和 API 错误模型，并将未知枚举值视为契约错误。

#### Scenario: Known task status is accepted
- **WHEN** 服务端返回已声明的任务状态
- **THEN** 类型解析返回对应领域类型

#### Scenario: Unknown enum is rejected
- **WHEN** 服务端返回未声明的任务状态或事件类型
- **THEN** 解析失败并返回明确的中文契约错误

### Requirement: Extra response fields are rejected
领域响应校验 SHALL 检查对象字段白名单，发现未声明字段时不得把响应交给页面。

#### Scenario: Extra field appears
- **WHEN** 任务响应包含契约外字段
- **THEN** 客户端拒绝响应并提示接口契约需要同步

