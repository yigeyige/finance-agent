-- 将 table-knowledge JSON 导入 governance.ems_table_knowledge（一次性、幂等）
-- 前置：先在「EMS 数据库配置」页创建名为 '隆地-UAT-claim' 的连接实例。

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_allo_record$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"报账单分配给会计（小组/成员）的分单流水记录，记录每次分单的报账单、分配时间与分配对象。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"CLAIM_NO","meaning":"报账单号"},{"name":"ALLO_DATE","meaning":"分配时间"},{"name":"YEAR_MONTH","meaning":"分配年月"},{"name":"GROUP_ID","meaning":"分配的会计组ID"},{"name":"CREW_LDAP","meaning":"分配的会计LDAP账号"},{"name":"ALLO_TYPE","meaning":"分配类型：HAND手工 / JOB自动"}],"relations":["t_account_group(GROUP_ID)","t_account_crew(CREW_LDAP)"],"promptHint":"报账单分单流水，按 CLAIM_NO 关联报账单；CREW_LDAP 存会计账号（LDAP）；ALLO_TYPE 区分手工(HAND)/自动(JOB)分单。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_assign_config$t$, $t$会计分单配置$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_budget$t$, $t$会计科目预算关联$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_calendar$t$, $t$会计工作日设置$t$, $t$DATA$t$, $t${"businessMeaning":"会计工作日历，定义工作日/非工作日及上下班时间，用于会计处理截止时间计算。","keyFields":[{"name":"WORK_ID","meaning":"主键"},{"name":"WORK_DATE","meaning":"日期；为null时表示默认时间设置"},{"name":"WORK_FLAG","meaning":"是否工作日：1是 / 0否；为null时表示默认时间设置"},{"name":"WORK_TITLE","meaning":"标题"},{"name":"WORK_STIME","meaning":"上午上班时间 HH:MM"},{"name":"WORK_ETIME","meaning":"上午下班时间 HH:MM"},{"name":"WORK_STIME2","meaning":"下午上班时间 HH:MM"},{"name":"WORK_ETIME2","meaning":"下午下班时间 HH:MM"}],"relations":[],"promptHint":"判断会计工作日与截止时间；WORK_FLAG=1 为工作日；WORK_DATE 为 null 表示全局默认时间设置。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_close_submit$t$, $t$会计关闭提单配置$t$, $t$DATA$t$, $t${"businessMeaning":"配置关账/关闭提单时间段：是否每月重复、每月开始/结束日、例外人员与例外费用类别。","keyFields":[{"name":"CLOSE_SUBMIT_ID","meaning":"关闭提单编号（主键）"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"OU_CODE","meaning":"归属公司编码（可能逗号分隔多公司）"},{"name":"OU_NAME","meaning":"归属公司名称"},{"name":"START_CLOSE_SUBMIT_DATE","meaning":"开始关闭提单日期"},{"name":"END_CLOSE_SUBMIT_DATE","meaning":"结束关闭提单日期"},{"name":"IS_MONTHLY_REPEAT","meaning":"是否每月重复"},{"name":"START_CLOSE_SUBMIT_DAY","meaning":"每月开始关闭提单日"},{"name":"END_CLOSE_SUBMIT_DAY","meaning":"每月结束关闭提单日"},{"name":"EXCLUDE_USER_CODE","meaning":"例外人员编码（可能逗号分隔）"},{"name":"EXCLUDE_CATEGORY_CODE","meaning":"例外费用类别编码（可能逗号分隔）"},{"name":"IS_ENABLED","meaning":"是否启用"},{"name":"IS_DELETE","meaning":"是否删除"}],"relations":[],"promptHint":"判断某公司某期间是否允许提单；IS_ENABLED 为生效开关；OU_CODE / EXCLUDE_USER_CODE / EXCLUDE_CATEGORY_CODE 可能存逗号分隔的编码集合，匹配时需考虑分隔。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_comment$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_crew$t$, $t$小组成员$t$, $t$DATA$t$, $t${"businessMeaning":"会计小组成员表，记录会计员工所属小组、分工上限、分单优先级及替换人。","keyFields":[{"name":"CREW_ID","meaning":"主键"},{"name":"CREW_EMP_ID","meaning":"会计员工ID"},{"name":"CREW_EMP_CODE","meaning":"会计员工工号"},{"name":"CREW_EMP_NAME","meaning":"会计员工姓名"},{"name":"GROUP_CODE","meaning":"会计小组编码"},{"name":"GROUP_ID","meaning":"会计小组ID"},{"name":"MEMBER_TASKS","meaning":"组员分工上限"},{"name":"PRIORITY_LEVEL","meaning":"分单优先级"},{"name":"CREW_EMP_CODE_REPLACER","meaning":"组员替换者员工编码"},{"name":"CREW_EMP_NAME_REPLACER","meaning":"组员替换者员工姓名"}],"relations":["t_account_group(GROUP_ID/GROUP_CODE)","人员（CREW_EMP_ID/CREW_EMP_CODE）"],"promptHint":"定位会计小组成员；CREW_EMP_CODE 为员工工号；替换人字段（*_REPLACER）用于人员离岗/交接；按 GROUP_ID 关联会计小组。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_crew_factor$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_fms_gldate$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"FMS 总账账期状态，记录各 BU/公司/账簿的账期开关与核算、报账状态。","keyFields":[{"name":"Period_Status_Id","meaning":"主键"},{"name":"Bu_Code","meaning":"BU"},{"name":"Set_Of_Book_Id","meaning":"BOOK ID"},{"name":"Company_Code","meaning":"公司(OU)编码"},{"name":"Company_Name","meaning":"公司(OU)名称"},{"name":"Period_Name","meaning":"账期"},{"name":"Period_Year","meaning":"年份"},{"name":"Period_Month","meaning":"月份"},{"name":"Start_Date","meaning":"开始时间"},{"name":"End_Date","meaning":"结束时间"},{"name":"Closing_Status","meaning":"核算状态"},{"name":"EMS_STATUS","meaning":"报账状态"}],"relations":[],"promptHint":"账期开关：Company_Code 定位公司，Period_Year/Period_Month 定位账期，Closing_Status 核算状态、EMS_STATUS 报账状态。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_fms_voucher_batch$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_fms_voucher_head$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_group$t$, $t$会计分组表$t$, $t$DATA$t$, $t${"businessMeaning":"会计小组定义，记录小组所属部门、组长、组员人数、小组类型（费用会计/复核）。","keyFields":[{"name":"GROUP_ID","meaning":"主键"},{"name":"GROUP_DEPT_ID","meaning":"所属部门ID"},{"name":"GROUP_DEPT_CODE","meaning":"所属部门编码"},{"name":"GROUP_DEPT_NAME","meaning":"所属部门名称"},{"name":"GROUP_CODE","meaning":"会计小组编码"},{"name":"GROUP_NAME","meaning":"会计小组名称"},{"name":"GROUP_LEADER_ID","meaning":"小组组长ID"},{"name":"GROUP_LEADER_CODE","meaning":"小组组长工号"},{"name":"GROUP_LEADER_NAME","meaning":"小组组长名称"},{"name":"GROUP_CREWCOUNT","meaning":"会计组员人数"},{"name":"DEL_FLG","meaning":"删除标志 0未删除/1已删除"},{"name":"GROUP_TYPE","meaning":"小组类型：0费用会计小组 / 1复核小组"},{"name":"MEMBER_TASKS","meaning":"组员分工上限"}],"relations":["t_account_crew(GROUP_ID)","t_account_groupwork(GROUP_ID)"],"promptHint":"定位会计小组：GROUP_CODE/GROUP_NAME 为小组标识；GROUP_TYPE 区分费用会计(0)/复核(1)小组；DEL_FLG=0 未删除；组长工号在 GROUP_LEADER_CODE。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_groupwork$t$, $t$会计小组工作范围$t$, $t$DATA$t$, $t${"businessMeaning":"会计小组与申请组织/费用类别/公司的分单工作范围及加权值映射。","keyFields":[{"name":"WORKSPACE_ID","meaning":"主键"},{"name":"DEPT_ID","meaning":"申请组织ID"},{"name":"DEPT_NAME","meaning":"申请组织名称"},{"name":"EXP_CATEGORY_ID","meaning":"费用类别ID"},{"name":"EXP_CATEGORY_CODE","meaning":"费用类别CODE"},{"name":"EXP_CATEGORY_NAME","meaning":"费用类别名称"},{"name":"GROUP_ID","meaning":"会计小组ID"},{"name":"GROUP_NAME","meaning":"会计小组名称"},{"name":"OU_CODE","meaning":"所属公司编码"},{"name":"OU_NAME","meaning":"所属公司名称"},{"name":"RATE_VALUE","meaning":"加权值"},{"name":"MODIFY_RATE_VALUE","meaning":"调整值"},{"name":"USE_FLAY","meaning":"是否启用（0/1 是/否）"},{"name":"GROUP_TYPE","meaning":"分组类型：0费用会计 / 1复核会计"}],"relations":["t_account_group(GROUP_ID)"],"promptHint":"分单工作范围：按 DEPT_ID + EXP_CATEGORY_CODE + OU_CODE 组合定位会计小组(GROUP_ID)；USE_FLAY 为启用开关；RATE_VALUE/MODIFY_RATE_VALUE 为加权值。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_monthly_closing$t$, $t$会计月结期间设置$t$, $t$DATA$t$, $t${"businessMeaning":"会计月结期间配置，定义各业务线/公司的月结开始与结束日、例外费用类别、加急标记。","keyFields":[{"name":"MONTHLY_CLOSING_ID","meaning":"月结期间编号（主键）"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"BUSINESS_LINE_NAME","meaning":"业务线名称"},{"name":"START_DAY","meaning":"月结开始日期（日）"},{"name":"END_DAY","meaning":"月结结束日期（日）"},{"name":"EXCLUDE_CATEGORY_CODE","meaning":"例外费用类别编码（可能逗号分隔）"},{"name":"EXCLUDE_CATEGORY_NAME","meaning":"例外费用类别名称"},{"name":"COMPANYS_CODE","meaning":"归属公司编码（可能逗号分隔）"},{"name":"COMPANYS_NAME","meaning":"归属公司名称"},{"name":"IS_URGENT_FLAG","meaning":"是否加急 Y是/N否"},{"name":"IS_DELETE","meaning":"是否删除"}],"relations":[],"promptHint":"判断月结期间：BUSINESS_LINE_CODE + COMPANYS_CODE 定位；START_DAY/END_DAY 为每月开始/结束日；COMPANYS_CODE/EXCLUDE_CATEGORY_CODE 可能存逗号分隔编码。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_record$t$, $t$会计(不)在岗记录$t$, $t$DATA$t$, $t${"businessMeaning":"会计在岗/不在岗记录，用于分单时排除不在岗会计。","keyFields":[{"name":"RECORD_ID","meaning":"主键"},{"name":"RECORD_DATE","meaning":"日期"},{"name":"RECORD_STIME","meaning":"(不)在岗开始时间"},{"name":"RECORD_ETIME","meaning":"(不)在岗结束时间"},{"name":"GROUP_CODE","meaning":"会计小组编码"},{"name":"GROUP_ID","meaning":"会计小组ID"},{"name":"CREW_EMP_CODE","meaning":"会计员工工号"},{"name":"CREW_EMP_ID","meaning":"会计员工ID"},{"name":"SETTING_DIFF","meaning":"在岗/不在岗区分：0不在岗设置 / 1在岗设置"},{"name":"GROUP_TYPE","meaning":"分组类型"}],"relations":["t_account_group(GROUP_ID)","t_account_crew(CREW_EMP_CODE)"],"promptHint":"判断会计是否在岗；SETTING_DIFF：0不在岗、1在岗；按 GROUP_ID + CREW_EMP_CODE + 时间区间过滤，分单时排除不在岗会计。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_account_voucher_data$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"报账单/付款单对应的记账凭证数据，记录凭证号、日记账、期间与凭证类型。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"claim_no","meaning":"报账单号"},{"name":"payclaim_no","meaning":"付款申请单号"},{"name":"paybatch_no","meaning":"付款准备批号"},{"name":"pay_line_detail_id","meaning":"付款准备明细编号"},{"name":"head_name","meaning":"日记账名称"},{"name":"voucher_no","meaning":"记账凭证号"},{"name":"batch_name","meaning":"日记账批名"},{"name":"voucher_type","meaning":"凭证类型：1报账单凭证 / 2付款单凭证"},{"name":"vouchered_no","meaning":"记账凭证号（已过账）"},{"name":"import_voucher_batchno","meaning":"导入凭证批号"},{"name":"GL_DATE","meaning":"期间"}],"relations":["报账单（claim_no）","付款申请单（payclaim_no）","付款准备明细（pay_line_detail_id）"],"promptHint":"查报账单/付款单对应凭证：claim_no 关联报账单、payclaim_no 关联付款单；voucher_type 1报账/2付款；voucher_no 为凭证号。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_claim_event_adapt$t$, $t$报账单导入渠道表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_esb_config$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_md_vendor$t$, $t$供应商$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_md_vendor_account$t$, $t$供应商地点银行账户$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_md_vendor_bk_account$t$, $t$供应商银行账户信息表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_md_vendor_site$t$, $t$供应商地址信息$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_payment_flow$t$, $t$支付流水记录$t$, $t$DATA$t$, $t${"businessMeaning":"支付流水，记录报账单/凭证/付款申请行的冻结与实际支付事件及金额。","keyFields":[{"name":"id","meaning":"主键"},{"name":"claim_no","meaning":"报账单号"},{"name":"voucher_id","meaning":"报账单对应凭证ID"},{"name":"payment_plan_line_id","meaning":"凭证对应付款计划ID"},{"name":"ems_pay_detail_id","meaning":"表单付款清单ID"},{"name":"payment_detail_line_id","meaning":"付款申请行ID"},{"name":"type","meaning":"流水类型：1冻结 / 2实际"},{"name":"amount","meaning":"流水金额"},{"name":"event_type","meaning":"事件类型：1创建付款申请/2支付/3拒付/4退票/5付款申请驳回"},{"name":"create_time","meaning":"发生时间"}],"relations":["报账单（claim_no）","付款申请行（payment_detail_line_id）"],"promptHint":"支付流水；type 1冻结/2实际；event_type 1-5 为付款事件；按 claim_no 或 payment_detail_line_id 关联；amount 为流水金额。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_payment_list_detail$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"付款清单明细，记录收款方（对私员工/对公供应商）、收款账户、付款方式、金额、银行与支付状态。含银行账号，敏感。","keyFields":[{"name":"PAYMENT_LIST_DETAIL_ID","meaning":"主键"},{"name":"VOUCHER_ID","meaning":"凭证主键"},{"name":"EMS_PAY_DETAIL_ID","meaning":"EMS表单付款清单ID"},{"name":"CLAIM_PAY_PLAN_ID","meaning":"付款计划ID"},{"name":"CLAIM_NO","meaning":"报账单号"},{"name":"TYPE","meaning":"类型：对私1 / 对公2"},{"name":"PAYMENT_METHOD","meaning":"付款方式（转账、网银等）"},{"name":"PAYMENT_TYPE","meaning":"付款类型（立即付款、账龄付款等）"},{"name":"EMP_ID","meaning":"员工编号"},{"name":"EMP_NAME","meaning":"员工名称"},{"name":"ACCOUNT_NO","meaning":"收款账号（敏感）"},{"name":"VENDOR_CODE","meaning":"供应商编号"},{"name":"VENDOR_SITE_CODE","meaning":"供应商地点编号"},{"name":"PAYMENT_CURRENCY","meaning":"付款币种"},{"name":"PAYMENT_AMOUNT","meaning":"付款金额"},{"name":"PAYMENT_ACCOUNT_NO","meaning":"付款银行账户（敏感）"},{"name":"IS_LOCK","meaning":"是否锁定 Y是/N否"},{"name":"IS_STOP_PAY","meaning":"是否止付 Y是/N否"},{"name":"PAY_BALANCE","meaning":"付款余额"},{"name":"PAID_AMOUNT","meaning":"已支付金额"},{"name":"VENDOR_TYPE","meaning":"供应商类型：员工EMPLOYEE/对公PROVIDER/第三方THIRD"},{"name":"DUE_DATE","meaning":"到期日"}],"relations":["报账单（CLAIM_NO）","t_md_vendor(VENDOR_CODE)","付款计划（CLAIM_PAY_PLAN_ID）"],"promptHint":"付款清单明细；ACCOUNT_NO/PAYMENT_ACCOUNT_NO 为收款/付款银行账号（敏感，禁止整行读取）；TYPE 1对私/2对公；IS_LOCK/IS_STOP_PAY 为锁定/止付开关；按 CLAIM_NO 关联报账单。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_payment_schedule_line$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"付款计划行，记录报账单/凭证的付款计划行号、预计付款时间、金额、付款方式、付款银行与弹性域。","keyFields":[{"name":"PAYMENT_SCHEDULE_LINE_ID","meaning":"付款计划行主键"},{"name":"CLAIM_NO","meaning":"报账单号"},{"name":"VOUCHER_ID","meaning":"凭证主键"},{"name":"CLAIM_PAY_PLAN_ID","meaning":"表单付款计划ID"},{"name":"PAY_PLAN_LINE_NUM","meaning":"付款计划行号"},{"name":"DUE_DATE","meaning":"预计付款时间"},{"name":"GROSS_AMOUNT","meaning":"金额"},{"name":"HOLD_FLAG","meaning":"是否暂挂"},{"name":"PAYMENT_TYPE","meaning":"付款方式"},{"name":"PAYMENT_PRIORITY","meaning":"优先级"},{"name":"BANK_NAME","meaning":"付款银行名称"},{"name":"BANK_ACCOUNT_NUM","meaning":"付款银行账号（敏感）"},{"name":"PROFILE_NAME","meaning":"配置文件"},{"name":"PAYMENT_DOCUMENT_NAME","meaning":"单据"}],"relations":["报账单（CLAIM_NO）","t_payment_list_detail(CLAIM_PAY_PLAN_ID)"],"promptHint":"付款计划行；BANK_ACCOUNT_NUM 为付款银行账号（敏感）；DUE_DATE 预计付款时间；HOLD_FLAG 是否暂挂；按 CLAIM_NO 关联报账单。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_voucher_excel_result$t$, $t$核销池导出报表记录$t$, $t$DATA$t$, $t${"businessMeaning":"核销池导出报表记录，记录导出文件名、路径、工号与导出状态。","keyFields":[{"name":"VOUCHER_EXCEL_RESULT_ID","meaning":"主键"},{"name":"FILE_NAME","meaning":"文件名"},{"name":"FILE_PATH","meaning":"文件路径"},{"name":"FILE_SIZE","meaning":"文件大小"},{"name":"FILE_TYPE","meaning":"文件类型"},{"name":"USER_NO","meaning":"工号"},{"name":"STATUS","meaning":"状态 100导出中 / 200导出成功"},{"name":"PARAMETER","meaning":"参数（JSON）"},{"name":"ERROR_FLAG","meaning":"错误标识"},{"name":"ERROR_MSG","meaning":"错误信息"}],"relations":[],"promptHint":"核销池导出记录；STATUS 100导出中/200成功；USER_NO 为工号；PARAMETER 为导出参数（JSON）。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_voucher_head$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"应付(AP)凭证头，记录报账单生成凭证的供应商、发票、付款、事件适配、科目/会计与复核信息。","keyFields":[{"name":"VOUCHER_ID","meaning":"主键"},{"name":"CLAIM_NO","meaning":"报账单号"},{"name":"claim_id","meaning":"表单ID"},{"name":"VOUCHER_MODULE","meaning":"凭证模块 AP/GL/FA/AR"},{"name":"VOUCHER_IMPORT_ESB_NUM","meaning":"凭证导入流水号"},{"name":"VOUCHER_STATUS","meaning":"凭证状态"},{"name":"IMPORT_FLAG","meaning":"是否导入FMS标识"},{"name":"PAYMENT_GENERATOR_FLAG","meaning":"付款凭证生成规则 0/1/2/3"},{"name":"EVENT_CODE","meaning":"事件编码"},{"name":"EVENT_NAME","meaning":"事件名称"},{"name":"ADAPT_CODE","meaning":"适配器编码"},{"name":"IMPORT_ID","meaning":"AP发票编号"},{"name":"BATCH_NUM","meaning":"AP批次编号"},{"name":"ORG_CODE","meaning":"AP业务实体编码"},{"name":"VENDOR_NUMBER","meaning":"供应商编码"},{"name":"VENDOR_ID","meaning":"供应商主键ID"},{"name":"INVOICE_NUM","meaning":"AP发票编号"},{"name":"FMS_VOUCHER_NUM","meaning":"FMS凭证编号"},{"name":"GL_DATE","meaning":"总账日期"},{"name":"VOUCHER_CURRENCY_CODE","meaning":"凭证币种"},{"name":"PAYMENT_CURRENCY_CODE","meaning":"本位币币种"},{"name":"VOUCHER_AMOUNT","meaning":"凭证金额"},{"name":"CLAIMMAKER","meaning":"应付会计LDAP账号"},{"name":"REVIEW_ACCOUNTANT_ID","meaning":"复核会计员工号"},{"name":"REVIEW_ACCOUNTANT_LDAP","meaning":"复核会计LDAP账号"},{"name":"APPLY_TYPE","meaning":"付款申请类型 1总账/2应付/3清账"},{"name":"CLAIM_APPLY_LDAP","meaning":"报账单申请人LDAP账号"},{"name":"GENERATOR_SOURCE","meaning":"凭证生成来源 CLAIM/PAYMENT"},{"name":"group_Code","meaning":"核算小组code"},{"name":"BU_CODE","meaning":"bu代码"}],"relations":["报账单（CLAIM_NO/claim_id）","t_voucher_line(VOUCHER_ID)","t_md_vendor(VENDOR_NUMBER/VENDOR_ID)"],"promptHint":"AP 凭证头；VOUCHER_ID 主键、CLAIM_NO 关联报账单；VENDOR_NUMBER/VENDOR_ID 定位供应商；GENERATOR_SOURCE 区分报账(CLAIM)/付款(PAYMENT)来源；REVIEW_ACCOUNTANT_LDAP 为复核会计LDAP。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_voucher_info$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_voucher_line$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"凭证行，记录报账单生成凭证的行类型、借贷方向、科目组合(SEGMENT1-10)、金额、采购订单与供应商信息。","keyFields":[{"name":"VOUCHER_LINE_ID","meaning":"凭证行主键"},{"name":"VOUCHER_ID","meaning":"凭证主键"},{"name":"CLAIM_NO","meaning":"报账单号"},{"name":"claim_id","meaning":"表单ID"},{"name":"VOUCHER_LINE_TYPE","meaning":"凭证行类型 ITEM/WRITEOFF/PO/TAX"},{"name":"DR_CR_FLAG","meaning":"借贷方向 DR/CR"},{"name":"LINE_NUM","meaning":"发票行号"},{"name":"LINE_TYPE_LOOKUP_CODE","meaning":"FMS行类型"},{"name":"SEGMENT1","meaning":"账户组合第一段"},{"name":"SEGMENT2","meaning":"账户组合第二段"},{"name":"SEGMENT3","meaning":"账户组合第三段"},{"name":"SEGMENT4","meaning":"账户组合第四段"},{"name":"SEGMENT5","meaning":"账户组合第五段"},{"name":"LINE_AMOUNT","meaning":"行金额"},{"name":"LINE_DESCRIPTION","meaning":"行摘要"},{"name":"PO_NUMBER","meaning":"采购订单编号"},{"name":"VENDOR_CODE","meaning":"供应商编码"},{"name":"VENDOR_SITE_CODE","meaning":"供应商地点"},{"name":"WRITEOFF_OTHER_CODE","meaning":"核销对方编号"},{"name":"RESOURCE_VOUCHER_ID","meaning":"原凭证ID"},{"name":"GL_DATE","meaning":"记账日期"}],"relations":["t_voucher_head(VOUCHER_ID)","报账单（CLAIM_NO/claim_id）","t_md_vendor(VENDOR_CODE)"],"promptHint":"凭证行；VOUCHER_ID 关联凭证头、CLAIM_NO 关联报账单；DR_CR_FLAG 为借贷方向；SEGMENT1-10 为账户组合分段；VOUCHER_LINE_TYPE 区分 ITEM/WRITEOFF/PO/TAX。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_voucher_pool$t$, $t$凭证核销池$t$, $t$DATA$t$, $t${"businessMeaning":"凭证核销池（挂账池），记录可核销凭证行的科目段、供应商、原币/本位币金额、已核销/冻结/余额与核销完成标记。","keyFields":[{"name":"voucher_pool_id","meaning":"主键"},{"name":"VOUCHER_ID","meaning":"凭证ID"},{"name":"VOUCHER_LINE_ID","meaning":"凭证行主键"},{"name":"BU_CODE","meaning":"BU"},{"name":"CLAIM_ID","meaning":"单据ID"},{"name":"CLAIM_NO","meaning":"单据编号"},{"name":"CLAIM_LINE_ID","meaning":"单据行ID"},{"name":"RESOURCE_VOUCHER_ID","meaning":"来源凭证ID"},{"name":"VENDOR_TYPE","meaning":"供应商类型"},{"name":"VENDOR_CODE","meaning":"供应商"},{"name":"CATEGORY_CODE","meaning":"业务大类"},{"name":"SUBCATEGORY_CODE","meaning":"小类"},{"name":"DR_CR_FLAG","meaning":"借贷方向"},{"name":"SEGMENT1","meaning":"机构段值"},{"name":"SEGMENT2","meaning":"成本中心段值"},{"name":"SEGMENT3","meaning":"科目段值"},{"name":"SEGMENT4","meaning":"会计子目"},{"name":"ORIGIN_CURRENCY","meaning":"原币"},{"name":"BASE_CURRENCY","meaning":"本位币"},{"name":"AMOUNT","meaning":"原币金额"},{"name":"ALREADY_WRITE_AMOUNT","meaning":"已核销金额"},{"name":"FREEZE_AMOUNT","meaning":"冻结金额"},{"name":"AVAILABLE_AMOUNT","meaning":"余额"},{"name":"IS_FULL","meaning":"是否核销完成"},{"name":"STATUS","meaning":"状态 100/200"}],"relations":["t_voucher_head(VOUCHER_ID)","t_voucher_line(VOUCHER_LINE_ID)","报账单（CLAIM_NO/CLAIM_ID）"],"promptHint":"凭证核销池；VOUCHER_ID/VOUCHER_LINE_ID 定位凭证行；AMOUNT/ALREADY_WRITE_AMOUNT/FREEZE_AMOUNT/AVAILABLE_AMOUNT 为金额与余额；IS_FULL 标记是否核销完成；按 CLAIM_NO 关联报账单。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$account$t$, $t$t_voucher_pool_detail$t$, $t$凭证核销池-流水$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ge_bytearray$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 引擎二进制资源表（标准引擎表），保存部署的流程定义、流程图等二进制内容。","keyFields":[{"name":"ID_","meaning":"主键"},{"name":"REV_","meaning":"版本号"},{"name":"NAME_","meaning":"资源名称"},{"name":"DEPLOYMENT_ID_","meaning":"部署ID"},{"name":"BYTES_","meaning":"二进制内容（流程图/流程定义）"},{"name":"GENERATED_","meaning":"是否引擎生成"}],"relations":["act_re_deployment(DEPLOYMENT_ID_)"],"promptHint":"引擎资源表（标准表）；NAME_ 为资源名、BYTES_ 存流程图/流程定义二进制；按 DEPLOYMENT_ID_ 关联部署。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ge_property$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 引擎属性表（标准引擎表），保存引擎 schema 版本等属性。","keyFields":[{"name":"NAME_","meaning":"属性名（主键）"},{"name":"VALUE_","meaning":"属性值"},{"name":"REV_","meaning":"版本号"}],"relations":[],"promptHint":"引擎属性表（标准表）；NAME_/VALUE_ 存引擎属性（如 schema.version）；一般业务查询不涉及。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_id_group$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_id_info$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_id_membership$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_id_user$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_procdef_info$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_re_deployment$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 部署表（标准引擎表），记录每次流程部署的名称、KEY 与部署时间。","keyFields":[{"name":"ID_","meaning":"主键"},{"name":"NAME_","meaning":"部署名称"},{"name":"CATEGORY_","meaning":"分类"},{"name":"DEPLOY_TIME_","meaning":"部署时间"},{"name":"KEY_","meaning":"部署KEY"},{"name":"ENGINE_VERSION_","meaning":"引擎版本"}],"relations":["act_re_procdef(DEPLOYMENT_ID_)","act_ge_bytearray(DEPLOYMENT_ID_)"],"promptHint":"流程部署（标准表）；ID_ 为部署ID；按 DEPLOYMENT_ID_ 关联流程定义与资源。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_re_model$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 流程模型表（标准引擎表），记录流程设计模型（BPMN 模型）与其版本。","keyFields":[{"name":"ID_","meaning":"主键"},{"name":"NAME_","meaning":"模型名称"},{"name":"KEY_","meaning":"模型KEY"},{"name":"CATEGORY_","meaning":"分类"},{"name":"VERSION_","meaning":"版本"},{"name":"META_INFO_","meaning":"元信息"},{"name":"DEPLOYMENT_ID_","meaning":"部署ID"}],"relations":["act_re_deployment(DEPLOYMENT_ID_)"],"promptHint":"流程模型（标准表）；KEY_ 为模型KEY、VERSION_ 为版本；模型编辑源在 EDITOR_SOURCE_VALUE_ID_。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_re_procdef$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 流程定义表（标准引擎表），记录已部署流程定义的 KEY、版本、资源与挂起状态。","keyFields":[{"name":"ID_","meaning":"主键"},{"name":"KEY_","meaning":"流程定义KEY"},{"name":"NAME_","meaning":"流程名称"},{"name":"VERSION_","meaning":"版本"},{"name":"DEPLOYMENT_ID_","meaning":"部署ID"},{"name":"RESOURCE_NAME_","meaning":"流程定义资源名"},{"name":"DGRM_RESOURCE_NAME_","meaning":"流程图资源名"},{"name":"DESCRIPTION_","meaning":"描述"},{"name":"SUSPENSION_STATE_","meaning":"挂起状态 1激活/2挂起"},{"name":"HAS_START_FORM_KEY_","meaning":"是否有开始表单"}],"relations":["act_re_deployment(DEPLOYMENT_ID_)"],"promptHint":"流程定义（标准表）；KEY_ + VERSION_ 唯一定位流程版本；SUSPENSION_STATE_ 1激活/2挂起；按 DEPLOYMENT_ID_ 关联部署。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_deadletter_job$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_delegate$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_delegatevar$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_event_subscr$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_execution$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 运行时执行表（标准引擎表），记录流程实例/执行的活动节点、业务键、挂起状态与发起人。","keyFields":[{"name":"ID_","meaning":"主键"},{"name":"PROC_INST_ID_","meaning":"流程实例ID"},{"name":"BUSINESS_KEY_","meaning":"业务键（通常为业务单号）"},{"name":"PROC_DEF_ID_","meaning":"流程定义ID"},{"name":"PARENT_ID_","meaning":"父执行ID"},{"name":"ACT_ID_","meaning":"当前活动节点ID"},{"name":"IS_ACTIVE_","meaning":"是否活跃"},{"name":"SUSPENSION_STATE_","meaning":"挂起状态"},{"name":"ROOT_PROC_INST_ID_","meaning":"根流程实例ID"},{"name":"START_USER_ID_","meaning":"发起人"},{"name":"START_TIME_","meaning":"开始时间"}],"relations":["act_re_procdef(PROC_DEF_ID_)","act_ru_task(EXECUTION_ID_)"],"promptHint":"运行时执行（标准表）；BUSINESS_KEY_ 通常为业务单号，按业务单号定位运行中的流程；ACT_ID_ 为当前活动节点；SUSPENSION_STATE_ 挂起状态。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_identitylink$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 运行时身份关联表（标准引擎表），记录任务/流程实例的候选人或参与人关系。","keyFields":[{"name":"ID_","meaning":"主键"},{"name":"GROUP_ID_","meaning":"群组ID"},{"name":"TYPE_","meaning":"身份类型"},{"name":"USER_ID_","meaning":"用户ID"},{"name":"TASK_ID_","meaning":"任务ID"},{"name":"PROC_INST_ID_","meaning":"流程实例ID"}],"relations":["act_ru_task(TASK_ID_)"],"promptHint":"运行时身份关联（标准表）；USER_ID_/GROUP_ID_ 为候选/参与人；按 TASK_ID_ 关联任务查候选人。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_job$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_suspended_job$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_task$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 运行时任务表（标准引擎表），记录当前待办任务、处理人、认领与到期信息。","keyFields":[{"name":"ID_","meaning":"主键"},{"name":"EXECUTION_ID_","meaning":"执行ID"},{"name":"PROC_INST_ID_","meaning":"流程实例ID"},{"name":"PROC_DEF_ID_","meaning":"流程定义ID"},{"name":"NAME_","meaning":"任务名称"},{"name":"TASK_DEF_KEY_","meaning":"任务定义KEY"},{"name":"OWNER_","meaning":"任务所有者"},{"name":"ASSIGNEE_","meaning":"任务处理人"},{"name":"DELEGATION_","meaning":"委托状态"},{"name":"PRIORITY_","meaning":"优先级"},{"name":"CREATE_TIME_","meaning":"创建时间"},{"name":"DUE_DATE_","meaning":"到期时间"},{"name":"FORM_KEY_","meaning":"表单KEY"},{"name":"CLAIM_TIME_","meaning":"认领时间"},{"name":"SUSPENSION_STATE_","meaning":"挂起状态"}],"relations":["act_ru_execution(EXECUTION_ID_)","act_re_procdef(PROC_DEF_ID_)"],"promptHint":"运行时待办任务（标准表）；ASSIGNEE_ 为当前处理人、OWNER_ 为所有者；按 PROC_INST_ID_ 关联流程实例查当前待办；TASK_DEF_KEY_ 为任务节点标识。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_timer_job$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$act_ru_variable$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"Activiti 运行时变量表（标准引擎表），记录运行中流程实例的变量值。","keyFields":[{"name":"ID_","meaning":"主键"},{"name":"TYPE_","meaning":"变量类型"},{"name":"NAME_","meaning":"变量名"},{"name":"EXECUTION_ID_","meaning":"执行ID"},{"name":"PROC_INST_ID_","meaning":"流程实例ID"},{"name":"TASK_ID_","meaning":"任务ID"},{"name":"LONG_","meaning":"长整型值"},{"name":"TEXT_","meaning":"文本值"},{"name":"DOUBLE_","meaning":"浮点值"}],"relations":["act_ru_execution(EXECUTION_ID_)"],"promptHint":"运行时变量（标准表）；NAME_ 变量名、TEXT_/LONG_/DOUBLE_ 为不同类型值；按 PROC_INST_ID_ 关联流程实例。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$excel_psc_form_proc$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_assignee$t$, $t$流程节点任务处理人$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_call_activity$t$, $t$子流程$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_call_activity_para$t$, $t$子流程参数$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_config$t$, $t$流程节点配置表$t$, $t$DATA$t$, $t${"businessMeaning":"流程节点配置，定义某流程版本的节点类型、表单、处理人/候选人、跳转与多实例等运行配置。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"procdef_id","meaning":"流程定义ID"},{"name":"act_name","meaning":"节点名称（流程集合名称）"},{"name":"act_id","meaning":"流程节点ID"},{"name":"act_type","meaning":"节点类型 start/用户节点/其他"},{"name":"form_key","meaning":"表单URL"},{"name":"is_async","meaning":"是否异步节点"},{"name":"due_date","meaning":"任务超时时间"},{"name":"jump_type","meaning":"0仅相邻处理人身份重复跳过 / 1处理人身份重复跳过"},{"name":"assignee","meaning":"任务处理人"},{"name":"assignee_type","meaning":"任务处理人类型"},{"name":"candidate_user","meaning":"候选人（授权信息）"},{"name":"candidate_user_type","meaning":"候选人类型 0指定人/1部门等"},{"name":"candidate_group","meaning":"候选人群组"},{"name":"default_choose","meaning":"0提交窗口默认选择 / 1需人工选择"},{"name":"del_flag","meaning":"删除标记 0正常/1删除"}],"relations":["psc_act_re_procdef(procdef_id)","psc_act_assignee(act_config_id)","psc_act_sequence_flow(act_config_id)"],"promptHint":"流程节点配置；act_id 为节点标识、act_type 为节点类型；assignee 为处理人、candidate_user/candidate_group 为候选人/群组；按 procdef_id 关联流程定义查节点配置。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_exec_filter$t$, $t$流程节点执行过滤器$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_prco_set$t$, $t$流程集合表$t$, $t$DATA$t$, $t${"businessMeaning":"流程集合定义，将多个流程版本聚合为一个流程集合（流程发起入口）。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"proc_set_name","meaning":"名称（流程集合名称）"},{"name":"proc_set_key","meaning":"流程模型KEY（自动生成UUID）"},{"name":"category_id","meaning":"业务系统流程分类ID"},{"name":"proc_set_type","meaning":"流程类型（区分子流程）"},{"name":"sort","meaning":"排序号"},{"name":"del_flag","meaning":"删除标记 0正常/1删除"}],"relations":["psc_act_prco_set_start(proc_set_key)","psc_formset_procset(proc_set_key)","psc_category(category_id)"],"promptHint":"流程集合；proc_set_key 为流程集合KEY，与表单集绑定(psc_formset_procset) 决定发起入口；category_id 关联流程分类。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_prco_set_start$t$, $t$流程发起授权$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_re_model$t$, $t$流程模型表$t$, $t$DATA$t$, $t${"businessMeaning":"EMS 流程模型，记录流程模型名称、模型KEY与版本。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"name","meaning":"模型名称"},{"name":"procdef_key","meaning":"流程版本的KEY（与流程模型KEY一致）"},{"name":"version","meaning":"排序号"},{"name":"model_id","meaning":"对应的模型ID"},{"name":"del_flag","meaning":"删除标记 0正常/1删除"}],"relations":["psc_act_re_procdef(model_id)"],"promptHint":"流程模型；procdef_key 为流程KEY；model_id 关联流程版本；version 为排序号。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_re_procdef$t$, $t$流程版本表$t$, $t$DATA$t$, $t${"businessMeaning":"EMS 流程版本定义，记录流程版本的名称、KEY、表单URL、分类、版本号与是否运行版本。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"procdef_id","meaning":"流程定义ID"},{"name":"procdef_name","meaning":"名称（流程集合名称）"},{"name":"procdef_key","meaning":"流程版本KEY（与流程模型KEY一致）"},{"name":"form_key","meaning":"外置表单启动URL（节点未配置时采用流程版本配置的URL）"},{"name":"category_id","meaning":"业务系统流程分类ID"},{"name":"version","meaning":"排序号"},{"name":"is_run_version","meaning":"是否运行版本"},{"name":"model_id","meaning":"对应的模型ID"},{"name":"del_flag","meaning":"删除标记 0正常/1删除"}],"relations":["psc_act_config(procdef_id)","psc_act_re_model(model_id)","psc_category(category_id)"],"promptHint":"流程版本；procdef_key 为流程KEY、is_run_version 标记运行版本；procdef_id 被节点配置(psc_act_config)引用；form_key 为默认表单URL。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_sequence_flow$t$, $t$流程出线配置表$t$, $t$DATA$t$, $t${"businessMeaning":"流程节点出线（连线）配置，定义节点间的流转关系、流转条件与默认流。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"seq_flow_id","meaning":"出线ID"},{"name":"procdef_id","meaning":"流程定义ID"},{"name":"name","meaning":"出线名称"},{"name":"type","meaning":"0出线 / 1网关"},{"name":"source_ref","meaning":"起始节点"},{"name":"target_ref","meaning":"目标节点"},{"name":"flow_order","meaning":"线执行顺序（网关具备）"},{"name":"default_flow_id","meaning":"0默认流 / 1非默认流"},{"name":"condition_expression","meaning":"流转条件"},{"name":"default_choose","meaning":"0提交窗口默认选择 / 1需人工选择"}],"relations":["psc_act_config(procdef_id)","psc_act_re_procdef(procdef_id)"],"promptHint":"流程出线；source_ref/target_ref 为起止节点；condition_expression 为流转条件（脚本，高风险，仅定位）；按 procdef_id 关联流程版本。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_act_set_assignee$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"流程节点指定处理人快照，记录运行中流程节点实际指定的处理人（后台指定或用户选择）。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"PROCESSID","meaning":"流程实例ID"},{"name":"RESOURCEID","meaning":"资源ID"},{"name":"ACT_NODE_ID","meaning":"节点ID"},{"name":"ACT_NODE_NAME","meaning":"节点名称"},{"name":"ASSIGNEE","meaning":"处理人"},{"name":"ASSIGNEE_NAME","meaning":"处理人名称"},{"name":"ASSIGN_TYPE","meaning":"指定类型 devOps后台指定 / user前端用户选择"}],"relations":["act_hi_procinst(PROCESSID)"],"promptHint":"节点指定处理人；ASSIGNEE/ASSIGNEE_NAME 为处理人；ASSIGN_TYPE 区分 devOps后台指定/user用户选择；按 PROCESSID 关联流程实例。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_business_unit$t$, $t$业务单位$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_category$t$, $t$树结构表$t$, $t$DATA$t$, $t${"businessMeaning":"通用树结构表，记录流程分类等树形结构的父子关系与排序。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"parent_id","meaning":"父级编号"},{"name":"parent_ids","meaning":"所有父级编号"},{"name":"name","meaning":"名称"},{"name":"sort","meaning":"排序"},{"name":"del_flag","meaning":"删除标记 0正常/1删除"}],"relations":["psc_act_prco_set(category_id)","psc_act_re_procdef(category_id)"],"promptHint":"树结构；parent_id/parent_ids 定义父子关系；name 为节点名称；被流程集合/流程版本以 category_id 引用作为流程分类。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_communication$t$, $t$沟通表$t$, $t$DATA$t$, $t${"businessMeaning":"流程沟通记录，记录流程/任务上的沟通内容、反馈与发起人/沟通人。","keyFields":[{"name":"id","meaning":"id（主键）"},{"name":"process_instance_id","meaning":"流程实例id"},{"name":"task_id","meaning":"任务id"},{"name":"process_type","meaning":"类型"},{"name":"title","meaning":"标题"},{"name":"content","meaning":"内容"},{"name":"reply_content","meaning":"反馈内容"},{"name":"parent_id","meaning":"父id"},{"name":"ldap_id_from","meaning":"发起人"},{"name":"ldap_id_to","meaning":"沟通人"},{"name":"communication_id","meaning":"沟通编号"},{"name":"status","meaning":"状态"}],"relations":["act_hi_procinst(process_instance_id)"],"promptHint":"流程沟通；process_instance_id 关联流程实例；ldap_id_from/ldap_id_to 为发起人/沟通人（LDAP账号）；content/reply_content 为沟通/反馈内容。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_dept$t$, $t$部门表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_employee$t$, $t$人员表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_form$t$, $t$表单版本表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_form_act_config$t$, $t$表单节点配置表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_form_auth$t$, $t$表单授权信息$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_form_button$t$, $t$表单按钮$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_form_proc$t$, $t$表单版本与流程版本的绑定关系表，主要实现表单授权管理等$t$, $t$DATA$t$, $t${"businessMeaning":"表单版本与流程版本的绑定关系，含角色/归属公司/机构单元/公司组等授权维度。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"form_id","meaning":"表单版本ID"},{"name":"procdef_id","meaning":"流程定义ID（流程版本ID）"},{"name":"form_proc_no","meaning":"表单流程编号"},{"name":"role_code","meaning":"角色编码"},{"name":"ou_code","meaning":"归属公司编码"},{"name":"ou_name","meaning":"归属公司名称"},{"name":"ORG_UNIT","meaning":"机构单元"},{"name":"ORG_UNIT_NAME","meaning":"机构单元名称"},{"name":"COMPANY_GROUP","meaning":"公司组字段"}],"relations":["psc_form(form_id)","psc_act_re_procdef(procdef_id)"],"promptHint":"表单-流程绑定；form_id 关联表单版本、procdef_id 关联流程版本；role_code/ou_code/ORG_UNIT/COMPANY_GROUP 为授权维度。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_form_set$t$, $t$表单版本表$t$, $t$DATA$t$, $t${"businessMeaning":"表单集定义，记录表单集名称、当前运行表单、流程集KEY、分类与排序。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"formset_name","meaning":"表单集名称"},{"name":"formset_name_app","meaning":"流程集（移动端）名称"},{"name":"formset_tips","meaning":"tips提示信息（发起入口显示）"},{"name":"formset_icon","meaning":"ICON（发起页面图标）"},{"name":"current_form_id","meaning":"当前运行表单版本"},{"name":"proc_set_key","meaning":"流程集KEY"},{"name":"category_id","meaning":"业务系统流程分类ID"},{"name":"sort","meaning":"排序号"}],"relations":["psc_act_prco_set(proc_set_key)","psc_form(current_form_id)","psc_category(category_id)"],"promptHint":"表单集；proc_set_key 关联流程集；current_form_id 为当前运行表单；用于定位流程发起入口。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_formset_procset$t$, $t$表单集与流程集绑定关系表，基于该关联关系实现流程发起页面，并且提供判断是否有权限发起流程$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_group$t$, $t$群组$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_group_user$t$, $t$群组成员$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_job$t$, $t$岗位表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_proc_notify$t$, $t$流程通知设置表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_procdef_variable_conf$t$, $t$流程环节扩展属性配置表$t$, $t$DATA$t$, $t${"businessMeaning":"流程环节扩展属性配置，定义流程节点的变量参数（名称、值、类型、默认值与是否可空）。","keyFields":[{"name":"id","meaning":"编号（主键）"},{"name":"variable_name","meaning":"参数名"},{"name":"procdef_id","meaning":"流程定义ID（含版本信息）"},{"name":"procdef_name","meaning":"名称（流程集合名称）"},{"name":"act_id","meaning":"流程节点ID"},{"name":"act_name","meaning":"节点名称"},{"name":"variable_value","meaning":"参数值（JSON格式配置信息）"},{"name":"variable_defaultvalue","meaning":"参数默认值"},{"name":"variable_isnull","meaning":"参数是否可空 1是/0否"},{"name":"variable_datatype","meaning":"参数对象类型 String/Date/Boolean/Long/Double"},{"name":"variable_format","meaning":"参数类别（动态变量/固定变量/JSON自定义变量）"},{"name":"scope","meaning":"作用域 global"}],"relations":["psc_act_re_procdef(procdef_id)","psc_act_config(act_id)"],"promptHint":"流程节点扩展变量；variable_name 为参数名、variable_value 为 JSON 配置；按 procdef_id + act_id 定位节点参数。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_rule_category$t$, $t$规则树结构表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_rule_package$t$, $t$规则包表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_tenant_app$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$activiti_ems$t$, $t$psc_tenant_app_privilege$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_register_user$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"用户登录主数据，记录登录账号（USERCODE）、姓名、密码、组织归属与员工关联。","keyFields":[{"name":"USERID","meaning":"用户ID（主键）"},{"name":"USERCODE","meaning":"登录账号（如 ZENGLINGRUI、LILYCHANG）"},{"name":"USERNAME","meaning":"用户姓名"},{"name":"USERPASSWORD","meaning":"密码（敏感，禁止读取）"},{"name":"MOBILE","meaning":"手机号"},{"name":"TYPE","meaning":"用户类型"},{"name":"ORGUNITID","meaning":"组织ID"},{"name":"LOCKED","meaning":"是否锁定"},{"name":"DELETED","meaning":"是否删除 0未删除"},{"name":"ENABLED","meaning":"是否启用"},{"name":"EMAIL","meaning":"邮箱"},{"name":"EMPNO","meaning":"员工工号"},{"name":"GUID","meaning":"全局ID"},{"name":"EFFECTBEGIN","meaning":"生效开始"},{"name":"EFFECTEND","meaning":"生效结束"}],"relations":["sys_user_role(USERID)","sys_user_org_rel(USER_ID)","sys_org(ORGUNITID)"],"promptHint":"人员定位核心表：USERCODE 存登录账号（字符串）、USERID 存数字ID、USERNAME 存姓名；查某人先按 USERCODE 或 USERNAME 定位，再关联 sys_user_role/sys_user_org_rel；USERPASSWORD 禁止读取。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_role$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"系统角色定义，记录角色名称、角色编码、角色类型、所属组织与菜单模板。","keyFields":[{"name":"ROLEID","meaning":"主键UUID"},{"name":"ROLENAME","meaning":"角色名称"},{"name":"ROLECODE","meaning":"角色编码"},{"name":"ROLETYPE","meaning":"角色类型"},{"name":"ORGID","meaning":"组织ID"},{"name":"MENUTEMPLATEID","meaning":"菜单模板ID"},{"name":"ENABLED","meaning":"是否启用 1启用"},{"name":"DELETED","meaning":"是否删除 0未删除"},{"name":"EFFECTBEGIN","meaning":"开始有效期"},{"name":"EFFECTEND","meaning":"结束有效期"}],"relations":["sys_user_role(ROLEID)","sys_role_menu(ROLEID)","sys_org(ORGID)"],"promptHint":"系统角色；ROLECODE 为角色编码、ROLENAME 为名称；与审批环节角色（t_audit_configuration.ROLE_CODE）不是一回事；按 ROLEID 关联 sys_user_role。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_user_role$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"用户与角色的关联关系，定义用户拥有的系统角色。","keyFields":[{"name":"URID","meaning":"主键"},{"name":"ROLEID","meaning":"角色ID"},{"name":"USERID","meaning":"用户ID"},{"name":"ENABLED","meaning":"是否启用"},{"name":"EFFECTBEGIN","meaning":"生效开始"},{"name":"EFFECTEND","meaning":"生效结束"}],"relations":["sys_register_user(USERID)","sys_role(ROLEID)"],"promptHint":"用户-角色关系；USERID 关联用户、ROLEID 关联角色；用于判断用户系统角色（非审批环节角色）。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_user_org_rel$t$, $t$用户组织关系表$t$, $t$DATA$t$, $t${"businessMeaning":"用户与组织的关系，记录用户归属组织（扩展字段存华润组织描述）。","keyFields":[{"name":"REL_ID","meaning":"用户组织关系ID（主键）"},{"name":"USER_ID","meaning":"用户ID"},{"name":"ORG_ID","meaning":"组织ID"},{"name":"EXT1","meaning":"扩展字段（存储华润组织描述）"}],"relations":["sys_register_user(USER_ID)","sys_org(ORG_ID)"],"promptHint":"用户组织关系；USER_ID 关联用户、ORG_ID 关联组织；用于定位人员所属组织。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_org$t$, $t$组织表$t$, $t$DATA$t$, $t${"businessMeaning":"组织定义，记录组织编码/名称、父组织、所属公司、组织管理者与组织路径。","keyFields":[{"name":"ORG_ID","meaning":"组织ID（主键）"},{"name":"PARENT_ID","meaning":"父组织ID"},{"name":"COMPANY_ID","meaning":"所属公司ID"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"ORG_NAME","meaning":"组织名称"},{"name":"ORG_SHORT_NAME","meaning":"简称"},{"name":"ORG_MANAGER","meaning":"组织管理者"},{"name":"ORG_GRADE_CODE","meaning":"组织层级字典项编码"},{"name":"ORG_PATH","meaning":"组织全路径"},{"name":"ENABLED","meaning":"是否启用"},{"name":"EFFECT_BEGIN","meaning":"起效日期"},{"name":"EFFECT_END","meaning":"失效日期"}],"relations":["sys_company(COMPANY_ID)","sys_user_org_rel(ORG_ID)","自身(PARENT_ID)"],"promptHint":"组织；ORG_CODE/ORG_NAME 为组织标识；PARENT_ID/ORG_PATH 定义层级；COMPANY_ID 关联公司；ORG_MANAGER 为组织管理者。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_company$t$, $t$公司表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_audit_configuration$t$, $t$审核人配置$t$, $t$DATA$t$, $t${"businessMeaning":"审核人配置，定义某组织/业务线/公司/大类下的审批环节角色由谁审核（EMP_IDS 存登录账号 USERCODE）。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"ORG_CODE","meaning":"部门编码"},{"name":"ORG_NAME","meaning":"部门名称"},{"name":"ROLE_CODE","meaning":"审批环节角色编码（如 ZCGLY 资产管理员，非系统角色）"},{"name":"ROLE_NAME","meaning":"审批环节角色名称"},{"name":"EMP_IDS","meaning":"审核人ID（存登录账号 USERCODE 字符串，如 ZENGLINGRUI）"},{"name":"EMP_NAMES","meaning":"审核人姓名（逗号分隔）"},{"name":"CATEGORY_CODE","meaning":"业务大类编码（可能逗号分隔）"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"BUSINESS_LINE_NAME","meaning":"业务线名称"},{"name":"COMPANY_CODE","meaning":"归属公司编码"},{"name":"COMPANY_NAME","meaning":"归属公司名称"},{"name":"BU_CODE","meaning":"BU"},{"name":"TYPE","meaning":"类型"},{"name":"ASSET_CLASS","meaning":"资产类型"},{"name":"IS_SUPPORT_ORG_DRAFT","meaning":"是否支持按组织起草"},{"name":"IS_DELETE","meaning":"删除标识 0未删除/1已删除"}],"relations":["sys_register_user(EMP_IDS→USERCODE)"],"promptHint":"审批人配置：EMP_IDS 存登录账号 USERCODE（字符串，非数字 USERID）、EMP_NAMES 存姓名；查某人审批配置用 USERCODE 或姓名匹配 EMP_IDS/EMP_NAMES；ROLE_CODE 是审批环节角色（如 ZCGLY），不要用 sys_user_role 的系统角色编码匹配；按 ORG_CODE/CATEGORY_CODE/COMPANY_CODE 过滤。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_audit_amount_configuration$t$, $t$审批限额表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_approval_rules_head$t$, $t$审批规则配置头表$t$, $t$DATA$t$, $t${"businessMeaning":"审批规则配置头，定义流程在大类/业务线/组织/地区下的分支条件（报账金额、付款金额）。","keyFields":[{"name":"RULES_HEAD_ID","meaning":"主键"},{"name":"PROCESS_ID","meaning":"流程标识"},{"name":"PROCESS_NAME","meaning":"流程名称"},{"name":"PROCESS_VERSION","meaning":"流程版本"},{"name":"CATEGORY_CODE","meaning":"大类编码"},{"name":"CATEGORY_NAME","meaning":"大类名称"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"ORG_TYPE_CODE","meaning":"组织类型编码"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"ORG_NAME","meaning":"组织名称"},{"name":"WORKFLOW_LINE_KEY","meaning":"分支条件Key（报账单金额）"},{"name":"WORKFLOW_LINE_KEY1","meaning":"分支条件Key（付款金额）"},{"name":"OU_CODE","meaning":"归属公司编码"},{"name":"AREA_VALUE","meaning":"适用地区"},{"name":"IS_DELETE","meaning":"是否删除 0否/1是"}],"relations":["t_approval_rules_line(RULES_HEAD_ID)","流程（PROCESS_ID）"],"promptHint":"审批规则头；RULES_HEAD_ID 关联行表；按 CATEGORY_CODE/ORG_CODE/PROCESS_ID 定位规则；WORKFLOW_LINE_KEY/KEY1 为报账/付款金额分支条件。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_approval_rules_line$t$, $t$审批规则配置行表$t$, $t$DATA$t$, $t${"businessMeaning":"审批规则配置行，定义分支条件值对应的报账/付款金额区间、地区与币种。","keyFields":[{"name":"RULES_LINE_ID","meaning":"行表主键"},{"name":"RULES_HEAD_ID","meaning":"头表主键"},{"name":"WORKFLOW_LINE_VALUE","meaning":"分支条件value"},{"name":"APPLY_AMOUNT_MIN","meaning":"报账最小金额"},{"name":"APPLY_AMOUNT_MAX","meaning":"报账最大金额"},{"name":"PAY_AMOUNT_MIN","meaning":"付款最小金额"},{"name":"PAY_AMOUNT_MAX","meaning":"付款最大金额"},{"name":"AREA_VALUE","meaning":"适用地区"},{"name":"CURRENCY","meaning":"币种"}],"relations":["t_approval_rules_head(RULES_HEAD_ID)"],"promptHint":"审批规则行；RULES_HEAD_ID 关联头表；WORKFLOW_LINE_VALUE 为分支值；APPLY/PAY_AMOUNT_MIN/MAX 为金额区间。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_approval_delegation$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"审批委托/代理配置，记录委托人、代理人的组织/职务、委托时间、金额区间、大类与流程范围。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"ORIGIN_USER_CODE","meaning":"委托人LDAP账号"},{"name":"ORIGIN_USER_NAME","meaning":"委托人姓名"},{"name":"ORIGIN_ORG_ID","meaning":"委托人组织ID"},{"name":"ORIGIN_POSITION_ID","meaning":"委托人职务ID"},{"name":"DELEGATE_USER_CODE","meaning":"代理人LDAP账号"},{"name":"DELEGATE_USER_NAME","meaning":"代理人姓名"},{"name":"DELEGATE_ORG_ID","meaning":"代理人组织ID"},{"name":"START_TIME","meaning":"委托开始时间"},{"name":"END_TIME","meaning":"委托结束时间"},{"name":"MIN_APPLY_AMOUNT","meaning":"最小报账金额"},{"name":"MAX_APPLY_AMOUNT","meaning":"最大报账金额"},{"name":"CATEGORY_CODE","meaning":"大类编码（可能逗号分隔）"},{"name":"PROCDEF_KEY","meaning":"流程集key"},{"name":"ACT_ID","meaning":"节点id"},{"name":"is_del","meaning":"是否删除 0删除/1未删除"}],"relations":["sys_register_user(ORIGIN_USER_CODE/DELEGATE_USER_CODE→USERCODE)"],"promptHint":"审批委托；ORIGIN_USER_CODE 委托人、DELEGATE_USER_CODE 代理人（均 LDAP 账号）；按 START_TIME/END_TIME 判断是否生效；CATEGORY_CODE 可能逗号分隔；PROCDEF_KEY/ACT_ID 限定流程节点。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_category$t$, $t$业务大类（费用类别）$t$, $t$DATA$t$, $t${"businessMeaning":"业务大类（费用类别）定义，记录费用类别层级、模板、对公/对私科目、导入适配器与是否允许报账。","keyFields":[{"name":"CATEGORY_CODE","meaning":"费用类别编码（主键）"},{"name":"CATEGORY_LEVEL","meaning":"费用类别级别"},{"name":"PARENT_CODE","meaning":"上级费用类别编码"},{"name":"FULL_PATH","meaning":"费用类别全路径"},{"name":"IS_VALIDITY","meaning":"是否启用"},{"name":"REIMBURSEMENT_ALLOWED","meaning":"是否允许报账 0否/1是"},{"name":"PUB_ACCOUNT_ITEM_CODE","meaning":"对公会计科目编码"},{"name":"PRI_ACCOUNT_ITEM_CODE","meaning":"对私会计科目编码"},{"name":"ADAPT_ID","meaning":"导入适配器ID"},{"name":"IS_ENTRUST_PAYMENT","meaning":"是否启用委托付款"},{"name":"IS_MERGE_PAY","meaning":"是否合并生成付款凭证"},{"name":"PUBLIC_OR_PRIVATE","meaning":"对公/对私表单 Y对公/N对私"},{"name":"BUSINESS_CYCLE_CODE","meaning":"业务循环编码"}],"relations":["t_subcategory（子类）","t_category_cfg","t_md_account(PUB/PRI_ACCOUNT_ITEM_CODE)"],"promptHint":"业务大类；CATEGORY_CODE 为费用类别编码；PARENT_CODE/FULL_PATH 定义层级；REIMBURSEMENT_ALLOWED 是否允许报账；对公/对私科目分别存 PUB/PRI_ACCOUNT_ITEM_CODE。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_subcategory$t$, $t$业务小类表$t$, $t$DATA$t$, $t${"businessMeaning":"业务小类定义，记录费用小类编码、排序与启用状态。","keyFields":[{"name":"SUBCATEGORY_CODE","meaning":"费用小类编码（主键）"},{"name":"DISPLAY_ORDER","meaning":"排序字段"},{"name":"IS_DELETE","meaning":"是否删除"},{"name":"VALID","meaning":"是否启用 1启用"}],"relations":["t_category_rel(SUBCATEGORY_CODE)","t_subcategory_cfg"],"promptHint":"业务小类；SUBCATEGORY_CODE 为小类编码；与业务大类通过 t_category_rel 建立多对多关系。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_account$t$, $t$会计科目表$t$, $t$DATA$t$, $t${"businessMeaning":"会计科目定义，记录科目编码、层级、属性、税率、过账与有效性。","keyFields":[{"name":"ACCOUNT_ID","meaning":"主键"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"ACCOUNT_ITEM_CODE","meaning":"会计科目编码"},{"name":"ACCOUNT_LEVEL","meaning":"层级"},{"name":"ITEM_PROPERTY","meaning":"科目属性 资产/负债/费用/收入/损益"},{"name":"IS_POST","meaning":"是否允许过账 N否/Y是"},{"name":"RATE","meaning":"存储税率"},{"name":"IS_PARENT_VALUE","meaning":"是否父值"},{"name":"VALID","meaning":"有效性 N禁用/Y启用"},{"name":"EFFECTIVE_DATE_START","meaning":"有效日期起"},{"name":"EFFECTIVE_DATE_END","meaning":"有效日期止"}],"relations":["t_category(PUB/PRI_ACCOUNT_ITEM_CODE)"],"promptHint":"会计科目；ACCOUNT_ITEM_CODE 为科目编码；ITEM_PROPERTY 为科目属性（费用/资产等）；VALID 为有效性；按 BU_CODE 区分。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_coa$t$, $t$COA机构实体$t$, $t$DATA$t$, $t${"businessMeaning":"COA 科目结构（弹性域段值），定义产品/地区/专用/关联方/备用等段的值与层级。","keyFields":[{"name":"coa_id","meaning":"主键"},{"name":"bu_code","meaning":"BU编码"},{"name":"coa_type","meaning":"COA类型 1产品/2地区/3专用/4关联方/5备用"},{"name":"coa_code","meaning":"COA编码"},{"name":"coa_level","meaning":"层级"},{"name":"qualifier","meaning":"限定词"},{"name":"is_validity","meaning":"ERP是否有效"},{"name":"ems_is_validity","meaning":"EMS是否有效"},{"name":"attribute2","meaning":"扩展字段2（成本中心）"}],"relations":["t_md_coa_lang(coa_id)"],"promptHint":"COA 段值；coa_type 区分段类型（1产品/2地区/3专用/4关联方/5备用）；coa_code 为段值编码；attribute2 关联成本中心。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_company$t$, $t$机构$t$, $t$DATA$t$, $t${"businessMeaning":"机构（核算机构）定义，记录机构编码、层级、有效性与核算版本。","keyFields":[{"name":"BU_CODE","meaning":"BU编码（联合主键）"},{"name":"COMPANY_CODE","meaning":"机构编码（联合主键）"},{"name":"LEVEL","meaning":"层级"},{"name":"VALID","meaning":"有效性 N禁用/Y启用"},{"name":"ENABLED","meaning":"EMS有效性"},{"name":"EFFECTIVE_FROM","meaning":"有效起始日期"},{"name":"EFFECTIVE_UNTIL","meaning":"有效结束日期"},{"name":"IS_PARENT_VALUE","meaning":"是否父值"},{"name":"FMS_VERSION","meaning":"核算版本"}],"relations":["t_md_company_mapping(COMPANY_CODE)","t_md_company_lang"],"promptHint":"机构（核算机构）；COMPANY_CODE 为机构编码；BU_CODE + COMPANY_CODE 为联合主键；VALID/ENABLED 为有效性。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_emp$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"员工主数据（HR 同步），记录员工 ID、用户 ID、中英文名、证件、联系方式、学历与法人公司。","keyFields":[{"name":"EMP_ID","meaning":"员工ID（主键）"},{"name":"OPRID","meaning":"用户ID"},{"name":"CHN_NAME","meaning":"中文姓名"},{"name":"ENG_NAME","meaning":"英文名"},{"name":"SEX","meaning":"性别"},{"name":"CERT_ID","meaning":"身份证号（敏感）"},{"name":"MOBILE_PHONE","meaning":"移动电话"},{"name":"EMAIL_ADDR","meaning":"电子邮件地址"},{"name":"CRC_START_DT","meaning":"任职开始时间"},{"name":"CRC_COMPANYID","meaning":"法人公司"},{"name":"CRC_COMP_DESCR","meaning":"法人公司描述"}],"relations":["sys_emp_job(EMPLID)","sys_register_user(OPRID)"],"promptHint":"员工主数据；EMP_ID 员工ID、OPRID 关联登录用户ID；CERT_ID 身份证号（敏感，禁止整行读取）；CHN_NAME 中文姓名。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_emp_job$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"员工岗位/任职记录，记录员工所属业务单位/公司/部门/成本中心、职位、上级、主管及对私付款收款账号。","keyFields":[{"name":"EMPLID","meaning":"员工ID（联合主键）"},{"name":"EMPL_RCD","meaning":"雇用记录编号（联合主键）"},{"name":"EMPL_STATUS","meaning":"薪资状态"},{"name":"HR_STATUS","meaning":"HR状态"},{"name":"BUSINESS_UNIT","meaning":"业务单位"},{"name":"COMPANY","meaning":"公司"},{"name":"DEPTID","meaning":"部门编码"},{"name":"CRC_COST_CENTER","meaning":"成本中心"},{"name":"POSITION_NBR","meaning":"职位号码"},{"name":"JOBCODE","meaning":"职务代码"},{"name":"SUPERVISOR_ID","meaning":"主管ID"},{"name":"SUPERVISOR_NAME","meaning":"主管姓名"},{"name":"MANAGER_ID","meaning":"管理员ID"},{"name":"MANAGER_NAME","meaning":"管理员姓名"},{"name":"SUPV_LVL_ID","meaning":"职级"},{"name":"TERMINATION_DT","meaning":"离职日期"},{"name":"OU_CODE","meaning":"OU编码"},{"name":"OU_NAME","meaning":"OU名称"},{"name":"CLAIM_RECEIVALES_ACCOUNT","meaning":"对私付款收款账号（敏感）"},{"name":"vendor_code","meaning":"供应商编号"}],"relations":["sys_emp(EMPLID)","sys_org(DEPTID)","t_md_costcenter(CRC_COST_CENTER)"],"promptHint":"员工任职；EMPLID+EMPL_RCD 定位一条任职记录；DEPTID 部门、SUPERVISOR_ID/NAME 主管、MANAGER_ID/NAME 管理员；CLAIM_RECEIVALES_ACCOUNT 为对私付款收款账号（敏感）。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_emp_bankacct$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"员工银行账户（HR 同步），记录员工的银行账号、开户行、币种与是否主要账户。含银行账号，敏感。","keyFields":[{"name":"EMPLID","meaning":"员工ID（联合主键）"},{"name":"ACCOUNT_ID","meaning":"账户ID（联合主键）"},{"name":"BUSINESS_UNIT","meaning":"业务单位（联合主键）"},{"name":"EFF_STATUS","meaning":"生效状态 A有效/I无效"},{"name":"ACCOUNT_TYPE_PYE","meaning":"账户类型"},{"name":"BANK_CD","meaning":"银行ID"},{"name":"CRC_BANK_NM","meaning":"银行名称"},{"name":"ACCOUNT_EC_ID","meaning":"银行账号（敏感）"},{"name":"ACCOUNT_NAME","meaning":"账户名称"},{"name":"CURRENCY_CD","meaning":"货币代码"},{"name":"IS_MAIN","meaning":"是否主要 Y是/N否"},{"name":"BANK_ID","meaning":"银行编码"},{"name":"cbank_code","meaning":"人行行号"}],"relations":["sys_emp(EMPLID)"],"promptHint":"员工银行账户；ACCOUNT_EC_ID 为银行账号（敏感，禁止整行读取）；EMPLID 关联员工；IS_MAIN 标记主要账户；EFF_STATUS 为有效性。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_role_menu$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"角色与菜单的关系，定义角色可见的菜单权限。","keyFields":[{"name":"RMID","meaning":"主键"},{"name":"ROLEID","meaning":"角色ID"},{"name":"MENUID","meaning":"菜单ID"}],"relations":["sys_role(ROLEID)","sys_menu(MENUID)"],"promptHint":"角色-菜单关系；ROLEID 关联角色、MENUID 关联菜单；用于判断角色可见菜单。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_org_ext$t$, $t$组织扩展表$t$, $t$DATA$t$, $t${"businessMeaning":"组织扩展信息，记录组织的核算 BU、业务线、部门类型、核算机构、成本中心与产品线。","keyFields":[{"name":"ORG_CODE","meaning":"SYS_ORG表的主键（组织ID）"},{"name":"BU_CODE","meaning":"核算BU编码"},{"name":"BUSINESS_LINE","meaning":"业务线"},{"name":"ORG_TYPE","meaning":"部门类型 1管理/2销售/3制造"},{"name":"COMPANY_CODE","meaning":"核算机构"},{"name":"COST_CENTER_CODE","meaning":"成本中心编码"},{"name":"COST_CENTER_NAME","meaning":"成本中心名称"},{"name":"COST_CENTER_ATTR","meaning":"成本中心属性 1管理/2销售"},{"name":"IS_CROSS_COMP_PAY","meaning":"是否跨公司报账"},{"name":"ORG_LEVEL","meaning":"组织层级"},{"name":"PRODUCT_LINE_CODE","meaning":"产品线code（COA产品）"},{"name":"IS_DELETE","meaning":"删除标识"}],"relations":["sys_org(ORG_CODE)","t_md_company(COMPANY_CODE)"],"promptHint":"组织扩展；ORG_CODE 关联组织；BU_CODE/COMPANY_CODE/COST_CENTER_CODE 定位核算 BU/机构/成本中心；ORG_TYPE 部门类型。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_category_rel$t$, $t$业务大类和业务小类多对多的关联关系表$t$, $t$DATA$t$, $t${"businessMeaning":"业务大类与业务小类的多对多关联关系。","keyFields":[{"name":"CATEGORY_CODE","meaning":"大类编码（联合主键）"},{"name":"SUBCATEGORY_CODE","meaning":"对应小类编码（联合主键）"}],"relations":["t_category(CATEGORY_CODE)","t_subcategory(SUBCATEGORY_CODE)"],"promptHint":"大类-小类关联；CATEGORY_CODE 关联业务大类、SUBCATEGORY_CODE 关联业务小类；用于确定某大类下可选的小类。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_category_cfg$t$, $t$业务大类配置表$t$, $t$DATA$t$, $t${"businessMeaning":"业务大类配置，记录大类下的配置项（编码/名称/值）与机构单元。","keyFields":[{"name":"CATEGORY_CFG_ID","meaning":"主键"},{"name":"CATEGORY_CFG_CODE","meaning":"费用类别配置编码"},{"name":"CATEGORY_CODE","meaning":"业务大类编码"},{"name":"CATEGORY_CFG_VALUE","meaning":"费用类别配置值"},{"name":"CATEGORY_CFG_NAME","meaning":"大类配置名称"},{"name":"VALID","meaning":"是否启用"},{"name":"ORG_UNIT","meaning":"机构单元"}],"relations":["t_category(CATEGORY_CODE)"],"promptHint":"业务大类配置；CATEGORY_CFG_CODE/NAME/VALUE 为配置项；按 CATEGORY_CODE 关联大类。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_category_org$t$, $t$业务大类关联组织$t$, $t$DATA$t$, $t${"businessMeaning":"业务大类与组织/业务线/机构的关联，记录大类在某组织下的角色与排除角色。","keyFields":[{"name":"CATEGORY_ORG_ID","meaning":"主键"},{"name":"CATEGORY_CODE","meaning":"大类代码"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"ORG_TYPE_CODE","meaning":"组织类型编码"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"FULL_PATH","meaning":"组织全路径"},{"name":"ROLE_CODE","meaning":"角色编码"},{"name":"ROLE_NAME","meaning":"角色名称"},{"name":"OU_CODE","meaning":"机构编码"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"EXCLUDE_ROLE_CODE","meaning":"排除角色编码"},{"name":"IS_DELETE","meaning":"是否删除 0未删除/1已删除"}],"relations":["t_category(CATEGORY_CODE)","sys_org(ORG_CODE)"],"promptHint":"业务大类-组织关联；CATEGORY_CODE 大类、ORG_CODE 组织、ROLE_CODE/EXCLUDE_ROLE_CODE 为角色/排除角色；用于确定某组织某大类适用角色。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_subcategory_cfg$t$, $t$业务小类配置表$t$, $t$DATA$t$, $t${"businessMeaning":"业务小类配置，记录小类下的配置项（编码/名称/值）。","keyFields":[{"name":"SUBCATEGORY_CFG_CODE","meaning":"配置编码（联合主键）"},{"name":"SUBCATEGORY_CODE","meaning":"业务小类编码（联合主键）"},{"name":"SUBCATEGORY_CFG_NAME","meaning":"小类配置名称"},{"name":"SUBCATEGORY_CFG_VALUE","meaning":"小类配置值"},{"name":"IS_DELETE","meaning":"是否删除"},{"name":"VALID","meaning":"是否启用 1启用"}],"relations":["t_subcategory(SUBCATEGORY_CODE)"],"promptHint":"业务小类配置；SUBCATEGORY_CFG_CODE/NAME/VALUE 为配置项；按 SUBCATEGORY_CODE 关联小类。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_debit_account_item$t$, $t$业务小类借方科目表$t$, $t$DATA$t$, $t${"businessMeaning":"业务小类借方科目映射，记录小类在 BU/成本中心/业务线/机构下对应的借方会计科目、子科目、贷方科目与预算科目。","keyFields":[{"name":"DEBIT_ACCOUNT_ID","meaning":"主键"},{"name":"SUBCATEGORY_CODE","meaning":"业务小类编码"},{"name":"BU_CODE","meaning":"所属BU"},{"name":"COST_CENTER_PROPERTY","meaning":"成本中心属性"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线"},{"name":"ACCOUNT_ITEM_CODE","meaning":"借方会计科目编码"},{"name":"ACCOUNT_ITEM_NAME","meaning":"借方会计科目名称"},{"name":"SUB_ACCOUNT_ITEM_CODE","meaning":"子会计科目编码"},{"name":"CR_ACCOUNT_ITEM_CODE","meaning":"贷方科目编码"},{"name":"BUDGET_SUBJECT","meaning":"预算科目编码"},{"name":"OU_CODE","meaning":"所属公司编码"},{"name":"INCRE_MOVE","meaning":"增减变动"},{"name":"DEBIT_VENDOR_CODE","meaning":"借方供应商编码"},{"name":"DEBIT_TRX_PARTY","meaning":"借方交易对象"},{"name":"IS_DELETE","meaning":"是否删除"}],"relations":["t_subcategory(SUBCATEGORY_CODE)","t_md_account(ACCOUNT_ID)"],"promptHint":"小类借方科目；SUBCATEGORY_CODE 小类 + BU_CODE/COST_CENTER_PROPERTY/BUSINESS_LINE_CODE 定位科目；ACCOUNT_ITEM_CODE/CR_ACCOUNT_ITEM_CODE 为借/贷方科目；BUDGET_SUBJECT 为预算科目。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_org_manger$t$, $t$部门管理者$t$, $t$DATA$t$, $t${"businessMeaning":"部门管理者配置，记录部门的经理/副经理/总监及其工号、LDAP。","keyFields":[{"name":"id","meaning":"主键"},{"name":"org_id","meaning":"部门ID"},{"name":"org_name","meaning":"部门名称"},{"name":"manager_type","meaning":"管理者类型 DEPARTMENT_MANAGER部门经理/DEPUTY_DEPARTMENT_MANAGER部门副经理/DEPARTMENT_DIRECTOR部门总监"},{"name":"emp_no","meaning":"工号"},{"name":"emp_name","meaning":"姓名"},{"name":"ldap","meaning":"LDAP账号"}],"relations":["sys_org(org_id)","sys_register_user(ldap→USERCODE)"],"promptHint":"部门管理者；manager_type 区分部门经理/副经理/总监；emp_no 工号、ldap 登录账号；按 org_id 定位部门管理者（审批人）。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_org_process_template$t$, $t$组织流程对应模板表$t$, $t$DATA$t$, $t${"businessMeaning":"组织与流程模板的对应关系，记录业务线/组织/角色下的流程模板、审批节点与取值规则。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"BUSINESS","meaning":"业务线"},{"name":"BUSINESS_NAME","meaning":"业务线名称"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"ORG_NAME","meaning":"组织名称"},{"name":"ROLE","meaning":"角色"},{"name":"ROLE_NAME","meaning":"角色名称"},{"name":"PROCESS_TEMPLATE_CODE","meaning":"流程模板编码"},{"name":"PROCESS_TEMPLATE","meaning":"流程模板"},{"name":"APPROVAL_NODE","meaning":"审批节点"},{"name":"NODE_RULE","meaning":"取值规则"},{"name":"START_AMOUNT","meaning":"起始金额"},{"name":"END_AMOUNT","meaning":"结束金额"},{"name":"COMPANY_CODE","meaning":"公司编码"},{"name":"COMPANY_NAME","meaning":"公司名称"}],"relations":["sys_org(ORG_CODE)"],"promptHint":"组织-流程模板对应；按 BUSINESS/ORG_CODE/ROLE 定位流程模板；APPROVAL_NODE 审批节点、NODE_RULE 取值规则；START/END_AMOUNT 为金额区间。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_apply_template$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"报账申请模板配置，记录大类/机构/公司在某机构单元与公司组下使用的模板编码。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"CATEGORY_CODE","meaning":"大类编码"},{"name":"ORG_UNIT","meaning":"机构单元"},{"name":"ORG_UNIT_NAME","meaning":"机构单元名称"},{"name":"COMPANY_GROUP","meaning":"公司组"},{"name":"OU_CODE","meaning":"OU编码"},{"name":"TP_CODE","meaning":"模板编码"},{"name":"TP_NAME","meaning":"模板名称"},{"name":"IS_DELETE","meaning":"是否删除"}],"relations":["t_category(CATEGORY_CODE)"],"promptHint":"报账申请模板；按 CATEGORY_CODE + ORG_UNIT/OU_CODE/COMPANY_GROUP 定位模板；TP_CODE/TP_NAME 为模板编码/名称。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_applyclaim_permission$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"代报权限申请记录，记录权限申请人、岗位、上级与关联报账单。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"APPLY_EMP_NO","meaning":"权限申请人工号"},{"name":"APPLY_EMP_NAME","meaning":"权限申请人名称"},{"name":"OU_CODE","meaning":"归属公司编码"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"JOB_CODE","meaning":"岗位编码"},{"name":"PARENT_ORG_NO","meaning":"部门上级负责人工号"},{"name":"DIRECT_NO","meaning":"直接上级工号"},{"name":"CLAIM_NO","meaning":"关联报账单号"},{"name":"IS_DELETE","meaning":"是否删除 N否/Y是"}],"relations":["报账单（CLAIM_NO）","sys_org(ORG_CODE)"],"promptHint":"代报权限申请；APPLY_EMP_NO 申请人、DIRECT_NO 直接上级、PARENT_ORG_NO 部门上级负责人；按 CLAIM_NO 关联报账单。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_approve_point_item$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"审核要点配置，记录业务小类在某 BU/业务线下的审核要点内容与显示环节。","keyFields":[{"name":"APPROVE_POINT_ID","meaning":"主键"},{"name":"SUBCATEGORY_CODE","meaning":"业务小类编码"},{"name":"BU_CODE","meaning":"所属BU"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线"},{"name":"APPROVE_POINT_CONTENT","meaning":"审核要点内容"},{"name":"VIEW_NODES","meaning":"显示环节"},{"name":"ENABLED_FLAG","meaning":"有效性 Y是/N否"}],"relations":["t_subcategory(SUBCATEGORY_CODE)"],"promptHint":"审核要点；SUBCATEGORY_CODE 小类 + BU_CODE/业务线 定位审核要点；APPROVE_POINT_CONTENT 为要点内容、VIEW_NODES 为显示环节。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_costcenter$t$, $t$成本中心$t$, $t$DATA$t$, $t${"businessMeaning":"成本中心定义，记录成本中心编码、所属机构、属性、过账与有效性。","keyFields":[{"name":"COSTCENTER_ID","meaning":"主键"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"COSTCENTER_CODE","meaning":"成本中心编码"},{"name":"COST_CENTER_PROPERTY","meaning":"成本中心属性"},{"name":"IS_PARENT_VALUE","meaning":"是否父值"},{"name":"COMPANY_CODE","meaning":"机构编码"},{"name":"IS_POSTING","meaning":"是否过账"},{"name":"IS_VALIDITY","meaning":"FMS是否有效"},{"name":"EMS_IS_VALIDITY","meaning":"EMS是否有效"}],"relations":["t_md_company(COMPANY_CODE)","t_debit_account_item(COST_CENTER_PROPERTY)"],"promptHint":"成本中心；COSTCENTER_CODE 为成本中心编码；COMPANY_CODE 关联机构；COST_CENTER_PROPERTY 为属性（1管理/2销售等）；EMS_IS_VALIDITY 为 EMS 有效性。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_bank$t$, $t$付款银行账号$t$, $t$DATA$t$, $t${"businessMeaning":"付款银行账号配置，记录公司付款的银行、账号、币种、付款方式与是否默认。含银行账号，敏感。","keyFields":[{"name":"BANK_ID","meaning":"主键"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"COMP_CODE","meaning":"OU编码"},{"name":"COMP_NAME","meaning":"OU名称"},{"name":"BANK_NAME","meaning":"银行名称"},{"name":"BANK_BRANCH_NAME","meaning":"分行名称"},{"name":"BANK_ACCOUNT_NAME","meaning":"银行账户名"},{"name":"BANK_ACCOUNT_NUM","meaning":"银行账号（敏感）"},{"name":"BANK_CURRENCY_CODE","meaning":"币种"},{"name":"IS_VALIDITY","meaning":"ERP有效性"},{"name":"IS_EMS_VALIDTY","meaning":"EMS有效性"},{"name":"DEFAULT_PAY","meaning":"付款默认"},{"name":"PAY_MODE","meaning":"付款方式"},{"name":"IS_PAY_SALARY_BANK","meaning":"是否工资发放银行"}],"relations":["t_md_company(COMP_CODE)"],"promptHint":"付款银行账号；BANK_ACCOUNT_NUM 为银行账号（敏感，禁止整行读取）；COMP_CODE 关联 OU/机构；DEFAULT_PAY 标记付款默认；PAY_MODE 为付款方式。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_project$t$, $t$项目$t$, $t$DATA$t$, $t${"businessMeaning":"项目定义，记录项目编码、父项目、业务实体与有效性。","keyFields":[{"name":"PROJECT_ID","meaning":"主键"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"PROJECT_CODE","meaning":"项目编码"},{"name":"PARENT_CODE","meaning":"父项目编码"},{"name":"IS_VALIDITY","meaning":"Y有效/N无效"},{"name":"IS_POST","meaning":"是否允许过账 Y允许/N不允许"},{"name":"IS_PARENT_VALUE","meaning":"是否父值"},{"name":"OU_CODE","meaning":"业务实体编码"}],"relations":["t_project_manage(PROJECT_CODE)"],"promptHint":"项目；PROJECT_CODE 为项目编码；PARENT_CODE 定义层级；OU_CODE 关联业务实体；IS_VALIDITY/IS_POST 为有效性与过账开关。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_ou$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"机构（OU）核算配置，记录机构的资产账簿、账套、本位币、预算、业务线、报账开关与组织层级等运行配置。","keyFields":[{"name":"OU_ID","meaning":"主键"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"COMPANY_CODE","meaning":"机构编码"},{"name":"MAIN_LEDGER","meaning":"主要分类账"},{"name":"CURRENCY_CODE","meaning":"本位币币种"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"BUSINESS_LINE_NAME","meaning":"业务线名称"},{"name":"Budget_Type","meaning":"预算类型（项目预算/部门预算/ALL）"},{"name":"VALID","meaning":"有效性 N禁用/Y启用"},{"name":"ENABLE_REIMBURSEMENT","meaning":"报账启用"},{"name":"IS_FSSC_PROCCESS","meaning":"是否会计处理"},{"name":"IS_TRAVEL_SWITCH_REIMBURSE","meaning":"是否启用报销模式 Y/N"},{"name":"COMPANY_GROUP","meaning":"公司组"},{"name":"ORG_UNIT","meaning":"机构单元"}],"relations":["t_md_company(COMPANY_CODE)","t_bu_ledger"],"promptHint":"机构核算配置；COMPANY_CODE 关联机构、BUSINESS_LINE_CODE 业务线；ENABLE_REIMBURSEMENT 报账启用开关；Budget_Type 预算类型；VALID 有效性。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_subaccount$t$, $t$子目会计科目表$t$, $t$DATA$t$, $t${"businessMeaning":"子目会计科目，记录子目与父级科目的关系及有效性。","keyFields":[{"name":"CHILD_ACCOUNT_ITEM_CODE","meaning":"子目会计科目编码（联合主键）"},{"name":"ACCOUNT_ITEM_CODE","meaning":"父级会计科目编码（联合主键）"},{"name":"BU_CODE","meaning":"BU编码（联合主键）"},{"name":"VALID","meaning":"有效性 N禁用/Y启用"},{"name":"EFFECTIVE_DATE_START","meaning":"有效日期起"},{"name":"EFFECTIVE_DATE_END","meaning":"有效日期止"},{"name":"summary_flag","meaning":"是否父级 Y/N"}],"relations":["t_md_account(ACCOUNT_ITEM_CODE)"],"promptHint":"子目科目；CHILD_ACCOUNT_ITEM_CODE 子目 + ACCOUNT_ITEM_CODE 父科目 + BU_CODE 联合主键；用于科目子目定位。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_invoice_main$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_type$t$, $t$发票类型，COST_TYPE费用类型  INVOICE_TYPE发票类型$t$, $t$DATA$t$, $t${"businessMeaning":"发票类型/费用类型分类，定义票夹类型的父分类（COST_TYPE费用类型、INVOICE_TYPE发票类型）。","keyFields":[{"name":"uuid","meaning":"主键"},{"name":"type_code","meaning":"分类编码"},{"name":"type_name","meaning":"分类名称"},{"name":"is_delete","meaning":"是否删除 0否/1是"}],"relations":["t_h_type_items(type_code)"],"promptHint":"发票/费用类型父分类；type_code 为分类编码（COST_TYPE/INVOICE_TYPE）；子项在 t_h_type_items。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_type_items$t$, $t$费用类型项$t$, $t$DATA$t$, $t${"businessMeaning":"发票/费用类型项，记录类型项编码、名称与父类编码。","keyFields":[{"name":"uuid","meaning":"主键"},{"name":"item_type_code","meaning":"类型编码"},{"name":"item_type_name","meaning":"类型名称"},{"name":"type_code","meaning":"父类编码"},{"name":"is_delete","meaning":"是否删除 0否/1是"}],"relations":["t_h_type(type_code)"],"promptHint":"发票/费用类型项；item_type_code/name 为类型项；type_code 关联父分类。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_exchange_rate$t$, $t$汇率$t$, $t$DATA$t$, $t${"businessMeaning":"汇率表，记录币种间在日期的汇率与折换汇率。","keyFields":[{"name":"EXCHANGE_RATE_ID","meaning":"主键"},{"name":"FROM_CURRENCY","meaning":"币种自"},{"name":"TO_CURRENCY","meaning":"币种至"},{"name":"CONVERSION_DATE","meaning":"日期"},{"name":"RATE_TYPE","meaning":"汇率类型"},{"name":"CONVERSION_RATE","meaning":"汇率"},{"name":"REDUCED_RATE","meaning":"折换汇率"}],"relations":[],"promptHint":"汇率；FROM_CURRENCY/TO_CURRENCY 为币种对；CONVERSION_DATE 为汇率日期；RATE_TYPE 为汇率类型；CONVERSION_RATE 为汇率值。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_tax_rate$t$, $t$税率$t$, $t$DATA$t$, $t${"businessMeaning":"税率配置，记录税率类型、税率值、关联科目与科目段（成本中心/产品/地区/项目等）。","keyFields":[{"name":"TAX_RATE_ID","meaning":"主键"},{"name":"TAX_STYPE_CODE","meaning":"税率类型编码"},{"name":"TAX_STYPE_NAME","meaning":"税率类型名称"},{"name":"TAX_RATE_VALUE","meaning":"税率值"},{"name":"TAX_RATE_NAME","meaning":"税率名称"},{"name":"IS_VALIDITY","meaning":"是否启用 1启用"},{"name":"RELATE_SUBJECT","meaning":"科目编码"},{"name":"COSTCENTER_CODE","meaning":"成本中心编码"},{"name":"PRODUCT_CODE","meaning":"产品/资金账户编码"},{"name":"REGION_CODE","meaning":"地区/纳税区编码"},{"name":"BU_CODE","meaning":"所属BU"},{"name":"OU_CODE","meaning":"归属公司code"}],"relations":["t_md_account(RELATE_SUBJECT)","t_md_costcenter(COSTCENTER_CODE)"],"promptHint":"税率；TAX_STYPE_CODE/NAME 为税率类型、TAX_RATE_VALUE 为税率值；RELATE_SUBJECT 关联科目；按 BU_CODE/OU_CODE/科目段定位税率。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_project_manage$t$, $t$项目信息管理头表$t$, $t$DATA$t$, $t${"businessMeaning":"项目信息管理头，记录项目编码/名称、项目经理、起止日期、类型、金额、成本中心、业务线与组织。","keyFields":[{"name":"PROJECT_ID","meaning":"主键"},{"name":"PROJECT_CODE","meaning":"项目编码"},{"name":"PROJECT_NAME","meaning":"项目名称"},{"name":"PROJECT_MANAGER","meaning":"项目经理"},{"name":"PROJECT_MANAGER_NAME","meaning":"项目经理名称"},{"name":"PROJECT_BEGIN_DATE","meaning":"项目开始日期"},{"name":"PROJECT_END_DATE","meaning":"项目结束日期"},{"name":"PROJECT_TYPE","meaning":"项目类型"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"CATEGORY","meaning":"业务类型"},{"name":"TOTAL_PROJECT_AMOUNT","meaning":"项目总金额"},{"name":"STATUS","meaning":"状态"},{"name":"COSTCENTER_CODE","meaning":"成本中心编码"},{"name":"BUSINESSLINE_CODE","meaning":"业务线编码"},{"name":"PROCDEF_ID","meaning":"流程定义ID"},{"name":"EXPENSE_CATEGORY_CODE","meaning":"费用类别编码"},{"name":"IS_DELETE","meaning":"是否删除 N否/Y是"},{"name":"ENABLED_FLAG","meaning":"有效标识 Y是/N否"}],"relations":["t_project_manage_budget(PROJECT_ID)","t_md_project(PROJECT_CODE)"],"promptHint":"项目信息头；PROJECT_CODE/NAME 为项目标识；PROJECT_MANAGER 项目经理；按 PROJECT_ID 关联预算；STATUS 为项目状态。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_project_manage_budget$t$, $t$项目预算信息$t$, $t$DATA$t$, $t${"businessMeaning":"项目预算信息，记录项目的预算年度、金额、类型、币种与核算子目科目。","keyFields":[{"name":"BUDGET_ID","meaning":"主键"},{"name":"BUDGET_NUMBER","meaning":"预算编号"},{"name":"PROJECT_ID","meaning":"项目ID"},{"name":"PROJECT_CODE","meaning":"项目编码"},{"name":"BUDGET_TYPE","meaning":"预算类型"},{"name":"BUDGET_YEAR","meaning":"预算年度"},{"name":"BUDGET_AMOUNT","meaning":"预算金额（立项币种）"},{"name":"BUDGET_CURRENCY","meaning":"币种"},{"name":"SUBCATEGORY_CODE","meaning":"业务小类编码"},{"name":"HS_CHILD_SUBJECT_CODE","meaning":"核算子目科目编码"},{"name":"IS_DELETE","meaning":"是否删除 N否/Y是"}],"relations":["t_project_manage(PROJECT_ID)","t_subcategory(SUBCATEGORY_CODE)"],"promptHint":"项目预算；PROJECT_ID 关联项目、BUDGET_YEAR 预算年度、BUDGET_AMOUNT 预算金额；SUBCATEGORY_CODE 限定业务小类；HS_CHILD_SUBJECT_CODE 为核算子目。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_business_server_standard$t$, $t$业务招待标准表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_communication_expense$t$, $t$通讯费标准$t$, $t$DATA$t$, $t${"businessMeaning":"通讯费标准，记录业务线/机构单元/公司/职务下的通讯费标准与控制规则。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"ORG_UNIT_CODE","meaning":"机构单元编码"},{"name":"COMPANY_CODE","meaning":"公司编码"},{"name":"POSITION_TYPE","meaning":"职级"},{"name":"COMMUNICAT_COSTS","meaning":"通讯费标准"},{"name":"CONTROL_RULE","meaning":"控制规则"},{"name":"ENABLED_FLAG","meaning":"有效标识 Y是/N否"},{"name":"ORG_CODE","meaning":"组织编码"}],"relations":[],"promptHint":"通讯费标准；按 BUSINESS_LINE_CODE + COMPANY_CODE + POSITION_TYPE 定位；COMMUNICAT_COSTS 为标准、CONTROL_RULE 为控制规则。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_standardarea$t$, $t$地区编码$t$, $t$DATA$t$, $t${"businessMeaning":"地区编码字典，记录地区编码与名称。","keyFields":[{"name":"AREA_ID","meaning":"地区ID（主键）"},{"name":"AREA_CODE","meaning":"地区编码"},{"name":"AREA_NAME","meaning":"地区名称"}],"relations":[],"promptHint":"地区字典；AREA_CODE/AREA_NAME 为地区编码/名称；被费用标准表以 AREA 引用。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_authorize_for_personal$t$, $t$针对个人的代报$t$, $t$DATA$t$, $t${"businessMeaning":"个人代报授权，记录被代报人与其代报人的 LDAP 对应及代报场景类型。","keyFields":[{"name":"ID","meaning":"ID（主键）"},{"name":"AUTHORIZED_NAME","meaning":"被代报人姓名"},{"name":"AUTHORIZED_LDAP","meaning":"被代报人LDAP"},{"name":"AUTHORIZED_FOR_NAME","meaning":"代报人名称"},{"name":"AUTHORIZED_FOR_LDAP","meaning":"代报人LDAP"},{"name":"AUTHORIZED_TYPE","meaning":"代报场景类型"},{"name":"AUTHORIZED_FOR_EMPL_RCD","meaning":"代报人雇佣记录"},{"name":"AUTHORIZED_FOR_OU_CODE","meaning":"代报人机构"}],"relations":["sys_register_user(AUTHORIZED_LDAP/AUTHORIZED_FOR_LDAP→USERCODE)"],"promptHint":"个人代报授权；AUTHORIZED_LDAP 被代报人、AUTHORIZED_FOR_LDAP 代报人（均 LDAP 账号）；AUTHORIZED_TYPE 为代报场景类型。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_delegate_company$t$, $t$代报部门公司$t$, $t$DATA$t$, $t${"businessMeaning":"代报部门公司配置，记录公司/组织/员工与代报关系的映射。","keyFields":[{"name":"ORG_COMP_ID","meaning":"主键"},{"name":"COMP_CODE","meaning":"公司代码"},{"name":"COMP_NAME","meaning":"公司名称"},{"name":"ORG_ID","meaning":"组织ID"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"ORG_NAME","meaning":"组织名称"},{"name":"EMP_ID","meaning":"员工ID"},{"name":"EMP_CODE","meaning":"员工编码"},{"name":"EMP_NAME","meaning":"员工姓名"},{"name":"IS_DELETE","meaning":"删除标识 0未删除/1已删除"}],"relations":["sys_org(ORG_ID)","sys_emp(EMP_ID)"],"promptHint":"代报部门公司；COMP_CODE 公司 + ORG_CODE 组织 + EMP_CODE 员工定义代报关系；用于跨组织/公司代报。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_hrcompany_ou_mapping$t$, $t$HR公司与核算机构映射关系表$t$, $t$DATA$t$, $t${"businessMeaning":"HR 公司与核算机构（OU）映射，记录 HR 公司、工作地点与核算机构、部门的对应。","keyFields":[{"name":"id","meaning":"主键"},{"name":"company_id","meaning":"HR公司id"},{"name":"company_name","meaning":"HR公司名称"},{"name":"crc_wrk_place","meaning":"工作地点编码"},{"name":"comp_code","meaning":"核算机构编码"},{"name":"comp_name","meaning":"核算机构名称"},{"name":"org_id","meaning":"部门ID"},{"name":"org_name","meaning":"部门名称"}],"relations":["t_md_company(comp_code)","sys_org(org_id)"],"promptHint":"HR公司-核算机构映射；company_id/name 为 HR 公司、comp_code/name 为核算机构；用于 HR 公司到核算机构的转换。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_bu_ledger$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"BU 与账套（Ledger）的对应关系，记录 BU 使用的账套ID/名称。","keyFields":[{"name":"id","meaning":"主键"},{"name":"bu","meaning":"BU"},{"name":"ledger_id","meaning":"账套ID"},{"name":"ledger_name","meaning":"账套名称"}],"relations":[],"promptHint":"BU-账套关系；bu 关联 BU、ledger_id/name 为账套；用于确定 BU 的核算账套。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_fms_version$t$, $t$核算版本配置表$t$, $t$DATA$t$, $t${"businessMeaning":"核算版本配置，记录 FMS 核算版本及其启用的模块（GL/FA/AR/AP/清账）。","keyFields":[{"name":"VID","meaning":"id（主键）"},{"name":"FMS_VERSION","meaning":"核算版本（联合主键）"},{"name":"FMS_DESC","meaning":"核算版本描述"},{"name":"ENABLE_GL","meaning":"是否启用GL"},{"name":"ENABLE_FA","meaning":"是否启用FA"},{"name":"ENABLE_AR","meaning":"是否启用AR"},{"name":"ENABLE_AP","meaning":"是否启用AP"},{"name":"ENABLE_SGL","meaning":"是否启用清账"}],"relations":["t_md_company(FMS_VERSION)"],"promptHint":"核算版本；FMS_VERSION 为核算版本、ENABLE_GL/FA/AR/AP/SGL 为启用模块开关；被机构以 FMS_VERSION 引用。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_invoice_info$t$, $t$发票信息（状态：未报销）$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_invoice_details$t$, $t$发票详细信息$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_invoice_main_info$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_cost_info$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_crc_emp_fin$t$, $t$员工财务属性(EDB)表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_loan_emp$t$, $t$借调员工的基本信息表$t$, $t$DATA$t$, $t${"businessMeaning":"借调员工基本信息，记录借调员工的 LDAP、姓名与邮箱。","keyFields":[{"name":"ID","meaning":"ID（主键）"},{"name":"EMP_ID","meaning":"员工ID"},{"name":"LDAP_ID","meaning":"员工LDAP"},{"name":"LDAP_NAME","meaning":"员工名称"},{"name":"EMAIL","meaning":"邮箱"},{"name":"IS_DEL","meaning":"有效标志 0有效/1无效"}],"relations":["sys_emp(EMP_ID)","t_loan_emp_job(LOAN_EMP_ID)"],"promptHint":"借调员工；LDAP_ID 为 LDAP 账号；IS_DEL 为有效标志；岗位在 t_loan_emp_job。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_loan_emp_job$t$, $t$借调员工的岗位信息表$t$, $t$DATA$t$, $t${"businessMeaning":"借调员工岗位信息，记录借调员工的核算公司、组织、岗位、职级与上下级领导。","keyFields":[{"name":"ID","meaning":"ID（主键）"},{"name":"LOAN_EMP_ID","meaning":"借调员工ID"},{"name":"OU_CODE","meaning":"核算公司编码"},{"name":"OU_NAME","meaning":"核算系统公司名称"},{"name":"ORG_ID","meaning":"组织id"},{"name":"ORG_NAME","meaning":"组织名称"},{"name":"JOB_CODE","meaning":"岗位编码"},{"name":"JOB_NAME","meaning":"岗位名称"},{"name":"SUPV_LVL_ID","meaning":"职级"},{"name":"SUPERVISOR_ID","meaning":"上级领导账号"},{"name":"SUPERVISOR_NAME","meaning":"上级领导名称"},{"name":"MANAGER_ID","meaning":"部门领导账号"},{"name":"MANAGER_NAME","meaning":"部门领导名称"}],"relations":["t_loan_emp(LOAN_EMP_ID)","sys_org(ORG_ID)"],"promptHint":"借调员工岗位；LOAN_EMP_ID 关联借调员工；OU_CODE 核算公司、ORG_ID 组织、SUPERVISOR_ID/MANAGER_ID 为上下级领导。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_ctrip_account$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"携程账户配置，记录员工的携程主/子账户、开卡状态与部门/公司/职级。","keyFields":[{"name":"ACCOUNT_ID","meaning":"主键ID"},{"name":"USER_CODE","meaning":"员工账号"},{"name":"EMP_NO","meaning":"员工工号"},{"name":"USER_NAME","meaning":"员工姓名"},{"name":"CARD_STATUS","meaning":"开卡状态 Y成功/N失败"},{"name":"SERVICE_TYPE","meaning":"服务类型 A开卡/I销卡"},{"name":"ORG_CODE","meaning":"部门代码（HR CR00*编码）"},{"name":"OU_CODE","meaning":"归属公司ouCode"},{"name":"SUPV_LVL","meaning":"主岗位职级"},{"name":"MAIN_ACCOUNT","meaning":"携程主账户"},{"name":"SUB_ACCOUNT","meaning":"携程子账户"}],"relations":["sys_register_user(USER_CODE→USERCODE)"],"promptHint":"携程账户；USER_CODE 员工账号、EMP_NO 工号；MAIN_ACCOUNT/SUB_ACCOUNT 为携程主/子账户；CARD_STATUS 开卡状态。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_claim_adapt$t$, $t$导入适配器$t$, $t$DATA$t$, $t${"businessMeaning":"导入适配器配置，记录适配器编码/名称、调用类、接口类型与 ESB 任务编码。","keyFields":[{"name":"ADAPT_ID","meaning":"主键"},{"name":"ADAPT_CODE","meaning":"导入适配器编码"},{"name":"ADAPT_NAME","meaning":"导入适配器名称"},{"name":"ADAPT_DESC","meaning":"适配器描述"},{"name":"IS_VALIDITY","meaning":"是否启用"},{"name":"ADAPT_CLASS","meaning":"适配器调用类"},{"name":"INTERFACE_CODE","meaning":"接口类型"},{"name":"ESB_TASK_CODE","meaning":"ESB TASK CODE"},{"name":"APPLY_TYPE","meaning":"1总账/2应付/3清账"}],"relations":["t_category(ADAPT_ID)"],"promptHint":"导入适配器；ADAPT_CODE/NAME 为适配器标识；ADAPT_CLASS 调用类、ESB_TASK_CODE ESB 任务；APPLY_TYPE 区分总账/应付/清账。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_claim_template_default_value_config$t$, $t$表单模板字段默认值配置表$t$, $t$DATA$t$, $t${"businessMeaning":"表单模板字段默认值配置，记录模板字段的赋值类型与默认值/表达式。","keyFields":[{"name":"ID","meaning":"ID（主键）"},{"name":"TP_CODE","meaning":"模板"},{"name":"FIELD_NAME","meaning":"字段名"},{"name":"ASSIGN_TYPE","meaning":"赋值类型 field直接设置默认值/methodInside调用系统内部方法"},{"name":"DEFAULT_VALUE","meaning":"默认值或表达式"},{"name":"IS_DEL","meaning":"有效标志 0有效/1无效"}],"relations":["t_apply_template(TP_CODE)"],"promptHint":"模板字段默认值；TP_CODE 模板 + FIELD_NAME 字段；ASSIGN_TYPE 赋值类型、DEFAULT_VALUE 默认值/表达式。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_category_process$t$, $t$业务大类对应流程$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_subcategory_org$t$, $t$业务小类关联组织$t$, $t$DATA$t$, $t${"businessMeaning":"业务小类与组织的关联，记录小类在某业务线/组织/机构下的角色与排除角色。","keyFields":[{"name":"SUBCATEGORY_ORG_ID","meaning":"序列号（主键）"},{"name":"SUBCATEGORY_CODE","meaning":"业务小类编码"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线"},{"name":"ORG_TYPE_CODE","meaning":"组织类型编码"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"ROLE_CODE","meaning":"角色编码"},{"name":"ROLE_NAME","meaning":"角色名称"},{"name":"OU_CODE","meaning":"归属公司（机构）编码"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"IS_DELETE","meaning":"正常0/删除1"}],"relations":["t_subcategory(SUBCATEGORY_CODE)","sys_org(ORG_CODE)"],"promptHint":"小类-组织关联；SUBCATEGORY_CODE 小类 + ORG_CODE 组织 + ROLE_CODE 角色；用于确定某组织某小类适用角色。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_label$t$, $t$发票标签表$t$, $t$DATA$t$, $t${"businessMeaning":"发票标签定义，记录标签编码、名称、颜色与所属人。","keyFields":[{"name":"id","meaning":"主键"},{"name":"label_code","meaning":"标签编码（唯一）"},{"name":"label_name","meaning":"标签名称"},{"name":"label_remarks","meaning":"标签备注"},{"name":"label_color","meaning":"标签颜色"},{"name":"owned_by","meaning":"所属人"},{"name":"is_delete","meaning":"是否删除 0否/1是"}],"relations":["t_h_invoice_label_rel(label_code)"],"promptHint":"发票标签；label_code/name 为标签标识；owned_by 为所属人；通过 t_h_invoice_label_rel 关联发票。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_esb_config$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"ESB 接口配置，记录任务、ESB 服务 ID、账号密码、MAC/SID 与请求 URL。含敏感凭据，仅做结构定位。","keyFields":[{"name":"TASK_SERVICE_ID","meaning":"任务ID（主键）"},{"name":"TASK_CODE","meaning":"任务CODE"},{"name":"TASK_NAME","meaning":"任务名称"},{"name":"ESB_SERVICE_ID","meaning":"ESB服务ID"},{"name":"ESB_USERNAME","meaning":"ESB用户名（敏感）"},{"name":"ESB_PASSWORD","meaning":"ESB密码（敏感）"},{"name":"ESB_MAC","meaning":"系统MAC地址"},{"name":"ESB_SID","meaning":"系统ID"},{"name":"ESB_URL","meaning":"ESB请求URL"},{"name":"BU_CODE","meaning":"财务BUCODE"}],"relations":[],"promptHint":"ESB 接口配置；含 ESB_USERNAME/ESB_PASSWORD 等敏感凭据，禁止读取配置值，仅用于定位接口任务（TASK_CODE）。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_asset_category$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"资产类别定义，记录资产类别、说明、所有权、启用与资本化标识。","keyFields":[{"name":"CATEGORY_ID","meaning":"资产类别ID（主键）"},{"name":"CATEGORY_TYPE","meaning":"分类类型"},{"name":"ASSET_CATEGORY","meaning":"资产类别"},{"name":"ASSET_CATEGORY_DESC","meaning":"资产说明"},{"name":"OWNED_LEASED","meaning":"所有权"},{"name":"ENABLED_FLAG","meaning":"启用标识"},{"name":"CAPITALIZE_FLAG","meaning":"资本化"}],"relations":["t_asset_category_item(CATEGORY_ID)"],"promptHint":"资产类别；ASSET_CATEGORY 为资产类别；ENABLED_FLAG 启用标识；用于资产卡片分类。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_assets_book$t$, $t$资产账簿基础数据$t$, $t$DATA$t$, $t${"businessMeaning":"资产账簿配置，记录资产账簿的增减变动映射、分类账与各类资产处置科目。","keyFields":[{"name":"ID","meaning":"ID（主键）"},{"name":"BU_CODE","meaning":"BU"},{"name":"BOOK_TYPE_CODE","meaning":"资产账簿编码"},{"name":"BOOK_TYPE_NAME","meaning":"资产账簿名称"},{"name":"MOVEMENT_MAPPING_CODE","meaning":"增减变动映射代码"},{"name":"LEDGER_NAME","meaning":"分类账"},{"name":"PROCEEDS_OF_SALE_GAIN_ACCT","meaning":"销售收入-收益科目"},{"name":"GL_POSTING_ALLOWED_FLAG","meaning":"允许GL过账"}],"relations":[],"promptHint":"资产账簿；BOOK_TYPE_CODE/NAME 为账簿标识；MOVEMENT_MAPPING_CODE 增减变动映射；各 *_ACCT 为资产处置科目。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_work_holiday$t$, $t$节假日期表$t$, $t$DATA$t$, $t${"businessMeaning":"节假日期表，记录节假日日期与类型（周末/法定节假日）。","keyFields":[{"name":"ID","meaning":"主键ID"},{"name":"HOLIDAY_DATE","meaning":"节假日日期"},{"name":"HOLIDAY_FLAG","meaning":"节假日类型（0周末/1法定节假日）"},{"name":"IS_DEL","meaning":"有效标志 0有效/1无效"}],"relations":[],"promptHint":"节假日；HOLIDAY_DATE 为日期、HOLIDAY_FLAG 0周末/1法定节假日；用于判断工作日。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_city_management$t$, $t$城市$t$, $t$DATA$t$, $t${"businessMeaning":"城市字典，记录城市名称、类型、地区类型、省份、是否热门与交通设施。","keyFields":[{"name":"CITY_ID","meaning":"城市编码（主键）"},{"name":"CITY_NAME","meaning":"城市名称"},{"name":"CITY_TYPE","meaning":"城市类型"},{"name":"AREA_TYPE","meaning":"地区类型"},{"name":"PROVINCE","meaning":"省份"},{"name":"FULL_CITY_NAME","meaning":"城市全拼"},{"name":"has_flight","meaning":"是否有机场 0否/1是"},{"name":"has_train","meaning":"是否有火车站 0否/1是"},{"name":"ENABLED_FLAG","meaning":"是否启用"}],"relations":[],"promptHint":"城市字典；CITY_NAME 城市名、PROVINCE 省份、AREA_TYPE 地区类型；has_flight/has_train 标记交通设施。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_travel_standard$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"差旅标准，记录公司/业务线/职务/地区下的交通工具等级、住宿/餐费/市内交通标准与控制规则。","keyFields":[{"name":"TRAVEL_STANDARD_ID","meaning":"主键"},{"name":"COMPANY_CODE","meaning":"公司代码"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"POSITION_TYPE","meaning":"职务类型"},{"name":"POSITION_CODE","meaning":"职务编码"},{"name":"AIR_CODE","meaning":"飞机舱位"},{"name":"HIGH_SPEED_CODE","meaning":"高铁/动车"},{"name":"TRAIN_CODE","meaning":"火车"},{"name":"AREA_CODE","meaning":"地区类型1"},{"name":"ROOM_RENTAL_STANDARD","meaning":"租房标准"},{"name":"HOTEL_STANDARD","meaning":"酒店标准"},{"name":"FOOD_STANDARD","meaning":"餐补标准"},{"name":"LOCAL_TRANSPORT_STANDARD","meaning":"当地交通费"},{"name":"CONTROL_RULE","meaning":"控制规则"},{"name":"IS_DELETE","meaning":"删除标识 0未删除/1已删除"}],"relations":[],"promptHint":"差旅标准；按 COMPANY_CODE + POSITION_TYPE/CODE + AREA_CODE 定位；AIR/HIGH_SPEED/TRAIN_CODE 为交通工具等级；HOTEL/FOOD/交通为标准金额；CONTROL_RULE 为控制规则。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_food_standard$t$, $t$会议费、差旅费的会议主办方餐费标准$t$, $t$DATA$t$, $t${"businessMeaning":"会议/差旅餐费标准，记录业务大类/小类、参会主体、地区下的餐费标准。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"CATEGORY_CODE","meaning":"业务大类编码"},{"name":"SUBCATEGORY_CODE","meaning":"业务小类编码"},{"name":"PARTICIPANTS_MAIN","meaning":"参会主体编码"},{"name":"PARTICIPANTS_MAIN_NAME","meaning":"参会主体名称"},{"name":"AREA_TYPE","meaning":"地区类型"},{"name":"CURRENCY","meaning":"币种"},{"name":"FOOD_STANDARD","meaning":"餐费标准"},{"name":"STATE","meaning":"0有效/1无效"},{"name":"IS_DEL","meaning":"0正常/1删除"}],"relations":["t_category(CATEGORY_CODE)","t_subcategory(SUBCATEGORY_CODE)"],"promptHint":"会议/差旅餐费标准；按 CATEGORY_CODE + PARTICIPANTS_MAIN + AREA_TYPE 定位；FOOD_STANDARD 为餐费标准。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_food_standard_company$t$, $t$膳食费关联的归属公司信息$t$, $t$DATA$t$, $t${"businessMeaning":"膳食费标准关联的归属公司，记录餐费标准适用的公司。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"COMPANY_CODE","meaning":"归属公司机构编码"},{"name":"COMPANY_NAME","meaning":"公司名称"},{"name":"COMPANY_CODE_REF","meaning":"归属公司机构外键"},{"name":"IS_DEL","meaning":"0正常/1删除"}],"relations":["t_food_standard(COMPANY_CODE_REF)"],"promptHint":"膳食费关联公司；COMPANY_CODE 为公司编码；按 COMPANY_CODE_REF 关联餐费标准。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_work_attendance$t$, $t$工作考勤记录表$t$, $t$DATA$t$, $t${"businessMeaning":"工作考勤记录，记录员工每日签到/签退时间与上下卡标记。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"USER_CODE","meaning":"员工LDAP账号"},{"name":"USER_NAME","meaning":"员工名称"},{"name":"WORK_ATTENDANCE_DATE","meaning":"考勤日期"},{"name":"SIGN_IN_TIME","meaning":"签到时间（文本）"},{"name":"SIGN_OUT_TIME","meaning":"签退时间（文本）"},{"name":"AM_SIGN_IN_CARD_FLAG","meaning":"上午是否签卡"},{"name":"PM_SIGN_IN_CARD_FLAG","meaning":"下午是否签卡"}],"relations":["sys_register_user(USER_CODE→USERCODE)"],"promptHint":"考勤记录；USER_CODE 员工 LDAP 账号、WORK_ATTENDANCE_DATE 考勤日期；SIGN_IN/OUT_TIME 签到/签退时间。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_white_list$t$, $t$暂时存放白名单$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_tms_fund_plan$t$, $t$TMS资金计划$t$, $t$DATA$t$, $t${"businessMeaning":"TMS 资金计划，记录资金计划编码/名称、父计划、交易方向与有效性。","keyFields":[{"name":"FUND_PLAN_ID","meaning":"资金计划编号（主键）"},{"name":"OU_CODE","meaning":"归属公司编码"},{"name":"TMS_COMPANY_CODE","meaning":"组织代码（ORGCODE）"},{"name":"FUND_PLAN_CODE","meaning":"资金计划编码（CODE）"},{"name":"FUND_PLAN_NAME","meaning":"资金计划名称（NAME）"},{"name":"PARENT_FUND_PLAN_CODE","meaning":"父资金计划编码（PCODE）"},{"name":"MONEY_WAY","meaning":"交易方向 1支出/2收入"},{"name":"IS_ACTIVE","meaning":"是否有效"},{"name":"IS_DELETE","meaning":"是否删除"}],"relations":[],"promptHint":"TMS 资金计划；FUND_PLAN_CODE/NAME 为资金计划；PARENT_FUND_PLAN_CODE 定义层级；MONEY_WAY 1支出/2收入。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_dimension$t$, $t$维度表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_ffe_attribute$t$, $t$弹性域字段$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_flex_field$t$, $t$弹性域$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_value_set$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"值集定义，记录值集编码/名称、类型、值来源与业务线/公司范围。","keyFields":[{"name":"VS_ID","meaning":"主键"},{"name":"VS_CODE","meaning":"值集编码"},{"name":"VS_NAME","meaning":"值集名称"},{"name":"VS_TYPE","meaning":"值集类型 1独立/2从属"},{"name":"VS_VALUE_SOURCE","meaning":"值来源 1自定义/2动态SQL"},{"name":"ENABLED","meaning":"是否启用"},{"name":"VS_PARENT_CODE","meaning":"父级编码"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"OU_CODE","meaning":"归属公司编码"}],"relations":["sys_value_set_item(VS_CODE)"],"promptHint":"值集定义；VS_CODE/NAME 为值集标识；VS_VALUE_SOURCE 1自定义/2动态SQL；值项在 sys_value_set_item。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_value_set_item$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"值集值项，记录值集的键值、父键、有效起止与业务线/公司范围。","keyFields":[{"name":"VSI_ID","meaning":"主键"},{"name":"VS_CODE","meaning":"值集编码"},{"name":"VSI_KEY","meaning":"键"},{"name":"VSI_PARENT_KEY","meaning":"父级键"},{"name":"VSI_START_DATE","meaning":"有效开始时间"},{"name":"VSI_END_DATE","meaning":"有效结束时间"},{"name":"ENABLED","meaning":"是否启用"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"OU_CODE","meaning":"归属公司编码"}],"relations":["sys_value_set(VS_CODE)"],"promptHint":"值集值项；VS_CODE 关联值集、VSI_KEY 为键值；按业务线/公司/有效期过滤。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_vendor$t$, $t$供应商$t$, $t$DATA$t$, $t${"businessMeaning":"供应商主数据，记录供应商编码/名称/类型、交易对象、纳税识别号、关联方与外部客商编码。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"VENDOR_ID","meaning":"供应商ID"},{"name":"VENDOR_CODE","meaning":"供应商编码"},{"name":"VENDOR_NAME","meaning":"供应商名称"},{"name":"VENDOR_TYPE","meaning":"供应商类型"},{"name":"TRXPARTY","meaning":"交易对象"},{"name":"VAT_REGISTRATION_NUM","meaning":"纳税识别号（敏感）"},{"name":"INTERCOMPANY_CODE","meaning":"关联方编码"},{"name":"VENDOR_CODE_ALIAS","meaning":"MDM供应商编码"},{"name":"EXT_PARTY_CODE","meaning":"外部客商编码"},{"name":"FAILURE","meaning":"有效性"}],"relations":["t_md_vendor_site(VENDOR_ID)","t_md_vendor_account(VENDOR_ID)"],"promptHint":"供应商主数据；VENDOR_CODE/NAME 为供应商标识；VAT_REGISTRATION_NUM 纳税识别号（敏感）；VENDOR_TYPE 为类型；地点/账户分别在 site/account 表。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_vendor_site$t$, $t$供应商地址信息$t$, $t$DATA$t$, $t${"businessMeaning":"供应商地点/地址，记录地点名称、省市区、付款条件、负债/预付/应收科目及维度值。","keyFields":[{"name":"id","meaning":"主键"},{"name":"VENDOR_SITE_ID","meaning":"供应商地点ID"},{"name":"VENDOR_ID","meaning":"供应商ID"},{"name":"VENDOR_CODE","meaning":"供应商编码"},{"name":"VENDOR_SITE_NAME","meaning":"供应商地址名称"},{"name":"PROVINCE","meaning":"省"},{"name":"CITY","meaning":"城市"},{"name":"PAY_CONDITION","meaning":"付款条件"},{"name":"LIABILITY_ACCOUNT","meaning":"负债科目"},{"name":"ADVANCE_PAYMENT_ACCOUNT","meaning":"预付款科目"},{"name":"RECEIVABLE_ACCOUNT","meaning":"应收科目编码"},{"name":"IS_VALIDITY","meaning":"有效性 N禁用/Y启用"},{"name":"COST_CENTER_VALUE","meaning":"成本中心"},{"name":"ATTRIBUTE11","meaning":"是否非经营性供应商 Y是/N否"}],"relations":["t_md_vendor(VENDOR_ID)"],"promptHint":"供应商地点；VENDOR_SITE_ID 关联银行账户；LIABILITY_ACCOUNT/ADVANCE_PAYMENT_ACCOUNT/RECEIVABLE_ACCOUNT 为负债/预付/应收科目；IS_VALIDITY 为启用开关。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_company_mapping$t$, $t$fms核算机构与tms核算机构mapping关系$t$, $t$DATA$t$, $t${"businessMeaning":"FMS 核算机构与 TMS 资金机构的映射关系。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"COMPANY_CODE","meaning":"核算系统机构编码"},{"name":"COMPANY_NAME","meaning":"核算系统机构名称"},{"name":"TMS_COMPANY_CODE","meaning":"资金系统机构编码"},{"name":"TMS_COMPANY_NAME","meaning":"资金系统机构名称"}],"relations":["t_md_company(COMPANY_CODE)"],"promptHint":"核算机构-资金机构映射；COMPANY_CODE 核算机构、TMS_COMPANY_CODE 资金机构；用于 FMS 与 TMS 机构转换。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_ncc_voucher$t$, $t$NCC凭证信息表$t$, $t$DATA$t$, $t${"businessMeaning":"NCC 凭证信息，记录 NCC 凭证的账簿、期间、凭证类别、借贷金额与制单/审核/记账人。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"PK_VOUCHER","meaning":"凭证主键"},{"name":"ACC_BOOK_CODE","meaning":"核算账簿编码"},{"name":"ACC_BOOK_NAME","meaning":"核算账簿名称"},{"name":"YEAR","meaning":"会计年度"},{"name":"PERIOD","meaning":"会计期间"},{"name":"NUM","meaning":"凭证号"},{"name":"VOUCHER_TYPE_CODE","meaning":"凭证类别编码"},{"name":"EXPLANATION","meaning":"摘要"},{"name":"PREPARED_CODE","meaning":"制单人编码"},{"name":"CHECKED_CODE","meaning":"审核人编码"},{"name":"MANAGER_CODE","meaning":"记账人编码"},{"name":"IS_DEL","meaning":"有效标志 0有效/1无效"}],"relations":["t_ncc_voucher_detail(PK_VOUCHER)"],"promptHint":"NCC 凭证；PK_VOUCHER 凭证主键、YEAR/PERIOD/NUM 定位期间与凭证号；VOUCHER_TYPE_CODE 凭证类别；分录在 t_ncc_voucher_detail。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_payment_result_msg$t$, $t$支付结果消息表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_ncc_account$t$, $t$NCC账户信息表$t$, $t$DATA$t$, $t${"businessMeaning":"NCC 会计科目，记录科目编码/名称、科目属性、级次、方向与辅助核算等科目属性。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"PK_ACCOUNT","meaning":"会计科目pk"},{"name":"CODE","meaning":"科目编码"},{"name":"NAME","meaning":"科目名称"},{"name":"ENABLE_STATE","meaning":"启用状态 1未启用/2已启用/3已停用"},{"name":"ACC_PROPERTY","meaning":"科目属性 0财务会计/1政府预算会计"},{"name":"CASH_TYPE","meaning":"现金分类 0其它/1现金/2银行/3现金等价物"},{"name":"ACCLEV","meaning":"科目级次"},{"name":"BALAN_ORIENT","meaning":"科目方向 0借方/1贷方"},{"name":"END_FLAG","meaning":"末级标志"},{"name":"IS_DEL","meaning":"有效标志 0有效/1无效"}],"relations":["t_ncc_voucher_detail(PK_ACCOUNT)"],"promptHint":"NCC 科目；CODE 科目编码、NAME 名称；ACC_PROPERTY/CASH_TYPE/ACCLEV/BALAN_ORIENT 为科目属性；辅助核算在 t_ncc_sub_account。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_oa_project$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_secretary_card_info$t$, $t$秘书卡信息维护$t$, $t$DATA$t$, $t${"businessMeaning":"秘书卡信息，记录携程卡号、虚拟员工工号、结算机构/成本中心/专项与审批人。","keyFields":[{"name":"ID","meaning":"ID（主键）"},{"name":"CTRIP_CARD_NO","meaning":"携程卡号"},{"name":"FICTITIOUS_EMP_NO","meaning":"虚拟员工工号"},{"name":"AFFILIATED_UNIT","meaning":"所属单位"},{"name":"EMAIL","meaning":"持卡人邮箱"},{"name":"PAY_OU_CODE","meaning":"结算机构编码"},{"name":"PAY_COST_CENTER_CODE","meaning":"结算成本中心"},{"name":"CRC_REVIEWER","meaning":"审批人"},{"name":"CRC_REVIEWER_NAME","meaning":"审批人名称"},{"name":"BU_CODE","meaning":"BU"}],"relations":[],"promptHint":"秘书卡；CTRIP_CARD_NO 携程卡号、FICTITIOUS_EMP_NO 虚拟工号；PAY_OU_CODE/PAY_COST_CENTER_CODE 为结算机构/成本中心。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_type_item$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_setting$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_md_vendor_account$t$, $t$供应商地点银行账户$t$, $t$DATA$t$, $t${"businessMeaning":"供应商地点下的银行账户，记录开户行、账号、户名、币种与账户有效期。含银行账号，敏感。","keyFields":[{"name":"VENDOR_ACCOUNT_ID","meaning":"主键"},{"name":"VENDOR_SITE_ID","meaning":"供应商地点ID"},{"name":"VENDOR_ID","meaning":"供应商ID"},{"name":"VENDOR_CODE","meaning":"供应商编号"},{"name":"BANK_NAME","meaning":"银行名称"},{"name":"VENDOR_ACCOUNT_NO","meaning":"银行账号（敏感）"},{"name":"VENDOR_ACCOUNT_NAME","meaning":"银行户名"},{"name":"CURRENCY","meaning":"币种"},{"name":"IS_VALIDITY","meaning":"有效性 N禁用/Y启用"},{"name":"START_DATE","meaning":"账户分配起始日期"},{"name":"END_DATE","meaning":"账户分配终止日期"}],"relations":["t_md_vendor(VENDOR_ID)","t_md_vendor_site(VENDOR_SITE_ID)"],"promptHint":"供应商银行账户；VENDOR_ACCOUNT_NO 为银行账号（敏感，禁止整行读取）；VENDOR_SITE_ID 关联供应商地点；IS_VALIDITY 为启用开关。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_ncc_voucher_detail$t$, $t$NCC凭证分录信息表$t$, $t$DATA$t$, $t${"businessMeaning":"NCC 凭证分录，记录分录的科目、借贷方向、原币/本币金额与汇率。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"VOUCHER_ID","meaning":"T_NCC_VOUCHER主键"},{"name":"PK_DETAIL","meaning":"分录pk"},{"name":"DETAIL_INDEX","meaning":"分录号"},{"name":"EXPLANATION","meaning":"摘要"},{"name":"ACCOUNT_CODE","meaning":"科目编码"},{"name":"CURR_TYPE_CODE","meaning":"币种编码"},{"name":"DEBIT_AMOUNT","meaning":"借方原币金额"},{"name":"CREDIT_AMOUNT","meaning":"贷方原币金额"},{"name":"IS_DEL","meaning":"有效标志 0有效/1无效"}],"relations":["t_ncc_voucher(VOUCHER_ID)","t_ncc_account(ACCOUNT_CODE)"],"promptHint":"NCC 凭证分录；VOUCHER_ID 关联凭证、ACCOUNT_CODE 科目；DEBIT_AMOUNT/CREDIT_AMOUNT 为借贷方金额。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_cost_claim_category$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_project_manage_approve$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_subcategory_category_label_info$t$, $t$大小类加签$t$, $t$DATA$t$, $t${"businessMeaning":"大小类加签配置，记录大类/小类在某机构/组织下的加签用户与数值。","keyFields":[{"name":"ID","meaning":"主键ID"},{"name":"CATEGORY_CODE","meaning":"大类编码"},{"name":"SUBCATEGORY_CODE","meaning":"小类编码"},{"name":"USER_CODE","meaning":"用户编码"},{"name":"USER_NAME","meaning":"用户名称"},{"name":"ORG_UNIT_CODE","meaning":"机构单元编码"},{"name":"COMPANY_CODE","meaning":"归属公司编码"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"NUMBERS","meaning":"加签数值"},{"name":"VALIDITY","meaning":"有效性"}],"relations":["t_category(CATEGORY_CODE)","t_subcategory(SUBCATEGORY_CODE)"],"promptHint":"大小类加签；按 CATEGORY_CODE + SUBCATEGORY_CODE + 组织定位加签用户；USER_CODE 为加签人、NUMBERS 为加签数值。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_group$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_group_member$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_mail_server$t$, $t$邮件服务器配置表$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_notice$t$, $t$公告内容$t$, $t$DATA$t$, $t${"businessMeaning":"系统公告，记录公告标题、类型、状态、内容与生效区间。","keyFields":[{"name":"notice_id","meaning":"主键"},{"name":"notice_title","meaning":"公告标题"},{"name":"notice_type","meaning":"公告类型"},{"name":"notice_status","meaning":"公告状态"},{"name":"notice_content","meaning":"公告内容"},{"name":"effect_begin","meaning":"生效开始"},{"name":"effect_end","meaning":"生效结束"},{"name":"language","meaning":"语言"}],"relations":[],"promptHint":"系统公告；notice_title/type/status 为公告属性；notice_content 为内容；按生效区间判断可见。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$sys_receipt_sequence$t$, $t$单据序列主表$t$, $t$DATA$t$, $t${"businessMeaning":"单据序列定义，记录单据序列编码、名称与重置频率。","keyFields":[{"name":"RECEIPT_SEQ_ID","meaning":"单据序列表（主键）"},{"name":"RECEIPT_SEQ_CODE","meaning":"序列代码"},{"name":"RECEIPT_SEQ_NAME","meaning":"序列名称"},{"name":"APPLICABLE_TO_BU","meaning":"适用组织"},{"name":"IS_AVAILABLE","meaning":"是否启用 Y是"},{"name":"RESET_FREQUENCY","meaning":"重置频率"}],"relations":["sys_receipt_seq_rule(RECEIPT_SEQ_ID)"],"promptHint":"单据序列；RECEIPT_SEQ_CODE/NAME 为序列标识；RESET_FREQUENCY 为重置频率；规则在 sys_receipt_seq_rule。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_invoice_claim_line_rel$t$, $t$发票与报帐单关系relation$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_invoice_element$t$, $t$发票动态字段$t$, $t$DATA$t$, $t${"businessMeaning":"发票动态字段定义，记录发票类型的动态列（t_invoice_main_info 的 field1-31）。","keyFields":[{"name":"id","meaning":"主键"},{"name":"el_code","meaning":"列名（field1-31）"},{"name":"el_name","meaning":"名称"},{"name":"invoice_type","meaning":"发票类型"},{"name":"is_show","meaning":"是否显示"},{"name":"is_delete","meaning":"是否删除 0否/1是"}],"relations":["t_h_invoice_info(invoice_type)"],"promptHint":"发票动态字段；el_code 对应 t_invoice_main_info 的 field1-31；el_name 为字段名称；invoice_type 限定发票类型。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_type_element_config$t$, $t$OCR发票类型映射到报账票据类型$t$, $t$DATA$t$, $t${"businessMeaning":"OCR 发票类型到报账票据类型的映射，记录 OCR 扫描类型与报账端发票类型的对应。","keyFields":[{"name":"id","meaning":"主键"},{"name":"ec_type","meaning":"OCR扫描后的发票类型"},{"name":"ec_sub_type","meaning":"OCR扫描后的二级类型"},{"name":"ec_type_key","meaning":"OCR扫描的票据类型值"},{"name":"item_type_code","meaning":"报账端发票类型"},{"name":"is_delete","meaning":"是否删除 0正常/1删除"}],"relations":["t_h_type_items(item_type_code)"],"promptHint":"OCR-报账发票类型映射；ec_type/ec_sub_type 为 OCR 类型、item_type_code 为报账端类型。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_type_items_category_config$t$, $t$票夹类型与业务大类对应的关系配置$t$, $t$DATA$t$, $t${"businessMeaning":"票夹类型与业务大类的对应配置，记录发票类型在业务大类/小类下的税率与合并规则。","keyFields":[{"name":"id","meaning":"主键"},{"name":"CATEGORY_CODE","meaning":"业务大类编码"},{"name":"SUBCATEGORY_CODE","meaning":"业务小类编码"},{"name":"item_type_code","meaning":"发票类型"},{"name":"tax_rate","meaning":"税率"},{"name":"BU_CODE","meaning":"BU编码"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"valid","meaning":"是否启用 Y启用/N未启用"},{"name":"is_merge","meaning":"是否合并 N不合并/Y合并"}],"relations":["t_category(CATEGORY_CODE)","t_h_type_items(item_type_code)"],"promptHint":"票夹-大类对应；按 CATEGORY_CODE + item_type_code 定位发票类型在大类下的税率(tax_rate)与合并规则(is_merge)。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_excel_project_manage$t$, $t$项目导入$t$, $t$DATA$t$, $t${"businessMeaning":"项目导入（Excel），记录项目编码/名称、预算年度、预算来源与预算金额。","keyFields":[{"name":"PROJECT_CODE","meaning":"项目编码（联合主键）"},{"name":"PROJECT_NAME","meaning":"项目名称"},{"name":"PROJECT_MANAGER","meaning":"项目经理"},{"name":"PROJECT_BEGIN_DATE","meaning":"项目开始日期"},{"name":"PROJECT_END_DATE","meaning":"项目结束日期"},{"name":"BUDGET_YEAR","meaning":"预算年度"},{"name":"BUDGET_RESOURCE","meaning":"预算来源（联合主键）"},{"name":"BUDGET_AMOUNT","meaning":"预算金额"},{"name":"BUDGET_NUMBER","meaning":"预算编号"},{"name":"VIRTUAL_BUDGET_FLAG","meaning":"是否虚拟预算"}],"relations":["t_project_manage(PROJECT_CODE)"],"promptHint":"项目导入（Excel）；PROJECT_CODE + BUDGET_RESOURCE 为联合主键；BUDGET_YEAR/AMOUNT 为预算年度/金额。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_oa_project_line$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_physical_category_collect$t$, $t$实物类别收藏表$t$, $t$DATA$t$, $t${"businessMeaning":"实物类别收藏，记录用户对实物类别的收藏标记。","keyFields":[{"name":"ID","meaning":"ID（主键）"},{"name":"LDAP_ID","meaning":"LDAP账号"},{"name":"PHYSICAL_CATEGORY_CODE","meaning":"实物类别编码"},{"name":"COLLECT_FLAG","meaning":"收藏标志 Y已收藏/N取消收藏"}],"relations":[],"promptHint":"实物类别收藏；LDAP_ID 用户、PHYSICAL_CATEGORY_CODE 实物类别；COLLECT_FLAG 收藏标记。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_trade_caravan$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"贸易商队/往来单位，记录用户/组织/机构单元与账单编辑权限、角色。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"USER_CODE","meaning":"用户编码"},{"name":"USER_NAME","meaning":"用户名称"},{"name":"ORG_CODE","meaning":"组织编码"},{"name":"OU_CODE","meaning":"归属公司编码"},{"name":"ORG_UNIT","meaning":"机构单元"},{"name":"EDIT_AUTH","meaning":"账单编辑权限"},{"name":"EDIT_GENERATE","meaning":"结算流程页面编辑权"},{"name":"ROLE","meaning":"角色"},{"name":"EMAIL","meaning":"邮件"}],"relations":["sys_org(ORG_CODE)"],"promptHint":"贸易商队；USER_CODE 用户、OU_CODE/ORG_UNIT 归属；EDIT_AUTH/EDIT_GENERATE 为编辑权限；ROLE 为角色。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_type_parent$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_user_work_hour_info$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"员工工时信息，记录员工的工时日期、有效工时与项目/产品/产品线归属。","keyFields":[{"name":"id","meaning":"主键"},{"name":"USER_ACCOUNT","meaning":"员工账号"},{"name":"USER_NAME","meaning":"员工姓名"},{"name":"WORK_DATE","meaning":"工时日期"},{"name":"EFFECTIVE_HOUR","meaning":"有效工时"},{"name":"PROJECT_CODE","meaning":"项目编号"},{"name":"PROJECT_NAME","meaning":"项目名称"},{"name":"PROJECT_MANAGER_ACCOUNT","meaning":"项目经理账号"}],"relations":["t_project_manage(PROJECT_CODE)"],"promptHint":"员工工时；USER_ACCOUNT 员工账号、WORK_DATE 工时日期、EFFECTIVE_HOUR 有效工时；PROJECT_CODE 关联项目。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_claim_data_send_receive$t$, $t$报账数据报送表$t$, $t$DATA$t$, $t${"businessMeaning":"报账数据报送表，记录报账数据报送的报送人、接收人、金额与报送/接收/处理状态。","keyFields":[{"name":"CLAIM_ID","meaning":"数据报送单主键id"},{"name":"CLAIM_NO","meaning":"单据号"},{"name":"SUBMIT_DATE","meaning":"报送日期"},{"name":"MONEY","meaning":"金额"},{"name":"SUBMITTER_CODE","meaning":"报送人员工ID"},{"name":"SUBMITTER_NAME","meaning":"报送人员工名称"},{"name":"RECIPIENT_CODE","meaning":"接收人员工ID"},{"name":"RECIPIENT_NAME","meaning":"接收人员工名称"},{"name":"SEND_STATE","meaning":"报送状态"},{"name":"RECEIVE_STATE","meaning":"接收状态"},{"name":"HAND_STATE","meaning":"处理状态"}],"relations":["报账单（CLAIM_NO）"],"promptHint":"报账数据报送；SUBMITTER_CODE 报送人、RECIPIENT_CODE 接收人；SEND/RECEIVE/HAND_STATE 为报送/接收/处理状态。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_claim_emp_receivales$t$, $t$员工报销表单放开收款账号字段$t$, $t$DATA$t$, $t${"businessMeaning":"员工对私收款账号放开配置，记录员工的收款账号与币种。","keyFields":[{"name":"id","meaning":"主键"},{"name":"EMPLID","meaning":"员工ID"},{"name":"EMPL_RCD","meaning":"雇用记录编号"},{"name":"CLAIM_RECEIVALES_ACCOUNT","meaning":"对私付款收款账号（敏感）"},{"name":"CLAIM_RECEIVALES_CURRENCY","meaning":"对私付款收款币种"},{"name":"IS_DEL","meaning":"0有效/1无效"}],"relations":["sys_emp(EMPLID)"],"promptHint":"员工收款账号放开；CLAIM_RECEIVALES_ACCOUNT 为收款账号（敏感）；EMPLID 关联员工。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_category_adapt$t$, $t$业务大类-导入适配器映射关系$t$, $t$DATA$t$, $t${"businessMeaning":"业务大类与导入适配器的映射，记录大类在 BU/机构/公司下使用的适配器、FMS 模块与事件。","keyFields":[{"name":"ADAPT_ID","meaning":"主键"},{"name":"CATEGORY_CODE","meaning":"业务大类编码"},{"name":"FMS_MODULE","meaning":"核算系统模块"},{"name":"ADAPT_CODE","meaning":"适配器类型"},{"name":"CLAIM_ADAPT_ID","meaning":"适配器ID"},{"name":"PRIORITY","meaning":"适配器优先级"},{"name":"event_code","meaning":"事件编码"},{"name":"event_name","meaning":"事件名称"},{"name":"OU_CODE","meaning":"归属公司编码"},{"name":"ORG_UNIT_CODE","meaning":"机构单元编码"}],"relations":["t_category(CATEGORY_CODE)","t_claim_adapt(CLAIM_ADAPT_ID)"],"promptHint":"大类-适配器映射；按 CATEGORY_CODE + FMS_MODULE + OU_CODE 定位适配器；ADAPT_CODE 适配器类型、event_code 事件。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_ctrip_same_account$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"携程同账户配置（同账户多人共享），记录员工的携程主/子账户。","keyFields":[{"name":"ACCOUNT_ID","meaning":"主键ID"},{"name":"USER_CODE","meaning":"员工账号"},{"name":"EMP_NO","meaning":"员工工号"},{"name":"USER_NAME","meaning":"员工姓名"},{"name":"MAIN_ACCOUNT","meaning":"携程主账户"},{"name":"SUB_ACCOUNT","meaning":"携程子账户"},{"name":"CARD_STATUS","meaning":"开卡状态 Y成功/N失败"}],"relations":["sys_register_user(USER_CODE→USERCODE)"],"promptHint":"携程同账户；USER_CODE 员工账号、MAIN_ACCOUNT/SUB_ACCOUNT 为携程主/子账户。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_business_server_standard_crc$t$, $t$总部业务招待费标准$t$, $t$DATA$t$, $t${"businessMeaning":"总部业务招待费标准，记录业务线/公司/组织/地区下的宴请、接待、纪念品标准与控制规则。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"BUSINESS_LINE_CODE","meaning":"业务线编码"},{"name":"COMPANY_CODE","meaning":"公司编码"},{"name":"AREA","meaning":"适用地区"},{"name":"INITIATOR","meaning":"发起主体"},{"name":"ENT_ACT","meaning":"招待活动"},{"name":"FETE","meaning":"宴请（餐饮）标准"},{"name":"RECEPTION","meaning":"接待（住宿）标准"},{"name":"BIZ_SOUVENIR","meaning":"商务纪念品标准"},{"name":"SOUVENIR","meaning":"行政纪念品标准"},{"name":"CONTROL_RULE","meaning":"控制规则"},{"name":"IS_DELETE","meaning":"是否删除 N否/Y是"}],"relations":[],"promptHint":"总部业务招待费标准；按 BUSINESS_LINE_CODE + COMPANY_CODE + AREA + INITIATOR 定位；FETE/RECEPTION/SOUVENIR 为各类标准；CONTROL_RULE 为控制规则。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_invoice_label_rel$t$, $t$发票标签关系表$t$, $t$DATA$t$, $t${"businessMeaning":"发票与标签的关联关系，记录发票上打的标签。","keyFields":[{"name":"id","meaning":"主键"},{"name":"label_code","meaning":"标签编码"},{"name":"invoice_id","meaning":"发票ID"},{"name":"label_order","meaning":"排序"},{"name":"owned_by","meaning":"所属人"}],"relations":["t_h_invoice_info(invoice_id)","t_h_label(label_code)"],"promptHint":"发票-标签关系；invoice_id 关联发票、label_code 关联标签；用于发票标签检索。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_h_cost_detail_source$t$, $t$费用明细来源$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_ncc_sub_account$t$, $t$NCC账户辅助核算信息表$t$, $t$DATA$t$, $t${"businessMeaning":"NCC 科目辅助核算，记录科目的辅助核算编码、是否允许为空与余额方向控制。","keyFields":[{"name":"ID","meaning":"主键"},{"name":"ACCOUNT_ID","meaning":"T_NCC_ACCOUNT主键"},{"name":"ACCASS_CODE","meaning":"辅助核算编码"},{"name":"IS_EMPTY","meaning":"允许为空"},{"name":"IS_BALANCE_CONTROL","meaning":"余额方向控制"},{"name":"IS_DEL","meaning":"有效标志 0有效/1无效"}],"relations":["t_ncc_account(ACCOUNT_ID)"],"promptHint":"NCC 科目辅助核算；ACCOUNT_ID 关联科目、ACCASS_CODE 辅助核算编码；IS_EMPTY/IS_BALANCE_CONTROL 为控制标志。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_ssdp_configure$t$, $t$$t$, $t$DATA$t$, $t${"businessMeaning":"SSDP 接口配置，记录 EMS 任务编号、连接地址、API 标识与签名密钥。含密钥，敏感。","keyFields":[{"name":"id","meaning":"表自增ID"},{"name":"ssdp_task_code","meaning":"EMS任务编号（主键）"},{"name":"ssdp_task_desc","meaning":"任务描述"},{"name":"model","meaning":"REST/RESTFUL/XML/SOAPXML/HESSIAN等"},{"name":"http_ip","meaning":"连接地址"},{"name":"api_id","meaning":"API标识"},{"name":"sign_secret","meaning":"签名密钥（敏感）"},{"name":"privateKey","meaning":"私钥（敏感）"},{"name":"msg_secret","meaning":"消息密钥（敏感）"},{"name":"app_token","meaning":"应用令牌（敏感）"}],"relations":[],"promptHint":"SSDP 接口配置；含 sign_secret/privateKey/msg_secret/app_token 等密钥（敏感，禁止读取）；ssdp_task_code 为任务编号。"}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();

INSERT INTO governance.ems_table_knowledge (config_id, schema_name, table_name, table_comment, data_status, knowledge)
SELECT c.config_id, $t$basedata$t$, $t$t_category_summary_config$t$, $t$$t$, $t$EMPTY$t$, $t${"businessMeaning":"","keyFields":[],"relations":[],"promptHint":""}$t$
FROM governance.ems_database_connection_config c
WHERE c.name = $t$隆地-UAT-claim$t$
ON CONFLICT (config_id, schema_name, table_name) DO UPDATE SET table_comment = EXCLUDED.table_comment, data_status = EXCLUDED.data_status, knowledge = EXCLUDED.knowledge, updated_at = now();
