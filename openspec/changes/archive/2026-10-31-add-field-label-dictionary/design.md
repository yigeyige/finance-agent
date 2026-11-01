## Context

前端已有多处展示模型字段：`WorkbenchHome.vue` 的流程事实、决策包卡片，`TaskDetail.vue` 的决策包卡片。字段名是英文/驼峰，业务用户不理解。

## Decisions

### 1. 集中字段字典

新增 `frontend/src/constants/field-labels.ts`：

- `FIELD_LABELS`：字段路径 → `{ label, description }`；
- `fieldLabel(path, fallback?)`：返回中文名；
- `fieldDescription(path)`：返回说明；
- `fieldAnnotation(path)`：返回 `path（中文名）`；
- `fieldTooltip(path)`：返回 `中文名：说明`。

覆盖 `guidanceDraft`、`guidanceDraft.processPlan`、`nodes`、`flows`、`missingItems`、`subjectKey`、`recommendedGoal`、`reason`、`conclusion`、`evidence` 等。

### 2. 渲染方式

在字段标题或标签后追加小号灰色 `field-path` 文本，例如：

```text
流程事实  guidanceDraft.processPlan（流程结构）
处理对象  subjectKey（处理对象）
推荐      recommendedGoal（推荐方案）
```

未命中字典时只显示原字段名，不报错。

### 3. 不改模型输出

提示词和后端 JSON 保持不变；模型解释继续放在 `conclusion`、`reason`、`missingItems`。

## Risks

- 字段过多导致视觉噪音 → 使用小号灰色文本，只标注主要字段。
- 字典遗漏 → 回退原字段名，不影响功能。
