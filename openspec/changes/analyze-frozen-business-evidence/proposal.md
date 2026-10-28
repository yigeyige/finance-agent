## Why

当前包直接沿用初稿结论，需要让模型依据固定的任务信息和已有证据重新分析，真正使用决策包提示词。

## What Changes

- 增加决策包专用模型调用，消费本次固定的 `decisionPacketPrompt`。
- 输出结论、推荐方案、理由及可选备选方案，交由下一切片保存。
- 生成时不查询补证；暂不建设冲突、缺口、覆盖度或分块分析机制。
- 无证据、输入超限、模型失败或输出格式非法时明确报错，不保存伪成功包。

## Capabilities

### New Capabilities
- `business-packet-analysis`: 基于固定已有证据生成业务结论与建议。

### Modified Capabilities
无。

## Impact

依赖 `capture-business-evidence-lineage`；复用现有模型客户端、超时及结构校验能力。本切片产出分析结果，正式包保存由下一切片完成。
