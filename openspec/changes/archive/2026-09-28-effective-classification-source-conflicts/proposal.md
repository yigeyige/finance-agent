## Why

来源事实需要按任务注入的检查时钟进行生效分类，且多个来源对同一对象的结果不能交由模型猜测。当前只有枚举值，没有统一的时间分类和目录驱动的来源权威裁决，容易把未来、过期或冲突事实误标为当前有效。

## What Changes

- 增加注入时钟的生效分类器，输出 `CURRENT_EFFECTIVE`、`FUTURE_SCHEDULED` 或 `UNDETERMINED`。
- 扩展 `SourceCatalog` 保存对象级来源权威顺序，并增加来源冲突协调器。
- 无登记权威、权威并列或输入不满足裁决条件时输出 `UNKNOWN_SOURCE_CONFLICT`，不调用模型裁决。
- 补充固定时钟、非法区间、缺时间、权威冲突和系统默认时区变化测试。

## Capabilities

### New Capabilities

- `effective-classification`: 基于注入时钟的事实生效分类和来源权威冲突处理。

### Modified Capabilities

- `synthetic-source`: 扩展来源目录以支持对象级权威顺序。

## Impact

- 影响 source/application、evidence/application 与 evidence/domain 相关代码及测试。
- 不新增依赖、不修改数据库结构；保留现有 `SourceCatalog` 两参数构造兼容性。
