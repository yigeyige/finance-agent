# Spec Delta

## Purpose

业务处理多轮关联证据跨轮累积，并支持管理员一键生成可查看、可追溯的业务决策包。

## ADDED Requirements

### Requirement: Evidence accumulation across rounds
系统 SHALL 在每次业务处理模型成功生成或修订草稿后，将当轮产生的关联证据追加保存到该任务的证据工作区，并保留轮次，不得覆盖历史轮次证据。

#### Scenario: Multi-round evidence accumulates
- **WHEN** 管理员对同一任务连续发起多轮业务处理且每轮均返回关联证据
- **THEN** 证据工作区按轮追加保留全部证据，历史轮次可追溯

### Requirement: Business decision packet generation
系统 SHALL 允许管理员为任务生成业务决策包，读取证据工作区累积证据与最新业务处理草稿，按 schema、table、purpose 去重保留最新，固化决策包并 revision 递增、旧版保留。

#### Scenario: Generate packet with deduplicated evidence
- **WHEN** 管理员点击生成业务决策包且任务已存在业务处理草稿与累积证据
- **THEN** 系统生成决策包，其中证据按 schema、table、purpose 去重保留最新，条数不超过 50

#### Scenario: Content unchanged skips new revision
- **WHEN** 再次生成且内容摘要与上一修订一致
- **THEN** 系统不新增修订并提示无变化

#### Scenario: No draft rejects generation
- **WHEN** 任务尚未生成任何业务处理草稿
- **THEN** 系统拒绝生成决策包并提示先完成业务处理

### Requirement: Business decision packet view
系统 SHALL 为管理员提供业务决策包的只读查看，展示决策包标识、任务标识、修订号、业务处理主体与结论建议以及去重后的证据，且 MUST NOT 暴露模型推理过程、原始提示词或连接凭据。

#### Scenario: View generated packet
- **WHEN** 管理员打开已生成的业务决策包
- **THEN** 页面展示决策包内容且不含推理过程、提示词或连接凭据
