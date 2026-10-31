## ADDED Requirements

### Requirement: Value set catalog and role-code fallback

系统 SHALL 将 basedata.sys_value_set 和 basedata.sys_value_set_item 纳入业务处理模型的 EMS 候选表目录。当用户说明包含角色代码时，受控提示词 SHALL 引导模型查询 approval_config_role 值集（VS_CODE 为 approval_config_role，角色代码取 VSI_KEY，显示名取 basedata.sys_value_set_item_lang.VSI_VALUE）。值集查询未命中时，系统 SHALL 使用用户提供的角色代码作为流程条件值，并在 missingItems 中标记未在值集表核实，MUST NOT 臆造其他角色代码或阻断初稿生成。

#### Scenario: Value set tables appear in candidate catalog
- **WHEN** 任务选择启用的 EMS 实例且模型请求表目录
- **THEN** 候选目录包含 basedata.sys_value_set 与 basedata.sys_value_set_item

#### Scenario: Role code is verified from value set
- **WHEN** 用户说明包含角色代码 ZYMD 且值集存在 approval_config_role.ZYMD
- **THEN** 模型可取得该角色项作为辅助证据，流程条件使用该代码

#### Scenario: Role code is not found in value set
- **WHEN** 值集查询未返回用户提供的角色代码
- **THEN** 系统仍使用用户提供的代码生成流程条件，并在 missingItems 中标记未核实，不阻断初稿返回
