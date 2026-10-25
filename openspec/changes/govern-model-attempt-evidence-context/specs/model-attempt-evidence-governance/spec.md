## ADDED Requirements

### Requirement: Each model action has an independent attempt
系统 SHALL 为每次业务处理初稿、修订或证据分析创建独立且不可变的模型 attempt。attempt MUST 记录任务、单调轮次、动作类型、冻结 Skill 修订、模板版本、状态、失败阶段和非敏感调用元数据；终态记录 MUST NOT 被后续重试覆盖。

#### Scenario: Model action succeeds
- **WHEN** 数据采集、模型调用和结构化响应校验全部成功
- **THEN** 当前 attempt 标记为 `SUCCEEDED`，并与其草稿和已分析证据建立可追溯关联

#### Scenario: Model action fails after data collection
- **WHEN** 查询成功但模型超时、失败或响应校验不通过
- **THEN** 当前 attempt 标记为 `FAILED` 并记录失败阶段，已采集证据保留且不得生成成功草稿

#### Scenario: Failed action is retried
- **WHEN** OPERATOR 重试一个失败的模型动作
- **THEN** 系统创建新的 attempt 并关联原 attempt，不修改原失败记录

### Requirement: Evidence lifecycle distinguishes collection from analysis
系统 SHALL 将原始证据按 attempt 和轮次追加保存。查询成功后的证据 MUST 先标记为 `COLLECTED_UNANALYZED`；只有对应模型输出成功且通过结构校验后才能标记为 `ANALYZED`。相同 schema、table 或 purpose 的后续证据 MUST NOT 覆盖历史证据。

#### Scenario: Query succeeds and model fails
- **WHEN** attempt 已保存查询结果但最终模型调用失败
- **THEN** 原始证据保持 `COLLECTED_UNANALYZED`，默认不具备正式决策包资格

#### Scenario: Repeated query returns changed facts
- **WHEN** 后续轮次对相同 schema、table 和 purpose 采集到不同结果
- **THEN** 系统保留两个证据版本、时间和来源信息，并将差异作为潜在冲突而非覆盖旧值

### Requirement: Evidence retry reuse is deterministic
系统 SHALL 仅在任务范围、冻结 Skill 修订、查询参数摘要、来源版本或水位、证据 digest 和新鲜度策略全部一致时复用既有证据。任一条件变化、缺失或无法验证时 MUST 重新查询并保留旧 attempt 与证据。

#### Scenario: Retry inputs remain unchanged
- **WHEN** 新 attempt 的全部复用条件与原 attempt 一致且证据完整
- **THEN** 系统可引用原 evidence ID，并记录复用来源而不复制或覆盖原证据

#### Scenario: Source watermark is unknown
- **WHEN** 重试时无法验证来源版本或水位是否变化
- **THEN** 系统重新执行只读查询并把结果保存到新 attempt

### Requirement: Evidence manifest covers every candidate record
系统 SHALL 为一次分析建立权威 evidence manifest，并为范围内每条候选证据记录 evidence ID、资格状态、来源与时间、参数摘要、来源版本或水位、行列统计、字段策略、内容 digest、截取状态、冲突组和 unknown。manifest MUST NOT 自行创造事实。

#### Scenario: Candidate evidence is not eligible
- **WHEN** 证据属于失败 attempt 且状态为 `COLLECTED_UNANALYZED`
- **THEN** manifest 仍记录该证据及排除原因，但正式分析输入默认不把它视为已分析事实

#### Scenario: Evidence reference is fabricated
- **WHEN** manifest 或分块摘要引用不存在或 digest 不匹配的 evidence ID
- **THEN** 校验失败且模型分析不得继续
