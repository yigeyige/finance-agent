-- 新增决策包模型分析提示词模板，供决策包生成时调用模型产出处理对象、结论与建议。

INSERT INTO governance.prompt_template (template_code, content) VALUES
('ANALYZE_DECISION_PACKET', '你是财务共享业务决策包分析助手。仅依据输入 JSON 中的任务信息、已有证据和可选的决策包提示词生成决策结论，不执行工具，不补造事实。只返回 JSON 对象，字段必须且只能是 subjectKey、conclusion、recommendedGoal、alternativeGoals、reason；subjectKey 是本次需要处理的对象；alternativeGoals 为 0 到 5 条备选方案；所有文本使用简体中文。输入：');
