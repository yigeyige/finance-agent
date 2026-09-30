## Context

现有 `SyntheticConfigSourceAdapter` 已提供固定 seed 的只读合成记录；`TaskLeaseService`、`StepExecutionGuard`、`ToolInvocationService`、`RuleEvaluationService` 与证据仓储已分别实现步骤领取、审批约束、调用幂等、确定性判断和持久化。核心纵向脚本尚未调用一个将这些能力编排起来的执行入口，因此审批后的任务不会产生证据。

本变更只服务 `local` / `test` 合成验证。调用者为具备 OPERATOR 角色的测试身份；输入仅为任务路径和幂等键，不能指定来源、SQL、URL、字段或任意场景。

## Goals / Non-Goals

**Goals:**

- 为指定任务的当前已审批步骤提供原子领取和短期租约。
- 固定执行 `SyntheticConfigSourceAdapter` 的已登记模板，基于审批字段策略构造最小快照，并写入覆盖度、确定性 Finding、证据关联和计算见证。
- 使用已有调用意图和审计链保留执行结果；同一幂等键重放终态结果，不重复写入证据。
- 让执行模式脚本在审批后调用此入口并保存响应，后续沿用既有流程。

**Non-Goals:**

- 不实现常驻调度器、并发线程池、真实来源、生产 profile 执行或任意工具调用。
- 不引入数据库迁移或第三方依赖。
- 不改变既有发布、专业确认、决策包或范围复核业务规则。

## Decisions

### 受限 REST 触发而非自动调度

新增仅在 `local` / `test` profile 注册的任务级触发入口；它要求 OPERATOR 身份和 `Idempotency-Key`。选择显式调用是为了让纵向脚本保留每个阶段的请求/响应证据，并避免为单一固定场景启动常驻 Worker。自动轮询调度器已由 `postgres-worker-runtime` 定义，但其线程与停止生命周期超出本变更范围。

### 按任务领取并复用现有租约与审批守卫

扩展 `TaskLeaseService` 的任务定向领取能力，而不是复用“全局下一条”领取方法，以避免脚本执行错领其他本地任务。领取后根据当前 ACTIVE 审批构造既有 `StepExecutionRequest` 并通过 `StepExecutionGuard` 复核。替代方案是由 Controller 直接读取审批和更新状态；该方案会绕过事务和锁语义，予以拒绝。

### 固定合成来源和最小证据投影

Worker 只调用 `SyntheticConfigSourceAdapter` 的已登记模板和固定 `EMP-SYN-001` 场景，将记录投影为审批允许的 `reference.id`、`reference.classification` 字段。快照、覆盖度和 Finding 的摘要由已有 `Sha256Digest`、`CoverageRecordFactory`、`RuleEvaluationService` 生成。替代方案是从请求体接收场景和字段；该方案会扩大输入面并破坏合成-only 边界。

### 幂等优先于重复执行

先使用既有 `ToolInvocationService` 写入调用意图；只有 `EXECUTE` 动作才领取步骤、写证据并完成调用。`REPLAY` 返回既有成功结果，`IN_PROGRESS` 或 `MANUAL_RECONCILIATION` 保持失败关闭。这样在 HTTP 重试或脚本重跑时不会产生重复的 Finding/见证。

### 原子证据提交与租约释放

Worker 用例在短事务中写入完整证据链、完成调用并推进任务/步骤到既有的后续状态；无论成功或失败都在 finally 释放租约。来源读取是固定内存合成数据，不存在不受控网络耗时。失败时调用标记为 FAILED/UNKNOWN，且不伪造完成证据。

## Risks / Trade-offs

- [已存在多任务时误领步骤] → 按 taskId 领取并在行锁下验证当前步骤，而不调用全局领取。
- [HTTP 重试重复写证据] → 复用工具调用幂等记录，只有首次 `EXECUTE` 写入。
- [合成入口被用于真实数据] → Controller 仅在 `local` / `test` 注册，Worker 不接受来源/字段参数且显式校验固定模板、版本与身份。
- [已领取后发生故障] → 调用终态记录失败分类，租约 finally 释放；部分写入由事务回滚，恢复仍由已有租约模型处理。

## Migration Plan

无需数据迁移。部署本地构建后，以 local profile 和测试身份种子启动；如需回滚，移除该 profile 受限入口即可，既有证据和审计记录保持只读留痕。

## Open Questions

无；固定合成场景、local/test 限制和 OPERATOR 触发由本次纵向验证边界确定。
