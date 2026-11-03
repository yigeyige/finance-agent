## Decisions

### 1. 只改提示词，不改 Java 业务映射

约定表、SQL 模板、分档规则全部写入场景提示词，由模型按提示词执行；不在 Java 中写死 act_id/角色码。仅当必须新增查询编排轮次时才改代码，本次不需要。

### 2. 新流程命名口径

- `processPlan.name = 任务标题`；标题为空或明显不是流程名时，取业务说明中的流程名。
- 附件 `审批流程名称` 仅作参考，不覆盖目标流程名。
- 新流程在 `activiti_ems.psc_act_re_model` 查不到是正常结果，不写入阻塞待补项；只有用户明确要求复用已有流程时才需要补充 procdef_id。

### 3. 角色码取值

- 约定表优先：组长 `XSZZZ`、部门负责人 `BMFZR`、公司负责人 `XSFZR`、隆地协管领导 `LDXGLD`、隆地分管领导 `LDFGLD`、财务复核 `CWFH`、会计复核 `KJFHG`。
- 约定表未覆盖时查 `basedata.sys_value_set_item` + `basedata.sys_value_set_item_lang`：
  - `LANG_CODE` 先 `zh-TW`，再 `zh-CN`；
  - 按 `VSI_VALUE LIKE '%名称%'` 取第一条 `VSI_KEY`；
  - 0 条则角色码留空并写入 `missingItems`，不猜。
- 明确 `sys_value_set_item` 没有 `VSI_NAME` 列，名称在 `sys_value_set_item_lang.VSI_VALUE`。

### 4. act_id 取值

- 约定表：单据起草 `act_010`、组长 `act_10`、部门负责人 `act_11`、公司负责人 `act_13`、隆地协管领导 `act_14`、隆地分管领导 `act_15`、会计审批 `ACCOUNTANT_010`、会计复核 `ACCOUNTANT_020`。
- 未覆盖时查 `activiti_ems.psc_act_config` 的 `act_id, act_name`，按 `act_name LIKE '%名称%'` 取第一条；0 条则留空并写入 `missingItems`。
- `nodes[].id` 直接使用 act_id；`departmentApprovals.approvalNode` 和 `nodeParameters.actId` 复用同一 act_id，确保规范化器能解析。

### 5. 审批规则与分档

- 有审批金额范围：`approvalRules` 按 EMS 契约输出：
  - `head.workflowLineKey = 'applyAmount'`；
  - `lineList[]` 包含 `workflowLineValue`、`applyAmountMin`、`applyAmountMax`、`currency`。
- 无金额范围、按起草角色分档：不输出 `approvalRules`；用一个 `exclusive-gateway` 承接多个起草角色，出边条件为 `{variable:'roleCode', operator:'==', value:'<角色码>'}`，转换器渲染为 `${roleCode=='<角色码>'}`。
- 只生成审批矩阵中标记 Y 的节点；未标记 Y 的节点不生成，并在 conclusion 说明“未标记适用”。

## Risks

- 约定表是业务约定，若后续 act_id 调整需要同步提示词；本次不写死代码，便于通过后台或迁移快速修改。
- 值集 `zh-TW` 下部分名称与业务名称不一致（如“公司负责人”对应“下属公司部门负责人”），因此约定表优先，值集只作兜底。
- 不生成 `审批规则.json` 依赖 EMS 导入侧接受“文件可缺省”；已验证 `ProcessArtifactService` 只在内容非空时写入文件。
