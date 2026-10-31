-- 审批流程场景文件生成：可选提示词与最近两批文件。旧场景和旧决策包不回填。
ALTER TABLE task.business_scene_skill
    ADD COLUMN process_artifact_prompt varchar(4000);

COMMENT ON COLUMN task.business_scene_skill.process_artifact_prompt IS '审批流程文件中间表示生成提示词；空值表示不支持文件生成';

CREATE TABLE task.process_artifact_batch (
    batch_id uuid PRIMARY KEY,
    task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
    packet_id uuid NOT NULL REFERENCES task.business_decision_packet(packet_id),
    packet_revision integer NOT NULL,
    plan_json jsonb NOT NULL,
    missing_items_json jsonb NOT NULL,
    import_status varchar(30) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_process_artifact_import_status CHECK (import_status IN ('IMPORT_CANDIDATE', 'NEEDS_REPAIR'))
);

CREATE INDEX idx_process_artifact_batch_task_created
    ON task.process_artifact_batch(task_id, created_at DESC, batch_id DESC);

CREATE TABLE task.process_artifact_file (
    file_id uuid PRIMARY KEY,
    batch_id uuid NOT NULL REFERENCES task.process_artifact_batch(batch_id) ON DELETE CASCADE,
    kind varchar(30) NOT NULL,
    file_name varchar(180) NOT NULL,
    content_text text NOT NULL,
    CONSTRAINT uq_process_artifact_file_kind UNIQUE (batch_id, kind),
    CONSTRAINT ck_process_artifact_file_kind CHECK (kind IN ('MODEL', 'NODE_PARAMETERS', 'DEPARTMENT_APPROVALS', 'APPROVAL_RULES'))
);

-- 通用模型输出契约仅增加一个可选字段；其余场景仍输出原七字段草稿。
UPDATE governance.prompt_template
SET content = replace(content,
    'draft 必须且只能包含 subjectKey、objectKey、recommendedGoal、alternativeGoals、reason、conclusion、evidence；',
    'draft 必须包含 subjectKey、objectKey、recommendedGoal、alternativeGoals、reason、conclusion、evidence；仅当 sceneSkill.name 为“审批流程生成-隆地”时，draft 还必须包含 processPlan，其他场景不得输出该字段。processPlan 只整理输入中有依据的流程事实，结构为 name、nodes、flows、nodeParameters、departmentApprovals、approvalRules、missingItems、unsupported；nodes 元素为 id、kind、name、assignee；flows 元素为 source、target、condition，condition 可为 null 或 variable/operator/value 对象。不得把样例当作目标流程事实；未知内容列入 missingItems，不支持的会签、并行等列入 unsupported；')
WHERE template_code = 'GUIDE_HANDLING';

UPDATE task.business_scene_skill
SET investigation_prompt = $$本场景只调查目标审批流程，不生成文件。依据用户说明、附件和按需查询的 EMS 证据，逐轮更新 draft.processPlan：name 为流程名；nodes 为已确认的节点（id、kind、name、assignee），kind 仅用 draft/approve/accountant/task-pool/import-erp/exclusive-gateway；flows 为 source、target、condition，起止节点用 start/end，条件为 null 或 variable/operator/value；nodeParameters、departmentApprovals、approvalRules 仅填有依据且适用的配置，分别为 JSON 对象数组；missingItems 列待补信息，unsupported 列会签、并行等暂不能转换的形态及原因。将用户提供、附件提取、数据库佐证和未知内容区分说明；样例只用于格式参考，不得复制其中的公司、角色、金额或旧流程标识。调查结论 conclusion 用中文总结已知结构与缺口；反馈轮次保留已确认事实，按意见修订。只按需查询实际存在的 EMS 表和值集。$$,
    decision_packet_prompt = $$依据输入 guidanceDraft.processPlan 及调查证据生成五字段决策结论。subjectKey 为目标流程，conclusion 概括已知节点与流转并明确待补项和未支持形态；recommendedGoal 给出本次采用的流程方案，reason 说明依据。仅在事实充分时给出具体业务值，不复制样例中的旧流程标识、公司或金额。备选方案供阅读，不参与本次文件生成。本阶段不输出文件正文；不得仅凭缺失证据声称流程完整或可导入。$$,
    process_artifact_prompt = $$你是 EMS 审批流程文件中间表示生成器。只依据输入决策包内固定的 guidanceDraft.processPlan、推荐方案和证据输出一个 JSON 对象，不查询外部数据，不使用当前可变草稿。字段必须且只能为 name、nodes、flows、nodeParameters、departmentApprovals、approvalRules、missingItems、unsupported。nodes 元素必须且只能为 id、kind、name、assignee；kind 仅允许 draft、approve、accountant、task-pool、import-erp、exclusive-gateway；start/end 为隐含节点。flows 元素必须且只能为 source、target、condition；condition 为 null 或仅有 variable、operator、value 的对象，operator 仅允许 ==、!=、>、>=、<、<=。五类任务节点、顺序流转和排他分支以外的形态列入 unsupported，不擅自转换。nodeParameters、departmentApprovals、approvalRules 仅输出适用且有依据的配置对象数组；审批规则每项必须包含 head 对象及 lineList 数组。未知业务值可为空字符串，并列入 missingItems；不得生成旧流程 ID、模型 ID、部署版本 ID、数据库主键或来源账号，Java 转换器和 EMS 导入负责这些标识。不要输出解释文字。输入：$$,
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0';
