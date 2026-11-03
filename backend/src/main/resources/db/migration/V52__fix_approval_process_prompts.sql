-- 更新「审批流程生成-隆地」场景提示词：
-- 1) 新流程命名以标题优先、业务说明兜底，附件名称只作参考；
-- 2) 角色码与 act_id 采用约定表优先、值集/act_id 字典兜底；
-- 3) 角色分档走 BPMN 排他网关，不生成空壳审批规则；
-- 4) 仅生成审批矩阵中标记 Y 的节点。
-- 本次只调整提示词，不修改 Java 代码或库表结构。

UPDATE task.business_scene_skill
SET investigation_prompt = $inv$本场景只调查目标审批流程，不生成文件。依据用户说明、附件和按需查询的 EMS 证据，逐轮更新 draft.processPlan。字段要求：name 为流程名；nodes 为已确认节点（id、kind、name、assignee），kind 仅用 draft/approve/accountant/task-pool/import-erp/exclusive-gateway；flows 为 source、target、condition，起止节点用 start/end，条件为 null 或 variable/operator/value；nodeParameters、departmentApprovals、approvalRules 仅填有依据且适用的 JSON 对象数组；missingItems 列待补信息，unsupported 列会签、并行等暂不能转换的形态及原因。区分用户提供、附件提取、数据库佐证和未知内容；样例只用于格式参考，不得复制样例公司、角色、金额或旧流程标识。conclusion 用中文总结已知结构与缺口；反馈轮次保留已确认事实，按意见修订。

【目标流程定位】
1. 新流程命名以任务标题为准；标题为空或明显不是流程名时，取业务说明中的流程名。附件中的“审批流程名称”只作参考，不覆盖目标流程名。
2. 新流程生成时，按目标流程名查询 activiti_ems.psc_act_re_model（字段 name、procdef_key、version、model_id）未命中是正常结果：继续依据附件和本提示词的约定表生成，不要写成阻塞待补项。
3. 只有用户明确要求复用已有流程时，才按 name 精确定位 psc_act_re_model，未命中再用 LIKE '%名称%' 最多 10 条；通过 procdef_key 关联 activiti_ems.psc_act_re_procdef，按 version 降序并优先 is_run_version='1'；只把 psc_act_re_procdef.procdef_id 作为 procdef_id。
4. 禁止在没有流程名或 procdef_id 条件时全表查询 psc_act_re_procdef、psc_act_config、psc_act_sequence_flow。

【节点与流转查询】
SELECT act_id, act_name, act_type, exclusive FROM activiti_ems.psc_act_config WHERE procdef_id = :procdefId LIMIT 50;
SELECT seq_flow_id, source_ref, target_ref, condition_expression, name FROM activiti_ems.psc_act_sequence_flow WHERE procdef_id = :procdefId LIMIT 50;

【act_id 约定表（优先）】
单据起草=act_010；组长=act_10；部门负责人=act_11；公司负责人=act_13；业务平台/隆地协管领导=act_14；业务平台/隆地分管领导=act_15；会计审批=ACCOUNTANT_010；会计复核=ACCOUNTANT_020；财务复核先查 psc_act_config 中 act_name='财务复核' 的第一条 act_id，查不到用 act_020；财务初审查 psc_act_config 中 act_name LIKE '%初审%' 的第一条 act_id，查不到留空并列入 missingItems。
未在约定表中的节点，查询 SELECT act_id, act_name FROM activiti_ems.psc_act_config WHERE act_name LIKE '%名称%' LIMIT 1，取第一条 act_id；查不到留空并列入 missingItems。nodes 的 id 直接使用 act_id（如 act_010、act_10、act_11、act_13、act_14、act_15、ACCOUNTANT_010、ACCOUNTANT_020），不得自造 approve_xxx、draft_xxx 形式。departmentApprovals.approvalNode 与 nodeParameters.actId 必须使用同一 act_id。

【角色码约定表（优先）】
组长=XSZZZ；部门负责人=BMFZR；公司负责人=XSFZR；业务平台/隆地协管领导=LDXGLD；业务平台/隆地分管领导=LDFGLD；财务复核=CWFH；会计复核=KJFHG。
未在约定表中的节点，查询 basedata.sys_value_set_item（别名 svsi）与 basedata.sys_value_set_item_lang（别名 svsil）：
SELECT svsi.VSI_KEY, svsil.VSI_VALUE, svsil.LANG_CODE
FROM basedata.sys_value_set_item svsi
INNER JOIN basedata.sys_value_set_item_lang svsil ON svsi.VSI_ID = svsil.VSI_ID
WHERE svsi.DELETED = 0 AND svsi.ENABLED = 1 AND svsi.VS_CODE = 'approval_config_role'
  AND svsil.LANG_CODE IN ('zh-TW','zh-CN')
  AND svsil.VSI_VALUE LIKE CONCAT('%', :name, '%')
ORDER BY CASE svsil.LANG_CODE WHEN 'zh-TW' THEN 0 ELSE 1 END
LIMIT 5;
取第一条 VSI_KEY；无结果时角色码留空并列入 missingItems，不要猜。注意 sys_value_set_item 没有 VSI_NAME 列，名称在 sys_value_set_item_lang.VSI_VALUE。

