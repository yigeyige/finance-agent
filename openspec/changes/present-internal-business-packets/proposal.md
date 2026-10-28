## Why

管理员需要在现有工作台直接查看新生成的决策包，并看到生成后的任务信息只读状态。

## What Changes

- 复用现有工作台和详情展示结论、推荐/备选方案、理由、关联证据和修订号。
- 保留生成中、失败重试、历史查看及继续补证操作。
- 根据已有包状态禁用任务信息编辑，页面契约与后端结果同步。
- 当前只有管理员，权限扩展暂缓，沿用现有鉴权，不新增角色隔离。
- 暂不设计冲突/缺口专门展示或其他 packet 发布入口。

## Capabilities

### New Capabilities
- `internal-business-packet-workbench`: 管理员在现有工作台直接生成和查看决策包。

### Modified Capabilities
- `business-decision-packet`: 展示新模型结果与已有证据。

## Impact

依赖 `finalize-business-packet-revisions`。修改当前工作台、任务详情与对应类型，使用现有组件样式；仅规划，不启动环境或部署。
