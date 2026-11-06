## Context

`BusinessModelClient` 是唯一模型端口，`BusinessModelConfiguration` 在外面包了一层 `CapturingBusinessModelClient` 用于异常快照。终端实现 `DeepSeekBusinessModelClient` 在 `invokeInternal` 中解析 `ChatResponse.getUsage().getTotalTokens()` 并生成 `BusinessModelCallMetadata`；但 `selectRelevantTables` 与 `planEmsDatabaseQuery` 自行调用适配器并只返回业务值，元数据被丢弃。`EmsDatabaseAssistanceService` 是这两个方法的唯一调用方。当前无 token 持久化，数据库迁移停在 `V55`。

## Goals / Non-Goals

**Goals:** 让每次 LIVE 调用都可归属到具体用户并落库；提供固定 30 天聚合查询；首页展示个人或全平台 token 总量与趋势；记录链路对模型调用零影响。

**Non-Goals:** 不做成本金额换算、不做按模型/任务维度报表、不做导出与告警；不改变提示词、任务流程、预算上限与权限模型；不追溯历史调用（上线前无数据）。

## Decisions

- **计量收口**：新增 `BusinessModelCall<T>(T value, BusinessModelCallMetadata metadata)`，把 `selectRelevantTables` / `planEmsDatabaseQuery` 改为返回该类型；实现内部复用统一的调用方法生成元数据，失败时返回空值 + 0 tokens 的元数据，保持“失败不抛异常”的既有语义。接口默认方法与 Stub 未发起真实调用时，统一使用 `BusinessModelCall.notCalled()` 提供的合法哨兵元数据（`BusinessModelCallMetadata` 要求 64 位 hex 摘要，不能随手构造空串）。
- **零用量不落库**：`tokens <= 0` 的记录直接跳过，避免失败调用、空响应和模拟调用产生噪声行；调用异常时无法取得 usage，同样不记录，统计口径为“成功返回 usage 的真实调用”。
- **记录位置**：在 `CapturingBusinessModelClient` 中统一记录（已有 `taskId` 与任务仓库），归属取 `IdentityContext.current().userId()`，为空时回落 `task.initiatorUserId()`；只记录 `mode = LIVE`。
- **记录容错**：记录使用独立写入并在自己的 try/catch 内吞掉异常，只写 WARN 日志，绝不改变模型返回值或抛出新异常。模型调用当前不在业务事务内（调用链无 `@Transactional`），因此同步写入不会随业务回滚丢失。
- **权限边界**：`PermissionGuardInterceptor` 只对白名单前缀校验身份，必须在白名单中加入 `/api/v1/usage`；否则控制器内的 `IdentityContext.require()` 会抛 `IdentityRequiredException`，落到全局兜底异常处理并返回 500。加入后未登录请求返回 401，普通用户与管理员均可读取（角色差异在查询层体现，不在路由层拦截）。
- **存储**：新建 schema `usage` 与表 `usage.model_token_usage`（`id`、`user_id`、`task_id`、`model_name`、`mode`、`tokens`、`occurred_at`），索引 `(user_id, occurred_at)` 与 `(occurred_at)`；不使用外键，避免用户或任务删除影响历史统计；`mode` 加 `CHECK (mode IN ('LIVE','STUB'))` 便于后续扩展。
- **保留期**：`RetentionCatalog` 是显式白名单，新表不加入清理清单，用量按永久保留，卡片标注“自上线起统计”。
- **聚合口径**：在 SQL 中按 `Asia/Shanghai` 的日期分组求和，服务层把结果补齐为固定 30 天序列（含今天），缺失日期为 0；普通用户按 `user_id` 过滤，`PLATFORM_ADMIN` 不过滤。
- **接口形态**：`GET /api/v1/usage/tokens` 无参数，返回 `{ scope: "SELF" | "ALL", totalTokens, points: [{ date, tokens }] }`；身份由 `IdentityContext.require()` 获取，未登录直接拒绝。
- **首页版式**：左窄右宽两栏（`1fr : 1.75fr`），左侧三步指南纵向排列并在标题右侧承载“进入业务处理”按钮，右侧用量卡利用更宽空间展示 30 天折线；移除独立业务处理快捷卡，≤1100px 回落单列。
- **前端图表**：`echarts@5.6.x` 只引入 `echarts/core` + `LineChart` + `GridComponent` + `TooltipComponent` + `CanvasRenderer`，在首页组件内动态 `import()`，构建产物实测拆成独立 chunk，不进入首屏主包；数据进入页面时请求一次，5 分钟内切回不重复请求。缓存放在模块级，键为当前用户标识（scope 由服务端按角色返回），避免同一浏览器切换账号后显示他人用量。
- **图表生命周期**：实例在 `mounted` 且数据就绪后初始化，监听 `window resize` 调用 `resize()`，`beforeDestroy` 中移除监听并 `dispose()`，避免布局错位与实例泄漏；总量展示千分位，tooltip 显示“日期 + tokens”。
- **前端降级**：接口失败或超时显示“用量暂不可用”，不阻塞 hero、快捷卡和三步指南；无数据展示总量 0 与全 0 折线；加载中显示轻量占位，避免卡片高度跳动。

## Risks / Trade-offs

- 改造两个端口方法签名会触达 1 个装饰器、2 个实现与 1 个调用方，业务处理主流程需回归初稿、方案、证据与决策包链路。
- 每次模型调用多一次数据库写入：写入在模型调用完成后同步执行，正常情况为毫秒级；失败只记日志。
- 30 天折线依赖新增前端依赖 `echarts`，`package-lock` 与镜像构建需要能安装该包。
- 统计从上线开始累积，历史调用无法回填，首屏趋势会出现长时间为 0 的区段，需要在卡片上说明“自上线起统计”。
- 时区固定 `Asia/Shanghai`：如果部署在其他时区，日期边界仍按北京时间切分，与业务侧一致。
- 调用失败或空响应不记录（拿不到 usage），统计结果会略低于真实消耗；这是为保证记录链路零影响而接受的偏差。
- 仓库没有 Testcontainers/H2，`application-test.yml` 指向本地 `finance_agent_test` 实库。验证策略：30 天补 0、时区边界、scope 选择、写入失败容错用单元测试；Mapper 聚合 SQL 与迁移执行依赖本地 test 库，环境不可用时明确说明未验证，不声称已通过。
- OpenAPI 契约与前端 `api/generated` 为手工维护，无自动校验，需在实施后人工比对字段与错误码。
