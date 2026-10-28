# Spec Delta

## MODIFIED Requirements

### Requirement: Operational premise provenance

系统 SHALL 将运维人员提供的前提作为独立记录保存，包含主体标识、原文陈述、提供人、来源说明和独立核实标志；前提不得被改写为源系统已核实事实。

#### Scenario: Store an operational premise
- **WHEN** 任务存在且运维人员提交一条带来源说明的前提
- **THEN** 系统追加 `task_premise` 记录并写入最小审计事件，`independentlyVerified` 默认保持为 false

#### Scenario: Premise source is explicit
- **WHEN** 前提缺少主体标识或来源说明
- **THEN** 请求被拒绝且不产生前提记录
