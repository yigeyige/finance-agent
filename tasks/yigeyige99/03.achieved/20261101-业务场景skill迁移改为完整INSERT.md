---
id: 20261101-business-scene-skill-seed
title: "业务场景 Skill 迁移改为完整 INSERT"
status: done
priority: P0
owner: yigeyige99
created_at: 2026-11-01
type: maintenance
source: conversation
openspec_change: ""
---

# 业务场景 Skill 迁移改为完整 INSERT

## 需求

### ID&标题

业务场景 Skill 迁移改为完整 INSERT

### 描述

`task.business_scene_skill` 的两条业务场景 Skill（审批流程生成-隆地、人员配置离职检查-隆地）
最初由前台手工新增。现有迁移只有针对固定 `skill_id` 的 `UPDATE`，全新库跑 Flyway 时
记录不存在，`UPDATE` 静默 0 行，V29 的附件 INSERT 会因外键失败。需要把两条 Skill 及其
4 个样例附件改为一次性完整 `INSERT`，并去掉所有针对该表的 `UPDATE`。

## 背景

- UAT 库 `finance-agent-168`（192.168.1.168:5432/finance_agent）实测只有两条 Skill，
  created_at 分别为 2026-10-27 与 2026-10-30，均早于 V29 的安装时间，确认为前台手工创建。
- 迁移历史表是 `finance_agent_schema_history`，已应用 V1–V38，全部 success。
- `process_artifact_prompt` 列在 V30 新增，因此完整 INSERT 不能放在 V29，必须后移到
  该列创建之后（本任务放在新增的 V39）。
- 迁移文件已被应用，改写后 Flyway 会报 checksum 不一致，需要执行一次 `--repair`；
  repair 只更新历史表校验和，不会重跑迁移，也不会删除或覆盖业务数据。

## 目标

- [x] 删除 V29/V30/V31/V32/V33/V35/V37/V38 中针对 `task.business_scene_skill` 的 UPDATE
- [x] 新增 V39，一次性 INSERT 两条 Skill（含调查、决策包、流程产物三段提示词）与 4 个样例附件
- [x] 全新库无 IAM 用户时，先幂等播种操作员，保证 `created_by`/`updated_by` 外键可用
- [x] `FlywayMigrationRunner` 支持 `--repair`，`scripts/flyway-migrate.ps1` 支持 `-Repair`
- [x] 在临时库按 V1–V39 顺序全量执行通过，种子数据与 168 库内容一致

- [x] 删除失效脚本 `scripts/update-business-scene-skill-knowledge.sql`

## 验收标准

- 仓库内不再存在 `UPDATE task.business_scene_skill`。
- 全新库执行 V1–V39 后，`task.business_scene_skill` 恰好两条记录，附件 4 条。
- 两条记录的 `investigation_prompt` / `decision_packet_prompt` / `process_artifact_prompt`
  与 168 库现有值一致（忽略 CRLF/LF 差异）。
- 迁移脚本幂等：重复执行不产生重复数据，也不覆盖已有记录。
- 后端编译通过。

## 执行记录

- 2026-11-01：完成迁移改写与 V39 生成；临时库 finance_agent_migcheck 全量执行通过；
  与 168 库提示词哈希比对一致；后端离线编译通过，FlywayMigrationContractTest 2 个用例通过。
- 2026-11-01：删除失效脚本 `scripts/update-business-scene-skill-knowledge.sql`（引用的 UUID 不存在，
  且 `description` 列已被 V21 改名）。
- 2026-11-01：在 168 执行 `--repair` → `status=REPAIRED`；随后执行迁移 → `status=OK, migrationsExecuted=1`
  （仅应用 V39）；`--validate` → `status=VALID`。核对 168 仍是 2 条 Skill、4 个附件，
  created_at/updated_at/updated_by 均未变化，V39 的 `ON CONFLICT DO NOTHING` 未覆盖任何已有数据。
- 2026-11-01：任务验收完成，归档至 `tasks/yigeyige99/03.achieved/`；本任务无 OpenSpec change，无需同步 delta spec。
