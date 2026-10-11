# EMS 数据库连接配置与业务处理模型分析

## Why

EMS 业务知识域已说明业务对象及关键表，但业务处理模型尚不能核实审批、角色、会计组和付款权限等实时配置。需要为平台管理员维护一条 EMS MySQL 8 连接，并以受控 Agent 工具向模型提供最小化查询结果。

## What Changes

- 引入 Spring Boot 4.0.4 管理的 MySQL Connector/J 9.6.0。
- 增加加密保存、唯一启用、连接测试与平台管理员保护的 EMS 数据库连接配置。
- 增加仅供后端 Agent 调用的只读查询执行器：只接受单条 `SELECT`，同时限制 EMS 业务库、超时和返回行数。
- 在业务处理模型指导生成前尝试规划并执行受控查询，将查询结果作为非权威辅助证据；未配置、无须查询或查询失败时保持现有模型流程。
- 在大模型配置下增加 EMS 数据库配置入口和页面。

## Non-goals

- 不向模型、浏览器或日志公开数据库密码、密文或完整错误详情。
- 不支持任何写入、DDL、存储过程、多语句、任意 SQL 控制台或人工直接执行 SQL。
- 不接入 `claim`、`xxljob`、`arch_db`、MySQL 系统库或生产发布。

## Impact

- 受影响模块：`modelconfig`、`harness`、`model`、`iam`、前端路由和配置页面、Flyway migration。
- 新增依赖：`com.mysql:mysql-connector-j`（由当前 Spring Boot BOM 管理）。
- 新增数据库表：`governance.ems_database_connection_config`。
