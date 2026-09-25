## Why

开发环境需要确认既有 `postgres-pgvector` 服务是否满足 P0 基线，但当前仅有应用配置占位，没有不依赖应用启动的版本探针，也没有明确的数据库与应用角色准备边界。现在提供环境变量驱动的探针，可以在不写入管理员凭证、不自动创建数据库对象的前提下给出可复现的连接、PostgreSQL 版本和 pgvector 扩展状态。

## What Changes

- 增加 PostgreSQL JDBC 驱动和可独立运行的连接探针入口。
- 探针读取 `FINANCE_AGENT_DB_URL`、`FINANCE_AGENT_DB_USERNAME`、`FINANCE_AGENT_DB_PASSWORD`，缺少凭证时明确失败，不回退默认密码。
- 查询 PostgreSQL 服务端版本、pgvector 扩展版本和基础 vector 类型操作，并使用稳定退出码/状态输出区分配置、连接、版本和扩展问题。
- 增加 PowerShell 运行包装、连接配置样例和数据库准备说明；只描述管理员准备步骤，不由应用自动创建数据库或角色。
- 增加无数据库依赖的配置负向单元测试。

## Capabilities

### New Capabilities

- `postgres-pgvector-probe`: 定义环境变量驱动的 PostgreSQL/pgvector 连接探针、状态和安全边界。
- `database-preparation-boundary`: 定义 `finance_agent` 数据库与应用角色的人工准备边界及配置样例。

### Modified Capabilities

无。

## Impact

- 影响 `backend/pom.xml`、后端诊断代码和测试代码。
- 新增 `scripts/postgres-probe.ps1`、数据库准备文档及 `.env.example` 探针变量说明。
- 不执行真实数据库连接，不启动 Spring Boot，不创建数据库/角色，不改变现有 schema 或迁移。
