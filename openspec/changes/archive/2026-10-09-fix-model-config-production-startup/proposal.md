## Why

真实模型连接已由管理员维护在数据库中，但生产启动仍要求旧的 DeepSeek 连接环境变量，导致按现行模型配置方案部署时无法启动。需要让部署级配置只承担基础设施和密钥保护职责，模型连接状态由运行时数据库配置决定。

## What Changes

- 移除生产启动对旧 DeepSeek API Key、地址和模型名称的强制校验。
- 在生产环境保留部署级模型密钥加密主密钥校验，并禁止缺失时写入或解密 API Key。
- 允许无启用模型连接的应用启动；真实模型调用改为返回安全、可理解的未配置错误。
- 更新相关单元测试和配置文档，避免要求已经废弃的连接变量。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `deepseek-adapter`: 明确真实模型连接从持久化配置获取，缺少启用连接时在调用期安全失败。
- `runtime-configuration-validation`: 生产启动校验改为验证部署级加密主密钥，而不是废弃的模型连接环境变量。

## Impact

- 影响 `ProductionStartupValidator`、模型加密组件、生产配置说明及其单元测试。
- 不变更 API、数据库表结构或前端页面；本地 Stub 行为保持不变。
