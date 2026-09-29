---
id: 20260925-dev-qa-007
title: "验证完整 Harness 能力"
status: done
priority: medium
owner: ""
created_at: 2026-09-25
type: test
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-QA-007
---

# 验证完整 Harness 能力

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-QA-007`，用于验证完整 Harness 能力。

## 前置条件

- 依赖：[DEV-HAR-009](20260925-dev-har-009-实现ContextCompression.md)、[DEV-HAR-010](20260925-dev-har-010-实现受控Workspace.md)、[DEV-HAR-011](20260925-dev-har-011-实现受控Skills.md)、[DEV-HAR-012](20260925-dev-har-012-实现受控Subagents.md)、[DEV-HAR-013](20260925-dev-har-013-实现受控Long-termMemory.md)、[DEV-HAR-014](20260925-dev-har-014-验证DockerSandbox.md)。
- 来源基线：[07-P0测试与验收.md](../solutions/开发任务基线/07-P0测试与验收.md)

## 目标

- [x] 逐项执行附件 C 中 Context Compression、Workspace、Skills、Subagents、Memory、Sandbox 的功能、安全和故障场景。
- [x] 每项保存独立能力记录。
- [x] 交付并核对以下产物：Harness 能力验收表。

## 验收标准

- 所有能力已实际接入/验证。
- 生产权限保持白名单。
- Sandbox 仅按确认模式运行。
- 实现方检查：能力配置和编译。
- 人工验证：用户执行需要运行环境的用例。
- 完成证据：版本、输入、结果、限制和人工结论。

## 来源与关联

- 来源任务：`DEV-QA-007`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-QA-007` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-29：完成 `harness-capability-acceptance-gate` OpenSpec 变更，新增 `harness-capability-acceptance-v1.json`、验收脚本和 `HarnessCapabilityAcceptanceContractTest`。
- 2026-09-29：验证通过：`mvn -q '-Dtest=HarnessCapabilityAcceptanceContractTest,ContextCompressionTest,ControlledWorkspaceManagerTest,SubagentBridgeTest,LongTermMemoryGatewayTest,DockerSandboxCapabilityTest,HarnessAgentSmokeContractTest' test`、`mvn -q -DskipTests compile`、`python scripts/verify-contracts.py`、`pwsh -NoProfile -File scripts/qa-harness-capability-acceptance.ps1`、`openspec validate harness-capability-acceptance-gate --strict`。
