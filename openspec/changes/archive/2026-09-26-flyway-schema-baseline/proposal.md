## Why

当前项目已经确认 PostgreSQL/pgvector 的连接前置条件，但 Agent 自有数据边界尚未形成可重复执行、可审计的数据库迁移基线。现在建立 Flyway 版本迁移，可以让空库初始化、重复启动和版本校验具有明确契约，同时把平台准备职责（扩展与权限）和应用迁移职责分开。

## What Changes

- 引入 Flyway 迁移运行器和命令脚本，默认从 `classpath:db/migration` 执行前向迁移。
- 按领域模型附件 A 提供 V1–V7 迁移：schema、IAM/治理、任务、证据、交付包、harness、知识库。
- 创建 Agent 自有的 `iam`、`task`、`evidence`、`packet`、`harness`、`knowledge`、`governance` schema 及对应表、约束和索引。
- 增加迁移静态校验、迁移说明和只读验收查询，明确迁移历史表、扩展预检和权限边界。
- 应用迁移不创建角色、数据库或 `vector` 扩展；应用账号不得承担 DDL，扩展由平台/准备脚本预先确认。
- Flyway 使用严格版本校验和禁止乱序，低于已安装基线或校验和不一致时拒绝继续。

## Capabilities

### New Capabilities

- `flyway-schema-baseline`: 提供可重复、前向且可审计的 Agent 数据库 schema 迁移与校验能力。

### Modified Capabilities

- 无。

## Impact

- 影响 `backend/pom.xml`、后端迁移运行器、`backend/src/main/resources/db/migration/`、迁移脚本和数据库说明文档。
- 新增 Flyway 运行时依赖，但不改变 Spring Boot 应用默认启动流程；迁移由显式脚本调用。
- 需要平台准备 `fa_migration` 等数据库角色、目标数据库及 pgvector 扩展；生产权限授予和人工负测仍由用户执行。
- 需要用户在现有 PostgreSQL 容器中执行迁移并验证 schema history、重复执行和应用账号 DDL 失败。
