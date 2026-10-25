## Why

当前多轮模型调用没有稳定复用历轮证据，且查询成功、模型失败时证据状态与分析状态混在一起。证据体量在极端情况下远超模型上下文，因此需要先建立独立 attempt、追加式证据生命周期和有界历史上下文，才能既保全证据又安全地参与后续分析。

## What Changes

- 每次初稿或修订调用创建不可变模型 attempt，记录任务、轮次、冻结 Skill 修订、模板版本、状态与非敏感模型元数据。
- 原始证据按轮追加保存并区分 `COLLECTED_UNANALYZED` 与已分析状态；模型失败不得删除或伪装成成功证据。
- 建立全部证据的权威索引，保留来源、时间、参数、版本或水位、摘要、digest、冲突与 unknown，不再按 schema/table/purpose 覆盖历史。
- 采用确定性裁剪、引用压缩、分块分析和最终汇总的有界上下文策略；预算必须配置化并在超限时安全失败或要求缩小范围。
- 重试仅在任务范围、冻结 Skill、查询参数、来源版本/水位和新鲜度均未变化时复用证据，否则重新查询并保留旧 attempt。

## Capabilities

### New Capabilities

- `model-attempt-evidence-governance`: 定义模型 attempt、证据状态、重试复用、追加留痕及失败语义。

### Modified Capabilities

- `guidance-draft-persistence`: 将草稿留痕绑定到成功 attempt，并使失败 attempt 的已采集证据保持可追溯但不冒充已分析结论。
- `business-model-interaction`: 多轮调用使用任务业务说明、冻结 Skill 和有界历史证据上下文，并返回明确的失败与完整性状态。
- `context-compression`: 将证据索引、分块预算、引用 digest 和 unknown 完整性纳入受控压缩链路。

## Impact

- 依赖 `require-versioned-business-scene-skill` 提供冻结 Skill 修订与模板版本。
- 后续实施将影响模型编排、证据工作区、持久化模型、重试接口、上下文压缩校验及相关测试。
- 不改变正式业务决策包的生成语义；该能力由 `analyze-evidence-businesspacket` 定义。
