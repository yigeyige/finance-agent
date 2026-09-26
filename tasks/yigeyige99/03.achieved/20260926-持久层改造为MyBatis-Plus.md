---
id: 20260926-mybatis-plus-persistence
title: "将持久层改造为 MyBatis-Plus 3.5.17"
status: done
priority: high
owner: yigeyige99
created_at: 2026-09-26
type: development
source: conversation
openspec_change: mybatis-plus-persistence
---

# 将持久层改造为 MyBatis-Plus 3.5.17

## 需求

### ID&标题

`20260926-mybatis-plus-persistence` 将持久层改造为 MyBatis-Plus 3.5.17。

### 描述

技术基线已经引入 MyBatis-Plus 3.5.17，现有 IAM、治理和任务模块仍通过 `JdbcTemplate` 访问数据库，需要统一迁移到 MyBatis-Plus Mapper、实体映射和显式 SQL，同时保持现有领域仓储接口、约束和验证行为。

## 背景

当前 `backend` 直接依赖并使用 `spring-jdbc` 的 `JdbcTemplate`。项目规范要求普通单表 CRUD 优先使用 `mybatis-plus-spring-boot4-starter`，复杂 SQL 集中在 Mapper 层，不能继续扩散 `JdbcTemplate`。

## 目标

- [x] 引入并配置 MyBatis-Plus 3.5.17 Spring Boot 4 starter 和 Mapper 扫描。
- [x] 将 IAM、治理、任务模块的 JdbcTemplate 仓储迁移为 MyBatis-Plus Mapper + 持久化实体。
- [x] 保留 JSONB、乐观版本、活动租约、审批唯一性和工具调用幂等约束行为。
- [x] 更新测试和文档，确认业务仓储接口不再依赖 `JdbcTemplate`。

## 验收标准

- `rg` 检索业务持久层不再出现 `JdbcTemplate`、`JdbcDataSourceConfiguration` 或 JDBC RowMapper 实现。
- MyBatis-Plus 版本固定为 3.5.17，应用上下文可加载 Mapper 扫描配置。
- 现有领域仓储契约测试、编译和完整测试通过。
- 不修改 V1-V8 数据库迁移，不执行数据库写入，不启动开发环境。

## 执行记录

- 2026-09-26：从用户需求创建任务，确认现有持久层仍使用 JdbcTemplate，进入 MyBatis-Plus 迁移设计。
- 2026-09-26：完成 MyBatis-Plus 3.5.17 starter、Mapper 扫描、持久化实体、JSONB TypeHandler 及 IAM/governance/task 仓储迁移；删除 JdbcTemplate 仓储实现。
- 2026-09-26：完成全量 Maven 测试、Flyway 迁移校验、严格 OpenSpec 校验和差异检查；测试中的数据库健康检查因本机未提供密码产生预期告警，但测试通过。
