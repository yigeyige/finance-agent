---
id: 20260925-dev-boot-003
title: "创建 Spring Boot 模块化单体骨架"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-BOOT-003
---

# 创建 Spring Boot 模块化单体骨架

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-BOOT-003`，用于创建 Spring Boot 模块化单体骨架。 方案追踪：主方案第 4、11 节。

## 前置条件

- 依赖：[DEV-BOOT-002](20260925-dev-boot-002-锁定Java与AgentScope依赖组合.md)。
- 来源基线：[01-P0工程与技术验证.md](../solutions/开发任务基线/01-P0工程与技术验证.md)

## 目标

- [ ] 使用 `cn.com.crc.ems:finance-agent`、包 `cn.com.crc.ems.finance.agent` 和应用名 `finance-agent`。
- [ ] 按 `iam`、`task`、`evidence`、`packet`、`harness`、`source`、`knowledge`、`governance`、`shared` 分包。
- [ ] 建立应用层、领域层和基础设施层依赖方向。
- [ ] 交付并核对以下产物：可执行 Spring Boot 应用、模块 package-info、架构规则。

## 验收标准

- 单 JAR 可构建。
- 领域模块不依赖 Web/数据库具体实现。
- 发布模块不能直接调用 Harness 内部状态改变任务。
- 负向检查：用 ArchUnit 样例证明跨层反向依赖会失败。
- 实现方检查：`./mvnw -q test -Dtest=*ArchitectureTest`、`./mvnw -q package -DskipTests`。
- 人工验证：用户后续手工启动 JAR。
- 完成证据：模块图、构建日志、ArchUnit 结果。

## 来源与关联

- 来源任务：`DEV-BOOT-003`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-BOOT-003` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
