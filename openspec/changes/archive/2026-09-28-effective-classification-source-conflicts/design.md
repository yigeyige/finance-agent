## Context

`EffectiveClass` 已被 `FactSnapshot` 和 `Finding` 复用，但尚无统一分类器。`SourceCatalog` 目前只保存模板参数白名单，无法表达对象级来源权威。实现需使用 Java 17 `java.time`，禁止依赖系统默认时区或模型推理裁决来源冲突。

## Goals / Non-Goals

**Goals:**

- 以调用方显式提供的 `Instant` 检查时钟计算生效分类。
- 将对象类型到来源 ID 的有序权威登记固化在 `SourceCatalog`。
- 对可裁决和不可裁决的来源候选返回可审计结果及 `UNKNOWN_SOURCE_CONFLICT`。

**Non-Goals:**

- 不改数据库字段或迁移，不实现真实来源认证。
- 不允许模型、自然语言或候选顺序替代目录权威。

## Decisions

- **显式 `Instant`**：分类器只接收 `Instant now`，调用方负责固定时钟，避免系统默认时区漂移。
- **兼容扩展目录**：为 `SourceCatalog` 增加对象级权威映射，并保留旧的两参数构造，旧来源默认无权威登记。
- **确定性冲突协调**：按目录中从高到低的来源 ID 顺序选择唯一最高权威；无登记、并列或对象信息缺失均返回未知冲突代码。
- **失败不猜测**：缺少生效时间或区间非法时输出 `UNDETERMINED`，不使用当前系统时间补齐。

## Risks / Trade-offs

- [Risk] 旧目录没有权威映射 → 多来源结果会保守地返回 `UNKNOWN_SOURCE_CONFLICT`，需要后续补齐登记。
- [Risk] 来源时间精度不一致 → 使用 `Instant` 的半开区间 `[effectiveFrom, effectiveTo)` 并在边界测试中固定行为。

## Migration Plan

无需数据库迁移。先部署代码和目录配置，调用方逐步传入固定检查时钟；旧构造仍可运行但不会自动获得权威裁决。

## Open Questions

无。
