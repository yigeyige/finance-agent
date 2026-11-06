## Why

业务处理第一步要求用户手工拆分业务标题与说明，并自行判断是否需要业务场景和数据库实例。用户通常手里只有一段业务描述，录入成本高且容易漏项。用模型预填可以在不创建任务的前提下完成结构化拆解。

## What Changes

- 新增独立预填接口 `POST /api/v1/business-intake/draft`：不创建任务，要求 TASK_READ，入参 10–2000 字，超时 60 秒。
- 新增模型能力 `extractBusinessIntake` 与提示词模板 `EXTRACT_BUSINESS_INTAKE`（Flyway `V57`）；DeepSeek / Stub / DatabaseConfigured / Capturing 四个实现全部落地，token 用量按当前用户记录。
- 后端按启用中的业务场景与数据库实例做名称精确匹配，未命中或重名留空；标题/说明按表单上限截断。
- 业务处理页“开始模型分析”左侧新增「AI 预填」弹窗：输入文本 → 只读预览 → 应用到表单。
- 同步 OpenAPI 契约与前端类型/解析。

## Capabilities

### New Capabilities

- `business-intake-prefill`: 自由文本转业务处理表单字段的预填能力与前端交互。

### Modified Capabilities

（无）

## Impact

后端：新增模块 `businessintake`（service/controller/DTO）、`harness/application`（上下文/建议/草稿记录）、`harness/infrastructure`（DeepSeek/Stub）、`DatabaseConfiguredBusinessModelClient`、`CapturingBusinessModelClient`、`iam/web/PermissionGuardInterceptor`、`V57` 迁移。前端：`WorkbenchHome.vue`、新增 `services/business-intake-api.ts`、`api/generated` 类型。契约：`contracts/openapi/finance-agent-v1.json` 与 json-schema。不影响任务创建、模型分析、决策包流程。
