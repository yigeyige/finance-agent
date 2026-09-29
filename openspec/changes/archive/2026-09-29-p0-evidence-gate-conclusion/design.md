## Context

DEV-QA-001 至 DEV-QA-009 已分别留下可复核的静态产物，但 DEV-QA-003、DEV-QA-004、DEV-QA-006、DEV-QA-008 和 DEV-QA-009 仍需要用户在本机执行运行时或真实环境验证。证据包必须区分实现方静态证据和用户证据。

## Decisions

1. 以 JSON fixture 作为唯一索引，任务状态只允许 `VERIFIED_STATIC` 或 `PENDING_USER`；任何 `PENDING_USER` 项都不能声明为通过。
2. 三项零容忍指标的当前静态基线值均为 0，但结论状态保持 `PENDING_USER`，直到用户完成数据边界扫描、审批/发布负向场景和并发恢复验证。
3. 检查脚本默认离线运行，只验证 fixture 与仓库内证据路径；不读取密钥、不调用网络、不连接数据库。
4. 指南使用 `CONDITIONAL` 结论模板，保留 P1 输入、已知限制和签署字段，避免将缺失的真实证据伪装成通过。
