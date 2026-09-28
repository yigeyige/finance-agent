## MODIFIED Requirements

### Requirement: Stable source adapter SPI

系统 SHALL 通过 `ConfigSourceAdapter` 的统一只读契约接收 `ApprovedQuery` 并返回包含分页、水位和结果摘要的 `ScopePage`，适配器不得承载写操作或任意查询片段。

#### Scenario: Adapter contract implementation
- **WHEN** 适配器收到合法目录版本和已批准模板参数
- **THEN** 返回稳定的 `ScopePage`，并保留页号、路径元数据和结果摘要

#### Scenario: Invalid query is rejected
- **WHEN** 查询缺少模板、目录版本或包含未登记参数
- **THEN** 适配器拒绝请求，不执行外部写入或回退到合成数据

### Requirement: Production source placeholder fails closed

系统 SHALL 在 prod 未配置真实 `SourceCatalog` 或真实只读源时返回稳定错误码，不产生确认事实或伪造成功页。

#### Scenario: Real source is not configured
- **WHEN** prod profile 调用真实源占位适配器
- **THEN** 返回 `SOURCE_NOT_CONFIGURED` 或 `SOURCE_CATALOG_REQUIRED` 错误，不返回空成功结果

### Requirement: Recheck result contract

系统 SHALL 使用不可变 `RecheckResult` 表达适配器复查状态和结果摘要，便于未来适配器通过同一契约交付。

#### Scenario: Recheck result is stable
- **WHEN** 适配器完成只读复查
- **THEN** 返回包含状态、错误码/说明和结果摘要的 `RecheckResult`
