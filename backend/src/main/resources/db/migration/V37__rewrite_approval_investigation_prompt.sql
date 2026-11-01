-- 重写审批流程生成场景调查提示词，收窄流程定义、节点和流转查询范围。
UPDATE task.business_scene_skill
SET investigation_prompt = $prompt$本场景只调查目标审批流程，不生成文件。依据用户说明、附件和按需查询的 EMS 证据，逐轮更新 draft.processPlan。字段要求：name 为流程名；nodes 为已确认节点（id、kind、name、assignee），kind 仅用 draft/approve/accountant/task-pool/import-erp/exclusive-gateway；flows 为 source、target、condition，起止节点用 start/end，条件为 null 或 variable/operator/value；nodeParameters、departmentApprovals、approvalRules 仅填有依据且适用的 JSON 对象数组；missingItems 列待补信息，unsupported 列会签、并行等暂不能转换的形态及原因。区分用户提供、附件提取、数据库佐证和未知内容；样例只用于格式参考，不得复制样例公司、角色、金额或旧流程标识。conclusion 用中文总结已知结构与缺口；反馈轮次保留已确认事实，按意见修订。

【目标流程定位】
1. 从用户业务说明中提取流程名称（例如“8号流程”）；不要直接把任务标题当成流程名。标题与流程名不一致时，以说明中的流程名为准，并在 missingItems 记录不一致。
2. 先用 act_re_model.NAME_ 精确定位；不确定时用 LIKE '%名称%'，最多返回 10 条。
3. 通过 pd.key_ = m.key_ 关联 act_re_procdef，按 pd.version_ DESC 排序；多版本时优先 is_run_version=1 或最新版本，并在证据中记录实际使用的 version_ 和 procdef_id。
4. 只把 pd.id_ 作为 procdef_id；不要用 m.key_ 或 deployment_id_ 代替。
5. 禁止在没有流程名称或 procdef_id 条件时全表查询 psc_act_re_procdef、psc_act_config、psc_act_sequence_flow。

【节点查询】
SELECT act_id, act_name, act_type, exclusive
FROM activiti_ems.psc_act_config
WHERE procdef_id = :procdefId
LIMIT 50;

【流转查询】
SELECT seq_flow_id, source_ref, target_ref, condition_expression, name
FROM activiti_ems.psc_act_sequence_flow
WHERE procdef_id = :procdefId
LIMIT 50;

【查询约束】
- 禁止把 psc_act_config 和 psc_act_sequence_flow 同时按 procdef_id join 在一条 SQL 中；两者都是一对多，会放大行数。需要节点名称时，先分别取节点和流转，再在结果中补名称。
- 单次查询超过 30 行时停止并细化条件，不要把全部行注入证据。
- 不要查询审核人、用户、角色主数据（sys_role、sys_user、sys_employee 等）来推断审批人；assignee 留空并列入 missingItems。
- 不要查询 basedata.t_org_process_template，也不要把它作为选表或 SQL 查询对象。
- 只按需查询实际存在的 EMS 表和值集。

【角色代码】
角色代码优先查询 basedata.sys_value_set_item（VS_CODE 为 approval_config_role，代码取 VSI_KEY）；查不到时使用用户提供的角色代码并列入 missingItems 标明未核实。

【BPMN 样例】
场景存在必读 BPMN 样例时，系统会在第一轮注入其结构投影；只参考节点类型、分支方式、条件写法和布局骨架，不得复制样例公司、角色、金额或旧流程标识。

【输出前自检】
nodes 不包含 start/end；节点 id 仅使用字母、数字、下划线；assignee 未知用空字符串；kind 仅用 draft、approve、accountant、task-pool、import-erp、exclusive-gateway；condition 的 value 仅用字母、数字、下划线、短横线；信息不足时输出合法空数组和 missingItems，不得输出非法枚举或 null。$prompt$,
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0';
