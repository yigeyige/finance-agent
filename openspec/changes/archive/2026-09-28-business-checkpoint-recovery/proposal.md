## Why

当前 Harness 已能保存安全 checkpoint，但任务运行时没有统一的业务安全点记录和恢复判定。进程重启后若不重新核验步骤、审批、范围、运行版本、租约和工具调用状态，可能跳过人工审批、重复产生外部副作用或恢复到过期上下文。

## What Changes

- 增加业务 checkpoint 服务，在审批完成、调用意图、结果裁剪、确定性计算、草稿和发布复核等安全点追加记录。
- 为 checkpoint 固化运行版本绑定，并提供恢复决策器，返回 `RESUME`、`REPLAY_RESULT`、`NEW_APPROVAL_REQUIRED` 或 `MANUAL_RECONCILIATION`。
- 恢复时重新校验 task/user/session、当前步骤、scope、approval、runtime bundle、活动租约和工具调用幂等状态。
- 明确旧审批、旧范围、旧运行版本、活动租约冲突和 `UNKNOWN` 调用状态不得自动恢复。
- 提供可供 Worker/API 复用的应用服务端口和恢复矩阵契约测试，不启动本地运行环境。

## Capabilities

### New Capabilities

### Modified Capabilities

- `harness-state-checkpoint-model`: 增加运行版本绑定、安全点记录和恢复决策约束。

## Impact

- 后端 harness 应用层新增 checkpoint 记录命令、恢复请求/结果和决策服务。
- `harness.checkpoint` 增加 `runtime_bundle_id` 字段及实体映射；不引入新框架或外部依赖。
- 任务、范围、审批、租约和工具调用仓储作为恢复校验端口被编排组合。
- 新增恢复矩阵、状态冲突和运行版本漂移测试。
