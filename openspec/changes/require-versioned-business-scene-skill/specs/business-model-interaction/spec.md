## MODIFIED Requirements

### Requirement: Guidance call uses the task-linked business scenario Skill
系统 SHALL 在业务处理初稿和会话修订调用时，从任务冻结的业务场景 Skill 修订读取名称、说明、处理方式和业务场景，并与任务业务说明及版本化受控系统模板共同构造模型输入。系统 MUST 校验修订摘要与模板版本，MUST NOT 接受浏览器提交的 Skill 内容、修订号、摘要或提示模板，也 MUST NOT 用业务代码中的具体场景提示覆盖冻结 Skill。

#### Scenario: Frozen Skill guides a draft
- **WHEN** 任务具有可验证的冻结 Skill 修订且 OPERATOR 请求业务处理初稿或会话修订
- **THEN** 系统使用该修订、任务业务说明和受控系统模板构造输入，并继续执行结构化输出校验

#### Scenario: Skill receives a newer revision
- **WHEN** 任务所关联的逻辑 Skill 在任务创建后产生新修订
- **THEN** 本任务模型调用仍使用冻结修订，不读取新的当前修订

#### Scenario: Frozen Skill is unavailable or invalid
- **WHEN** 任务冻结修订不存在、摘要不匹配或系统模板版本不可解析
- **THEN** 系统拒绝模型调用并提示修复任务场景配置，且不发送不完整的模型上下文

#### Scenario: Browser submits Skill content
- **WHEN** 客户端在模型请求中附带 Skill 正文、修订或摘要
- **THEN** 系统忽略或拒绝非契约字段，并仅使用服务端任务冻结记录
