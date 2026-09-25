---
id: 20260925-dev-boot-002
title: "锁定 Java 与 AgentScope 依赖组合"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-BOOT-002
---

# 锁定 Java 与 AgentScope 依赖组合

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-BOOT-002`，用于锁定 Java 与 AgentScope 依赖组合。 方案追踪：主方案第 8、11 节；Harness 附件第 1 节。

## 前置条件

- 依赖：[DEV-BOOT-001](20260925-dev-boot-001-初始化仓库规范与目录.md)。
- 来源基线：[01-P0工程与技术验证.md](../solutions/开发任务基线/01-P0工程与技术验证.md)

## 目标

- [ ] 创建 Maven Wrapper。
- [ ] 以 JDK 17、AgentScope Java 2.0.3 BOM、Spring Boot 4.0.4 建立最小 POM。
- [ ] 加入 `agentscope-harness` 和 DeepSeek 所需的 OpenAI 兼容模型扩展。
- [ ] 输出依赖树。验证 `HarnessAgent`、RuntimeContext、模型扩展、permission、workspace、state store SPI 的实际 API。
- [ ] 交付并核对以下产物：`backend/pom.xml`、Maven Wrapper、`docs/dependency-baseline.md`、最小编译类。

## 验收标准

- 离线 Stub 场景可编译。
- 依赖无未解释版本覆盖。
- 记录框架 API 与技术方案的差异。
- 负向检查：禁止使用 `SNAPSHOT` 或 `main` 分支源码替代 2.0.3 发布物。
- 实现方检查：`./mvnw -q -DskipTests package`、`./mvnw dependency:tree`。
- 人工验证：无。
- 完成证据：JDK/Maven 版本、依赖树、编译日志、API spike 结论。

## 来源与关联

- 来源任务：`DEV-BOOT-002`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-BOOT-002` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
