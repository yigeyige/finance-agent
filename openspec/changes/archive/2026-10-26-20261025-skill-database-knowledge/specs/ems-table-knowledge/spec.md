# Spec Delta

## Purpose

按数据库连接实例存储、关联并注入 EMS 表级结构化知识，供业务处理模型的选表与查询规划使用。

## ADDED Requirements

### Requirement: Table knowledge is stored per database
系统 SHALL 以数据库连接实例、库与表的组合唯一存储 EMS 表知识，包含表注释、数据状态（有数据/无数据）与结构化语义（业务用途、关键字段、关联表、提示词片段）。系统 MUST NOT 存储真实数据行或连接凭据。

#### Scenario: Knowledge is read per database
- **WHEN** 系统按数据库连接实例与库读取 EMS 表知识
- **THEN** 返回对应表知识，且不包含真实数据行或敏感字段

#### Scenario: No-data table is marked
- **WHEN** 某表在目标数据库中无数据行
- **THEN** 系统以「无数据」状态记录该表，而不写入详细字段语义

### Requirement: Business scene Skill associates one database
系统 SHALL 允许业务场景 Skill 关联至多一个 EMS 数据库连接实例，并将该关联作为 Skill 的受控属性保存与返回。

#### Scenario: Skill is associated with a database
- **WHEN** 平台管理员为业务场景 Skill 选择并保存一个 EMS 数据库
- **THEN** 系统保存该关联，并在后续读取 Skill 时返回关联的数据库标识

### Requirement: Guidance injects skill-linked table knowledge
系统 SHALL 在业务处理模型的选表与查询规划阶段，仅使用任务关联 Skill 的数据库连接实例，并向模型注入该库对应的表知识提示词片段，而非全局唯一启用连接或浏览器提交的知识。表知识 MUST 作为选表与查询规划的辅助定位参考注入；用户 Skill 说明中的明确表名与指令 MUST 优先于表知识，表知识 MUST NOT 覆盖或改写用户显式意图。

#### Scenario: Skill-linked database supplies table knowledge
- **WHEN** 任务关联的 Skill 已关联一个数据库，且模型进入选表或查询规划
- **THEN** 系统从该数据库的表知识中组装候选表提示并注入模型上下文，且不返回连接凭据

#### Scenario: User instruction takes precedence over table knowledge
- **WHEN** 用户 Skill 说明已明确指定某张表，而表知识提示了其他候选表
- **THEN** 系统以用户明确表名为准，表知识仅作为参考，不覆盖用户意图

#### Scenario: No linked database or knowledge
- **WHEN** 任务关联的 Skill 未关联数据库，或关联库没有表知识
- **THEN** 系统安全降级，继续生成业务处理建议，且不泄露连接或查询细节
