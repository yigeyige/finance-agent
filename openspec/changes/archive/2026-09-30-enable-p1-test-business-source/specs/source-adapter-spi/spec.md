## ADDED Requirements

### Requirement: Test and production source isolation

系统 SHALL 将登记的测试业务源与生产真实源隔离。测试业务源只可使用固定、离线、虚构或已脱敏样本；测试目录版本不匹配、模板未登记、字段权限不明或测试源不可用时 MUST 失败关闭，且不得连接、回退或推断真实源。

#### Scenario: Test source request is invalid

- **WHEN** 测试业务源请求的目录版本、模板、参数或字段权限不符合登记契约
- **THEN** 适配器拒绝请求并且不产生外部读取、真实源回退或伪造成功页

#### Scenario: Test source is unavailable

- **WHEN** 测试业务源不可用或其固定目录无法加载
- **THEN** 适配器返回稳定失败结果，且生产真实源占位仍保持失败关闭
