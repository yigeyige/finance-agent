# Spec Delta

## MODIFIED Requirements

### Requirement: Task draft intake

系统 SHALL 通过任务应用服务创建 `DRAFT` 调查任务，保存原始问题、标题、发起人、运行版本和可选数据库实例；创建请求不得启动步骤执行或跨范围读取。

#### Scenario: Create a task draft
- **WHEN** 有效的任务标题、原始问题、发起人和 runtime bundle 被提交，可选携带已启用的数据库实例
- **THEN** 系统创建一个 `DRAFT` 任务并返回任务标识、行版本和审计事件

#### Scenario: Invalid task request is rejected
- **WHEN** 标题、原始问题、操作者或 runtime bundle 缺失/为空
- **THEN** 请求在持久化前失败且不创建任务或审计事件
