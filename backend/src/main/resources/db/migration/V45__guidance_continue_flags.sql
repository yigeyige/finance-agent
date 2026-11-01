-- 初稿输出增加继续分析和重建候选表标志。
UPDATE governance.prompt_template
SET content = replace(content, '输入：', '输出必须包含 needMoreEvidence 和 needTableReselection 两个布尔字段：需要用户继续补充证据时 needMoreEvidence=true；业务范围变化需要重建候选表时 needTableReselection=true。输入：')
WHERE template_code = 'GUIDE_HANDLING'
  AND position('needMoreEvidence' in content) = 0;
