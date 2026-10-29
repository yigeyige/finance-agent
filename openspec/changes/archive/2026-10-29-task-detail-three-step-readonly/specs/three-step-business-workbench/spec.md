## ADDED Requirements

### Requirement: Read-only task detail three-step view
系统 SHALL 在从任务列表双击进入的任务详情页，以与新建业务处理一致的三步骤（业务说明、模型协作、决策包）布局只读展示任务；页面 SHALL 隐藏模型聊天框与全部业务操作按钮，仅允许点击步骤切换查看。

#### Scenario: Open a task from the list
- **WHEN** 管理员双击任务列表中的任务行
- **THEN** 进入只读详情并以三步骤布局展示，步骤可点击切换，不显示聊天框或业务操作按钮

#### Scenario: View unavailable result
- **WHEN** 某步骤对应的结论、证据或决策包尚未生成
- **THEN** 仍可切换到该步骤并展示对应的空状态文案
