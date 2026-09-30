## MODIFIED Requirements

### Requirement: Domain repository behavior is preserved

系统 SHALL 保持现有领域仓储接口和数据库约束行为，仅将实现替换为 Entity、Mapper 和显式映射。对于以 `String` 主键字段配置 `IdType.ASSIGN_UUID` 的实体，领域 `UUID` 在调用 `BaseMapper.selectById` 前 SHALL 复用统一的存储映射。

#### Scenario: Published runtime bundle remains filtered

- **WHEN** 通过运行版本仓储查询指定 bundle
- **THEN** 只有 `PUBLISHED` 状态返回领域对象

#### Scenario: Task concurrency and idempotency remain guarded

- **WHEN** 更新任务版本、获取活动租约或写入重复工具幂等键
- **THEN** Mapper SQL 保留乐观版本、部分唯一索引和唯一键约束行为

#### Scenario: String UUID primary key lookup receives storage text

- **WHEN** 仓储以领域 `UUID` 查询使用 `ASSIGN_UUID` 字符串主键的实体
- **THEN** 仓储在调用 `BaseMapper.selectById` 前将该值转换为标准 UUID 字符串，且领域接口仍接收 `UUID`

#### Scenario: Non-primary UUID values use PostgreSQL mapping

- **WHEN** MyBatis-Plus 读写实体中的 UUID 外键或审计字段
- **THEN** 通过统一 PostgreSQL UUID TypeHandler 以 JDBC `OTHER` 参数类型绑定，并将数据库返回值转换为领域 `UUID`
