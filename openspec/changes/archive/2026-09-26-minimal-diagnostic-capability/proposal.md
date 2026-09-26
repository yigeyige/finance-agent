## Why

DEV-BOOT-009 需要让最小 Spring Boot 骨架可被安全诊断：调用方能区分数据库、模型 Stub 和源 Stub 状态，运维日志能按任务上下文检索，同时不能把凭证、禁止字段、完整 prompt/response 或 reasoning 写入日志。本变更只覆盖诊断与可观测性基础能力，不引入监控告警、持久化审计或真实模型/源接入。

## What Changes

- 增加 readiness/liveness 分组，并为数据库、模型 Stub、源 Stub 提供只读健康指示器。
- 增加只读诊断 API，返回非敏感的依赖状态与当前相关上下文。
- 增加请求上下文过滤器，维护 `traceId/taskId/stepId/invocationId` 的 MDC 和响应头。
- 启用 Spring Boot 结构化 JSON 日志，并通过白名单/模式过滤器阻断敏感字段、凭证、完整 prompt/response 和 reasoning。
- 为正向和负向日志过滤、健康状态及诊断 API 增加单元测试和契约说明。

## Non-goals

- 不实现监控告警、指标仪表盘、日志外发或持久化审计。
- 不创建数据库对象，不调用真实模型或业务源；Stub 状态只反映配置/显式探针结果。

## Impact

- `backend/src/main/java` 增加 diagnostics、web/config 组件。
- `backend/src/main/resources` 增加 Actuator、结构化日志和诊断配置。
- `backend/src/test/java` 增加脱敏、上下文、健康状态和 API 测试。
- `docs/build-and-run.md`、任务卡和相关方案补充人工验证示例。
