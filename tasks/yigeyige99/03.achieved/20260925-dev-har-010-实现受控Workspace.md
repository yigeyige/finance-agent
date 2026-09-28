---
id: 20260925-dev-har-010
title: "实现受控 Workspace"
status: done
priority: medium
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: controlled-workspace
source_task: DEV-HAR-010
---

# 实现受控 Workspace

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-010`，用于实现受控 Workspace。

## 前置条件

- 依赖：[DEV-HAR-002](20260925-dev-har-002-完成HarnessAgent最小冒烟.md)、[DEV-BOOT-004](20260925-dev-boot-004-建立配置profile与启动前校验.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [x] 按 task/session 隔离目录。
- [x] 服务端生成路径。
- [x] 设置配额、允许类型和清理接口。
- [x] 权威证据不以 workspace 为唯一副本。
- [x] 交付并核对以下产物：Workspace manager、路径策略和 local/test 配置。

## 验收标准

- ROOTED 路径、跨任务隔离、配额有效。
- prod 不允许未配置本地回退。
- 负向检查：绝对路径、`..`、符号链接逃逸和跨用户访问。
- 实现方检查：路径安全契约。
- 人工验证：用户检查任务目录隔离。
- 完成证据：路径负测结果。

## 来源与关联

- 来源任务：`DEV-HAR-010`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-010` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-29：完成受控 Workspace 配置、用户/任务/session 隔离路径管理、NIO 实路径与符号链接防护、媒体类型及文件/Workspace 配额、临时文件原子写入、摘要元数据、读取和边界清理；保留 WorkspaceRef 的 objectRef/digest 引用边界，不将临时 Workspace 作为权威证据副本。
- 2026-09-29：新增路径、隔离、符号链接、配额、媒体类型和清理契约测试；定向测试通过，`mvn -q verify` 通过，Surefire 208 tests / 0 failures / 0 errors / 1 skipped；`openspec validate controlled-workspace --strict` 通过。`openspec validate --all --strict` 仍受仓库既有 18 个 spec 基线失败影响。
