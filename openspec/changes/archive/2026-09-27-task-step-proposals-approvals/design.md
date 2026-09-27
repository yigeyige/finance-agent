## Context

`DEV-RUN-002` 已提供任务草稿、前提、不可变范围修订和范围确认状态守卫。现有数据库已经包含 `task.investigation_step` 与 `task.step_approval`，领域对象也包含步骤状态、审批 revision、范围修订、字段策略版本、查询模板版本、调用上限和审批摘要，但尚无应用服务、接口和执行前校验入口。

实现必须复用现有 MyBatis-Plus、审计摘要链、UTC Clock、UUID 和 SHA-256 基础能力，不新增依赖、不修改既有迁移，也不能把 permit 返回给浏览器或模型。

## Goals / Non-Goals

**Goals:**

- 在一次事务中保存一个推荐步骤和零至两个备选步骤，并限制提议必须引用当前确认范围。
- 追加不可变审批 revision，校验目标、来源、范围、字段策略、查询模板、调用上限和有效期。
- 提供只在服务端内部传递的短期 `ExecutionPermit`，并在真正调用前复核任务、步骤、审批、范围、字段策略、模板和调用额度。
- 暴露版本化 REST 契约、最小响应、正反样例和单元/契约验证。

**Non-Goals:**

- 不实现真实源系统工具调用、幂等调用意图、租约、Worker 或模型适配器。
- 不引入 JWT、缓存、消息队列或新的通用安全框架；permit 仅作为内部不可序列化领域值对象。
- 不修改数据库迁移；审批调用次数的持久化扣减留给后续 `DEV-RUN-005`。

## Decisions

1. **步骤提议批次使用现有步骤表**：请求一次提交 1 个推荐和最多 2 个备选，服务校验同一任务内的顺序号、目标非空和推荐数量后追加 `InvestigationStep`。这样不新增计划表，后续计划 revision 可在 Harness 任务中扩展。
2. **审批 revision 由服务端生成**：客户端只提交步骤绑定字段和 `expectedRowVersion`，服务端计算目标摘要、生成 UUID 和下一 revision；数据库唯一键 `(step_id, revision_no)` 防止原位覆盖。相同绑定在相同当前 revision 下返回既有审批，绑定任一字段改变则追加新 revision。
3. **permit 只做内部值对象**：`ExecutionPermit` 保存 task、step、approval、scope、approvalDigest、idempotencyKey 和短期过期时间；`StepExecutionGuard` 每次签发前重新读取/校验审批上下文。permit 不作为 Controller 返回类型、不写入审计 payload、不进入模型输入。
4. **字段扩大以版本绑定拒绝**：调用请求必须带 field policy version 和 query template version，任一版本与审批卡不一致即拒绝；具体字段白名单裁剪由 `DEV-TOOL-003` 负责。
5. **状态边界保持现有状态机**：步骤提议要求任务处于 `READY`，成功保存后推进到 `WAITING_STEP_APPROVAL`；审批创建只改变步骤为 `APPROVED` 并保留任务等待状态，实际执行状态由后续 Worker/调用任务推进，避免本任务绕过运行时状态机。

## Risks / Trade-offs

- [Risk] 未实现调用次数扣减时，permit 只能验证当前额度，不能独立防止并发重复消费 → [Mitigation] 本任务将额度扣减和调用意图绑定放入 `DEV-RUN-005`，本任务只签发短期 permit 并拒绝已耗尽审批。
- [Risk] 现有步骤表没有计划批次字段 → [Mitigation] 先以单次最多三步提议满足 P0 边界，后续 Harness 计划 revision 以步骤集合和摘要扩展。
- [Risk] 过期或撤销状态可能由测试/后续服务直接写入 → [Mitigation] 守卫统一检查 `ACTIVE`、有效期、当前范围和绑定摘要，任何不匹配均在外部调用前失败。

## Migration Plan

无需数据库迁移。发布顺序为：先部署后端应用服务、守卫和契约，再由用户启动本地环境进行人工审批卡和重试验证。回滚时删除新增应用入口即可，既有步骤和审批表结构保持兼容。

## Open Questions

- 后续 `DEV-RUN-005` 需要决定调用意图如何原子扣减 `used_calls`，本任务不预先引入该持久化接口。
