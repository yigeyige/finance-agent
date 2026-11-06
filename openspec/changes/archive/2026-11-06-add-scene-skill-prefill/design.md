## Context

场景页当前是整宽表格 + 1000px 弹窗，表单只有 4 个字段；`BusinessSceneSkill` 限制：名称 100、调查提示词 8000、决策包 2000、流程文件 4000。后端已有按实例读取表清单的受控方法（`EmsDatabaseQueryExecutor.listAllowedTables`），但现有 HTTP 入口仅管理员可用。刚上线的 intake 预填使用独立模型方法与提示词，场景预填必须同样独立，不能修改既有接口。

## Goals / Non-Goals

**Goals:** 提供按目标生成场景提示词的后端能力；表名筛选确定、可解释；权限与场景维护权限一致；前端表单与弹窗交互明确可验收。

**Non-Goals:** 不保存数据库实例到场景、不读取字段名/注释、不自动创建或修改场景、不改 intake 与任务编排、不做多轮对话式修改。

## Decisions

- **接口与权限**：新增 `POST /api/v1/business-scene-skills/prefill`；带 `skillId` 时按现有 `requireMaintainer` 校验（管理员或创建人），不带时要求 `PLATFORM_USER`（新建场景）。
- **生成目标**：`target` 取 `INVESTIGATION` / `DECISION_PACKET` / `PROCESS_ARTIFACT`。调查目标返回场景名称 + 调查提示词；另两个只返回内容。决策包必须已有调查提示词、流程文件必须已有决策包提示词，缺失时直接 400，不调用模型。
- **上下文**：调查 = 文本 + 当前名称 + 筛选后的表名；决策包 = 文本 + 当前名称 + 调查提示词；流程文件 = 文本 + 当前名称 + 决策包提示词。决策包与流程文件不传表名。
- **表名筛选**：从实例的允许表中取 `schema.table`；若文本中出现已知 schema（含 `schema.table` 写法）则先按这些 schema 过滤；再排除表名中作为独立片段的 `log/logs/tmp/temp/temporary/bak/backup/his/history/mq/msg/message(s)/queue(s)/topic/tx/trans/transaction(s)`，以及带 6/8 位日期或 `YYYY_MM_DD` 后缀的表；剩余表名全部传给模型，不设数量上限。筛选后为空时仍生成，并返回 warning。
- **安全与规模**：实例必须存在且启用（`resolveEnabled`）；只传表名不传字段；输入文本 10–4000 字；模型输入超门禁时返回明确错误。
- **模型调用**：`BusinessModelClient` 新增抽象方法 `extractSceneSkillPrefill`，DeepSeek 使用独立 `invokeScenePrefill` 与 60 秒超时；Stub 返回确定性内容；Capturing 以 `taskId=null` 记录 token。
- **输出约束**：名称截断到 100、调查 8000、决策包 2000、流程文件 4000，超长加省略号；输出超限不报错，避免用户丢失已生成内容。
- **前端交互**：列表与表单在内容区切换；表单占满内容区（不遮侧栏）；标题右侧「AI 预填」在「返回」左侧；返回或路由离开时若有未保存修改需确认。弹窗内一个文本框、一个实例下拉与三个生成按钮，每次生成展示对应字段只读预览，应用时调查按钮回填名称+调查提示词，另两个只回填自身字段。
- **复用**：弹窗壳复用 `v-workbench-dialog` 与 `workbench-dialog` 样式；实例下拉复用 `listEnabledDatabaseInstances`。

## Risks / Trade-offs

- 表名全量传给模型仍可能触发上下文门禁；届时返回明确错误并保留表单输入。
- 普通场景维护者可读取启用实例的**表名**（已确认接受），但不暴露字段、连接信息或数据。
- 全页表单改造涉及附件上传与保存流程，需要回归编辑态附件。
- 生成存在前后依赖，用户跳过前置步骤时会被阻断，这是刻意设计。
- 提示词在数据库，部署必须执行 `V58`，否则接口返回模板缺失错误。
