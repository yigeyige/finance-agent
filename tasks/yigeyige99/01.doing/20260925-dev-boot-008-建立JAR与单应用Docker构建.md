---
id: 20260925-dev-boot-008
title: "建立 JAR 与单应用 Docker 构建"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-BOOT-008
---

# 建立 JAR 与单应用 Docker 构建

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-BOOT-008`，用于建立 JAR 与单应用 Docker 构建。 方案追踪：主方案第 12 节。

## 前置条件

- 依赖：[DEV-BOOT-003](20260925-dev-boot-003-创建SpringBoot模块化单体骨架.md)、[DEV-BOOT-005](20260925-dev-boot-005-建立Vue2工作台工程骨架.md)。
- 来源基线：[01-P0工程与技术验证.md](../solutions/开发任务基线/01-P0工程与技术验证.md)

## 目标

- [ ] 前端构建产物纳入后端静态资源。
- [ ] 建立多阶段 Dockerfile、非 root 用户、只读优先文件系统、工作目录和健康检查。
- [ ] 不提供 Compose，不挂载 Docker socket。
- [ ] 交付并核对以下产物：可执行 JAR、Dockerfile、`.dockerignore`、JAR/Docker 启动说明。

## 验收标准

- 同一版本生成 JAR 和镜像。
- 镜像不包含源码、npm cache、Maven cache、密钥或 `.env`。
- 负向检查：容器以 root 运行或要求 Docker socket 时不通过。
- 实现方检查：JAR/镜像构建；不启动长期运行容器。
- 人工验证：用户分别启动 JAR 和应用镜像并检查页面/健康端点。
- 完成证据：制品摘要、镜像元数据、用户验证结果。

## 来源与关联

- 来源任务：`DEV-BOOT-008`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-BOOT-008` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
