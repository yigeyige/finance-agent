# Spec Delta

## ADDED Requirements

### Requirement: Decision packet heading and flattened presentation
决策包步骤 SHALL 在“业务决策包”标题右侧以垂直居中方式展示“修订”标签，并将“业务决策包”层扁平化为外壳卡片内的直接内容；数据对象、操作、结论、证据与流程文件卡片 SHALL 直接平铺在外壳卡片内，“业务决策包”不叠加独立边框或背景。

#### Scenario: View packet heading
- **WHEN** 用户进入决策包步骤
- **THEN** “修订”标签位于“业务决策包”标题右侧且垂直居中，不再位于“返回模型协作”按钮左侧

#### Scenario: View flattened packet content
- **WHEN** 用户查看决策包内容
- **THEN** 数据对象、操作、结论、证据与流程文件卡片直接平铺在外壳卡片内，“业务决策包”不叠加独立边框或背景

## MODIFIED Requirements

### Requirement: Viewport-aligned creation and collaboration content
系统 SHALL 在业务说明步骤保持适中高度且不随视口拉伸的说明输入框；业务说明步骤 SHALL 不渲染模型聊天框，业务表单区占满整行宽度，表单各段的剩余空间 SHALL 均衡分布，不得在业务场景与附件材料之间集中留下大片空白。附件材料标签、选择附件按钮和开始模型分析操作 SHALL 靠右，左侧卡片 SHALL 不出现独立纵向滚动条。模型协作步骤与决策包步骤在桌面宽屏下左侧内容超出视口时 SHALL 在左侧卡片内纵向滚动，右侧聊天框保持可见。窄屏单列布局 SHALL 保持自然页面滚动且不裁切操作内容。

#### Scenario: Fill a new business description
- **WHEN** 用户在桌面宽屏打开业务说明步骤
- **THEN** 页面不显示模型聊天框，业务表单区占满整行，说明输入框适度增高，表单字段间距均衡且场景与附件之间没有大片空白，附件标签与按钮靠右，没有独立纵向滚动条

#### Scenario: Review long model collaboration content
- **WHEN** 模型协作步骤的左侧内容超过可用视口高度
- **THEN** 左侧卡片内部出现纵向滚动，右侧聊天框仍在视口内

#### Scenario: Review decision packet content
- **WHEN** 决策包步骤的左侧内容超过可用视口高度
- **THEN** 左侧卡片内部出现纵向滚动，右侧聊天框仍在视口内

#### Scenario: Use a narrow screen
- **WHEN** 页面宽度不足以并排显示左右两栏
- **THEN** 两栏上下排列并按文档自然滚动，所有输入与操作仍可访问
