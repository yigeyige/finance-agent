## 1. 模型端口计量

- [x] 1.1 新增 `BusinessModelCall`（含 `notCalled()` 哨兵元数据），把 `selectRelevantTables` / `planEmsDatabaseQuery` 改为返回带元数据的结果，并适配 DeepSeek、Stub 与 `EmsDatabaseAssistanceService`。

## 2. 用量落库

- [x] 2.1 新增 `V56__model_token_usage.sql`（schema `usage`、表、`mode` CHECK、两个索引），以及实体、Mapper、仓储。
- [x] 2.2 新增 `TokenUsageRecorder` 并在 `CapturingBusinessModelClient` 中记录 LIVE 调用：归属优先当前身份、回落任务发起人，`tokens <= 0` 跳过，写入失败只记 WARN。

## 3. 用量查询接口

- [x] 3.1 新增 30 天聚合查询服务与 `GET /api/v1/usage/tokens`：普通用户本人、管理员全平台，缺失日期补 0。
- [x] 3.2 在 `PermissionGuardInterceptor` 白名单加入 `/api/v1/usage`，保证未登录返回 401（避免落到兜底 500）。
- [x] 3.3 同步 `contracts/openapi/finance-agent-v1.json`，并在前端 `api/generated` 增加类型与解析、新增 `services/usage-api.ts`。

## 4. 首页用量卡

- [x] 4.1 引入 `echarts@5.6.x` 按需模块（LineChart + Grid + Tooltip + Canvas），组件内动态导入。
- [x] 4.2 首页改为左窄右宽两栏：三步指南纵向排列、标题右侧放“进入业务处理”按钮；用量卡展示总量千分位、30 天折线、统计口径说明、加载占位与失败降级。
- [x] 4.3 模块级缓存按用户标识区分并保留 5 分钟；图表随窗口 resize，`beforeDestroy` 中解除监听并销毁实例；窄屏回落单列。

## 5. 验证

- [x] 5.1 纯逻辑单元测试：30 天补 0、`Asia/Shanghai` 日期边界、SELF/ALL scope、`tokens <= 0` 跳过、写入失败不影响返回值。
- [x] 5.2 运行后端定向测试与编译；Mapper 聚合 SQL 与迁移执行仅在本地 `finance_agent_test` 可用时验证，不可用则明确记录未验证。
- [x] 5.3 前端：`npm run typecheck`、`npm run build`，并用本机 Chrome 渲染首页核对两栏布局、折线、加载/空数据/失败态与窄屏单列。

## 验证记录

- 2026-11-06：后端 `mvn -o -DskipTests compile` 通过；`TokenUsageQueryServiceTest`、`MybatisTokenUsageRecorderTest`、`CapturingBusinessModelClientUsageTest`、`CapturingBusinessModelClientTest`、`EmsDatabaseAssistanceServiceTest`、`FlywayMigrationContractTest` 共 20 项定向测试通过。
- 2026-11-06：契约 `python scripts/verify-contracts.py` 通过（29 个 schema、33 个样例），OpenAPI 升版到 1.5.0。
- 2026-11-06：前端 `npm run typecheck`、`npm test`（47 项全通过）、`npm run build` 通过；构建产物中 ECharts 拆为独立 chunk，主包体积未增加。用本机 Chrome 渲染 1440 宽两栏布局：步骤区 927px、用量卡 463px、等高 291px，折线与总量千分位正常。
- 未验证：V56 迁移未对真实 PostgreSQL 执行（本机无 psql 客户端，未连接仓库外部数据库）；未启动后端/前端联调，接口真实返回与登录后页面需人工验收。
- 2026-11-06（联调补充）：通过 `FlywayMigrationRunner` 在本地库执行 V56（`status=OK`，history=56），只读校验 `usage.model_token_usage` 已创建、聚合 SQL 可执行；新 jar 重启后端后 `/api/v1/usage/tokens` 未登录返回 401；Vite 5173 与 `/api` 代理正常。带登录态的 200 响应与首页卡片由用户实机验收。
- 2026-11-06（版式调整）：移除业务处理快捷卡，按钮移至三步卡右上角；步骤卡纵向收窄（约 505px）、用量卡加宽（约 885px），两卡等高约 507px；`npm run typecheck`、47 项前端测试、`npm run build` 通过。

