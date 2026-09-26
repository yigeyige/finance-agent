## Why

P0 需要明确大原件和导出文件的对象存储边界，但正式对象存储协议、凭证和容量尚未确定。若现在让业务代码直接写本地绝对路径，后续会形成跨环境不可迁移、不可审计且可能绕过生产安全守卫的实现。

## What Changes

- 定义领域无关的 `ObjectStorageGateway`、逻辑对象引用、SHA-256 内容摘要、大小和媒体类型契约。
- 提供 local/test 专用的受控临时目录实现，强制相对逻辑键、路径隔离、大小上限和流式摘要。
- 提供明确的未配置实现；prod 未注册正式适配器时拒绝调用，不静默回退 local/memory。
- 将正式决策包 JSON/HTML 的权威保存边界记录为 PostgreSQL，本任务不迁移或实现正式对象存储。
- 更新配置、启动守卫、运行文档、方案基线和负向测试，覆盖路径穿越、超限和生产回退失败。

## Capabilities

### New Capabilities

- `deferred-object-storage-boundary`: 对象引用、受控临时存储和正式适配器缺失时的安全失败契约。

### Modified Capabilities

<!-- 本变更不修改现有规格的行为要求。 -->

## Impact

- `backend/src/main/java` 增加 storage 端口、领域引用、local/test 适配器和 disabled 适配器。
- `FinanceAgentProperties`、生产启动校验和三个 profile 配置增加适配器类型、临时目录和大小上限。
- `backend/src/test/java` 增加契约、路径、大小和生产守卫测试。
- `docs/configuration-profiles.md`、`docs/build-and-run.md`、`solutions/` 方案和开发任务记录同步边界；不新增依赖，不改变 PostgreSQL 决策包权威存储。
