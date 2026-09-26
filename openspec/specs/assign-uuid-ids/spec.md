## Purpose

统一 MyBatis-Plus UUID 主键生成策略，在保持 PostgreSQL `uuid` 数据库结构和领域 `UUID` 类型不变的前提下，由持久层自动分配 UUID。

## Requirements

### Requirement: Unified UUID assignment

系统 SHALL 使用 MyBatis-Plus 3.5.17 的 `IdType.ASSIGN_UUID` 为所有持久化 Entity 的 UUID 主键提供默认生成策略；持久化主键属性使用 `String` 接收生成器结果，领域层继续使用 `UUID`，数据源按 PostgreSQL UUID 参数规则绑定字符串，并保持 PostgreSQL `uuid` 类型不变。

#### Scenario: Missing primary key is assigned
- **WHEN** 通过 MyBatis-Plus Mapper 插入未设置主键的持久化 Entity
- **THEN** Entity 在执行插入前获得非空 UUID 字符串主键，领域映射可将其转换为 `UUID`，且不需要新增外部 ID 框架

#### Scenario: Explicit primary key remains compatible
- **WHEN** 通过 Mapper 插入已显式设置 UUID 主键的 Entity
- **THEN** 使用调用方提供的 UUID 字符串值，不被自动生成值覆盖

### Requirement: UUID strategy covers all persistence entities

系统 SHALL 让 IAM、治理和 task 模块的 MyBatis-Plus 持久化 Entity 统一使用 `ASSIGN_UUID`，不得继续使用 `IdType.INPUT` 作为默认主键策略。

#### Scenario: Entity strategy is consistent
- **WHEN** 检查持久化 Entity 的 `@TableId` 配置
- **THEN** UUID 主键均声明 `IdType.ASSIGN_UUID`，持久化主键为 `String`，领域对象仍使用 `java.util.UUID`

#### Scenario: Database contract is preserved
- **WHEN** 执行现有数据库迁移和仓储契约测试
- **THEN** 不修改既有迁移脚本，UUID 主键、外键和现有仓储行为保持兼容
