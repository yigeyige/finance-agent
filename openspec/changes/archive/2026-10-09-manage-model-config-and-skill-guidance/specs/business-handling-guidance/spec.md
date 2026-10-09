## ADDED Requirements

### Requirement: Task records selected business scenario Skill
系统 SHALL 允许业务处理任务在创建时关联一个当前有效的业务场景 Skill，并在后续业务处理初稿和会话修订中复用该关联。未选择 Skill 的任务 MUST 保持既有通用业务处理向导能力。

#### Scenario: Administrator starts handling from a business scenario Skill
- **WHEN** OPERATOR 从业务场景 Skill 目录进入新建业务处理并创建任务
- **THEN** 系统将所选 Skill 标识持久化到任务，并在后续模型初稿调用中使用该 Skill 的受控上下文

#### Scenario: Administrator creates a generic task
- **WHEN** OPERATOR 未选择业务场景 Skill 创建任务
- **THEN** 系统创建不关联 Skill 的任务，且业务处理向导仍可生成通用初稿
