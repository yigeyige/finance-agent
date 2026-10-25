## MODIFIED Requirements

### Requirement: Task records selected business scenario Skill
系统 SHALL 要求业务处理任务在创建时关联一个当前有效的业务场景 Skill，并在创建事务中冻结该 Skill 的不可变修订、内容摘要和受控提示模板版本。业务处理页面 MUST 默认选择服务端声明的“通用业务分析” Skill；服务端 MUST NOT 为缺失 Skill 的请求隐式补默认值。

#### Scenario: Administrator starts handling from a business scenario Skill
- **WHEN** OPERATOR 选择一个当前有效的业务场景 Skill 并创建任务
- **THEN** 系统将所选 Skill 的当前修订及版本元数据冻结到任务，并在后续模型调用中使用该修订

#### Scenario: Administrator opens generic task creation
- **WHEN** OPERATOR 打开未预指定场景的新建业务处理页面
- **THEN** 页面默认选择唯一有效的“通用业务分析” Skill，且管理员可在提交前改选其他有效 Skill

#### Scenario: Task request omits a Skill
- **WHEN** 客户端直接提交未包含业务场景 Skill 标识的任务创建请求
- **THEN** 系统拒绝创建并返回可理解的必选校验错误，且不隐式绑定默认 Skill

#### Scenario: Selected Skill becomes unavailable
- **WHEN** 任务创建事务解析到不存在、已删除或没有当前修订的 Skill
- **THEN** 系统拒绝创建任务且不保存不完整的冻结引用
