# EMS 关键表初始目录

本目录只收录首轮核查中已经通过 DBX 确认表名、字段或注释的关键表。`关系` 一列未注明数据库外键时均为候选关系。

## 人员、组织与权限

| 表 | 用途 | 关键字段 | 关系与注意事项 | 可信等级 |
|---|---|---|---|---|
| `basedata.sys_register_user` | 系统注册用户 | `USERID`、`USERCODE`、`EMPNO`、`GUID`、`ENABLED`、`DELETED`、有效期 | 账号标识入口；与员工表的实际关联键待样本验证 | `SCHEMA_VERIFIED` |
| `basedata.sys_emp` | 员工主数据 | `EMP_ID`、`OPRID`、`CHN_NAME` | 包含大量个人敏感字段，场景查询必须做最小投影 | `SCHEMA_VERIFIED` |
| `basedata.sys_emp_job` | 员工任职和岗位状态 | `EMPLID`、`EMPL_RCD`、`HR_STATUS`、`EFFDT`、`TERMINATION_DT`、岗位和组织字段 | 候选关联 `EMPLID -> sys_emp.EMP_ID`，尚无样本验证 | `INFERRED` |
| `basedata.sys_user_role` | 用户角色关系 | `USERID`、`ROLEID`、有效期、启用状态 | 候选关联用户和角色 | `SCHEMA_VERIFIED` |
| `basedata.sys_role` | 角色定义 | `ROLEID`、`ROLECODE`、`ORGID`、有效期 | 角色可能同时受组织范围限制 | `SCHEMA_VERIFIED` |
| `basedata.sys_group_member` | 用户群组成员 | `GROUP_ID`、`USER_ID`、启用和删除状态 | 候选关联 `sys_group` 与用户 | `SCHEMA_VERIFIED` |
| `basedata.sys_group` | 用户群组 | `GROUP_ID`、`GROUP_CODE`、组织、有效期 | 需区分业务群组与流程候选人群组 | `SCHEMA_VERIFIED` |
| `basedata.t_org_manger` | 部门管理者配置 | `org_id`、`manager_type`、`emp_no`、`ldap` | 可用于查部门经理、副经理、总监配置 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_employee` | 流程平台人员 | `id`、`employee_emp_id`、`employee_oprid`、部门 | 与基础人员表的同步关系待确认 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_job` | 流程平台岗位 | 人员、岗位、上级岗位、任职状态 | 可用于流程岗位处理人解析 | `SCHEMA_VERIFIED` |

## 流程定义与运行

| 表 | 用途 | 关键字段 | 关系与注意事项 | 可信等级 |
|---|---|---|---|---|
| `activiti_ems.psc_act_prco_set` | 流程集合 | `id`、`proc_set_key`、名称、分类、类型 | 流程业务入口 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_act_re_model` | 流程模型 | `procdef_key`、`version`、`model_id` | 模型与流程版本的候选映射 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_act_re_procdef` | 流程版本 | `procdef_id`、`procdef_key`、`version`、`is_run_version`、部署信息 | `is_run_version` 的实际取值需验证 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_act_config` | 流程节点配置 | `procdef_id`、`act_id`、节点类型、候选人、处理人、多实例和跳过条件 | 节点语义核心表 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_act_sequence_flow` | 节点连线和网关条件 | `procdef_id`、`source_ref`、`target_ref`、`condition_expression` | 可还原流程图；表达式不得未经审查直接交给模型修改 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_act_assignee` | 流程节点任务处理人 | `procdef_id`、`act_id`、`assignee`、`assignee_type` | 处理人标识类型需结合实际值确认 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_act_call_activity` | 子流程配置 | `procdef_id`、`act_id`、`call_element_id` | 子流程引用关系 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_act_exec_filter` | 节点执行前后过滤器 | `procdef_id`、`act_id`、启用状态、过滤器内容 | 含 BLOB 自定义逻辑，应人工审查 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_form_proc` | 表单版本与流程版本绑定 | `form_id`、`procdef_id`、组织和角色范围 | 用于确定流程的表单和适用范围 | `SCHEMA_VERIFIED` |
| `activiti_ems.psc_formset_procset` | 表单集与流程集绑定 | `formset_id`、`procdef_key` | 用于流程发起入口 | `SCHEMA_VERIFIED` |
| `activiti_ems.act_ru_identitylink` | 运行中任务/流程的人员和群组关系 | `USER_ID_`、`GROUP_ID_`、`TASK_ID_`、`PROC_INST_ID_` | 已有 Activiti 外键约束连接运行任务和流程实例 | `CONSTRAINT_VERIFIED` |

