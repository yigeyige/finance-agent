## Why

业务处理目前允许不选择场景 Skill，并在模型调用时读取可变的当前 Skill 内容，导致提示语义可能由代码默认值或后续编辑悄然改变，无法稳定复现多轮分析。需要先把“业务说明 + 指定且冻结的场景 Skill”确立为每个任务的唯一场景输入基线。

## What Changes

- **BREAKING**：创建业务处理任务时必须提交有效的业务场景 Skill；前端默认选择“通用业务分析”，后端对缺失或无效 Skill 一律拒绝。
- 为业务场景 Skill 引入不可变修订；编辑产生新修订，任务创建时冻结 Skill、修订号、内容摘要和提示模板版本。
- 模型上下文由受控系统模板、用户业务说明和冻结 Skill 组合，业务代码不得内嵌供应商、系统、表字段或具体场景结论。
- 增加默认“通用业务分析” Skill 的幂等 Flyway 初始化迁移设计；其处理方式为 `INVESTIGATE`，后续实施时生成对应 SQL。
- 当前默认修订不得直接删除；替换默认或创建新修订只影响后续任务，既有任务继续引用冻结版本。

## Capabilities

### New Capabilities

- `business-scene-skill-versioning`: 定义场景 Skill 不可变修订、默认 Skill、任务版本冻结、删除保护和提示内容来源。

### Modified Capabilities

- `business-handling-guidance`: 将可选 Skill 改为创建任务时强制选择，并取消“未选择时继续通用向导”的旧行为。
- `business-model-interaction`: 模型调用改为只使用任务冻结的 Skill 修订与版本化受控模板。

## Impact

- 后续实施将影响 Skill 与任务数据模型、Flyway 迁移、任务创建及模型调用 API、Skill 管理服务和业务处理页面。
- 现有未关联 Skill 的历史任务需要显式兼容或数据回填策略；新请求不再接受空 Skill。
- 本 change 不包含模型 attempt、证据分块分析或业务决策包生成，它们由后续依赖 change 承担。
