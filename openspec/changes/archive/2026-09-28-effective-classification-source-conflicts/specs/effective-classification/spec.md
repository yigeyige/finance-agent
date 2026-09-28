## MODIFIED Requirements

### Requirement: Time-based effective classification

系统 SHALL 使用调用方注入的检查时钟和半开生效区间 `[effectiveFrom, effectiveTo)` 分类事实，不读取系统默认时区或当前时间。

#### Scenario: Current and future facts
- **WHEN** 检查时点位于生效区间内或早于生效开始时间
- **THEN** 系统分别返回 `CURRENT_EFFECTIVE` 或 `FUTURE_SCHEDULED`

#### Scenario: Missing or invalid timing
- **WHEN** 生效开始时间缺失、区间非法或检查时点已超出结束时间
- **THEN** 系统返回 `UNDETERMINED`

### Requirement: Catalog-driven source authority

系统 SHALL 使用 `SourceCatalog` 中按对象类型登记的来源权威顺序裁决来源候选，不得使用模型或候选输入顺序裁决。

#### Scenario: Unique authoritative source
- **WHEN** 同一对象存在多个来源候选且目录登记了唯一最高权威来源
- **THEN** 系统返回该来源作为唯一裁决结果

#### Scenario: Unknown source conflict
- **WHEN** 对象无权威登记、最高权威并列或对象/来源标识缺失
- **THEN** 系统返回 `UNKNOWN_SOURCE_CONFLICT` 并保留冲突候选
