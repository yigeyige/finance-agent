## MODIFIED Requirements

### Requirement: Deterministic synthetic source adapter

系统 SHALL 通过注册查询模板访问合成财务配置源；相同 seed、模板和参数必须产生相同结果，且不得提供 SQL、任意 URL、写操作或未声明参数入口。

#### Scenario: Registered template returns synthetic records

- **WHEN** 使用合法模板和 page/size 参数查询
- **THEN** 返回不含真实人员、金额或单据数据的稳定合成记录

#### Scenario: Unregistered input is rejected

- **WHEN** 请求包含未知模板、未声明参数、SQL 或写意图
- **THEN** 适配器拒绝请求且不执行任何外部操作
