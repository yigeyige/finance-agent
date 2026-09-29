## Why

当前合成源、任务状态、证据链和决策包能力分别已有单元或契约测试，但缺少一份可重复执行的核心纵向切片资产，无法在本地按同一组身份、范围、Stub 模型和发布前变化复核端到端闭环。

## What Changes

- 新增版本化的合成纵向切片 fixture，固定本地 operator/viewer、运行版本、任务前提、范围、步骤和发布前重查数据。
- 新增可校验的 PowerShell 验收脚本，默认只做静态 fixture 检查，显式传入执行开关后调用本地 API 并保存响应结果包。
- 新增 PostgreSQL 只读证据查询模板，按 task_id 汇总状态事件、审批、快照、覆盖、finding、witness、确认、草稿、复核、重查、revision、audit、outbox 和 handoff。
- 新增后端契约测试，验证流程顺序、敏感信息排除、Stub 绑定、发布前新增引用导致旧复核失效以及稳定发布视图要求。

## Capabilities

### New Capabilities

- `synthetic-vertical-slice`: 提供不含真实人员和资金数据的核心合成纵向切片 fixture、验收脚本、证据查询和契约测试。

### Modified Capabilities

- 无

## Impact

- 新增 `scripts/fixtures/`、纵向切片 PowerShell/SQL 资产和后端测试资源。
- 不新增运行时依赖、不改变生产 API 契约；显式执行脚本依赖本地已启动的应用和用户自行提供的 PostgreSQL 只读连接。
