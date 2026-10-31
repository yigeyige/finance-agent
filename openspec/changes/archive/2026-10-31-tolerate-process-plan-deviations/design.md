## Context

业务处理初稿由 `DeepSeekBusinessModelClient` 调用真实模型后，经 `BusinessGuidanceSuggestion.fromJson` → `BusinessHandlingDraft.fromJson` → `ProcessArtifactPlan.fromJson` 严格解析。当前任一 `processPlan` 偏差都会使整份初稿被丢弃，`BusinessModelExceptionHandler` 再统一映射为 `503 MODEL_UNAVAILABLE`，用户看不到具体原因。EMS 辅助查询的候选表由 `EmsDatabaseAssistanceService.coarseFilter` 按关键词过滤，值集表未命中，模型只能查询 `basedata.sys_role`。

约束：
- 不记录模型原文、提示词、密钥；错误信息只能包含安全字段名和校验原因。
- 流程文件生成阶段的严格校验不能放宽，避免非法结构进入 BPMN。
- 前端已有可选 `processPlan` 解析，但后端响应未返回该字段。
- 提示词模板存于数据库，通过 Flyway 迁移更新。

## Goals / Non-Goals

**Goals:**
- 初稿生成阶段对 `processPlan` 偏差宽容处理，返回可检查草稿和警告。
- 响应返回 `processPlan` 和 `warnings`，前端展示。
- 契约错误可定位、可区分：日志记录 cause 链，API 返回 `MODEL_OUTPUT_INVALID`。
- 值集表进入 EMS 候选目录，角色代码优先查 `approval_config_role`，查不到时使用用户输入并标记未核实。
- 提示词补充结构约束、值集查询和自检清单。

**Non-Goals:**
- 不实现模型输出 self-repair 自动重试。
- 不放宽顶层草稿字段契约。
- 不放宽流程文件生成/Java 转换器校验。
- 不改变决策包和文件批次的持久化结构。

## Decisions

### 1. 新建 `ProcessArtifactPlanNormalizer`，严格解析保持不变

在 `processartifact/domain` 新增 `ProcessArtifactPlanNormalizer`，输入模型 `processPlan` JSON，输出 `NormalizationResult(ProcessArtifactPlan plan, List<String> warnings)`。

- `ProcessArtifactPlan.fromJson` 继续用于流程文件生成，保持严格。
- 规范化规则：
  - 顶层缺失/非对象：返回空计划 + 警告。
  - `name` 缺失/非法：回退“未命名流程”并警告。
  - `nodes`：丢弃显式 `start`/`end`；`kind` 别名归一（`gateway`→`exclusive-gateway`、`approval`→`approve`）；`assignee` 缺失/`null`→`""`；`id` 非法字符替换、数字开头加前缀、去重；未知 `kind` 丢弃节点并警告。
  - `flows`：`source`/`target` 为 `start`/`end` 保留；引用未知节点或自环丢弃；`condition` 非对象或运算符/值不合法降级为 `null`。
  - `nodeParameters` / `departmentApprovals`：引用未知节点的条目丢弃；`approvalRules` 保持对象数组。
  - `missingItems` / `unsupported`：过滤空白、截断超长文本。
  - 最后用严格构造器复核；仍失败则返回空计划 + 警告。
- 选择独立规范化器而不是修改严格解析器，避免文件生成阶段被宽松规则污染。

### 2. 警告通过 `BusinessModelResult` 贯通

- `BusinessModelResult` 增加 `List<String> warnings`，保留两参构造器兼容既有调用。
- `DeepSeekBusinessModelClient.invoke` 的解析器改为返回 `ParsedModelOutput<T>(T value, List<String> warnings)`；其他用例包装为空警告。
- `BusinessGuidanceSuggestion.fromJson` 增加带警告的解析入口，`BusinessHandlingDraft.fromJson` 在 `processPlan` 解析失败时使用规范化器，不再抛异常。
- `BusinessModelInteractionService` 移除 `processPlan == null` 硬拦截，改为 `result.withWarning(...)`；草稿照常保存，供用户检查后修订。

### 3. API 响应补字段，前端展示警告

- `BusinessModelResponse.Guidance` 增加 `List<String> warnings`；`BusinessHandlingDraftResponse` 增加 `ProcessArtifactPlan processPlan`（可为空）。
- 前端 `BusinessGuidanceResponse` 增加 `warnings: string[]`，解析器容错为空数组；`WorkbenchHome` 在模型协作面板顶部用警告样式展示。
- 不新增接口；沿用现有 guidance 端点。

### 4. 契约错误与模型不可用分离

- 新增 `BusinessModelOutputException extends BusinessModelException`，用于模型输出不符合契约。
- `invoke` 对 `BusinessModelException` 记录 `describe(exception)`（含 cause 链，不含模型原文）。
- `BusinessModelExceptionHandler` 先处理 `BusinessModelOutputException`，返回 `422 MODEL_OUTPUT_INVALID` + 安全原因；其余 `BusinessModelException` 保持 `503 MODEL_UNAVAILABLE`。
- 规范化降级不算契约错误，返回 200 + `warnings`。

### 5. EMS 值集表纳入与角色回退

- `EmsDatabaseAssistanceService.FILTER_KEYWORDS` 增加 `value_set`、`dict`、`value`，使 `basedata.sys_value_set`、`basedata.sys_value_set_item` 进入候选表。
- 提示词明确角色代码查询：`basedata.sys_value_set_item`，`VS_CODE='approval_config_role'`，取 `VSI_KEY`；显示名在 `sys_value_set_item_lang.VSI_VALUE`。
- 查不到时使用用户提供的角色代码（如 ZYMD）作为条件值，并写入 `missingItems` 标明未核实；不臆造其他角色。

### 6. 提示词通过 Flyway V31 更新

- 新增 `V31__tolerant_guidance_process_plan.sql`。
- 更新 `GUIDE_HANDLING`：补充完整 JSON 结构、节点/条件约束、`assignee` 规则、`missingItems` 规则、值集查询与自检清单。
- 更新场景 `a6a050ae-...` 的 `investigation_prompt`：明确 start/end 隐含、ID/kind/条件约束、ZYMD 值集查询与回退。
- 迁移使用幂等 `UPDATE ... WHERE`，不修改业务数据。

### 7. 严格边界

- `ProcessArtifactService` / `ProcessArtifactPlan.fromJson` 不变；缺失或非法 `processPlan` 时，文件生成返回明确中文错误，旧批次保留。

## Risks / Trade-offs

- [警告过多导致前端噪音] → 同类规范化合并为一条，最多展示前 10 条。
- [规范化改变模型原意] → 只做结构修复，不改业务值；无法安全修复时置空并提示用户重新调查。
- [空 `processPlan` 仍可保存草稿] → 决策包/文件生成阶段明确拒绝并提示补充；不放宽导入产物。
- [提示词迁移覆盖用户手工修改] → 仅更新指定模板和指定 skill id；迁移内容以约束补充为主。
- [错误码变更影响前端] → `MODEL_OUTPUT_INVALID` 为新增错误码，前端按通用错误展示即可。

## Migration Plan

1. 发布后端与前端代码。
2. Flyway V31 更新提示词模板和场景提示词。
3. 回归：重跑“2号测试流程生成”，确认返回初稿、`processPlan`/`warnings`，文件生成仍严格。
4. 回滚：回退代码与 V31 迁移；V31 只更新模板文本，回滚可重跑 V30 的模板内容或恢复备份。

## Open Questions

- 无。self-repair 重试和 warnings 交互样式留待后续切片。
