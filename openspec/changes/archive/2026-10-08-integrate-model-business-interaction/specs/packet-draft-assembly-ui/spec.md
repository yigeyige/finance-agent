## MODIFIED Requirements

### Requirement: Business-facing packet assembly uses existing evidence
决策包页面 SHALL 查询当前任务的脱敏证据，并在业务人员触发后调用受控模型生成业务摘要、风险判断和后续建议，再自动生成问题、来源、三类事实、计算见证、专业判断、模型建议、业务确认、验证准备和交接占位结构。业务人员 MUST 核对模型产出并显式确认业务影响，不得被要求输入 JSON、范围 UUID 或运行版本 UUID。

#### Scenario: Analyze evidence and assemble packet
- **WHEN** 当前任务已有范围、事实快照、覆盖记录和判断，模型分析成功，且业务人员确认已核对业务影响
- **THEN** 页面把模型摘要和建议纳入严格草稿请求并提交，成功后展示业务摘要和复核入口

#### Scenario: Evidence is not ready
- **WHEN** 当前任务缺少范围标识、事实快照、覆盖记录或判断
- **THEN** 页面阻止模型分析和草稿生成，并说明需要先完成哪项业务步骤

#### Scenario: Model evidence analysis fails
- **WHEN** 模型分析失败、超时或输出不符合结构
- **THEN** 页面保留当前证据概况，显示重试入口且不生成决策包草稿

### Requirement: Technical packet details are secondary
页面 SHALL 默认展示事实、覆盖度、模型摘要、风险判断和建议的中文摘要；canonical JSON、内容摘要、模型调用摘要和内部版本 MUST 放在默认收起的只读技术明细中。

#### Scenario: Business user opens a draft
- **WHEN** 草稿生成成功
- **THEN** 首屏展示经过人工确认的业务摘要和模型建议，而不是原始 JSON
