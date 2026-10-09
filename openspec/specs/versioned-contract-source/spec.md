# versioned-contract-source Specification

## Purpose
TBD - created by archiving change contract-directory-generation-boundary. Update Purpose after archive.
## Requirements
### Requirement: Contract sources are versioned and closed by default
The system SHALL store an OpenAPI 3.1 document under `contracts/openapi` and independent Draft 2020-12 JSON Schemas under `contracts/json-schema`, and every schema SHALL include a semantic-versioned `$id` and explicitly set `additionalProperties` for object shapes.

#### Scenario: Contract inventory is discoverable
- **WHEN** a developer inspects the contracts directory
- **THEN** OpenAPI, JSON Schema, examples, and generation-boundary documentation are present in their designated directories

#### Scenario: Schema identity is stable
- **WHEN** a JSON Schema is loaded
- **THEN** it declares Draft 2020-12, a `schema://` `$id` ending in a semantic version, and explicit object closure behavior

### Requirement: One source feeds Java and TypeScript boundaries
The system SHALL document `contracts/` as the only source of API/domain contract truth, with Java generation or validation on the backend side and TypeScript output reserved for `frontend/src/api/generated`; handwritten duplicate DTOs SHALL NOT be treated as source contracts.

#### Scenario: Generated output destinations are explicit
- **WHEN** a later type-generation task is implemented
- **THEN** it can locate the backend and frontend output boundaries without inventing a second source directory

### Requirement: Business processing extension contracts are discoverable
版本化 OpenAPI 和 JSON Schema SHALL 覆盖业务场景 Skill、大模型连接配置、任务列表与只读详情、推荐方案修订、模型协作，以及任务草稿关联业务场景 Skill 的字段；请求和响应对象 MUST 显式声明字段边界。

#### Scenario: A client discovers new business processing APIs
- **WHEN** 开发者检查 `contracts/openapi/finance-agent-v1.json`
- **THEN** 能找到上述已实现入口、相应的请求或响应模型，以及认证或权限失败响应

#### Scenario: Model connection response is inspected
- **WHEN** 客户端读取大模型连接配置响应模型
- **THEN** 模型名称、推理强度、启用状态和密钥已配置状态可用，且不包含 API Key 明文或密文
