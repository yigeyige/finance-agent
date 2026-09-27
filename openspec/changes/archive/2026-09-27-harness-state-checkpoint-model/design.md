## Context

V6 已创建 `harness.session_state` 和 `harness.checkpoint`，V8 已补充审计字段，但 `harness` Java 模块只有包说明文件。现有 task/evidence/packet 模块采用 Java record、MyBatis-Plus Entity/Mapper、显式映射和 `ASSIGN_UUID`，本任务沿用该模式。方案还要求计划修订和 workspace 引用表，但当前迁移没有这两张表，因此需要追加 V9。

## Goals / Non-Goals

**Goals:**

- 建立 session、checkpoint、plan revision 和 workspace reference 四个领域对象及仓储端口。
- 用带 schema version、digest 和 JSON 的 `StateReference` 作为最小状态载体。
- 复用 Jackson 严格解析并递归拒绝源原响应、凭证、Token、reasoning 内容等禁止字段。
- 让查询端口显式要求 task/user/session，恢复校验 task、step、scope 和 approval 绑定。
- 使用 V9 追加 checkpoint user/scope 绑定并新建 plan/workspace 表，提供迁移契约测试。

**Non-Goals:**

- 不实现 HarnessAgent、Worker、恢复决策 API、workspace 文件系统配额或真实对象存储。
- 不计算 canonical digest；调用方必须提供摘要，DEV-DATA-008 再提供统一摘要工具。
- 不修改已发布的 V1-V8 迁移；仅追加 V9。

## Decisions

1. **StateReference 作为统一安全边界。** session、checkpoint 和 plan revision 均使用 `StateReference`，构造时严格解析 JSON、校验 schema/digest，并递归检查禁止字段；workspace 只保存相对路径和对象引用，不保存文件原文。
2. **Jackson 作为序列化实现。** 新增 `HarnessStateSerializer` 接口及 Jackson 实现，复用项目已有 Jackson，不引入新库；serializer 将 `StateReference` 作为唯一输入输出契约。
3. **跨边界查询强制复合条件。** Repository 方法同时接收 session/task/user，checkpoint 额外绑定 step/scope/approval；不提供只按 session ID 的公开查询，避免跨任务或跨用户读取。
4. **V9 增量迁移。** 为既有 checkpoint 增加 user_id 和 scope_revision_id，并创建 plan_revision/workspace_ref；新增表直接包含审计字段和 JSONB/唯一约束。V9 只向前追加，保持 Flyway 历史不变。
5. **计划和 checkpoint 只追加。** plan revision、checkpoint 和 workspace reference 只提供 insert/read 端口；session state 允许在同一 task/user/session 下更新状态和 updated_at。替代方案是全部使用通用 CRUD，但会绕过恢复状态的不可变边界。

## Risks / Trade-offs

- [Risk] 禁止字段规则按 JSON key 拦截，无法识别任意字符串中隐藏的敏感值。→ [Mitigation] 状态只接受最小引用结构，后续字段策略和扫描任务补充值级检测。
- [Risk] V9 为历史 checkpoint 回填 user_id，但无法推导旧 scope_revision_id。→ [Mitigation] scope_revision_id 对新 checkpoint 必填；旧记录读入失败并转人工恢复，不静默恢复旧状态。
- [Risk] digest 由调用方提供，当前不保证与 JSON 内容自动重算。→ [Mitigation] 强制 64 位摘要并记录 schema version，DEV-DATA-008 提供统一摘要实现后接入。

## Migration Plan

先执行 V1-V8，再执行 V9；V9 回填 checkpoint.user_id 并创建计划/workspace 表。应用回滚只回滚 Java 代码，保留新增表和历史状态；若 V9 执行失败，Flyway 事务回滚本次迁移。

## Open Questions

- 恢复决策器和 workspace manager 的运行时策略由 DEV-RUN-006、DEV-HAR-010 负责，本任务只提供数据和序列化边界。
