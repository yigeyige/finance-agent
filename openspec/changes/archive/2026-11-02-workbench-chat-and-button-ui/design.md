# Design

## Context

业务处理工作台 `WorkbenchHome.vue` 用 `el-row` 两列布局（主列 `span=16`、聊天列 `span=8`），聊天列始终渲染 `workbench-chat`，仅用 `enabled` prop 控制可交互性。补充证据提示当前以 `el-alert` 展示在页面顶部。`WorkbenchChat.vue` 的输入框 placeholder 为固定文案。动机见 proposal.md。

## Goals / Non-Goals

**Goals:**
- 业务说明步骤（`activeStep === 0`）完全不渲染聊天框，主列占满整行。
- 补充证据提示进入聊天框输入框 placeholder，移除页面 alert。
- “保存任务”与“生成决策包”按钮统一为 `size="small"`。

**Non-Goals:**
- 不改变三步流程、模型调用、证据累积或决策包生成逻辑。
- 不调整模型协作步骤与决策包步骤的聊天框展示（仅业务说明步骤隐藏）。
- 不引入新的状态管理或公共组件。

## Decisions

1. **聊天框用 `v-if` 而非 `v-show` 隐藏**
   - 主列 `:span="activeStep === 0 ? 24 : 16"`，聊天列 `v-if="activeStep !== 0"`。
   - 理由：`WorkbenchChat` 的 `fitViewport` 依赖 `getBoundingClientRect()`，`v-show` 的 `display:none` 会得到错误的顶部位置；`v-if` 让业务说明步骤完全卸载组件，第二步首次挂载时重新测量。
   - 备选：`v-show` + 挂载时重算高度——增加测量时机复杂度，收益低。
   - 影响：步骤切换会销毁/重建 `workbench-chat`，但聊天消息、模型摘要由父组件持有，重建无数据丢失。

2. **placeholder 由父组件传入字符串 prop**
   - 给 `WorkbenchChat` 新增 `placeholder` prop（默认保留原文案），输入框绑定 `:placeholder="placeholder"`。
   - 父组件按 `guidance?.needMoreEvidence` 选择文案，传入“可继续在聊天框发送修改意见补充证据”。
   - 理由：组件保持纯展示、无业务字段语义，未来其他页面也可复用；业务判断留在页面层。
   - 备选：传 `needMoreEvidence` 布尔 prop 让组件内部分支——把业务语义耦合进展示组件，不采用。

3. **按钮统一为小号**
   - “生成决策包”按钮补充 `size="small"`；“保存任务”按钮已为小号，保持不变。

## Risks / Trade-offs

- [步骤切换重建聊天组件带来的轻微挂载开销] → 该页面无高频步骤切换，且消息状态在父层，可接受。
- [placeholder 文案较长] → placeholder 在窄屏可能被截断，但仅作提示，不承载关键信息，可接受。
