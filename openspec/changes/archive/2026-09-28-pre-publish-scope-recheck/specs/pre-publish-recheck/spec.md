## MODIFIED Requirements

### Requirement: Recheck the complete approved scope

系统 SHALL 基于批准范围重新枚举结果，比较新增、删除、字段摘要、生效分类、来源版本、规则版本和模板版本。

#### Scenario: New reference invalidates draft
- **WHEN** 当前全量结果出现批准基线不存在的新对象
- **THEN** 系统记录新增差异并将草稿影响判定为 `STALE_DRAFT`

#### Scenario: Effective or version change is detected
- **WHEN** 对象字段、生效分类、来源、规则或模板版本任一变化
- **THEN** 系统记录对应差异，不能仅因来源版本未变而忽略变化

### Requirement: Publish readiness and recheck record

系统 SHALL 输出可持久化的重查记录；仅当全范围无差异时返回 `READY_TO_PUBLISH`，否则返回 `STALE_DRAFT`。

#### Scenario: No impact enters ready state
- **WHEN** 批准范围与当前全量结果集合及全部比较维度一致
- **THEN** 系统生成重查记录并返回 `READY_TO_PUBLISH`

#### Scenario: Persist affected result
- **WHEN** 重查发现任一新增、删除或属性变化
- **THEN** 系统生成包含差异摘要和结果摘要的记录，并标记草稿为 `STALE_DRAFT`
