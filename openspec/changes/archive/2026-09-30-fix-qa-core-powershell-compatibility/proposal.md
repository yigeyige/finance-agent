## Why

用户在已启动的 local 环境执行核心合成纵向测试时，Windows PowerShell 无法解析无 BOM 的 UTF-8 脚本，PowerShell 7 则无法读取非 2xx `HttpResponseMessage` 的响应体，导致预期的 409 重查场景无法完成。运行验证还发现使用 `ASSIGN_UUID` 的字符串主键实体将领域 `UUID` 直接传给 `BaseMapper.selectById`，导致 MyBatis 试图从 `UUID` 读取实体主键属性而失败。

## What Changes

- 将核心合成纵向测试脚本保存为带 BOM 的 UTF-8，确保 Windows PowerShell 5.1 以正确编码解析。
- 抽取兼容 Windows PowerShell 与 PowerShell 7 的 HTTP 响应读取逻辑，并让执行模式的健康检查保持匿名。
- 对字符串 UUID 主键实体的 `BaseMapper.selectById` 调用统一复用既有 `PersistenceUuid.toStorage` 映射。
- 注册 PostgreSQL UUID TypeHandler，使实体中的 UUID 外键、审计字段和关联字段可安全读写。
- 让未知 API 异常输出与响应 traceId 关联的最小结构化遥测，不记录异常正文、堆栈、SQL 或请求内容。
- 将重复的 Flyway `V10` 检查点迁移编号顺延为 `V12`，使审计列迁移可以被唯一识别和应用。
- 为两种 PowerShell 宿主补充静态解析与预期非 2xx 响应处理验证。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `synthetic-vertical-slice`: 执行模式须可在 Windows PowerShell 5.1 与 PowerShell 7 下保留预期非 2xx 响应并继续断言。

## Impact

- 修改 `scripts/qa-core-synthetic-vertical-slice.ps1`、相关契约测试、MyBatis-Plus 仓储及 UUID 参数映射配置。
- 修正 Flyway 迁移资源版本号；现有数据库须经既有迁移入口更新到审计 outbox 契约。
- 不变更 API、领域模型、运行 profile 或默认静态模式的离线边界，也不引入依赖。
