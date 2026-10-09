## Why

业务场景 Skill、大模型连接、任务只读查询、方案修订和模型协作已经投入前后端使用，但 `contracts/` 尚未完整描述这些入口和新增字段。页面服务层因此维护了独立 DTO，违背了契约目录作为唯一事实来源的约束，并增加后续接口演进漂移风险。

## What Changes

- 在版本化 OpenAPI 与 JSON Schema 中补充业务场景 Skill、大模型连接、任务列表与详情、推荐方案修订、模型协作接口，以及任务草稿的 `businessSceneSkillId`。
- 为新增的可请求对象补充正向和负向合成样例，并扩展本地契约校验覆盖范围。
- 扩展 `frontend/src/api/generated` 的临时生成边界，并让相关前端服务复用该边界中的请求、响应和枚举类型。

## Capabilities

### Modified Capabilities

- `versioned-contract-source`: 版本化契约必须覆盖本轮业务处理新增的 REST 路径、字段和模型。
- `contract-validation-gate`: 契约校验必须验证新增请求模型的正反样例。
- `frontend-api-contract`: 临时生成边界必须承接新增的业务处理领域类型，服务层不得再重复定义这些 API DTO。

## Impact

- `contracts/openapi/finance-agent-v1.json` 与 `contracts/json-schema/`、`contracts/examples/`
- `scripts/verify-contracts.py`
- `frontend/src/api/generated/index.ts`，以及相关 `frontend/src/services/*.ts`
- 不修改现有接口行为、数据库结构、权限规则或模型调用逻辑。
