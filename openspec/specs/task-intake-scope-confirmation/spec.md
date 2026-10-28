## Purpose

为调查任务提供受控的任务发起、运维前提留痕、范围确认和版本化 REST 接入边界，确保未确认范围不会进入执行路径，并以最小审计信息支持追溯。

## Requirements

### Requirement: Task draft intake

系统 SHALL 通过任务应用服务创建 `DRAFT` 调查任务，保存原始问题、标题、发起人、运行版本和可选数据库实例；创建请求不得启动步骤执行或跨范围读取。

#### Scenario: Create a task draft

- **WHEN** 有效的任务标题、原始问题、发起人和 runtime bundle 被提交，可选携带已启用的数据库实例
- **THEN** 系统创建一个 `DRAFT` 任务并返回任务标识、行版本和审计事件

#### Scenario: Invalid task request is rejected

- **WHEN** 标题、原始问题、操作者或 runtime bundle 缺失/为空
- **THEN** 请求在持久化前失败且不创建任务或审计事件

#### Scenario: Update task basic information
- **WHEN** OPERATOR 修改业务说明、业务场景 Skill 或数据库实例
- **THEN** 系统更新任务对应字段并返回最新任务视图，且未确认范围时仍保持可继续创建流程

### Requirement: Operational premise provenance

系统 SHALL 将运维人员提供的前提作为独立记录保存，包含主体标识、提供人和独立核实标志；前提不得被改写为源系统已核实事实。

#### Scenario: Store an operational premise

- **WHEN** 任务存在且运维人员提交一条带来源说明的前提
- **THEN** 系统追加 `task_premise` 记录并写入最小审计事件，`independentlyVerified` 默认保持为 false

#### Scenario: Premise source is explicit

- **WHEN** 前提缺少主体标识或来源说明
- **THEN** 请求被拒绝且不产生前提记录

### Requirement: Confirmed immutable scope revision

系统 SHALL 只通过结构化确认请求创建不可变 `ScopeRevision`，保存对象、排除路径及读取、留存、送模字段权限，并计算 revision 摘要。

#### Scenario: Confirm scope and enter ready state

- **WHEN** 任务处于草稿/等待范围确认/可就绪阶段且提交严格合法的范围 JSON
- **THEN** 系统追加递增范围修订、绑定任务当前范围并通过状态服务进入 `READY`

#### Scenario: Scope JSON must be strict and object-shaped

- **WHEN** 任一范围 JSON 非法、带尾随 token、schema 版本为空或对象选择不是 JSON object
- **THEN** 系统在数据库写入前拒绝请求

#### Scenario: Unconfirmed scope cannot run

- **WHEN** 任务尚未绑定确认范围时尝试进入步骤审批或运行路径
- **THEN** 状态/应用守卫拒绝该操作，不产生步骤执行副作用

### Requirement: Intake REST contract and audit

系统 SHALL 提供版本化 REST 接口创建任务、追加前提和确认范围；写请求必须带显式操作者和幂等键头，响应不得包含 prompt、reasoning 或源系统原始响应。

#### Scenario: REST intake endpoints are documented

- **WHEN** 读取 OpenAPI 3.1 契约
- **THEN** 契约包含 `POST /api/v1/tasks`、`POST /api/v1/tasks/{taskId}/premises` 和 `POST /api/v1/tasks/{taskId}/scope-confirmations`，并引用独立 JSON Schema

#### Scenario: Audit event is minimal

- **WHEN** 任务、前提或范围确认成功
- **THEN** 审计链记录动作类型、任务标识、操作者、修订/摘要元数据，不保存完整请求原文或模型内容
