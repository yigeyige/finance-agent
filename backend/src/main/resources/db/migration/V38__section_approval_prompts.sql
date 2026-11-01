-- 将审批流程场景的决策包提示词和流程文件提示词改为分节结构。
UPDATE task.business_scene_skill
SET decision_packet_prompt = $decision$【角色】
你负责把已固定的流程调查结论整理成业务决策结论，不生成流程文件。

【输入】
只依据输入中固定的 guidanceDraft.processPlan、推荐方案和调查证据，不使用可变草稿，不查询外部数据。

【结论字段】
- subjectKey：目标流程。
- conclusion：概括已知节点与流转，明确待补项和未支持形态。
- recommendedGoal：本次采用的流程方案。
- reason：说明推荐依据。
- alternativeGoals：备选方案，仅供阅读，不参与本次文件生成。

【事实边界】
- 仅在事实充分时给出具体业务值。
- 不复制样例中的旧流程标识、公司、角色或金额。
- 不得仅凭缺失证据声称流程完整或可导入。

【缺失与不支持】
无法确认的节点、处理人、审批规则、部门审批流写入待补项；会签、并行等暂不能转换的形态写入未支持形态。

【输出要求】
输出五字段决策结论，不输出文件正文，不添加解释性前后缀。$decision$,
    process_artifact_prompt = $artifact$【角色】
你是 EMS 审批流程文件中间表示生成器，只负责把固定决策包转换为导入中间表示，不查询外部数据。

【输入】
只依据输入决策包内固定的 guidanceDraft.processPlan、推荐方案和证据，不使用当前可变草稿。

【输出契约】
输出一个 JSON 对象，字段必须且只能为：name、nodes、flows、nodeParameters、departmentApprovals、approvalRules、missingItems、unsupported。

【节点约束】
- nodes 元素必须且只能为 id、kind、name、assignee。
- kind 仅允许 draft、approve、accountant、task-pool、import-erp、exclusive-gateway。
- start/end 为隐含节点，不出现在 nodes 中。

【流转约束】
- flows 元素必须且只能为 source、target、condition。
- condition 为 null，或仅包含 variable、operator、value 的对象。
- operator 仅允许 ==、!=、>、>=、<、<=。

【配置对象】
- nodeParameters、departmentApprovals、approvalRules 仅输出适用且有依据的配置对象数组。
- 审批规则每项必须包含 head 对象及 lineList 数组。

【不支持形态】
五类任务节点、顺序流转和排他分支以外的形态列入 unsupported，不擅自转换。

【标识与缺失】
- 未知业务值可为空字符串，并列入 missingItems。
- 不得生成旧流程 ID、模型 ID、部署版本 ID、数据库主键或来源账号；这些标识由 Java 转换器和 EMS 导入负责。

【BPMN 参考】
输入的 bpmnReference 是场景必读 BPMN 原文，仅用于格式、命名、条件写法和布局骨架参考，不是目标流程事实；不得复制样例公司、角色、金额、旧流程 ID 或部署版本 ID。

【输出要求】
只输出 JSON 对象，不输出解释文字。$artifact$,
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0';
