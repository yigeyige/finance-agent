## 1. 后端模型能力

- [x] 1.1 新增 `BusinessIntakeOption` / `BusinessIntakeContext` / `BusinessIntakeSuggestion` / `BusinessIntakeDraft` 记录，并在 `BusinessModelClient` 增加抽象方法。
- [x] 1.2 DeepSeek 实现独立 60s 超时与 JSON 解析；Stub 返回确定性结果；DatabaseConfigured 转发；Capturing 装饰器记录 token（taskId 为空）。

## 2. 预填接口

- [x] 2.1 新增 `V57` 提示词模板 `EXTRACT_BUSINESS_INTAKE`。
- [x] 2.2 新增 `businessintake` 模块的 `BusinessIntakeDraftService`：校验 10–2000 字、加载候选、名称精确匹配、截断输出。
- [x] 2.3 新增 `POST /api/v1/business-intake/draft`，在权限拦截器登记 `/api/v1/business-intake`，映射 400 / 502 / 504。

## 3. 契约与前端

- [x] 3.1 OpenAPI 升到 1.6.0 并新增路径、schema、样例；前端 generated 增加类型与解析，新增 `services/business-intake-api.ts`。
- [x] 3.2 业务处理页新增「AI 预填」按钮、弹窗、只读预览与“应用到表单”。

## 4. 验证

- [x] 4.1 后端单测：名称匹配（唯一/重名/未命中）、部分字段、长度校验、截断、Stub 分支、装饰器记录 token。
- [x] 4.2 后端 `compile` 与定向测试；契约 `verify-contracts.py`。
- [x] 4.3 前端 `typecheck`、`npm test`、`npm run build`。

## 验证记录

- 2026-11-06：后端定向测试 `BusinessIntakeDraftServiceTest`、`BusinessIntakeStubTest`、`CapturingBusinessModelClientUsageTest`、`CapturingBusinessModelClientTest`、`FlywayMigrationContractTest` 共 14 项通过，`mvn -o -DskipTests compile` 通过。
- 2026-11-06：`python scripts/verify-contracts.py` 通过（31 个 schema、34 个样例），OpenAPI 升到 1.6.0。
- 2026-11-06：前端 `tsc --noEmit`、`npm test`（47 项）、`npm run build` 通过；工作台测试夹具补充了新服务 mock。
- 未验证：V57 尚未在运行库执行；未启动前后端联调，弹窗与真实模型返回需实机验收。
