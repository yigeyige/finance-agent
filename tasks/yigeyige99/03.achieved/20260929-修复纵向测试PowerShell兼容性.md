---
id: 20260929-fix-qa-core-powershell-compatibility
title: "修复纵向测试 PowerShell 兼容性"
status: done
priority: high
owner: yigeyige99
created_at: 2026-09-29
type: fix
source: conversation
openspec_change: fix-qa-core-powershell-compatibility
---

# 修复纵向测试 PowerShell 兼容性

## 需求

### 标题

修复纵向测试 PowerShell 兼容性

### 描述

修复 `qa-core-synthetic-vertical-slice.ps1` 在 Windows PowerShell 解析 UTF-8 脚本失败、以及在 PowerShell 7 处理非 2xx HTTP 响应失败的问题；修复运行验证发现的 MyBatis-Plus 字符串 UUID 主键查询参数映射异常。

## 背景

用户已在本机完成 JAR 构建并启动 local profile，health 已恢复正常。执行纵向测试时，PowerShell 7 因 `HttpResponseMessage` 不具备 `GetResponseStream()` 中断；改用 Windows PowerShell 又因无 BOM UTF-8 文件中的中文文本解析失败。

运行验证继续暴露 `AgentUserMapper.selectById(UUID)` 与 `AgentUserEntity` 的 `String` + `ASSIGN_UUID` 主键映射不匹配，MyBatis 将裸 UUID 当作实体参数读取 `userId` 属性而抛出反射异常。同一持久化模式存在于其他领域仓储，统一复用既有 `PersistenceUuid` 映射处理。

## 目标

- [x] Windows PowerShell 5.1 能解析并执行纵向测试脚本。
- [x] PowerShell 7 能保留非 2xx 响应内容并继续按预期状态断言。
- [x] 使用 ASSIGN_UUID 字符串主键的仓储可按领域 UUID 正常查询。
- [x] 交付可复核的跨 PowerShell 解析和异常处理验证，并完成本地库迁移验证。

## 验收标准

- `powershell.exe` 和 `pwsh` 均可通过脚本语法解析。
- 本地 API 返回预期 409 时，PowerShell 7 不因响应体读取方式中断。
- 默认静态模式不访问网络、数据库或本地 API。
- 领域 UUID 传入 `BaseMapper.selectById` 前转换为标准 UUID 字符串，不再出现主键属性反射异常。

## 执行记录

