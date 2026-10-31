## ADDED Requirements

### Requirement: Approval scene evidence scope

审批流程生成场景的 EMS 辅助查询 SHALL 只获取流程结构、分支条件和值集所需信息。系统 MUST NOT 查询审核人、用户、角色主数据（如 sys_role、sys_user、sys_employee）来推断审批人；只有需要校验角色代码时，才查询 `approval_config_role` 值集。值集未命中时 MUST 使用用户提供的角色代码并列入 missingItems。

#### Scenario: Role master data is not queried
- **WHEN** 审批流程场景分析用户说明中的角色代码
- **THEN** 模型不查询 sys_role/sys_user/sys_employee 等主数据

#### Scenario: Value set is queried
- **WHEN** 需要校验角色代码
- **THEN** 查询 basedata.sys_value_set_item 的 approval_config_role 值集

#### Scenario: Role code not found
- **WHEN** 值集未返回用户提供的代码
- **THEN** 使用用户代码并列入 missingItems，不继续查主数据
