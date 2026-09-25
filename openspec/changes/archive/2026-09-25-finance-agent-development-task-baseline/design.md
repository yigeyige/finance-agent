## Context

当前仓库只有目录骨架，后续任务依赖 AgentScope Harness 和 OpenAI 兼容模型扩展。官方 AgentScope 2.0.3 BOM 管理外部依赖，但不声明自身模块的版本，因此 Harness 与 OpenAI 扩展需要显式使用同一个 `agentscope.version` 属性。

## Decisions

- 以 `spring-boot-starter-parent:4.0.4` 作为 Maven parent，以 AgentScope 2.0.3 BOM 作为依赖管理入口。
- 将 `agentscope-harness` 与 `agentscope-extensions-model-openai` 显式锁定到 2.0.3；不使用 SNAPSHOT、Git main 或本地源码替代物。
- 将 `spring-framework.version` 显式对齐到 7.0.7，因为 AgentScope 2.0.3 BOM要求该版本，而 Spring Boot 4.0.4 parent 默认解析为 7.0.6。
- 使用官方 Maven Wrapper 3.3.4，Maven 发行版锁定为 3.9.11，并在 properties 中校验 wrapper JAR SHA-256。
- 只编译探针，不启用 Spring Boot repackage；可执行应用入口在 DEV-BOOT-003 建立。

## API Verification

探针引用 `HarnessAgent`、`RuntimeContext`、`OpenAIChatModel`、`PermissionContextState`、`WorkspaceManager`、`AgentStateStore` 和 `DistributedStore`。使用 `javap` 对 2.0.3 JAR 检查 builder、状态存储和工作区公开签名，结论记录在 `docs/dependency-baseline.md`。

## Verification

在 Temurin JDK 17.0.20.1 下运行 Wrapper Maven 3.9.11：在线解析依赖后，`-o -q -DskipTests package` 离线编译通过；`dependency:tree` 离线输出成功。探针只读取 `DEEPSEEK_API_KEY` 环境变量，不发起网络调用。
