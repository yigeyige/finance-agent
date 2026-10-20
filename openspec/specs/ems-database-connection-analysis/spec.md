# EMS Database Connection Analysis Specification

## Purpose

让平台管理员受控维护 EMS MySQL 连接，并让业务处理模型仅通过后端只读工具取得有限的辅助查询结果。

## Requirements

### Requirement: Managed EMS database configuration

系统 SHALL 允许具有 `PLATFORM_ADMIN` 角色的身份维护 EMS MySQL 数据库连接配置，并且最多启用一条配置。密码 MUST 使用应用密钥加密保存，任何响应、日志和模型上下文 MUST NOT 返回密码或密文。未配置或长度不足的加密主密钥时，系统 MUST 拒绝保存并返回安全、可操作的配置不可用错误，MUST NOT 将其映射为业务状态冲突。

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

### Requirement: Controlled model analysis data assistance

系统 SHALL 使业务处理模型通过后端受控工具，先基于表目录粗筛候选表，再进行最多三轮只读查询，并将带表溯源的受限结果标记为待核验辅助信息。未启用连接、无需查询、查询不合规或查询失败时，系统 MUST 继续生成原有业务处理建议。

#### Scenario: Enabled configuration supplies evidence

- **WHEN** 存在启用的 EMS 连接且 Agent 生成通过守卫的查询
- **THEN** 模型获得带表溯源（库名、表名）的受限查询结果作为辅助上下文，而不能获得连接凭据或直接数据库访问能力

#### Scenario: Multiple rounds refine evidence

- **WHEN** 首轮查询结果不足以支撑结论且轮次未达到上限
- **THEN** 系统允许 Agent 基于已有证据和候选表目录继续规划下一轮只读查询，并累积证据

#### Scenario: Database assistance cannot run

- **WHEN** 没有启用连接或查询执行失败
- **THEN** 系统不泄露连接、SQL 或驱动错误详情，并继续既有模型指导流程

### Requirement: Workbench configuration entry

前端 SHALL 在“大模型配置”下提供受权限保护的“EMS数据库配置”入口，供平台管理员管理和测试连接。

#### Scenario: Administrator opens configuration page

- **WHEN** 平台管理员点击侧栏中的 EMS 数据库配置
- **THEN** 页面展示受控配置表单、状态、启停和测试操作，密码字段不回填
