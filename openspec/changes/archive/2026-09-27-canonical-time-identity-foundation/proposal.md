## Why

项目当前已经出现 `VersionedJson`、持久化 UUID 转换和审计摘要等基础实现，但规范化规则、摘要计算、时钟和时区契约仍分散在业务模块中，容易产生不同摘要、隐式系统时区和不可复现的测试结果。现在建立共享基础库，为后续仓储、运行时和前端工作台提供统一边界。

## What Changes

- 新增共享 canonical JSON、SHA-256、UUID 和 UTC 时间值对象/工具。
- 固定数字、null、Unicode、对象字段顺序和数组顺序的 canonicalization 规则。
- 注册 UTC `Clock` 与 Jackson ISO-8601 UTC 序列化配置。
- 让审计摘要复用共享 canonical JSON 和 SHA-256 实现。
- 增加前端 Asia/Shanghai 显示格式化工具，不改变 API 的 UTC 传输格式。
- 增加固定测试向量和时区/时钟契约测试，不引入新依赖。

## Capabilities

### New Capabilities

- `canonical-time-identity-foundation`: 统一 JSON、摘要、UUID、时钟和 API/UI 时区契约。

### Modified Capabilities

无。

## Impact

- 后端 `shared` 模块、Jackson 配置和审计摘要实现。
- 前端 `src/utils` 新增时间显示工具。
- `application.properties` 增加 UTC/ISO 序列化约束。
- 不修改数据库结构，不新增第三方依赖。
