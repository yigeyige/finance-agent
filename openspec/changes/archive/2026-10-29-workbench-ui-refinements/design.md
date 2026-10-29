## Context

业务处理页当前结构：顶部 `task-overview-bar` 展示任务标题/内容/状态，卡片内 `el-steps` 展示三步名称，第二步任务信息表单底部有“保存任务信息”按钮，右侧 `WorkbenchChat` 面板标题为“模型协作”。

## Goals / Non-Goals

**Goals:** 顶部用与卡片内一致的三步骤条替代标题/内容摘要并高亮当前步骤；任务信息分区右上角放置紧凑“保存任务”按钮；聊天标题改为“模型聊天”且发送按钮垂直居中。

**Non-Goals:** 不改变三步业务流程、后端接口、数据契约、权限或步骤条（el-steps）的视觉风格。

## Decisions

- 顶部步骤条复用 `el-steps :active="activeStep" simple`，与卡片内原步骤条视觉风格保持一致；同时移除卡片内重复渲染的 `el-steps`。
- 顶部步骤条在三个步骤均渲染，替代原先 `v-if="task"` 条件渲染的任务标题/内容/状态摘要栏。
- “保存任务”按钮移入任务信息分区标题行（`handling-form-panel__heading`），使用 `size="small"`，`modelLoading` 时一并禁用，避免分析中误点。
- 聊天标题 `模型协作` 改为 `模型聊天`，同步更新面板与输入框的 aria-label；`.workbench-chat__composer` 由 `align-items: flex-end` 改为 `center`。

## Risks / Trade-offs

顶部步骤条与原卡片内步骤条风格一致，仅从卡片内移动到顶部摘要栏位置，页面不再重复显示两处步骤条。移除“处理中/未开始”状态标签，后续如需可再补充。

## Migration Plan

纯前端样式与文案调整，无迁移。实施后运行类型检查与构建，交用户启动并手动验收。

## Open Questions

无。
