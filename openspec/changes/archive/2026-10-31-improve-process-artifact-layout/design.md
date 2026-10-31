## Context

`EmsProcessArtifactConverter` 负责把 `ProcessArtifactPlan` 转成 EMS 可导入的 BPMN JSON。当前 `shape()` 用 `40 + position * 160, 140` 把所有图形放在同一行；网关与任务同尺寸，连线 dockers 固定为 `(50,40)`。场景 Skill 附件已有 4 个文件，但调查阶段只注入摘要，文件生成阶段不注入附件正文。

约束：
- 模型输出 `ProcessArtifactPlan` 不包含坐标；排版必须由后端决定。
- 不能把样例公司、角色、金额、旧流程 ID 带入目标流程。
- 提示词存于数据库，通过 Flyway 更新；已有 V31，新迁移为 V32。
- 新增 ELK 依赖但不调用，避免本 change 引入布局引擎行为。

## Goals / Non-Goals

**Goals:**
- 分层布局，消除节点重叠和网关文字挤压。
- BPMN XML 作为必读附件：调查阶段投影，文件生成阶段原文。
- 100KB 原文上限和明确失败。
- 提示词 SQL 更新，限定样例参考范围。

**Non-Goals:**
- 不启用 ELK 实际布局。
- 不改变 `ProcessArtifactPlan` 的节点/流转契约。
- 不让模型输出坐标或 BPMN 原文。

## Decisions

### 1. 后端分层布局

在 `EmsProcessArtifactConverter` 内新增布局计算：

- 根据 `flows` 构建邻接关系，忽略回边；按最长路径计算节点层级。
- `start` 固定第 0 层，`end` 固定在最大层级 + 1。
- 同层节点按 `processPlan.nodes()` 顺序纵向排列。
- 尺寸：任务 120×60，排他网关 100×80，开始/结束 40×40。
- 水平间距 `LAYER_GAP=220`，垂直间距 `ROW_GAP=120`，整体以 `CENTER_Y=300` 居中。
- 网关分支的目标节点自然落在下一层，通过纵向排列分开。
- 连线：
  - 相邻层正向边：源右中点到目标左中点，水平线；
  - 跨层或回向边：源下/上中点经中间折点绕行，避免穿过节点；
  - `dockers` 按线段边界计算，不再固定 `(50,40)`；
  - 条件标签沿用 `conditionsequenceflow`，位置由 dockers 控制。
- 无法形成 DAG（存在回边）时标记 `NEEDS_REPAIR`，不强行生成错误布局。

### 2. ELK 依赖预留

在 `backend/pom.xml` 增加 `org.eclipse.elk:org.eclipse.elk.core` 和 `org.eclipse.elk.alg.layered`（版本固定），仅作为后续复杂流程布局候选；本 change 不新增任何 ELK 调用。同步更新 `docs/dependency-baseline.md`。

不选 Graphviz：需要本机二进制，部署和 Windows 本地环境成本更高。

### 3. 附件必读机制

- `task.business_scene_skill_attachment` 增加 `required boolean not null default false`。
- `SceneSkillAttachment` 领域对象、Repository、DTO 增加 `required`。
- BPMN XML 附件设为必读（按 `skill_id` + `.xml` 文件名匹配）。
- 调查阶段：
  - `findSceneAttachmentCatalog` 返回 `required` 标记；
  - 后端自动读取必读附件；XML 转换为结构投影后放入 `sceneAttachmentBodies`，不依赖模型 `requestedAttachments`；
  - 非 XML 必读附件按正文注入（受现有大小限制）。
- 文件生成阶段：
  - `ProcessArtifactService` 读取必读 XML 原文，注入 `ProcessArtifactModelContext`；
  - 超过 100KB 抛中文错误，不静默截断。

### 4. BPMN 结构投影

新增 `BpmnStructureProjector`：

- 使用 JDK DOM，禁用 DTD 和外部实体。
- 提取 `startEvent/endEvent/userTask/exclusiveGateway`、`sequenceFlow`（sourceRef/targetRef/conditionExpression）、`BPMNShape/BPMNEdge` 的坐标骨架。
- 输出紧凑 JSON：`processName`、`nodes[{id,type,name,x,y,w,h}]`、`flows[{source,target,condition,waypoints}]`。
- 限制节点/流转数量和输出长度；解析失败时返回安全错误，不阻断调查，只注入附件摘要。

投影作为 `sceneAttachmentBodies` 的一项，`fileName` 标记为“BPMN 结构投影”，提示词说明它是结构参考。

### 5. 提示词 SQL（V32）

`V32__process_layout_and_bpmn_reference.sql`：

- 增加必读字段并设置 BPMN XML 必读。
- 更新 `GUIDE_HANDLING`：说明 `sceneAttachmentCatalog.required` 和 BPMN 结构投影用途；限定只参考节点类型/条件/命名/布局骨架。
- 更新场景 `investigation_prompt`：要求结合必读 BPMN 投影理解结构和条件写法，不复制业务值。
- 更新场景 `process_artifact_prompt`：说明 `bpmnReference` 是必读格式/结构参考，不是目标事实；输出仍只包含中间表示 JSON。

### 6. 文件生成上下文

`ProcessArtifactModelContext` 增加 `bpmnReference` 字段：

- 长度上限 100KB；
- `toInputJson` 输出 `bpmnReference`；
- 提示词说明其用途和边界；
- `ModelInputBudget` 继续做最终上下文上限校验。

## Risks / Trade-offs

- [分层布局对复杂分支仍可能交叉] → 先保证节点不重叠；复杂流程后续用 ELK 评估。
- [100KB XML 增加 token 成本] → 只用于文件生成阶段，调查阶段用投影；保留输入预算校验。
- [必读附件缺失导致调查/生成失败] → 调查阶段降级为摘要并记录 warning；文件生成阶段明确报错。
- [ELK 依赖未被使用] → 只加依赖和基线记录，不进入运行路径；后续评估后再接线。
- [提示词迁移覆盖用户手工修改] → 仅更新指定模板和指定 skill，内容以追加/替换边界为主。

## Migration Plan

1. 部署代码与 V32 迁移。
2. 验证附件必读字段和提示词内容。
3. 重跑审批流程，检查布局、调查投影、文件生成原文注入。
4. 回滚：回退代码和 V32；V32 只新增字段/更新模板，可恢复备份内容。

## Open Questions

无。ELK 实际启用留待后续评估。
