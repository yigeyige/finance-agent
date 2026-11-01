## Why

上一轮将业务说明输入区强制填满视口，造成输入框过高，并让左侧卡片出现用户不需要的滚动条。需要让创建步骤回到自然高度，同时保留模型协作的独立滚动。

## What Changes

- 业务说明输入框使用适中的固定行数，不随视口继续拉伸。
- 视口高度约束和左侧卡片滚动仅用于模型协作步骤。
- 保留附件与开始模型分析按钮右对齐。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `three-step-business-workbench`: 修正业务说明输入高度与滚动边界。

## Impact

仅修改 `WorkbenchHome.vue` 的创建步骤布局和对应主规格，不改业务接口或模型协作逻辑。
