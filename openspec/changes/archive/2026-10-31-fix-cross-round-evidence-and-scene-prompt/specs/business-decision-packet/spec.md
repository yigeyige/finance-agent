## ADDED Requirements

### Requirement: Decision packet uses latest evidence snapshot

决策包生成 SHALL 使用任务最新一轮证据快照；若最新轮次是历史增量（snapshot=false），SHALL 合并全部轮次以保持兼容。证据为空时 MUST 拒绝并返回“尚无调查证据，请先完成调查”，MUST NOT 调用模型。

#### Scenario: Latest snapshot is used
- **WHEN** 任务存在多轮证据且最新轮次为快照
- **THEN** 决策包只使用最新快照

#### Scenario: Legacy data is merged
- **WHEN** 最新轮次为历史增量
- **THEN** 决策包合并全部轮次

#### Scenario: Empty evidence is rejected
- **WHEN** 任务没有证据
- **THEN** 系统拒绝生成且不调用模型
