---
id: 20260925-task-index
title: "财务共享智能运维 Agent 开发顺序任务索引"
status: index 
owner: ""
created_at: 2026-09-25
type: index
source: development-workplan 
---

# 财务共享智能运维 Agent 开发顺序任务索引

> 共 90 份独立 task：P0 73 项、P1 9 项、P2 8 项。本索引依据任务卡“前置条件”进行拓扑排序；同一依赖层级按照 BOOT、DATA、RUN、TOOL、HAR、UI、QA、P1、P2 排列。

## 使用说明

- 按“开发顺序”从小到大执行；前置任务完成并达到验收标准后，才能启动依赖它的任务。
- 连续任务之间如果没有依赖关系，可以并行开发；“开发顺序”用于确定默认领取次序。
- 文件名列保留完整 Markdown 文件名，点击任务编号或文件名均可打开对应任务卡。
- 完成状态按任务卡 frontmatter 中的 `status` 字段同步：`todo` 显示为“未完成”，`doing` 显示为“进行中”，`done` 或 `completed` 显示为“已完成”。
- P1 必须满足 `P1-ENTRY`，P2 必须满足 `P2-ENTRY`；阶段入口不能只凭前序任务完成自动放行。
- 任务技术基线见[开发任务总览](../solutions/开发任务基线/00-财务共享智能运维Agent-开发任务总览.md)。

## 按开发顺序排列的任务

