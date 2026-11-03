## Why

“测试2号出差流程”验证暴露出「审批流程生成-隆地」场景的三类问题：新流程命名混用了附件名称、角色码查询使用了不存在的 `VSI_NAME` 列、角色分档被写成空壳 `审批规则.json`。这些问题都可以通过调整场景提示词解决，不需要在 Java 代码中写死业务映射。

## What Changes

- 重写场景 `investigation_prompt`：
  - 新流程命名以任务标题优先、业务说明兜底，附件名称只作参考。
  - 新流程在 `psc_act_re_model` 查不到时不再作为阻塞待补项。
  - 角色码改为“约定表优先 + 值集兜底”，值集按 `zh-TW` → `zh-CN` 取第一条。
  - act_id 改为“约定表优先 + `psc_act_config` 首条兜底”，`nodes[].id` 直接使用 act_id。
  - 角色分档改为 BPMN 排他网关条件 `${roleCode=='<角色码>'}`，不生成审批规则。
  - 只生成审批矩阵中标记 Y 的节点。
- 重写场景 `process_artifact_prompt`：
  - `approvalRules` 仅在提供审批金额范围时按 EMS 契约输出。
  - `nodeParameters.actId` 与 `departmentApprovals.approvalNode` 使用 act_id，不得使用节点名称或自造 ID。
  - 明确角色分档的网关条件写法。
- 新增 Flyway V52 更新上述两段提示词。

## Impact

- 数据：Flyway V52 `task.business_scene_skill` 更新。
- 行为：模型选表、角色码/act_id 取值和产物结构更稳定；不涉及 Java 代码、接口或库表结构变更。
