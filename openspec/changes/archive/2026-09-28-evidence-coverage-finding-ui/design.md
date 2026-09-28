## Context

证据快照、覆盖记录、发现和计算见证已经由后端领域模型持久化。本变更只补充面向工作台的安全查询层和前端只读展示。

## Goals / Non-Goals
**Goals:**
- 清晰区分当前、未来、未确定事实及 COMPLETE、PARTIAL、UNKNOWN 覆盖状态。
- 将确定性发现与算法版本、输入输出摘要、快照和复算状态关联展示。
- 通过角色校验及 DTO 白名单避免返回或渲染裁剪字段。

**Non-Goals:**
- 不重新计算发现，不修改证据写入流程。
- 不展示原始模型响应、推理链或受限字段值。

## Decisions
- 控制器聚合现有仓储并映射到不可变响应视图，前端通过 typed service 获取。
- `allowedFields` 不进入响应；前端只渲染后端白名单字段。
- 非 OPERATOR 身份在发布前访问直接返回 403，保持后续发布权限边界。

## Verification

- 后端执行 `mvn -q test` 与 `mvn -q -DskipTests compile`。
- 前端执行 `npm run typecheck` 与 `npm run build`。
- 用户启动环境后抽查三类事实、覆盖缺口、发现见证和 VIEWER 拒绝路径。
