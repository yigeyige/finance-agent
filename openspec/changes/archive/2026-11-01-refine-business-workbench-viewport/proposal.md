## Why

创建页左侧表单与按视口填高的聊天框高度不一致，产生明显留白。模型协作内容变长又撑高整页，向下滚动后聊天框离开视线。

## What Changes

- 业务说明输入区随可用高度增长，附件及开始分析按钮靠右。
- 宽屏模型协作左侧内容在卡片内部滚动，聊天框保持在视口内。
- 窄屏继续单列自然滚动。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `three-step-business-workbench`: 增加创建与模型协作步骤的视口布局和操作对齐要求。

## Impact

只影响 `WorkbenchHome.vue` 的布局样式及对应静态测试，不改业务接口、聊天内容和左侧菜单。
