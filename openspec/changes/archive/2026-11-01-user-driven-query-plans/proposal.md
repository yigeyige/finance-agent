## Why

当前一次 guidance 内自动执行最多 3 轮查询，用户无法控制节奏；空表会反复查询；单条 SQL 无法表达“流程定义 → 节点/流转”的依赖查询。

## What Changes

- 一次用户动作（首次分析、聊天反馈）只执行一个查询计划，不再自动进入下一轮。
- 查询计划模型输出 `steps[]`，支持 `dependsOn` 和参数化 `bindings`，最多 5 步。
- 执行引擎按拓扑顺序串行执行，单步 30 行；0 行/失败时跳过依赖步骤，独立步骤继续。
- 初稿模型输出 `needMoreEvidence`、`needTableReselection`；聊天框即继续入口，不新增按钮。
- 执行完成后先保存证据快照，再调用初稿模型；初稿失败证据保留。
- 候选表和选表任务级缓存，`needTableReselection` 时失效。
- 同一任务串行执行 + 幂等键。
- 响应增加 `evidenceRound`、`needMoreEvidence`、`needTableReselection`。

## Capabilities

### New Capabilities

- `user-driven-query-plans`: 用户动作驱动的多步证据查询计划。

### Modified Capabilities

- `business-handling-guidance`: 初稿输出增加继续/重建标志，证据先落快照。
- `ems-database-connection-analysis`: 查询计划改为 steps + 参数绑定。

## Impact

- 后端：`EmsDatabaseAssistanceService`、`EmsDatabaseQueryRequest/Plan`、`BusinessModelClient`、`DeepSeekBusinessModelClient`、`BusinessGuidanceSuggestion`、`BusinessModelInteractionService`、执行器/守卫。
- 前端：guidance 响应类型与轻提示、轮次展示。
- 数据：无新表；沿用证据快照轮次。
- 提示词：查询计划、初稿标志、场景 SQL 顺序。
- 测试：多步依赖、0 行跳过、绑定注入、504 证据复用、串行与缓存失效。
