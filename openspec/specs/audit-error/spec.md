# Audit and Error Model

## Purpose

统一关键业务动作的审计拦截和对外错误契约，保证失败可追溯且不泄漏内部细节。
## Requirements
### Requirement: Stable safe error model

系统 SHALL 使用稳定领域错误码和 `retryClass` 表达可重试、新审批、人工对账或不可重试；HTTP 响应不得包含 SQL、路径、凭证、Token 或异常堆栈。

#### Scenario: Sensitive exception text is not exposed

- **WHEN** 底层异常包含 SQL、文件路径或凭证片段
- **THEN** HTTP 响应只返回安全目录消息

### Requirement: Fail-closed audited actions

关键任务、范围、审批、工具、确认、复核、发布、交接和管理动作 SHALL 在失败时写入审计；审计写入失败时动作整体失败关闭。

#### Scenario: Audit failure closes action

- **WHEN** 关键动作失败且失败审计无法写入
- **THEN** 执行器返回 `AUDIT_WRITE_FAILED`，不得假报原始动作已完成

