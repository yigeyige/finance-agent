---
id: 20260925-dev-har-011
title: "实现受控 Skills"
status: todo
priority: medium
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-011
---

# 实现受控 Skills

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-011`，用于实现受控 Skills。

## 前置条件

- 依赖：[DEV-HAR-003](20260925-dev-har-003-实现Session、Plan与业务任务映射.md)、[DEV-TOOL-001](20260925-dev-tool-001-建立版本化工具目录.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] 建立技能包元数据、DRAFT/TESTED/PUBLISHED/RETIRED 生命周期、内容摘要和工具权限。
- [ ] 提供一个不访问真实数据的合成调查技能。
- [ ] 运行任务冻结技能版本。
- [ ] 交付并核对以下产物：Skill catalog、加载器、示例技能和审批记录。

## 验收标准

- 只加载发布版本。
- 技能不能覆盖状态机、字段策略或工具白名单。
- 负向检查：未发布/退役/摘要篡改/权限超集技能加载失败。
- 实现方检查：技能生命周期契约。
- 人工验证：用户发布/退役示例技能。
- 完成证据：技能版本和负测记录。

## 来源与关联

- 来源任务：`DEV-HAR-011`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-011` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
