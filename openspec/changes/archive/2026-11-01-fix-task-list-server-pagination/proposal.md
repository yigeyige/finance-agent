## Why

用户实测 `pageSize=10` 仍返回全量记录，且 `total=0`。仓储调用依赖分页拦截器的 `selectPage`，项目却未注册该拦截器，致使任务列表的服务端分页契约未落实。

## What Changes

- 任务仓储改为执行带参数绑定的筛选总数查询和 `LIMIT/OFFSET` 有界查询。
- 保留管理员与普通操作员的身份范围、状态和标题筛选、创建时间倒序与稳定次序。
- 增加分页查询映射及仓储行为的静态测试。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `task-list-audit-query`：落实已有服务端分页、准确总数和身份范围要求。

## Impact

修改任务持久层 Mapper、仓储与测试；响应结构、前端、数据库结构及依赖不变。
