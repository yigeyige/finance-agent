# Spec Delta

## MODIFIED Requirements

### Requirement: Extension request contracts have executable examples

契约校验 SHALL 验证业务场景 Skill、大模型连接和任务草稿扩展字段的正向及负向合成样例，并在出现未知字段或非法枚举时失败。

#### Scenario: Valid extension requests pass
- **WHEN** 开发者运行离线契约校验
- **THEN** 新增请求模型的 `*.valid.json` 样例全部通过

#### Scenario: Invalid extension requests are rejected
- **WHEN** 新增请求样例包含契约外字段或非法枚举值
- **THEN** 校验以确定性错误失败，而不会把样例视为有效
