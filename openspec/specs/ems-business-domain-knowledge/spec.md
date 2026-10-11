# EMS Business Domain Knowledge Specification

## Purpose

为财务运维工作台提供受控、只读的 EMS 业务域结构知识目录，支持后续业务场景定位，同时不接入运行时 EMS 数据库。

## Requirements

### Requirement: Curated EMS business domain catalog

系统 SHALL 提供一份内置且只读的 EMS 财务系统业务域目录。每个业务域 MUST 包含业务名称、用途、所属数据库、结构快照表数量、关键表、结构知识可信边界和后续核实提示。目录 MUST 包含基础主数据、审批流程、会计处理、预算管理、付款管理、稽核管理、智能审核、移动服务和运行配置。

#### Scenario: Authorized user reads domain catalog

- **WHEN** 具有 `TASK_READ` 权限的用户请求 EMS 业务知识域目录
- **THEN** 系统返回所有受控业务域及其已批准的结构知识，且不访问 EMS 业务数据库

#### Scenario: Catalog preserves structural knowledge boundary

- **WHEN** 用户查看任一业务域
- **THEN** 系统明确说明该目录仅基于已核实结构快照，跨表业务关系、状态枚举和权威来源仍待样本或代码确认

### Requirement: Catalog excludes out-of-scope databases and sensitive data

系统 MUST NOT 在 EMS 业务知识域目录中包含 `claim`、`xxljob`、`arch_db`，也 MUST NOT 返回真实人员、账号、单据、金额、连接信息、任意 SQL、脚本正文或未批准的表字段。

#### Scenario: User reads complete catalog

- **WHEN** 用户读取完整 EMS 业务知识域目录
- **THEN** 响应中不出现被排除数据库或敏感运行数据

### Requirement: Catalog access is protected

系统 SHALL 在服务端校验 `TASK_READ` 权限后才返回 EMS 业务知识域目录。

#### Scenario: Unauthorized user requests catalog

- **WHEN** 不具有 `TASK_READ` 权限的身份直接调用目录接口
- **THEN** 系统拒绝请求且不返回任何目录内容

### Requirement: Workbench displays business domain knowledge

前端 SHALL 在受保护的工作台页面展示 EMS 业务知识域目录，并提供加载中、空数据和读取失败状态。每个域 SHALL 显示其用途、所属库、关键表和可信边界；页面 MUST 使用既有工作台标题、内容卡片和可读表格或详情样式。

#### Scenario: User opens a domain detail

- **WHEN** 已授权用户进入 EMS业务知识域页面并展开一个业务域
- **THEN** 页面展示该域受控的关键表及核实提示，不渲染 HTML 或敏感原始数据

#### Scenario: Catalog request fails

- **WHEN** 前端读取 EMS 业务知识域目录失败
- **THEN** 页面展示中文失败说明和重新加载入口，且不显示陈旧或伪造的目录内容
