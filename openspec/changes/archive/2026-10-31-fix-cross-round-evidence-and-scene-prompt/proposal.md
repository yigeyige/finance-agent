## Why

当前业务处理每轮分析没有加载上一轮 EMS 证据，第二轮只看到本轮新查询；同时决策包把所有轮次证据合并，导致证据越来越杂。审批流程场景还会查询审核人/角色主数据，但流程生成只需要角色代码、分支条件和值集校验。

## What Changes

- 审批流程场景调查提示词明确：不查询审核人、用户、角色主数据；只按需查询 `approval_config_role` 值集。
- 每轮分析加载上一轮证据，与本轮修改意见和新查询合并后保存为完整证据快照。
- 决策包只使用最新一轮快照；历史增量数据通过快照标记兼容。
- 第二轮附件分析保留必读 BPMN 结构投影，并与模型请求的附件去重合并。
- 决策包在证据为空时明确拒绝，不调用模型。

**Non-goals**
- 不改流程文件布局。
- 不引入审核人主数据的查询能力。
- 不改附件必读策略。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-handling-guidance`: 跨轮证据继承、证据快照、必读附件保留。
- `business-decision-packet`: 决策包使用最新一轮证据快照，并校验证据非空。
- `ems-database-connection-analysis`: 审批流程场景限制审核人主数据查询。

## Impact

- 后端：`BusinessModelInteractionService`、`EmsDatabaseAssistanceService`、`BusinessDecisionPacketService`、`BusinessEvidenceAccum`/Repository。
- 数据：Flyway V33 增加证据快照标记并更新场景提示词。
- 测试：跨轮证据、决策包最新轮次、空证据拒绝、附件保留。
