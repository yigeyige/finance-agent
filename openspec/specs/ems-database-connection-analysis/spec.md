# EMS Database Connection Analysis Specification

## Purpose

让平台管理员受控维护 EMS MySQL 连接，并让业务处理模型仅通过后端只读工具取得有限的辅助查询结果。
## Requirements
### Requirement: Database instance page compact header and responsive table

数据库实例页面 SHALL 将现有只读安全提醒放在页面标题右侧同一栏。宽屏下配置表格 SHALL 使用可用宽度，避免无意义的横向滚动；窄屏下 SHALL 保留表格内横向滚动与可读列宽。

#### Scenario: Wide administrator viewport

- **WHEN** 管理员在可容纳全部列的宽屏打开数据库实例页面
- **THEN** 提醒位于标题右侧，表格不出现横向滚动条

#### Scenario: Narrow administrator viewport

- **WHEN** 管理员在窄屏打开数据库实例页面
- **THEN** 提醒可换行且表格列保持可读，通过表格区域横向滚动访问

### Requirement: Managed EMS database configuration

系统 SHALL 允许具有 `PLATFORM_ADMIN` 角色的身份维护 EMS MySQL 数据库连接配置，并且允许同时启用多条配置。密码 MUST 使用应用密钥加密保存，任何响应、日志和模型上下文 MUST NOT 返回密码或密文。未配置或长度不足的加密主密钥时，系统 MUST 拒绝保存并返回安全、可操作的配置不可用错误，MUST NOT 将其映射为业务状态冲突。

#### Scenario: Administrator tests a connection

- **WHEN** 平台管理员对有效配置执行连接测试
- **THEN** 系统以只读方式执行固定连通性检查并仅返回成功或安全失败结果

#### Scenario: Non-administrator accesses configuration

- **WHEN** 不具备 `PLATFORM_ADMIN` 角色的身份请求 EMS 数据库配置接口
- **THEN** 系统拒绝请求且不返回连接信息

#### Scenario: Missing encryption master key

- **WHEN** 管理员保存连接且部署未配置有效的加密主密钥
- **THEN** 系统以安全的配置不可用错误拒绝请求，不返回密码、连接串或底层异常

### Requirement: Hard read-only SQL execution guard

系统 SHALL 在任何 EMS 数据库查询被执行前使用统一代码守卫验证 SQL。守卫 MUST 仅允许一条无注释的 `SELECT`，并拒绝分号、多语句、DML、DDL、会话控制、文件读写、危险函数、系统库、`claim`、`xxljob` 与 `arch_db`。

#### Scenario: Agent proposes a write statement

- **WHEN** Agent 提交包含 `UPDATE`、`DELETE`、`TRUNCATE`、`DROP` 或其他非只读语法的查询
- **THEN** 系统在创建 JDBC 语句前拒绝该查询，且不访问 EMS 数据库

#### Scenario: Agent proposes a valid select statement

- **WHEN** Agent 提交单条、符合业务库边界的 `SELECT`
- **THEN** 系统以 JDBC 只读连接、查询超时和最大返回行数执行，并仅产生受限结果投影

### Requirement: Operator-readable enabled instance list

系统 SHALL 为 OPERATOR 提供只读的启用数据库实例列表，供业务处理创建时选择；响应 MUST 仅包含实例标识与名称，MUST NOT 返回连接串、账号、密码、密文或其他连接配置。

#### Scenario: Operator lists enabled instances
- **WHEN** OPERATOR 请求启用实例列表
- **THEN** 系统返回启用实例的标识与名称，不包含任何连接配置或凭据

#### Scenario: No enabled instance
- **WHEN** 没有启用实例
- **THEN** 系统返回空列表且不报错，业务处理仍可在不选择实例的情况下创建

### Requirement: Controlled model analysis data assistance

