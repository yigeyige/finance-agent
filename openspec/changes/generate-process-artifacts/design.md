## Context

EMS 审批流程以 4 个配置文件导入（部门审批流.json / 节点参数配置.json / 审批规则.json / BPMN 编辑器 JSON），schema 精确性是导入成功的前提。经评审确定：模型负责把业务方案结构化为小的中间表示，Java 负责按构件模板精确转换；文件生成作为决策包之后的独立步骤。

## Goals / Non-Goals

**Goals:** 模型产出中间表示 + Java 构件转换生成 2~4 个文件；产物持久化与下载；场景级生成文件提示词。
**Non-Goals:** 不做产物版本历史（覆盖式）、不做 ZIP 打包下载、不做新构件类型（会签/并行网关留待后续按构件扩展）、不改变现有调查/决策包链路。

## Decisions

### 总体架构
- **模型出中间表示，Java 出文件**：模型输出业务语义小 JSON（节点/流转/分档/模板），转换器内置 5 类构件工厂精确拼装；模型不接触监听器、坐标、resourceId 等技术字段。
- **独立触发**：必须已有决策包才可生成文件；失败可独立重试。
- **覆盖式更新**：重复生成时删除该任务旧产物再插入新一批；中间表示留档行同样覆盖。
- **产物数量 2~4**：`rules` 或 `orgTemplate` 为空时对应文件不生成；BPMN 与节点参数配置必生成。

### 中间表示 schema（模型输出契约）
```json
{
  "processName": "中艺2号测试流程",
  "companyCode": "ZYSM0",
  "currency": "HKD",
  "nodes": [
    { "id": "act_010", "name": "单据起草", "type": "draft" },
    { "id": "act_10", "name": "组长审批", "type": "approve", "joinSkip": true,
      "assignee": { "kind": "role", "roleCode": "XSZZZ" } },
    { "id": "act_11", "name": "部门负责人审批", "type": "approve",
      "assignee": { "kind": "tier", "tier": 3 } }
  ],
  "flows": [
    { "from": "act_010", "to": "act_10", "condition": "${roleCode=='ZYMD'}" },
    { "from": "act_010", "to": "act_11", "condition": null },
    { "from": "act_10", "to": "act_11" },
    { "from": "act_11", "to": "end" }
  ],
  "rules": { "lines": [ { "value": "A001", "min": -99, "max": 10000 } ] },
  "orgTemplate": { "business": "WY", "approvalNode": "act_11", "nodeRule": "tier3" }
}
```
- `nodes[].type` 枚举五类，对应 BPMN node_type：`draft`(0)/`approve`(1)/`accountant`(2)/`task-pool`(4)/`import-erp`(5)；证明人/影像/资产等特殊节点均为 `approve` + 不同 assignee。
- `assignee.kind` 三种：`role`（roleCode 变量）、`tier`（走 t_org_process_template 层级 + getAssigneePath=Auditor）、`fixed`（fixedCode 变量）。
- `nodes[].id` 由模型按样例惯例填写（act_010/act_10/CSIMG_010 等），Java 校验唯一、非空、被 flows 正确引用；`joinSkip` 默认 false。
- 网关不在 schema 中：节点出线 ≥2 时转换器自动插入 ExclusiveGateway，`condition=null` 为默认线。
- 校验规则：节点 id 唯一、flows 的 from/to 必须引用存在节点或 `end`、type/assignee 枚举合法、rules 区间连续性不强制但 min<max。

### 转换器（Java）
- 构件工厂：UserTask（按 type 变体带 node_type/监听器等默认配置）、ExclusiveGateway、SequenceFlow（含 conditionsequenceflow）、StartNoneEvent、EndNoneEvent。
- 通用组装：按 nodes/flows 拼装 childShapes，resourceId 用 UUID 生成。
- 分层自动布局：x 按拓扑层级递增，y 按同层分支展开，保证不重叠（坐标不追求美观，导入后可在 modeler 微调）。
- 文件生成：BPMN（编辑器 JSON 单行）、节点参数配置（每节点每变量一行，assignee 展开为 roleCode/getAssigneePath/is_choose/fixedCode 变量行）、审批规则（head + lineList，rules 为空不生成）、部门审批流（单对象，orgTemplate 为空不生成）。
- 转换在事务内完成：校验失败或任一文件生成失败则整体不落库。

### 提示词与模型调用
- `business_scene_skill` 新增 `process_artifact_prompt` 列（可空，≤4000 字符）：完整描述中间表示 schema、填写规则与惯例（节点 id 命名、条件表达式格式、金额分档规则）；该提示词为场景专有能力（审批流程生成场景维护，其他场景留空）。
- 新模型方法 `generateProcessDefinition`：输入 = 任务标题/业务说明 + 决策包方案（结论+推荐方案+备选）+ 场景 process_artifact_prompt；输出 = 中间表示 JSON（严格解析，多余/缺失字段报错）。
- 场景无该提示词时接口直接报错「当前场景未配置生成文件提示词」。

### 存储与接口
- 新表 `task.business_process_artifact`：`artifact_id / task_id / artifact_type(process-definition|bpmn|node-variables|approval-rules|org-template) / file_name / content_text / created_by / created_at / updated_by / updated_at`，索引 task_id。
- `POST /api/v1/tasks/{taskId}/process-artifacts`：触发生成（要求已有决策包；覆盖式）。
- `GET /api/v1/tasks/{taskId}/process-artifacts`：产物列表（不含正文）。
- `GET /api/v1/tasks/{taskId}/process-artifacts/{artifactId}/download`：文件下载（正文流）。

### 前端
- WorkbenchHome 决策包卡片下方：无产物时「生成流程文件」按钮（无决策包置灰），有产物时按钮变「重新生成」+ 文件卡片列表（类型图标 + 文件名 + 下载）。
- BusinessSkillCatalog 场景编辑弹窗：新增「生成文件提示词」textarea（可选），与调查/决策包提示词并列。

### V30 迁移（顺带两项调优）
- scene 表加 `process_artifact_prompt` 列。
- 建产物表。
- GUIDE_HANDLING 模板强化：明确「需要样例附件完整结构时必须把文件名写入 requestedAttachments」。
- 「审批流程生成-隆地」调查提示词补充：角色代码在值集表 `sys_value_set`（ZYMD 等），避免模型空查 sys_role。

## Risks / Trade-offs

- 构件工厂的默认配置（监听器/node_type 等）从单一流程样例抽象，其他流程形态可能需要修正默认值——由真实导入验证驱动迭代。
- 模型填写节点 id 与条件表达式质量依赖生成文件提示词的描述质量，首次落地后需按实际生成效果调优提示词。

## Migration Plan

V30 纯新增（加列 + 建表 + 模板/提示词更新），不影响既有数据；产物表为空起步。

## Open Questions

无（方案已经 grill 共识）。
