# Spec Delta

## MODIFIED Requirements

### Requirement: Guidance call uses the task-linked business scenario Skill
系统 SHALL 在业务处理初稿和会话修订调用时，从任务关联的当前有效业务场景 Skill 中读取名称、说明、处理方式、业务场景及关联的数据库连接实例，作为受控 `sceneSkill` 上下文投影给模型。任务未关联 Skill 时，系统 SHALL 降级为不查询 EMS 数据库的纯文本分析。系统 MUST NOT 接受浏览器直接提交的 Skill 内容或数据库作为模型上下文。

#### Scenario: Task-linked Skill guides a draft
- **WHEN** 任务关联一个当前有效的业务场景 Skill，且 OPERATOR 请求业务处理初稿或会话修订
- **THEN** 系统将该 Skill 的受控字段及关联数据库加入模型输入，并继续执行已有结构化输出校验

#### Scenario: No linked Skill degrades to text-only
- **WHEN** 任务未关联业务场景 Skill
- **THEN** 系统跳过 EMS 数据库查询，仅以任务说明生成纯文本初稿，并继续结构化输出校验

#### Scenario: Linked Skill is unavailable
- **WHEN** 任务关联的 Skill 已删除或不再有效
- **THEN** 系统拒绝本次模型调用并提示管理员重新选择有效业务场景，且不发送不完整的模型上下文
