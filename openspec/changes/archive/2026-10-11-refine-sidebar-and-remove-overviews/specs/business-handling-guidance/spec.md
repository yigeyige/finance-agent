## MODIFIED Requirements

### Requirement: Unified administrative entry layout
系统 SHALL 为业务处理向导和业务场景 Skill 管理页面提供一致的工作台布局，包括统一的标题节奏、明确操作区和圆角内容卡片。业务场景 Skill 管理页面 MUST 在标题后直接展示操作区和记录内容，不得展示重复业务名称、状态或数量的总览卡片。

#### Scenario: Administrator switches between handling and Skill management
- **WHEN** 管理员在新建业务处理与业务场景 Skill 页面之间切换
- **THEN** 两个页面以一致的标题、操作和内容区层级展示，且原有任务创建、Skill 新增、编辑、删除和开始处理入口保持可用
