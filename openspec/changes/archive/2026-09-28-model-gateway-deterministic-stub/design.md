# Design: ModelGateway 和确定性 Stub

## Contracts

- `ModelGateway.generateStructured` 是唯一模型调用入口，调用方必须传入用途、模板版本、字段策略投影后的 `AllowedContext`、输出 `JsonSchema`、`ModelBudget` 和 `CancellationToken`。
- `AllowedContext` 使用不可变 JSON 快照并复用现有 `SensitiveFieldScanner`，禁止 prompt、response、token、credential 和 reasoning 等字段。
- `ModelResult` 只返回已校验的结构化值和 `ModelMetadata`。元数据使用输入证据 ID、字段摘要、输入/输出摘要、模型绑定版本、token、延迟和完成原因，不携带原文。

## Deterministic Stub

`DeterministicModelStub` 通过用途、模板版本和允许上下文摘要定位 `ModelFixture`。fixture 深拷贝后交由调用方提供的 schema 校验，再返回确定性结果；不存在 fixture 时失败，不生成自然语言或回退网络模型。

## Budget and cancellation

Stub 在读取 fixture 前检查取消令牌，并按 fixture 声明的 token/cost 与单次步骤消费检查 `ModelBudget`。超限返回稳定 `BUDGET_EXHAUSTED`；取消返回 `CANCELLED`。

## Schema

`StepProposalSchema` 作为首个内置 schema，按照方案附件 B 的 `additionalProperties=false`、必填字段、备选数量、字符串长度、UUID 和调用次数约束解析步骤提议。
