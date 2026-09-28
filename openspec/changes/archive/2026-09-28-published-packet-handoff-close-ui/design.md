# Design

## Backend

- `PacketLifecycleService` 在只读查询中读取任务最新已发布修订和交接记录；查询不到已发布修订时返回不存在。
- 交接和关闭均在事务中锁定任务，校验修订归属和生命周期状态，分别复用 `TaskStateTransitionService` 推进到 `HANDED_OFF`、`CLOSED`。
- 交接只追加 `packet.handoff` 和审计摘要；关闭只更新任务状态、关闭时间并追加审计摘要。两者均不更新 `packet.packet_revision`。
- 控制器对查询允许 OPERATOR/VIEWER，对写操作只允许 OPERATOR，写请求严格拒绝未知字段并要求幂等键。

## Frontend

- 新增发布视图 API 类型、服务和 `/tasks/:taskId/published` 页面。
- 页面同一份数据面向 OPERATOR/VIEWER 展示；仅 OPERATOR 显示交接、关闭表单。
- 展示发布修订摘要、核查时间、来源版本、覆盖摘要、限制说明、HTML 快照及交接状态。

## Negative guards

- VIEWER 不能访问草稿或提交交接/关闭。
- 修订 ID 不属于任务或不是最新已发布修订时拒绝交接。
- 没有责任引用的交接记录不能关闭任务。
- 关闭响应显式返回 `remediationComplete=false`，不把关闭等同整改完成。
