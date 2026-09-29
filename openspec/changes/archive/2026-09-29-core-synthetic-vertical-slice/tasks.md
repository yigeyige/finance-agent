## 1. 固化切片输入

- [x] 1.1 新增版本化合成纵向切片 fixture，固定 synthetic identity、runtime bundle、任务前提、范围、Stub 步骤、来源版本、重查快照和必需证据集合。
- [x] 1.2 将发布前重查记录落到 `packet.scope_recheck`，提供 MyBatis-Plus Entity、Mapper、Repository 和迁移。
- [x] 1.3 新增后端契约测试，校验 fixture 版本/敏感字段、步骤顺序、重查持久化、ScopeRecheck stale/clean 结果和发布视图摘要绑定。

## 2. 提供验收执行资产

- [x] 2.1 新增 PowerShell 脚本，默认执行离线 fixture 校验，显式执行时按 API 顺序保存每一步响应并在失败时保留结果目录。
- [x] 2.2 新增只读 PostgreSQL 证据查询模板，覆盖状态、审批、证据、覆盖、计算见证、确认、草稿、review、recheck、revision、audit、outbox 和 handoff。

## 3. 验证和交付

- [x] 3.1 运行后端目标测试、Maven 编译、契约校验和脚本静态检查。
- [x] 3.2 更新任务执行记录与任务索引，完成 OpenSpec 严格校验并归档变更。
