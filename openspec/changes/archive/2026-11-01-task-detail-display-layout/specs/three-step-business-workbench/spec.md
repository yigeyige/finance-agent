## MODIFIED Requirements

### Requirement: Read-only task detail three-step view
系统 SHALL 在从任务列表双击进入的任务详情页，以与新建业务处理一致的三步骤（业务说明、模型协作、决策包）布局只读展示任务；页面 SHALL 隐藏模型聊天框与全部业务操作按钮，仅允许点击步骤切换查看。业务说明 SHALL 以与创建模式接近的字段顺序和双列布局显示业务标题、业务说明、业务场景、数据库实例、发起人名称、处理状态和创建时间，不得以发起人内部 ID 替代名称；无关联业务场景或数据库实例时 SHALL 显示空值占位。决策包存在流程产物按钮时，按钮所在标题栏 SHALL 与下方卡片保持可见间距。

#### Scenario: Open a task from the list
- **WHEN** 管理员双击任务列表中的任务行
- **THEN** 进入只读详情并以三步骤布局展示，步骤可点击切换，不显示聊天框或业务操作按钮

#### Scenario: View business description
- **WHEN** 用户打开业务说明步骤
- **THEN** 页面显示发起人名称、业务场景名称和数据库实例名称，业务标题和说明占整行，桌面端场景和实例同排且窄屏单列

#### Scenario: View task without optional associations
- **WHEN** 任务没有关联业务场景或数据库实例
- **THEN** 对应字段显示“—”，不显示内部 ID

#### Scenario: View process artifacts entry
- **WHEN** 决策包步骤有流程产物按钮
- **THEN** 标题栏和下方卡片之间存在可见间距

#### Scenario: View unavailable result
- **WHEN** 某步骤对应的结论、证据或决策包尚未生成
- **THEN** 仍可切换到该步骤并展示对应的空状态文案
