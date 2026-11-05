## Why

管理员目前只能等待调度时间到来，无法在配置或排查清理任务时主动执行一次。增加受控手动入口，可即时触发相同清理逻辑并查看结果。

## What Changes

- 在“保存”按钮右侧增加“立即执行一次”，操作前确认。
- 新增仅平台管理员可调用的固定任务触发 API，后台异步执行，防止重复提交。
- 页面反馈已提交或失败，并更新最新一次调度日志。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `scheduled-retention-cleanup`: 允许管理员手动触发一次清理，沿用现有权限、执行互斥和日志。

## Impact

影响清理任务调度 Controller/Service、前端调度任务页与 API 客户端、相关测试。无数据库迁移和新依赖；不修改保留期、清理清单或定时配置。
