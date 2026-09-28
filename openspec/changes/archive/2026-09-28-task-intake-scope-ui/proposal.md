## Why

工作台当前只有空页面，无法完成任务草稿、前提来源和不可变范围确认，用户也看不到 row version 与范围修订。现在接入已有任务发起 API，可形成第一个可人工验证的前端纵向切片。

## What Changes

- 增加任务发起、前提来源和范围确认表单及当前任务详情。
- 统一校验系统、对象、时间、路径和三类字段权限，展示 scope revision、row version 和 stale 提示。
- 通过 API 客户端提交幂等写请求，不让页面传递或覆盖操作者身份。

## Capabilities

### New Capabilities

- `task-intake-scope-ui`: 任务创建、前提补充和范围修订确认工作台。

### Modified Capabilities

## Impact

- 影响 `frontend/src/views/WorkbenchHome.vue`、任务 API 服务和前端领域类型。
- 不新增依赖或改变后端领域契约；用户继续负责浏览器人工验证。
