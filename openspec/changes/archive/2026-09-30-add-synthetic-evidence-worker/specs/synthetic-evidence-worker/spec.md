## ADDED Requirements

### Requirement: Local synthetic evidence worker is approval-bound

系统 SHALL 仅在 `local` 或 `test` profile 暴露一个受 OPERATOR 身份和有效幂等键保护的任务级合成执行入口。该入口 MUST 仅领取指定任务的当前 `APPROVED` 步骤，并复核任务、步骤、范围、ACTIVE 审批、字段策略、查询模板和调用额度后才执行。

#### Scenario: Approved current step executes
- **WHEN** OPERATOR 为当前 `WAITING_STEP_APPROVAL` 任务提交有效幂等键，且当前步骤具有匹配的 ACTIVE 审批
- **THEN** 系统在同一领取事务中创建租约、将步骤和任务推进为运行状态，并仅对该步骤签发执行许可

#### Scenario: Unapproved or mismatched step is rejected
- **WHEN** 任务没有当前已批准步骤，或审批的范围、字段策略、查询模板、调用额度与当前绑定不一致
- **THEN** 系统拒绝执行且不读取来源、不创建证据或重复租约

#### Scenario: Production does not expose synthetic execution
- **WHEN** 应用以非 `local` / `test` profile 启动
- **THEN** 合成 Worker 的 HTTP 入口不可用，且不会访问合成来源

### Requirement: Synthetic worker writes a minimal deterministic evidence chain

系统 SHALL 只使用已登记的固定合成来源模板和合成场景，按审批允许字段投影写入任务、步骤、范围和来源绑定关联的最小事实快照、覆盖记录、确定性 Finding、Finding 证据关联与计算见证，并为执行结果追加审计记录。

#### Scenario: Fixed synthetic records produce evidence
- **WHEN** 已审批的合成步骤成功执行
- **THEN** 系统写入不含真实业务数据的快照和 COMPLETE 覆盖记录，Finding 引用其快照并具有匹配的确定性计算见证

#### Scenario: Request cannot select arbitrary source or fields
- **WHEN** 调用者尝试通过请求指定来源、URL、SQL、字段或非固定合成场景
- **THEN** 接口不接受这些参数，Worker 也不执行任何未登记来源查询

### Requirement: Synthetic execution is traceable and idempotent

系统 SHALL 在执行前记录已有模型定义的工具调用意图，并使用其幂等状态控制证据写入和对外结果。每次成功执行 MUST 留下安全审计事件和已释放的租约。

#### Scenario: Repeated idempotency key replays without duplicate evidence
- **WHEN** 同一任务、步骤和幂等键的合成执行已成功完成后再次请求
- **THEN** 系统返回原执行结果，不新增事实快照、Finding、见证或调用记录

#### Scenario: Execution failure stays explainable
- **WHEN** 写入证据或规则计算发生未预期失败
- **THEN** 事务不留下伪造的完整证据链，调用记录进入受控失败状态，租约被释放且响应不暴露内部细节
