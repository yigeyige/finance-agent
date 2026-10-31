## MODIFIED Requirements

### Requirement: Controlled model analysis data assistance

系统 SHALL 使业务处理模型通过后端受控工具定位候选表，再进行最多三轮只读查询，并将带表溯源的受限结果标记为待核验辅助信息。任务选择的实例从未成功同步配置结构目录时，系统 SHALL 保持现有直连表目录与字段元数据的辅助查询行为；实例已有成功同步目录时，系统 SHALL 从该实例本地目录进行关键词检索，复用一次模型选表且最多选择 10 张表，选中表字段 MUST 来自同一目录快照，不得为选表或字段再次访问 EMS 元数据。已同步目录关键词未命中时，系统 MUST 明确提示并终止本次模型分析；任务未选择实例、所选实例不可用、查询不合规或查询失败时，系统 MUST 继续生成原有业务处理建议。实际配置值查询 MUST 继续通过现有只读 SQL 守卫、白名单、超时和行数约束。

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
