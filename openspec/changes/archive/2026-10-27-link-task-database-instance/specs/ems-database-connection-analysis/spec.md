# Spec Delta

## ADDED Requirements

### Requirement: Operator-readable enabled instance list

系统 SHALL 为 OPERATOR 提供只读的启用数据库实例列表，供业务处理创建时选择；响应 MUST 仅包含实例标识与名称，MUST NOT 返回连接串、账号、密码、密文或其他连接配置。

#### Scenario: Operator lists enabled instances
- **WHEN** OPERATOR 请求启用实例列表
- **THEN** 系统返回启用实例的标识与名称，不包含任何连接配置或凭据

#### Scenario: No enabled instance
- **WHEN** 没有启用实例
- **THEN** 系统返回空列表且不报错，业务处理仍可在不选择实例的情况下创建

## MODIFIED Requirements

### Requirement: Controlled model analysis data assistance

系统 SHALL 使业务处理模型通过后端受控工具，先基于表目录粗筛候选表，再进行最多三轮只读查询，并将带表溯源的受限结果标记为待核验辅助信息。任务未选择数据库实例、所选实例不可用、无需查询、查询不合规或查询失败时，系统 MUST 继续生成原有业务处理建议。

#### Scenario: Enabled configuration supplies evidence
- **WHEN** 任务已选择启用的 EMS 实例且 Agent 生成通过守卫的查询
- **THEN** 模型获得带表溯源（库名、表名）的受限查询结果作为辅助上下文，而不能获得连接凭据或直接数据库访问能力

#### Scenario: Multiple rounds refine evidence
- **WHEN** 首轮查询结果不足以支撑结论且轮次未达到上限
- **THEN** 系统允许 Agent 基于已有证据和候选表目录继续规划下一轮只读查询，并累积证据

#### Scenario: Database assistance cannot run
- **WHEN** 任务未选择实例、所选实例不可用或查询执行失败
- **THEN** 系统不泄露连接、SQL 或驱动错误详情，并继续既有模型指导流程
