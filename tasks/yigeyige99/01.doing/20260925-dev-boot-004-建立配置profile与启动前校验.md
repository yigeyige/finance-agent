---
id: 20260925-dev-boot-004
title: "建立配置 profile 与启动前校验"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-BOOT-004
---

# 建立配置 profile 与启动前校验

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-BOOT-004`，用于建立配置 profile 与启动前校验。 方案追踪：主方案第 10、11 节。

## 前置条件

- 依赖：[DEV-BOOT-003](20260925-dev-boot-003-创建SpringBoot模块化单体骨架.md)。
- 来源基线：[01-P0工程与技术验证.md](../solutions/开发任务基线/01-P0工程与技术验证.md)

## 目标

- [ ] 建立 `local`、`test`、`prod` 配置。
- [ ] 数据库、DeepSeek、工作目录、预算和端口全部环境变量化。
- [ ] 提供 `.env.example` 但不读取或提交真实值。
- [ ] `prod` 检测 Stub 模型、测试身份、本地对象存储或宽松字段策略时启动失败。
- [ ] 交付并核对以下产物：类型化配置类、profile 文件、配置说明和启动校验器。

## 验收标准

- 缺少生产必需配置时失败并给出不含秘密的明确错误。
- local 可使用 Stub。
- 负向检查：日志不得输出 API Key、数据库密码或完整连接串秘密部分。
- 实现方检查：配置绑定单元检查、`prod` 失败场景契约检查。
- 人工验证：用户验证本机环境变量注入。
- 完成证据：配置清单、失败样例、检查结果。

## 来源与关联

- 来源任务：`DEV-BOOT-004`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-BOOT-004` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
