# business-process-artifact Specification

## Purpose
依据用户多轮修订后的审批流程调查结论与证据，固定当次决策包，再按该包生成流程模型和适用配置文件。用户可下载单文件或选择配置构建 ZIP，在 EMS 中手动导入和补全；系统保留最近两批来源与缺口信息。
## Requirements
### Requirement: Scene investigation conclusion handoff

系统 SHALL 在「审批流程生成-隆地」场景展示一份可通过现有多轮反馈修订的流程调查结论，区分已知流程事实、关联证据与待补信息。用户点击生成决策包时，系统 SHALL 固定当时的调查结论和关联证据到该决策包，保留原有文字方案字段；生成文件时 MUST 只依据该决策包固定的内容和场景生成文件提示词，不重新读取可能已变化的调查草稿。系统 MUST NOT 将场景样例当作目标流程事实。

#### Scenario: Generate packet from current investigation

- **WHEN** 用户在多轮调查后点击生成决策包
- **THEN** 决策包记录当时的流程调查结论和关联证据，列出仍待补的信息

#### Scenario: Later investigation does not change packet

- **WHEN** 决策包已生成后用户继续修订调查结论
- **THEN** 旧决策包的固定内容保持不变；旧批次文件继续按其来源决策包修订下载，新文件生成必须由页面当前决策包触发

### Requirement: Scene process artifact generation

系统 SHALL 仅在「审批流程生成-隆地」任务已有包含固定调查结论的决策包、且场景已配置生成文件提示词时，允许用户独立触发文件生成。模型 SHALL 依据该决策包的推荐方案和固定调查结论生成小型中间表示，Java 转换器 SHALL 生成流程模型文件，并只在适用时生成节点参数、审批规则和部门审批流文件。首版支持五类节点、顺序流转及排他分支；未支持的形态 SHALL 标记遗漏原因，允许生成部分流程。生成与转换失败 MUST 保留原有批次。

#### Scenario: Generate applicable files

- **WHEN** 用户点击生成文件且决策包包含支持范围内的流程内容
- **THEN** 系统生成流程模型及适用的配置文件，保存来源决策包修订、中间表示和待补项

#### Scenario: Unsupported process component

- **WHEN** 调查结论包含会签、并行或其他未支持的流程形态
- **THEN** 系统标记未生成部分及原因，不把它默默转换成受支持节点，并交付可生成的部分

#### Scenario: Missing value and import boundary

- **WHEN** 适用文件的部分业务字段未知
- **THEN** 系统允许使用空值并列出待补项；若产物未达到 EMS 最低导入要求，则标为待修复文件，不宣称可直接导入

#### Scenario: Reject without packet or prompt

- **WHEN** 任务没有包含固定调查结论的决策包，或本场景没有生成文件提示词
- **THEN** 系统拒绝生成并给出中文说明

### Requirement: Process artifact batches and download

系统 SHALL 按任务保留最新及上一次两批文件，每批关联来源决策包 ID 和修订号。新批次完整保存后 SHALL 清理更早批次；失败时 MUST 保持旧批次不变。系统 SHALL 提供文件列表与单文件下载，并允许用户选择适用的配置文件构建 ZIP；ZIP MUST 包含流程模型文件。系统 MUST 在 ZIP 下载处说明现有 EMS 导入入口会自动部署并设置运行版本。

#### Scenario: New packet leaves old files available

- **WHEN** 用户生成新决策包但尚未重新生成文件
- **THEN** 已有文件继续可下载，并标明基于旧决策包

#### Scenario: Keep only two batches

- **WHEN** 用户成功生成第三批文件
- **THEN** 最新和上一次批次可查询、下载，更早批次被清理

#### Scenario: Download selected ZIP

- **WHEN** 用户选择部分已生成配置并下载导入 ZIP
- **THEN** ZIP 包含流程模型及所选配置，页面提示导入会自动部署并设置运行版本

#### Scenario: Failed generation preserves batches

- **WHEN** 模型输出非法、转换失败或产物写入失败
- **THEN** 不产生新批次，原有两批文件及其下载保持不变

### Requirement: Bounded model context

系统 SHALL 在每次模型调用前检查输入规模，MUST 不超过 1M 且不超过实际模型可用上下文；超限时 SHALL 用中文提示缩减材料，不自动拆分并继续调用。

#### Scenario: Context exceeds available limit

- **WHEN** 本次调查、决策包或文件生成的模型输入超过适用上限
- **THEN** 本次调用被拒绝，已有调查结论、决策包和文件批次保持不变

### Requirement: Strict process plan validation at artifact generation

业务处理初稿生成阶段允许对 processPlan 做结构规范化和降级，但流程文件生成阶段 MUST 继续严格校验该结构。系统 SHALL 在生成流程文件前重新校验决策包内固定的 processPlan；缺失、非法或无法满足转换器约束时 MUST 拒绝生成，返回中文说明并保留原有文件批次，MUST NOT 直接使用初稿阶段的宽松结果生成 BPMN 或导入产物。

#### Scenario: Invalid plan is rejected
- **WHEN** 决策包内的 processPlan 含非法节点 ID、未知节点类型、无效流转或非法条件
- **THEN** 系统拒绝生成文件并返回可理解的中文错误，不产生新批次

#### Scenario: Missing plan is rejected
- **WHEN** 决策包内的 processPlan 为空
- **THEN** 系统拒绝生成文件并提示用户返回模型协作补充流程结论

#### Scenario: Existing batches are preserved
- **WHEN** 文件生成因严格校验失败
- **THEN** 原有两批文件及其下载保持不变

#### Scenario: Valid plan proceeds
- **WHEN** 决策包内的 processPlan 通过严格校验且场景已配置生成提示词
- **THEN** 系统按既有流程生成文件并保存批次

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

