## MODIFIED Requirements

### Requirement: Frozen versioned tool catalog

系统 SHALL 提供固定版本工具目录；每个工具包含 schema 引用、风险等级、允许任务状态、审批要求、超时、结果大小和环境白名单，并生成版本摘要。

#### Scenario: Runtime bundle freezes catalog version

- **WHEN** 任务使用已发布 runtime bundle 解析工具
- **THEN** 仅接受 bundle 声明版本和摘要一致的目录条目

#### Scenario: Unknown or retired tool is rejected

- **WHEN** 请求伪造工具名、schema 版本、环境或退役条目
- **THEN** 目录解析失败，不允许动态注册或执行