## 业务类别、表单与审批配置

| 表 | 用途 | 关键字段 | 关系与注意事项 | 可信等级 |
|---|---|---|---|---|
| `basedata.t_category_process` | 业务大类对应流程 | 流程编码、版本、业务线、组织、业务大类、表单版本 | 连接业务大类、表单和流程版本 | `SCHEMA_VERIFIED` |
| `basedata.t_org_process_template` | 组织流程模板 | 业务、组织、角色、流程模板、审批节点、节点规则 | 字段注释部分乱码，规则枚举待确认 | `SCHEMA_VERIFIED` |
| `basedata.t_approval_rules_head` | 审批规则头 | 流程、版本、业务类别、组织和分支键 | 与规则行通过 `RULES_HEAD_ID` 候选关联 | `SCHEMA_VERIFIED` |
| `basedata.t_approval_rules_line` | 审批规则行 | `RULES_HEAD_ID`、分支值、金额区间、地区、币种 | 未发现数据库外键；关联仍需验证 | `INFERRED` |
| `basedata.t_audit_configuration` | 审核人配置 | 组织、角色、`EMP_IDS`、业务类别、业务线、公司 | `EMP_IDS` 为字符串集合，匹配规则和分隔符待验证 | `SCHEMA_VERIFIED` |
| `basedata.t_approval_delegation` | 审批委托 | 委托人、代理人、时间、组织、流程集和节点 | 委托生效条件、状态码和权威来源待确认 | `PENDING_CONFIRMATION` |
| `basedata.t_project_manage_approve` | 项目审批人配置 | 项目、节点、审批人、金额范围、启用和删除状态 | 项目类固定审批人 | `SCHEMA_VERIFIED` |

## 会计、预算与付款

| 表 | 用途 | 关键字段 | 关系与注意事项 | 可信等级 |
|---|---|---|---|---|
| `account.t_account_assign_config` | 会计分单配置 | BU、公司、业务类别、启停时间、删除状态 | 决定是否按范围进行分单 | `SCHEMA_VERIFIED` |
| `account.t_account_group` | 会计小组 | 部门、小组编码、组长、组类型、删除状态 | 组类型区分费用会计和复核小组 | `SCHEMA_VERIFIED` |
| `account.t_account_crew` | 会计小组成员 | 员工、小组、任务上限、优先级、替换者 | 可支撑会计人员离岗影响分析 | `SCHEMA_VERIFIED` |
| `account.t_account_groupwork` | 会计小组工作范围 | 组织、费用类别、小组、权重、启用状态 | 将业务范围分配给会计小组 | `SCHEMA_VERIFIED` |
| `budget.t_budget_account_item` | 预算会计科目配置 | 具体字段待后续展开 | 已确认表注释 | `SCHEMA_VERIFIED` |
| `budget.t_budget_authority_conf` | 预算权限配置 | 用户、BU、平台、成本中心、预算科目、公司、有效性 | 多个范围字段为字符串集合 | `SCHEMA_VERIFIED` |
| `payment.t_payment_permission_config` | 付款工作台权限配置 | 公司、业务类型、员工、自动审核、默认审核人 | 同时包含操作人和默认审核人 | `SCHEMA_VERIFIED` |
| `payment.t_payment_permission_user_config` | 付款通道用户权限 | 公司、付款通道、用户、删除状态 | 付款权限清理场景候选表 | `SCHEMA_VERIFIED` |

## 稽核与智能审核

