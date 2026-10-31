-- 初稿生成阶段容忍流程结构偏差，并补充值集查询与输出自检规则。
UPDATE governance.prompt_template
SET content = replace(content, '输入：', '输出前自检：nodes 不包含 start/end；每个节点 id 仅使用字母、数字、下划线；kind 仅使用 draft、approve、accountant、task-pool、import-erp、exclusive-gateway；assignee 必须是字符串，未知用空字符串；condition 的 operator 仅用 ==、!=、>、>=、<、<=，value 仅用字母、数字、下划线、短横线；missingItems 与 unsupported 每项非空。角色代码优先查询 basedata.sys_value_set_item（VS_CODE 为 approval_config_role，代码取 VSI_KEY，显示名取 basedata.sys_value_set_item_lang.VSI_VALUE）；查不到时直接使用用户提供的角色代码，并列入 missingItems 标明未核实。信息不足时输出合法空数组和 missingItems，不得输出非法枚举或 null。输入：')
WHERE template_code = 'GUIDE_HANDLING'
  AND position('输出前自检' in content) = 0;

UPDATE task.business_scene_skill
SET investigation_prompt = investigation_prompt || ' 输出前自检：nodes 不包含 start/end；节点 id 仅使用字母、数字、下划线；assignee 未知用空字符串；kind 仅用 draft、approve、accountant、task-pool、import-erp、exclusive-gateway；condition 的 value 仅用字母、数字、下划线、短横线。角色代码优先查询 basedata.sys_value_set_item（VS_CODE 为 approval_config_role，代码取 VSI_KEY）；查不到时使用用户提供的角色代码并列入 missingItems 标明未核实。',
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0'
  AND position('输出前自检' in investigation_prompt) = 0;
