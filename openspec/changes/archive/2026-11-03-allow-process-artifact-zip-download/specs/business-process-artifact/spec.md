## MODIFIED Requirements

### Requirement: Task detail artifact dialog

只读任务详情页的“决策包”步骤在任务存在决策包、关联的有效业务场景配置了非空白流程文件提示词时 SHALL 提供“流程产物”入口；提示词为 null、空串或纯空白，或缺少决策包、有效场景时 MUST 隐藏入口。入口状态 SHALL 根据任务关联的业务场景 ID 查询，并 MUST 在任务权限检查后仅返回是否可用，不暴露提示词正文。入口状态查询 SHALL 异步执行，不阻塞决策包主体展示；查询失败时入口保持隐藏。点击入口后以弹窗展示任务最近的流程文件批次。弹窗 MUST 展示批次、决策包修订、导入状态、生成时间、待补信息和文件列表，并 SHALL 支持重新下载单个文件和包含模型文件的批次 ZIP，无论批次导入状态是 `IMPORT_CANDIDATE` 还是 `NEEDS_REPAIR`。入口 MUST NOT 触发生成新的流程文件或调用模型。无产物时 MUST 显示空状态，加载或下载失败 MUST 显示可理解的错误提示。

#### Scenario: Prompt configured
- **WHEN** 可见任务已有决策包且关联的有效业务场景配置了非空白流程文件提示词
- **THEN** 决策包主体先展示，入口状态查询完成后显示“流程产物”按钮

#### Scenario: Prompt missing
- **WHEN** 任务场景流程文件提示词为 null、空串或纯空白
- **THEN** 决策包主体正常展示，且不显示“流程产物”按钮

#### Scenario: Missing packet or scene
- **WHEN** 任务缺少决策包、场景关联或有效场景
- **THEN** 不显示“流程产物”按钮

#### Scenario: Entry state unavailable
- **WHEN** 入口状态查询失败
- **THEN** 决策包主体正常展示且入口保持隐藏

#### Scenario: Unauthorized task
- **WHEN** 当前身份无权查看任务并查询入口状态
- **THEN** 系统拒绝查询且不泄露业务场景配置

#### Scenario: Open artifact dialog
- **WHEN** 用户在任务详情决策包步骤点击“流程产物”
- **THEN** 系统加载并展示最近批次列表

#### Scenario: Re-download a file
- **WHEN** 用户点击某个批次中的文件下载
- **THEN** 浏览器下载该文件

#### Scenario: Download import candidate ZIP
- **WHEN** 批次状态为 `IMPORT_CANDIDATE` 且批次包含模型文件
- **THEN** 浏览器下载该批次包含模型文件的 ZIP

#### Scenario: Download repair-needed ZIP
- **WHEN** 批次状态为 `NEEDS_REPAIR` 且批次包含模型文件
- **THEN** 浏览器下载该批次包含模型文件的 ZIP，页面仍显示“结构待修复”和待补信息

#### Scenario: Reject ZIP without model
- **WHEN** 批次不包含模型文件，或请求选择的文件类型不在允许范围内或未包含模型文件
- **THEN** 系统拒绝打包并返回中文错误

#### Scenario: No artifacts
- **WHEN** 任务没有流程文件批次
- **THEN** 弹窗显示“暂无流程产物”

#### Scenario: Old packet revision
- **WHEN** 批次绑定的决策包修订不是当前修订
- **THEN** 弹窗展示该修订号，提示可能基于旧决策包
