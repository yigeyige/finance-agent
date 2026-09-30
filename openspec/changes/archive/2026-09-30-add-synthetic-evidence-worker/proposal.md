## Why

核心合成纵向切片在步骤审批后没有执行器产出证据，无法验证既有的决策包、复核、发布、交接和关闭链路。需要一个仅面向本地合成场景的受控 Worker，复用已有审批、租约、范围和证据模型，而不引入真实来源访问。

## What Changes

- 新增可显式触发的本地合成 Worker：只领取已审批的固定合成步骤，执行已登记的 `configuration-drift` 场景。
- Worker 在已有租约、审批、范围和字段策略边界内写入最小事实快照、覆盖记录、确定性 Finding、证据关联和计算见证，并追加审计事件。
- 为运行入口和写入过程定义幂等边界，拒绝非合成、未审批、越范围或重复的执行请求。
- 更新核心合成纵向切片脚本：在审批成功后调用该 Worker，再继续既有证据、决策包和发布链路验证。

## Capabilities

### New Capabilities

- `synthetic-evidence-worker`: 受审批、租约和固定合成场景约束的本地 Worker 执行及证据写入能力。

### Modified Capabilities

- `synthetic-vertical-slice`: 执行模式在步骤审批后触发受控合成 Worker，并保留该阶段响应作为纵向证据。

## Impact

- 影响后端任务 Worker、合成适配器、证据持久化和审计调用链，以及核心纵向 PowerShell 脚本与相关契约/集成测试。
- 不新增第三方依赖、不新增真实来源连接、不读取或写入真实业务数据。
