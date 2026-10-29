## MODIFIED Requirements

### Requirement: Business decision packet generation
系统 SHALL 固定服务端任务信息、已有累积证据与本次提示，调用模型分析后保存业务决策包。包 SHALL 保存本次模型结果（处理对象、结论、推荐方案、理由及可选备选方案）、场景/实例留痕与证据，按 schema、table、purpose 去重保留最新，沿用现有修订和摘要机制，旧版只读保留。系统 MUST NOT 在模型失败时回退为直接复制初稿的正式包。

#### Scenario: Generate packet with deduplicated evidence
- **WHEN** 管理员生成业务决策包且任务已有累积证据
- **THEN** 系统调用模型分析后生成决策包，其中证据按 schema、table、purpose 去重保留最新，条数不超过 50

#### Scenario: Content unchanged skips new revision
- **WHEN** 本次保存内容摘要与最新修订相同
- **THEN** 复用旧修订；内容变化时追加新修订

#### Scenario: No draft rejects generation
- **WHEN** 任务尚无累积证据
- **THEN** 系统拒绝生成决策包并提示先完成证据采集

#### Scenario: Generate packet using model result
- **WHEN** 任务已有证据且决策包模型分析成功
- **THEN** 保存固定输入与本次分析结果，生成即完成，不进入复核发布流程

#### Scenario: Supplement evidence and generate again
- **WHEN** 已生成包后补充证据并再次成功生成
- **THEN** 使用新一次固定输入保存结果，旧修订内容不变

#### Scenario: Concurrent saves
- **WHEN** 同任务并发保存分析结果
- **THEN** 保存过程保证修订号唯一，不覆盖旧修订
