## Why

覆盖枚举已经区分 `COMPLETE`、`PARTIAL` 和 `UNKNOWN`，但决策包校验目前主要关注章节结构，无法阻止“不完整覆盖却写成全范围结论”、把 RAG/记忆/模型提示写成当前事实，或在业务影响未确认时给出确定影响判断。

## What Changes

- 新增版本化覆盖度与事实措辞 fixture，固定分页变化、冲突、未来转当前、RAG/记忆占位和业务影响未确认场景。
- 新增领域化 `PacketWordingValidator`，在决策包验证前检查覆盖限制、事实来源和业务影响措辞。
- 新增离线 PowerShell 校验脚本与后端契约测试，输出各场景校验结果和审阅证据要求。

## Capabilities

### New Capabilities

- `coverage-wording-gate`: 将覆盖度和事实来源约束落实为可执行的决策包措辞门禁。

### Modified Capabilities

- 无

## Impact

- 新增一个后端领域校验组件、单元/契约测试、fixture 和脚本。
- 不新增外部依赖；调用方可以在现有 `PacketDocumentValidator` 之后显式调用措辞校验。
