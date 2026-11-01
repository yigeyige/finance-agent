# EMS 流程后台表分析：中艺深圳-退还按金（含对账 SQL）

> 分析日期：2026-10-26
> 分析方式：Playwright 登录 EMS（adminJw）实际打开 modeler.html 页面抓取数据请求，并通过 REST 接口实测拉取模型 JSON、部署 BPMN XML、节点/流转配置，交叉验证 `emsld` 源码确认列名。

---

## 一、流程基本信息（实测确认）

| 项目 | 值 |
|---|---|
| 模型ID（modelId） | `69426d7b-058b-11f1-9863-fa163eeb12c1` |
| 流程Key | `crc_ems-0be371ed-5db2-464b-a634-d97606fb8b01` |
| 名称 | 中艺深圳-退还按金 |
| 版本 | v1 + v2（**v2 为运行版本**） |
| v2 流程定义ID（procdefId） | `279e1e05-058c-11f1-9863-fa163eeb12c1` |
| v1 流程定义ID | `698d8040-058b-11f1-9863-fa163eeb12c1` |
| v2 部署ID（deploymentId） | `278a6ef2-058c-11f1-9863-fa163eeb12c1` |
| 分类ID（categoryId） | `f32ce9dbe5994f228966d8103297930f` |
| 图形规模 | 52 个图形：13 个用户任务、7 个排他网关、16 条条件流转 |

### 用户任务清单

单据起草(act_010)、证明人(act_100)、影像初审岗(CSIMG_010)、资产管理员(ZC_010)、组长(act_10)、部门负责人(act_11)、公司负责人(act_13)、业务平台/隆地分管领导(act_15)、财务主管(act_16)、会计复核(ACCOUNTANT_020)、会计任务池(GXKJC_010)、会计审批(ACCOUNTANT_010)、导入FMS(ERP_010)

### 分支条件（示例）

`${roleCode=='XSFZR'}`、`${roleCode=='ZYMD'&&roleCode!='XSZZZ'}`、`${applyAmount=='A001'}`、`${paySelecter=='FIXED_PURCHASE'}`、`${isProve}`、`${IS_IMPORT_FMS}`、`${isFmsPass}`

---

## 二、modeler.html 页面组装流程图实际请求的数据（Playwright 抓包实测）

页面只发 3 个数据请求：

| 请求 | 返回 | 对应表 | 说明 |
|---|---|---|---|
| `GET /service/model/{modelId}/json` | 200，35KB JSON | `ACT_RE_MODEL` + `ACT_GE_BYTEARRAY`（EDITOR_SOURCE_JSON_） | **流程图核心数据**：52 个图形、属性、监听器、条件表达式 |
| `GET /service/editor/stencilset` | 200，131KB | 静态资源（不涉及表） | 元件面板定义 |
| `GET /service/editor/stencilset2` | 200，131KB | 静态资源（不涉及表） | EMS 元件扩展 |

---

## 三、涉及的表（共 10 张）

| # | 表 | 存什么 |
|---|---|---|
| 1 | `act_re_model` | 模型元数据 |
| 2 | `act_ge_bytearray` | 编辑器JSON（画流程图的源数据）/ 部署BPMN XML / 图片 |
| 3 | `act_re_procdef` | 已部署流程定义（v1/v2） |
| 4 | `act_re_deployment` | 部署批次 |
| 5 | `act_procdef_info` | 定义↔图片映射 |
| 6 | `psc_act_re_model` | 模型扩展（编辑器/下载URL） |
| 7 | `psc_act_re_procdef` | 流程版本扩展（运行版本标记） |
| 8 | `psc_act_config` | 节点配置（处理人/候选人/表单） |
| 9 | `psc_act_sequence_flow` | 分支条件 |
| 10 | `psc_category` | 流程分类 |

---

## 四、实测数据量（后台核对基准）

| 表 | 实测行数 | 核对条件 |
|---|---|---|
| `act_re_model` | 1 行 | modelId |
| `act_re_procdef` | 2 行（v1/v2） | procdefKey |
| `psc_act_re_procdef` | 2 行 | procdefKey |
| `psc_act_config` | **10 行** | procdefId（v2） |
| `psc_act_sequence_flow` | **16 行** | procdefId（v2） |

---

## 五、对账 SQL（MySQL，直接执行）

