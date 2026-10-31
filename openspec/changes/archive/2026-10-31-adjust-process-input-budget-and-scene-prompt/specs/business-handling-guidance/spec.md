## ADDED Requirements

### Requirement: Longdi process investigation excludes organization process template table

系统 SHALL 在「审批流程生成-隆地」的调查提示词中明确要求模型不得将 `basedata.t_org_process_template` 作为选表或 SQL 查询对象。该规则 SHALL 通过迁移应用到既有场景记录，不覆盖其他调查内容、决策包提示词或流程文件生成提示词。

#### Scenario: Migration adds rule to the target scene

- **WHEN** 目标场景记录存在且调查提示词尚无该禁查规则
- **THEN** 迁移只向该场景调查提示词追加一次规则，其他场景和提示词字段不变

#### Scenario: Rule is already present

- **WHEN** 目标场景调查提示词已包含该禁查规则
- **THEN** 迁移不重复追加，也不改写现有内容
