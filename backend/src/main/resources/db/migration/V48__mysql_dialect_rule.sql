UPDATE governance.prompt_template
SET content = content || ' EMS 源库是 MySQL，分页只能使用 LIMIT n；禁止 FETCH FIRST、ROWNUM、TOP。'
WHERE template_code = 'PLAN_QUERY'
  AND position('FETCH FIRST' in content) = 0;
