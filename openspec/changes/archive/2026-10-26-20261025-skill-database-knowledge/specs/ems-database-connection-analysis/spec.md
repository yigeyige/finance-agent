# Spec Delta

## MODIFIED Requirements

### Requirement: Controlled model analysis data assistance
系统 SHALL 使业务处理模型通过后端受控工具，基于任务关联 Skill 的数据库连接实例，先依据表目录与表知识粗筛候选表，再进行最多三轮只读查询，并将带表溯源的受限结果标记为待核验辅助信息。关联库不可用、无需查询、查询不合规或查询失败时，系统 MUST 继续生成原有业务处理建议。

#### Scenario: Enabled configuration supplies evidence
- **WHEN** 任务关联 Skill 已关联可用的 EMS 数据库，且 Agent 生成通过守卫的查询
- **THEN** 模型获得带表溯源（库名、表名）的受限查询结果作为辅助上下文，而不能获得连接凭据或直接数据库访问能力

#### Scenario: Multiple rounds refine evidence
- **WHEN** 首轮查询结果不足以支撑结论且轮次未达到上限
- **THEN** 系统允许 Agent 基于已有证据和候选表目录继续规划下一轮只读查询，并累积证据

#### Scenario: Database assistance cannot run
- **WHEN** 关联库不可用或查询执行失败
- **THEN** 系统不泄露连接、SQL 或驱动错误详情，并继续既有模型指导流程
