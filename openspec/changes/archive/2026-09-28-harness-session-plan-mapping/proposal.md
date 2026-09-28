# Proposal: 实现 Session、Plan 与业务任务映射

## Why

Harness 的 session/plan 状态需要与业务 task、用户、runtime bundle 和确认范围形成明确边界；否则计划重写可能越过业务审批直接执行，或把跨任务状态混在一起。

## What Changes

- 增加带 task/user/runtime bundle/scope 的 Harness session binding 和 session service。
- 将追加的 Harness plan revision 严格映射为一个推荐、最多两个备选的业务步骤提议命令。
- 通过 task/user/session 全边界校验阻断跨任务计划，计划映射仅产生审批输入，不预取数据、不推进任务状态。

## Scope

本变更不修改业务状态机，不自动创建审批卡或工具调用；真实步骤审批继续由既有业务应用服务负责。
