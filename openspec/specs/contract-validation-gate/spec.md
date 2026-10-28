# contract-validation-gate Specification

## Purpose
TBD - created by archiving change contract-directory-generation-boundary. Update Purpose after archive.
## Requirements
### Requirement: Contract validation covers positive and negative examples
The system SHALL provide valid and invalid examples for the frozen task, approval, event, decision-packet, and error contracts, and SHALL fail validation for extra object fields, invalid enum values, malformed date-time values, and arrays beyond declared limits.

#### Scenario: Valid examples pass
- **WHEN** the contract verification script reads files marked `*.valid.json`
- **THEN** every valid example passes its corresponding JSON Schema

#### Scenario: Extra fields are rejected
- **WHEN** an otherwise valid task example includes an undeclared property
- **THEN** verification fails with the property path and schema name

#### Scenario: Invalid enum is rejected
- **WHEN** an error example uses a retry class outside the declared enum
- **THEN** verification fails rather than accepting the unknown value

#### Scenario: Date-time and array bounds are enforced
- **WHEN** an event uses a non-RFC3339 date-time or a decision packet exceeds its findings limit
- **THEN** verification fails with a deterministic validation message

### Requirement: Contract verification is runnable without application startup
The system SHALL expose `scripts/verify-contracts.py` and a Windows PowerShell wrapper, and SHALL complete without starting Spring Boot, Vite, a database, or an external service.

#### Scenario: Local verification is self-contained
- **WHEN** a developer runs the documented verification command from the repository root
- **THEN** the script exits successfully using repository files and standard tooling only

### Requirement: Extension request contracts have executable examples
契约校验 SHALL 验证业务场景 Skill、大模型连接和任务草稿扩展字段的正向及负向合成样例，并在出现未知字段或非法枚举时失败。

#### Scenario: Valid extension requests pass
- **WHEN** 开发者运行离线契约校验
- **THEN** 新增请求模型的 `*.valid.json` 样例全部通过

#### Scenario: Invalid extension requests are rejected
- **WHEN** 新增请求样例包含契约外字段或非法枚举值
- **THEN** 校验以确定性错误失败，而不会把样例视为有效
