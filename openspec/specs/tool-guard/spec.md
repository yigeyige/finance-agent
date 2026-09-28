# Tool Guard

## Purpose

在外部源副作用前统一校验业务审批、工具目录版本、环境和幂等意图。
## Requirements
### Requirement: Two-layer tool guard

系统 SHALL 在外部调用前同时校验 Harness/业务 permit、当前步骤、审批摘要、范围、工具目录版本、状态、环境和幂等意图；任一不匹配均拒绝调用。

#### Scenario: Tampered or expired permit is rejected

- **WHEN** permit 被修改、跨任务使用或超过有效期
- **THEN** ToolGateway 在创建调用意图前拒绝请求

#### Scenario: Idempotent intent is recorded after guards

- **WHEN** 两层守卫均通过
- **THEN** 仅调用 `ToolInvocationService` 记录幂等意图，外部副作用由后续执行器负责

