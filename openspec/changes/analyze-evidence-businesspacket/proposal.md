## Why

现有 `businesspacket` 只把最新草稿和去重后的证据直接装配，既没有在生成前基于全部历轮有效证据进行独立分析，也会掩盖同一对象的时间变化与冲突。业务决策包必须成为一次可审计的证据分析产物，而不是已有字段的简单拼接。

## What Changes

- **BREAKING**：生成业务决策包前必须对任务的全部有效证据执行索引、分块和最终综合分析；不再按 schema/table/purpose 只保留最新证据作为分析输入。
- 正式包仅纳入满足资格的证据；失败 attempt 下的待分析证据默认排除，并在完整性信息中显式说明。
- 无有效证据时拒绝生成正式包，仅保留“待核查业务分析草稿”语义。
- `businesspacket` 保持独立模型：生成即完成，不进入其他 packet 的复核、发布或交接状态机；每个修订不可变且可追溯。
- 固化输入证据 ID、摘要 digest、冻结 Skill 修订、模板版本、分块分析记录、模型元数据与内容摘要。
- 通过幂等键、事务锁或唯一约束保证并发安全；相同内容不新增修订，内容变化才产生下一修订。
- 只有 OPERATOR 可以生成、查看业务决策包和内部累积证据；VIEWER 请求必须拒绝。

## Capabilities

### New Capabilities

无新增能力；本 change 收紧并扩展现有能力契约。

### Modified Capabilities

- `business-decision-packet`: 从“最新草稿 + 最新去重证据装配”调整为“全部有效证据先分析、再生成不可变完成态修订”。
- `business-model-interaction`: 增加决策包专用的分块证据分析与最终综合调用，并要求可验证的结构化输出。
- `role-permission-guard`: 明确内部证据和业务决策包仅对 OPERATOR 开放，VIEWER 不属于公开发布读取范围。

## Impact

- 依赖 `require-versioned-business-scene-skill` 和 `govern-model-attempt-evidence-context`。
- 后续实施将影响 businesspacket 服务与存储、模型编排、权限守卫、生成/查看接口、前端生成与查看流程以及并发和失败测试。
- 不接入既有 packet 审批发布状态机，不授予 VIEWER 任何新增读取权限。