```sql
-- 1. 模型
SELECT * FROM act_re_model WHERE id_ = '69426d7b-058b-11f1-9863-fa163eeb12c1';

-- 2. 流程定义（按key查全部版本）
SELECT * FROM act_re_procdef WHERE key_ = 'crc_ems-0be371ed-5db2-464b-a634-d97606fb8b01' ORDER BY version_;

-- 3. 部署
SELECT * FROM act_re_deployment WHERE id_ IN ('278a6ef2-058c-11f1-9863-fa163eeb12c1','697a6d6d-058b-11f1-9863-fa163eeb12c1');

-- 4. 模型编辑器JSON（画流程图的源数据，BYTES_ 列是 JSON）
SELECT ba.* FROM act_ge_bytearray ba
WHERE ba.id_ IN (
  SELECT editor_source_value_id_ FROM act_re_model WHERE id_ = '69426d7b-058b-11f1-9863-fa163eeb12c1'
);

-- 5. 部署后的BPMN XML / 图片
SELECT ba.* FROM act_ge_bytearray ba
WHERE ba.deployment_id_ = '278a6ef2-058c-11f1-9863-fa163eeb12c1';

-- 6. 定义-图片映射
SELECT * FROM act_procdef_info WHERE proc_def_id_ = '279e1e05-058c-11f1-9863-fa163eeb12c1';

-- 7. 模型扩展表
SELECT * FROM psc_act_re_model WHERE model_id = '69426d7b-058b-11f1-9863-fa163eeb12c1';

-- 8. 流程版本扩展（注意列名 deployement_id 是代码原样拼写，含笔误）
SELECT * FROM psc_act_re_procdef WHERE procdef_key = 'crc_ems-0be371ed-5db2-464b-a634-d97606fb8b01' ORDER BY version;

-- 9. 节点配置（v2运行版本，预期 10 行）
SELECT id, procdef_id, act_id, act_name, act_type, form_key, assignee, assignee_type,
       candidate_user, candidate_group, is_async, exclusive, is_multi_inst, multi_type
FROM psc_act_config WHERE procdef_id = '279e1e05-058c-11f1-9863-fa163eeb12c1' ORDER BY act_id;

-- 10. 分支条件（v2运行版本，预期 16 行）
SELECT id, seq_flow_id, source_ref, target_ref, condition_expression, type, flow_order, default_flow_id
FROM psc_act_sequence_flow WHERE procdef_id = '279e1e05-058c-11f1-9863-fa163eeb12c1' ORDER BY source_ref;

-- 11. 分类
SELECT * FROM psc_category WHERE id = 'f32ce9dbe5994f228966d8103297930f';
```

## 六、一条 SQL 串起全链路（先跑这个看整体）

```sql
SELECT m.id_ AS model_id, m.name_ AS model_name,
       pd.id_ AS procdef_id, pd.key_ AS procdef_key, pd.version_ AS procdef_version,
       d.id_ AS deployment_id, d.deploy_time_,
       pr.id AS psc_procdef_id, pr.is_run_version, pr.category_id,
       (SELECT COUNT(*) FROM psc_act_config c WHERE c.procdef_id = pd.id_) AS node_config_cnt,
       (SELECT COUNT(*) FROM psc_act_sequence_flow sf WHERE sf.procdef_id = pd.id_) AS seqflow_cnt
FROM act_re_model m
LEFT JOIN act_re_procdef pd ON pd.key_ = m.key_
LEFT JOIN act_re_deployment d ON d.id_ = pd.deployment_id_
LEFT JOIN psc_act_re_procdef pr ON pr.procdef_id = pd.id_
WHERE m.id_ = '69426d7b-058b-11f1-9863-fa163eeb12c1'
ORDER BY pd.version_;
```

---

## 七、导出 ZIP 的四个文件 ↔ 后台表对应关系

> 来源：`PscActReModelPrvService.exportProcessZip()`（ems-process-provider）
> 导出按钮实际调用：`GET /psc/actmodel/pscActReModel/exportZip?id={modelId}`，`id` 为**模型ID**（69426d7b-058b-11f1-9863-fa163eeb12c1），不是流程定义ID

### 7.1 文件与表对应

