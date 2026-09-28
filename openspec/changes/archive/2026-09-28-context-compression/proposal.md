## Why

长会话压缩不能把摘要当成新的证据，也不能丢失任务范围、当前步骤或未知项。当前 Harness 已有任务边界和摘要工具，但没有独立的压缩输出契约、引用目录及 digest 校验器，伪造 snapshot ID 或篡改摘要时无法稳定失败。

## What Changes

- 增加受限的 Context Compression 结构化摘要 schema，只允许任务、范围、前提、证据、finding、当前步骤引用及摘要。
- 增加压缩配置，限制摘要长度、引用类型和未知项保留规则。
- 增加引用目录/校验器，验证引用存在、digest 匹配、摘要不升级为证据，并在失败时返回人工缩小范围所需的安全错误。
- 增加伪造 ID、digest 不匹配、丢失未知项和摘要伪装证据的契约测试。

## Capabilities

### New Capabilities

- `context-compression`: 定义长会话压缩输出、引用完整性和失败门禁。

### Modified Capabilities

无。

## Impact

- 影响 `harness/application` 的压缩模型、配置和校验器，以及离线契约测试。
- 复用现有 JDK、Jackson 和摘要组件，不增加依赖，不写入数据库或改变运行时部署方式。
