# Tasks

## 1. 数据库迁移

- [x] 1.1 新增 `V18__guidance_draft.sql` 建 `task.guidance_draft` 表；验证 Flyway 迁移通过

## 2. 后端持久化

- [x] 2.1 新增 `GuidanceDraftEntity` 与 `GuidanceDraftMapper`；验证编译通过
- [x] 2.2 新增 `GuidanceDraftRepository`；验证编译通过
- [x] 2.3 在 `BusinessModelInteractionService.guideBusinessHandling` 成功后调用 `save` 持久化草稿全文；验证编译通过

## 3. 后端详情返回

- [x] 3.1 `TaskDetailService.find` 组装最新草稿的 `conclusion` 与 `evidence` 到 `Detail`；验证编译通过

## 4. 前端详情展示

- [x] 4.1 `api/generated/index.ts` 扩展 `TaskDetail` 类型与 `parseTaskDetail`；验证 typecheck 通过
- [x] 4.2 `TaskDetail.vue` 新增“检查结论”与“关联证据表”展示区；验证通过

## 5. 端到端验证

- [x] 5.1 用真实人员 ZENGLINGRUI 执行模型分析后，详情接口正确返回 conclusion 与 evidence（sys_register_user 1 行、t_audit_configuration 15 行）

## Workflow follow-up

- 实现并验证通过后，归档 change 并同步 specs。
