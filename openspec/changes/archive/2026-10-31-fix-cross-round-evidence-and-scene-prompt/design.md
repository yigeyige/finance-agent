## Context

`BusinessModelInteractionService.guideBusinessHandling` 每轮都用空证据构造上下文；`EmsDatabaseAssistanceService.collect` 只使用本轮查询证据；`accumulateEvidence` 追加增量；`BusinessDecisionPacketService` 合并所有轮次。第二轮附件调用还丢失了第一轮注入的必读 BPMN 投影。

## Decisions

### 1. 证据继承与快照

- 新增 `latestEvidence(taskId)`：读取任务最新一轮证据。
- 第一轮/后续轮都加载上一轮证据，放入 `BusinessGuidanceContext.evidence`。
- `EmsDatabaseAssistanceService.collect` 内部证据列表以 `context.evidence()` 初始化，使查询规划看到上一轮证据。
- 本轮结束 `mergeDedup(previous, collected)`；有新增证据时保存为完整快照，没有新增则保留上一轮。
- 去重键：schema + table + purpose。

### 2. 历史数据兼容

- `task.business_evidence_accum` 增加 `snapshot boolean not null default false`；新轮次写 true。
- 决策包读取最新一行：`snapshot=true` 直接用；`snapshot=false`（历史增量）合并全部轮次。
- 现有数据不需要回填，旧任务保持原行为直到产生新快照。

### 3. 决策包最新轮次与空证据

- `BusinessDecisionPacketService.generate` 使用 `latestEvidence(taskId)` 的结果。
- 若最终证据为空，抛 `IllegalStateException("尚无调查证据，请先完成调查")`，不调用模型。

### 4. 附件保留

- 第二轮模型调用使用 `mergeBodies(requiredAttachmentBodies(taskId), resolveRequestedBodies(...))`，按 fileName 去重。

### 5. 提示词 SQL

- `V33__evidence_snapshot_and_approval_prompt.sql`：
  - 增加 `snapshot` 字段；
  - 更新场景 `investigation_prompt`：不查审核人/用户/角色主数据；只按需查 `approval_config_role` 值集；查不到用用户代码并列入 missingItems；
  - 可选补充 `GUIDE_HANDLING` 同样的边界说明。

## Risks

- 历史增量与快照混用 → 用 snapshot 标记兼容。
- 上一轮证据过多 → 按 MAX_EVIDENCE 截断，保留最近条目。
- 提示词不足以阻止选表 → 后续再加场景级表过滤。
