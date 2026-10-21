# guidance-draft-persistence

## Purpose

业务处理草稿（含检查结论与关联证据）在模型分析成功后自动持久化留痕，并在任务详情页只读展示最新草稿的结论与证据。

## Requirements

### Requirement: Guidance draft persistence
系统 SHALL 在业务处理模型成功生成或修订草稿后，自动将草稿全文（含处理类型、对象前提、信息来源、系统、日期范围、处理内容、推荐方案、简短说明、检查结论和关联证据）持久化为只读留痕记录。草稿持久化 MUST NOT 暴露模型推理过程、原始提示词或连接凭据。任务详情 MUST 展示最新草稿的检查结论与关联证据。

#### Scenario: Initial draft is persisted automatically
- **WHEN** 管理员在新建业务处理页触发模型分析并成功返回草稿
- **THEN** 系统自动保存草稿全文并生成版本留痕，且不保存推理过程或原始提示词

#### Scenario: Revised draft is persisted
- **WHEN** 管理员在模型协助修改中提交修改意见并成功返回新草稿
- **THEN** 系统追加保存新草稿，历史草稿版本可追溯

#### Scenario: Task detail shows conclusion and evidence
- **WHEN** 具有查看权限的身份打开任务详情
- **THEN** 页面展示最新草稿的检查结论与结构化关联证据，证据不含连接凭据

#### Scenario: Draft is absent
- **WHEN** 任务尚未生成任何草稿
- **THEN** 详情页以空态展示“尚未生成检查结论与关联证据”，且不报错
