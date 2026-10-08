<!-- 业务处理工作台：以标题说明、模型协作和一次确认承接调查与调整，不展示技术 JSON 或执行审批字段。 -->
<template>
  <section class="task-workbench" aria-labelledby="workbench-title">
    <div class="page-heading">
      <div><p class="eyebrow">业务处理向导</p><h2 id="workbench-title">把业务问题整理成可执行方案</h2><p class="page-description">先说明要处理什么，再与模型共同确认对象和范围；既可用于调查，也可用于业务调整。</p></div>
      <div><el-tag v-if="task" :type="task.status === 'DRAFT' ? 'warning' : 'success'">{{ taskStatusLabel }}</el-tag><el-button v-if="task" type="text" @click="openDiagnostics">查看运行状态</el-button></div>
    </div>
    <el-alert v-if="staleMessage" :title="staleMessage" type="warning" :closable="false" show-icon />
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-row :gutter="20">
      <el-col :span="16"><el-card shadow="never" class="form-card guided-workflow">
        <el-steps :active="activeStep" finish-status="success" simple><el-step title="业务说明" /><el-step title="模型协作与确认" /></el-steps>
        <div class="step-intro"><span class="step-intro__number">{{ activeStep + 1 }}</span><div><h3>{{ currentStepTitle }}</h3><p>{{ currentStepDescription }}</p></div></div>
        <el-form :model="form" label-position="top" class="task-form">
          <template v-if="activeStep === 0">
            <el-form-item label="业务标题"><el-input v-model.trim="form.title" :maxlength="300" placeholder="例如：离职人员审批权限处置" /></el-form-item>
            <el-form-item label="业务说明"><el-input v-model.trim="form.question" type="textarea" :rows="6" :maxlength="8000" placeholder="请说明要调查或调整的业务情况、已知影响和希望形成的处理结果" /></el-form-item>
            <el-button type="primary" :loading="saving" @click="createTaskDraft">开始模型分析</el-button>
          </template>
        </el-form>
        <template v-if="activeStep === 1">
          <div class="handling-workspace">
            <section class="handling-form-panel" aria-labelledby="handling-form-title">
              <div class="card-heading"><div><p class="eyebrow">可编辑初稿</p><h4 id="handling-form-title">确认对象和处理范围</h4></div><el-tag v-if="guidance" :type="guidance.metadata.mode === 'LIVE' ? 'success' : 'info'">{{ modelLabel }}</el-tag></div>
              <p v-if="modelLoading" class="model-stage__status">模型正在根据业务说明整理对象、范围和处理方案，请稍候。</p>
              <el-form v-else label-position="top" :model="form" class="task-form">
                <el-form-item label="处理方式"><el-radio-group v-model="form.handlingType"><el-radio label="INVESTIGATE">业务调查</el-radio><el-radio label="ADJUST">业务调整</el-radio></el-radio-group></el-form-item>
                <el-form-item label="业务场景"><el-select v-model="form.premiseType" placeholder="请选择业务场景"><el-option label="业务调整" value="业务调整" /><el-option label="人员离任或岗位变更" value="人员变更" /><el-option label="审批流程异常" value="审批异常" /><el-option label="权限或配置调整" value="配置调整" /><el-option label="其他业务处理" value="其他处理" /></el-select></el-form-item>
                <el-form-item label="业务对象"><el-input v-model.trim="form.subjectKey" placeholder="例如：员工号、岗位号或配置对象编号" /></el-form-item>
                <el-form-item label="已知情况"><el-input v-model.trim="form.statement" type="textarea" :rows="3" placeholder="说明现状、影响或需要处理的事项" /></el-form-item>
                <el-form-item label="信息来源"><el-input v-model.trim="form.sourceDescription" placeholder="例如：人事通知、业务部门反馈或日常巡检" /></el-form-item>
                <el-form-item label="处理系统"><el-select v-model="form.systemCode" placeholder="请选择系统"><el-option label="财务共享合成验证系统" value="FIN-SYN" /></el-select></el-form-item>
                <el-form-item label="系统中的业务对象"><el-input v-model.trim="form.objectKey" placeholder="填写对应的系统对象" /></el-form-item>
                <el-row :gutter="16"><el-col :span="12"><el-form-item label="开始日期"><el-input v-model.trim="form.startDate" placeholder="例如：2026-01-01" /></el-form-item></el-col><el-col :span="12"><el-form-item label="结束日期"><el-input v-model.trim="form.endDate" placeholder="例如：2026-12-31" /></el-form-item></el-col></el-row>
                <el-form-item label="处理内容"><el-checkbox-group v-model="form.investigationAreas" class="business-options"><el-checkbox label="APPROVAL">审批流程</el-checkbox><el-checkbox label="ROLE">角色权限</el-checkbox><el-checkbox label="ACCOUNTING">会计配置</el-checkbox><el-checkbox label="CONFIG">系统配置引用</el-checkbox></el-checkbox-group></el-form-item>
                <el-form-item label="数据使用范围"><el-radio-group v-model="form.dataUse"><el-radio label="MINIMUM">仅使用形成结论所需的最少字段</el-radio><el-radio label="ANALYSIS">允许在本次处理中使用全部已授权字段</el-radio></el-radio-group></el-form-item>
                <div class="plan-editor"><h4>业务方案</h4><p v-if="guidance" class="model-reason">{{ guidance.draft.reason }}</p><el-form-item v-for="(goal, index) in planGoals" :key="`${index}-${goal}`" :label="index === 0 ? '推荐方案' : `备选方案 ${index}`"><el-input v-model.trim="planGoals[index]" /></el-form-item></div>
                <el-button v-if="!proposal" type="primary" :loading="saving" :disabled="modelLoading" @click="generateBusinessPlan">确认对象和范围，生成业务方案</el-button>
              </el-form>
            </section>
            <aside class="model-conversation" aria-labelledby="model-chat-title">
              <div class="card-heading"><div><p class="eyebrow">大模型协作</p><h4 id="model-chat-title">直接告诉模型如何修改</h4></div><el-tag v-if="guidance" type="info">{{ modelLabel }}</el-tag></div>
              <div class="chat-history" role="log" aria-live="polite"><div v-if="!chatMessages.length && !modelLoading" class="empty-state">模型初稿准备完成后，会在这里说明它的处理思路。</div><article v-for="message in chatMessages" :key="message.id" class="chat-message" :class="`chat-message--${message.role}`"><strong>{{ message.role === 'operator' ? '管理员' : '模型' }}</strong><p>{{ message.content }}</p></article></div>
              <el-input v-model.trim="chatMessage" type="textarea" :rows="4" :maxlength="1000" :disabled="!guidance || chatLoading || Boolean(proposal)" placeholder="例如：改为业务调整，重点处理审批权限，日期范围限定为本季度" />
              <div class="chat-actions"><el-button :loading="chatLoading" :disabled="!guidance || !chatMessage || Boolean(proposal)" type="primary" @click="reviseGuidance">发送修改意见</el-button><el-button :disabled="modelLoading || Boolean(proposal)" @click="requestInitialGuidance">重新生成初稿</el-button></div>
              <el-alert title="模型建议仅用于协助填写；只有点击“确认对象和范围”后，系统才会写入业务前提和范围。" type="info" :closable="false" show-icon />
            </aside>
          </div>
          <div v-if="proposal" class="next-action-panel business-plan-result"><div><strong>业务方案已生成</strong><p>方案已保存为当前任务的处理步骤；执行仍由既有受控流程管理。</p></div><el-tag type="success">{{ proposal.steps.length }} 个处理步骤</el-tag></div>
          <h4 class="section-heading">任务进度</h4><el-tag type="info">{{ eventStatus }}</el-tag><ol v-if="events.length" class="event-timeline"><li v-for="event in events" :key="event.sequence"><time>{{ event.occurredAt }}</time><span>{{ eventText(event) }}</span></li></ol><div v-else class="empty-state" role="status">等待系统返回处理进度</div>
        </template>
      </el-card></el-col>
      <el-col :span="8"><el-card shadow="never" class="detail-card journey-card"><div class="card-heading"><h3>业务处理概览</h3><span v-if="task">{{ taskStatusLabel }}</span></div><div v-if="!task" class="empty-state" role="status">提交业务标题和说明后，这里会显示当前处理概要</div><template v-else><dl class="task-summary"><dt>业务标题</dt><dd>{{ task.title }}</dd><dt>业务说明</dt><dd>{{ form.question }}</dd><dt>处理方式</dt><dd>{{ handlingTypeLabel }}</dd><dt>业务对象</dt><dd>{{ form.subjectKey || '待模型分析' }}</dd><dt>处理内容</dt><dd>{{ areaLabels }}</dd></dl><el-collapse class="technical-details"><el-collapse-item title="技术明细"><dl class="task-summary"><dt>任务标识</dt><dd class="mono">{{ task.taskId }}</dd><dt>范围修订</dt><dd>{{ task.scopeRevisionNo || '尚未确认' }}</dd><dt>范围摘要</dt><dd class="mono">{{ task.scopeDigest || '尚未生成' }}</dd></dl></el-collapse-item></el-collapse></template></el-card></el-col>
    </el-row>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { PublicTaskEvent, StepProposalResponse, TaskResponse } from '../api/generated'
