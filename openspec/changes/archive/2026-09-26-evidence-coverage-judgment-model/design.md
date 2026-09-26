## Context

任务、范围、步骤和工具调用模型已通过 MyBatis-Plus 建立。V4 迁移已建立 evidence schema 下的六张表，V8 已统一补充审计字段，但 Java 侧仍只有 package-info，缺少可复核的领域不变量、映射和仓储端口。

## Goals / Non-Goals

**Goals:**

- 为六张 evidence 表建立领域记录、受控枚举、审计字段和最小不变量。
- 使用 MyBatis-Plus Entity/Mapper/Repository 复用现有持久层模式。
- 通过显式映射保持 UUID、枚举、JSONB 和领域对象之间的边界。
- 提供 finding 发布前证据、计算见证和人工确认的领域守卫。

**Non-Goals:**

- 不新增或修改 evidence 数据库迁移；V4/V8 是当前结构基线。
- 不实现来源查询、规则引擎、覆盖度算法或专业确认 Web API。
- 不在本任务引入事务编排或发布流程；由后续运行时/事务任务负责。

## Decisions

- 每张表使用独立领域对象、应用仓储接口、MyBatis-Plus Entity、Mapper 和 Repository，保持与现有 task/iam 模块一致。
- Entity 主键使用 `IdType.ASSIGN_UUID` 的字符串字段；复合主键 `finding_evidence` 使用输入 UUID 字段，由数据库复合主键保证唯一性。
- JSONB 字段继续使用现有 `JsonbTypeHandler`；领域层对快照 `allowedFields` 复用 `VersionedJson` 的严格 object 校验，其余 JSONB 字段由数据库类型和非空约束承载结构校验。
- 枚举使用领域 enum，Entity 使用数据库对应字符串，映射集中在 `MybatisEvidenceMappings`。
- `Finding.requirePublishable` 只检查当前任务可表达的证据链前置条件：已确认状态、至少一条证据、确定性判断有 witness、专业判断有人工确认；跨仓储的加载编排留给应用层。

替代方案：直接让业务代码操作 Entity 会泄漏数据库结构；复用任务模块映射会形成跨聚合耦合；把所有判断门禁放进数据库触发器无法表达人工确认和可读错误，因此采用领域守卫加数据库 FK/UNIQUE/CHECK 双层约束。

## Risks / Trade-offs

- [JSONB 数组字段的 schema 尚未冻结] → 本任务仅保证非空并通过现有 JSONB 类型处理，具体 schema/规范化由后续工具任务固化。
- [复合主键的 MyBatis-Plus 更新能力有限] → 当前仓储只提供插入和按 finding 查询，不提供依赖单列主键的更新操作。
- [发布门禁需要多仓储读取] → 暂不伪造单表领域判断，在 `Finding.requirePublishable` 中接收已加载的证据链状态。

## Migration Plan

无需数据库迁移。实现完成后运行 Maven 编译、单元测试和 schema 契约测试；用户按任务验收标准抽查数据库证据链。

## Open Questions

无。JSONB 业务 schema 和实际来源字段策略由后续工具任务冻结。
