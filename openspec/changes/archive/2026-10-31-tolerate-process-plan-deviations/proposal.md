## Why

“审批流程生成-隆地”场景在模型已返回初稿时，因 `processPlan` 任一字段不符合严格结构校验而丢弃整份草稿，前端只看到 `503 MODEL_UNAVAILABLE`；用户无法检查模型结果，也无法通过对话修订。同时，模型查询角色代码时未使用值集表，导致 ZYMD 在内的角色证据为空。

## What Changes

- 初稿生成阶段对 `processPlan` 采用“规范化 + 降级 + 警告”策略：常见偏差自动修正；无法修正时置空 `processPlan` 并返回其余草稿，不再整单失败。
- 业务处理初稿响应补回 `processPlan`，并新增 `warnings`，前端展示本次规范化/降级/未核实项，供用户检查后继续对话修订。
- 移除“审批流程生成-隆地”场景对 `processPlan == null` 的硬拦截，改为警告。
- 模型契约错误记录完整 cause 链，并返回独立错误码 `MODEL_OUTPUT_INVALID` 与安全的具体原因；模型/网络故障保持 `MODEL_UNAVAILABLE`。
- EMS 候选表增加值集表（`basedata.sys_value_set` / `basedata.sys_value_set_item`），提示词明确 `approval_config_role` 查询方式；值集查不到时使用用户提供的角色代码并标记未核实。
- 更新 `GUIDE_HANDLING` 与“审批流程生成-隆地”场景提示词，补充 JSON 结构、节点/条件约束、值集查询和输出前自检。
- 流程文件生成阶段继续保持严格校验；非法或缺失的 `processPlan` 不得生成 BPMN/导入产物。

**Non-goals**
- 不自动重试模型（self-repair）——留待后续独立切片。
- 不放宽顶层初稿字段契约（`reply`/`draft`/`requestedAttachments` 与草稿必填字段）。
- 不放宽流程文件生成和 Java 转换器的严格校验。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-handling-guidance`: 初稿生成从“严格失败”调整为“规范化后返回 + 警告”，响应返回 `processPlan` 与 `warnings`，并区分契约错误与模型不可用。
- `ems-database-connection-analysis`: 受控模型数据辅助的候选表目录纳入值集表，并规定角色代码查不到时的用户输入回退与未核实标记。
- `business-process-artifact`: 明确严格校验边界仍在文件生成阶段，非法/缺失流程结构必须被拒绝且保留旧批次。

## Impact

- 后端：`ProcessArtifactPlan` 增加宽容规范化入口；`BusinessHandlingDraft`、`BusinessGuidanceSuggestion`、`BusinessModelResult`、`DeepSeekBusinessModelClient`、`BusinessModelInteractionService`、`BusinessModelResponse`、`BusinessModelExceptionHandler`、`EmsDatabaseAssistanceService`。
- 契约：业务处理初稿响应新增 `processPlan` 与 `warnings`；新增 `MODEL_OUTPUT_INVALID` 错误码。
- 前端：`BusinessGuidanceResponse` 类型、解析器与 `WorkbenchHome` 结果面板展示警告；`processPlan` 复用既有可选解析。
- 数据：新增 Flyway 迁移更新 `GUIDE_HANDLING` 与场景提示词；不修改业务数据。
- 测试：规范化器单元测试、初稿解析与响应测试、EMS 候选表过滤测试；后端测试与前端 `typecheck`/`build`。
