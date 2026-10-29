## MODIFIED Requirements

### Requirement: Business decision packet view
系统 SHALL 在现有工作台为管理员只读展示包标识、修订号、处理对象、结论、推荐方案、理由、可选备选方案、场景/实例留痕和关联证据，并支持历史修订。系统 MUST NOT 展示模型推理、提示词原文或连接凭据。

#### Scenario: View generated packet
- **WHEN** 管理员打开已生成的业务决策包
- **THEN** 展示对应修订的模型结果及证据，不含提示词、推理或凭据

#### Scenario: Alternative goals are present
- **WHEN** 包中包含备选方案
- **THEN** 前端正确展示备选方案，不因契约缺失而丢弃

#### Scenario: View old packet
- **WHEN** 历史包缺少新增展示字段
- **THEN** 仍可查看原内容，不要求回填或伪造字段
