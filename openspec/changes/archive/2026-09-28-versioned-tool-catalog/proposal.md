## Why

工具调用目前只有调用意图和审批绑定，缺少冻结在 runtime bundle 中的版本化工具目录，无法稳定拒绝伪造工具名、schema 版本或非白名单环境。

## What Changes

建立首期固定工具目录、版本摘要、风险和审批元数据，并提供 runtime bundle 绑定校验和只读管理查询 API。

