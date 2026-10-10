## Why

当前数据库业务表和字段均缺少注释，数据库浏览、数据治理和故障定位无法直接理解对象语义。需要用前向迁移补齐可审阅的中文元数据，而不改写既有迁移历史。

## What Changes

- 新增一份 Flyway V15 SQL，给七个业务 schema 的现有业务表与字段添加中文注释。
- 在迁移中维护受控的表、字段说明映射，遇到未覆盖字段时失败，避免后续新增字段无声漏注释。
- 新增静态覆盖校验，确认注释迁移与当前建表迁移的表、列集合一致。

## Capabilities

### New Capabilities

- `database-schema-comments`: 为 finance-agent 业务数据库提供完整、可迁移的表和字段中文注释。

### Modified Capabilities

- 无。

## Impact

- 影响后端 Flyway 迁移资源和其静态校验脚本。
- 不修改业务数据、历史迁移、应用接口或运行时权限。
