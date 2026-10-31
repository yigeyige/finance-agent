## ADDED Requirements

### Requirement: Required BPMN structural projection in investigation

调查阶段 SHALL 识别场景 Skill 的必读 BPMN XML 附件，并在第一轮模型输入中自动注入其结构投影；投影 MUST 包含节点标识、类型、名称、流转、条件和坐标骨架，MUST NOT 包含原始 XML 全文。模型 SHALL 使用投影理解节点类型、条件写法和命名约定，但 MUST NOT 把样例业务值当作目标流程事实。投影解析失败或必读附件缺失时，系统 MUST 继续生成初稿并记录待核查说明。

#### Scenario: Projection is injected automatically
- **WHEN** 任务关联的场景存在必读 BPMN XML 附件
- **THEN** 第一轮模型输入包含结构投影，模型无需主动请求该附件

#### Scenario: Projection excludes raw XML
- **WHEN** 系统注入 BPMN 结构投影
- **THEN** 注入内容不包含原始 XML 全文，只包含节点、流转、条件和坐标骨架

#### Scenario: Projection parsing fails
- **WHEN** BPMN XML 无法解析或缺少必要结构
- **THEN** 系统不阻断初稿生成，注入附件摘要并在 missingItems 或 warnings 中记录需要补充核查
