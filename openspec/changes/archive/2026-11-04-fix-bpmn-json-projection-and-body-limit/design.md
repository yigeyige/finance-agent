## Decisions

### 1. 场景正文上限改为 3

`requiredAttachmentBodies` 固定最多 1 个必读 BPMN；`BusinessGuidanceSuggestion.requestedAttachments` 允许最多 2 个；`mergeBodies` 按文件名去重后最大为 1 + 2 = 3。因此把 `BusinessGuidanceContext` 的上限从 2 改为 3，抽常量：

```java
// 必读附件 1 个 + 模型最多请求 2 个
private static final int MAX_SCENE_ATTACHMENT_BODIES = 3;
```

不采用“截断到 2”，避免静默丢弃模型请求的第二个样例正文，影响节点参数/部门审批流的完整性。

### 2. 投影器按内容而非扩展名分流

`BpmnStructureProjector.project` 先去除 BOM 和首部空白：

- `startsWith("{")` → `projectActivitiModelJson`；
- 否则 → 现有 `projectXml`。

JSON 解析失败时回退 XML；两者都失败时抛 `IllegalStateException`，由调用方回退附件摘要。

### 3. Activiti model JSON 投影映射

- `processName` ← `properties.name`；
- 节点 ← `childShapes`，按 `stencil.id`：
  - `StartNoneEvent`/`StartEvent` → `startEvent`
  - `EndNoneEvent`/`EndEvent` → `endEvent`
  - `UserTask`/`Task` → `userTask`
  - `ExclusiveGateway` → `exclusiveGateway`
  - 其他类型跳过；
- 节点 `id` ← `properties.overrideid`，为空回退 `resourceId`；
- `name` ← `properties.name`；
- `bounds` ← `bounds.upperLeft/lowerRight` 换算 `x,y,w,h`；
- 流转：
  - `target` ← `target.resourceId`，为空回退 `outgoing[0].resourceId`；
  - `source` ← 该导出格式中 `source` 常为 `null`，先遍历节点，把节点 `outgoing` 中的 flow `resourceId` 反推为 source 节点；
  - `condition` ← `properties.conditionsequenceflow`；
  - `resourceId` 映射为节点 `overrideid`。
- 输出截断到 `MAX_OUTPUT`，与 XML 路径一致。

### 4. 规格同步

`business-scene-skill-attachment` 的 “Scene attachment used on demand” 要求改为：

- 模型单轮最多请求 2 个附件正文；
- 合并必读附件后总数至多 3 个；
- `.xml` 必读附件可能是标准 BPMN XML 或 EMS Activiti model JSON，均以结构投影注入。

## Risks

- Activiti model JSON 的 `SequenceFlow.source` 在不同导出中可能为 `null` 或存在；投影器两种都兼容。
- `properties.overrideid` 可能为空，回退 `resourceId` 保证节点 id 不丢。
- 标准 BPMN XML 路径完全复用现有逻辑，回归风险低。
- 3 个正文的输入体积约 54KB，远低于模型预算；`ModelInputBudget` 仍是最终兜底。
