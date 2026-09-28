## Why

项目已经具备快照、判断和计算见证领域模型，但还没有版本化规则执行边界，无法保证同一证据复算可比，也无法在字段越权、摘要不匹配或未知规则版本时安全拒绝确定性结论。

## What Changes

- 增加版本化确定性规则 SPI、获准快照输入规范化和首个人员引用规则。
- 增加规则执行服务，输出 `Finding`、输入/输出摘要、规则/算法版本和快照 ID 的 `ComputationWitness`。
- 增加独立 `rules.recompute` 复算服务，返回 `MATCHED`、`MISMATCHED` 或 `UNAVAILABLE`。
- 对缺快照、敏感字段、摘要变化和未知规则版本补充负向测试，不新增依赖或数据库迁移。

## Capabilities

### New Capabilities

- `deterministic-rules`: 版本化规则执行、人员引用规则和计算见证复算。

### Modified Capabilities

<!-- No existing requirement changes. -->

## Impact

- 影响 evidence/application、rules/application 与测试代码，复用现有 `Finding`、`ComputationWitness`、`TrustedFieldProjector`、`CanonicalJson`。
- 不修改数据库结构和外部 API，不引入新框架或依赖。
