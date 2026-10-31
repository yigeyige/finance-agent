## Why

流程文件生成在 `deepseek-v4.1-flash` 下误用未知模型的 48,000 字节门禁，真实可用材料被提前拒绝；隆地审批场景的禁查表规则也仅存在于当前运行配置，缺少可重复部署的迁移。

## What Changes

- 为 `deepseek-v4.1-flash` 单独设置 500,000 UTF-8 字节的本地输入门禁，按用户确认的 1M 输入上限取一半；384K 输出上限独立，其他模型门禁不变。
- 新增 Flyway 迁移，只向「审批流程生成-隆地」的调查提示词追加不得查询 `basedata.t_org_process_template` 的规则；重复应用不重复追加，不覆盖管理员的其他提示词内容。
- 保留超限时的中文错误和模型调用前拦截，不改变流程文件生成的输入来源或输出协议。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-process-artifact`: 当前模型的输入规模门禁按已确认输入上限的一半检查。
- `business-handling-guidance`: 隆地审批流程调查提示词明确排除指定表，且通过迁移保持跨环境一致。

## Impact

- 后端：`ModelInputBudget` 与针对性测试。
- 数据：新增 V35 Flyway SQL，只更新目标场景的调查提示词。
- 无新增依赖或接口；迁移执行与手工运行验证由用户负责。