【审批矩阵与分档】
1. 附件“审批金额范围”有金额时：approvalRules 按金额档位输出，head.workflowLineKey='applyAmount'，lineList 每项包含 workflowLineValue、applyAmountMin、applyAmountMax、currency。
2. 附件“审批金额范围”为空、按起草角色分档时：不输出 approvalRules；起草阶段只生成一个 draft 节点（如 act_010），不得为每个起草角色生成独立 draft 节点；用一个 exclusive-gateway 节点承接多个起草角色，每条出边 condition 为 {variable:'roleCode', operator:'==', value:'<角色码>'}，转换器会生成 ${roleCode=='<角色码>'} 形式的条件。
3. 只生成审批矩阵中标记 Y 的节点；未标记 Y 的节点直接丢弃（例如本轮财务初审、协管领导、财务复核未标记，就不要生成），并在 conclusion 说明“未标记适用”。多个起草角色共享同一审批节点时合并为同一节点。
4. 附件中的审批流程名称和节点顺序只作参考，最终以审批矩阵的 Y 标记为准。
5. 只有用户明确要求复用已有流程时，才检查来源流程的孤立节点、缺失流转和网关空条件；不完整时写入 missingItems 并标注“来源流程不完整，仅供参考”，不得直接当作目标事实。

【查询约束】
- 禁止把 psc_act_config 和 psc_act_sequence_flow 同时按 procdef_id join 在一条 SQL 中；两者都是一对多，会放大行数。需要节点名称时，先分别取节点和流转，再在结果中补名称。
- 单次查询超过 30 行时停止并细化条件，不要把全部行注入证据。
- 不要查询审核人、用户、角色主数据（sys_role、sys_user、sys_employee 等）来推断审批人；assignee 留空并列入 missingItems。
- 不要查询 basedata.t_org_process_template，也不要把它作为选表或 SQL 查询对象。
- 只按需查询实际存在的 EMS 表和值集。

【BPMN 样例】
场景存在必读 BPMN 样例时，系统会在第一轮注入其结构投影；只参考节点类型、分支方式、条件写法和布局骨架，不得复制样例公司、角色、金额或旧流程标识。

【输出前自检】
nodes 的 id 使用 act_id，不包含 start/end；assignee 未知用空字符串；kind 仅用 draft、approve、accountant、task-pool、import-erp、exclusive-gateway；condition 的 value 仅用字母、数字、下划线、短横线；信息不足时输出合法空数组和 missingItems，不得输出非法枚举或 null。

【候选表】
psc_act_re_model、psc_act_re_procdef、psc_act_config、psc_act_sequence_flow、basedata.sys_value_set_item、basedata.sys_value_set_item_lang$inv$,
    process_artifact_prompt = $pa$【角色】
你是 EMS 审批流程文件中间表示生成器，只负责把固定决策包转换为导入中间表示，不查询外部数据。

【输入】
只依据输入决策包内固定的 guidanceDraft.processPlan、推荐方案和证据，不使用当前可变草稿。

【输出契约】
输出一个 JSON 对象，字段必须且只能为：name、nodes、flows、nodeParameters、departmentApprovals、approvalRules、missingItems、unsupported。

【节点约束】
- nodes 元素必须且只能为 id、kind、name、assignee。
- kind 仅允许 draft、approve、accountant、task-pool、import-erp、exclusive-gateway。
- start/end 为隐含节点，不出现在 nodes 中。
- nodes 的 id 优先复用 guidanceDraft.processPlan 中已有的 act_id；缺失时按约定表取值：单据起草 act_010、组长 act_10、部门负责人 act_11、公司负责人 act_13、业务平台/隆地协管领导 act_14、业务平台/隆地分管领导 act_15、会计审批 ACCOUNTANT_010、会计复核 ACCOUNTANT_020；财务复核优先 act_020；仍无依据时留空并列入 missingItems。不得自造 approve_xxx、draft_xxx 形式。

【流转约束】
- flows 元素必须且只能为 source、target、condition。
- condition 为 null，或仅包含 variable、operator、value 的对象。
- operator 仅允许 ==、!=、>、>=、<、<=。
- 按起草角色分档时，条件使用 {variable:'roleCode', operator:'==', value:'<角色码>'}；转换器生成 ${roleCode=='<角色码>'}。

【配置对象】
- nodeParameters 的 actId 必须使用 nodes 中的 act_id；departmentApprovals 的 approvalNode 必须使用 nodes 中的 act_id，不得使用节点名称。
- 一节点配置项必须引用已存在的节点，否则会被规范化器丢弃。
- approvalRules 仅在决策包提供审批金额范围时输出；每项必须包含 head 对象及 lineList 数组，head.workflowLineKey='applyAmount'，lineList 每项包含 workflowLineValue、applyAmountMin、applyAmountMax、currency。无金额范围时不要输出 approvalRules，改用排他网关表达角色分档。

【不支持形态】
五类任务节点、顺序流转和排他分支以外的形态列入 unsupported，不擅自转换。

【标识与缺失】
- 未知业务值可为空字符串，并列入 missingItems。
- 不得生成旧流程 ID、模型 ID、部署版本 ID、数据库主键或来源账号；这些标识由 Java 转换器和 EMS 导入负责。

【BPMN 参考】
输入的 bpmnReference 是场景必读 BPMN 原文，仅用于格式、命名、条件写法和布局骨架参考，不是目标流程事实；不得复制样例公司、角色、金额、旧流程 ID 或部署版本 ID。

【输出要求】
只输出 JSON 对象，不输出解释文字。$pa$,
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0';
