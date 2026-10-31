-- 流程图布局与 BPMN 必读参考提示词更新。
UPDATE governance.prompt_template
SET content = replace(content, '输入：', '调查阶段的 sceneAttachmentBodies 可能包含必读 BPMN 结构投影（节点、流转、条件和坐标骨架）；它只用于理解节点类型、条件写法和命名，不得复制样例公司、角色、金额或旧流程标识。信息不足时列入 missingItems。输入：')
WHERE template_code = 'GUIDE_HANDLING'
  AND position('必读 BPMN 结构投影' in content) = 0;

UPDATE task.business_scene_skill
SET investigation_prompt = investigation_prompt || ' 场景存在必读 BPMN 样例时，系统会在第一轮注入其结构投影；请参考节点类型、分支方式、条件写法和布局骨架，只参考结构，不得复制样例公司、角色、金额或旧流程标识。',
    process_artifact_prompt = process_artifact_prompt || ' 输入的 bpmnReference 是场景必读 BPMN 原文，仅用于格式、命名、条件写法和布局骨架参考，不是目标流程事实；不得复制样例公司、角色、金额、旧流程 ID 或部署版本 ID。',
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0'
  AND position('bpmnReference' in process_artifact_prompt) = 0;
