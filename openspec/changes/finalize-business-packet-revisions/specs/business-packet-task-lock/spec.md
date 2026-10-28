## ADDED Requirements

### Requirement: Task information is read-only after packet generation
系统 SHALL 在任务已有决策包时拒绝修改任务基础信息，包括标题、业务说明、处理对象、场景绑定和数据库实例。后端 MUST 执行此校验；首次生成失败且无已有包时 SHALL 仍允许编辑。

#### Scenario: Task already has a packet
- **WHEN** 请求修改已有决策包的任务基础信息
- **THEN** 后端拒绝修改并给出中文说明

#### Scenario: First generation fails
- **WHEN** 首次生成失败且任务尚无决策包
- **THEN** 任务仍可编辑，不因失败进入只读

### Requirement: Supplementing evidence remains available
已生成包的任务 SHALL 允许在原调查目标下补充证据和再次生成，MUST 保留旧包修订。

#### Scenario: Continue investigation
- **WHEN** 管理员对已有包任务继续补证并生成
- **THEN** 操作允许，任务基础信息仍只读，旧包保留
