## Context

V52 调查提示词已有两种查询：按 `procdef_id` 读取已有流程结构，以及全新流程按节点名称从 `psc_act_config` 取得示例 `act_id`。目前通用禁止条款没有区分用途。

## Goals / Non-Goals

**Goals:** 将已有流程结构查询与新流程标识参考查询写成各自适用的规则。

**Non-Goals:** 不修改 `act_id` 约定表、查询 SQL、Java 转换逻辑、文件生成提示词或运行时查库机制。

## Decisions

1. 在 `【目标流程定位】` 中规定：已有流程结构查询先取得 `procdef_id`；没有时跳过 `psc_act_re_procdef`、`psc_act_config`、`psc_act_sequence_flow` 的结构查询。
2. 在 `【act_id 约定表】` 中明确：全新流程可按节点名称定向查询 `psc_act_config` 获取参考 `act_id`；结果不视为目标流程的结构证据。保留既有约定表优先及缺失处理。
3. 使用新 Flyway 版本，仅替换目标场景 `investigation_prompt` 内需要澄清的两处语句。V52 已提交且可能已执行，不能修改其校验和；迁移不覆盖 `process_artifact_prompt`。

## Risks / Trade-offs

- 提示词变更不能代替真实模型执行验证；迁移、内容断言和 OpenSpec 校验完成后，模型链路由用户启动环境手动验证。
- 回滚时使用后续补偿迁移恢复原提示词片段，不回改已执行的 Flyway 文件。
