## MODIFIED Requirements

### Requirement: Three-class field policy

系统 SHALL 分别维护 read、retain、model 字段集合；retain 和 model 必须是 read 的子集，源返回包含未授权字段时整次调用失败关闭。

#### Scenario: Unauthorized source field fails closed

- **WHEN** 源返回字段不在 read 集合
- **THEN** 投影器拒绝整次响应，不生成留存或模型 payload

#### Scenario: Trusted projections discard unretained fields

- **WHEN** 字段允许读取但不允许留存
- **THEN** 该字段只可用于易失内存校验，不出现在 retained/model 投影
