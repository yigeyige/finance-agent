## Context

现有 businesspacket 已有修订号、摘要和历史读取，本切片复用这些能力接入新模型结果、固定输入组装和任务信息锁定。

## Goals / Non-Goals

**Goals:** 组装固定输入、调用模型并保存包，保留历史修订，成功生成后禁止修改任务信息。
**Non-Goals:** 不建设请求幂等平台、分析谱系、旧包回填、自动发布或复核状态机。

## Decisions

- 生成服务依次：读取任务信息 + 已有证据（沿用现有 deduplicate 按 schema/table/purpose 去重、≤50）+ decisionPacketPrompt 组装固定输入（方法内只读一次）、调用模型、组装 content、保存包。
- content 结构：`packetId/taskId/revision/createdAt` + 模型结果（subjectKey/conclusion/recommendedGoal/alternativeGoals/reason）+ `sceneSkillId/Name` + `databaseConfigId/Name` + `evidence`。不存 title/originalQuestion（展示时查 taskId）。
- 模型调用放在保存事务外。保存时沿用内容摘要判断是否变化；相同内容复用旧修订，变化才追加。复用唯一约束和简单任务级短事务保护修订号，避免重复 revision。
- 锁定校验放在 web 层（TaskIntakeController.updateTask）：注入查询，用 findLatestByTaskId(taskId).isPresent() 判断「已有包」，有包则拒绝修改 originalQuestion、businessSceneSkillId、databaseConfigId；以此避免 task ↔ businesspacket 模块循环依赖。
- 并发上：generate 保存沿用 (taskId, revision) 唯一约束防重复修订；「校验通过后、保存前」的极小并发窗口接受（管理员单人使用），不引入额外锁表。
- title 创建后本就不可改；subjectKey 是模型结果非任务信息，不锁。锁定范围 = updateTask 三个字段。
- 失败不写入新包，也不对原本无包的任务加锁。旧包保持原内容，直接只读查看。

## Risks / Trade-offs

同一输入重新调用模型可能产生不同结果；本批按现有内容摘要决定是否新增修订，不专门治理模型输出波动。

## Migration Plan

优先复用既有表和接口，必要字段调整按现有方式兼容旧包，不做批量历史重写。环境由用户启动与人工验证。

## Open Questions

无。
