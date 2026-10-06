# task-intake-scope-ui Specification

## Purpose
TBD - created by archiving change task-intake-scope-ui. Update Purpose after archive.
## Requirements
### Requirement: Task intake and scope confirmation form
工作台 SHALL 提供任务标题、问题、前提来源和范围确认表单，在提交前校验必填字段和 JSON 字段，并在每次写请求进行中阻止重复提交。任务草稿创建成功后 SHALL 显示返回的任务标识和行版本并进入前提来源步骤；前提保存成功后 SHALL 进入范围确认步骤；范围确认成功后 SHALL 显示任务状态、scope revision 和最新 row version。

#### Scenario: Create and confirm scope
- **WHEN** OPERATOR 填写任务、前提和范围并依次提交
- **THEN** 页面依次进入前提来源和范围确认步骤，最终显示任务状态、scope revision 和最新 row version

#### Scenario: Task draft creation fails

- **WHEN** 任务草稿创建请求发生网络错误或后端拒绝请求
- **THEN** 页面保留任务问题表单、恢复提交按钮并显示明确中文错误提示

#### Scenario: Duplicate task draft submission is blocked

- **WHEN** 任务草稿创建请求仍在处理中且用户再次触发提交
- **THEN** 页面不发送第二个创建请求

#### Scenario: Invalid scope input
- **WHEN** 范围 JSON 非法或三类字段权限为空
- **THEN** 页面阻止提交并显示中文校验提示

### Requirement: Stale scope feedback
页面 SHALL 显示旧 row version 或范围冲突，禁止把冲突响应当作成功。

#### Scenario: Old row version
- **WHEN** 后端返回 409 或 412
- **THEN** 页面提示范围已变化并要求重新读取后再提交
