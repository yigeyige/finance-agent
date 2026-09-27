## Context

附件 A 定义了从 `DRAFT` 到 `CLOSED` 的任务生命周期，并允许非终态按授权取消，模型不可用、预算耗尽和源不可用进入暂停状态。当前仓储已经支持行锁和乐观版本，但没有集中判断转移合法性，也没有状态事件追加入口。

## Decisions

### 1. 纯领域状态机

- `TaskStateMachine` 不依赖 Spring、Mapper 或数据库，只根据当前状态、目标状态和显式转移原因判断是否合法。
- 正常路径按附件 A 实现；`FAILED_RECOVERABLE` 支持回到 `WAITING_STEP_APPROVAL` 或在原绑定仍有效时回到 `RUNNING_STEP`。
- 三类暂停状态只能按显式恢复目标回到 `WAITING_STEP_APPROVAL`、`RUNNING_STEP` 或 `ANALYZING`，不允许跳过审批。
- `STALE_APPROVAL` 只能回到 `WAITING_STEP_APPROVAL`；`CLOSED`、`CANCELLED` 不再转移。
- 非终态可触发 `CANCELLED`，但取消原因必须非空。

### 2. 应用事务服务

`TaskStateTransitionService` 使用 `@Transactional`：先通过 `findByIdForUpdate` 锁定任务，再执行状态机守卫，构造新任务快照，按当前 `row_version` 更新，最后追加状态事件。任一步失败都不提交数据库副作用。

### 3. 状态事件复用审计链

`TaskStateEvent` 作为应用端口对象，由 `MybatisTaskStateEventRepository` 转换为现有 `AuditEvent`，事件类型固定为 `TASK_STATE_CHANGED`，目标为任务，payload 只包含 from/to/reason/status version 等必要元数据。审计仓储继续负责摘要链完整性。

### 4. 绕过防线

- 任务状态字段只由 `TaskStateTransitionService` 生成更新快照；普通任务仓储保留查询和乐观更新能力，但不提供状态专用快捷方法。
- 架构测试禁止 Controller/Harness 依赖状态机基础设施以外的持久化 Entity；状态服务测试验证仓储更新发生在事件追加之前。

## Verification

- 状态矩阵测试覆盖正常、暂停、恢复、stale、取消、终态和非法转移。
- 服务测试覆盖任务不存在、当前状态漂移、版本冲突、事件追加失败以及成功顺序。
- 编译和全量后端测试通过；用户人工抽查审计状态事件。
