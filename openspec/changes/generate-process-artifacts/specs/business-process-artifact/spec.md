## ADDED Requirements

### Requirement: Process artifact generation from definition
系统 SHALL 在任务已有决策包且场景配置了生成文件提示词时，允许管理员触发生成流程文件：模型依据任务信息、决策包方案与场景生成文件提示词产出流程中间表示，Java 转换器按构件模板将其转换为 2~4 个文件（BPMN 定义与节点参数配置必生成；审批规则、部门审批流在对应部分为空时不生成）。系统 MUST 对中间表示做严格校验（节点 id 唯一、流转引用存在、类型与处理人枚举合法），校验或转换失败时整体不落库并返回中文错误。

#### Scenario: Generate artifacts after packet confirmed
- **WHEN** 已有决策包的任务触发生成流程文件且模型返回合法中间表示
- **THEN** 生成 2~4 个文件并落库，中间表示同步留档，旧产物被覆盖

#### Scenario: Reject without packet or prompt
- **WHEN** 任务尚无决策包，或场景未配置生成文件提示词
- **THEN** 拒绝生成并给出中文说明

#### Scenario: Invalid definition rejected
- **WHEN** 模型返回的中间表示存在重复节点 id、引用不存在的节点或非法枚举
- **THEN** 生成失败，不产生任何产物，已有旧产物保持不变

### Requirement: Process artifact persistence and download
系统 SHALL 持久化流程文件产物并支持按任务查询列表与按文件下载；列表与下载 MUST NOT 修改产物内容，产物按任务覆盖式更新，不做版本历史。

#### Scenario: List artifacts
- **WHEN** 管理员查看已生成产物的任务
- **THEN** 返回产物清单（类型、文件名、时间），不含正文

#### Scenario: Download artifact
- **WHEN** 管理员下载指定产物文件
- **THEN** 返回该文件的完整内容
