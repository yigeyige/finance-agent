## MODIFIED Requirements

### Requirement: Versioned task scope persistence

系统 SHALL 通过任务应用服务和 MyBatis-Plus 仓储持久化任务、范围修订和前提，并将范围权限字段以带 `schemaVersion` 的 JSONB 对象保存；范围修订 SHALL 只追加，不提供原位更新；只有确认请求才能绑定新的当前范围修订。

#### Scenario: Scope revision is append-only
- **WHEN** 为任务提交新的范围修订
- **THEN** 仓储写入递增 `revisionNo`、范围摘要和 schema 版本，历史修订保持不变，任务仅更新当前修订引用

#### Scenario: Invalid scope JSON is rejected
- **WHEN** 对象选择或三类字段权限不是严格合法的 JSON object，或 schema 版本为空
- **THEN** 领域边界拒绝写入，不执行数据库更新

#### Scenario: Valid JSON container is accepted
- **WHEN** 范围 JSON 是严格合法的 JSON object 或 JSON array，且 schema 版本非空
- **THEN** `VersionedJson` 可正常构造并继续进入现有领域校验流程

#### Scenario: Trailing JSON content is rejected
- **WHEN** 范围 JSON 在首个 JSON 值之后包含额外 JSON token 或其他非空内容
- **THEN** 领域边界拒绝该值，不执行数据库更新

#### Scenario: Unconfirmed revision is not current
- **WHEN** 范围请求尚未通过确认应用服务
- **THEN** 系统不更新任务的 `currentScopeRevisionId`，步骤审批和运行路径不能引用该请求
