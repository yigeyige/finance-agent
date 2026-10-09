## Why

业务处理向导已经可生成初稿，但重复说明和重复对象字段干扰管理员聚焦；方案生成后聊天又被锁定，无法用模型持续修改已保存的推荐方案。

## What Changes

- 收敛工作台标题、步骤引导和协作提示，只保留完成业务操作所必需的信息。
- 以单一处理对象字段映射既有前提与范围对象，隐藏内部字段拆分。
- 方案区域仅保留推荐方案，并与模型聊天合并；模型修订可更新尚未执行的已生成方案。
- 扩展技术明细展示安全模型元数据，并仅在服务端提供时显示推理强度。
- 将业务场景 Skill 从静态入口升级为管理员可维护的列表，支持新增、编辑和删除。

## Capabilities

### New Capabilities

- `business-plan-revision`: 管理员在步骤尚未执行时更新已生成的推荐业务方案。

### Modified Capabilities

- `task-intake-scope-ui`: 两步向导使用单一处理对象并收敛重复引导。
- `business-handling-guidance`: 业务处理草稿在界面中只提供推荐方案，并支持方案协作修订。
- `business-model-interaction`: 模型调用元数据在技术明细中安全展示。
- `plan-approval-progress-ui`: 推荐方案区域与模型聊天协作合并，生成后允许修订。
- `business-handling-guidance`: 业务场景 Skill 目录支持受控维护，供管理员管理可复用的处理场景。

## Impact

- 前端工作台、模型 API 类型、步骤提议客户端和业务场景 Skill 管理页。
- 步骤提议与业务场景 Skill 的应用服务、Repository、REST 控制器、数据库迁移及其测试；不新增依赖，不改变执行中的步骤。
