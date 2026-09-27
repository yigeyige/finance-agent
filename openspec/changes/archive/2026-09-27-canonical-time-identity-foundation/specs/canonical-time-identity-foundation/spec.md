## ADDED Requirements

### Requirement: Deterministic canonical JSON and digest

系统 SHALL 使用统一 canonical JSON 编码和 UTF-8 SHA-256 摘要；对象字段顺序、系统默认时区不得影响摘要，数组顺序必须保持语义顺序。

#### Scenario: Field order does not change digest

- **WHEN** 两个 JSON 对象仅字段顺序不同
- **THEN** canonical JSON 和 SHA-256 摘要完全一致

#### Scenario: Numeric and null boundaries are explicit

- **WHEN** 输入包含 `1`、`1.0`、`0.00`、null、Unicode 字符或非 JSON 浮点值
- **THEN** 合法数字按固定规则归一化，null/Unicode 保留，NaN/Infinity 被拒绝

#### Scenario: Array order remains significant

- **WHEN** 两个 JSON 仅数组元素顺序不同
- **THEN** canonical JSON 和摘要不同

### Requirement: Unified UUID and injectable UTC time

系统 SHALL 通过共享 UUID 生成/解析入口和注入的 UTC `Clock` 提供身份与时间能力；禁止新业务逻辑依赖系统默认时区或隐式当前时间。

#### Scenario: Clock controls current time

- **WHEN** 使用固定 `Clock` 请求当前时间
- **THEN** 返回固定 UTC `Instant`，测试结果不依赖机器时钟

#### Scenario: UUID round-trips through persistence format

- **WHEN** 领域 UUID 转换为 MyBatis 存储文本再转换回来
- **THEN** UUID 值保持一致并支持无连字符 ASSIGN_UUID 文本

### Requirement: UTC API and Shanghai UI time boundary

系统 SHALL 以 ISO-8601 UTC 传输 API 时间；前端工作台 SHALL 将 UTC 时间格式化为 `Asia/Shanghai` 展示，不能把浏览器默认时区作为业务时间。

#### Scenario: API serializes an instant as UTC ISO-8601

- **WHEN** API 序列化 `Instant`
- **THEN** 输出带 `Z` 的 ISO-8601 字符串而非时间戳

#### Scenario: UI formats API time in Shanghai timezone

- **WHEN** 前端格式化合法 UTC 时间字符串
- **THEN** 使用 `Asia/Shanghai` 展示，输入非法时返回安全的原始值或明确错误

### Requirement: Shared serialization contract is reused by audit digests

系统 SHALL 让审计摘要复用共享 canonical JSON 和 SHA-256 实现，避免业务模块复制基础算法。

#### Scenario: Audit digest follows shared canonicalization vector

- **WHEN** 审计 payload 使用共享测试向量计算摘要
- **THEN** 审计 payload digest 与共享摘要结果一致
