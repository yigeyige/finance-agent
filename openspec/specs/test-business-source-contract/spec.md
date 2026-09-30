# Test Business Source Contract

## Purpose

定义 P1 测试业务源的目录、字段权限、样本、查询和数据边界契约，并与生产真实源保持隔离。

## Requirements

### Requirement: P1 test business source catalog

系统 SHALL 将 P1 试点限定为登记的测试业务源；其 SourceCatalog、版本、查询模板、参数白名单、对象权威级别、字段权限和样本语义 MUST 以可审阅的仓库交付物冻结。目录不得引用真实人员、金额、单据、凭证、生产配置或生产连接串。

#### Scenario: Registered test catalog is used

- **WHEN** P1 测试业务源执行登记查询
- **THEN** 它只使用已冻结的目录版本、模板和参数，并返回可重复的虚构或脱敏样本

#### Scenario: Test catalog contains prohibited data

- **WHEN** 目录、字段矩阵、查询样本或执行产物出现真实业务数据、凭证或生产连接信息
- **THEN** 该交付物验证失败，且不得作为 P1 入口或试点证据

### Requirement: Test field permission matrix

系统 SHALL 为 P1 测试业务源的每个字段记录读取、留存和送模三类权限，以及逻辑名称、测试物理字段、分类和适用场景。未明确允许的字段 MUST 视为禁止；测试身份标识不得映射为真实身份或送入模型。

#### Scenario: Field is explicitly permitted

- **WHEN** 测试查询使用字段矩阵中已允许的字段
- **THEN** 系统仅按照该字段的读取、留存和送模权限处理，并保留目录版本和场景标识

#### Scenario: Field permission is missing

- **WHEN** 测试查询、持久化或模型输入包含未登记字段或未知权限
- **THEN** 系统拒绝该字段，不将其读取、留存或送入模型

### Requirement: P1 test samples preserve unknown and conflict semantics

系统 SHALL 为 P1 测试业务源提供有效、无效、未知、冲突、分页失败和未来生效样本；`UNKNOWN` 与 `CONFLICT` MUST 保持为非确认结果，不能由模型、候选顺序或默认值改写为确认事实。

#### Scenario: Unknown or conflict sample is evaluated

- **WHEN** 测试源返回 `UNKNOWN` 或 `CONFLICT` 分类
- **THEN** 结果保留该分类并要求人工或后续权威来源处理，而不是产生确认事实

#### Scenario: Unregistered query is requested

- **WHEN** 请求未登记模板、参数、目录版本或任意 SQL/URL
- **THEN** 测试源拒绝请求，不访问外部系统且不回退到真实源

### Requirement: P1 completion does not approve production source access

P1 测试业务源的目录确认、样本验证和任务完成 SHALL 仅证明本地/测试范围内的契约；它们 MUST NOT 解除真实源、生产统一认证、真实字段授权或生产部署的 P2 门禁。

#### Scenario: P1 test task is completed

- **WHEN** P1 测试业务源任务被标记完成
- **THEN** 生产接入仍要求独立的真实对象、字段权限、凭证、责任人和验收证据
