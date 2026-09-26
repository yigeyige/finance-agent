---
id: 20260925-dev-data-001
title: "建立 Flyway 基线和 schema"
status: achieved
priority: high
owner: yigeyige99
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: flyway-schema-baseline
source_task: DEV-DATA-001
---

# 建立 Flyway 基线和 schema

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-DATA-001`，用于建立 Flyway 基线和 schema。 用可重复验证的迁移建立 Agent 自有数据边界。

## 前置条件

- 依赖：[DEV-BOOT-007](20260925-dev-boot-007-验证现有PostgreSQLpgvector连接.md)。
- 来源基线：[02-P0领域模型与数据库.md](../../../solutions/开发任务基线/02-P0领域模型与数据库.md)

## 目标

- [x] 创建 `iam`、`task`、`evidence`、`packet`、`harness`、`knowledge`、`governance` schema。
- [x] 按附件 A 拆分版本迁移。
- [x] 扩展由平台/准备脚本确认，应用迁移不获取超级权限。
- [x] 交付并核对以下产物：Flyway V1 基线、迁移说明、校验查询。

## 验收标准

- 空库迁移成功。
- 重复启动不重复建表。
- schema history 完整。
- 负向检查：应用账号执行 DDL 失败。
- 负向检查：版本低于基线时拒绝继续。
- 实现方检查：Flyway 脚本静态检查、编译。
- 人工验证：用户在现有容器执行迁移和权限负测。
- 完成证据：迁移记录、schema 清单、用户验证结果。

## 来源与关联

- 来源任务：`DEV-DATA-001`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-DATA-001` 拆分为独立 task。
- 2026-09-26：进入开发，建立 OpenSpec change `flyway-schema-baseline`，采用显式 Flyway runner；应用迁移不创建角色、数据库或 pgvector 扩展。
- 2026-09-26：完成 V1–V7 迁移、runner、脚本、文档和静态契约测试；`verify-flyway-migrations.ps1` 通过，`mvn -q -f backend/pom.xml clean test` 通过。未执行真实数据库迁移、权限负测或应用启动，待用户在现有容器中人工验收。
- 2026-09-26：OpenSpec change 已完成待归档；按流程先同步 delta spec，再执行归档。
- 2026-09-26：检查本地工作区相关改动后，确认迁移静态检查、全量测试和 OpenSpec change 校验通过；已将 delta spec 同步至主规格 `openspec/specs/flyway-schema-baseline/spec.md`，并归档 change `2026-09-26-flyway-schema-baseline`。
