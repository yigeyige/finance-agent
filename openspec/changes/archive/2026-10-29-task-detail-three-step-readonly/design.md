## Context

双击任务行进入 `/tasks/:taskId` 的 `TaskDetail.vue`，通过 `getTaskDetail(taskId)` 返回只读扁平数据 `TaskDetail { title, description, status, initiatorUserId, createdAt, conclusion, evidence, decisionPacket }`。新建业务处理 `WorkbenchHome` 使用 `el-steps simple` 三步条与 `workflow-steps-bar`/`guidance-result-panel` 等全局样式。

## Goals / Non-Goals

**Goals:** 详情页复用三步布局与全局样式，隐藏聊天框与操作按钮，仅步骤可切换，按现有只读数据映射各步骤内容。

**Non-Goals:** 不接入模型聊天、不提供编辑或生成操作、不新增后端数据字段或接口、不改变路由与权限。

## Decisions

- 复用 `task-workbench`、`workflow-steps-bar`、`el-steps simple`、`guidance-result-panel`、`handling-form-panel`、`card-heading` 等全局样式，保证与新建业务处理视觉一致。
- 三步内容映射：业务说明=标题/状态/发起人/创建时间/说明；模型协作=检查结论+关联证据；决策包=修订/结论/证据。
- `el-step` 通过 `@click.native` 绑定 `activeStep` 实现步骤切换；`activeStep` 初始为 0。
- 不渲染 `workbench-chat`，也不渲染任何业务操作按钮；仅保留页面标题旁的“返回任务列表”。
- 清理 `TaskDetail` 中未使用的 `businessObject`/`timeRange`/`planStatusLabel` 及对应样式。

## Risks / Trade-offs

详情数据不含处理对象、业务场景 Skill、数据库实例与推荐方案字段，只读步骤仅展示接口已有字段，不做伪造或补齐。若后续需要这些字段再扩展接口与展示。

## Migration Plan

纯前端展示调整，无迁移。实施后运行类型检查与构建，交用户启动并手动验收。

## Open Questions

无。
