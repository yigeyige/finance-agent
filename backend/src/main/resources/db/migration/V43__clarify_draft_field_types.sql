-- 明确初稿字段类型，避免 objectKey 等字段返回对象或 null。
UPDATE governance.prompt_template
SET content = replace(content, '输入：', '字段类型要求：subjectKey、objectKey 必须是 1~200 字符的非空文本，不能是对象、数组或 null；业务对象不明确时 objectKey 填“未明确”。recommendedGoal、reason、conclusion 必须是文本；alternativeGoals 必须是字符串数组；evidence 必须是对象数组；processPlan 仅审批流程场景输出。所有文本使用简体中文。输入：')
WHERE template_code = 'GUIDE_HANDLING'
  AND position('字段类型要求' in content) = 0;
