# Tasks

## 1. 数据库迁移

- [x] 1.1 新增 `V21__business_scene_skill_prompts.sql`：重命名 `description`→`investigation_prompt` 并扩为 `varchar(2000)`，新增 `decision_packet_prompt varchar(2000) NOT NULL DEFAULT ''`，删除 `handling_type`、`premise_type`；验证迁移 SQL 语法可被 Flyway 解析
- [x] 1.2 确认 V13/V15 无其他对 `handling_type`/`premise_type` 的引用后，触发 Flyway 迁移并按需 repair；验证 `task.business_scene_skill` 结构为 `name/investigation_prompt/decision_packet_prompt` 且旧 description 数据已保留

## 2. 后端领域与持久层

- [x] 2.1 调整 `BusinessSceneSkill` 领域对象：去掉 `handlingType/premiseType`，`description`→`investigationPrompt`，新增 `decisionPacketPrompt`，校验长度 2000；验证后端编译通过
- [x] 2.2 同步 `BusinessSceneSkillEntity` 与 `MybatisBusinessSceneSkillRepository` 的字段映射；验证仓储映射与领域对象一致且编译通过
- [x] 2.3 更新 `BusinessSceneSkillService` 的 create/update 签名与调用；验证编译通过

## 3. 后端 Web 契约与模型上下文

- [x] 3.1 调整 `BusinessSceneSkillRequest`/`BusinessSceneSkillResponse`/`BusinessSceneSkillController` 为 `name/investigationPrompt/decisionPacketPrompt`；验证 `GET/POST/PUT /api/v1/business-scene-skills` 契约字段正确
- [x] 3.2 调整 `BusinessSceneSkillContext` 与 `BusinessModelInteractionService.findSceneSkill`，模型上下文改为 `name/investigationPrompt/decisionPacketPrompt`；验证编译通过
- [x] 3.3 同步 `DeepSeekBusinessModelClient` 与 `StubBusinessModelClient` 的 `sceneSkill` 注入字段；验证编译通过

## 4. 前端契约与页面

- [x] 4.1 更新 `frontend/src/api/generated/index.ts` 的 `BusinessSceneSkill`/`BusinessSceneSkillPayload`、字段白名单与 `parseBusinessSceneSkill`；验证前端类型检查通过
- [x] 4.2 更新 `BusinessSkillCatalog.vue` 列表列、表单、保存逻辑，去掉处理方式/业务场景，加入调查提示词与决策包提示词；验证页面可正常新增/编辑
- [x] 4.3 调宽新增/编辑弹窗（`width` 560px→720px）并将两个提示词 textarea `:rows` 调为 8；验证弹窗布局正常
- [x] 4.4 更新 `WorkbenchHome.vue` 去掉对 `skill.premiseType` 的展示依赖，仅展示 `skill.name`；验证页面加载与选择正常

## 5. 验证与收尾

- [x] 5.1 运行后端编译与相关测试、前端类型检查与构建，记录结果
- [x] 5.2 重启本地后端，验证 `/api/v1/business-scene-skills` 读写、Skill 页面与新建业务处理页面均无契约错误

## Workflow follow-up

- 同步 delta specs 到 `openspec/specs/` 并归档 change。
- 更新任务目标勾选与执行记录，并按项目流程提交。
