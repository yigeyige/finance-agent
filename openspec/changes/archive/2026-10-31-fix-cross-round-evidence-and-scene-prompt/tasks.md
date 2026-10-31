## 1. Backend evidence semantics

- [x] 1.1 BusinessEvidenceAccum 增加 snapshot 标记并更新 Repository/Entity/Mapper
- [x] 1.2 BusinessModelInteractionService 加载上一轮证据、合并并保存快照
- [x] 1.3 EmsDatabaseAssistanceService 查询规划使用上一轮证据

## 2. Decision packet

- [x] 2.1 决策包使用最新快照；历史增量合并兼容
- [x] 2.2 决策包空证据明确拒绝
- [x] 2.3 第二轮附件分析保留必读 BPMN 投影并去重

## 3. Prompt SQL

- [x] 3.1 新增 Flyway V33：snapshot 字段 + 审批流程场景提示词

## 4. Verification

- [x] 4.1 更新/新增跨轮证据、决策包、附件保留测试
- [x] 4.2 后端测试通过并应用 V33
