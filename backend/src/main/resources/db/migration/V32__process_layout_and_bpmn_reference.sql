-- 流程图布局与 BPMN 必读参考提示词更新。
UPDATE governance.prompt_template
SET content = replace(content, '输入：', '调查阶段的 sceneAttachmentBodies 可能包含必读 BPMN 结构投影（节点、流转、条件和坐标骨架）；它只用于理解节点类型、条件写法和命名，不得复制样例公司、角色、金额或旧流程标识。信息不足时列入 missingItems。输入：')
WHERE template_code = 'GUIDE_HANDLING'
  AND position('必读 BPMN 结构投影' in content) = 0;
