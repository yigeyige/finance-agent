## ADDED Requirements

### Requirement: Registered test business read adapter
系统 SHALL 为已批准的测试业务对象提供独立的只读适配器。适配器 MUST 只使用仓库内固定、离线、虚构或已脱敏 fixture，且 MUST 不包含外部网络客户端、真实凭证、写操作、任意 SQL 或任意 URL。

#### Scenario: Registered template returns fixture page
- **WHEN** 本地或测试环境以已批准目录版本、模板和白名单参数查询测试业务对象
- **THEN** 适配器返回稳定排序、分页标识、水位、来源版本和结果摘要的只读结果页

#### Scenario: Production profile requests test adapter
- **WHEN** prod profile 尝试解析或调用测试业务只读适配器
- **THEN** 系统失败关闭，不读取 fixture，不回退到真实源，也不返回成功页

### Requirement: Test query and field projection are allowlisted
系统 SHALL 在读取 fixture 前校验 SourceCatalog 版本、模板代码、参数名称、页大小和字段投影。未登记或未明确允许的字段 MUST 被拒绝，且禁止字段 MUST 不进入返回结果、日志、快照或模型输入。

#### Scenario: Query contains an unregistered parameter or field
- **WHEN** 查询包含未登记参数、字段或不匹配的目录版本
- **THEN** 适配器返回稳定拒绝结果，且不执行 fixture 读取或任何外部回退

#### Scenario: Query requests permitted fields
- **WHEN** 查询只请求字段矩阵中明确允许的字段
- **THEN** 返回记录仅包含已允许字段并保留目录版本与模板标识

### Requirement: Test adapter detects catalog drift and source failure
系统 SHALL 对测试 SourceCatalog 与 fixture 版本的漂移、缺失或不可读情况失败关闭，并返回稳定、无敏感细节的错误语义。

#### Scenario: Catalog and fixture versions diverge
- **WHEN** 目录声明的来源版本与 fixture 版本不一致
- **THEN** 适配器拒绝查询并返回目录漂移错误，不产生空成功页

#### Scenario: Fixture is unavailable
- **WHEN** fixture 无法加载或结构不符合登记模板
- **THEN** 适配器返回测试源不可用错误，且不访问网络或生产来源
