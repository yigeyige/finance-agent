## Why

模型分析后需要保存业务决策包，并落实“已生成后任务信息不可修改，仍可补证再生成”的规则。

## What Changes

- 将固定任务信息、已有证据和模型结果保存为业务决策包。
- 沿用现有 revision、内容摘要和历史读取，变化新增修订，旧包只读保留。
- 首次成功生成后禁止修改任务信息；失败不锁定，补证和再次生成仍可用。
- 暂不建设请求幂等键、调用谱系、旧包回填或额外生命周期。

## Capabilities

### New Capabilities
- `business-packet-task-lock`: 包生成后的任务信息只读规则。

### Modified Capabilities
- `business-decision-packet`: 固定输入经模型分析后保存修订。

## Impact

依赖前两个切片，修改现有生成服务和任务更新校验；复用现有修订存储，不接复核、发布或交接流程。
