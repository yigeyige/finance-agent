## Why

步骤审批、工具 permit、专业确认、草稿复核和原子发布已经分别实现，但缺少一份按请求入口组织的负向验收清单。没有统一样例时，旧审批、跨任务 permit、范围漂移和 VIEWER 越权容易只在单元测试中被覆盖，无法由用户按同一组请求复核拒绝状态与审计结果。

## What Changes

- 新增版本化负向用例 fixture，覆盖范围/审批/字段模板/permit/专业确认/review/并发发布/VIEWER 越权等场景。
- 新增请求样例和 PowerShell 离线校验脚本；显式执行时仅读取用户保存的响应与审计证据目录。
- 新增后端契约测试，校验角色矩阵、发布和复核入口的权限边界、审批绑定校验、旧 review 拒绝和发布幂等契约。

## Capabilities

### New Capabilities

- `approval-permission-publication-negative-gate`: 提供审批、权限和发布的负向验收请求与证据约束。

### Modified Capabilities

- 无

## Impact

- 新增 `scripts/fixtures/`、PowerShell 验收脚本和后端 QA 契约测试。
- 不改变运行时 API、数据库结构或角色定义；人工执行阶段由用户启动本地服务并发送 fixture 中的请求。
