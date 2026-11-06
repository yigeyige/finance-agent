## Why

业务场景维护者需要手写场景名称、调查提示词、决策包提示词与流程文件提示词，且难以结合真实表结构。现有 AI 预填只服务业务处理第一步，场景表单仍靠人工撰写；把新建/编辑从弹窗改为内容区全页表单后，也需要一个与表单并排的生成入口。

## What Changes

- 场景页：新建/编辑改为右侧内容区全页表单（侧栏可见），返回按钮左侧新增「AI 预填」；未保存返回需确认。
- AI 预填弹窗：业务描述 + 数据库实例下拉 + 三个生成按钮（调查 / 决策包 / 流程文件），每个结果只读预览后单独应用；调查按钮同时回填场景名称。
- 新增独立接口 `POST /api/v1/business-scene-skills/prefill`：新建要求 `PLATFORM_USER`，编辑仅场景维护者；不改既有场景 CRUD、intake 与任务编排链路。
- 表名筛选：文本中出现的 schema 优先筛选，排除日志/临时/备份/日期后缀/MQ/事务类表，剩余表名全部传给模型；实例仅本次生成使用，不落库。
- 新增模型方法与提示词模板（Flyway `V58`），独立 60 秒超时；token 按当前用户记录。
- 同步 OpenAPI 契约、前端类型与 API 客户端。

## Capabilities

### New Capabilities

- `scene-skill-prefill`: 业务场景表单的 AI 预填能力与页面交互。

### Modified Capabilities

（无）

## Impact

后端：`harness/application`（新增 target/context/suggestion 记录与方法）、`harness/infrastructure`（DeepSeek/Stub）、`DatabaseConfiguredBusinessModelClient`、`CapturingBusinessModelClient`、`scenario/application`（表名筛选与预填服务）、`scenario/web`（新控制器/DTO）、`V58` 迁移。前端：`BusinessSkillCatalog.vue`（全页表单）、新增 `SceneSkillPrefillDialog.vue` 与 `services/scene-skill-prefill-api.ts`、`api/generated` 类型。契约：OpenAPI 与 json-schema。
