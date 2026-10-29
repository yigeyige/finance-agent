# 业务处理三步工作台

## Purpose

定义业务说明输入、模型协作与决策包成果的三步工作台展示，使用户明确当前操作阶段、模型反馈与最终成果的关系。同时规定紧凑输入布局、聊天角色和回复状态、发送保护及自动滚动，以复用现有业务接口并维持可信身份和左侧导航边界。

## Requirements

### Requirement: Simplified heading and input layout
系统 SHALL 使用“财务智能运维Agent”单一顶栏标题，保留用户菜单，且左侧菜单不变。业务处理页顶部 SHALL 显示与任务信息分区一致的三步骤条（业务说明、模型协作、决策包）并高亮当前步骤，卡片内不再重复渲染步骤条，也不再渲染任务标题/内容摘要；任务信息分区 SHALL 将小型“保存任务”按钮置于分区右上角，不在表单底部重复出现。业务场景与数据库实例在桌面同排，窄屏单列。

#### Scenario: Start a new task
- **WHEN** 用户进入新建业务处理
- **THEN** 顶部显示三步骤条并高亮业务说明，卡片内无重复步骤条，无重复工作台标题或空等待区，两个选择项同排并保持完整标签

#### Scenario: Edit task info in collaboration step
- **WHEN** 用户在模型协作步骤查看任务信息
- **THEN** 分区右上角显示紧凑“保存任务”按钮，表单底部不再出现保存按钮

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