import { buildScopePayload, DEFAULT_RUNTIME_BUNDLE_ID } from '../services/business-workflow'
import { proposeSteps } from '../services/execution-api'
import { getCurrentIdentity } from '../services/identity'
import { BusinessGuidanceResponse, generateBusinessGuidance, reviseBusinessGuidance } from '../services/model-api'
import { openSse, SseConnection } from '../services/sse'
import { addPremise, confirmScope, createTask, isStaleError } from '../services/task-api'

interface WorkbenchForm { title: string; question: string; handlingType: 'INVESTIGATE' | 'ADJUST'; premiseType: string; subjectKey: string; statement: string; sourceDescription: string; systemCode: 'FIN-SYN'; objectKey: string; startDate: string; endDate: string; investigationAreas: Array<'APPROVAL' | 'ROLE' | 'ACCOUNTING' | 'CONFIG'>; dataUse: 'MINIMUM' | 'ANALYSIS' }
interface ChatMessage { id: string; role: 'operator' | 'model'; content: string }
const STATUS_LABELS: Record<string, string> = { DRAFT: '填写中', WAITING_SCOPE_CONFIRMATION: '待确认范围', READY: '范围已确认', WAITING_STEP_APPROVAL: '业务方案已生成', RUNNING_STEP: '处理中', ANALYZING: '分析中', WAITING_PROFESSIONAL_CONFIRMATION: '待专业确认', DRAFT_PACKET: '准备决策包', WAITING_REVIEW: '待复核', PUBLISHED: '已发布', HANDED_OFF: '已交接', CLOSED: '已关闭' }
const AREA_LABELS: Record<string, string> = { APPROVAL: '审批流程', ROLE: '角色权限', ACCOUNTING: '会计配置', CONFIG: '系统配置引用' }

