## ADDED Requirements

### Requirement: Two registered test sources are cross-validated
系统 SHALL 为 `test-personnel-assignment` 使用两个独立且已登记的固定测试来源进行交叉印证。每个来源 MUST 有独立来源标识、固定版本与可审阅的虚构候选摘要；适配器不得将同一来源的重复读取当作第二来源。

#### Scenario: Matching candidates are cross-validated
- **WHEN** 两个已登记来源对同一测试对象返回相同候选摘要
- **THEN** 系统依据目录中唯一最高权威来源返回可审计的已解析来源结果

#### Scenario: Conflicting candidates are cross-validated
- **WHEN** 两个已登记来源对同一测试对象返回不同候选摘要
- **THEN** 系统保留两份候选并返回 `UNKNOWN_SOURCE_CONFLICT`，不产生确认事实且不调用模型裁决

### Requirement: Cross-validation fails closed for missing test source
系统 SHALL 在第二测试来源未登记、不可用、目录版本不匹配或对象键不一致时失败关闭，不回退到主来源的单源确认。

#### Scenario: Verification source is unavailable
- **WHEN** 第二测试来源无法提供已登记对象的候选摘要
- **THEN** 系统返回稳定失败结果，不将主来源候选升级为确认事实
