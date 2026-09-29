## Context

任务索引要求把合成任务从发起一直验证到交接。仓库已有各领域应用服务和 REST 入口，但没有统一的测试输入、固定的发布前新增引用和证据收集方式。开发环境由用户启动，自动化资产不能假设真实认证、真实财务源或真实模型可用。

## Goals / Non-Goals

**Goals:**

- 用固定 synthetic identity、runtime bundle、scope 和源版本描述首个核心切片。
- 让脚本既能在未启动环境时做离线静态校验，也能在用户显式开启时按 API 顺序执行并保存每步响应。
- 用只读 SQL 汇总所有关键持久化证据，并明确旧 review 被重查标记为 stale 后再生成新草稿和新 review。
- 用 JUnit 契约测试约束顺序、敏感字段和关键发布断言，不引入测试数据库或第三方依赖。

**Non-Goals:**

- 不新增一个绕过业务审批的“万能执行”接口。
- 不在 AI 侧启动应用、写入用户数据库或调用真实模型/财务源。
- 不实现真实认证、Worker 调度或生产部署能力。

## Decisions

1. **固定 JSON fixture 作为唯一输入**：脚本和测试共同读取同一版本化 fixture，避免在 PowerShell 中重复散落业务常量；fixture 只使用 EMP-SYN-001 等合成标识。
2. **脚本默认静态模式，执行需要显式开关**：`-Execute` 才访问 localhost；默认运行不会产生外部副作用，符合用户负责启动和人工验证的边界。
3. **重查变化通过两组快照表达**：第一组 current 注入一个新增范围引用，断言返回 stale；第二组 current 与 baseline 一致，断言可进入发布。
4. **证据采用只读 SQL 模板并持久化重查记录**：不把数据库凭据写入仓库；使用 psql 变量或 dbx 连接执行，输出仅包含业务 ID、状态和摘要字段。为避免重查结果只存在进程内，新增 `packet.scope_recheck` 迁移和 MyBatis-Plus 仓储。
5. **契约测试验证边界而非模拟完整数据库**：沿用项目现有 Mockito/JUnit 测试分层，覆盖脚本契约、fixture 版本、敏感信息和 ScopeRecheckService 行为，避免再造内存持久层。

## Risks / Trade-offs

- [Risk] 用户未先运行 Worker 或未产生证据时，脚本在证据查询/草稿阶段停止 → [Mitigation] 明确检查点并保留已保存响应，提示从该步骤继续。
- [Risk] 本地身份种子或 runtime bundle 未启用 → [Mitigation] fixture 固定种子 ID，脚本在首个 API 调用前检查 health/identity，并给出配置提示。
- [Risk] SQL 模式随迁移演进 → [Mitigation] 查询只使用稳定表名和核心列，契约测试检查查询中覆盖的证据集合；变更时同步 fixture 版本。

## Migration Plan

无需数据库迁移。先运行静态校验，再由用户在 local profile 启动应用后使用 `-Execute` 执行；失败时根据输出目录中的最后一个响应继续人工复核，不修改生产环境。

## Open Questions

- 真实 P1 环境的认证、源系统和模型调用不属于本任务，沿用后续 P1 任务确认的契约。
