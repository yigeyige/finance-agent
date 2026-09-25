## Why

仓库需要一个可重复、可离线复现的 Java 与 AgentScope 编译基线，避免后续能力切片在版本漂移或未验证 API 上继续开发。现在锁定 Java 17、Spring Boot 4.0.4 与 AgentScope Java 2.0.3，可以先验证 Harness、OpenAI 兼容模型以及权限、工作区和状态存储入口。

## What Changes

- 增加 Maven Wrapper 与最小后端 POM。
- 锁定 Java 17、Spring Boot 4.0.4、AgentScope BOM 2.0.3 及 Harness/OpenAI 扩展。
- 增加不访问真实模型的编译探针，验证公开 API 入口和 SPI 类型。
- 输出依赖基线、依赖树和框架 API 差异记录。

## Capabilities

### New Capabilities

- `java-agentscope-baseline`: 提供可重复的 Java/AgentScope 依赖、编译和 API 基线。

### Modified Capabilities

无。

## Impact

- 受影响代码：`backend/pom.xml`、后端编译探针。
- 受影响工具：仓库根目录 Maven Wrapper 与 `.mvn/wrapper/`。
- 受影响文档：`docs/dependency-baseline.md`。
- 不连接真实 DeepSeek、不启动应用、不引入数据库或生产凭据。
