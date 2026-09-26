## ADDED Requirements

### Requirement: MyBatis-Plus persistence baseline

系统 SHALL 使用 `mybatis-plus-spring-boot4-starter` 3.5.17 配置 Mapper 扫描和 DataSource；IAM、治理和 task 仓储 SHALL 不直接依赖 `JdbcTemplate`。

#### Scenario: Mapper context loads
- **WHEN** 应用测试上下文加载
- **THEN** MyBatis-Plus Mapper 被扫描并可以注入，未注册 JdbcTemplate bean

#### Scenario: Version is fixed
- **WHEN** 构建解析后端依赖
- **THEN** MyBatis-Plus starter 版本为 3.5.17 且与 Spring Boot 4 适配

### Requirement: Domain repository behavior is preserved

系统 SHALL 保持现有领域仓储接口和数据库约束行为，仅将实现替换为 Entity、Mapper 和显式映射。

#### Scenario: Published runtime bundle remains filtered
- **WHEN** 通过运行版本仓储查询指定 bundle
- **THEN** 只有 `PUBLISHED` 状态返回领域对象

#### Scenario: Task concurrency and idempotency remain guarded
- **WHEN** 更新任务版本、获取活动租约或写入重复工具幂等键
- **THEN** Mapper SQL 保留乐观版本、部分唯一索引和唯一键约束行为

### Requirement: PostgreSQL JSONB mapping

系统 SHALL 通过参数化 MyBatis TypeHandler 读写范围 JSONB，不能将 JSON 文本拼接到 SQL。

#### Scenario: Scope JSON uses jsonb handler
- **WHEN** 范围修订被插入或读取
- **THEN** Mapper 使用 JSONB TypeHandler 与 PostgreSQL `jsonb` 类型交互，领域层仍接收版本化 JSON 值对象
