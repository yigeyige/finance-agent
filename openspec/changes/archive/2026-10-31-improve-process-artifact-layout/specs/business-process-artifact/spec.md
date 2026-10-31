## ADDED Requirements

### Requirement: Layered process layout and BPMN reference

流程文件生成阶段 SHALL 使用分层布局生成 EMS BPMN 图形：按从开始节点的最长路径分层，同层节点纵向分布，任务、网关和开始/结束事件使用不同尺寸，连线按源/目标节点边界计算 dockers。系统 MUST 继续使用严格流程结构校验，非法或缺失的 processPlan 不得生成文件。文件生成模型输入 SHALL 包含场景必读 BPMN XML 原文作为格式/结构参考，并 MUST 限制为 100KB；超限时拒绝生成并返回中文错误。样例仅可用于结构、命名、条件写法和布局骨架参考，MUST NOT 复制样例公司、角色、金额或旧流程标识。

#### Scenario: Simple process layout
- **WHEN** processPlan 为顺序流程或单层排他分支
- **THEN** 生成的 BPMN 中节点不重叠、网关文字不被任务框压住、开始节点在最左、结束节点在最右

#### Scenario: Branch layout
- **WHEN** 排他网关存在多个出口
- **THEN** 分支目标节点在下一层上下分开，连线按节点边界连接，条件标签不互相覆盖

#### Scenario: Required BPMN XML is injected
- **WHEN** 场景存在必读 BPMN XML 且文件生成请求有效
- **THEN** 模型输入包含原文参考，提示词明确其只用于格式和结构参考

#### Scenario: BPMN XML exceeds limit
- **WHEN** 必读 BPMN XML 超过 100KB
- **THEN** 系统拒绝生成文件并返回可理解的中文错误，不保存新批次

#### Scenario: Invalid process plan
- **WHEN** processPlan 缺失或未通过严格校验
- **THEN** 系统拒绝生成并保留原有批次
