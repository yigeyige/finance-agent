# permission-hitl-tool Specification

## Purpose
TBD - created by archiving change permission-hitl-tool-bridge. Update Purpose after archive.
## Requirements
### Requirement: Harness permission pauses at tool boundary

系统 SHALL 在 Harness 工具请求处先经过 Permission middleware，并将允许请求暂停到 HITL；Harness permission 不得替代业务审批。

#### Scenario: Tool request waits for HITL
- **WHEN** 默认 Harness permission 收到已登记工具请求
- **THEN** 返回 `WAITING_APPROVAL` 事件且不调用业务 ToolGateway

#### Scenario: Bypass cannot skip business gate
- **WHEN** Harness mode 为 BYPASS 或 DONT_ASK
- **THEN** 请求被拒绝或继续等待，不能直接获得业务 permit

### Requirement: HITL bridge requires the business ToolGateway

系统 SHALL 在人工批准后再次调用现有业务 `ToolGateway`，并以结构化事件表达批准、拒绝和过期；permit 不得进入模型或 HTTP 响应。

#### Scenario: Approved request passes two layers
- **WHEN** 待审批请求未过期且人工批准
- **THEN** bridge 调用业务 ToolGateway 二次守卫，业务守卫通过后才返回服务端内部 permit

#### Scenario: Rejected or expired request stops
- **WHEN** 人工拒绝、请求不存在或请求超过 TTL
- **THEN** 返回 `REJECTED`/`EXPIRED` 事件，不调用业务 ToolGateway

### Requirement: Dynamic and injected tool requests are rejected

系统 SHALL 只接受已构造的固定工具请求，不根据 prompt、模型文本或动态工具名签发 permit。

#### Scenario: Prompt injection cannot bypass approval
- **WHEN** 模型文本要求跳过审批或伪造 permit
- **THEN** bridge 不解析该文本，仍需独立 HITL 和业务 ToolGateway 校验

