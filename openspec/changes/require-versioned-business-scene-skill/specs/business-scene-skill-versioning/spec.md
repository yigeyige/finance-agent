## ADDED Requirements

### Requirement: Business scenario Skill revisions are immutable
系统 SHALL 将业务场景 Skill 的逻辑身份与不可变修订分离。每次编辑 MUST 创建新修订并保留旧修订；每个修订 MUST 记录规范化内容摘要、摘要算法版本、创建人和创建时间，且不得原地覆盖。

#### Scenario: Administrator edits a Skill
- **WHEN** OPERATOR 编辑一个当前有效的业务场景 Skill
- **THEN** 系统创建新的不可变修订并将其设为后续任务的当前修订，旧修订仍可由历史任务读取

#### Scenario: Revision content is tampered with
- **WHEN** 持久化修订内容与其记录的摘要不一致
- **THEN** 系统拒绝使用该修订发起模型调用并记录不含提示正文的安全诊断

### Requirement: Task freezes the selected Skill revision
系统 SHALL 在任务创建事务中解析所选 Skill 的当前有效修订，并冻结 Skill 标识、修订标识、修订号、内容摘要和受控系统提示模板版本。后续模型调用 MUST 使用该冻结修订，不得自动切换到较新修订。

#### Scenario: Skill is edited after task creation
- **WHEN** 任务创建后管理员为同一 Skill 创建了新修订
- **THEN** 既有任务继续使用创建时冻结的修订，新任务使用新的当前修订

#### Scenario: Frozen revision cannot be verified
- **WHEN** 任务记录的 Skill 修订、摘要或模板版本缺失或不匹配
- **THEN** 系统拒绝模型调用并提示修复任务场景配置

### Requirement: A default generic Skill is seeded and protected
系统 SHALL 通过可重复执行的数据库迁移提供唯一默认 Skill“通用业务分析”，其处理方式 MUST 为 `INVESTIGATE`，其提示 MUST 要求模型基于用户业务说明识别目标、范围、约束、假设、事实、冲突、缺口和下一步，禁止预设具体系统、表、字段、流程或结论。当前默认 Skill MUST NOT 被直接删除；切换默认项 MUST 在同一事务中维持唯一默认约束。

#### Scenario: Fresh database is migrated
- **WHEN** 数据库首次执行包含默认 Skill 的迁移
- **THEN** 系统存在且仅存在一个可选的默认“通用业务分析” Skill 及不可变首版修订

#### Scenario: Migration is applied to an initialized database
- **WHEN** 稳定业务键对应的默认 Skill 已存在
- **THEN** 迁移不重复插入、不覆盖管理员创建的修订且保持唯一默认约束

#### Scenario: Administrator deletes the current default
- **WHEN** OPERATOR 尝试删除当前默认 Skill
- **THEN** 系统拒绝删除并要求先选择另一个有效默认 Skill

### Requirement: Skill cannot override platform safety boundaries
系统 SHALL 将冻结 Skill 作为业务场景上下文，并由更高优先级的版本化系统模板强制执行权限、只读查询、字段白名单、脱敏和结构化输出约束。Skill 内容 MUST NOT 提供凭据、放宽权限或授权写操作。

#### Scenario: Skill requests a write operation
- **WHEN** Skill 内容要求修改业务数据或绕过只读限制
- **THEN** 系统安全边界继续禁止该操作，且模型工具层不获得写权限
