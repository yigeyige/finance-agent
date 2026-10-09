## ADDED Requirements

### Requirement: Guidance call uses the task-linked business scenario Skill
系统 SHALL 在业务处理初稿和会话修订调用时，仅从任务关联的当前有效业务场景 Skill 中读取名称、说明、处理方式和业务场景，并作为受控 `sceneSkill` 上下文投影给模型。系统 MUST NOT 接受浏览器直接提交的 Skill 内容作为模型上下文。

#### Scenario: Task-linked Skill guides a draft
- **WHEN** 任务关联一个当前有效的业务场景 Skill 且 OPERATOR 请求业务处理初稿或会话修订
- **THEN** 系统将该 Skill 的受控字段加入模型输入，并继续执行已有结构化输出校验

#### Scenario: Linked Skill is unavailable
- **WHEN** 任务关联的 Skill 已删除或不再有效
- **THEN** 系统拒绝本次模型调用并提示管理员重新选择有效业务场景，且不发送不完整的模型上下文

### Requirement: Live model calls resolve the enabled persisted connection
系统 SHALL 在每次真实模型调用开始前解析当前唯一启用的持久化模型连接。没有启用连接时 MUST 安全拒绝调用；Stub 调用 MUST 保持显式标识，且不得伪装为真实模型调用。

#### Scenario: No live model connection is enabled
- **WHEN** OPERATOR 请求真实模型交互且不存在启用模型连接
- **THEN** 系统返回“请先启用大模型配置”的业务错误，不保存模型结果或生成伪结果
