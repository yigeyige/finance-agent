# Proposal

## Why

业务处理工作台三步向导中，业务说明步骤仍渲染一个禁用态的模型聊天框，与“尚未开始协作”的阶段不符；补充证据的提示以页面 alert 展示，与聊天上下文割裂；“保存任务”与“生成决策包”两个按钮尺寸不一致，破坏工作台操作区的视觉一致性。

## What Changes

- 业务说明步骤不再渲染右侧模型聊天框，业务表单区占满整行（24 列）。
- “可继续在聊天框发送修改意见补充证据”由页面 alert 改为聊天框输入框的 placeholder 提示。
- “保存任务”与“生成决策包”按钮统一为小号（`size="small"`）。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `three-step-business-workbench`: 业务说明步骤隐藏聊天框并让表单占满整行；生成决策包按钮与保存任务按钮统一为小号。
- `user-driven-query-plans`: 补充证据的继续提示由独立页面提示改为聊天框输入框 placeholder。

## Impact

- 前端 `frontend/src/views/WorkbenchHome.vue`：聊天框条件渲染、主列宽切换、移除补充证据 alert、生成决策包按钮尺寸。
- 前端 `frontend/src/components/WorkbenchChat.vue`：新增可配置的输入框 placeholder 属性。
- 无后端、API、数据库或依赖变更。
