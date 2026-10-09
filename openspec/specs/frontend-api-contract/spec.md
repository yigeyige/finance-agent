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

### Requirement: Business processing types use the generated boundary
前端 SHALL 在 `frontend/src/api/generated` 集中定义业务场景 Skill、大模型连接、任务只读查询、方案修订和模型协作的 API 类型；相关服务层 MUST 复用这些类型，而不得声明等价的第二份 API DTO。

#### Scenario: A service invokes a business processing API
- **WHEN** 前端服务调用业务场景 Skill、大模型连接、任务详情或模型协作接口
- **THEN** 其请求和响应类型来自临时生成边界，页面只保留交互专属状态
