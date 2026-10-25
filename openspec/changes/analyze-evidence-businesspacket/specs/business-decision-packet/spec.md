## MODIFIED Requirements

### Requirement: Evidence accumulation across rounds
系统 SHALL 在每次业务处理 attempt 中追加保存当轮产生的关联证据并保留轮次、状态、来源、时间和 digest，不得覆盖历史轮次证据。相同 schema、table 或 purpose 的证据 MUST 作为时间变化或冲突候选共同保留；失败 attempt 的待分析证据 MUST 明确标识且默认不具备正式决策包资格。

#### Scenario: Multi-round evidence accumulates
- **WHEN** 管理员对同一任务连续发起多轮业务处理且各轮产生关联证据
- **THEN** 证据工作区按 attempt 和轮次追加保留全部证据、状态与谱系，历史轮次可追溯

#### Scenario: Repeated evidence conflicts
- **WHEN** 不同轮次对同一 schema、table 和 purpose 产生不一致事实
- **THEN** 系统并列保留证据并在 manifest 中标记潜在冲突，不以最新记录覆盖旧记录

### Requirement: Business decision packet generation
系统 SHALL 允许 OPERATOR 为任务生成业务决策包。生成流程 MUST 固定覆盖全部候选证据的 manifest，以全部 eligible 证据执行受控分块分析和最终综合，并在分析成功后创建状态为 `COMPLETED` 的不可变 `businesspacket` 修订。修订 MUST 固化输入 evidence ID、排除原因、摘要 digest、冻结 Skill 修订、模板版本、分块分析记录、非敏感模型元数据与内容 digest；相同内容不得新增修订，并发生成不得产生重复修订号。

#### Scenario: Generate packet from all eligible evidence
- **WHEN** OPERATOR 点击生成且任务至少存在一条可验证的 eligible 证据
- **THEN** 系统对 manifest 覆盖的全部 eligible 证据完成分块与最终分析，并生成一个立即完成的不可变修订

#### Scenario: Content unchanged skips new revision
- **WHEN** 再次生成所得规范化输入谱系与业务结果 digest 和上一修订一致
- **THEN** 系统返回既有修订并提示无变化，不新增 revision

#### Scenario: Content changes create a revision
- **WHEN** eligible 证据、冻结 Skill、模板版本或规范化业务结果发生变化
- **THEN** 系统原子创建下一不可变 revision 并保留旧版

#### Scenario: Concurrent generation races
- **WHEN** 同一任务的两个请求并发生成相同或不同内容
- **THEN** 系统通过幂等约束和原子 revision 分配避免重复 revision 与 `max+1` 竞争

#### Scenario: No eligible evidence rejects generation
- **WHEN** 任务没有任何 eligible 证据
- **THEN** 系统拒绝生成正式决策包，并将现有业务内容维持为“待核查业务分析草稿”

#### Scenario: Analysis fails before persistence
- **WHEN** 任一分块、引用校验或最终综合失败
- **THEN** 系统不创建完成态决策包，保留失败 attempt 与中间谱系供重试

### Requirement: Business decision packet view
系统 SHALL 为 OPERATOR 提供业务决策包的只读查看，展示决策包标识、任务标识、完成状态、修订号、业务分析结论、风险与建议、证据引用和完整性信息，且 MUST NOT 暴露模型推理过程、原始提示词、连接凭据或未授权原始字段。VIEWER MUST NOT 读取 businesspacket、内部 evidence manifest、分块分析或累积证据。

#### Scenario: Operator views a generated packet
- **WHEN** OPERATOR 打开已生成的业务决策包
- **THEN** 页面展示完成态修订及可追溯证据引用，且不含推理过程、提示词、凭据或未授权字段

#### Scenario: Viewer requests a business packet
- **WHEN** VIEWER 直接调用 businesspacket 或内部证据读取接口
- **THEN** 服务端返回 403 且不返回包摘要、证据计数或其他内部信息
