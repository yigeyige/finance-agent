---
id: 20260925-dev-qa-009
title: "验证 JAR 和 Docker 部署"
status: done
priority: medium
owner: ""
created_at: 2026-09-25
type: test
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-QA-009
---

# 验证 JAR 和 Docker 部署

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-QA-009`，用于验证 JAR 和 Docker 部署。

## 前置条件

- 依赖：[DEV-BOOT-008](20260925-dev-boot-008-建立JAR与单应用Docker构建.md)、[DEV-QA-002](20260925-dev-qa-002-贯通核心合成纵向切片.md)。
- 来源基线：[07-P0测试与验收.md](../solutions/开发任务基线/07-P0测试与验收.md)

## 目标

- [x] 提供外部 PostgreSQL 环境变量、端口、工作目录、health/readiness 和停止流程。
- [x] Docker 模式确认不依赖 Docker socket。
- [x] 不提供 Compose。
- [x] 交付并核对以下产物：部署检查表。

## 验收标准

- 同一版本 JAR/镜像均可连接外部数据库、提供页面和恢复任务。
- 实现方检查：制品构建和镜像静态检查。
- 人工验证：用户分别启动并验证。
- 完成证据：制品摘要、用户启动结果。

## 来源与关联

- 来源任务：`DEV-QA-009`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-QA-009` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-29：完成 `jar-docker-deployment-gate` OpenSpec 变更，新增部署静态检查脚本、`docs/deployment-checklist.md` 和 `JarDockerDeploymentGateContractTest`。
- 2026-09-29：验证通过：`mvn -q '-Dtest=JarDockerDeploymentGateContractTest,DockerSandboxCapabilityTest' test`、`mvn -q -DskipTests compile`、`python scripts/verify-contracts.py`、`pwsh -NoProfile -File scripts/qa-deployment-artifacts.ps1`、`openspec validate jar-docker-deployment-gate --strict`；未启动容器或外部数据库。
