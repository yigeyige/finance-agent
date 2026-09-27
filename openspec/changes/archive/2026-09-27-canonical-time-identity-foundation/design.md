## Context

后端所有数据库时间字段已经使用 `Instant` 映射 PostgreSQL `timestamptz`，但应用仍有直接 `Instant.now()`，Jackson 和前端没有集中声明时区边界。`VersionedJson` 和治理模块各自解析 JSON，审计摘要逻辑重复了 canonicalization。任务要求固定摘要测试向量，并明确后端 UTC、API ISO-8601 和工作台 Asia/Shanghai 的职责边界。

## Goals / Non-Goals

**Goals:**

- 共享模块提供确定性 JSON canonicalization、SHA-256、UUID 生成/解析和 UTC 时间接口。
- canonical JSON 对对象键排序、数字归一化、null/Unicode 保留、数组顺序保留，并拒绝 NaN/Infinity 等非 JSON 数值。
- 通过 Spring `Clock` Bean 注入可替换时钟；Jackson 输出 ISO-8601 UTC。
- 前端只负责把 API UTC 字符串格式化为 Asia/Shanghai 展示，不把本地时间写回 API。
- 审计摘要复用共享实现，确保历史摘要规则一致。

**Non-Goals:**

- 不改变现有数据库 `timestamptz` 类型或迁移历史。
- 不批量改造所有业务类的时间获取方式；只提供统一入口并在新代码中使用。
- 不引入 RFC 8785 外部库、日期库或 UUID 第三方库。

## Decisions

- **Canonical JSON**：使用项目已有 Jackson 严格解析，再由共享编码器递归输出。对象键按 Unicode 字符串排序；数组保持输入顺序；null、布尔和字符串保持语义；数字使用 `BigDecimal.stripTrailingZeros().toPlainString()`，零统一为 `0`。相比直接 `ObjectMapper.writeValueAsString`，该方案明确处理 `1` 与 `1.0` 和字段顺序差异。
- **摘要**：使用 JDK `MessageDigest` 和 `HexFormat` 计算 UTF-8 SHA-256，避免新增依赖；审计 `AuditDigest` 委托共享 canonical JSON 和摘要工具。
- **UUID**：共享 `UuidGenerator` 使用 JDK `UUID.randomUUID()`；持久化层的 MyBatis-Plus `ASSIGN_UUID` 仍保留，`PersistenceUuid` 只负责领域/存储格式转换并委托共享解析。
- **时间**：Spring 提供 `Clock.systemUTC()`，业务通过构造器注入 `Clock`；Jackson 属性固定 UTC 和非时间戳输出。前端使用原生 `Intl.DateTimeFormat` 的 `Asia/Shanghai`，不把浏览器系统时区作为业务依据。
- **兼容迁移**：保留 `VersionedJson` 的严格容器校验和原始 JSON 访问，新增 canonical 方法供摘要场景使用，避免改变已有持久化 payload 的隐式格式。

## Risks / Trade-offs

- [Risk] 数字规范化改变部分历史摘要 → 共享摘要版本固定并补充测试向量；新旧摘要不可混用时由调用方保留版本。
- [Risk] 浏览器运行环境不支持指定时区 → `Intl.DateTimeFormat` 失败时返回明确的 UTC ISO 文本，不猜测本地时区。
- [Risk] 旧代码仍直接调用 `Instant.now()` → 本任务提供 Clock Bean 和测试门禁，后续任务逐步替换，不做无关批量重构。

## Migration Plan

1. 新增 shared 基础类、UTC Jackson/Clock 配置和前端显示工具。
2. 修改审计摘要实现委托 shared canonical JSON/摘要。
3. 运行后端编译、共享契约测试和前端 typecheck/build。
4. 回滚仅需回滚应用代码与配置，不涉及数据库迁移。

## Open Questions

- 业务 API DTO 的具体字段格式由后续 API 任务沿用本 change 的 ISO-8601 UTC 契约。
