## Why

当前包直接沿用初稿结论，需要让模型依据固定的任务信息与已有证据重新分析，真正使用决策包提示词，并产出处理对象。

## What Changes

- 在 `BusinessModelClient` 增加决策包专用分析方法。
- 输入：任务信息（title、originalQuestion）+ 已有累积证据 + `decisionPacketPrompt`。
- 输出：`subjectKey`（处理对象）、`conclusion`、`recommendedGoal`、`reason` 及可选 `alternativeGoals`。
- 生成时不查询补证；无证据、输入超限、模型失败或输出格式非法时明确报错。

## Capabilities

### New Capabilities
- `business-packet-analysis`: 基于固定已有证据生成处理对象、结论与建议。

### Modified Capabilities
无。

## Impact

复用现有模型客户端、超时及结构校验能力。本切片产出分析结果，正式包保存由下一切片完成。
