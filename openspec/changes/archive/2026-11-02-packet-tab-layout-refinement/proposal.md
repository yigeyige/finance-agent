# Proposal

## Why

业务处理工作台决策包页签（第三步）存在三处布局问题：修订标签位于“返回模型协作”按钮左侧而非标题旁；左侧“外壳卡片 → 业务决策包卡片 → 子卡片”三层嵌套冗余；第三步未纳入视口内滚动，整页滚动导致右侧聊天框随内容滚走、不可见。

## What Changes

- “修订”标签移至“业务决策包”标题右侧并垂直居中。
- “业务决策包”层扁平化：保留外壳卡片，去掉其独立边框/背景，子卡片直接平铺在外壳内。
- 决策包步骤纳入视口内滚动：左侧内容在左侧表单区内部纵向滚动，右侧聊天框保持可见。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `three-step-business-workbench`: 决策包步骤的标题栏修订标签位置、扁平化展示与视口内滚动行为。

## Impact

- 前端 `frontend/src/views/WorkbenchHome.vue`：修订标签 DOM 位置、`--viewport` 条件扩展、`packet-result-panel` 扁平化样式。
- 无后端、API、数据库或依赖变更。
