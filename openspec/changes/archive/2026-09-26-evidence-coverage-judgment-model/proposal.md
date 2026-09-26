## Why

任务、范围和步骤模型已经具备，但证据快照、覆盖度、finding、计算见证和人工确认尚未有领域对象与仓储边界。没有这条证据链，确定性结论和专业判断无法被复核、追溯或阻止不完整发布。

## What Changes

- 建立 `fact_snapshot`、`coverage_record`、`finding`、`finding_evidence`、`computation_witness`、`professional_confirmation` 的领域模型。
- 为六张证据域表补充 MyBatis-Plus Entity、Mapper、Repository 接口和显式映射。
- 对 effective class、completeness、judgment mode、finding status、recompute status、confirmation decision 使用受控枚举。
- 增加证据字段裁剪、确定性 finding 必须有 witness、专业 finding 必须有人工确认等领域守卫。
- 增加迁移/schema 契约和仓储映射测试，复用已有 V4/V8 迁移，不重复创建表。

## Capabilities

### New Capabilities

- `evidence-coverage-judgment-model`: 证据快照、覆盖记录、判断结果、计算见证和人工确认的可追溯模型。

### Modified Capabilities

无。

## Impact

- 新增 `evidence` 领域、应用仓储接口和 MyBatis-Plus 基础设施实现。
- 复用现有 `evidence` schema、V4 迁移、V8 审计字段和 `PersistenceUuid`。
- 不新增外部依赖，不改变已有任务/范围/步骤表结构。