系统 SHALL 使业务处理模型通过后端受控工具定位候选表，再进行最多三轮只读查询，并将带表溯源的受限结果标记为待核验辅助信息。任务选择的实例从未成功同步配置结构目录时，系统 SHALL 保持现有直连表目录与字段元数据的辅助查询行为；实例已有成功同步目录时，系统 SHALL 从该实例本地目录进行关键词检索，复用一次模型选表且最多选择 10 张表，选中表字段 MUST 来自同一目录快照，不得为选表或字段再次访问 EMS 元数据。已同步目录关键词未命中时，系统 MUST 明确提示并终止本次模型分析；任务未选择实例、所选实例不可用、无需查询、查询不合规或查询失败时，系统 MUST 继续生成原有业务处理建议。实际配置值查询 MUST 继续通过现有只读 SQL 守卫、白名单、超时和行数约束。

#### Scenario: Enabled configuration supplies evidence

- **WHEN** 任务已选择启用的 EMS 实例且 Agent 生成通过守卫的查询
- **THEN** 模型获得带表溯源（库名、表名）的受限查询结果作为辅助上下文，而不能获得连接凭据或直接数据库访问能力

#### Scenario: Multiple rounds refine evidence

- **WHEN** 首轮查询结果不足以支撑结论且轮次未达到上限
- **THEN** 系统允许 Agent 基于已有证据和候选表目录继续规划下一轮只读查询，并累积证据；总查询规划轮次仍最多为三轮

#### Scenario: Instance has never been synchronized

- **WHEN** 任务选择的实例没有成功同步记录
- **THEN** 系统沿用当前直连 EMS 结构元数据的辅助查询流程及原有选表边界

#### Scenario: Synced catalog matches the task

- **WHEN** 任务选择的实例已有成功同步目录且关键词命中表或字段
- **THEN** 系统从该目录构造有界候选，模型一次最多选择 10 张表，并从同一快照读取字段，不为这些结构信息查询 EMS `information_schema`；后续实际配置数据仍连接所选 EMS 实例执行受控只读查询

#### Scenario: Synced catalog does not match

- **WHEN** 任务选择的实例已有成功同步目录但关键词无候选，包括目录为空
- **THEN** 系统返回明确中文提示并终止本次模型分析，不回退到 EMS 结构查询，不继续模型选表或实际配置值查询

#### Scenario: Database assistance cannot run

- **WHEN** 任务未选择实例、所选实例不可用或查询执行失败
- **THEN** 系统不泄露连接、SQL 或驱动错误详情，并继续既有模型指导流程

### Requirement: Workbench configuration entry

前端 SHALL 在“大模型配置”下提供受权限保护的“数据库实例”入口，供平台管理员管理和测试连接。

#### Scenario: Administrator opens configuration page

- **WHEN** 平台管理员点击侧栏中的数据库实例
- **THEN** 页面展示受控配置表单、状态、启停和测试操作，密码字段不回填

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

### Requirement: Approval investigation query scope

审批流程调查提示词 SHALL 先定位唯一 `procdef_id`，再分别查询节点和流转；MUST 禁止把 `psc_act_config` 与 `psc_act_sequence_flow` 按 `procdef_id` 直接 join；MUST 禁止无流程名称或 `procdef_id` 条件的全表查询；单次查询超过 30 行 SHALL 停止并细化条件。

#### Scenario: Resolve process definition
- **WHEN** 用户说明包含流程名称
- **THEN** 先按 `act_re_model.NAME_` 和 `act_re_procdef.key_` 定位唯一 `procdef_id`，并记录使用的版本

#### Scenario: Query nodes and flows separately
- **WHEN** 已取得 `procdef_id`
- **THEN** 分别查询 `psc_act_config` 和 `psc_act_sequence_flow`，不在一张 SQL 中同时 join

#### Scenario: Row amplification
- **WHEN** 查询返回超过 30 行
- **THEN** 停止并细化条件，不把全部行注入证据

#### Scenario: Title differs from process name
- **WHEN** 任务标题与用户说明中的流程名不一致
- **THEN** 以用户说明中的流程名为准，并在 missingItems 记录不一致

