# Java 与 AgentScope 依赖基线

## 锁定组合

| 组件 | 坐标/版本 | 说明 |
| --- | --- | --- |
| Java | 17 | `backend/pom.xml` 通过 `java.version` 锁定编译目标 |
| Spring Boot | 4.0.4 | 使用官方 `spring-boot-starter-parent` |
| AgentScope BOM | `io.agentscope:agentscope-dependencies-bom:2.0.3` | 唯一 AgentScope 版本入口 |
| Harness | `io.agentscope:agentscope-harness:2.0.3` | AgentScope BOM 不管理自身模块，显式与 BOM 版本保持一致 |
| OpenAI 兼容模型 | `io.agentscope:agentscope-extensions-model-openai:2.0.3` | 用于 DeepSeek 兼容端点；显式与 BOM 版本保持一致 |
| 图布局（预留） | `org.eclipse.elk:org.eclipse.elk.core` / `org.eclipse.elk.alg.layered:0.9.1` | 仅引入依赖，本阶段不调用；用于后续复杂流程布局评估 |

POM 没有引入 `SNAPSHOT`、Git `main` 源码或未解释的依赖版本覆盖。两个 AgentScope 扩展模块的显式 `2.0.3` 是因为官方 BOM 不声明自身模块版本，属于与 `agentscope.version` 同源的必要锁定。Spring Boot 与 AgentScope BOM 的版本组合与方案基线一致。

当前 POM 保持为编译基线，不启用 Spring Boot `repackage`：探针没有应用入口类，DEV-BOOT-003 建立模块化单体骨架后再生成可执行 JAR。

## 依赖树摘要

`./mvnw -o dependency:tree` 的直接依赖为：

```text
cn.com.crc.ems:finance-agent:0.1.0
+- org.springframework.boot:spring-boot-starter:4.0.4
\- io.agentscope:agentscope-harness:2.0.3
   \- io.agentscope:agentscope-core:2.0.3
\- io.agentscope:agentscope-extensions-model-openai:2.0.3
```

关键解析结果包括 Spring Framework 7.0.7、Reactor 3.8.2、MCP SDK 0.17.2 和 SQLite JDBC 3.50.3.0；完整树在执行验证时输出到 `backend/target/dependency-tree.txt`（`target/` 已被忽略，不作为源码提交）。

## API spike 结论

最小编译探针位于 `backend/src/main/java/cn/com/crc/ems/finance/agent/bootstrap/AgentScopeApiProbe.java`：

- `HarnessAgent` 的实际包为 `io.agentscope.harness.agent.HarnessAgent`，可通过 `HarnessAgent.builder()` 创建 builder，并设置名称、模型、文件系统工具和 shell 工具开关。
- `RuntimeContext` 的实际包为 `io.agentscope.core.agent.RuntimeContext`，可通过 `RuntimeContext.builder()` 设置 `userId`、`sessionId` 并构建。
- OpenAI 兼容模型的实际实现为 `io.agentscope.extensions.model.openai.OpenAIChatModel`，builder 支持 `apiKey`、`modelName`、`baseUrl` 和 `stream`。探针只读取环境变量，不包含真实密钥，也不会发起网络请求。
- permission、workspace、state store 的实际公开类型分别为 `PermissionContextState`、`io.agentscope.harness.agent.workspace.WorkspaceManager`、`AgentStateStore`；Harness 的分布式状态组合入口为 `io.agentscope.harness.agent.DistributedStore`。`AgentStateStore` 提供 `save/get/getList/exists/delete/listSessionIds`，并支持可选版本化读写；`WorkspaceManager` 以 `Path` 构造并提供工作区、会话文件和受控相对路径访问；`HarnessAgent.Builder` 提供 `permissionContext(...)`、`stateStore(...)`、`distributedStore(...)`、`workspace(...)` 等接入点。探针通过类型引用验证这些类存在，后续实现按 2.0.3 签名适配，不虚构独立 SPI。

## 验证记录

| 检查 | 结果 |
| --- | --- |
| Maven Wrapper | 3.3.4，使用官方 wrapper distribution；Maven 发行版由 `.mvn/wrapper/maven-wrapper.properties` 锁定 |
| JDK | 目标为 JDK 17；本机验证若使用更高 JDK，会通过 `java.version=17` 的编译目标保持 Java 17 字节码约束，并在执行日志中注明实际 JDK |
| 编译 | 以 `./mvnw -q -DskipTests package` 验证；结果见本任务执行记录 |
| 依赖树 | 以 `./mvnw dependency:tree` 验证；结果见本任务执行记录 |

本文件只记录依赖基线和 API 事实，不代表已连接真实 DeepSeek 或已启动应用。
