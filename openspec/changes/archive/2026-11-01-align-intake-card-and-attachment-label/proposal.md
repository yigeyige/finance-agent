## Why

业务说明步骤的附件标签留在左边，与已靠右的选择按钮不一致。左侧卡片又比聊天框矮，形成明显的底部空白。

## What Changes

- 附件标签、选择按钮和说明文字在宽屏布局靠右。
- 业务说明左侧卡片与聊天框等高，底部操作贴近卡片下缘，输入框保持适中高度。
- 创建步骤不启用卡片内部纵向滚动；模型协作继续独立滚动。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `three-step-business-workbench`: 细化业务说明双栏高度及附件标签的对齐方式。

## Impact

只改业务处理创建页局部样式及对应规格，不改任务或模型接口。
