---
id: 20260925-dev-p1-002
title: "冻结 SourceCatalog 和字段策略"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: design
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-P1-002
---

# 冻结 SourceCatalog 和字段策略

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-P1-002`，用于冻结 SourceCatalog 和字段策略。

## 前置条件

- 依赖：[P1-ENTRY](../开发任务基线_20260925_194829827/08-P1真实源试点_20260925_194829827.md#p1-entry-入口门禁)。
- 来源基线：[08-P1真实源试点.md](../solutions/开发任务基线/08-P1真实源试点.md)

## 目标

- [ ] 逐对象确认 API/只读视图、权威级别、逻辑/物理字段、三类权限、分页、稳定排序、水位、版本、生效和错误语义。
- [ ] 交付并核对以下产物：签署 SourceCatalog、字段矩阵、查询模板和有效/无效样例。

## 验收标准

- 每个确认结论能指向权威来源。
- 未知冲突规则保持 UNKNOWN。
- 完成证据：系统/数据负责人和功能运维签署。

## 来源与关联

- 来源任务：`DEV-P1-002`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-P1-002` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
