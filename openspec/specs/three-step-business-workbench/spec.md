# 业务处理三步工作台

## Purpose

定义业务说明输入、模型协作与决策包成果的三步工作台展示，使用户明确当前操作阶段、模型反馈与最终成果的关系。同时规定紧凑输入布局、聊天角色和回复状态、发送保护及自动滚动，以复用现有业务接口并维持可信身份和左侧导航边界。

## Requirements

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

### Requirement: Three distinct workbench steps
系统 SHALL 将业务处理展示为业务说明、模型协作、决策包三步；生成成功后进入第三步，失败保留第二步。第三步 SHALL 支持返回协作且保留任务和消息；模型再次修改或成功保存任务信息后 SHALL 重新生成决策包再展示成果。

#### Scenario: Generate or retry a packet
- **WHEN** 用户在协作页生成决策包
- **THEN** 忙碌状态不重复发送请求，成功才进入决策包页，失败允许保留上下文重试

#### Scenario: Continue collaboration
- **WHEN** 用户从成果返回协作并收到新模型响应或成功保存任务信息
- **THEN** 保留任务和会话，清除本地旧成果展示并允许重新生成，服务端历史不变

### Requirement: Conversational collaboration panel
系统 SHALL 提供角色明确的聊天消息、分析与回复状态、底部输入框和发送按钮，禁止空白或忙碌状态发送，并支持 Ctrl+Enter 和最新消息自动滚动。

#### Scenario: Send and receive a revision
- **WHEN** 模型初稿已可用且用户输入非空修改意见
- **THEN** 可以按钮或快捷键发送，发送中禁止重复发送，新消息出现后滚动到最新内容

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

### Requirement: Read-only task detail long-content presentation
系统 SHALL 在桌面宽屏的任务详情“模型协作”和“决策包”步骤中，将长内容的纵向滚动限制在表单内容卡片内部，使顶部步骤栏保持可见；窄屏或较矮窗口 SHALL 保持内容可访问。模型协作的检查结论和关联证据 SHALL 分别以卡片展示，检查结论文字 SHALL 占满卡片内容宽度，并按分号或句号分行显示，不得改变原始结论数据。

#### Scenario: Scroll long model collaboration content
- **WHEN** 模型协作的检查结论或证据超出可用视口高度
- **THEN** 表单卡片内部纵向滚动，顶部步骤栏仍可见

#### Scenario: Read guidance result cards
- **WHEN** 用户打开已有检查结论与关联证据的任务详情
- **THEN** 检查结论与关联证据各有卡片，结论占整行且在分号或句号后换行

#### Scenario: Read empty guidance result
- **WHEN** 检查结论或关联证据尚未生成
- **THEN** 对应卡片保留中文空状态文案

#### Scenario: Scroll long decision packet
- **WHEN** 决策包的数据对象、操作、结论或证据超出可用视口高度
- **THEN** 表单卡片内部纵向滚动，顶部步骤栏仍可见

### Requirement: Decision packet heading and flattened presentation
决策包步骤 SHALL 在“业务决策包”标题右侧以垂直居中方式展示“修订”标签，并将“业务决策包”层扁平化为外壳卡片内的直接内容；数据对象、操作、结论、证据与流程文件卡片 SHALL 直接平铺在外壳卡片内，“业务决策包”不叠加独立边框或背景。

#### Scenario: View packet heading
- **WHEN** 用户进入决策包步骤
- **THEN** “修订”标签位于“业务决策包”标题右侧且垂直居中，不再位于“返回模型协作”按钮左侧

#### Scenario: View flattened packet content
- **WHEN** 用户查看决策包内容
- **THEN** 数据对象、操作、结论、证据与流程文件卡片直接平铺在外壳卡片内，“业务决策包”不叠加独立边框或背景
