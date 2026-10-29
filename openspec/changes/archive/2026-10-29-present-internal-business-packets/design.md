## Context

当前使用者只有管理员，页面已有业务决策包入口，本切片接入新内容即可。

## Goals / Non-Goals

**Goals:** 直接展示模型生成的包，呈现基础信息只读、补证和重新生成操作。
**Non-Goals:** 不新增权限矩阵、角色隔离、冲突缺口区域、审核或发布流程；不碰 B 的 PacketDraft.vue / PublishedPacket.vue。

## Decisions

- 复用 WorkbenchHome 与现有包展示组件，显示 subjectKey、conclusion、recommendedGoal、reason、alternativeGoals、evidence、场景/实例留痕和修订信息。
- 同步接口类型：去掉无后端来源的 statement，补齐 alternativeGoals、sceneSkillId/Name、databaseConfigId/Name；不增设额外分析分类页面。
- 成功生成或加载已有包后，任务基础信息改为只读；继续调查、补证、生成和历史查看仍可操作。后端锁定规则由切片二提供。
- 保留普通生成中状态和中文失败说明，失败继续显示旧包并允许重试。
- 历史包可能缺少新字段，页面按可用内容显示，不伪造缺失内容。
- 标题/业务说明复用页面已加载的任务信息，不新增接口：WorkbenchHome 用 task.title + form.question，TaskDetail 用 detail.title + detail.description。
- 未选场景/实例时 sceneSkillId/Name、databaseConfigId/Name 显示为空。

## Risks / Trade-offs

历史包可能缺少新分析字段，页面按可用内容显示，不伪造缺失内容。

## Migration Plan

只同步现有接口类型和页面，无权限迁移。实施后进行静态检查、测试与构建，用户负责启动和手动验证。

## Open Questions

无。
