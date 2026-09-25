## Why

当前工程只有应用名称配置，数据库、模型、工作目录与执行预算尚未形成可绑定的环境配置契约，导致本地、测试和生产环境之间容易混用不安全默认值。现在建立 profile 与生产启动前校验，可以在后续接入真实数据源和模型前先固定部署边界，并让错误在启动阶段以不泄露秘密的方式暴露。

## What Changes

- 新增 `local`、`test`、`prod` 三套 Spring profile 配置。
- 新增类型化的 `finance-agent` 配置绑定，覆盖数据库、DeepSeek、工作目录、预算、身份、存储和字段策略。
- 统一通过环境变量注入端口和运行时配置，提供不含真实凭据的 `.env.example` 与配置说明。
- 新增生产启动校验：拒绝 Stub 模型、测试身份、本地/内存存储、宽松字段策略以及缺失的生产必需项。
- 新增配置绑定与生产失败契约测试；校验错误只包含配置项名称，不输出 API Key、数据库密码或完整连接串。

## Capabilities

### New Capabilities

- `configuration-profiles`: 定义 profile、环境变量和类型化配置绑定契约。
- `production-startup-validation`: 定义生产 profile 启动前安全校验契约。

### Modified Capabilities

无。

## Impact

- 影响 `backend` Spring Boot 启动配置、配置绑定类和启动校验器。
- 新增配置单元测试与 profile 配置文档，不引入新的运行时依赖。
- 本任务不读取或提交真实 `.env` 值，不启动服务，也不改变数据库结构。
