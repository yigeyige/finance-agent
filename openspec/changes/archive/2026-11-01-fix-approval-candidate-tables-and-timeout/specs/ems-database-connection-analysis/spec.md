## ADDED Requirements

### Requirement: Approval candidate tables and timeout fallback

审批流程调查 SHALL 在 schema 搜索中包含 `act_re_model`、`act_re_procdef`、`psc_act_config`、`psc_act_sequence_flow` 等核心表提示。`investigationPrompt` MUST 支持至少 8000 字符。前置流程定义查询为空时，系统 MUST 停止后续节点和流转查询，并在 missingItems 记录未定位流程。模型网关返回 504 时 SHALL 重试一次。

#### Scenario: Candidate tables are recalled
- **WHEN** 审批流程场景执行选表
- **THEN** 候选表包含 act_re_model 等核心表

#### Scenario: Long investigation prompt
- **WHEN** investigationPrompt 超过 2000 字符
- **THEN** 系统仍可加载场景 Skill，不返回 MODEL_CONTEXT_INVALID

#### Scenario: Process definition not found
- **WHEN** act_re_model/act_re_procdef 查询为空
- **THEN** 系统停止后续节点/流转查询并记录 missingItems

#### Scenario: Gateway 504
- **WHEN** 模型网关返回 504
- **THEN** 系统重试一次，仍失败才返回 MODEL_UNAVAILABLE