| 表 | 用途 | 关键字段 | 关系与注意事项 | 可信等级 |
|---|---|---|---|---|
| `quality.t_audit_project` | 稽核计划 | 计划、负责人、部门、期间、状态和来源计划 | 支持复制来源计划，但复制语义需业务确认 | `SCHEMA_VERIFIED` |
| `quality.t_audit_item` | 稽核事项 | 稽核业务、审核类型、业务大类、组织和启用状态 | 稽核配置对象 | `SCHEMA_VERIFIED` |
| `quality.t_audit_item_detail` | 稽核事项明细 | 事项 ID、稽核内容、业务小类 | 候选关联事项主表 | `SCHEMA_VERIFIED` |
| `quality.t_audit_content` | 稽核任务的抽样和规则范围 | 任务、计划、组织、期间、业务类别、客商、规则和审核会计 | 含金额和人员信息，后续需最小字段白名单 | `SCHEMA_VERIFIED` |
| `quality.t_audit_close` | 稽核任务关闭记录 | 计划、任务、影响金额、来源和关闭说明 | 关闭状态与任务主表关系待补充 | `SCHEMA_VERIFIED` |
| `smart_audit.sa_audit_rule` | 智能审核规则 | 规则编码、版本、执行脚本、有效期、状态、提示词 | 脚本和提示词属于高风险配置，不应直接由模型改写 | `SCHEMA_VERIFIED` |
| `smart_audit.sa_rule_collection` | 审核规则集 | 名称、有效期、状态、业务小类、是否 AI | 规则组合对象 | `SCHEMA_VERIFIED` |
| `smart_audit.sa_audit_solution` | 审核方案 | 名称、有效期、类型和状态 | 审核方案顶层对象 | `SCHEMA_VERIFIED` |
| `smart_audit.sa_rule_collection_solution_rel` | 方案、规则集和规则关系 | 方案 ID、规则集 ID、规则编码、异常策略和排序 | 表注释与字段共同确认组合关系 | `SCHEMA_VERIFIED` |
| `smart_audit.sa_audit_claim_main` | 智能审核报账任务 | 单据、流程节点、方案、OCR 和 AI 任务状态 | `CLAIM_DATA` 为完整报文，禁止无场景授权读取 | `SCHEMA_VERIFIED` |

## 移动服务与运行配置

| 表 | 用途 | 关键字段 | 关系与注意事项 | 可信等级 |
|---|---|---|---|---|
| `mobile.t_process_config_rel` | 移动端流程配置关联 | 关联字段待后续展开 | 用于定位移动流程配置入口，实际关联键待接口或代码确认 | `SCHEMA_VERIFIED` |
| `mobile.t_process_templete` | 移动端流程模板 | 模板字段待后续展开 | 表名沿用原系统拼写；不据此推断审批规则 | `SCHEMA_VERIFIED` |
| `mobile.t_process_theme` | 移动端流程主题 | 主题字段待后续展开 | 与流程模板的实际绑定关系待确认 | `SCHEMA_VERIFIED` |
| `mobile.t_draft_templete` | 移动端草稿模板 | 模板字段待后续展开 | 草稿生命周期和正式业务对象关联待确认 | `SCHEMA_VERIFIED` |
| `mobile.t_cost_claim_category` | 移动端费用类别 | 类别字段待后续展开 | 仅作为费用类别候选入口，不读取费用数据 | `SCHEMA_VERIFIED` |
| `apolloconfigdb.app` | Apollo 应用 | 应用标识 | 技术运行配置，不展示配置值或连接信息 | `SCHEMA_VERIFIED` |
| `apolloconfigdb.item` | Apollo 配置项目 | 配置项字段 | 配置内容属于敏感运行信息，后续只能按授权最小读取 | `SCHEMA_VERIFIED` |
| `apolloconfigdb.release` | Apollo 发布 | 发布字段 | 仅确认结构存在，发布状态语义待确认 | `SCHEMA_VERIFIED` |
| `apolloportaldb.role` | Apollo 门户角色 | 角色字段 | 与门户权限的实际授权语义待确认 | `SCHEMA_VERIFIED` |
| `apolloportaldb.rolepermission` | Apollo 门户角色权限关系 | 关联字段 | 用于定位门户权限关系，关联规则待确认 | `SCHEMA_VERIFIED` |

## 当前待确认事项

1. `sys_register_user`、`sys_emp`、`sys_emp_job` 和 `psc_employee` 的权威关联键及同步方向。
2. 流程版本中 `is_run_version`、删除标志和其他状态码的实际枚举。
3. `basedata` 与业务库同名配置表的权威来源和同步机制。
4. 逗号分隔字段的统一分隔符、转义规则和空值语义。
5. 各类表中的 `0/1`、`Y/N` 状态值是否存在反向定义。
6. 备份表、历史表和当前表的切换规则。
7. 业务表之间未建立数据库外键的候选关系，需要通过 EMS 源代码调用链或脱敏样本补证。
