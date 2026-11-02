# Spec Delta

## MODIFIED Requirements

### Requirement: Simplified heading and input layout
系统 SHALL 使用“财务智能运维Agent”单一顶栏标题，保留用户菜单，且左侧菜单不变。业务处理页顶部 SHALL 显示与任务信息分区一致的三步骤条（业务说明、模型协作、决策包）并高亮当前步骤，卡片内不再重复渲染步骤条，也不再渲染任务标题/内容摘要；任务信息分区 SHALL 将小型“保存任务”按钮置于分区右上角，不在表单底部重复出现。协作区“生成决策包”按钮 SHALL 与“保存任务”按钮统一为小号。业务场景与数据库实例在桌面同排，窄屏单列。

#### Scenario: Start a new task
- **WHEN** 用户进入新建业务处理
- **THEN** 顶部显示三步骤条并高亮业务说明，卡片内无重复步骤条，无重复工作台标题或空等待区，两个选择项同排并保持完整标签

#### Scenario: Edit task info in collaboration step
- **WHEN** 用户在模型协作步骤查看任务信息
- **THEN** 分区右上角显示紧凑“保存任务”按钮，表单底部不再出现保存按钮

#### Scenario: Align action button size
- **WHEN** 用户在模型协作步骤查看“保存任务”与“生成决策包”按钮
- **THEN** 两个按钮均使用小号尺寸，视觉一致

### Requirement: Viewport-aligned creation and collaboration content
系统 SHALL 在业务说明步骤保持适中高度且不随视口拉伸的说明输入框；业务说明步骤 SHALL 不渲染模型聊天框，业务表单区占满整行宽度，表单各段的剩余空间 SHALL 均衡分布，不得在业务场景与附件材料之间集中留下大片空白。附件材料标签、选择附件按钮和开始模型分析操作 SHALL 靠右，左侧卡片 SHALL 不出现独立纵向滚动条。模型协作步骤在桌面宽屏下左侧内容超出视口时 SHALL 在左侧卡片内纵向滚动，右侧聊天框保持可见。窄屏单列布局 SHALL 保持自然页面滚动且不裁切操作内容。

#### Scenario: Fill a new business description
- **WHEN** 用户在桌面宽屏打开业务说明步骤
- **THEN** 页面不显示模型聊天框，业务表单区占满整行，说明输入框适度增高，表单字段间距均衡且场景与附件之间没有大片空白，附件标签与按钮靠右，没有独立纵向滚动条

#### Scenario: Review long model collaboration content
- **WHEN** 模型协作步骤的左侧内容超过可用视口高度
- **THEN** 左侧卡片内部出现纵向滚动，右侧聊天框仍在视口内

#### Scenario: Use a narrow screen
- **WHEN** 页面宽度不足以并排显示左右两栏
- **THEN** 两栏上下排列并按文档自然滚动，所有输入与操作仍可访问