| zip 内文件 | 导出数据来源 | 后台表 | 关联条件 | 本次数据量 |
|---|---|---|---|---|
| **`中艺深圳-退还按金BPMN.xml`**（扩展名 .xml，内容实为 Activiti 编辑器 JSON） | `ModelSourceClient.getModelBytes()` → 引擎 REST `/repository/models/{id}/source` | **`act_re_model`**（name_/key_）+ **`act_ge_bytearray`**（`EDITOR_SOURCE_JSON_` 记录，经 `act_re_model.editor_source_value_id_` 关联） | `model_id = 69426d7b...` | 1 行 |
| **`节点参数配置.json`** | `procdefVariableConfService.findList()`，procdefId 取 `pscActReProcdefService.getRunVersion(key)`（**运行版本**） | **`psc_procdef_variable_conf`** | `procdef_id = 279e1e05-058c-11f1-9863-fa163eeb12c1`（v2 运行版本） | 48 行 |
| **`部门审批流.json`** | `adminClient.findAllExpOrgProcessTemplateInfo()`（跨服务调 ems-admin） | **`t_org_process_template`** | `PROCESS_TEMPLATE_CODE = crc_ems-0be371ed...` 且 `STATE = 1` | 1 行 |
| **`审批规则.json`** | `adminClient.findByProcessId()` 取 head，再按 `rulesHeadId` 取 line | **`t_approval_rules_head`** + **`t_approval_rules_line`** | head: `PROCESS_ID = key` 且 `IS_DELETE = '0'`；line: `RULES_HEAD_ID = 442` | head 1 行 + line 2 行 |

### 7.2 字段级映射

**节点参数配置 ↔ `psc_procdef_variable_conf`**（蛇形列）：
`variableName→variable_name`、`procdefId→procdef_id`、`actId→act_id`、`variableValue→variable_value`、`variableIsnull→variable_isnull`、`usednextact→usednextact`、`actName→act_name`、`variableDefaultvalue→variable_defaultvalue`、`variableFormat→variable_format`、`scope→scope`、`delFlag→del_flag`

**部门审批流 ↔ `t_org_process_template`**（大写列）：
`business→BUSINESS`、`processTemplateCode→PROCESS_TEMPLATE_CODE`、`approvalNode→APPROVAL_NODE`、`nodeRule→NODE_RULE`、`valueUrl→VALUE_URL`、`companyCode→COMPANY_CODE`、`startAmount/endAmount→START_AMOUNT/END_AMOUNT`、`createdBy→CREATED_BY`、`updatedBy→UPDATED_BY`（另有 `STATE` 列做过滤）

**审批规则 head ↔ `t_approval_rules_head`**：
`rulesHeadId→RULES_HEAD_ID`、`processId→PROCESS_ID`、`workflowLineKey→WORKFLOW_LINE_KEY`、`isDelete→IS_DELETE`（`RULES_HEAD_ID=442` 表示"按申请金额 applyAmount 分档"）

**审批规则 line ↔ `t_approval_rules_line`**：
`rulesLineId→RULES_LINE_ID`、`rulesHeadId→RULES_HEAD_ID`、`workflowLineValue→WORKFLOW_LINE_VALUE`、`applyAmountMin→APPLY_AMOUNT_MIN`、`applyAmountMax→APPLY_AMOUNT_MAX`、`currency→CURRENCY`（line 1374=A001 金额≤1w，1375=A002 金额 1w~100w）

### 7.3 对账时注意

1. **BPMN.xml 与前述 10 张表的关系**：`act_re_model` + `act_ge_bytearray` 即 BPMN.xml 来源；其余 3 类表（`psc_procdef_variable_conf`、`t_org_process_template`、`t_approval_rules_head/line`）是流程模型之外的"旁路配置表"，属 ems-admin 侧。
2. **节点参数配置导出是"全量拉取"**：按 procdefId 过滤但不过滤节点，含 act_050、act_090、act_170、NCC_010 等**不在当前 BPMN 13 个用户任务里**的历史遗留节点数据（48 行），导入时会原样写回。
3. **审批规则 head 的 processName 是"中艺香港-退还按金（对接用友）"**，与模型名"中艺深圳-退还按金"不一致，属数据脏值；processId 正确。

### 7.4 对账 SQL（MySQL）

```sql
-- 1. 节点参数配置（运行版本 279e1e05-058c-11f1-9863-fa163eeb12c1，预期 48 行）
SELECT id, procdef_id, act_id, act_name, variable_name, variable_value,
       variable_format, variable_isnull, usednextact, del_flag
FROM psc_procdef_variable_conf
WHERE procdef_id = '279e1e05-058c-11f1-9863-fa163eeb12c1' AND del_flag = '0';

-- 2. 部门审批流（预期 1 行）
SELECT * FROM t_org_process_template
WHERE PROCESS_TEMPLATE_CODE = 'crc_ems-0be371ed-5db2-464b-a634-d97606fb8b01' AND STATE = 1;

-- 3. 审批规则 head + line（head 1 行，line 2 行）
SELECT * FROM t_approval_rules_head
WHERE PROCESS_ID = 'crc_ems-0be371ed-5db2-464b-a634-d97606fb8b01' AND IS_DELETE = '0';

SELECT * FROM t_approval_rules_line WHERE RULES_HEAD_ID = 442;
```
