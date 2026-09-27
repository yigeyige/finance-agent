## ADDED Requirements

### Requirement: Bounded step proposal batch

系统 SHALL 通过任务应用服务追加一个推荐步骤和零至两个备选步骤；每个步骤必须绑定同一任务和当前已确认的 scope revision，步骤目标和顺序号不得为空或重复。

#### Scenario: Store one recommendation and two alternatives

- **WHEN** READY 任务提交一个推荐步骤和不超过两个备选步骤
- **THEN** 系统在一个事务中追加步骤提议，推荐步骤标记为 `recommended=true`，任务进入 `WAITING_STEP_APPROVAL`

#### Scenario: Proposal cardinality is rejected

- **WHEN** 请求没有推荐步骤、包含三个以上备选步骤、存在多个推荐或步骤目标为空
- **THEN** 系统在持久化前拒绝请求且不追加任何步骤

#### Scenario: Proposal requires current confirmed scope

- **WHEN** 任务不是 READY 或没有当前 scope revision
- **THEN** 系统拒绝步骤提议，不产生执行副作用

### Requirement: Immutable approval revision binding

系统 SHALL 为批准的步骤追加不可变 approval revision，绑定目标摘要、来源标识、当前 scope revision、字段策略版本、查询模板版本、调用上限和有效期；任何绑定变化都必须生成新的 revision。

#### Scenario: Approval binds all execution boundaries

- **WHEN** 运维人员提交有效步骤、范围、来源、字段策略、查询模板和正数调用上限
- **THEN** 系统保存 `ACTIVE` 审批、递增 revision、approval digest 和审计摘要

#### Scenario: Binding drift creates a new revision

- **WHEN** 同一步骤的来源、范围、字段策略、查询模板、目标摘要或调用上限任一改变
- **THEN** 系统不覆盖历史审批，而是追加新的 approval revision

#### Scenario: Same binding is idempotently reusable

- **WHEN** 同一步骤再次提交与当前活动审批完全相同的绑定且调用额度未耗尽
- **THEN** 系统返回既有审批引用，不新增 revision，并允许后续重新签发内部 permit

### Requirement: Short-lived internal execution permit

系统 SHALL 仅在服务端内部签发短期 `ExecutionPermit`，permit 必须绑定 task、step、approval、scope、approval digest 和幂等键，且不得出现在 REST 响应、浏览器、模型上下文或审计原文中。

#### Scenario: Permit is issued for a valid approval

- **WHEN** 当前任务、步骤、范围和 `ACTIVE` 审批一致，审批未过期且 `usedCalls < maxCalls`
- **THEN** 守卫签发短期 permit，permit 的过期时间不晚于审批过期时间

#### Scenario: Invalid approval context is rejected

- **WHEN** 审批已过期、撤销、stale、属于旧范围、调用次数耗尽、字段策略版本或查询模板版本漂移
- **THEN** 守卫拒绝签发 permit，且不调用外部源

#### Scenario: Requested fields cannot expand approved policy

- **WHEN** 执行请求携带的字段策略版本与审批卡不一致
- **THEN** 守卫拒绝请求，不返回 permit

### Requirement: Proposal and approval REST contract

系统 SHALL 提供版本化的步骤提议和审批接口；写请求必须带显式操作者及幂等键，响应只返回任务、步骤、审批状态和摘要元数据，不返回 permit、模型内容或源系统响应。

#### Scenario: Step and approval endpoints are documented

- **WHEN** 读取 OpenAPI 3.1 契约
- **THEN** 契约包含步骤提议和步骤审批路径，并引用独立、默认拒绝未知字段的 JSON Schema

#### Scenario: Unknown request field is rejected

- **WHEN** 请求 JSON 包含未声明字段
- **THEN** API 返回契约错误且不写入步骤或审批
