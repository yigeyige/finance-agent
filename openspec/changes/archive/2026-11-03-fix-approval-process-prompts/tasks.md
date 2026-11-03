## 1. Prompt rewrite

- [x] 1.1 重写「审批流程生成-隆地」`investigation_prompt`：标题优先命名、角色码约定表+值集兜底、act_id 约定表+首条兜底、单起草节点+roleCode 网关、仅生成 Y 标记节点、来源流程不完整只作参考。
- [x] 1.2 重写同场景 `process_artifact_prompt`：审批规则契约、act_id/approvalNode 取值、网关条件写法。

## 2. Migration

- [x] 2.1 新增 Flyway V52 更新两段提示词。
- [x] 2.2 重启后端应用迁移并校验提示词内容。

## 3. Code fallback

- [x] 3.1 规范化器支持“节点名称 → 节点 ID”兜底，并在警告中带出被丢弃的引用值。
- [x] 3.2 转换器跳过不合规审批规则；全部跳过时不生成 `审批规则.json`，且不因此标记待修复。
- [x] 3.3 修复 `WorkbenchHome.vue` 证据轮次重复 key。

## 4. Verification

- [x] 4.1 运行后端流程产物相关单元测试（含网关尺寸修正，全部通过）。
- [x] 4.2 运行前端类型检查/测试（typecheck 通过、40 项测试通过）。
- [ ] 4.3 重启后端应用 V52 并重跑“测试2号出差流程”链路（模型服务关闭，暂缓）。
