## Why

登录后直接落在业务处理页，用户首次进入时看不到 Agent 的能力范围和标准使用路径；同时根路径 `/` 被业务处理占用，无法挂载独立首页。新增首页并把业务处理迁移到独立路由，可以让“介绍与引导”和“实际处理”各归其位。

## What Changes

- 新增 `/home` 首页：品牌 hero、业务处理快捷卡、三步使用指南，不读取业务数据。
- 路由调整：`/` 重定向到 `/home`；业务处理迁移到 `/handling`；两个路由都要求已登录身份。
- 左侧菜单新增“首页”（所有已登录身份可见），业务处理入口指向 `/handling`；首页隐藏顶栏产品大标题，保留右上角用户菜单。
- 登录成功后默认进入 `/home`，`redirect` 查询参数仍优先。
- 任务列表、业务场景、证据、决策包草稿/已发布、任务诊断等入口与“返回任务”统一改为 `/handling`。

## Capabilities

### New Capabilities

- `workbench-home`: 首页介绍 Agent 定位与三步使用流程，并提供业务处理快捷入口。

### Modified Capabilities

- `frontend-identity-navigation`: 登录默认落点改为首页，左侧菜单新增“首页”。
- `frontend-workflow-navigation`: 业务处理使用独立路由 `/handling`，任务子页确定性返回到该路由。

## Impact

影响前端 `App.vue`、`router/index.ts`、`Login.vue`、新增 `HomePage.vue`、`TaskList.vue`、`BusinessSkillCatalog.vue`、`PublishedPacket.vue`、`EvidenceOverview.vue`、`TaskDiagnostics.vue`。无后端接口、数据库迁移或新增依赖；token 用量卡片与后端统计留待后续 change。
