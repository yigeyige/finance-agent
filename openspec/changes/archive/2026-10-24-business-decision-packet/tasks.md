# Tasks

## 1. 数据库迁移

- [ ] 1.1 新增 `V20__business_decision_packet.sql`，在 `task` schema 建 `business_evidence_accum` 与 `business_decision_packet` 两张表（字段与唯一约束见 design.md），验证后端启动时 Flyway 迁移成功、无重复版本冲突

## 2. 后端领域模型与仓储

- [ ] 2.1 新建 `businesspacket` 包，编写 domain 值对象 `BusinessEvidenceAccum`、`BusinessDecisionPacket`（含字段校验），验证 `mvn compile` 通过
- [ ] 2.2 编写 application 仓储接口（`BusinessEvidenceAccumRepository`、`BusinessDecisionPacketRepository`）与 infrastructure MyBatis 实现（entity/mapper/mappings），验证新增仓储单测通过

## 3. 证据累积写入

- [ ] 3.1 在 `BusinessModelInteractionService.guideBusinessHandling` 中 `collect` 完成后，把当轮非空 evidence 追加写入证据工作区（`round_no = max+1`），验证连续多轮处理后工作区按轮追加且不覆盖

## 4. 决策包生成

- [ ] 4.1 实现 `BusinessDecisionPacketService`：读最新草稿（无草稿拒绝）、读工作区全部证据按 `(schema, table, purpose)` 去重保留最新并截断到 50、装配三块 canonical JSON、按 sha256 摘要与上一 revision 比较决定跳过或 revision+1，验证单测覆盖去重/无草稿拒绝/重复跳过/上限截断

## 5. Web 接口

- [ ] 5.1 实现 `BusinessDecisionPacketController` 及 request/response：生成决策包、查看最新/指定修订、查看累积证据，验证接口契约编译通过且响应不含推理过程或连接凭据

## 6. 前端

- [ ] 6.1 在 `services/model-api.ts` 与 `api/generated/index.ts` 增加决策包与累积证据接口，验证前端类型检查通过
- [ ] 6.2 在 `WorkbenchHome.vue` 证据面板改为请求累积证据视图，并新增"生成业务决策包"按钮与决策包查看区，验证前端 `npm run build` 通过

## 7. 集成验证

- [ ] 7.1 后端 `mvn test` 全量通过
- [ ] 7.2 前端 `npm run build` 通过

## Workflow follow-up

- 更新 `tasks/yigeyige99/01.doing/task-index.md` 中本任务完成状态
- 归档 change：openspec-sync-specs 同步 delta spec，再 openspec-archive-change 归档
- 创建 commit（message 格式 `<type>: <task-name>`）
