## Why

「审批流程生成」场景的最终交付物是可导入 EMS 的配置文件（部门审批流 / 节点参数配置 / 审批规则 / BPMN 定义），当前决策包只有文字方案。直接让模型手写这些文件不可靠（BPMN 编辑器 JSON 约 35KB、含坐标/监听器/网关等复杂结构，且导入 EMS 对 schema 精确性要求高），需要一条「模型出结构化中间表示 + Java 精确转换」的文件产物生成链路。

## What Changes

- 业务场景 Skill 新增第三个提示词字段 `process_artifact_prompt`（生成文件提示词）：描述中间表示 schema 与填写要求，按场景维护，其他场景可空。
- 新增模型方法：输入任务信息 + 决策包方案 + 生成文件提示词，输出流程中间表示（小 JSON），严格校验。
- 新增 Java 转换器：5 类构件工厂（draft/approve/accountant/task-pool/import-erp）+ 通用图组装 + 分层自动布局，把中间表示转换为 2~4 个文件内容。
- 新增产物表 `business_process_artifact` 持久化文件内容，含中间表示留档行。
- 新增接口：触发生成（要求已有决策包）、产物列表、文件下载。
- 前端：决策包卡片下方提供「生成流程文件」按钮与文件卡片下载；场景编辑弹窗新增第三个提示词输入。
- V30 迁移：scene 表加列、建产物表，并顺带两项调优（GUIDE_HANDLING 模板加强两段式附件请求引导、该场景调查提示词补充角色值集表 sys_value_set）。

## Capabilities

### New Capabilities
- `business-process-artifact`: 依据决策包生成可导入的流程配置文件产物。

### Modified Capabilities
- `business-scene-skill-attachment`: 场景 Skill 新增生成文件提示词字段（与附件能力同一维护界面）。

## Impact

模型调用新增一次（生成中间表示）；不改变调查与决策包现有链路；产物覆盖式更新（不做版本历史）；仅当场景存在生成文件提示词且任务已有决策包时可触发。
