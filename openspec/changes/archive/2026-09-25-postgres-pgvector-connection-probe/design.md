## Context

当前后端只绑定了数据库配置属性，尚未引入 PostgreSQL JDBC 驱动或可独立执行的数据库探针。DEV-BOOT-007 需要在用户准备本机 `postgres-pgvector` 后确认 PostgreSQL 17.11、pgvector 0.8.6 和 `finance_agent` 连接，但 AI 不能依赖真实凭证或替用户创建管理员对象。

## Goals / Non-Goals

**Goals:**

- 通过 Java JDBC 探针读取现有 `FINANCE_AGENT_DB_*` 环境变量并执行只读版本/扩展查询。
- 将缺少凭证、连接失败、PostgreSQL 版本不符、扩展缺失/版本不符区分为稳定状态和退出码。
- 让用户可以从仓库根目录用 PowerShell 命令运行探针，不启动 Spring Boot。
- 用单元测试覆盖配置缺失时不回退默认密码的安全边界。

**Non-Goals:**

- 不创建数据库、角色、schema、扩展、表或迁移。
- 不在探针输出密码、完整连接串或异常中的敏感连接信息。
- 不把探针变成应用启动时的强制连接检查。

## Decisions

1. **使用 PostgreSQL JDBC 42.7.11 和独立 `main` 探针。**
   - 探针通过 `exec-maven-plugin` 在运行时加载后端 runtime classpath，避免启动 Spring Boot 或引入第二套脚本语言数据库驱动。
   - 不采用 `psql` 作为唯一入口，因为本机未必安装客户端且无法参与 Java 编译验证。

2. **所有连接值必须来自环境变量。**
   - URL、用户名和密码均要求非空；密码缺失直接返回配置错误，绝不使用默认密码或 `.env.example` 值。
   - 期望版本允许使用 `FINANCE_AGENT_EXPECTED_PG_VERSION` 和 `FINANCE_AGENT_EXPECTED_PGVECTOR_VERSION` 覆盖，默认冻结为 17.11 和 0.8.6。

3. **只读 SQL 查询服务端事实。**
   - 查询 `SHOW server_version`、`pg_extension` 中的 `vector` 版本，并执行 `vector_dims('[1,2,3]'::vector)` 验证类型操作。
   - 不执行 DDL/DML，不自动建库建角，不依赖管理员权限。

4. **状态输出不泄露连接信息。**
   - 标准输出仅包含状态、版本和 vector 探针结果；失败时只输出配置键、SQLState 或安全的版本信息，不打印 URL、用户名、密码或原始异常消息。

## Risks / Trade-offs

- [Risk] 服务端发行版可能在 `server_version` 后追加发行包后缀 → 仅比较期望版本前缀，并保留完整版本供用户人工确认。
- [Risk] pgvector 扩展未安装或权限不足 → 分别输出扩展缺失/扩展查询失败状态，不尝试安装或修复。
- [Risk] `exec-maven-plugin` 首次运行需下载插件 → 后端常规编译仍只依赖已锁定 Maven 构建；文档明确探针命令需要可访问 Maven 仓库。

## Migration Plan

1. 增加 JDBC 依赖、探针类、单元测试和 PowerShell 包装。
2. 用户准备环境变量后执行探针，保存不含凭证的版本/状态输出作为 DEV-BOOT-007 证据。
3. 若探针失败，按状态修复外部 PostgreSQL 环境后重新执行；代码不执行回滚型数据库操作。

## Open Questions

无。数据库角色命名和授权范围仅在准备说明中作为建议，实际由用户/数据库管理员确认。