| 顺序 | 阶段 | 任务 | 标题 | 文件名 | 完成状态 | 直接依赖 |
|---:|---|---|---|---|---|---|
| 001 | P0 工程与技术验证 | [DEV-BOOT-001](../03.achieved/20260925-dev-boot-001-初始化仓库规范与目录.md) | 初始化仓库规范与目录 | [20260925-dev-boot-001-初始化仓库规范与目录.md](../03.achieved/20260925-dev-boot-001-初始化仓库规范与目录.md) | 已完成 | 无 |
| 002 | P0 工程与技术验证 | [DEV-BOOT-002](../03.achieved/20260925-dev-boot-002-锁定Java与AgentScope依赖组合.md) | 锁定 Java 与 AgentScope 依赖组合 | [20260925-dev-boot-002-锁定Java与AgentScope依赖组合.md](../03.achieved/20260925-dev-boot-002-锁定Java与AgentScope依赖组合.md) | 已完成 | `DEV-BOOT-001` |
| 003 | P0 工程与技术验证 | [DEV-BOOT-003](../03.achieved/20260925-dev-boot-003-创建SpringBoot模块化单体骨架.md) | 创建 Spring Boot 模块化单体骨架 | [20260925-dev-boot-003-创建SpringBoot模块化单体骨架.md](../03.achieved/20260925-dev-boot-003-创建SpringBoot模块化单体骨架.md) | 已完成 | `DEV-BOOT-002` |
| 004 | P0 工程与技术验证 | [DEV-BOOT-004](../03.achieved/20260925-dev-boot-004-建立配置profile与启动前校验.md) | 建立配置 profile 与启动前校验 | [20260925-dev-boot-004-建立配置profile与启动前校验.md](../03.achieved/20260925-dev-boot-004-建立配置profile与启动前校验.md) | 已完成 | `DEV-BOOT-003` |
| 005 | P0 工程与技术验证 | [DEV-BOOT-005](../03.achieved/20260925-dev-boot-005-建立Vue2工作台工程骨架.md) | 建立 Vue 2 工作台工程骨架 | [20260925-dev-boot-005-建立Vue2工作台工程骨架.md](../03.achieved/20260925-dev-boot-005-建立Vue2工作台工程骨架.md) | 已完成 | `DEV-BOOT-001` |
| 006 | P0 工程与技术验证 | [DEV-BOOT-006](../03.achieved/20260925-dev-boot-006-建立契约目录和生成边界.md) | 建立契约目录和生成边界 | [20260925-dev-boot-006-建立契约目录和生成边界.md](../03.achieved/20260925-dev-boot-006-建立契约目录和生成边界.md) | 已完成 | `DEV-BOOT-001` |
| 007 | P0 工程与技术验证 | [DEV-BOOT-007](../03.achieved/20260925-dev-boot-007-验证现有PostgreSQLpgvector连接.md) | 验证现有 PostgreSQL/pgvector 连接 | [20260925-dev-boot-007-验证现有PostgreSQLpgvector连接.md](../03.achieved/20260925-dev-boot-007-验证现有PostgreSQLpgvector连接.md) | 已完成 | `DEV-BOOT-004` |
| 008 | P0 工程与技术验证 | [DEV-BOOT-008](../03.achieved/20260925-dev-boot-008-建立前后端分离构建与部署.md) | 建立前后端分离构建与部署 | [20260925-dev-boot-008-建立前后端分离构建与部署.md](../03.achieved/20260925-dev-boot-008-建立前后端分离构建与部署.md) | 已完成 | `DEV-BOOT-003`、`DEV-BOOT-005` |
| 009 | P0 工程与技术验证 | [DEV-BOOT-009](../03.achieved/20260925-dev-boot-009-实现最小诊断能力.md) | 实现最小诊断能力 | [20260925-dev-boot-009-实现最小诊断能力.md](../03.achieved/20260925-dev-boot-009-实现最小诊断能力.md) | 已完成 | `DEV-BOOT-003`、`DEV-BOOT-004` |
| 010 | P0 工程与技术验证 | [DEV-BOOT-010](../03.achieved/20260925-dev-boot-010-定义延后对象存储边界.md) | 定义延后对象存储边界 | [20260925-dev-boot-010-定义延后对象存储边界.md](../03.achieved/20260925-dev-boot-010-定义延后对象存储边界.md) | 已完成 | `DEV-BOOT-003` |
| 011 | P0 领域模型与数据库 | [DEV-DATA-001](../03.achieved/20260925-dev-data-001-建立Flyway基线和schema.md) | 建立 Flyway 基线和 schema | [20260925-dev-data-001-建立Flyway基线和schema.md](../03.achieved/20260925-dev-data-001-建立Flyway基线和schema.md) | 已完成 | `DEV-BOOT-007` |
| 011A | P0 领域模型与数据库 | [CUSTOM-DATA-001](../03.achieved/20260926-补充数据库审计字段.md) | 补充数据库审计字段 | [20260926-补充数据库审计字段.md](../03.achieved/20260926-补充数据库审计字段.md) | 已完成 | `DEV-DATA-001` |
| 012 | P0 领域模型与数据库 | [DEV-DATA-002](../03.achieved/20260925-dev-data-002-建立身份和运行版本模型.md) | 建立身份和运行版本模型 | [20260925-dev-data-002-建立身份和运行版本模型.md](../03.achieved/20260925-dev-data-002-建立身份和运行版本模型.md) | 已完成 | `DEV-DATA-001` |
| 013 | P0 领域模型与数据库 | [DEV-DATA-003](../03.achieved/20260925-dev-data-003-建立任务、范围、前提和步骤模型.md) | 建立任务、范围、前提和步骤模型 | [20260925-dev-data-003-建立任务、范围、前提和步骤模型.md](../03.achieved/20260925-dev-data-003-建立任务、范围、前提和步骤模型.md) | 已完成 | `DEV-DATA-001` |
| 014 | P0 领域模型与数据库 | [DEV-DATA-004](20260925-dev-data-004-建立证据、覆盖度和判断模型.md) | 建立证据、覆盖度和判断模型 | [20260925-dev-data-004-建立证据、覆盖度和判断模型.md](20260925-dev-data-004-建立证据、覆盖度和判断模型.md) | 已完成 | `DEV-DATA-003` |
| 015 | P0 领域模型与数据库 | [DEV-DATA-005](20260925-dev-data-005-建立决策包、复核、发布和交接模型.md) | 建立决策包、复核、发布和交接模型 | [20260925-dev-data-005-建立决策包、复核、发布和交接模型.md](20260925-dev-data-005-建立决策包、复核、发布和交接模型.md) | 已完成 | `DEV-DATA-003`、`DEV-DATA-004` |
| 016 | P0 领域模型与数据库 | [DEV-DATA-006](20260925-dev-data-006-建立Harness状态与checkpoint模型.md) | 建立 Harness 状态与 checkpoint 模型 | [20260925-dev-data-006-建立Harness状态与checkpoint模型.md](20260925-dev-data-006-建立Harness状态与checkpoint模型.md) | 已完成 | `DEV-DATA-003` |
| 017 | P0 领域模型与数据库 | [DEV-DATA-007](20260925-dev-data-007-建立审计和本地outbox.md) | 建立审计和本地 outbox | [20260925-dev-data-007-建立审计和本地outbox.md](20260925-dev-data-007-建立审计和本地outbox.md) | 已完成 | `DEV-DATA-002`、`DEV-DATA-003` |
| 018 | P0 领域模型与数据库 | [DEV-DATA-008](20260925-dev-data-008-实现规范化JSON、UUID、时间和摘要基础库.md) | 实现规范化 JSON、UUID、时间和摘要基础库 | [20260925-dev-data-008-实现规范化JSON、UUID、时间和摘要基础库.md](20260925-dev-data-008-实现规范化JSON、UUID、时间和摘要基础库.md) | 已完成 | `DEV-BOOT-003` |
| 019 | P0 领域模型与数据库 | [DEV-DATA-009](20260925-dev-data-009-实现PostgreSQL仓储事务边界.md) | 实现 PostgreSQL 仓储事务边界 | [20260925-dev-data-009-实现PostgreSQL仓储事务边界.md](20260925-dev-data-009-实现PostgreSQL仓储事务边界.md) | 已完成 | `DEV-DATA-002`、`DEV-DATA-003`、`DEV-DATA-004`、`DEV-DATA-005`、`DEV-DATA-006`、`DEV-DATA-007`、`DEV-DATA-008` |
| 020 | P0 任务状态机与运行时 | [DEV-RUN-001](20260925-dev-run-001-实现权威任务状态机.md) | 实现权威任务状态机 | [20260925-dev-run-001-实现权威任务状态机.md](20260925-dev-run-001-实现权威任务状态机.md) | 已完成 | `DEV-DATA-003`、`DEV-DATA-009` |
| 021 | P0 任务状态机与运行时 | [DEV-RUN-002](20260925-dev-run-002-实现任务发起、前提和范围确认.md) | 实现任务发起、前提和范围确认 | [20260925-dev-run-002-实现任务发起、前提和范围确认.md](20260925-dev-run-002-实现任务发起、前提和范围确认.md) | 已完成 | `DEV-RUN-001`、`DEV-DATA-002` |
| 022 | P0 任务状态机与运行时 | [DEV-RUN-003](../02.done/20260925-dev-run-003-实现步骤提议和审批卡.md) | 实现步骤提议和审批卡 | [20260925-dev-run-003-实现步骤提议和审批卡.md](../02.done/20260925-dev-run-003-实现步骤提议和审批卡.md) | 已完成 | `DEV-RUN-002` |
| 023 | P0 任务状态机与运行时 | [DEV-RUN-004](../02.done/20260925-dev-run-004-实现PostgreSQLWorker和租约.md) | 实现 PostgreSQL Worker 和租约 | [20260925-dev-run-004-实现PostgreSQLWorker和租约.md](../02.done/20260925-dev-run-004-实现PostgreSQLWorker和租约.md) | 已完成 | `DEV-RUN-001`、`DEV-DATA-009` |
| 024 | P0 任务状态机与运行时 | [DEV-RUN-005](../03.achieved/20260925-dev-run-005-实现幂等和调用意图.md) | 实现幂等和调用意图 | [20260925-dev-run-005-实现幂等和调用意图.md](../03.achieved/20260925-dev-run-005-实现幂等和调用意图.md) | 已完成 | `DEV-RUN-003`、`DEV-RUN-004` |
| 025 | P0 任务状态机与运行时 | [DEV-RUN-006](../03.achieved/20260925-dev-run-006-实现业务checkpoint与恢复判定.md) | 实现业务 checkpoint 与恢复判定 | [20260925-dev-run-006-实现业务checkpoint与恢复判定.md](../03.achieved/20260925-dev-run-006-实现业务checkpoint与恢复判定.md) | 已完成 | `DEV-RUN-003`、`DEV-RUN-005`、`DEV-DATA-006` |
| 026 | P0 任务状态机与运行时 | [DEV-RUN-007](../03.achieved/20260925-dev-run-007-实现暂停、预算和取消.md) | 实现暂停、预算和取消 | [20260925-dev-run-007-实现暂停、预算和取消.md](../03.achieved/20260925-dev-run-007-实现暂停、预算和取消.md) | 已完成 | `DEV-RUN-001`、`DEV-RUN-004` |
| 027 | P0 任务状态机与运行时 | [DEV-RUN-008](20260925-dev-run-008-实现公开任务事件和SSE事件源.md) | 实现公开任务事件和 SSE 事件源 | [20260925-dev-run-008-实现公开任务事件和SSE事件源.md](20260925-dev-run-008-实现公开任务事件和SSE事件源.md) | 未完成 | `DEV-RUN-001`、`DEV-RUN-003` |
| 028 | P0 任务状态机与运行时 | [DEV-RUN-009](20260925-dev-run-009-实现审计拦截和错误模型.md) | 实现审计拦截和错误模型 | [20260925-dev-run-009-实现审计拦截和错误模型.md](20260925-dev-run-009-实现审计拦截和错误模型.md) | 未完成 | `DEV-DATA-007`、`DEV-RUN-001` |
| 029 | P0 工具网关与合成适配器 | [DEV-TOOL-001](20260925-dev-tool-001-建立版本化工具目录.md) | 建立版本化工具目录 | [20260925-dev-tool-001-建立版本化工具目录.md](20260925-dev-tool-001-建立版本化工具目录.md) | 未完成 | `DEV-BOOT-006`、`DEV-DATA-002` |
| 030 | P0 工具网关与合成适配器 | [DEV-TOOL-002](20260925-dev-tool-002-实现双层工具调用守卫.md) | 实现双层工具调用守卫 | [20260925-dev-tool-002-实现双层工具调用守卫.md](20260925-dev-tool-002-实现双层工具调用守卫.md) | 未完成 | `DEV-RUN-003`、`DEV-TOOL-001` |
| 031 | P0 工具网关与合成适配器 | [DEV-TOOL-003](20260925-dev-tool-003-实现三类字段策略与可信裁剪.md) | 实现三类字段策略与可信裁剪 | [20260925-dev-tool-003-实现三类字段策略与可信裁剪.md](20260925-dev-tool-003-实现三类字段策略与可信裁剪.md) | 未完成 | `DEV-TOOL-002`、`DEV-DATA-004` |
| 032 | P0 工具网关与合成适配器 | [DEV-TOOL-004](20260925-dev-tool-004-实现合成财务配置源.md) | 实现合成财务配置源 | [20260925-dev-tool-004-实现合成财务配置源.md](20260925-dev-tool-004-实现合成财务配置源.md) | 未完成 | `DEV-TOOL-003` |
| 033 | P0 工具网关与合成适配器 | [DEV-TOOL-005](20260925-dev-tool-005-实现分页、枚举和覆盖度验证.md) | 实现分页、枚举和覆盖度验证 | [20260925-dev-tool-005-实现分页、枚举和覆盖度验证.md](20260925-dev-tool-005-实现分页、枚举和覆盖度验证.md) | 未完成 | `DEV-TOOL-004`、`DEV-DATA-004` |
| 034 | P0 工具网关与合成适配器 | [DEV-TOOL-006](20260925-dev-tool-006-实现生效分类和来源冲突处理.md) | 实现生效分类和来源冲突处理 | [20260925-dev-tool-006-实现生效分类和来源冲突处理.md](20260925-dev-tool-006-实现生效分类和来源冲突处理.md) | 未完成 | `DEV-TOOL-004`、`DEV-DATA-008` |
| 035 | P0 工具网关与合成适配器 | [DEV-TOOL-007](20260925-dev-tool-007-实现确定性规则和计算见证.md) | 实现确定性规则和计算见证 | [20260925-dev-tool-007-实现确定性规则和计算见证.md](20260925-dev-tool-007-实现确定性规则和计算见证.md) | 未完成 | `DEV-TOOL-005`、`DEV-TOOL-006`、`DEV-DATA-004` |
| 036 | P0 工具网关与合成适配器 | [DEV-TOOL-008](20260925-dev-tool-008-实现发布前全范围重查.md) | 实现发布前全范围重查 | [20260925-dev-tool-008-实现发布前全范围重查.md](20260925-dev-tool-008-实现发布前全范围重查.md) | 未完成 | `DEV-TOOL-005`、`DEV-TOOL-006`、`DEV-TOOL-007` |
| 037 | P0 工具网关与合成适配器 | [DEV-TOOL-009](20260925-dev-tool-009-实现源适配器SPI和真实接入占位.md) | 实现源适配器 SPI 和真实接入占位 | [20260925-dev-tool-009-实现源适配器SPI和真实接入占位.md](20260925-dev-tool-009-实现源适配器SPI和真实接入占位.md) | 未完成 | `DEV-TOOL-004` |
| 038 | P0 Harness 与模型 | [DEV-HAR-001](20260925-dev-har-001-实现ModelGateway和确定性Stub.md) | 实现 ModelGateway 和确定性 Stub | [20260925-dev-har-001-实现ModelGateway和确定性Stub.md](20260925-dev-har-001-实现ModelGateway和确定性Stub.md) | 未完成 | `DEV-BOOT-006`、`DEV-RUN-003` |
| 039 | P0 Harness 与模型 | [DEV-HAR-002](20260925-dev-har-002-完成HarnessAgent最小冒烟.md) | 完成 HarnessAgent 最小冒烟 | [20260925-dev-har-002-完成HarnessAgent最小冒烟.md](20260925-dev-har-002-完成HarnessAgent最小冒烟.md) | 未完成 | `DEV-BOOT-002`、`DEV-HAR-001` |
| 040 | P0 Harness 与模型 | [DEV-HAR-003](20260925-dev-har-003-实现Session、Plan与业务任务映射.md) | 实现 Session、Plan 与业务任务映射 | [20260925-dev-har-003-实现Session、Plan与业务任务映射.md](20260925-dev-har-003-实现Session、Plan与业务任务映射.md) | 未完成 | `DEV-HAR-002`、`DEV-RUN-002`、`DEV-DATA-006` |
| 041 | P0 Harness 与模型 | [DEV-HAR-004](20260925-dev-har-004-集成PermissionHITL与工具网关.md) | 集成 Permission/HITL 与工具网关 | [20260925-dev-har-004-集成PermissionHITL与工具网关.md](20260925-dev-har-004-集成PermissionHITL与工具网关.md) | 未完成 | `DEV-HAR-003`、`DEV-TOOL-002` |
| 042 | P0 Harness 与模型 | [DEV-HAR-005](20260925-dev-har-005-实现PostgreSQLHarness状态适配.md) | 实现 PostgreSQL Harness 状态适配 | [20260925-dev-har-005-实现PostgreSQLHarness状态适配.md](20260925-dev-har-005-实现PostgreSQLHarness状态适配.md) | 未完成 | `DEV-HAR-002`、`DEV-DATA-006` |
| 043 | P0 Harness 与模型 | [DEV-HAR-006](20260925-dev-har-006-实现AllowedContext和提示模板版本.md) | 实现 AllowedContext 和提示模板版本 | [20260925-dev-har-006-实现AllowedContext和提示模板版本.md](20260925-dev-har-006-实现AllowedContext和提示模板版本.md) | 未完成 | `DEV-HAR-001`、`DEV-TOOL-003`、`DEV-HAR-003` |
| 044 | P0 Harness 与模型 | [DEV-HAR-007](20260925-dev-har-007-实现DeepSeekOpenAI兼容适配器.md) | 实现 DeepSeek OpenAI 兼容适配器 | [20260925-dev-har-007-实现DeepSeekOpenAI兼容适配器.md](20260925-dev-har-007-实现DeepSeekOpenAI兼容适配器.md) | 未完成 | `DEV-HAR-001`、`DEV-BOOT-004` |
| 045 | P0 Harness 与模型 | [DEV-HAR-008](20260925-dev-har-008-控制Thinking内容生命周期.md) | 控制 Thinking 内容生命周期 | [20260925-dev-har-008-控制Thinking内容生命周期.md](20260925-dev-har-008-控制Thinking内容生命周期.md) | 未完成 | `DEV-HAR-007` |
| 046 | P0 Harness 与模型 | [DEV-HAR-009](20260925-dev-har-009-实现ContextCompression.md) | 实现 Context Compression | [20260925-dev-har-009-实现ContextCompression.md](20260925-dev-har-009-实现ContextCompression.md) | 未完成 | `DEV-HAR-005`、`DEV-HAR-006` |
| 047 | P0 Harness 与模型 | [DEV-HAR-010](20260925-dev-har-010-实现受控Workspace.md) | 实现受控 Workspace | [20260925-dev-har-010-实现受控Workspace.md](20260925-dev-har-010-实现受控Workspace.md) | 未完成 | `DEV-HAR-002`、`DEV-BOOT-004` |
| 048 | P0 Harness 与模型 | [DEV-HAR-011](20260925-dev-har-011-实现受控Skills.md) | 实现受控 Skills | [20260925-dev-har-011-实现受控Skills.md](20260925-dev-har-011-实现受控Skills.md) | 未完成 | `DEV-HAR-003`、`DEV-TOOL-001` |
| 049 | P0 Harness 与模型 | [DEV-HAR-012](20260925-dev-har-012-实现受控Subagents.md) | 实现受控 Subagents | [20260925-dev-har-012-实现受控Subagents.md](20260925-dev-har-012-实现受控Subagents.md) | 未完成 | `DEV-HAR-004`、`DEV-HAR-011` |
| 050 | P0 Harness 与模型 | [DEV-HAR-013](20260925-dev-har-013-实现受控Long-termMemory.md) | 实现受控 Long-term Memory | [20260925-dev-har-013-实现受控Long-termMemory.md](20260925-dev-har-013-实现受控Long-termMemory.md) | 未完成 | `DEV-HAR-005` |
| 051 | P0 Harness 与模型 | [DEV-HAR-014](20260925-dev-har-014-验证DockerSandbox.md) | 验证 Docker Sandbox | [20260925-dev-har-014-验证DockerSandbox.md](20260925-dev-har-014-验证DockerSandbox.md) | 未完成 | `DEV-HAR-010`、`DEV-BOOT-008` |
| 052 | P0 工作台与决策包 | [DEV-UI-001](20260925-dev-ui-001-建立API客户端和前端领域类型.md) | 建立 API 客户端和前端领域类型 | [20260925-dev-ui-001-建立API客户端和前端领域类型.md](20260925-dev-ui-001-建立API客户端和前端领域类型.md) | 未完成 | `DEV-BOOT-005`、`DEV-BOOT-006` |
| 053 | P0 工作台与决策包 | [DEV-UI-002](20260925-dev-ui-002-实现P0测试身份和权限导航.md) | 实现 P0 测试身份和权限导航 | [20260925-dev-ui-002-实现P0测试身份和权限导航.md](20260925-dev-ui-002-实现P0测试身份和权限导航.md) | 未完成 | `DEV-DATA-002`、`DEV-BOOT-004`、`DEV-UI-001` |
| 054 | P0 工作台与决策包 | [DEV-UI-003](20260925-dev-ui-003-实现任务发起和范围确认页面.md) | 实现任务发起和范围确认页面 | [20260925-dev-ui-003-实现任务发起和范围确认页面.md](20260925-dev-ui-003-实现任务发起和范围确认页面.md) | 未完成 | `DEV-RUN-002`、`DEV-UI-002` |
| 055 | P0 工作台与决策包 | [DEV-UI-004](20260925-dev-ui-004-实现计划、审批卡和实时进度.md) | 实现计划、审批卡和实时进度 | [20260925-dev-ui-004-实现计划、审批卡和实时进度.md](20260925-dev-ui-004-实现计划、审批卡和实时进度.md) | 未完成 | `DEV-HAR-004`、`DEV-RUN-008`、`DEV-UI-003` |
| 056 | P0 工作台与决策包 | [DEV-UI-005](20260925-dev-ui-005-实现证据、覆盖度和发现页面.md) | 实现证据、覆盖度和发现页面 | [20260925-dev-ui-005-实现证据、覆盖度和发现页面.md](20260925-dev-ui-005-实现证据、覆盖度和发现页面.md) | 未完成 | `DEV-TOOL-007`、`DEV-UI-004` |
| 057 | P0 工作台与决策包 | [DEV-UI-006](20260925-dev-ui-006-实现专业确认页面.md) | 实现专业确认页面 | [20260925-dev-ui-006-实现专业确认页面.md](20260925-dev-ui-006-实现专业确认页面.md) | 未完成 | `DEV-DATA-004`、`DEV-UI-005` |
| 058 | P0 工作台与决策包 | [DEV-UI-007](20260925-dev-ui-007-实现决策包组装和HTML快照.md) | 实现决策包组装和 HTML 快照 | [20260925-dev-ui-007-实现决策包组装和HTML快照.md](20260925-dev-ui-007-实现决策包组装和HTML快照.md) | 未完成 | `DEV-DATA-005`、`DEV-UI-006` |
| 059 | P0 工作台与决策包 | [DEV-UI-008](20260925-dev-ui-008-实现准确草稿复核.md) | 实现准确草稿复核 | [20260925-dev-ui-008-实现准确草稿复核.md](20260925-dev-ui-008-实现准确草稿复核.md) | 未完成 | `DEV-UI-007` |
| 060 | P0 工作台与决策包 | [DEV-UI-009](20260925-dev-ui-009-实现发布前重查和原子发布.md) | 实现发布前重查和原子发布 | [20260925-dev-ui-009-实现发布前重查和原子发布.md](20260925-dev-ui-009-实现发布前重查和原子发布.md) | 未完成 | `DEV-TOOL-008`、`DEV-UI-008`、`DEV-DATA-007` |
| 061 | P0 工作台与决策包 | [DEV-UI-010](20260925-dev-ui-010-实现发布查看、交接和关闭.md) | 实现发布查看、交接和关闭 | [20260925-dev-ui-010-实现发布查看、交接和关闭.md](20260925-dev-ui-010-实现发布查看、交接和关闭.md) | 未完成 | `DEV-UI-009` |
| 062 | P0 工作台与决策包 | [DEV-UI-011](20260925-dev-ui-011-实现任务诊断页面.md) | 实现任务诊断页面 | [20260925-dev-ui-011-实现任务诊断页面.md](20260925-dev-ui-011-实现任务诊断页面.md) | 未完成 | `DEV-BOOT-009`、`DEV-RUN-007` |
| 063 | P0 测试与验收 | [DEV-QA-001](20260925-dev-qa-001-固化合成黄金任务集.md) | 固化合成黄金任务集 | [20260925-dev-qa-001-固化合成黄金任务集.md](20260925-dev-qa-001-固化合成黄金任务集.md) | 未完成 | `DEV-TOOL-004`、`DEV-TOOL-005`、`DEV-TOOL-006`、`DEV-TOOL-007` |
| 064 | P0 测试与验收 | [DEV-QA-002](20260925-dev-qa-002-贯通核心合成纵向切片.md) | 贯通核心合成纵向切片 | [20260925-dev-qa-002-贯通核心合成纵向切片.md](20260925-dev-qa-002-贯通核心合成纵向切片.md) | 未完成 | `DEV-UI-010`、`DEV-HAR-008`、`DEV-QA-001` |
| 065 | P0 测试与验收 | [DEV-QA-003](20260925-dev-qa-003-执行数据边界负向门禁.md) | 执行数据边界负向门禁 | [20260925-dev-qa-003-执行数据边界负向门禁.md](20260925-dev-qa-003-执行数据边界负向门禁.md) | 未完成 | `DEV-TOOL-003`、`DEV-QA-002` |
| 066 | P0 测试与验收 | [DEV-QA-004](20260925-dev-qa-004-执行审批、权限和发布负向门禁.md) | 执行审批、权限和发布负向门禁 | [20260925-dev-qa-004-执行审批、权限和发布负向门禁.md](20260925-dev-qa-004-执行审批、权限和发布负向门禁.md) | 未完成 | `DEV-RUN-003`、`DEV-UI-009`、`DEV-QA-002` |
| 067 | P0 测试与验收 | [DEV-QA-005](20260925-dev-qa-005-执行覆盖度和事实措辞门禁.md) | 执行覆盖度和事实措辞门禁 | [20260925-dev-qa-005-执行覆盖度和事实措辞门禁.md](20260925-dev-qa-005-执行覆盖度和事实措辞门禁.md) | 未完成 | `DEV-TOOL-005`、`DEV-UI-007` |
| 068 | P0 测试与验收 | [DEV-QA-006](20260925-dev-qa-006-执行恢复、幂等和租约测试.md) | 执行恢复、幂等和租约测试 | [20260925-dev-qa-006-执行恢复、幂等和租约测试.md](20260925-dev-qa-006-执行恢复、幂等和租约测试.md) | 未完成 | `DEV-RUN-004`、`DEV-RUN-005`、`DEV-RUN-006`、`DEV-RUN-007`、`DEV-HAR-005` |
| 069 | P0 测试与验收 | [DEV-QA-007](20260925-dev-qa-007-验证完整Harness能力.md) | 验证完整 Harness 能力 | [20260925-dev-qa-007-验证完整Harness能力.md](20260925-dev-qa-007-验证完整Harness能力.md) | 未完成 | `DEV-HAR-009`、`DEV-HAR-010`、`DEV-HAR-011`、`DEV-HAR-012`、`DEV-HAR-013`、`DEV-HAR-014` |
| 070 | P0 测试与验收 | [DEV-QA-008](20260925-dev-qa-008-用户手动验证真实DeepSeek.md) | 用户手动验证真实 DeepSeek | [20260925-dev-qa-008-用户手动验证真实DeepSeek.md](20260925-dev-qa-008-用户手动验证真实DeepSeek.md) | 未完成 | `DEV-HAR-007`、`DEV-HAR-008`、`DEV-QA-002` |
| 071 | P0 测试与验收 | [DEV-QA-009](20260925-dev-qa-009-验证JAR和Docker部署.md) | 验证 JAR 和 Docker 部署 | [20260925-dev-qa-009-验证JAR和Docker部署.md](20260925-dev-qa-009-验证JAR和Docker部署.md) | 未完成 | `DEV-BOOT-008`、`DEV-QA-002` |
| 072 | P0 测试与验收 | [DEV-QA-010](20260925-dev-qa-010-形成P0证据包和门禁结论.md) | 形成 P0 证据包和门禁结论 | [20260925-dev-qa-010-形成P0证据包和门禁结论.md](20260925-dev-qa-010-形成P0证据包和门禁结论.md) | 未完成 | `DEV-QA-001`、`DEV-QA-002`、`DEV-QA-003`、`DEV-QA-004`、`DEV-QA-005`、`DEV-QA-006`、`DEV-QA-007`、`DEV-QA-008`、`DEV-QA-009` |
| 073 | P1 真实源试点 | [DEV-P1-001](20260925-dev-p1-001-冻结统一认证契约.md) | 冻结统一认证契约 | [20260925-dev-p1-001-冻结统一认证契约.md](20260925-dev-p1-001-冻结统一认证契约.md) | 未完成 | `P1-ENTRY` |
| 074 | P1 真实源试点 | [DEV-P1-002](20260925-dev-p1-002-冻结SourceCatalog和字段策略.md) | 冻结 SourceCatalog 和字段策略 | [20260925-dev-p1-002-冻结SourceCatalog和字段策略.md](20260925-dev-p1-002-冻结SourceCatalog和字段策略.md) | 未完成 | `P1-ENTRY` |
| 075 | P1 真实源试点 | [DEV-P1-003](20260925-dev-p1-003-实现统一认证适配器.md) | 实现统一认证适配器 | [20260925-dev-p1-003-实现统一认证适配器.md](20260925-dev-p1-003-实现统一认证适配器.md) | 未完成 | `DEV-P1-001` |
| 076 | P1 真实源试点 | [DEV-P1-004](20260925-dev-p1-004-实现首个真实只读源适配器.md) | 实现首个真实只读源适配器 | [20260925-dev-p1-004-实现首个真实只读源适配器.md](20260925-dev-p1-004-实现首个真实只读源适配器.md) | 未完成 | `DEV-P1-002`、`DEV-TOOL-009` |
| 077 | P1 真实源试点 | [DEV-P1-005](20260925-dev-p1-005-实现交叉印证或单源例外.md) | 实现交叉印证或单源例外 | [20260925-dev-p1-005-实现交叉印证或单源例外.md](20260925-dev-p1-005-实现交叉印证或单源例外.md) | 未完成 | `DEV-P1-004` |
| 078 | P1 真实源试点 | [DEV-P1-006](20260925-dev-p1-006-建立多类型参考样本.md) | 建立多类型参考样本 | [20260925-dev-p1-006-建立多类型参考样本.md](20260925-dev-p1-006-建立多类型参考样本.md) | 未完成 | `DEV-P1-002` |
| 079 | P1 真实源试点 | [DEV-P1-007](20260925-dev-p1-007-执行有限真实试点.md) | 执行有限真实试点 | [20260925-dev-p1-007-执行有限真实试点.md](20260925-dev-p1-007-执行有限真实试点.md) | 未完成 | `DEV-P1-003`、`DEV-P1-004`、`DEV-P1-005`、`DEV-P1-006` |
| 080 | P1 真实源试点 | [DEV-P1-008](20260925-dev-p1-008-量测准确率、性能、成本和人工负担.md) | 量测准确率、性能、成本和人工负担 | [20260925-dev-p1-008-量测准确率、性能、成本和人工负担.md](20260925-dev-p1-008-量测准确率、性能、成本和人工负担.md) | 未完成 | `DEV-P1-007` |
| 081 | P1 真实源试点 | [DEV-P1-009](20260925-dev-p1-009-关闭P1缺陷并形成P2输入.md) | 关闭 P1 缺陷并形成 P2 输入 | [20260925-dev-p1-009-关闭P1缺陷并形成P2输入.md](20260925-dev-p1-009-关闭P1缺陷并形成P2输入.md) | 未完成 | `DEV-P1-008` |
| 082 | P2 生产放行 | [DEV-P2-001](20260925-dev-p2-001-冻结生产runtimebundle.md) | 冻结生产 runtime bundle | [20260925-dev-p2-001-冻结生产runtimebundle.md](20260925-dev-p2-001-冻结生产runtimebundle.md) | 未完成 | `P2-ENTRY` |
| 083 | P2 生产放行 | [DEV-P2-002](20260925-dev-p2-002-验证生产数据库迁移.md) | 验证生产数据库迁移 | [20260925-dev-p2-002-验证生产数据库迁移.md](20260925-dev-p2-002-验证生产数据库迁移.md) | 未完成 | `DEV-P2-001` |
| 084 | P2 生产放行 | [DEV-P2-003](20260925-dev-p2-003-构建生产JAR和应用镜像.md) | 构建生产 JAR 和应用镜像 | [20260925-dev-p2-003-构建生产JAR和应用镜像.md](20260925-dev-p2-003-构建生产JAR和应用镜像.md) | 未完成 | `DEV-P2-001` |
| 085 | P2 生产放行 | [DEV-P2-004](20260925-dev-p2-004-验证生产配置和密钥注入.md) | 验证生产配置和密钥注入 | [20260925-dev-p2-004-验证生产配置和密钥注入.md](20260925-dev-p2-004-验证生产配置和密钥注入.md) | 未完成 | `DEV-P2-001`、`DEV-P2-003` |
| 086 | P2 生产放行 | [DEV-P2-005](20260925-dev-p2-005-执行生产候选全量回归.md) | 执行生产候选全量回归 | [20260925-dev-p2-005-执行生产候选全量回归.md](20260925-dev-p2-005-执行生产候选全量回归.md) | 未完成 | `DEV-P2-002`、`DEV-P2-003`、`DEV-P2-004` |
| 087 | P2 生产放行 | [DEV-P2-006](20260925-dev-p2-006-完成来源、规则和发布签署.md) | 完成来源、规则和发布签署 | [20260925-dev-p2-006-完成来源、规则和发布签署.md](20260925-dev-p2-006-完成来源、规则和发布签署.md) | 未完成 | `DEV-P2-005` |
| 088 | P2 生产放行 | [DEV-P2-007](20260925-dev-p2-007-完成操作与开发移交.md) | 完成操作与开发移交 | [20260925-dev-p2-007-完成操作与开发移交.md](20260925-dev-p2-007-完成操作与开发移交.md) | 未完成 | `DEV-P2-006` |
| 089 | P2 生产放行 | [DEV-P2-008](20260925-dev-p2-008-生产放行与首批有界任务.md) | 生产放行与首批有界任务 | [20260925-dev-p2-008-生产放行与首批有界任务.md](20260925-dev-p2-008-生产放行与首批有界任务.md) | 未完成 | `DEV-P2-007` |
| 090 | P0 专项加固 | [20260926-harden-diagnostics-and-config](../03.achieved/20260926-加固诊断与配置.md) | 加固诊断与配置 | [20260926-加固诊断与配置.md](../03.achieved/20260926-加固诊断与配置.md) | 已完成 | `DEV-BOOT-004`、`DEV-BOOT-009` |
| 091 | P2 自定义需求 | [20260926-add-backend-chinese-comments](../03.achieved/20260926-补充后端中文注释.md) | 补充后端中文注释 | [20260926-补充后端中文注释.md](../03.achieved/20260926-补充后端中文注释.md) | 已完成 | 无 |
| 092 | P2 自定义需求 | [20260926-adjust-backend-comment-rules](../03.achieved/20260926-调整后端注释规则.md) | 调整后端注释规则 | [20260926-调整后端注释规则.md](../03.achieved/20260926-调整后端注释规则.md) | 已完成 | 无 |
| 093 | P2 自定义需求 | [20260926-strict-versioned-json-validation](../03.achieved/20260926-严格解析VersionedJson并补充基础能力复用规范.md) | 严格解析 VersionedJson 并补充基础能力复用规范 | [20260926-严格解析VersionedJson并补充基础能力复用规范.md](../03.achieved/20260926-严格解析VersionedJson并补充基础能力复用规范.md) | 已完成 | 无 |

## 阶段统计

| 阶段 | 数量 |
|---|---:|
| P0 | 73 |
| P1 | 9 |
| P2 | 10 |
| 合计 | 92 |
