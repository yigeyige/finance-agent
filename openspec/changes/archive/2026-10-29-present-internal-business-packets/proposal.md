## Why

管理员需要在现有工作台直接查看新生成的决策包，并看到生成后的任务信息只读状态。

## What Changes

- 复用现有工作台展示处理对象、结论、推荐/备选方案、理由、场景/实例留痕、关联证据和修订号。
- 同步接口类型：content 契约对齐切片二，去掉无后端来源的 `statement`，补齐 `alternativeGoals`、`sceneSkill/databaseInstance` 的 id+name。
- 保留生成中、失败重试、历史查看及继续补证操作。
- 根据已有包状态禁用任务信息编辑，页面契约与后端结果同步。
- 当前只有管理员，权限扩展暂缓，沿用现有鉴权，不新增角色隔离。

## Capabilities

### New Capabilities
- `internal-business-packet-workbench`: 管理员在现有工作台直接生成和查看决策包。

### Modified Capabilities
- `business-decision-packet`: 展示新模型结果与已有证据。

## Impact

依赖 `finalize-business-packet-revisions`。修改当前工作台与对应类型，使用现有组件样式；不碰 `PacketDraft.vue` / `PublishedPacket.vue`。
