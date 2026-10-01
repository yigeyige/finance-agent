# Test Business Source Contract

## Purpose

定义 P1 测试业务源的目录、字段权限、样本、查询与数据边界契约，并将固定测试 fixture、交叉印证证据和 local/test 运行边界与未来生产真实源接入严格隔离。

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

### Requirement: First test business adapter acceptance record

系统 SHALL 为首个测试业务只读适配器交付可审阅的 SourceCatalog 版本、查询模板、字段投影、fixture 版本和 TCK 结果。该记录 MUST 明确其仅适用于 local/test，且不得包含真实人员、金额、单据、凭证、生产配置或连接信息。

#### Scenario: Adapter technical acceptance is reviewed
- **WHEN** 首个测试业务只读适配器完成技术验收
- **THEN** 验收记录能够核对目录、字段投影、只读限制、拒绝路径、契约漂移和故障语义的测试结果

### Requirement: Test source cross-validation evidence and scope confirmation

系统 SHALL 交付第二测试来源的目录版本、来源权威顺序、一致/冲突样本、TCK 结果和测试业务范围确认记录。确认记录 MUST 标明当前会话项目负责人、确认日期和仅限 local/test 的边界，且 MUST NOT 构成真实源、生产认证、字段授权或生产放行批准。

#### Scenario: Technical cross-validation package is reviewed
- **WHEN** 测试交叉印证技术包被审阅并完成测试业务范围确认
- **THEN** 审阅者能够核对两个测试来源、对象范围、冲突保留、失败关闭和确认边界

### Requirement: P1 multi-type reference sample baseline
系统 SHALL 为 P1 测试业务源维护版本化的多类型参考样本与黄金事实，覆盖人员、岗位、角色、流程节点、组织/权限范围以及当前、未来、未知和无匹配情形。该基线 MUST 使用固定虚构或已脱敏样本，并且 MUST 与真实业务源和生产数据隔离。

#### Scenario: P1 reference sample is selected
- **WHEN** 测试执行选择已登记的多类型参考样本
- **THEN** 系统能够按样本版本核对期望事实、适用范围和非确认状态，且不读取外部来源

### Requirement: P1 test business pilot is local/test and read-only
系统 SHALL 将 P1 试点限定为 local/test 中的固定虚构或已脱敏测试业务样本。试点准备、人工验证和问题清单 MUST 不访问真实源、不写入源、不读取禁止字段，且 MUST NOT 推断实际业务影响或生产事实。

#### Scenario: P1 test business pilot is prepared
- **WHEN** 系统准备 P1 测试业务试点任务包
- **THEN** 任务包明确测试范围、只读边界、人工验证步骤和非生产限制
