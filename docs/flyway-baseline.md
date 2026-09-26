# Flyway 数据库基线

本项目的数据库迁移由显式 Flyway runner 执行，不在 Spring Boot 应用启动时自动执行。迁移脚本位于 `backend/src/main/resources/db/migration/`，版本为 V1–V7，history 表为 `public.finance_agent_schema_history`。

## 平台前置

执行迁移前由平台/DBA 完成以下准备：

- 准备目标数据库和专用迁移账号（建议使用 `fa_migration`）。
- 按安全评审授予迁移账号所需的 schema/table DDL 权限；应用账号不得拥有 `CREATE` schema、table 或 extension 权限。
- 安装并确认 pgvector 扩展。当前开发基线探针结果为 PostgreSQL 17.11、pgvector 0.8.6、向量维度 3。迁移只引用 `vector(3)`，不会执行 `CREATE EXTENSION`、创建角色或创建数据库。
- 先确认目标库没有需要保留但未纳入 Flyway 的旧对象；本 runner 不使用 `baselineOnMigrate`，不会替已有对象自动建立基线。

## 执行命令

在仓库根目录设置迁移账号连接变量（不要把密码写入脚本或提交到仓库）：

```powershell
$env:FINANCE_AGENT_DB_URL = "jdbc:postgresql://localhost:5432/finance_agent"
$env:FINANCE_AGENT_DB_USERNAME = "fa_migration"
$env:FINANCE_AGENT_DB_PASSWORD = "<password>"
./scripts/verify-flyway-migrations.ps1
./scripts/flyway-migrate.ps1
./scripts/flyway-migrate.ps1 -ValidateOnly
```

`flyway-migrate.ps1` 只调用后端的显式 Java runner；不启动 Spring Boot。runner 默认关闭乱序迁移并关闭 `baselineOnMigrate`，低于已安装版本、版本顺序不合法或 checksum 漂移都会失败。失败时命令只输出状态码，不输出 JDBC URL、用户名或密码。

## 只读验收查询

以下查询由用户在现有 PostgreSQL 容器中执行，不由应用迁移脚本自动执行：

```sql
SELECT installed_rank, version, description, success
FROM public.finance_agent_schema_history
ORDER BY installed_rank;

SELECT schema_name
FROM information_schema.schemata
WHERE schema_name IN ('iam','task','evidence','packet','harness','knowledge','governance')
ORDER BY schema_name;

SELECT table_schema, count(*) AS table_count
FROM information_schema.tables
WHERE table_schema IN ('iam','task','evidence','packet','harness','knowledge','governance')
GROUP BY table_schema
ORDER BY table_schema;
```

预期 history 包含 V1–V7 且 `success=true`，schema 查询返回七行。重复执行迁移应返回成功且 `migrationsExecuted=0`。用应用账号执行 `CREATE SCHEMA` 或迁移命令应被数据库拒绝；这是权限负测，需由用户人工完成。

## 变更规则

已安装的 V1–V7 文件不可编辑。领域模型变更必须新增 V8+ 前向迁移，并同步更新 OpenSpec 与本说明。不要删除 schema、清空 history 或使用 `repair` 掩盖未评审的 checksum 漂移。
