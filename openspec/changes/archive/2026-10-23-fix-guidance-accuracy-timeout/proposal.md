# Proposal

## Why

对「新建业务处理 → 开始模型分析」全链路验证时发现两个问题：模型查询 `basedata.t_audit_configuration` 审批配置时用数字 USERID 和系统角色编码匹配，而该表 `EMP_IDS` 存的是 USERCODE 字符串、`ROLE_CODE` 存的是审批环节角色，导致漏查返回 0 行；同时 guidance 同步接口耗时超过前端 240s 超时，页面报「网络暂时不可用」但后端实际能完成。

## What Changes

- 后端 `DeepSeekBusinessModelClient` 选表与查询规划提示词补充 `t_audit_configuration` 字段语义：`EMP_IDS` 存 USERCODE、`EMP_NAMES` 存姓名、`ROLE_CODE` 是审批环节角色，查询审批配置时用 USERCODE/姓名匹配，不用数字 USERID 或系统角色编码。
- 前端 `model-api.ts` 模型调用超时从 240s 提高到 600s，覆盖后端选表、多轮只读查询与初稿生成的总耗时。

## Capabilities

### Modified Capabilities

- `business-handling-guidance`: 模型基于受控 EMS 证据生成结论与关联证据时，正确识别审批配置表的字段语义，避免漏查。

## Impact

- 后端：`DeepSeekBusinessModelClient`（选表/查询规划提示词字符串）。
- 前端：`model-api.ts`（`MODEL_TIMEOUT` 常量）。
- 不改变接口契约、数据库结构或依赖；不引入新依赖。
