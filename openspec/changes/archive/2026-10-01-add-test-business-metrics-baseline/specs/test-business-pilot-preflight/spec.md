## ADDED Requirements

### Requirement: Pilot preflight is a metrics baseline input
系统 SHALL 允许测试业务量测基线读取试点预检的覆盖、未知和预期人工确认计数。该输入 MUST 保持固定样本版本与 local/test 边界，且不得替代运行时采集。

#### Scenario: Metrics baseline consumes preflight summary
- **WHEN** 系统生成测试业务样本指标
- **THEN** 指标与预检摘要中的覆盖、未知和人工确认计数一致
