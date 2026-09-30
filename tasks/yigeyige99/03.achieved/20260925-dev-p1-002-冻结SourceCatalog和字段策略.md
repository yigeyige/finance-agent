---
id: 20260925-dev-p1-002
title: "冻结 SourceCatalog 和字段策略"
status: done
priority: high
owner: yigeyige99
created_at: 2026-09-25
type: design
source: development-workplan
openspec_change: enable-p1-test-business-source
source_task: DEV-P1-002
---

# 冻结 SourceCatalog 和字段策略

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-P1-002`，用于冻结 P1 测试业务源的 SourceCatalog 和字段策略。测试范围只使用固定、离线、虚构或已脱敏样本，不接入生产或真实业务数据。

## 前置条件

- 依赖：[P1-ENTRY](../../../solutions/开发任务基线/08-P1真实源试点.md#p1-entry-入口门禁)。
- 来源基线：[08-P1真实源试点.md](../../../solutions/开发任务基线/08-P1真实源试点.md)
- 边界确认：[P1 测试业务源边界确认](../assets/20260930-P1测试业务源边界确认.md)。

## 目标

- [x] 按测试业务源逐对象确认 API/只读视图、测试范围内权威级别、逻辑/物理字段、三类权限、分页、稳定排序、水位/版本、生效和错误语义。
- [x] 交付并核对测试 SourceCatalog、字段矩阵、查询模板和有效/无效样例。

## 验收标准

- 每个确认结论能指向受控测试源和仓库内交付物。
- 未知冲突规则保持 UNKNOWN。
- 完成证据：测试业务源边界确认、目录/矩阵/样本核对和 OpenSpec 严格校验。真实源责任人、真实字段权限和生产凭证不属于本任务。

## 来源与关联

- 来源任务：`DEV-P1-002`
- OpenSpec 变更：`enable-p1-test-business-source`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-P1-002` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-30：用户确认 P1 使用测试业务源与虚构/脱敏样本；开始按 `enable-p1-test-business-source` 固化目录和字段策略，生产真实源继续后移至 P2 独立门禁。
- 2026-09-30：完成测试业务源边界确认、SourceCatalog、字段策略矩阵与查询样本；静态核对与 `SyntheticConfigSourceAdapter` 一致，未发现真实数据、凭证或生产连接标识，`openspec validate enable-p1-test-business-source --strict` 与 `git diff --check` 通过。
- 2026-09-30：已将 delta specs 同步至主规格，并归档 OpenSpec 变更 `2026-09-30-enable-p1-test-business-source`。
