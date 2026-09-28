## Why

工具目录需要一个完全离线、可重复且不接触真实财务数据的配置源，用于验证分页、字段策略、覆盖度和冲突场景。

## What Changes

增加固定查询模板的 ConfigSourceAdapter 合成实现，提供固定 seed、可切换场景、SourceCatalog 和契约样例；禁止 SQL、URL、写操作和未声明参数入口。

