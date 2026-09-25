# PostgreSQL/pgvector 连接探针

探针用于 DEV-BOOT-007 的本机环境确认。它只读取环境变量并执行只读 SQL，不启动 Spring Boot，也不会创建数据库、角色、扩展、schema、表或密码。

## 配置

必须在当前 PowerShell 会话中提供以下变量；探针不会从默认值或仓库文件回退密码：

| 变量 | 用途 |
|---|---|
| `FINANCE_AGENT_DB_URL` | PostgreSQL JDBC URL，例如 `jdbc:postgresql://localhost:5432/finance_agent` |
| `FINANCE_AGENT_DB_USERNAME` | 应用只读/业务角色 |
| `FINANCE_AGENT_DB_PASSWORD` | 当前会话注入的密码，不写入仓库 |
| `FINANCE_AGENT_EXPECTED_PG_VERSION` | 可选，默认 `17.11` |
| `FINANCE_AGENT_EXPECTED_PGVECTOR_VERSION` | 可选，默认 `0.8.6` |

示例（密码仅从外部安全渠道填入当前会话）：

```powershell
$env:FINANCE_AGENT_DB_URL = "jdbc:postgresql://localhost:5432/finance_agent"
$env:FINANCE_AGENT_DB_USERNAME = "finance_agent"
$env:FINANCE_AGENT_DB_PASSWORD = "<由用户在本机安全注入>"
./scripts/postgres-probe.ps1
```

只检查配置是否完整且不连接数据库：

```powershell
./scripts/postgres-probe.ps1 -CheckConfig
```

也可以将 `-MavenCommand .\mvnw.cmd` 传给脚本使用仓库 Maven Wrapper。

## 状态与退出码

Java 探针入口使用下表退出码；PowerShell 包装通过 Maven `exec:java` 运行，失败时会以非零码返回（不同 Maven 版本可能统一为 1），应以输出的 `status=` 行判断具体失败类别。

| 退出码 | 状态 | 含义 |
|---:|---|---|
| 0 | `OK` / `CONFIGURATION_OK` | 连接、版本和 vector 类型检查通过，或仅配置检查通过 |
| 2 | `CONFIGURATION_ERROR` | 必需变量为空，或期望版本格式不合法 |
| 3 | `CONNECTION_FAILED` | 无法建立 JDBC 连接 |
| 4 | `POSTGRES_VERSION_MISMATCH` | 服务端版本不符合期望值 |
| 5 | `PGVECTOR_MISSING` / `PGVECTOR_QUERY_FAILED` | 扩展不存在或扩展查询失败 |
| 6 | `PGVECTOR_VERSION_MISMATCH` | pgvector 版本不符合期望值 |
| 7 | `PGVECTOR_OPERATION_FAILED` | vector 类型基础操作失败 |

成功证据不含凭证，格式类似：

```text
status=OK
postgresVersion=17.11
pgvectorVersion=0.8.6
vectorDimensions=3
```

## 数据库准备边界

`finance_agent` 数据库、应用角色和 `vector` 扩展由数据库管理员在目标环境中人工准备。以下仅为占位示例，尖括号中的值不得提交到仓库：

```sql
CREATE DATABASE finance_agent;
CREATE ROLE finance_agent LOGIN PASSWORD '<由管理员在密码系统中设置>';
GRANT CONNECT ON DATABASE finance_agent TO finance_agent;
\c finance_agent
CREATE EXTENSION IF NOT EXISTS vector;
```

实际授权应按部署环境的最小权限方案调整。应用启动和连接探针都不执行上述管理 SQL；探针只验证当前连接能否读取服务端版本、扩展版本并执行 `vector_dims`。
