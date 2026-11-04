## Why

“测试3号出差流程”首次分析报 `当前任务信息无法用于模型分析`。诊断日志定位到第二轮上下文构造抛出 `IllegalArgumentException: sceneAttachmentBodies is invalid`：

- 必读 BPMN 固定占 1 个场景正文；
- 模型允许请求 2 个附件正文；
- 合并后最多 3 个，但 `BusinessGuidanceContext` 上限是 2。

同时，EMS 的 `.xml` 样例附件实际是 Activiti model JSON（`childShapes`），`BpmnStructureProjector` 只按 XML 解析，导致 `[Fatal Error] :1:1` 并回退为 54 字符摘要，模型拿不到 BPMN 结构。

## What Changes

- `BusinessGuidanceContext` 的场景正文上限从 2 改为 3，并抽常量说明“1 必读 + 2 请求”。
- `BpmnStructureProjector` 增加内容嗅探：
  - 内容以 `{` 开头 → 按 Activiti model JSON 投影；
  - 否则走现有 BPMN XML 投影。
- Activiti model JSON 投影输出与 XML 投影一致的 `processName/nodes/flows` 骨架；`SequenceFlow.source` 为空时从节点 `outgoing` 反推。
- 更新 `business-scene-skill-attachment` 规格：明确总数上限 3，以及 `.xml` 附件可能是 Activiti model JSON。
- 新增 `BpmnStructureProjectorTest` 与 `BusinessGuidanceContextTest`。

## Impact

- 仅后端行为与规格；不涉及数据库迁移、接口契约或前端改动。
- 标准 BPMN XML 路径保持不变；Activiti model JSON 从“解析失败回退摘要”变为“结构投影”。
