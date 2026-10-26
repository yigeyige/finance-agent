# EMS 表知识（结构化）

本目录沉淀 EMS 三库（`basedata` / `account` / `activiti_ems`）的表级结构化知识，供业务场景 Skill 的固定提示词（`task.business_scene_skill.description`）与查询规划使用。

## 数据来源与口径

- 来源：DBX 连接 `隆地-报账claim-UAT数据库`（MySQL，只读，10.200.192.113:3307）。
- 采集方式：每批 5 张表，先取元数据（表注释 + 字段名/类型/主键/注释），再做语义分析后结构化落盘。
- 过滤：跳过备份/日期后缀表（如 `*_20230818`）、导入中间表（`*_importable/_imported/_importing`）与占位表（`t1`/`t2`）。
- **跳过 log 表**：名称以 `_log`/`_hist`/`_history`/`_messages`/`_fail` 结尾、`act_hi_*` 历史表、`t_dsb_tx_*` 分布式事务、`t_mq_*`、`t_task_*`、`t_esb_messages`、`sys_operation_log`、`t_url_log`、`t_serial_number`、`psc_call_uts*`、`t_process_*_log/callback_event` 等运行时日志表不写知识（纯运行日志，无业务配置价值）。
- **无数据表标记**：`information_schema.TABLE_ROWS = 0` 的表（如 `activiti_ems.psc_employee`/`psc_dept`/`psc_job` 已废弃，人员/组织/岗位真实数据在 `basedata.sys_*`）只记 `dataStatus: 无数据`，不再深挖字段。
- 知识可信等级沿用 `docs/ems-knowledge/README.md`：表注释/字段注释属 `SCHEMA_VERIFIED`，据此推断的语义属 `INFERRED`，需样本或代码证据确认后升级。

## 文件结构

- `account.json` / `activiti_ems.json` / `basedata.json`：每 schema 一个 JSON 数组，每项为一张表的知识。

## 单表 JSON 结构

```json
{
  "schema": "account",
  "table": "t_account_assign_config",
  "tableComment": "会计分单配置",
  "businessMeaning": "……",
  "keyFields": [{ "name": "……", "meaning": "……" }],
  "relations": ["……"],
  "promptHint": "……"
}
```

字段说明：

- `businessMeaning`：表的中文业务用途一句话。
- `keyFields`：关键字段及语义（含主键、关联键、业务含义字段）。
- `relations`：关联表清单（`schema.table` 或 `表名(字段)`）。
- `promptHint`：可直接拼入 `business_scene_skill.description` 的提示词片段（含字段语义与查询顺序提示）。

## 接入方式

`promptHint` 聚合后写入对应业务场景 Skill 的 `description`，参考 `scripts/update-business-scene-skill-knowledge.sql`。
