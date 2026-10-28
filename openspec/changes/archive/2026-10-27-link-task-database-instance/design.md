# Design

## Context

模型分析当前默认使用第一个启用的 EMS 实例；任务创建没有数据库实例入口。动机见 proposal.md。

## Goals / Non-Goals

**Goals:**

- 任务支持可选数据库实例并持久化；模型分析按任务实例执行，未选则不查询。
- 新增 OPERATOR 可读的启用实例列表接口。

**Non-Goals:**

- 不改草稿/范围/前提字段（后续 change）。
- 不改 Skill 版本冻结、决策包模型分析。
- 不新增任务更新接口（后续 change）。

## Decisions

1. **启用实例列表权限**
   - 新增只读接口返回启用实例的标识与名称（不含连接配置/凭据），供 OPERATOR 在第一步选择；保留现有 PLATFORM_ADMIN 的完整配置列表接口。

2. **任务-数据库实例关联**
   - `InvestigationTask` 增加 `databaseConfigId`（nullable），`task.investigation_task` 新增 `database_config_id` 列。
   - `CreateTaskRequest`/`CreateTaskDraftCommand` 增加可选 `databaseConfigId`。

3. **EMS 辅助查询按任务实例**
   - `EmsDatabaseAssistanceService.collect` 增加 `UUID databaseConfigId` 参数；为空直接返回空证据；非空用 `configService.resolveEnabled(configId)` 解析实例。

## Risks / Trade-offs

- [任务未选实例时不产生证据，结论可能缺少 EMS 佐证] → 符合“无需数据库数据参与”的语义，模型仍基于业务说明生成草稿。
- [任务更新需要乐观锁与幂等] → 沿用任务 rowVersion 校验。

## Migration Plan

1. 新增 `V24__task_database_instance.sql`：`task.investigation_task` 加 `database_config_id` 并触发 Flyway。
2. 后端同步任务字段、模型分析链路与两个新接口。
3. 前端同步第一步实例选择与第二步修改。
4. 运行后端编译/测试、前端类型检查/构建，重启验证。
