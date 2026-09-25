---
id: 20260925-dev-boot-003
title: "创建 Spring Boot 模块化单体骨架"
status: done
priority: high
owner: "yigeyige99"
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: spring-boot-modular-monolith-skeleton
source_task: DEV-BOOT-003
---

# 创建 Spring Boot 模块化单体骨架

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-BOOT-003`，用于创建 Spring Boot 模块化单体骨架。 方案追踪：主方案第 4、11 节。

## 前置条件

- 依赖：[DEV-BOOT-002](../03.achieved/20260925-dev-boot-002-锁定Java与AgentScope依赖组合.md)。
- 来源基线：[01-P0工程与技术验证.md](../../../solutions/开发任务基线/01-P0工程与技术验证.md)

## 目标

- [x] 使用 `cn.com.crc.ems:finance-agent`、包 `cn.com.crc.ems.finance.agent` 和应用名 `finance-agent`。
- [x] 按 `iam`、`task`、`evidence`、`packet`、`harness`、`source`、`knowledge`、`governance`、`shared` 分包。
- [x] 建立应用层、领域层和基础设施层依赖方向。
- [x] 交付并核对以下产物：可执行 Spring Boot 应用、模块 package-info、架构规则。

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
- OpenSpec 变更：`spring-boot-modular-monolith-skeleton`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-BOOT-003` 拆分为独立 task；状态更新为 `doing`，建立 OpenSpec change `spring-boot-modular-monolith-skeleton`。
- 2026-09-25：新增 `FinanceAgentApplication`、`spring.application.name=finance-agent` 和 Spring Boot repackage 配置，生成可执行 JAR。
- 2026-09-25：为 `iam`、`task`、`evidence`、`packet`、`harness`、`source`、`knowledge`、`governance`、`shared` 建立 application/domain/infrastructure 三层 package-info。
- 2026-09-25：新增 ArchUnit 规则，禁止领域层依赖 Web/JDBC/JPA、基础设施、Harness 和状态实现；`ModuleArchitectureTest` 通过 3 项检查。
- 2026-09-25：使用 Java 17.0.1 和 Maven Wrapper 完成 `mvnw -f backend/pom.xml -Dtest=*ArchitectureTest test` 与 `mvnw -f backend/pom.xml -q -DskipTests package`；可执行 JAR 包含 `FinanceAgentApplication.class` 和 `BOOT-INF`。
- 2026-09-25：OpenSpec 严格校验通过；任务状态更新为 `done`，等待用户确认归档。
- 2026-09-25：用户确认归档；OpenSpec change 已归档至 `openspec/changes/archive/2026-09-25-spring-boot-modular-monolith-skeleton/`，任务卡待移入 `03.achieved/`。
