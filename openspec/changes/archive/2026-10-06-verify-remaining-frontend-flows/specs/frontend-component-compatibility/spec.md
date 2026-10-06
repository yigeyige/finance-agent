## ADDED Requirements

### Requirement: Components match the fixed frontend baseline
前端 SHALL 仅使用 Element UI 2.0.0 已提供的组件和合法属性类型；分隔、空状态和描述信息如果基线未提供对应组件，MUST 使用项目内可维护的语义化结构。

#### Scenario: Render remaining workflow pages
- **WHEN** 用户打开工作台、任务诊断、证据、决策包草稿或已发布决策包页面
- **THEN** 页面不产生未知 Element 组件或输入属性类型错误，关键信息仍可读
