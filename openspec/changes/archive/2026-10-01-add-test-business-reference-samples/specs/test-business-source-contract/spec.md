## ADDED Requirements

### Requirement: P1 multi-type reference sample baseline
系统 SHALL 为 P1 测试业务源维护版本化的多类型参考样本与黄金事实，覆盖人员、岗位、角色、流程节点、组织/权限范围以及当前、未来、未知和无匹配情形。该基线 MUST 使用固定虚构或已脱敏样本，并且 MUST 与真实业务源和生产数据隔离。

#### Scenario: P1 reference sample is selected
- **WHEN** 测试执行选择已登记的多类型参考样本
- **THEN** 系统能够按样本版本核对期望事实、适用范围和非确认状态，且不读取外部来源
