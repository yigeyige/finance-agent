## Context

现有业务决策包服务读取最新业务处理草稿，将证据按 `(schema, table, purpose)` 去重保留最新后直接固化。该流程没有在用户点击生成时调用模型重新分析历轮证据，也会丢失同一对象随时间变化、不同来源冲突和待核查缺口。

本 change 建立在冻结 Skill 修订、模型 attempt、evidence manifest 和有界分块分析之上。用户已经选择独立的 `businesspacket`，生成即完成，不复用现有 packet 的审核、发布和交接状态机。

## Goals / Non-Goals

**Goals:**

- 点击生成时，对范围内全部有效证据完成一次独立、可审计的模型分析。
- 生成不可变、完成态、版本化的业务决策包，并保留精确输入谱系。
- 相同输入与结果幂等，并发请求不产生修订竞争。
- 只允许 OPERATOR 生成和查看内部证据与业务决策包。

**Non-Goals:**

- 不把 `businesspacket` 接入审核、发布、退回或交接流程。
- 不向 VIEWER 发布业务决策包。
- 不把待分析证据默认为正式结论依据。

## Decisions

### 1. “全部证据”以 manifest 覆盖率定义

生成时固定一个 evidence manifest 快照。所有任务内候选证据必须在 manifest 中出现；只有状态为 `ANALYZED`、引用和 digest 可验证、满足当前任务范围与字段策略的记录属于 eligible。非 eligible 记录保留在完整性清单及排除原因中，不进入正式事实综合。

这样既满足全量可追溯，也不会把失败 attempt 的原始查询结果误当成模型已分析事实。

### 2. 生成使用专用分析 attempt

点击生成创建 `BUSINESSPACKET_ANALYSIS` attempt：构建 manifest、按既定预算分块、校验每个分块摘要，再做最终综合。最终输出 schema 至少包含业务目标、已证实事实、推断、冲突、缺口、风险、建议、前置条件、需人工确认事项和 evidence 引用。

任一分块失败、引用校验失败或综合不完整时，不生成正式包；attempt 失败和中间记录保留用于诊断与重试。

### 3. businesspacket 是不可变完成态快照

成功综合后在同一事务中创建 packet revision，状态直接为 `COMPLETED`。修订固化：任务 ID、revision、生成时间、冻结 Skill 修订、模板版本、manifest ID、eligible/排除 evidence ID、分块分析 ID 与 digest、最终输出、模型非敏感元数据和内容 digest。旧修订不可修改。

不复用现有 packet 状态机，避免引入与本业务入口无关的 scope/runtime bundle 和发布语义。

### 4. 幂等和并发由数据库约束保证

内容 digest 由规范化任务业务说明、冻结 Skill 修订、模板版本、manifest digest、分块摘要 digest 和最终结果生成。以 `(task_id, content_digest)` 唯一约束防止相同内容重复修订；以任务级事务锁或原子 revision 分配避免 `max(revision)+1` 竞争。接口接收可选幂等键并复用同一次生成结果。

### 5. 无有效证据只保留草稿语义

eligible 数量为零时拒绝生成正式包。页面可以继续显示已有业务处理内容，但必须标识为“待核查业务分析草稿”，不能显示完成态 packet ID 或 revision。

### 6. 权限按内部工作产物处理

生成、查看最新/历史 businesspacket、查看 manifest、分块结果和累积证据均要求 OPERATOR。VIEWER 只能读取另有明确发布契约的内容；本 change 不创建发布契约。权限在服务端执行，前端隐藏按钮仅作体验优化。

## Risks / Trade-offs

- [多轮模型调用增加延迟与成本] → 配置化预算、缓存已验证分块摘要，并在 UI 显示生成中与失败状态。
- [缓存摘要对应的证据发生变化] → digest 和 manifest 快照不匹配即失效，不跨版本复用。
- [相同内容 digest 但模型元数据不同] → 内容幂等以规范化业务结果和完整输入谱系为准，调用元数据不触发无意义修订。
- [并发生成长期持锁] → 模型调用在锁外执行，最终落库阶段以幂等键和短事务锁仲裁。
- [VIEWER 既有接口可能已经能读取] → 后端统一收紧并增加回归测试，不能依赖前端路由隐藏。

## Migration Plan

1. 扩展 businesspacket 修订存储以保存完成态、manifest、分析 attempt、版本和 digest 谱系，并建立唯一约束。
2. 保留既有修订为 legacy 来源，不追认其已执行全量证据分析；展示时明确生成方式。
3. 先上线专用分析服务和服务端权限，再切换前端生成入口。
4. 在兼容期保留旧数据只读，不允许旧装配路径生成新修订。
5. 回滚应用时保留已生成的不可变修订；不得降级修改其完成态或谱系。

## Open Questions

无。业务状态已确定为独立 `businesspacket` 生成即完成。
