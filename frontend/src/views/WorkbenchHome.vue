<!-- 财务运维业务向导，把任务、范围、方案和审批的技术契约转换为业务人员可理解的操作。 -->
<template>
  <section class="task-workbench" aria-labelledby="workbench-title">
    <div class="page-heading">
      <div><p class="eyebrow">业务调查向导</p><h2 id="workbench-title">按业务问题发起一次调查</h2><p class="page-description">只需说明调查对象和范围，系统会自动准备技术参数和证据结构。</p></div>
      <div><el-tag v-if="task" :type="task.status === 'DRAFT' ? 'warning' : 'success'">{{ taskStatusLabel }}</el-tag><el-button v-if="task" type="text" @click="openDiagnostics">查看运行状态</el-button></div>
    </div>
    <el-alert v-if="staleMessage" :title="staleMessage" type="warning" :closable="false" show-icon />
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-row :gutter="20">
      <el-col :span="16"><el-card shadow="never" class="form-card guided-workflow">
        <el-steps :active="activeStep" finish-status="success" simple><el-step title="说明问题" /><el-step title="确认对象" /><el-step title="设置范围" /><el-step title="批准方案" /></el-steps>
        <div class="step-intro"><span class="step-intro__number">{{ activeStep + 1 }}</span><div><h3>{{ currentStepTitle }}</h3><p>{{ currentStepDescription }}</p></div></div>
        <el-form :model="form" label-position="top" class="task-form">
          <template v-if="activeStep === 0">
            <el-form-item label="这次调查叫什么"><el-input v-model.trim="form.title" :maxlength="300" placeholder="例如：核查离职人员仍在生效的审批配置" /></el-form-item>
            <el-form-item label="希望系统查清什么"><el-input v-model.trim="form.question" type="textarea" :rows="5" :maxlength="8000" placeholder="请用业务语言描述现象、疑问和期望得到的结论" /></el-form-item>
            <el-button type="primary" :loading="saving" @click="createTaskDraft">下一步：确认调查对象</el-button>
          </template>
          <template v-else-if="activeStep === 1">
            <el-form-item label="调查场景"><el-select v-model="form.premiseType" placeholder="请选择调查场景"><el-option label="人员离任或岗位变更" value="人员变更" /><el-option label="审批流程异常" value="审批异常" /><el-option label="权限或配置核查" value="配置核查" /><el-option label="其他业务核查" value="其他核查" /></el-select></el-form-item>
            <el-form-item label="业务对象编号"><el-input v-model.trim="form.subjectKey" placeholder="例如：员工号、岗位号或配置对象编号" /></el-form-item>
            <el-form-item label="已知情况"><el-input v-model.trim="form.statement" type="textarea" :rows="3" placeholder="例如：该人员已于本月离职，需要核查仍然生效的审批与角色配置" /></el-form-item>
            <el-form-item label="信息来源"><el-input v-model.trim="form.sourceDescription" placeholder="例如：人事通知、业务部门反馈或日常巡检" /></el-form-item>
            <el-button type="primary" :loading="saving" @click="savePremise">下一步：设置调查范围</el-button>
          </template>
          <template v-else-if="activeStep === 2">
            <el-form-item label="要核查的系统"><el-select v-model="form.systemCode" placeholder="请选择系统"><el-option label="财务共享合成验证系统" value="FIN-SYN" /></el-select></el-form-item>
            <el-form-item label="系统中的业务对象"><el-input v-model.trim="form.objectKey" placeholder="默认沿用上一步的业务对象编号" /></el-form-item>
            <el-row :gutter="16"><el-col :span="12"><el-form-item label="核查开始日期"><el-input v-model.trim="form.startDate" placeholder="例如：2026-01-01" /></el-form-item></el-col><el-col :span="12"><el-form-item label="核查结束日期"><el-input v-model.trim="form.endDate" placeholder="例如：2026-12-31" /></el-form-item></el-col></el-row>
            <el-form-item label="需要核查哪些内容"><el-checkbox-group v-model="form.investigationAreas" class="business-options"><el-checkbox label="APPROVAL">审批流程</el-checkbox><el-checkbox label="ROLE">角色权限</el-checkbox><el-checkbox label="ACCOUNTING">会计配置</el-checkbox><el-checkbox label="CONFIG">系统配置引用</el-checkbox></el-checkbox-group></el-form-item>
            <el-form-item label="数据使用范围"><el-radio-group v-model="form.dataUse"><el-radio label="MINIMUM">仅使用形成结论所需的最少字段</el-radio><el-radio label="ANALYSIS">允许在本次调查中使用全部已授权字段</el-radio></el-radio-group></el-form-item>
            <el-button type="primary" :loading="saving" @click="saveScope">确认范围并交给模型分析</el-button>
          </template>
        </el-form>
        <div v-if="activeStep === 3 && task && task.scopeRevisionNo" class="execution-panel">
          <div class="model-stage" :class="{ 'model-stage--ready': modelPlan }">
            <div class="model-stage__heading"><div><p class="eyebrow">大模型交互</p><h4>{{ modelLoading ? '正在理解问题和调查范围' : modelPlan ? '模型已生成调查方案' : '等待模型生成调查方案' }}</h4></div><el-tag v-if="modelPlan" :type="modelPlan.metadata.mode === 'LIVE' ? 'success' : 'info'">{{ modelPlan.metadata.mode === 'LIVE' ? modelPlan.metadata.modelName : '本地模拟模型' }}</el-tag></div>
            <p v-if="modelLoading" class="model-stage__status">模型正在结合业务问题、调查对象和授权范围形成建议，请稍候。</p>
            <template v-else-if="modelPlan"><p class="model-reason">{{ modelPlan.reason }}</p><el-alert title="以下内容是模型建议，只有提交人工批准后才会执行。" type="warning" :closable="false" show-icon /></template>
            <el-button v-else type="primary" :loading="modelLoading" @click="requestModelPlan">重新请求模型生成方案</el-button>
          </div>
          <el-form v-if="modelPlan && !proposal" label-position="top"><el-form-item v-for="(goal, index) in planGoals" :key="`${index}-${goal}`" :label="index === 0 ? '模型推荐步骤' : `模型备选步骤 ${index}`"><el-input v-model.trim="planGoals[index]" /></el-form-item><el-button type="primary" :loading="saving" @click="submitPlan">采用建议并进入人工审批</el-button></el-form>
          <el-card v-if="proposal" shadow="never" class="approval-card">
            <div class="card-heading"><h4>批准本次调查</h4><el-tag type="info">系统已补齐技术绑定</el-tag></div><el-alert title="请选择要执行的步骤，并确认本次最多允许查询多少次。" type="info" :closable="false" show-icon />
            <el-form v-if="!approvalResult" label-position="top"><el-form-item label="执行步骤"><el-select v-model="selectedStepId"><el-option v-for="step in proposal.steps" :key="step.stepId" :label="step.goal" :value="step.stepId" /></el-select></el-form-item><el-form-item label="审批决定"><el-radio-group v-model="approval.decision"><el-radio label="APPROVED">批准执行</el-radio><el-radio label="REJECTED">暂不执行</el-radio></el-radio-group></el-form-item><el-form-item label="最多查询次数"><el-input-number v-model="approval.maxCalls" :min="1" :max="20" /></el-form-item><el-button type="primary" :loading="saving" @click="submitApproval">提交审批决定</el-button></el-form>
            <div v-else class="next-action-panel"><div><strong>{{ approval.decision === 'APPROVED' ? '已批准，等待系统调查' : '本次调查暂不执行' }}</strong><p>{{ approval.decision === 'APPROVED' ? '系统将按批准范围执行调查并形成证据。' : '审批决定已保存，如需继续可重新发起调查。' }}</p></div><el-button v-if="approval.decision === 'APPROVED'" type="primary" @click="$router.push(`/tasks/${task.taskId}/evidence`)">查看调查证据</el-button></div>
          </el-card>
          <h4 class="section-heading">任务进度</h4><el-tag type="info">{{ eventStatus }}</el-tag><ol v-if="events.length" class="event-timeline"><li v-for="event in events" :key="event.sequence"><time>{{ event.occurredAt }}</time><span>{{ eventText(event) }}</span></li></ol><div v-else class="empty-state" role="status">等待系统返回调查进度</div>
        </div>
      </el-card></el-col>
      <el-col :span="8"><el-card shadow="never" class="detail-card journey-card"><div class="card-heading"><h3>本次调查</h3><span v-if="task">{{ taskStatusLabel }}</span></div><div v-if="!task" class="empty-state" role="status">填写问题后，这里会持续显示调查进度</div><template v-else><dl class="task-summary"><dt>任务名称</dt><dd>{{ task.title }}</dd><dt>当前阶段</dt><dd>{{ currentStepTitle }}</dd><dt>调查对象</dt><dd>{{ form.subjectKey || '待确认' }}</dd><dt>调查内容</dt><dd>{{ areaLabels }}</dd></dl><el-button v-if="task.scopeRevisionNo" type="text" @click="$router.push(`/tasks/${task.taskId}/evidence`)">查看证据与结论</el-button><el-collapse class="technical-details"><el-collapse-item title="技术明细"><dl class="task-summary"><dt>任务标识</dt><dd class="mono">{{ task.taskId }}</dd><dt>范围修订</dt><dd>{{ task.scopeRevisionNo || '尚未确认' }}</dd><dt>范围摘要</dt><dd class="mono">{{ task.scopeDigest || '尚未生成' }}</dd></dl></el-collapse-item></el-collapse></template></el-card></el-col>
    </el-row>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { PublicTaskEvent, StepApprovalResponse, StepProposalResponse, TaskResponse } from '../api/generated'
