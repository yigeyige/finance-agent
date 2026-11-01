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
