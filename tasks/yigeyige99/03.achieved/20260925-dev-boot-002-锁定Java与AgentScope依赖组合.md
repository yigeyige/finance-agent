---
id: 20260925-dev-boot-002
title: "锁定 Java 与 AgentScope 依赖组合"
status: done
priority: high
owner: "yigeyige99"
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

- [x] 创建 Maven Wrapper。
- [x] 以 JDK 17、AgentScope Java 2.0.3 BOM、Spring Boot 4.0.4 建立最小 POM。
- [x] 加入 `agentscope-harness` 和 DeepSeek 所需的 OpenAI 兼容模型扩展。
- [x] 输出依赖树。验证 `HarnessAgent`、RuntimeContext、模型扩展、permission、workspace、state store SPI 的实际 API。
- [x] 交付并核对以下产物：`backend/pom.xml`、Maven Wrapper、`docs/dependency-baseline.md`、最小编译类。

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

- 2026-09-25：从开发任务基线 `DEV-BOOT-002` 拆分为独立 task；补齐 OpenSpec change `finance-agent-development-task-baseline`。
- 2026-09-25：新增官方 Maven Wrapper 3.3.4，Maven 发行版锁定 3.9.11；POM 锁定 Java 17、Spring Boot 4.0.4、AgentScope BOM/Harness/OpenAI 扩展 2.0.3，并将 Spring Framework 对齐到 7.0.7。
- 2026-09-25：新增 `AgentScopeApiProbe`，验证 HarnessAgent、RuntimeContext、OpenAIChatModel、PermissionContextState、WorkspaceManager、AgentStateStore、DistributedStore 的公开 API 类型与接入点。
- 2026-09-25：使用 Temurin JDK 17.0.20.1 与 Maven 3.9.11 完成 `./mvnw -q -DskipTests package`；依赖首次解析后完成离线 `./mvnw -o -q -DskipTests package`；`dependency:tree` 离线输出成功。首次在线解析遇到 TLS 握手失败并通过 Java TLS 1.2/IPv4 参数重试成功，未修改依赖源。
- 2026-09-25：OpenSpec 严格校验通过；任务状态更新为 `done`，等待用户确认后续任务或归档。
- 2026-09-25：用户确认归档；OpenSpec change 已归档至 `openspec/changes/archive/2026-09-25-finance-agent-development-task-baseline/`，本任务待移入 `03.achieved/`。
