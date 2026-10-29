## MODIFIED Requirements

### Requirement: Simplified heading and input layout
系统 SHALL 使用“财务智能运维Agent”单一顶栏标题，保留用户菜单，且左侧菜单不变。业务处理页顶部 SHALL 显示与任务信息分区一致的三步骤条（业务说明、模型协作、决策包）并高亮当前步骤，卡片内不再重复渲染步骤条，也不再渲染任务标题/内容摘要；任务信息分区 SHALL 将小型“保存任务”按钮置于分区右上角，不在表单底部重复出现。业务场景与数据库实例在桌面同排，窄屏单列。

#### Scenario: Start a new task
- **WHEN** 用户进入新建业务处理
- **THEN** 顶部显示三步骤条并高亮业务说明，卡片内无重复步骤条，无重复工作台标题或空等待区，两个选择项同排并保持完整标签

#### Scenario: Edit task info in collaboration step
- **WHEN** 用户在模型协作步骤查看任务信息
- **THEN** 分区右上角显示紧凑“保存任务”按钮，表单底部不再出现保存按钮
