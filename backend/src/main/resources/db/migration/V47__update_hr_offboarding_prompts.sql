-- 确保提示词列可容纳结构化内容。
ALTER TABLE task.business_scene_skill ALTER COLUMN investigation_prompt TYPE text;
ALTER TABLE task.business_scene_skill ALTER COLUMN decision_packet_prompt TYPE text;

-- 更新“人员配置离职检查-隆地”的调查提示词、决策包提示词和候选表。
UPDATE task.business_scene_skill
SET investigation_prompt = $inv$本场景只排查一名员工在 EMS 中的人员配置：用户给出员工账号或工号，逐项列出其全部配置供人工核对，不修改任何数据。字段名以查询规划的 tableColumns 或初稿的 databaseEvidence 中的实际字段和注释为准，不得臆造。

【人员定位】
1. 以 basedata.sys_register_user 为入口，按 USERCODE（账号）或 EMPNO（工号）精确匹配，可用姓名辅助确认；取 USERID、USERCODE、EMPNO、ENABLED、DELETED。
2. 未命中时用 LIKE 模糊查询，最多返回 5 条请用户确认后在下一轮继续，不得猜测人员身份。
3. 记录定位到的 USERCODE、EMPNO、EMP_ID 或 EMPLID、OPRID，后续步骤用真实键关联，不要混用数字 USERID 与账号或工号。

【配置范围】
按人员、组织、权限、审批、流程平台逐类检查，常见位置：
- 账号与任职：basedata.sys_register_user、sys_emp、sys_emp_job（任职、离职日期、HR 状态）。
- 角色与群组：basedata.sys_user_role、sys_role、sys_group_member、sys_group。
- 组织与管理者：basedata.sys_user_org_rel、t_org_manger（emp_no、ldap）。
- 审批配置：basedata.t_audit_configuration（EMP_IDS 存 USERCODE、EMP_NAMES 存姓名，用 USERCODE 或姓名匹配，不要用数字 USERID 匹配；ROLE_CODE 是审批环节角色）、t_approval_delegation、t_project_manage_approve。
- 流程平台：activiti_ems.psc_job、psc_act_assignee（assignee）、psc_act_config 中的处理人或候选人字段。
- 不要查询 activiti_ems.psc_employee（已停用），也不要把该表作为人员定位入口。
清单外的表，只要字段注释表明与人员相关也应检查；禁止查询日志、历史、备份和临时表。

【查询约束】
1. 每条 SQL 必须是以 basedata 或 activiti_ems 限定的单条 SELECT，带人员条件，禁止全表扫描、UNION、WITH、注释和分号。
2. 先定位人员，再查各配置表；每轮最多 5 步，单步最多返回 20 行。
3. 关联键不确定时先查字段值和注释，不跨未知表 JOIN；已确认无数据的表不要重复查询。

【输出要求】
1. draft.conclusion 按配置项逐条列出：模块、表名（注释）、关键字段与取值、记录数、是否有效；已删除、停用或过期的记录单独标注；不同配置项用分号分隔，不列无数据的表。
2. draft.evidence 每张有数据的表一条，含 schema、table、purpose、columns、rows，rows 每表最多 20 行；超过 10 张时保留最关键的 10 张，其余在 conclusion 中列明并提示可分轮补充。
3. 人员无法唯一定位、配置表未覆盖或候选表明显不含人员相关表时，在 reply 说明剩余范围和下一步，并置 needMoreEvidence 或 needTableReselection。
4. 用户补充或纠正后按 feedback 继续，只补查未覆盖或存疑项，保留已确认结论。
5. 只展示业务标识，不输出密码、证件号等敏感字段；任务说明中的离职状态属于用户提供，不是系统核实事实。$inv$,
    decision_packet_prompt = $dec$【角色】
你负责把已固定的离职人员配置调查结论整理成处置决策包，只依据输入证据，不重新查询数据库、不补造事实、不执行任何修改。

【输入】
只使用 evidence 中的表名、字段和行数据，以及 guidanceDraft 中已确认的结论；不采用证据之外的配置，不沿用未核实的历史结论。

【结论字段】
- subjectKey：员工账号或工号加姓名，例如 zhangsan / 张三。
- conclusion：按模块逐条列出需人工核对的配置，格式为 模块、表名（注释）、关键字段值、记录数、是否有效；用分号分句，便于逐条阅读；无数据的表不列，未检查的表注明本轮未覆盖；超过 10 条时按模块合并并保留表名与记录数。
- recommendedGoal：逐条给出建议处置动作，写成 操作加数据对象，例如 停用 basedata.sys_register_user 账号；移除 basedata.sys_user_role 的角色绑定；转移 basedata.t_audit_configuration 的审批配置；删除 basedata.t_org_manger 的管理者配置。动作只用停用、移除、转移、删除、保留核对、补充核查，不写具体 SQL。
- alternativeGoals：0 到 2 条备选方案，例如先冻结账号再清理配置、先转移审批配置再停用账号。
- reason：说明处置依据，引用表名和关键字段；列出未覆盖、待核实或相互冲突的项。

【组织要求】
整体按 操作、数据对象、证据 三要素组织：recommendedGoal 写操作和数据对象，conclusion 写对象现状，reason 写对应证据来源。

【事实边界】
1. 不把任务说明中的离职状态写成系统已核实事实，只写用户提供。
2. 查询返回 0 行的表可写已确认无数据；未查询的表只能写本轮未覆盖。
3. 不引用 activiti_ems.psc_employee（已停用）。
4. 不声称已经修改任何数据，不输出密码、证件号等敏感字段。

【输出要求】
只输出 subjectKey、conclusion、recommendedGoal、alternativeGoals、reason 五个字段的 JSON，文本使用简体中文，不添加解释性前后缀。$dec$,
    table_hints = 'basedata.sys_register_user basedata.sys_emp basedata.sys_emp_job basedata.sys_user_role basedata.sys_role basedata.sys_group_member basedata.sys_group basedata.sys_user_org_rel basedata.t_org_manger basedata.t_audit_configuration basedata.t_approval_delegation basedata.t_project_manage_approve activiti_ems.psc_job activiti_ems.psc_act_assignee activiti_ems.psc_act_config basedata.sys_value_set_item',
    updated_at = now()
WHERE skill_id = '6ec7774b-b226-4a7c-be5f-49349c3a14d8';
