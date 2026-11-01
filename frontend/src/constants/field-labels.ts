/**
 * 前端字段字典：集中维护模型字段路径与中文名称/说明，渲染时补充业务可读注释。
 * 仅用于展示，不改变模型输出或后端契约。
 */
export interface FieldLabel {
  label: string
  description: string
}

export const FIELD_LABELS: Record<string, FieldLabel> = {
  guidanceDraft: { label: '业务处理初稿', description: '模型生成的业务处理草稿' },
  'guidanceDraft.subjectKey': { label: '处理对象', description: '本次业务处理针对的对象' },
  'guidanceDraft.objectKey': { label: '业务对象', description: '业务对象或单据类型' },
  'guidanceDraft.recommendedGoal': { label: '推荐方案', description: '模型推荐的处理方案' },
  'guidanceDraft.alternativeGoals': { label: '备选方案', description: '可替代的处理方案' },
  'guidanceDraft.reason': { label: '推荐理由', description: '推荐该方案的依据' },
  'guidanceDraft.conclusion': { label: '调查结论', description: '调查分析形成的结论' },
  'guidanceDraft.evidence': { label: '关联证据', description: '模型引用的调查证据' },
  'guidanceDraft.processPlan': { label: '流程结构', description: '流程节点、流转和分支条件' },
  'guidanceDraft.processPlan.name': { label: '流程名称', description: '流程的显示名称' },
  'guidanceDraft.processPlan.nodes': { label: '流程节点', description: '起草、审批等流程节点' },
  'guidanceDraft.processPlan.flows': { label: '流程流转', description: '节点之间的流转和条件' },
  'guidanceDraft.processPlan.nodeParameters': { label: '节点参数', description: '节点处理人及变量配置' },
  'guidanceDraft.processPlan.departmentApprovals': { label: '部门审批流', description: '按部门层级配置的审批关系' },
  'guidanceDraft.processPlan.approvalRules': { label: '审批规则', description: '金额或条件审批规则' },
  'guidanceDraft.processPlan.missingItems': { label: '待补信息', description: '仍需用户补充的信息' },
  'guidanceDraft.processPlan.unsupported': { label: '暂未支持', description: '当前无法表达的流程形态' },
  subjectKey: { label: '处理对象', description: '决策包对应的处理对象' },
  objectKey: { label: '业务对象', description: '决策包对应的业务对象' },
  recommendedGoal: { label: '推荐方案', description: '决策包推荐的处理方案' },
  alternativeGoals: { label: '备选方案', description: '决策包中的备选方案' },
  reason: { label: '推荐理由', description: '推荐方案的依据' },
  conclusion: { label: '调查结论', description: '决策包的调查结论' },
  evidence: { label: '关联证据', description: '决策包引用的调查证据' },
  databaseInstanceName: { label: '数据库实例', description: '调查使用的 EMS 数据库实例' },
  decisionPacket: { label: '业务决策包', description: '固定业务处理结论的决策包' },
  revision: { label: '修订号', description: '决策包修订版本' }
}

/** 返回字段中文名，未命中时回退到指定文案或字段路径。 */
export function fieldLabel(path: string, fallback?: string): string {
  return FIELD_LABELS[path]?.label ?? fallback ?? path
}

/** 返回字段说明，未命中时返回空字符串。 */
export function fieldDescription(path: string): string {
  return FIELD_LABELS[path]?.description ?? ''
}

/** 返回“字段路径（中文名）”，用于页面补充注释。 */
export function fieldAnnotation(path: string): string {
  const item = FIELD_LABELS[path]
  return item ? `${path}（${item.label}）` : path
}

/** 返回“中文名：说明”，用于 tooltip。 */
export function fieldTooltip(path: string): string {
  const item = FIELD_LABELS[path]
  return item ? `${item.label}：${item.description}` : path
}
