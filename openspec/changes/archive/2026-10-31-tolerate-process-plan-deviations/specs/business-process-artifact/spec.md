## ADDED Requirements

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
