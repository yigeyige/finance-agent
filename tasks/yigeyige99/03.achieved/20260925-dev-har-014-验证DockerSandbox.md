---
id: 20260925-dev-har-014
title: "验证 Docker Sandbox"
status: done
priority: medium
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: docker-sandbox-capability
source_task: DEV-HAR-014
---

# 验证 Docker Sandbox

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-014`，用于验证 Docker Sandbox。

## 前置条件

- 依赖：[DEV-HAR-010](20260925-dev-har-010-实现受控Workspace.md)、[DEV-BOOT-008](20260925-dev-boot-008-建立JAR与单应用Docker构建.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [x] 仅在 JAR/local 测试 profile 使用 `DockerFilesystemSpec`。
- [x] 合成/非敏感输入。
- [x] 限制网络、环境变量、挂载、CPU/内存/时间。
- [x] 应用镜像 profile 禁用 Docker socket 方式并给出明确状态。
- [x] 交付并核对以下产物：Sandbox 配置、合成计算工具、能力探针和安全说明。

## 验收标准

- JAR 模式可运行合成辅助计算。
- 应用镜像不要求挂载 Docker socket。
- 负向检查：访问宿主路径、凭证、Agent PG、生产网络和发布 API 失败。
- 实现方检查：配置/编译；实际 Sandbox 安全测试由用户执行。
- 人工验证：用户在本机 Docker 执行 Sandbox 正反用例。
- 完成证据：容器配置和用户结果。

## 来源与关联

- 来源任务：`DEV-HAR-014`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-014` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-29：完成 JAR/local 与 application-container 两种 `DockerFilesystemSpec` 合同、网络/环境/挂载/CPU/内存/时间限制、无 Docker socket 的应用容器能力探针和确定性合成计算；未启动 Docker，实际 Sandbox 正反用例保留给用户手工验证。
- 2026-09-29：新增 profile、能力状态、资源边界、合成计算及 Dockerfile/socket/Compose 静态契约测试；定向测试通过。