import { buildScopePayload, createWorkflowUuid, DEFAULT_RUNTIME_BUNDLE_ID, sha256Hex } from '../services/business-workflow'
import { approveStep, ApproveStepPayload, proposeSteps } from '../services/execution-api'
import { getCurrentIdentity } from '../services/identity'
import { generateModelPlan, ModelPlanResponse } from '../services/model-api'
import { openSse, SseConnection } from '../services/sse'
import { addPremise, confirmScope, createTask, isStaleError } from '../services/task-api'

interface WorkbenchForm { title: string; question: string; premiseType: string; subjectKey: string; statement: string; sourceDescription: string; systemCode: string; objectKey: string; startDate: string; endDate: string; investigationAreas: string[]; dataUse: 'MINIMUM' | 'ANALYSIS' }
const STATUS_LABELS: Record<string, string> = { DRAFT: '填写中', WAITING_SCOPE_CONFIRMATION: '待确认范围', READY: '范围已确认', WAITING_STEP_APPROVAL: '待审批', RUNNING_STEP: '调查中', ANALYZING: '分析中', WAITING_PROFESSIONAL_CONFIRMATION: '待专业确认', DRAFT_PACKET: '准备决策包', WAITING_REVIEW: '待复核', PUBLISHED: '已发布', HANDED_OFF: '已交接', CLOSED: '已关闭' }
const AREA_LABELS: Record<string, string> = { APPROVAL: '审批流程', ROLE: '角色权限', ACCOUNTING: '会计配置', CONFIG: '系统配置引用' }

