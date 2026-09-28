# Deterministic Rules

## Purpose

对获准快照执行版本化确定性规则，并生成可复算的判断与计算见证。
## Requirements
### Requirement: Versioned deterministic rule execution

系统 SHALL 通过版本化 Java 规则 SPI 对获准快照执行确定性计算，输出 Finding 和包含输入/输出摘要、规则/算法版本及快照 ID 的 ComputationWitness。

#### Scenario: Personnel reference golden vector
- **WHEN** 使用同一获准快照集合和已登记人员引用规则执行
- **THEN** 系统输出稳定 Finding、输入/输出摘要和规则版本一致的计算见证

#### Scenario: Reject unsafe or incomplete input
- **WHEN** 输入缺少快照、包含敏感字段或字段不在获准投影内
- **THEN** 系统拒绝生成确定性结论和 MATCHED 见证

### Requirement: Independent rule recomputation

系统 SHALL 提供独立 `rules.recompute` 能力，按规则版本复算并将结果分类为 `MATCHED`、`MISMATCHED` 或 `UNAVAILABLE`。

#### Scenario: Recompute matches
- **WHEN** 快照、规则版本、算法版本及输入/输出摘要均与原见证一致
- **THEN** 复算结果为 `MATCHED`

#### Scenario: Recompute cannot confirm
- **WHEN** 摘要不匹配、快照集合变化或规则版本未知
- **THEN** 复算结果不得为 `MATCHED`，分别返回 `MISMATCHED` 或 `UNAVAILABLE`

