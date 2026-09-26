## Context

项目已有 PostgreSQL/pgvector 连接探针和 Java 17/Spring Boot 4 基线，但没有迁移目录、schema history 或可审计的数据库初始化入口。领域模型附件 A 已确定七个业务 schema 及其表依赖顺序；当前数据库准备和生产权限由平台负责，应用运行账号不应拥有 DDL 能力。

## Goals / Non-Goals

**Goals:**

- 以 Flyway V1–V7 建立可重复、前向、可校验的 Agent 数据库基线。
- 将迁移作为显式命令运行，支持迁移和 validate-only 两种模式。
- 保留扩展、角色、数据库创建等平台准备边界，并提供静态检查及只读验收查询。
- 让低版本、乱序或校验和漂移在迁移前失败。

**Non-Goals:**

- 不在应用迁移中创建数据库角色、数据库或 pgvector 扩展。
- 不改变 Spring Boot 默认启动流程，不自动在应用启动时执行 DDL。
- 不执行生产权限授予、容器启动、数据库写入或人工验收。

## Decisions

1. **显式 Flyway runner，而非 Spring Boot 自动迁移。** 使用独立 Java main 和 PowerShell 包装脚本读取 `FINANCE_AGENT_DB_*` 环境变量。这样不会因为当前应用没有 DataSource 自动配置而改变启动行为，也便于仅由 `fa_migration` 账号执行。
2. **V1–V7 固定拆分。** 迁移文件按附件 A 的依赖顺序命名：schema、IAM/治理、task、evidence、packet、harness、knowledge。Flyway history 表放在 `public.finance_agent_schema_history`，不把业务表放入 public。
3. **严格前向校验。** `baselineOnMigrate=false`、`outOfOrder=false`，默认执行 Flyway validate；低于已安装版本、重复版本或 checksum 漂移均由 Flyway 拒绝。回滚采用修复数据库或前向补丁，不提供自动 down migration。
4. **平台预检扩展。** V7 使用已确认的 `vector(3)` 类型，但迁移只引用类型，不执行 `CREATE EXTENSION`。pgvector 扩展由平台/准备脚本确认，缺少扩展时迁移失败并由运维处理。
5. **SQL 保持可审计。** 迁移中不使用角色/密码/数据库创建语句；schema 使用 `IF NOT EXISTS`，表、约束和索引由 Flyway history 控制一次性执行，避免用宽泛的幂等语句掩盖漂移。
6. **静态契约测试。** Java 测试检查七个迁移文件、schema 名称、vector 维度和禁止语句；PowerShell 脚本提供不连接数据库的人工/CI 校验。真实空库、重复执行、权限负测由用户在现有容器中完成。

## Risks / Trade-offs

- [Risk] 目标库未预装 pgvector，V7 会失败 → 在文档和脚本中明确扩展预检，并保留失败状态，不绕过类型检查。
- [Risk] 迁移账号权限过大 → 只在文档中规定平台创建的 `fa_migration` 账号，静态检查禁止角色创建；权限授予由安全评审完成。
- [Risk] 已有非 Flyway 表或历史版本与基线不一致 → 禁止 `baselineOnMigrate`，要求先在目标库检查并人工确认，不自动覆盖。
- [Risk] 附件 A 的模型仍可能演进 → 迁移只追加新版本，禁止编辑已安装版本；后续变更通过新 OpenSpec 和 V8+ 前向迁移。

## Migration Plan

1. 平台准备目标数据库、`fa_migration` 账号和 pgvector 扩展，并通过现有探针确认版本。
2. 使用迁移账号设置 `FINANCE_AGENT_DB_URL/USERNAME/PASSWORD`，运行 `scripts/verify-flyway-migrations.ps1` 后执行 `scripts/flyway-migrate.ps1`。
3. 执行 `scripts/flyway-migrate.ps1 -ValidateOnly` 和文档中的只读查询，确认 history 与七个 schema。
4. 应用账号只用于运行时读写授权对象；用应用账号执行 DDL 应失败。
5. 若迁移失败，保留 Flyway history 和失败日志，修复平台准备或新增前向迁移后重试；不删除 schema、不重置 history。

## Open Questions

- 生产环境最终是否将 history 表放入独立管理 schema，待 DBA 权限评审后决定；本任务固定使用 `public.finance_agent_schema_history` 以保证空库可执行。
- 后续是否需要把向量维度从当前已确认的 3 抽取为部署参数，留待知识检索能力实现时单独评估。
