# Design

## Context

EMS 数据库连接当前通过 `governance.ems_database_connection_config` 唯一启用（V17 部分唯一索引 + `enable()` 先 `disableAll`）。数据探索与模型分析都通过 `requireEnabled()` 取唯一启用实例。本次需放开多实例配置，并让数据探索按实例切换。动机见 proposal.md。

## Goals / Non-Goals

**Goals:**

- 允许配置并同时启用多个 EMS 数据库实例。
- 数据探索页可切换实例，默认第一个启用实例。
- 侧边菜单与配置页文案改为「数据库实例」。
- 模型分析继续使用第一个启用实例，保持既有行为。

**Non-Goals:**

- 不实现任务级数据库实例选择与持久化（后续 change）。
- 不改变 EMS 只读 SQL Guard 的 schema/表白名单与只读校验规则。
- 不改变模型分析的跨库/多库查询策略。

## Decisions

1. **去掉唯一启用约束**
   - 新增 `V22__drop_ems_database_connection_unique_index.sql`，删除部分唯一索引 `governance.uq_ems_database_connection_config_enabled`。
   - `EmsDatabaseConnectionConfigService.enable()` 去掉 `repository.disableAll(...)` 调用，允许存在多个 `enabled=true`。
   - Repository 新增 `listEnabled()`（`selectList` 过滤 `enabled=true`），替代 `findEnabled()` 的 `selectOne`（多行会抛异常）。

2. **实例解析语义**
   - `EmsDatabaseConnectionConfigService` 新增 `resolveEnabled(UUID configId)`：`configId` 为 null 时返回第一个启用实例；否则按 id 查找并要求 `enabled=true`。
   - 保留无参 `requireEnabled()` 供模型分析使用，语义不变（取第一个启用实例），避免破坏既有业务处理。

3. **数据探索接口按实例定位**
   - `GET /catalog?configId=`、`GET /tables/{schema}/{table}/columns?configId=`、`POST /queries` 请求体新增可选 `configId`。
   - `DataExplorationService` 的 catalog/columns/query 通过 `resolveEnabled(configId)` 解析实例。
   - 备选：新增独立 `/instances` 列表接口——被否，前端可直接复用既有 `listEmsDatabaseConfigurations()` 过滤 enabled。

4. **前端实例下拉**
   - `DataExploration.vue` 加载启用实例列表（复用 `listEmsDatabaseConfigurations` 过滤 `enabled`），提供下拉，默认第一个启用实例。
   - 切换实例后重新加载目录；字段查看与查询请求携带 `configId`。
   - 备选：后端提供启用实例列表——被否，避免重复契约。

## Risks / Trade-offs

- [多个实例同时启用后，模型分析只取第一个，可能不是用户预期实例] → 本次明确 Non-Goal，任务级实例关联在后续 change 处理；当前行为与既有单实例一致。
- [去掉唯一索引后，若多个实例 schema 重叠，数据探索目录互不干扰] → 每次只按所选实例读取，目录不跨实例聚合。
- [契约新增可选 `configId` 需保持向后兼容] → 不传时回退到第一个启用实例，旧调用不受影响。

## Migration Plan

1. 新增 V22 迁移删除唯一索引并触发 Flyway。
2. 后端调整 enable/resolveEnabled/repository/数据探索服务与控制器。
3. 前端调整菜单、配置页文案与数据探索实例切换。
4. 运行后端编译/测试与前端类型检查/构建，重启后验证多实例启用与数据探索切换。
