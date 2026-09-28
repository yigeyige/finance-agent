## Why

合成财务配置源已经能够返回分页结果，但目前没有统一的全量枚举、分页防循环和覆盖度证据生成能力，无法可靠支撑限定范围内的否定结论。现在补齐可验证的分页遍历契约，能够把缺页、动态变化和来源失败显式降级，而不引入新的运行时依赖。

## What Changes

- 增加通用分页枚举结果模型，记录页数、预期总量、实际总量、遍历路径、水位、失败原因和结果集摘要。
- 增加分页枚举器与覆盖度验证器，检测重复 token、总量变化、页遗漏、路径失败和水位变化。
- 复用现有 `CoverageRecord`、`CoverageCompleteness` 和 SHA-256 摘要能力生成覆盖度证据。
- 为完整、部分、未知覆盖场景补充单元测试；不新增外部依赖或数据库迁移。

## Capabilities

### New Capabilities

- `coverage-validation`: 为只读来源提供稳定分页枚举、覆盖度判断和 `CoverageRecord` 生成能力。

### Modified Capabilities

<!-- No existing requirement changes. -->

## Impact

- 影响后端 source/evidence 应用层与领域层代码及其测试。
- 不改变现有来源查询 API，不修改数据库结构，不增加依赖。
