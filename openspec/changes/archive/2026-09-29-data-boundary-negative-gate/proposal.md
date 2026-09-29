## Why

现有字段策略和可信裁剪已能在内存边界拒绝敏感字段，但缺少一组可重复的负向输入和跨边界扫描资产，无法证明禁止读取、禁止留存、未批准送模字段以及源写入尝试在数据库、日志、trace、workspace、checkpoint、SSE 和导出边界均被拦截。

## What Changes

- 新增版本化数据边界负向门禁 fixture，固定 synthetic marker、三类字段、扫描目标和源写入探针预期。
- 新增默认离线、显式执行才扫描用户提供目录的 PowerShell 脚本，并生成不含业务 payload 的结果报告。
- 新增只读 PostgreSQL 扫描 SQL 模板，按任务范围统计禁止字段和 marker 命中，不返回原始内容。
- 新增后端契约测试，验证可信投影、上下文证据、reasoning 过滤和配置源只读接口。

## Capabilities

### New Capabilities

- `data-boundary-negative-gate`: 提供跨持久化、模型、日志和文件边界的数据泄漏负向门禁。

### Modified Capabilities

- 无

## Impact

- 新增 `scripts/fixtures/`、PowerShell/SQL 验收资产及后端契约测试。
- 不新增运行时依赖，不新增源写入 API；人工执行阶段由用户提供本地应用、数据库只读连接和结果目录。
