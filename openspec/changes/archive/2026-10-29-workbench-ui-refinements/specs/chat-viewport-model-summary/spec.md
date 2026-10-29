## MODIFIED Requirements

### Requirement: Fill the available chat viewport
系统 SHALL 在新建业务处理页显示与任务列表同层级的“业务处理”标题；聊天面板标题 SHALL 为“模型聊天”，根据实际顶部位置与视口大小填满可用高度，输入框与“发送”按钮同排且发送按钮垂直居中，消息区独立滚动，不显示“等待业务说明”。

#### Scenario: Resize viewport or create task
- **WHEN** 视口大小或任务概览导致聊天顶部位置变化
- **THEN** 聊天高度更新到可用区域，发送按钮与输入框同排居中，清理卸载后的观察与监听，发送区保持可见
