# task-intake-scope-ui Specification

## Purpose
TBD - created by archiving change task-intake-scope-ui. Update Purpose after archive.
## Requirements
### Requirement: Task intake and scope confirmation form
工作台 SHALL 提供任务标题、问题、前提来源和范围确认表单，并在提交前校验必填字段和 JSON 字段。

#### Scenario: Create and confirm scope
- **WHEN** OPERATOR 填写任务、前提和范围并依次提交
- **THEN** 页面显示任务状态、scope revision 和 row version

#### Scenario: Invalid scope input
- **WHEN** 范围 JSON 非法或三类字段权限为空
- **THEN** 页面阻止提交并显示中文校验提示

### Requirement: Stale scope feedback
页面 SHALL 显示旧 row version 或范围冲突，禁止把冲突响应当作成功。

#### Scenario: Old row version
- **WHEN** 后端返回 409 或 412
- **THEN** 页面提示范围已变化并要求重新读取后再提交

