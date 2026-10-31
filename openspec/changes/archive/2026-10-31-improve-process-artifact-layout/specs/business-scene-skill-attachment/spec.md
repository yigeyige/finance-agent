## MODIFIED Requirements

### Requirement: Scene attachment used on demand
系统 SHALL 在调查阶段把场景附件清单（文件名、摘要和必读标记）注入模型第一轮；模型声明需要的附件后，第二轮按需注入至多 2 个附件的完整正文。标记为必读的附件 MUST 由后端自动注入，不依赖模型请求；其中 BPMN XML 必读附件 MUST 以结构投影而非原文注入。无附件或模型不请求正文时一轮结束，不得一股脑全传正文。

#### Scenario: Model requests attachment body
- **WHEN** 模型第一轮根据清单声明需要某些附件正文
- **THEN** 系统仅注入被选中且至多 2 个附件的完整正文，进行第二轮分析

#### Scenario: No attachment requested
- **WHEN** 场景无必读附件且模型不请求正文
- **THEN** 系统一轮返回结果，不注入任何附件正文

#### Scenario: Required BPMN attachment is auto-injected
- **WHEN** 场景存在标记为必读的 BPMN XML 附件
- **THEN** 系统在第一轮自动注入其结构投影（节点、流转、条件、坐标骨架），不注入原始 XML，也不要求模型主动请求

#### Scenario: Required attachment body is unavailable
- **WHEN** 必读附件缺失或无法解析
- **THEN** 系统不阻断调查，注入附件摘要并在结果中记录需要补充核查的说明
