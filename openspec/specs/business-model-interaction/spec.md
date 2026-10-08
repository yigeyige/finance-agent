# business-model-interaction Specification

## Purpose
规定调查方案生成和证据分析中的受控大模型交互边界，确保模型输入受限、输出可校验、运行模式可辨识，并始终保留人工确认。

## Requirements

### Requirement: Controlled model plan generation
系统 SHALL 在当前任务范围确认后，通过受控模型服务生成一个推荐调查目标、最多两个备选目标和业务理由。模型输入 MUST 由后端从当前任务和已确认范围构造，不接受前端提交任意上下文 JSON；模型输出 MUST 通过严格结构校验后返回。

#### Scenario: Live model generates a plan
- **WHEN** 真实模型已配置且 OPERATOR 请求为当前任务生成调查方案
- **THEN** 系统调用已配置模型并返回推荐目标、备选目标、业务理由以及不含敏感内容的调用元数据

#### Scenario: Stub model generates a plan
- **WHEN** 当前环境启用模型 Stub 且 OPERATOR 请求生成调查方案
- **THEN** 系统返回确定性模拟方案，并明确标识模式为 `STUB` 和模型名称为“本地模拟模型”

### Requirement: Controlled model evidence analysis
系统 SHALL 在任务已有脱敏事实、覆盖记录和规则判断后，通过受控模型服务生成业务摘要、风险判断和后续建议。发送给模型的数据 MUST 限于计数、覆盖状态、事实分类和规则判断陈述，不得包含未授权原始字段、密钥或模型推理过程。

#### Scenario: Evidence is analyzed
- **WHEN** 当前任务具有事实快照、覆盖记录和判断，且 OPERATOR 请求模型分析
- **THEN** 系统返回业务摘要、风险等级、风险说明、建议列表和非敏感调用元数据

#### Scenario: Evidence is incomplete
- **WHEN** 当前任务缺少事实快照、覆盖记录或判断
- **THEN** 系统拒绝模型调用并用业务语言说明需要先完成证据采集

### Requirement: Model calls fail safely
模型调用 SHALL 使用有界超时、单次尝试和结构化响应校验。调用失败、超时或输出非法时，系统 MUST NOT 保存步骤、生成决策包或回退成未标识的伪模型结果。

#### Scenario: Provider fails
- **WHEN** 模型服务超时、限流或返回非法结构
- **THEN** API 返回安全的业务错误，不包含密钥、原始提示词、推理内容或供应商响应正文

### Requirement: Model interaction metadata is visible
每次成功模型交互 SHALL 返回模式、模型显示名、输入摘要、输出摘要、token 数和耗时，且 MUST NOT 返回提示词原文或思考过程。

#### Scenario: User reviews model output
- **WHEN** 前端收到成功的模型方案或证据分析
- **THEN** 页面能够明确区分真实模型与本地模拟模型，并展示模型产出需要人工确认

### Requirement: Model guidance is visible and editable
前端 SHALL 在业务处理向导中展示模型的初稿、模式、显示名称、简短理由和会话回复。管理员 MUST 能编辑所有业务字段，并能通过自然语言修改意见请求新的结构化初稿。

#### Scenario: Stub guidance is clearly identified
- **WHEN** 当前环境启用模型 Stub 并返回业务处理初稿或会话修订
- **THEN** 页面明确显示“本地模拟模型”，不将结果表述为真实模型输出

#### Scenario: Model request fails
- **WHEN** 初稿或会话修订请求失败、超时或响应不符合结构
- **THEN** 页面保留管理员已填写内容，展示中文失败说明和重试入口，不提交前提、范围或业务方案
