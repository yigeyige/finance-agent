## Why

业务处理向导被缓存后，重复点击新建入口不会清除上一份业务内容，容易误在旧任务上继续操作。两个管理页的新增弹窗也没有融入当前工作台样式，影响表单操作的一致性。

## What Changes

- 为“新建业务处理”入口增加显式的新草稿信号，使缓存的向导重置为第一步的默认表单。
- 让任务列表的新建入口复用相同的新草稿行为。
- 统一业务场景 Skill 与大模型配置的新增、编辑弹窗视觉层级和响应式表单布局。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `task-intake-scope-ui`: 新建入口重复使用时的草稿重置行为。
- `business-handling-guidance`: 业务场景 Skill 管理弹窗的工作台表单样式。
- `model-connection-management`: 大模型配置管理弹窗的工作台表单样式。

## Impact

- 影响 `App.vue`、业务处理向导、任务列表、两个管理页组件及共享前端样式。
- 不修改后端接口、数据库、权限、密钥处理或模型调用逻辑。
