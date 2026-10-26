# Tasks

## 1. 数据库迁移与知识导入

- [ ] 1.1 新建 `V21__ems_table_knowledge.sql`：`governance.ems_table_knowledge`（config_id/schema/table 唯一键、table_comment、data_status、knowledge JSONB、审计字段）与 `task.business_scene_skill` 增加可空 `database_config_id` 列——验证：执行 `./mvnw flyway:migrate` 后新表与新列存在，重复执行幂等
- [ ] 1.2 先在「EMS 数据库配置」建「隆地-报账claim-UAT」实例；编写/执行导入：将 3 个 JSON 写入 `ems_table_knowledge`（按 name 定位 config_id，无数据表 data_status=EMPTY）——验证：按该 config_id 查询返回 205 条，「无数据」标记条数正确（account 14/activiti_ems 34/basedata 28）

## 2. 后端：表知识领域与查询

- [ ] 2.1 新增 `EmsTableKnowledge` 领域对象 + Repository + MyBatis 实体/Mapper——验证：单测覆盖「按 config_id+schema+table 读取」与「按 config_id 读取」
- [ ] 2.2 新增表知识查询服务，按 configId 组装「schema.table + promptHint」紧凑摘要——验证：单测覆盖无数据表被跳过、摘要不包含敏感字段

## 3. 后端：Skill 关联数据库

- [ ] 3.1 `BusinessSceneSkill` 增加可空 `databaseConfigId`，增改时保存——验证：单测覆盖保存、替换与置空
- [ ] 3.2 Skill 接口请求/响应增加 `databaseConfigId`，同步契约（contracts/openapi + 前端 generated 类型）——验证：契约校验命令通过
- [ ] 3.3 `BusinessSceneSkillContext` 增加 `databaseConfigId`，`BusinessModelInteractionService.findSceneSkill` 返回带库标识的上下文——验证：单测覆盖上下文携带关联库标识

## 4. 后端：模型辅助查询改造

- [ ] 4.1 `EmsDatabaseAssistanceService.collect` 改为按任务关联 Skill 的 `databaseConfigId` 取连接（不再 `requireEnabled()`），无关联库时跳过 EMS 查询——验证：单测覆盖「有库查库」与「无库降级」
- [ ] 4.2 在 `selectRelevantTables` 与 `planEmsDatabaseQuery` 两个阶段的输入中注入对应库的表知识摘要——验证：集成/单测覆盖注入内容来自 `ems_table_knowledge` 且不返回凭据

## 5. 后端：业务处理 Skill 必选 + 历史兼容

- [ ] 5.1 `CreateTaskDraftCommand`/`CreateTaskRequest` 将 `businessSceneSkillId` 设为必填，`TaskIntakeApplicationService` 创建任务时校验——验证：单测覆盖「未选 Skill 拒绝创建并返回中文提示」
- [ ] 5.2 `guideBusinessHandling` 对无 Skill 的历史任务降级为纯文本（不查库、不报错）——验证：单测覆盖历史无 Skill 任务可正常生成纯文本初稿

## 6. 前端

- [ ] 6.1 Skill 管理页增加「数据库」选择并随增改提交 `databaseConfigId`——验证：`npm run build` 通过，页面可选择并保存
- [ ] 6.2 `WorkbenchHome.vue` 第一步 Skill 改为必选，未选时禁用「开始模型分析」并提示——验证：构建通过，未选 Skill 无法发起

## 7. 集成验证

- [ ] 7.1 后端 `./mvnw -q compile` 与相关模块测试通过；前端构建通过——验证：命令输出无失败
- [ ] 7.2 端到端冒烟：创建关联了「隆地」数据库的 Skill → 工作台选该 Skill 发起模型分析 → 模型查询使用该库并返回带表溯源证据——验证：本地 `local` 启动后走通流程

## Workflow follow-up

- 实现完成并验证后，按项目流程归档 change 并同步 delta spec 到主规格。
- 归档后创建 commit（由用户执行或按显式授权）。
