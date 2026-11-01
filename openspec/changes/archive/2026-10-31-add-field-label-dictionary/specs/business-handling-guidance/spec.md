## ADDED Requirements

### Requirement: Field label dictionary in rendering

业务处理与任务详情页面 SHALL 在展示模型字段时提供统一中文说明。系统 MUST 集中维护字段路径到中文名称/说明的映射，并在流程事实、调查结论和决策包字段旁展示字段路径与中文名。未命中字典的字段 MUST 回退为原字段名，且不得影响页面渲染。模型输出、提示词和后端契约 MUST 保持不变。

#### Scenario: Process plan annotation
- **WHEN** 页面展示 guidanceDraft.processPlan
- **THEN** 同时展示“流程结构”中文说明

#### Scenario: Common packet fields
- **WHEN** 页面展示 subjectKey、recommendedGoal、reason、conclusion 或 evidence
- **THEN** 展示对应中文说明

#### Scenario: Unknown field fallback
- **WHEN** 页面遇到字典中不存在的字段
- **THEN** 只展示原字段名，不报错

#### Scenario: Contract unchanged
- **WHEN** 增加字段说明
- **THEN** 模型输出 JSON、提示词和后端接口保持不变
