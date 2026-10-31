## ADDED Requirements

### Requirement: Task detail artifact dialog

只读任务详情页的“决策包”步骤 SHALL 提供“流程产物”入口，点击后以弹窗展示任务最近的流程文件批次。弹窗 MUST 展示批次、决策包修订、导入状态、生成时间、待补信息和文件列表，并 SHALL 支持重新下载单个文件和导入候选批次的 ZIP。无产物时 MUST 显示空状态，加载或下载失败 MUST 显示可理解的错误提示。该入口 MUST NOT 触发生成新的流程文件或调用模型。

#### Scenario: Open artifact dialog
- **WHEN** 用户在任务详情决策包步骤点击“流程产物”
- **THEN** 系统加载并展示最近批次列表

#### Scenario: Re-download a file
- **WHEN** 用户点击某个批次中的文件下载
- **THEN** 浏览器下载该文件

#### Scenario: Download ZIP
- **WHEN** 批次状态为导入候选且用户点击下载 ZIP
- **THEN** 浏览器下载该批次 ZIP

#### Scenario: No artifacts
- **WHEN** 任务没有流程文件批次
- **THEN** 弹窗显示“暂无流程产物”

#### Scenario: Old packet revision
- **WHEN** 批次绑定的决策包修订不是当前修订
- **THEN** 弹窗展示该修订号，提示可能基于旧决策包
