## ADDED Requirements

### Requirement: Cross-round evidence snapshots

业务处理每轮分析 SHALL 加载上一轮证据，并与本轮修改意见、新查询结果合并为完整证据快照。系统 MUST 在有新增证据时保存新快照，MUST NOT 把上一轮证据丢弃；无新增证据时保留上一轮，不创建空轮次。决策包 SHALL 使用最新一轮快照；历史增量轮次 MUST 通过快照标记兼容。

#### Scenario: Second round inherits previous evidence
- **WHEN** 用户在第一轮后发送修改意见
- **THEN** 第二轮模型上下文包含第一轮证据和本轮修改意见

#### Scenario: Round snapshot is saved
- **WHEN** 本轮产生新证据
- **THEN** 系统保存“上一轮 + 本轮”去重后的完整快照，并记录新轮次

#### Scenario: No new evidence
- **WHEN** 本轮没有产生新证据
- **THEN** 系统保留上一轮快照，不创建空的新轮次

#### Scenario: Decision packet uses latest snapshot
- **WHEN** 决策包生成时存在多轮证据
- **THEN** 只使用最新一轮快照，不合并更早轮次的独有证据

#### Scenario: Legacy incremental evidence
- **WHEN** 最新一轮是历史增量数据（snapshot=false）
- **THEN** 系统按旧逻辑合并全部轮次，保证旧任务可用

## ADDED Requirements

### Requirement: Second round keeps required BPMN projection

第二轮附件分析 SHALL 同时包含第一轮自动注入的必读 BPMN 结构投影和模型请求的附件正文，并按文件名去重。系统 MUST NOT 因第二轮请求附件而丢失必读投影。

#### Scenario: Required projection is retained
- **WHEN** 模型第一轮请求额外附件并进入第二轮
- **THEN** 第二轮上下文仍包含 BPMN 结构投影

#### Scenario: No duplicate attachments
- **WHEN** 模型请求的文件与必读附件同名
- **THEN** 第二轮上下文只注入一次该文件

## ADDED Requirements

### Requirement: Decision packet rejects empty evidence

决策包生成 SHALL 在证据为空时拒绝并返回中文错误，MUST NOT 调用模型。

#### Scenario: Empty evidence
- **WHEN** 任务没有可用的证据快照
- **THEN** 系统返回“尚无调查证据，请先完成调查”，不调用模型
