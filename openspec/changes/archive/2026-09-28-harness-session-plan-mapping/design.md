# Design: Session、Plan 与业务任务映射

`HarnessSessionBinding` 携带五个必需 UUID：session、task、user、runtime bundle 和 scope revision。`HarnessSessionService` 只允许在完整 task/user/session 边界下创建 session、追加 plan revision，并使用仓储查询计算修订号。

`HarnessPlanMapper` 严格解析 plan JSON 的 `steps` 数组，每项只能有 `goal` 和 `recommended`，总数 1-3 且恰好一个推荐项。输出既有 `ProposeStepsCommand`，不会调用步骤执行、工具网关或数据源；后续业务服务负责审批和状态推进。
