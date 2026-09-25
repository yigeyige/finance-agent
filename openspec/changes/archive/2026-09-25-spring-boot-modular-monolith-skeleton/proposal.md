## Why

DEV-BOOT-002 已锁定 Java、Spring Boot 和 AgentScope 依赖，但后端仍只有编译探针，无法作为可启动的单体应用承载后续业务模块。现在建立清晰的模块包、应用入口和架构门禁，可以让后续任务在稳定的分层边界内迭代。

## What Changes

- 增加 `finance-agent` Spring Boot 应用入口和可执行 JAR 配置。
- 建立 `iam`、`task`、`evidence`、`packet`、`harness`、`source`、`knowledge`、`governance`、`shared` 模块包及 application/domain/infrastructure 分层占位。
- 增加 ArchUnit 架构规则，禁止领域层依赖 Web/数据库具体实现和基础设施反向依赖。
- 保留现有 AgentScope 编译探针和依赖基线，不引入真实外部系统连接。

## Capabilities

### New Capabilities

- `spring-boot-modular-skeleton`: 提供可执行 Spring Boot 单体入口、模块分层和架构依赖门禁。

### Modified Capabilities

无。

## Impact

- 影响 `backend/pom.xml`、`backend/src/main/java` 和 `backend/src/test/java`。
- 增加 ArchUnit 测试依赖和 Spring Boot repackage 插件。
- 不改变数据库、前端、部署文件或外部 API，不启动运行环境。
