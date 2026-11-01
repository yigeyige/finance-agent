## Why

当前业务说明卡片虽与聊天框等高，却将多余高度集中在业务场景和附件材料之间，视觉上形成大片空白。用户截图确认这块空白明显破坏表单阅读节奏，需要在保留等高与无内部滚动的前提下调整布局。

## What Changes

- 业务说明输入框适度增高。
- 宽屏表单把剩余高度均匀分配到字段间，避免单一大空白。
- 保持卡片等高、附件与提交操作靠右、业务说明卡片无独立滚动。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `three-step-business-workbench`: 明确业务说明字段的均衡间距。

## Impact

仅修改 `WorkbenchHome.vue` 的业务说明表单样式及对应规格。
