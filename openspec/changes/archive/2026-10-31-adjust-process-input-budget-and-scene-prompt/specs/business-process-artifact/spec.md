## MODIFIED Requirements

### Requirement: Bounded model context

系统 SHALL 在每次模型调用前检查输入规模，MUST 不超过实际模型可用上下文；对 `deepseek-v4.1-flash` MUST 按用户确认的 1M 输入上限的一半，即 500,000 UTF-8 字节，作为本地保守输入门禁，不因独立的 384K 输出上限再次扣减该门禁。其他模型既有门禁 SHALL 保持不变。超限时 SHALL 用中文提示缩减材料，不自动拆分并继续调用。

#### Scenario: Current model input is within half limit

- **WHEN** `deepseek-v4.1-flash` 的本次模型输入恰好为 500,000 UTF-8 字节
- **THEN** 本地输入门禁允许模型调用继续

#### Scenario: Context exceeds available limit

- **WHEN** 本次调查、决策包或文件生成的模型输入超过适用上限
- **THEN** 本次调用被拒绝，已有调查结论、决策包和文件批次保持不变
