# Spec Delta

## MODIFIED Requirements

### Requirement: Business scenario Skill management

系统 SHALL 为 OPERATOR 提供业务场景 Skill 的列表、新增、编辑和删除能力。每个 Skill MUST 包含场景名称、调查提示词和决策包提示词；接口 MUST 校验输入字段，删除 MUST 仅对当前有效记录生效并保留操作审计。

#### Scenario: Administrator creates or edits a business scenario Skill
- **WHEN** OPERATOR 提交合法的场景名称、调查提示词和决策包提示词
- **THEN** 系统保存 Skill 并返回最新内容，后续列表显示该 Skill

#### Scenario: Administrator removes a business scenario Skill
- **WHEN** OPERATOR 确认删除一个当前有效的 Skill
- **THEN** 系统将其标记为删除、记录操作者和时间，且后续列表不再显示该 Skill

#### Scenario: Non-operator attempts to maintain Skills
- **WHEN** 非 OPERATOR 身份请求新增、编辑或删除业务场景 Skill
- **THEN** 系统拒绝写操作且不修改任何 Skill
