## 1. Backend normalization

- [x] 1.1 新增 ProcessArtifactPlanNormalizer，实现节点/流转/条件/配置的安全规范化并返回 warnings
- [x] 1.2 在 BusinessHandlingDraft.fromJson 中使用规范化器，保留 ProcessArtifactPlan.fromJson 严格入口
- [x] 1.3 新增规范化器与初稿解析单元测试（显式 start/end、kind 别名、null assignee、非法 ID、无效条件、不可修复降级）

## 2. Backend response and diagnostics

- [x] 2.1 为 BusinessModelResult 增加 warnings，并在模型调用解析链中传递
- [x] 2.2 BusinessModelResponse.Guidance 返回 processPlan 和 warnings
- [x] 2.3 新增 BusinessModelOutputException、记录 cause 链、映射 422 MODEL_OUTPUT_INVALID
- [x] 2.4 移除 BusinessModelInteractionService 对 processPlan 为空的硬拦截，改为警告

## 3. EMS value set and prompts

- [x] 3.1 EmsDatabaseAssistanceService 候选表纳入值集表并补充过滤单元测试
- [x] 3.2 新增 Flyway V31 更新 GUIDE_HANDLING 与审批流程场景提示词（值集查询、ZYMD 回退、结构约束、自检）

## 4. Frontend

- [x] 4.1 BusinessGuidanceResponse 增加 warnings，解析器容错为空数组
- [x] 4.2 WorkbenchHome 展示 warnings，并确认 processPlan 可展示

## 5. Verification

- [x] 5.1 运行后端相关单元测试
- [x] 5.2 运行前端 typecheck 和 build
- [x] 5.3 重跑“2号测试流程生成”，确认初稿/warnings/processPlan 返回，文件生成阶段仍严格校验