export default Vue.extend({
  name: 'WorkbenchHome',
  data() { return { activeStep: 0, saving: false, modelLoading: false, chatLoading: false, guidance: null as BusinessGuidanceResponse | null, task: null as TaskResponse | null, proposal: null as StepProposalResponse | null, planGoals: [] as string[], chatMessage: '', chatMessages: [] as ChatMessage[], events: [] as PublicTaskEvent[], eventStatus: '尚未开始', sseConnection: null as SseConnection | null, errorMessage: '', staleMessage: '', form: { title: '', question: '', handlingType: 'INVESTIGATE', premiseType: '人员变更', subjectKey: '', statement: '', sourceDescription: '', systemCode: 'FIN-SYN', objectKey: '', startDate: '2026-01-01', endDate: '2026-12-31', investigationAreas: ['APPROVAL', 'ROLE'], dataUse: 'MINIMUM' } as WorkbenchForm } },
  computed: {
    /** 返回当前步骤的业务化标题。 */
    currentStepTitle(): string { return this.activeStep === 0 ? '说明这次要处理的业务' : '与模型共同确认对象、范围和方案' },
    /** 返回当前步骤的操作说明。 */
    currentStepDescription(): string { return this.activeStep === 0 ? '填写业务标题和说明，系统将用它生成任务概览并准备模型初稿。' : '模型先给出初稿；你可以直接修改字段或用自然语言让模型调整方案。' },
    /** 显示当前任务的业务状态。 */
    taskStatusLabel(): string { return this.task ? (STATUS_LABELS[this.task.status] || '处理中') : '未开始' },
    /** 汇总当前处理内容。 */
    areaLabels(): string { const values = this.form.investigationAreas.map((item) => AREA_LABELS[item]).filter(Boolean); return values.length ? values.join('、') : '待模型分析' },
    /** 将内部处理类型转换为业务文案。 */
    handlingTypeLabel(): string { return this.form.handlingType === 'ADJUST' ? '业务调整' : '业务调查' },
    /** 展示模型身份，避免 Stub 被误认为真实调用。 */
    modelLabel(): string { return this.guidance?.metadata.mode === 'LIVE' ? this.guidance.metadata.modelName : '本地模拟模型' },
  },
  methods: {
    /** 打开当前任务的运行状态页面。 */
    openDiagnostics(): void { if (this.task) void this.$router.push('/tasks/' + this.task.taskId + '/diagnostics') },
    /** 创建任务草稿后请求模型生成对象、范围和方案初稿。 */
    async createTaskDraft(): Promise<void> { const identity = getCurrentIdentity(); if (!identity || !this.form.title || !this.form.question) { this.errorMessage = '请填写业务标题和业务说明'; return }; await this.execute(async () => { this.task = await createTask({ title: this.form.title, originalQuestion: this.form.question, initiatorUserId: identity.userId, runtimeBundleId: DEFAULT_RUNTIME_BUNDLE_ID }); this.activeStep = 1; this.connectEvents() }); await this.requestInitialGuidance() },
    /** 请求模型重新根据任务说明生成初稿，不写入任何业务范围。 */
    async requestInitialGuidance(): Promise<void> { if (!this.task || this.modelLoading || this.proposal) return; this.modelLoading = true; this.errorMessage = ''; try { const response = await generateBusinessGuidance(this.task.taskId); this.applyGuidance(response); this.chatMessages = [{ id: `model-initial-${Date.now()}`, role: 'model', content: response.reply }] } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '模型暂时无法生成业务处理初稿，请稍后重试' } finally { this.modelLoading = false } },
    /** 将管理员的自然语言意见发给受控模型，并用完整新草稿回填页面。 */
    async reviseGuidance(): Promise<void> { if (!this.task || !this.guidance || !this.chatMessage.trim() || this.chatLoading || this.proposal) return; const message = this.chatMessage.trim(); this.chatLoading = true; this.errorMessage = ''; this.chatMessages.push({ id: `operator-${Date.now()}`, role: 'operator', content: message }); this.chatMessage = ''; try { const response = await reviseBusinessGuidance(this.task.taskId, message, this.guidance.draft); this.applyGuidance(response); this.chatMessages.push({ id: `model-${Date.now()}`, role: 'model', content: response.reply }) } catch (error: unknown) { this.chatMessages = this.chatMessages.filter((item) => item.content !== message || item.role !== 'operator'); this.errorMessage = error instanceof Error ? error.message : '模型暂时无法修改业务方案，请稍后重试' } finally { this.chatLoading = false } },
    /** 将受控模型草稿投影到可编辑表单和业务方案列表。 */
    applyGuidance(response: BusinessGuidanceResponse): void { const draft = response.draft; this.guidance = response; this.form.handlingType = draft.handlingType; this.form.premiseType = draft.premiseType; this.form.subjectKey = draft.subjectKey; this.form.statement = draft.statement; this.form.sourceDescription = draft.sourceDescription; this.form.systemCode = draft.systemCode; this.form.objectKey = draft.objectKey; this.form.startDate = draft.startDate; this.form.endDate = draft.endDate; this.form.investigationAreas = draft.investigationAreas; this.form.dataUse = draft.dataUse; this.planGoals = [draft.recommendedGoal, ...draft.alternativeGoals] },
    /** 按顺序保存管理员确认的前提和范围，再直接生成业务方案。 */
    async generateBusinessPlan(): Promise<void> { if (!this.task || !this.guidance || !this.form.subjectKey || !this.form.statement || !this.form.sourceDescription || !this.form.objectKey || !/^\d{4}-\d{2}-\d{2}$/.test(this.form.startDate) || !/^\d{4}-\d{2}-\d{2}$/.test(this.form.endDate) || !this.form.investigationAreas.length || !this.planGoals[0]?.trim()) { this.errorMessage = '请补全业务对象、信息依据、日期范围、处理内容和推荐方案'; return }; const steps = this.planGoals.map((goal, index) => ({ goal: goal.trim(), recommended: index === 0 })).filter((step) => step.goal); await this.execute(async () => { this.task = await addPremise(this.task!.taskId, { premiseType: this.form.premiseType, subjectKey: this.form.subjectKey, statement: this.form.statement, sourceDescription: this.form.sourceDescription }, this.task!.rowVersion); const scope = buildScopePayload({ systemCode: this.form.systemCode, objectKey: this.form.objectKey, dateRange: [this.form.startDate, this.form.endDate], investigationAreas: this.form.investigationAreas, dataUse: this.form.dataUse }); this.task = await confirmScope(this.task!.taskId, scope, this.task!.rowVersion); this.proposal = await proposeSteps(this.task!.taskId, { steps }, this.task!.rowVersion); this.syncTaskProgress(this.proposal.status, this.proposal.rowVersion, this.proposal.scopeRevisionId) }) },
    /** 将步骤接口返回的新状态同步到当前任务卡。 */
    syncTaskProgress(status: TaskResponse['status'], rowVersion: number, scopeRevisionId?: string | null): void { if (!this.task) return; this.task = { ...this.task, status, rowVersion, currentScopeRevisionId: scopeRevisionId === undefined ? this.task.currentScopeRevisionId : scopeRevisionId } },
    /** 建立当前任务的公开事件流。 */
    connectEvents(): void { if (!this.task) return; this.sseConnection?.close(); this.eventStatus = '正在连接'; const base = import.meta.env.VITE_API_BASE_URL || '/api'; this.sseConnection = openSse(`${base}/tasks/${this.task.taskId}/events/stream`, { onOpen: () => { this.eventStatus = '业务处理进度已连接' }, onMessage: (event) => { if (event.taskId !== this.task?.taskId) return; this.events = [...this.events, event].slice(-50) }, onReconnect: () => { this.eventStatus = '等待新的业务处理进度' }, onError: () => { this.eventStatus = '实时消息暂时无法识别' } }) },
    /** 将公开事件转换为业务可读的状态信息。 */
    eventText(event: PublicTaskEvent): string { return event.publicReason || event.resultReference || (event.status ? (STATUS_LABELS[event.status] || '业务处理状态已更新') : '业务处理状态已更新') },
    /** 统一处理保存状态、并发冲突和中文错误。 */
    async execute(action: () => Promise<void>): Promise<void> { if (this.saving) return; this.saving = true; this.errorMessage = ''; this.staleMessage = ''; try { await action() } catch (error: unknown) { if (isStaleError(error)) this.staleMessage = '当前范围已发生变化，请刷新后重新确认'; else this.errorMessage = error instanceof Error ? error.message : '请求未完成，请稍后重试' } finally { this.saving = false } },
  },
  beforeDestroy() { this.sseConnection?.close() },
})
</script>
