## MODIFIED Requirements

### Requirement: Guidance draft persistence
系统 SHALL 仅在业务处理模型 attempt 成功且结构化响应通过校验后，将草稿全文持久化为只读留痕，并关联该成功 attempt、冻结 Skill 修订和模板版本。草稿 MUST 包含处理类型、对象前提、信息来源、系统、日期范围、处理内容、推荐方案、简短说明、检查结论和关联证据引用，且 MUST NOT 暴露模型推理过程、原始提示词或连接凭据。任务详情 MUST 展示跨轮 attempt 的结论与证据状态，不得把失败 attempt 的 `COLLECTED_UNANALYZED` 证据表述为已分析结论。

#### Scenario: Initial draft is persisted automatically
- **WHEN** 管理员触发模型分析且 attempt 成功返回通过校验的草稿
- **THEN** 系统保存草稿全文、attempt 引用、Skill 修订和模板版本，且不保存推理过程或原始提示词

#### Scenario: Revised draft is persisted
- **WHEN** 管理员提交修改意见且新的 attempt 成功返回通过校验的草稿
- **THEN** 系统追加保存新草稿并保留历史草稿与 attempt 关系

#### Scenario: Task detail shows conclusion and evidence states
- **WHEN** 具有相应权限的身份打开任务详情
- **THEN** 页面展示跨轮结论、结构化证据引用和明确状态，且不含连接凭据

#### Scenario: Model fails after evidence collection
- **WHEN** attempt 已采集证据但最终模型调用或输出校验失败
- **THEN** 系统不保存成功草稿，保留失败 attempt 与待分析证据并展示可理解状态

#### Scenario: Draft is absent
- **WHEN** 任务尚未产生成功草稿
- **THEN** 详情页以空态展示“尚未生成检查结论与已分析证据”，且不报错
