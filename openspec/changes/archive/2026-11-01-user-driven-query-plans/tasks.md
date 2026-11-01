## 1. Query plan contract

- [x] 1.1 新增 `EmsDatabaseQueryPlan` / `EmsDatabaseQueryStep` / 绑定结构
- [x] 1.2 实现严格 JSON 解析与校验（步骤数、依赖、占位符、白名单列）
- [x] 1.3 更新 `BusinessModelClient` 与 `DeepSeekBusinessModelClient` 返回计划

## 2. Execution

- [x] 2.1 执行器支持参数绑定
- [x] 2.2 拓扑执行、0 行/失败隔离、证据合成
- [x] 2.3 移除 `MAX_ROUNDS` 自动循环，一次动作一个计划

## 3. Service and evidence

- [x] 3.1 先保存证据快照，再调用初稿模型
- [x] 3.2 初稿失败保留证据并返回错误
- [x] 3.3 单任务串行 + 幂等键

## 4. Cache and flags

- [x] 4.1 候选/选表任务级缓存
- [x] 4.2 `needTableReselection` 失效重建
- [x] 4.3 响应增加 evidenceRound / needMoreEvidence / needTableReselection

## 5. Prompt and frontend

- [x] 5.1 提示词改为 steps/bindings 契约
- [x] 5.2 前端类型、轮次与继续提示

## 6. Verification

- [x] 6.1 后端单测：依赖、0 行、失败、绑定注入、证据复用
- [x] 6.2 前端 typecheck/build
- [x] 6.3 实际链路验证