- 2026-09-29：根据用户本机验证反馈创建任务；待实施。
- 2026-09-29：脚本改为 UTF-8 BOM；默认路径在脚本体内通过 `$MyInvocation.MyCommand.Path` 解析；fixture 显式按 UTF-8 读取；PowerShell 7 的 `HttpResponseMessage` 与 Windows PowerShell 响应均由兼容函数读取。
- 2026-09-29：已通过 Windows PowerShell 与 PowerShell 7 静态运行、`mvn -q '-Dtest=CoreSyntheticVerticalSliceContractTest' test`、`mvn -q -DskipTests compile`、`python scripts/verify-contracts.py` 和 `openspec validate fix-qa-core-powershell-compatibility --strict`；等待用户对已运行的 local API 执行一次 `-Execute` 验收。
- 2026-09-30：根据执行模式的 500 根因，将所有 String + ASSIGN_UUID 实体的 `BaseMapper.selectById` 统一接入既有 `PersistenceUuid.toStorage`；新增 IAM 回归测试，并通过针对性 JUnit、Maven 编译、契约校验与 OpenSpec 严格校验。JAR 打包受正在运行的旧 JAR 文件锁定，待停止进程后重试打包并继续 API 纵向验证。
- 2026-09-30：用户重新打包并启动后，健康检查返回 200，纵向切片已越过原 MyBatis 500 并在创建任务时返回 401。只读核对 `finance_agent` 后确认合成用户、角色绑定和运行版本均为 0，说明当前启动未开启 local 合成种子；未写入测试任务，待以 `local` profile 和显式种子开关重启后复跑。
- 2026-09-30：启动合成种子时发现 MyBatis 未注册 UUID 非主键字段的 TypeHandler，角色绑定的 `grantedBy` 参数绑定失败。已新增 PostgreSQL UUID TypeHandler 并通过 MyBatis-Plus `ConfigurationCustomizer` 全局注册；使用 JDBC `OTHER` 绑定、兼容 UUID 对象与文本读取，未新增依赖。已通过 UUID Handler、UUID 主键映射、纵向脚本契约 JUnit，Maven 编译、契约校验、OpenSpec 严格校验及 JAR 打包；待用户以 local profile 和种子开关重新启动后复跑。
- 2026-09-30：新 JAR 已通过 health 检查，合成种子已成功写入 3 个用户、3 条角色绑定和 1 个运行版本；创建任务仍返回受控 500（traceId `392de27e-da75-40ce-b544-48aa9fd8e0b9`）。服务仅输出结构化控制台日志且未写日志文件，需取得该 traceId 的服务端堆栈后继续定位；本次失败事务未形成测试任务。
- 2026-09-30：用户确认控制台未显示异常日志。已修复全局未知异常处理：响应复用请求 traceId，并输出受限结构化错误事件（事件名、组件、异常类别、SQLSTATE 分类），不记录异常正文、堆栈、SQL、请求内容或凭据。已通过全局错误响应、UUID Handler、UUID 主键映射与纵向脚本契约 JUnit，Maven 编译、契约校验和 OpenSpec 严格校验；待停止当前 JAR 后打包、重启并复跑以取得实际失败分类。
- 2026-09-30：根据新的结构化错误日志定位为 SQLSTATE `42703`。只读核对本地 `governance.audit_event` 表，确认缺少创建任务审计写入所需的 `canonicalization_version`，该列由 `V10__audit_outbox_contract.sql` 提供。另发现迁移资源存在重复 `V10` 编号，已将检查点运行版本绑定迁移顺延为 V12，并补充资源清单契约，避免 Flyway 因版本重复拒绝迁移。数据库尚未写入，待用户确认并执行迁移后复跑纵向测试。
- 2026-09-30：清理构建目录后，Flyway 迁移资源契约、Harness 迁移契约、全局错误处理、UUID Handler、UUID 主键映射与纵向脚本契约测试全部通过；`mvn -q -DskipTests compile`、`mvn -q -DskipTests package`、两种 PowerShell 静态模式、OpenSpec 严格校验与差异格式检查均通过。已核验重新打包的 JAR 仅包含 V10 审计 outbox、V11 范围复核和 V12 检查点绑定迁移；待用户在本地库执行 Flyway 迁移后复跑执行模式。
- 2026-09-30：只读核对进一步确认当前本地 `finance_agent` 库存在既有业务表，但没有 `public.finance_agent_schema_history`，且既缺 `canonicalization_version` 也缺 `packet.scope_recheck`。现有 runner 按稳定契约禁用自动 baseline，因此不能安全地对该历史库直接执行 V1–V12；需要用户在“重建本地测试库”与“经确认后增加受控 V9 基线迁移”之间选择后才能继续数据库写入和 API 纵向复验。
- 2026-09-30：用户选择重建本地测试库。已只读确认执行连接位于 `postgres` 库、目标 `finance_agent` 存在；DBX MCP 因高风险 SQL 策略拒绝 `DROP DATABASE ... WITH (FORCE)`，未对数据库产生变更。本执行环境未发现 `psql.exe`，也未继承数据库环境变量；待用户启用 DBX 高风险 SQL 权限或在 DBX 中完成目标库重建后继续。
- 2026-09-30：用户启用 DBX 高风险 SQL 后，已在 `postgres` 库执行 `DROP DATABASE "finance_agent" WITH (FORCE)` 和 `CREATE DATABASE "finance_agent"`；随后连接到新建的 `finance_agent` 只读确认用户表数量为 0。Flyway runner 仍需从用户本机进程环境读取数据库凭据，待用户执行迁移命令后继续结构、历史表和 API 纵向复验。
- 2026-09-30：用户执行 Flyway 后，V1–V6 已成功写入历史，V7 因新库未启用 PostgreSQL `vector` 扩展而回滚，未遗留 V7 半迁移。已在新建的 `finance_agent` 库启用已安装的 `vector` 0.8.6 扩展，并只读确认成功历史仍停在 V6；待用户重跑既有 Flyway 命令继续 V7–V12。
- 2026-09-30：用户重跑 Flyway 后成功应用 V7–V12，迁移校验返回 `status=VALID`。只读核验显示 12 条成功记录、0 条失败记录，最后一条为 V12；`governance.audit_event.canonicalization_version`、`packet.scope_recheck`、`harness.checkpoint.runtime_bundle_id` 和 `vector` 扩展均已存在。待用户以 local profile 与合成种子启动新 JAR 后执行核心纵向切片。
- 2026-09-30：PowerShell 7 执行模式通过健康检查和任务创建，但追加前提返回 400。定位为审计事件参与摘要计算的纳秒时间在 PostgreSQL `timestamptz` 重读时降为微秒，导致上一事件的摘要完整性复算失败；已在 `AuditEvent` 创建与重读边界统一截断为微秒，并新增 PostgreSQL 时间精度往返回归测试。相关 JUnit、Maven 编译、OpenSpec 严格校验和差异格式检查均通过；当前运行 JAR 不含此修复，待用户停止、重新打包并以原参数重启后复跑。
- 2026-09-30：重启后，PowerShell 7 执行模式已通过健康检查、创建任务和追加前提，证明审计时间精度修复生效；范围确认阶段返回受控 500（traceId `6118d754-d23b-48da-b55c-5a28b48fe5b6`）。当前工作区未附加用户启动 JAR 的终端，需取得该 traceId 对应的受限结构化错误事件分类后继续最小修复。
- 2026-09-30：结构化遥测显示 `HttpMessageConversionException`，新增 MVC 回归测试确认 Spring Boot 4 HTTP 转换器使用 Jackson 3，无法构造 Jackson 2 `JsonNode`。已将范围确认和决策包草稿 Web 请求 DTO 切换为 Jackson 3 节点；决策包 Controller 仅在 HTTP/应用边界通过既有 `CanonicalJsonCodec` 严格转换为内部 Jackson 2 节点。范围 MVC、两类请求绑定、审计摘要与纵向脚本契约测试，Maven 编译、OpenSpec 严格校验和差异格式检查均通过；待用户重新打包并重启后复跑。
- 2026-09-30：AI 已使用 `.env.example` 的本地数据库配置重新打包并启动 JAR，health 返回 200。Windows PowerShell 5.1 执行纵向切片后，范围确认、步骤提案和步骤审批均已通过；随后审计链查询因 `selectOne` 读取多条历史事件触发 `TooManyResultsException`。已改为按时间和 ID 排序后 `LIMIT 1` 取首条，并为该仓储及受限根因分类日志白名单补齐回归测试；针对性 JUnit 和完整前后端构建均通过。
- 2026-09-30：重启最新 JAR 后，执行模式已越过范围确认、步骤提案和步骤审批，停在脚本既有的证据检查点：当前运行时没有“批准的合成步骤/Worker”入口产生 `fact_snapshot`、`coverage_record` 或 `finding`，因此脚本按设计停止并提示补充该外部前置条件。未将其误报为纵向切片通过，任务仍保持进行中。
- 2026-09-30：已由 AI 使用本地合成配置重新打包、启动并完成 Windows PowerShell 5.1 的完整 17 步执行模式验证。修复草稿组装后遗漏的 `DRAFT_PACKET → WAITING_REVIEW` 状态推进，并补齐固定 fixture 的交接必填 `downstreamStatus`；完整 Maven 测试、两种 PowerShell 静态模式、JAR 打包及 OpenSpec 严格校验均通过。
- 2026-09-30：已将 delta spec 同步至主规格，并归档 OpenSpec 变更 `2026-09-30-fix-qa-core-powershell-compatibility`。
