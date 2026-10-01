## ADDED Requirements

### Requirement: P1 test business pilot is local/test and read-only
系统 SHALL 将 P1 试点限定为 local/test 中的固定虚构或已脱敏测试业务样本。试点准备、人工验证和问题清单 MUST 不访问真实源、不写入源、不读取禁止字段，且 MUST NOT 推断实际业务影响或生产事实。

#### Scenario: P1 test business pilot is prepared
- **WHEN** 系统准备 P1 测试业务试点任务包
- **THEN** 任务包明确测试范围、只读边界、人工验证步骤和非生产限制
