-- 将 EMS 表定位、字段语义与查询顺序知识写入业务场景 Skill 的 description（可 UI 配置的固定提示词）
-- 目标表：task.business_scene_skill（skill 创建自业务场景 Skill 目录，本脚本更新 description 使查询规划阶段可复用）

-- 「审批流程处理」：检查 activiti_ems 审批流程，补充人员/审批/角色/组织表定位、字段语义与查询顺序
UPDATE task.business_scene_skill
SET description = '检查 activiti_ems 下是否有审批流程。人员登录账号（USERCODE，如 LILYCHANG、ZENGLINGRUI）在 basedata.sys_register_user.USERCODE 字段；员工基础信息在 activiti_ems.psc_employee（仅明确员工工号时查 psc_employee.employee_emp_id）；审批人配置在 basedata.t_audit_configuration（EMP_IDS 存 USERCODE、EMP_NAMES 存姓名，查某人审批配置用 USERCODE 或姓名匹配 EMP_IDS/EMP_NAMES，不要用数字 USERID 匹配 EMP_IDS，也不要用系统角色编码匹配 ROLE_CODE，ROLE_CODE 是审批环节角色编码如 ZCGLY）；角色在 basedata.sys_role 与 basedata.sys_user_role；组织在 basedata.sys_user_org_rel。查询顺序：先查人员表定位该人员，再查审批、角色或配置表。',
    updated_at = now()
WHERE skill_id = '6a3de26b-3aaa-47f4-afbc-f07a3d6afb72' AND deleted_at IS NULL;

-- 「人员配置查询」：检查 basedata 与 activiti_ems 人员配置，补充相同的表定位、字段语义与查询顺序
UPDATE task.business_scene_skill
SET description = '检查 basedata 与 activiti_ems 下是否有相关人员配置。人员登录账号（USERCODE，如 LILYCHANG、ZENGLINGRUI）在 basedata.sys_register_user.USERCODE 字段；员工基础信息在 activiti_ems.psc_employee（仅明确员工工号时查 psc_employee.employee_emp_id）；审批人配置在 basedata.t_audit_configuration（EMP_IDS 存 USERCODE、EMP_NAMES 存姓名，查某人审批配置用 USERCODE 或姓名匹配 EMP_IDS/EMP_NAMES，不要用数字 USERID 匹配 EMP_IDS，也不要用系统角色编码匹配 ROLE_CODE，ROLE_CODE 是审批环节角色编码如 ZCGLY）；角色在 basedata.sys_role 与 basedata.sys_user_role；组织在 basedata.sys_user_org_rel。查询顺序：先查人员表定位该人员，再查审批、角色或配置表。',
    updated_at = now()
WHERE skill_id = 'e35f1a6c-fc6a-48b1-a527-83e7d40e897f' AND deleted_at IS NULL;
