## ADDED Requirements

### Requirement: User-driven multi-step query plans

一次用户动作 SHALL 只执行一个查询计划，MUST NOT 在服务内自动进入下一轮规划。查询计划 SHALL 由 1~5 个步骤组成，支持 `dependsOn` 和参数化 `bindings`；SQL MUST 只使用 `:param` 占位符并通过 JDBC 参数绑定，MUST NOT 拼接绑定值。执行 SHALL 按依赖顺序串行；单步 0 行或失败时 MUST 跳过依赖步骤并继续独立步骤，MUST NOT 自动重试。全部步骤完成后 SHALL 先保存证据快照，再调用初稿模型。

#### Scenario: Dependent process queries
- **WHEN** 用户发起一次分析，计划包含流程定义、节点、流转 4 个步骤
- **THEN** 系统按依赖顺序执行，节点/流转使用流程定义返回的 procdef_id

#### Scenario: No data step
- **WHEN** 某步骤返回 0 行
- **THEN** 记录 noDataTable 和合成证据，跳过依赖步骤，独立步骤继续

#### Scenario: Step failure
- **WHEN** 某步骤 SQL 异常或超时
- **THEN** 记录失败合成证据，跳过依赖步骤，不自动重试

#### Scenario: Injection attempt
- **WHEN** 查询计划尝试拼接绑定值或使用非白名单列
- **THEN** 系统拒绝该步骤

#### Scenario: Draft failure keeps evidence
- **WHEN** 初稿模型返回 504
- **THEN** 本轮证据快照已保存，可在下一次用户动作复用

## ADDED Requirements

### Requirement: Continue and reselection signals

初稿模型 SHALL 输出 `needMoreEvidence` 和 `needTableReselection` 两个布尔字段。响应 SHALL 返回 `evidenceRound`、`needMoreEvidence`、`needTableReselection`。`needTableReselection=true` 时系统 MUST 失效候选/选表缓存并重建。继续分析仅通过聊天输入触发，MUST NOT 新增继续按钮。

#### Scenario: More evidence needed
- **WHEN** 初稿模型返回 needMoreEvidence=true
- **THEN** 响应带轮次和 true，前端显示继续提示

#### Scenario: Reselect tables
- **WHEN** needTableReselection=true
- **THEN** 下一次动作重建候选表并重新选表

#### Scenario: Chat continues analysis
- **WHEN** 用户发送修改意见
- **THEN** 系统执行一轮新查询计划和初稿修订，不并发执行
