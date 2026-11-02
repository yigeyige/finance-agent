## ADDED Requirements

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