export default Vue.extend({
  name: 'WorkbenchHome',
  data() { return { activeStep: 0, saving: false, modelLoading: false, modelPlan: null as ModelPlanResponse | null, task: null as TaskResponse | null, proposal: null as StepProposalResponse | null, approvalResult: null as StepApprovalResponse | null, selectedStepId: '', planGoals: [] as string[], sourceBindingId: createWorkflowUuid(), events: [] as PublicTaskEvent[], eventStatus: '尚未开始', sseConnection: null as SseConnection | null, approval: { maxCalls: 3, decision: 'APPROVED' as 'APPROVED' | 'REJECTED' }, errorMessage: '', staleMessage: '', form: { title: '', question: '', premiseType: '人员变更', subjectKey: '', statement: '', sourceDescription: '', systemCode: 'FIN-SYN', objectKey: '', startDate: '2026-01-01', endDate: '2026-12-31', investigationAreas: ['APPROVAL', 'ROLE'], dataUse: 'MINIMUM' } as WorkbenchForm } },
  computed: {
    /** 返回当前步骤面向业务人员的标题。 */
    currentStepTitle(): string { return ['说明要解决的问题', '确认调查对象和依据', '圈定系统与核查内容', '确认系统建议的调查方案'][this.activeStep] || '调查处理中' },
    /** 返回当前步骤需要完成的业务动作说明。 */
    currentStepDescription(): string { return ['先描述现象和希望得到的结论。', '说明要查谁或什么对象，以及已知情况来自哪里。', '选择时间范围和核查内容，技术字段由系统生成。', '确认系统将执行的步骤和查询上限。'][this.activeStep] || '' },
    /** 把内部任务状态转换为业务状态。 */
    taskStatusLabel(): string { if (this.approvalResult) return this.approval.decision === 'APPROVED' ? '已批准，等待调查' : '暂不执行'; return this.task ? (STATUS_LABELS[this.task.status] || '处理中') : '未开始' },
    /** 汇总已选择的调查内容。 */
    areaLabels(): string { const values = this.form.investigationAreas.map((item) => AREA_LABELS[item]).filter(Boolean); return values.length ? values.join('、') : '待选择' },
  },
  methods: {
    /** 打开当前任务的运行状态页面。 */
    openDiagnostics(): void { if (this.task) void this.$router.push('/tasks/' + this.task.taskId + '/diagnostics') },
    /** 创建任务草稿并进入业务对象确认。 */
    async createTaskDraft(): Promise<void> { const identity = getCurrentIdentity(); if (!identity || !this.form.title || !this.form.question) { this.errorMessage = '请填写调查名称和需要查清的问题'; return }; await this.execute(async () => { this.task = await createTask({ title: this.form.title, originalQuestion: this.form.question, initiatorUserId: identity.userId, runtimeBundleId: DEFAULT_RUNTIME_BUNDLE_ID }); this.activeStep = 1; this.connectEvents() }) },
    /** 保存业务对象和信息来源，随后进入范围设置。 */
    async savePremise(): Promise<void> { if (!this.task || !this.form.subjectKey || !this.form.statement || !this.form.sourceDescription) { this.errorMessage = '请填写业务对象编号、已知情况和信息来源'; return }; await this.execute(async () => { this.task = await addPremise(this.task!.taskId, { premiseType: this.form.premiseType, subjectKey: this.form.subjectKey, statement: this.form.statement, sourceDescription: this.form.sourceDescription }, this.task!.rowVersion); this.form.objectKey = this.form.subjectKey; this.activeStep = 2 }) },
    /** 把业务范围转换为严格请求，确认后立即请求模型生成调查方案。 */
    async saveScope(): Promise<void> { if (!this.task || !this.form.systemCode || !this.form.objectKey || !/^\d{4}-\d{2}-\d{2}$/.test(this.form.startDate) || !/^\d{4}-\d{2}-\d{2}$/.test(this.form.endDate) || !this.form.investigationAreas.length) { this.errorMessage = '请完整选择系统、业务对象、日期范围和核查内容'; return }; const payload = buildScopePayload({ systemCode: this.form.systemCode, objectKey: this.form.objectKey, dateRange: [this.form.startDate, this.form.endDate], investigationAreas: this.form.investigationAreas, dataUse: this.form.dataUse }); await this.execute(async () => { this.task = await confirmScope(this.task!.taskId, payload, this.task!.rowVersion); this.activeStep = 3 }); if (this.task?.scopeRevisionNo) await this.requestModelPlan() },
    /** 调用后端受控模型并把结构化建议转成可人工调整的步骤。 */
    async requestModelPlan(): Promise<void> { if (!this.task || this.modelLoading) return; this.modelLoading = true; this.modelPlan = null; this.planGoals = []; this.errorMessage = ''; try { this.modelPlan = await generateModelPlan(this.task.taskId); this.planGoals = [this.modelPlan.recommendedGoal, ...this.modelPlan.alternativeGoals] } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '模型暂时无法生成调查方案，请稍后重试' } finally { this.modelLoading = false } },
    /** 提交系统建议的推荐步骤和备选步骤。 */
    async submitPlan(): Promise<void> { if (!this.task || !this.planGoals[0]?.trim()) { this.errorMessage = '调查方案尚未准备完成'; return }; const steps = this.planGoals.map((goal, index) => ({ goal: goal.trim(), recommended: index === 0 })).filter((step) => step.goal); await this.execute(async () => { this.proposal = await proposeSteps(this.task!.taskId, { steps }, this.task!.rowVersion); this.syncTaskProgress(this.proposal.status, this.proposal.rowVersion, this.proposal.scopeRevisionId); this.selectedStepId = this.proposal.currentStepId || this.proposal.steps[0]?.stepId || '' }) },
    /** 自动生成技术绑定后提交业务审批决定。 */
    async submitApproval(): Promise<void> { if (!this.task || !this.proposal || !this.selectedStepId || !this.task.currentScopeRevisionId) { this.errorMessage = '请先选择要执行的调查步骤'; return }; const selected = this.proposal.steps.find((step) => step.stepId === this.selectedStepId); if (!selected) { this.errorMessage = '所选调查步骤已变化，请重新选择'; return }; const payload: ApproveStepPayload = { stepId: selected.stepId, goalHash: await sha256Hex(selected.goal), sourceBindingId: this.sourceBindingId, scopeRevisionId: this.task.currentScopeRevisionId, fieldPolicyVersion: 'field-v1', queryTemplateVersion: 'query-v1', maxCalls: this.approval.maxCalls, expiresAt: null, expectedRowVersion: this.task.rowVersion, decision: this.approval.decision }; await this.execute(async () => { this.approvalResult = await approveStep(this.task!.taskId, payload); this.syncTaskProgress(this.task!.status, this.approvalResult.rowVersion) }) },
    /** 将步骤接口返回的新状态同步到当前任务卡。 */
    syncTaskProgress(status: TaskResponse['status'], rowVersion: number, scopeRevisionId?: string | null): void { if (!this.task) return; this.task = { ...this.task, status, rowVersion, currentScopeRevisionId: scopeRevisionId === undefined ? this.task.currentScopeRevisionId : scopeRevisionId } },
    /** 建立公开事件流并把事件限制在当前任务。 */
    connectEvents(): void { if (!this.task) return; this.sseConnection?.close(); this.eventStatus = '正在连接'; const base = import.meta.env.VITE_API_BASE_URL || '/api'; this.sseConnection = openSse(`${base}/tasks/${this.task.taskId}/events/stream`, { onOpen: () => { this.eventStatus = '调查进度已连接' }, onMessage: (event) => { if (event.taskId !== this.task?.taskId) return; this.events = [...this.events, event].slice(-50) }, onReconnect: () => { this.eventStatus = '等待新的调查进度' }, onError: () => { this.eventStatus = '实时消息暂时无法识别' } }) },
    /** 把公开事件转换为不暴露内部枚举的业务描述。 */
    eventText(event: PublicTaskEvent): string { return event.publicReason || event.resultReference || (event.status ? (STATUS_LABELS[event.status] || '调查状态已更新') : '调查状态已更新') },
    /** 统一处理保存状态、并发冲突和中文错误。 */
    async execute(action: () => Promise<void>): Promise<void> { if (this.saving) return; this.saving = true; this.errorMessage = ''; this.staleMessage = ''; try { await action() } catch (error: unknown) { if (isStaleError(error)) this.staleMessage = '任务范围已发生变化，请刷新后重新确认'; else this.errorMessage = error instanceof Error ? error.message : '请求未完成，请稍后重试' } finally { this.saving = false } },
  },
  beforeDestroy() { this.sseConnection?.close() },
})
</script>
