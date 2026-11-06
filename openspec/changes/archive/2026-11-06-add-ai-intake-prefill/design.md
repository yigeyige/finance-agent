## Context

业务处理第一步是纯前端表单，现有模型能力全部挂在任务上（`BusinessGuidanceContext` 强制 `taskId`）。提示词存在 `governance.prompt_template`，没有资源文件；模型端口有 4 个实现（DeepSeek / Stub / DatabaseConfigured / Capturing），漏改任何一个都会在真实模式静默降级。前端已加载业务场景与启用中的数据库实例列表。

## Goals / Non-Goals

**Goals:** 不创建任务即可把自由文本解析为标题、说明、可选场景与实例；结果可预览、可丢弃；匹配失败不误导用户；token 用量可统计。

**Non-Goals:** 不自动创建任务、不调用 EMS 数据库取数、不解析附件、不做模糊匹配、不做服务端限流、不修改既有模型分析流程。

## Decisions

- **代码归属**：service 与 controller 放独立模块 `businessintake`，只依赖 harness 端口、场景与数据库配置服务，避免 `harness/application` 反向依赖 `emsdbconfig/application` 形成循环。
- **隔离既有链路**：只在模型端口新增抽象方法，DeepSeek 使用独立的 `invokeIntake`，不改动 `invokeInternal`、guidance/plan/evidence/决策包端点及其 600 秒超时。
- **候选脱敏**：传给模型的只有候选名称列表，不传内部 id，减少 token 与信息暴露；匹配在服务端用本地列表完成。
- **接口**：`POST /api/v1/business-intake/draft`，仅 `PLATFORM_USER`；请求 `{text}`，响应 `{title, description, sceneSkillId?, sceneSkillName?, databaseConfigId?, databaseName?}`；不接收候选列表，由后端自行加载，避免前端伪造。
- **模型端口**：新增抽象方法 `extractBusinessIntake(BusinessIntakeContext)`，强制四个实现落地；DeepSeek 使用独立 `INTAKE_TIMEOUT=60s`，其余调用保持 600s。
- **提示词**：新增模板 `EXTRACT_BUSINESS_INTAKE`，由 `V57` 插入并 `ON CONFLICT DO UPDATE`；模板只描述规则，候选列表放在输入 JSON 的 `sceneSkills` / `databaseInstances` 中。
- **候选范围**：业务场景取现有全部列表；数据库实例只取启用项（与表单下拉一致）。全部传给模型，不截断；若触发 `ModelInputBudget`，返回明确错误而不是静默丢候选。
- **匹配**：`trim` + 大小写不敏感精确匹配；0 个命中或 2 个以上同名候选都视为未匹配并留空；不使用包含匹配。
- **输出约束**：标题截断到 300 字、说明截断到 8000 字；模型只返回部分字段时按“能填多少填多少”处理，缺失字段不回填。
- **前端交互**：弹窗内只读预览；「应用到表单」只用非空字段覆盖，空字段保留原值；未匹配场景/实例不展示模型建议名称；解析期间禁用提交并显示 loading。
- **错误语义**：入参非法 400；模型失败 502；超过 60 秒 504；错误提示中文且不暴露内部细节。

## Risks / Trade-offs

- 每次预填都消耗 token，当前仅靠前端 loading 防重复；滥用需后续补限流。
- 候选全量传入，条目极多时可能触发上下文门禁，用户会看到“请缩减材料后重试”。
- 60 秒比现有模型调用严格，慢模型会失败；可后续按需调整。
- 预填调用没有 `taskId`，用量记录 `task_id` 为空，会计入个人/全平台统计。
- 提示词在数据库，部署时必须执行 `V57`，否则接口返回模板缺失错误。
- STUB 模式只在文本中直接包含候选名称时才返回匹配项，用于本地联调，不代表真实语义理解。
