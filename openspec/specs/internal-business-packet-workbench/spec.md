# internal-business-packet-workbench Specification

## Purpose
TBD - created by archiving change present-internal-business-packets. Update Purpose after archive.

## Requirements

### Requirement: Direct administrator packet workflow
工作台 SHALL 复用现有组件提供生成中、成功、失败重试和历史查看。管理员已有包时任务信息 SHALL 只读，但继续补证和重新生成仍可操作。权限 SHALL 沿用现状，本切片不新增角色隔离。

#### Scenario: Generation succeeds
- **WHEN** 管理员成功生成决策包
- **THEN** 页面展示包并禁用基础信息编辑，保留补证和再次生成入口

#### Scenario: New generation fails
- **WHEN** 已有旧包的任务新一次生成失败
- **THEN** 页面保留旧包并展示中文错误，允许重试
