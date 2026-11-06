## Why

模型调用已经返回 token 用量，但只随响应丢弃，数据库没有任何历史记录，也没有按用户统计的接口。管理员和业务用户都无法回答“这个月模型用了多少 token”，首页也无法展示与模型成本相关的运行信息。

## What Changes

- 补齐选表与查询规划两次模型调用的元数据返回，使一次业务处理中的所有真实调用都可被计量。
- 在模型客户端统一装饰器中记录 LIVE 调用用量（用户、任务、模型名、mode、tokens、发生时间），写入失败不影响主流程；STUB 调用不记录。
- 新增 `usage.model_token_usage` 表与 Flyway 迁移。
- 新增 `GET /api/v1/usage/tokens`：固定最近 30 个自然日（Asia/Shanghai，含今天），返回总量与按天序列；普通用户只读本人，平台管理员读全平台。
- 首页改为左窄右宽两栏：左侧三步指南（标题右侧放“进入业务处理”按钮），右侧 token 用量卡（总量 + 30 天折线），引入 ECharts 5.6 按需模块。
- 同步 OpenAPI 契约与前端 API 类型/解析。

## Capabilities

### New Capabilities

- `model-token-usage`: 真实模型调用的 token 用量记录、按用户聚合的 30 天查询，以及首页用量展示。

### Modified Capabilities

- `workbench-home`: 首页新增 token 用量卡，版式从单列改为左三步指南 + 右用量卡。

## Impact

后端：`harness/application`（`BusinessModelClient`、新增 `BusinessModelCall`）、`harness/infrastructure`（DeepSeek / Stub 客户端）、`diagnostics/CapturingBusinessModelClient`、`emsdbconfig/EmsDatabaseAssistanceService`、`iam/web/PermissionGuardInterceptor`（新增 `/api/v1/usage` 身份校验前缀）、新增 `usage` 模块与 `V56` 迁移、`BusinessModelConfiguration` 装配。前端：`HomePage.vue`、新增 `services/usage-api.ts`、`api/generated` 类型、`package.json` 新增 `echarts@5.6.x`。契约：`contracts/openapi/finance-agent-v1.json`。不改变模型提示词、任务流程、预算限制或权限模型。
