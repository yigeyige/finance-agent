## ADDED Requirements

### Requirement: Business processing types use the generated boundary
前端 SHALL 在 `frontend/src/api/generated` 集中定义业务场景 Skill、大模型连接、任务只读查询、方案修订和模型协作的 API 类型；相关服务层 MUST 复用这些类型，而不得声明等价的第二份 API DTO。

#### Scenario: A service invokes a business processing API
- **WHEN** 前端服务调用业务场景 Skill、大模型连接、任务详情或模型协作接口
- **THEN** 其请求和响应类型来自临时生成边界，页面只保留交互专属状态
