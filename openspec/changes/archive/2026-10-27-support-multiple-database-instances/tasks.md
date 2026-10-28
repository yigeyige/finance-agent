# Tasks

## 1. 数据库迁移

- [x] 1.1 新增 `V22__drop_ems_database_connection_unique_index.sql` 删除 `governance.uq_ems_database_connection_config_enabled`；验证 Flyway 迁移到 v22 且索引已删除

## 2. 后端连接配置多启用

- [x] 2.1 调整 `EmsDatabaseConnectionConfigRepository` 与 MyBatis 实现：新增 `listEnabled()`，替换 `findEnabled()` 的 `selectOne`；验证编译通过
- [x] 2.2 调整 `EmsDatabaseConnectionConfigService`：`enable()` 去掉 `disableAll`，新增 `resolveEnabled(UUID)`，保留无参 `requireEnabled()` 取第一个启用实例；验证编译通过
- [x] 2.3 同步启用语义相关注释/文案，验证多实例可同时启用且列表展示正确

## 3. 后端数据探索按实例

- [x] 3.1 调整 `DataExplorationService` 的 catalog/columns/query 使用 `resolveEnabled(configId)`；验证编译通过
- [x] 3.2 调整 `DataExplorationController` 与请求/响应契约，catalog/columns 支持可选 `configId` 查询参数、query 请求体支持可选 `configId`；验证接口按实例返回

## 4. 前端菜单与实例切换

- [x] 4.1 更新 `App.vue` 菜单与路由 meta：`EMS数据库配置`→`数据库实例`；验证页面标题一致
- [x] 4.2 更新 `EmsDatabaseConfiguration.vue` 标题与启用文案，去掉“唯一”语义；验证多个实例可同时启用
- [x] 4.3 更新 `DataExploration.vue` 与 `data-exploration-api.ts`：加载启用实例列表、实例下拉切换，目录/字段/查询携带 `configId`；验证切换实例后目录与查询正确
- [x] 4.4 运行前端类型检查与构建，记录结果

## 5. 验证与收尾

- [x] 5.1 运行后端编译与相关测试，记录结果
- [x] 5.2 重启本地后端，验证数据库实例多启用、数据探索实例切换与菜单文案

## Workflow follow-up

- 同步 delta specs 到 `openspec/specs/` 并归档 change。
- 更新任务目标勾选与执行记录，并按项目流程提交。
