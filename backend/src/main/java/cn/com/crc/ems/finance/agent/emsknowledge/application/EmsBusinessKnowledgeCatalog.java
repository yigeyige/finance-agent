package cn.com.crc.ems.finance.agent.emsknowledge.application;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.emsknowledge.domain.EmsBusinessDomainKnowledge;

/**
 *
 * @Description: 提供不访问 EMS 数据库的内置业务域知识目录。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 00:00:00
 */
@Service
public class EmsBusinessKnowledgeCatalog {

    private static final String STRUCTURE_BOUNDARY = "已核实库、表和部分字段注释的结构快照；跨表业务关系、状态枚举和权威来源仍待样本或代码确认。";

    private static final List<EmsBusinessDomainKnowledge> DOMAINS = List.of(
            domain("MASTER_DATA", "基础主数据与组织权限", "人员、组织、角色、群组及业务类别等共享配置的候选入口。",
                    List.of("basedata"), 410,
                    List.of("sys_register_user", "sys_emp", "sys_emp_job", "sys_user_role", "sys_role", "sys_group",
                            "sys_group_member", "t_org_manger", "t_category_process", "t_approval_rules_head",
                            "t_approval_rules_line", "t_audit_configuration", "t_approval_delegation",
                            "t_project_manage_approve"),
                    "查询人员配置前先确认用户标识、员工标识与工号的映射；涉及逗号分隔人员字段时，先用脱敏样本确认分隔规则。"),
            domain("APPROVAL_WORKFLOW", "审批流程", "流程集、流程版本、节点、连线、处理人和表单绑定的结构目录。",
                    List.of("activiti_ems", "activiti_admin"), 131,
                    List.of("psc_act_prco_set", "psc_act_re_model", "psc_act_re_procdef", "psc_act_config",
                            "psc_act_sequence_flow", "psc_act_assignee", "psc_act_call_activity", "psc_act_exec_filter",
                            "psc_form_proc", "psc_formset_procset", "act_ru_identitylink"),
                    "先从流程集、运行版本、节点配置和处理人配置定位；流程条件表达式、处理人类型与运行版本状态必须结合代码或样本确认。"),
            domain("ACCOUNTING", "会计处理", "会计分单、会计小组、成员与工作范围配置。", List.of("account"), 65,
                    List.of("t_account_assign_config", "t_account_group", "t_account_crew", "t_account_groupwork"),
                    "人员变更场景应核验会计小组成员、替换关系和工作范围，不以表名推断实际生效规则。"),
            domain("BUDGET", "预算管理", "预算科目和预算权限等预算控制配置。", List.of("budget"), 40,
                    List.of("t_budget_account_item", "t_budget_authority_conf"),
                    "权限范围字段可能是编码集合；使用前需确认用户、组织、成本中心和预算科目的实际关联方式。"),
            domain("PAYMENT", "付款管理", "付款工作台权限、付款通道用户权限等配置。", List.of("payment"), 50,
                    List.of("t_payment_permission_config", "t_payment_permission_user_config"),
                    "人员离岗或权限调整时，分别核验工作台默认审核人和付款通道授权，不读取付款单据或流水。"),
            domain("QUALITY", "稽核管理", "稽核计划、事项、内容和关闭记录等稽核配置与任务结构。", List.of("quality"), 69,
                    List.of("t_audit_project", "t_audit_item", "t_audit_item_detail", "t_audit_content", "t_audit_close"),
                    "稽核任务可能携带金额和人员信息；后续场景查询必须建立字段白名单并确认事项关联键。"),
            domain("SMART_AUDIT", "智能审核", "智能审核规则、规则集、审核方案和任务的配置结构。", List.of("smart_audit"), 84,
                    List.of("sa_audit_rule", "sa_rule_collection", "sa_audit_solution", "sa_rule_collection_solution_rel",
                            "sa_audit_claim_main", "sa_ai_extraction_field_mapping"),
                    "规则脚本、提示词和完整报文均属于高风险内容；目录只用于定位，不能据此自动改写或读取原文。"),
            domain("MOBILE", "移动服务", "移动端用户及辅助数据的结构范围。", List.of("mobile"), 24,
                    List.of("t_cost_claim_category", "t_cost_info", "t_draft_templete", "t_process_config_rel",
                            "t_process_templete", "t_process_theme", "t_invoice_main_info", "t_invoice_details"),
                    "表用途以结构快照为准；移动端流程、草稿与费用信息的实际关联需从接口或代码调用链进一步确认。"),
            domain("RUNTIME_CONFIGURATION", "运行配置", "Apollo 配置中心与门户权限的技术运行配置。", List.of("apolloconfigdb", "apolloportaldb"), 32,
                    List.of("app", "appnamespace", "item", "release", "namespace", "serverconfig", "consumer", "role",
                            "rolepermission", "userrole"),
                    "该域仅用于识别运行配置边界，不返回配置值、发布记录细节、账号或连接信息。"));

    /** 返回受控的不可变业务域目录。 */
    public List<EmsBusinessDomainKnowledge> list() {
        return DOMAINS;
    }

    private static EmsBusinessDomainKnowledge domain(String code, String name, String summary, List<String> schemas,
            int tableCount, List<String> keyTables, String verificationHint) {
        return new EmsBusinessDomainKnowledge(code, name, summary, schemas, tableCount, keyTables, STRUCTURE_BOUNDARY,
                verificationHint);
    }
}
