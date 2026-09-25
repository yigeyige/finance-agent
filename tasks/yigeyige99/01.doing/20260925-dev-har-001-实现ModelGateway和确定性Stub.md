---
id: 20260925-dev-har-001
title: "实现 ModelGateway 和确定性 Stub"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-001
---

# 实现 ModelGateway 和确定性 Stub

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-001`，用于实现 ModelGateway 和确定性 Stub。

## 前置条件

- 依赖：[DEV-BOOT-006](20260925-dev-boot-006-建立契约目录和生成边界.md)、[DEV-RUN-003](20260925-dev-run-003-实现步骤提议和审批卡.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] 定义 `ModelGateway`、用途、模板版本、AllowedContext、输出 schema、预算和取消。
- [ ] Stub 按输入 fixture 生成稳定步骤提议和草稿，不伪造自然语言智能。
- [ ] 交付并核对以下产物：接口、Stub、模型元数据记录和 schema 校验器。

## 验收标准

- 默认构建/测试不访问网络。
- 非法结构、额外字段和预算超限失败。
- 负向检查：完整 prompt/response 和 reasoning 不进入长期存储或日志。
- 实现方检查：模型契约检查。
- 人工验证：无。
- 完成证据：Stub fixture、schema 结果和网络零调用证明。

## 来源与关联

- 来源任务：`DEV-HAR-001`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-001` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
