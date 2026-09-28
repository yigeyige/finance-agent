## ADDED Requirements

### Requirement: Unified structured model gateway

系统 SHALL 通过 `ModelGateway` 接收用途、模板版本、字段策略投影后的允许上下文、输出 schema、预算和取消令牌，并只返回已通过 schema 校验的结构化结果和非敏感模型元数据。

#### Scenario: Structured request is validated
- **WHEN** 调用方提交合法用途、模板、允许上下文、输出 schema 和预算
- **THEN** 网关校验结构化输出并返回版本、摘要、token、延迟和完成原因等元数据，不返回完整 prompt、response 或 reasoning

#### Scenario: Forbidden context is rejected
- **WHEN** 允许上下文包含 prompt、response、credential、token 或 reasoning 等禁止字段
- **THEN** 请求被拒绝且不执行模型调用

### Requirement: Deterministic fixture stub

系统 SHALL 提供不访问网络的确定性 Stub，按输入 fixture 稳定生成步骤提议或草稿；缺失 fixture 时不得生成自然语言智能或回退到网络模型。

#### Scenario: Same fixture is reproducible
- **WHEN** 使用相同用途、模板版本和允许上下文摘要调用 Stub
- **THEN** 返回相同结构化结果摘要和模型元数据

#### Scenario: Missing fixture fails closed
- **WHEN** Stub 找不到对应 fixture
- **THEN** 返回稳定的 `FIXTURE_NOT_FOUND` 错误，不发起网络请求

### Requirement: Output schema and budget enforcement

系统 SHALL 拒绝非法结构、额外字段、预算超限和取消中的调用。

#### Scenario: Invalid output is rejected
- **WHEN** fixture 输出不符合 `JsonSchema`，包含额外字段或超出步骤提议约束
- **THEN** 返回 `SCHEMA_INVALID`，不产生模型结果

#### Scenario: Budget or cancellation stops invocation
- **WHEN** fixture 消费超过调用预算或取消令牌已取消
- **THEN** 返回 `BUDGET_EXHAUSTED` 或 `CANCELLED`，不继续执行 schema 结果交付
