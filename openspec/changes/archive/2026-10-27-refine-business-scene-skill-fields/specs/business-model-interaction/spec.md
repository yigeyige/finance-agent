# Spec Delta

## MODIFIED Requirements

### Requirement: Guidance call uses the task-linked business scenario Skill

系统 SHALL 在业务处理初稿和会话修订调用时，仅从任务关联的当前有效业务场景 Skill 中读取名称、调查提示词和决策包提示词，并作为受控 `sceneSkill` 上下文投影给模型。系统 MUST NOT 接受浏览器直接提交的 Skill 内容作为模型上下文。

#### Scenario: Task-linked Skill guides a draft
- **WHEN** 任务关联一个当前有效的业务场景 Skill 且 OPERATOR 请求业务处理初稿或会话修订
- **THEN** 系统将该 Skill 的受控字段加入模型输入，并继续执行已有结构化输出校验

#### Scenario: Linked Skill is unavailable
- **WHEN** 任务关联的 Skill 已删除或不再有效
- **THEN** 系统拒绝本次模型调用并提示管理员重新选择有效业务场景，且不发送不完整的模型上下文
