## Context

当前应用只有 Spring Boot 启动骨架和独立 PostgreSQL 探针，没有 Web 诊断端点，也没有统一的请求上下文。实现必须兼容 Java 17、Spring Boot 4.0.4，并遵守“不启动运行环境、由用户手动验证”的仓库规则。

## Decisions

### Health and diagnostics

- 使用 Actuator health endpoint；`liveness` 只反映进程，`readiness` 聚合 `database`、`modelStub`、`sourceStub`。
- 数据库指示器只做短超时、只读 `SELECT 1`，缺少配置或连接失败返回 DOWN，并只记录安全状态码，不输出 URL、用户名、密码或异常原文。
- 模型和源以 Stub 指示器表达当前可用性：Stub 开启时为 UP；生产配置禁止 Stub 时仍由配置和显式状态给出安全的 DOWN/UNKNOWN，不伪造真实连接成功。
- `GET /api/v1/diagnostics` 返回总体状态、组件状态和上下文 ID；不返回配置值、凭证、模型原文或异常堆栈。

### Correlation context

- `DiagnosticContextFilter` 读取 `X-Trace-Id`、`X-Task-Id`、`X-Step-Id`、`X-Invocation-Id`，只接受有限长度的安全字符；缺失的 trace/invocation 由服务生成 UUID。
- 过滤器把四个字段写入 SLF4J MDC，响应回写 trace/invocation，并在 finally 中清理 MDC，避免线程复用串数据。

### Structured and safe logging

- 使用 Spring Boot 4 内置 `logging.structured.format.console=logstash`，MDC 字段进入 JSON 日志。
- `DiagnosticLogSanitizer` 采用允许字段白名单；键名命中 secret/token/password/prompt/response/reasoning 等规则时拒绝，值命中凭证或完整模型内容时替换为 `[REDACTED]`。
- 业务日志只通过结构化字段方法输出，禁止直接拼接原始请求、配置对象或异常消息。

### Verification

- 单元测试覆盖允许字段、禁止字段、凭证和 prompt/response/reasoning 的负向样例。
- MockMvc 测试覆盖诊断 API 上下文回显和安全响应头；健康指示器测试覆盖 UP/DOWN/配置缺失。
- Maven 测试/编译和 OpenSpec 校验作为实现方证据；不启动 Spring Boot、数据库或容器。
