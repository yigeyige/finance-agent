## Why

当前审批流程文件由 `EmsProcessArtifactConverter` 生成，所有节点固定在同一水平线，网关与任务同尺寸、连线 dockers 固定，导致导入 EMS 后节点重叠、网关文字被压住、分支挤在一起。场景 Skill 已有 BPMN 样例附件，但调查阶段只传附件清单，文件生成阶段完全不传附件正文，样例的节点结构、条件写法和布局骨架没有被使用。

## What Changes

- `EmsProcessArtifactConverter` 改为分层布局：按最长路径分层、同层纵向分布、按节点类型设置尺寸、按源/目标边界计算连线 dockers。
- 引入 Eclipse ELK Java 依赖，供后续复杂流程布局使用；本 change 只加依赖和基线记录，不写调用代码。
- 场景 Skill 附件增加“必读”标记；BPMN XML 样例设为必读。
- 调查阶段自动注入 BPMN 结构投影（节点/流转/条件/坐标骨架），不注入原文；模型不再需要主动请求该附件。
- 文件生成阶段向 `ProcessArtifactModelContext` 注入 BPMN 原文，设置 100KB 上限，超限明确失败。
- 通过 Flyway SQL 更新 `GUIDE_HANDLING`、调查提示词和文件生成提示词，限定样例只参考结构/命名/条件/布局骨架，不复制业务值。

**Non-goals**
- 本 change 不启用 ELK 布局调用；复杂流程效果评估后再决定。
- 不改变流程中间表示的节点/流转契约。
- 不让模型输出 BPMN 坐标或原始 XML。
- 不复制样例中的公司、角色、金额和旧流程标识。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-process-artifact`: 文件生成阶段使用分层布局；注入必读 BPMN 原文（100KB 上限）作为格式/结构参考。
- `business-handling-guidance`: 调查阶段自动注入必读 BPMN 结构投影，不依赖模型主动请求附件正文。
- `business-scene-skill-attachment`: 附件增加必读标记，并支持按场景自动注入。

## Impact

- 后端：`EmsProcessArtifactConverter`、`ProcessArtifactModelContext`、`ProcessArtifactService`、`BusinessModelInteractionService`、`SceneSkillAttachment`/Repository、`BusinessSceneSkillAttachment` 相关 DTO。
- 构建：`backend/pom.xml` 增加 ELK 依赖；`docs/dependency-baseline.md` 记录版本与用途。
- 数据：Flyway V32 增加附件必读字段、设置 BPMN 必读、更新提示词。
- 测试：布局单元测试、结构投影测试、必读附件注入测试、100KB 上限测试。
