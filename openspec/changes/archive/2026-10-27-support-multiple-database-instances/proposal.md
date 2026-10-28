# Proposal

## Why

当前 EMS 数据库连接强制唯一启用，无法同时配置多个数据库实例；数据探索也只能读取唯一启用实例的元数据。需要放开多实例配置，并让数据探索可在多个实例间切换，为后续按任务选择实例留出空间。

## What Changes

- 侧边菜单「EMS数据库配置」更名为「数据库实例」，配置页标题同步。
- 去掉数据库连接配置的唯一启用约束，允许同时启用多个实例。
- 数据探索页新增「实例」下拉切换，默认第一个启用实例；目录、字段结构与查询按所选实例执行。
- 业务处理模型分析本次仍使用第一个启用实例，任务级实例选择仅记录为后续工作，不在本 change 实现。

## Capabilities

### New Capabilities

（无新增能力）

### Modified Capabilities

- `ems-database-connection-analysis`: EMS 数据库连接由「最多启用一条」改为「允许配置多条并同时启用」；工作台入口文案由「EMS数据库配置」改为「数据库实例」。
- `data-exploration`: 数据探索的元数据浏览与只读查询由「当前启用连接」改为「所选启用实例（默认第一个启用实例）」，并支持页面切换实例。

## Impact

- 后端：`EmsDatabaseConnectionConfigService`/`EmsDatabaseConnectionConfigRepository`（去掉 disableAll、支持多启用、按 id 解析实例）、`DataExplorationService`/`DataExplorationController`（按实例读取目录/字段/查询）、`EmsDatabaseConnectionConfigController`（启用语义）、Flyway `V22` 迁移删除唯一索引。
- 前端：`App.vue` 菜单、`EmsDatabaseConfiguration.vue` 标题与启用文案、`DataExploration.vue` 实例下拉、`data-exploration-api.ts` 契约参数。
- 契约：数据探索目录/字段/查询接口支持可选 `configId` 定位实例。
