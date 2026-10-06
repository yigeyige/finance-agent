## ADDED Requirements

### Requirement: Business-facing packet assembly uses existing evidence
决策包页面 SHALL 查询当前任务的脱敏证据并自动生成问题、来源、三类事实、计算见证、专业判断、建议、业务确认、验证准备和交接占位结构。业务人员 MUST 只需核对业务摘要、填写问题说明并显式确认业务影响，不得被要求输入 JSON、范围 UUID 或运行版本 UUID。

#### Scenario: Assemble packet from complete evidence
- **WHEN** 当前任务已有范围、事实快照和覆盖记录，且业务人员确认已核对业务影响
- **THEN** 页面自动生成严格的草稿请求并提交，成功后展示业务摘要和复核入口

#### Scenario: Evidence is not ready
- **WHEN** 当前任务缺少范围标识、事实快照或覆盖记录
- **THEN** 页面阻止生成草稿并说明需要先完成哪项业务步骤

### Requirement: Technical packet details are secondary
页面 SHALL 默认展示事实、覆盖度、判断和建议的中文摘要；canonical JSON、内容摘要和内部版本 MUST 放在默认收起的只读技术明细中。

#### Scenario: Business user opens a draft
- **WHEN** 草稿生成成功
- **THEN** 首屏展示可复核的业务摘要而不是原始 JSON
