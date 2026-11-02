# Tasks

## 1. 业务说明步骤隐藏聊天框

- [x] 1.1 在 `WorkbenchHome.vue` 将主列 `:span` 改为按步骤切换（`activeStep === 0` 时为 24，否则 16），聊天列加 `v-if="activeStep !== 0"`；验证 `activeStep === 0` 时页面不渲染 `workbench-chat` 且主列占满整行
- [x] 1.2 验证模型协作与决策包步骤聊天框仍正常显示，步骤切换后聊天消息与模型摘要不丢失

## 2. 补充证据提示改为输入框 placeholder

- [x] 2.1 在 `WorkbenchChat.vue` 新增 `placeholder` prop（默认 `'补充信息，或说说希望如何调整方案'`）并绑定到输入框 `:placeholder`；验证未传值时默认文案不变
- [x] 2.2 在 `WorkbenchHome.vue` 移除页面顶部 `guidance.needMoreEvidence` 的 `el-alert`，向聊天组件传入条件 placeholder；验证 `needMoreEvidence=true` 时输入框 placeholder 显示“可继续在聊天框发送修改意见补充证据”

## 3. 按钮统一为小号

- [x] 3.1 给“生成决策包”按钮补充 `size="small"`；验证其与“保存任务”按钮尺寸一致

## 4. 前端验证

- [x] 4.1 运行前端类型检查与生产构建，验证无报错

## Workflow follow-up

- 实现与验证通过后归档该 change 并同步 delta spec 到主规格。
