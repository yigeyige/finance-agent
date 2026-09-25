# Java/AgentScope 编译基线

## ADDED Requirements

### Requirement: reproducible dependency lock
系统 MUST 通过 Maven Wrapper 可重复解析 Java 17、Spring Boot 4.0.4、AgentScope BOM 2.0.3、Harness 2.0.3 和 OpenAI 扩展 2.0.3，且不得使用 SNAPSHOT 或 Git main 替代发布物。

#### Scenario: offline compile after dependency resolution
- **WHEN** 依赖已由 Wrapper 解析到本地缓存
- **THEN** `./mvnw -o -q -DskipTests package` 必须成功

### Requirement: compile-only API probe
系统 MUST 提供不需要真实模型凭据、不发起模型请求的最小 Java 编译探针，并验证 HarnessAgent、RuntimeContext、OpenAIChatModel、permission、workspace 和 state store 的公开入口。

#### Scenario: stub API compilation
- **WHEN** 使用 Java 17 编译后端
- **THEN** 探针必须成功编译，并且代码中不得包含真实 API key 或生产连接串
