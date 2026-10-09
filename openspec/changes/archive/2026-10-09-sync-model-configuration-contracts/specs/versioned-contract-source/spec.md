## ADDED Requirements

### Requirement: Business processing extension contracts are discoverable
版本化 OpenAPI 和 JSON Schema SHALL 覆盖业务场景 Skill、大模型连接配置、任务列表与只读详情、推荐方案修订、模型协作，以及任务草稿关联业务场景 Skill 的字段；请求和响应对象 MUST 显式声明字段边界。

#### Scenario: A client discovers new business processing APIs
- **WHEN** 开发者检查 `contracts/openapi/finance-agent-v1.json`
- **THEN** 能找到上述已实现入口、相应的请求或响应模型，以及认证或权限失败响应

#### Scenario: Model connection response is inspected
- **WHEN** 客户端读取大模型连接配置响应模型
- **THEN** 模型名称、推理强度、启用状态和密钥已配置状态可用，且不包含 API Key 明文或密文
