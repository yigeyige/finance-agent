## MODIFIED Requirements

### Requirement: Viewport-aligned creation and collaboration content
系统 SHALL 在业务说明步骤保持适中高度且不随视口拉伸的说明输入框；桌面宽屏下左侧业务信息卡片 SHALL 与右侧聊天框等高，附件材料标签、选择附件按钮和开始模型分析操作 SHALL 靠右，左侧卡片 SHALL 不出现独立纵向滚动条。模型协作步骤在桌面宽屏下左侧内容超出视口时 SHALL 在左侧卡片内纵向滚动，右侧聊天框保持可见。窄屏单列布局 SHALL 保持自然页面滚动且不裁切操作内容。

#### Scenario: Fill a new business description
- **WHEN** 用户在桌面宽屏打开业务说明步骤
- **THEN** 左侧卡片与聊天框下缘对齐、说明输入框高度适中，附件标签与按钮靠右，左侧卡片没有独立纵向滚动条

#### Scenario: Review long model collaboration content
- **WHEN** 模型协作步骤的左侧内容超过可用视口高度
- **THEN** 左侧卡片内部出现纵向滚动，右侧聊天框仍在视口内

#### Scenario: Use a narrow screen
- **WHEN** 页面宽度不足以并排显示左右两栏
- **THEN** 两栏上下排列并按文档自然滚动，所有输入与操作仍可访问
