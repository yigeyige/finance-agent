<template>
  <section class="task-workbench" aria-labelledby="workbench-title">
    <div class="page-heading"><div><p class="eyebrow">任务发起与范围确认</p><h2 id="workbench-title">建立一个有边界的调查任务</h2><p class="page-description">先说明问题和来源，再确认系统、对象、路径、时间及字段权限。</p></div><el-tag v-if="task" :type="task.status === 'DRAFT' ? 'warning' : 'success'" effect="plain">{{ task.status }}</el-tag></div>
    <el-alert v-if="staleMessage" :title="staleMessage" type="warning" :closable="false" show-icon />
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-row :gutter="20"><el-col :span="15"><el-card shadow="never" class="form-card">
      <el-steps :active="activeStep" finish-status="success" simple><el-step title="任务问题" /><el-step title="前提来源" /><el-step title="范围确认" /></el-steps>
      <el-form :model="form" label-position="top" class="task-form">
        <template v-if="activeStep === 0"><el-form-item label="任务标题"><el-input v-model.trim="form.title" maxlength="300" show-word-limit placeholder="例如：核查审批配置异常" /></el-form-item><el-form-item label="原始问题"><el-input v-model.trim="form.question" type="textarea" :rows="6" maxlength="8000" show-word-limit placeholder="描述需要核查的业务问题和预期结果" /></el-form-item><el-button type="primary" :loading="saving" @click="createTaskDraft">创建任务草稿</el-button></template>
        <template v-else-if="activeStep === 1"><el-form-item label="前提类型"><el-input v-model.trim="form.premiseType" /></el-form-item><el-form-item label="前提主体"><el-input v-model.trim="form.subjectKey" /></el-form-item><el-form-item label="前提说明"><el-input v-model.trim="form.statement" type="textarea" :rows="3" /></el-form-item><el-form-item label="来源说明"><el-input v-model.trim="form.sourceDescription" type="textarea" :rows="3" /></el-form-item><el-button type="primary" :loading="saving" @click="savePremise">保存前提并进入范围确认</el-button></template>
        <template v-else><el-form-item label="系统编码"><el-input v-model.trim="form.systemCode" placeholder="例如：FIN-SYN" /></el-form-item><el-form-item v-for="field in scopeFields" :key="field.key" :label="field.label"><el-input v-model.trim="form[field.key]" type="textarea" :rows="2" /></el-form-item><el-form-item label="契约版本"><el-input v-model.trim="form.schemaVersion" /></el-form-item><el-button type="primary" :loading="saving" @click="saveScope">确认范围修订</el-button></template>
      </el-form>
    </el-card></el-col><el-col :span="9"><el-card shadow="never" class="detail-card"><div class="card-heading"><h3>当前任务</h3><span v-if="task">v{{ task.rowVersion }}</span></div><el-empty v-if="!task" description="完成第一步后显示任务详情" :image-size="64" /><dl v-else class="task-summary"><dt>任务标题</dt><dd>{{ task.title }}</dd><dt>任务标识</dt><dd class="mono">{{ task.taskId }}</dd><dt>范围修订</dt><dd>{{ task.scopeRevisionNo || '尚未确认' }}</dd><dt>范围摘要</dt><dd class="mono">{{ task.scopeDigest || '尚未生成' }}</dd></dl></el-card></el-col></el-row>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { TaskResponse } from '../api/generated'
import { getCurrentIdentity } from '../services/identity'
import { addPremise, ConfirmScopePayload, confirmScope, createTask, isStaleError, parseJsonField } from '../services/task-api'

interface WorkbenchForm { title: string; question: string; premiseType: string; subjectKey: string; statement: string; sourceDescription: string; systemCode: string; objectSelector: string; effectiveTimeRange: string; includedPaths: string; excludedPaths: string; allowedReadFields: string; allowedRetainFields: string; allowedModelFields: string; schemaVersion: string }
type ScopeField = 'objectSelector' | 'effectiveTimeRange' | 'includedPaths' | 'excludedPaths' | 'allowedReadFields' | 'allowedRetainFields' | 'allowedModelFields'

export default Vue.extend({
  name: 'WorkbenchHome',
  data() { return { activeStep: 0, saving: false, task: null as TaskResponse | null, errorMessage: '', staleMessage: '', scopeFields: [{ key: 'objectSelector' as ScopeField, label: '对象选择器 JSON' }, { key: 'effectiveTimeRange' as ScopeField, label: '生效时间范围 JSON' }, { key: 'includedPaths' as ScopeField, label: '纳入路径 JSON 数组' }, { key: 'excludedPaths' as ScopeField, label: '排除路径 JSON 数组' }, { key: 'allowedReadFields' as ScopeField, label: '允许读取字段 JSON 数组' }, { key: 'allowedRetainFields' as ScopeField, label: '允许保留字段 JSON 数组' }, { key: 'allowedModelFields' as ScopeField, label: '允许模型字段 JSON 数组' }], form: { title: '', question: '', premiseType: '业务规则', subjectKey: '', statement: '', sourceDescription: '', systemCode: '', objectSelector: '{"module":"approval"}', effectiveTimeRange: '{"from":"2026-01-01","to":"2026-12-31"}', includedPaths: '["workflow.approvals"]', excludedPaths: '[]', allowedReadFields: '["workflow.id"]', allowedRetainFields: '["workflow.id"]', allowedModelFields: '["workflow.id"]', schemaVersion: 'scope-v1' } as WorkbenchForm } },
  methods: {
    /** 创建任务草稿并推进到前提来源步骤。 */
    async createTaskDraft(): Promise<void> { const identity = getCurrentIdentity(); if (!identity || !this.form.title || !this.form.question) { this.errorMessage = '请先填写任务标题、原始问题并确认当前身份'; return }; await this.execute(async () => { this.task = await createTask({ title: this.form.title, originalQuestion: this.form.question, initiatorUserId: identity.userId, runtimeBundleId: '00000000-0000-4000-8000-000000000010' }); this.activeStep = 1 }) },
    /** 保存前提来源并推进到范围确认步骤。 */
    async savePremise(): Promise<void> { if (!this.task || !this.form.subjectKey || !this.form.statement || !this.form.sourceDescription) { this.errorMessage = '请完整填写前提来源'; return }; await this.execute(async () => { this.task = await addPremise(this.task!.taskId, { premiseType: this.form.premiseType, subjectKey: this.form.subjectKey, statement: this.form.statement, sourceDescription: this.form.sourceDescription }, this.task!.rowVersion); this.activeStep = 2 }) },
    /** 提交范围 JSON 并显示新的不可变修订。 */
    async saveScope(): Promise<void> { if (!this.task || !this.form.systemCode) { this.errorMessage = '请填写系统编码'; return }; try { const payload: ConfirmScopePayload = { systemCode: this.form.systemCode, objectSelector: parseJsonField(this.form.objectSelector, '对象选择器'), effectiveTimeRange: parseJsonField(this.form.effectiveTimeRange, '生效时间范围'), includedPaths: parseJsonField(this.form.includedPaths, '纳入路径'), excludedPaths: parseJsonField(this.form.excludedPaths, '排除路径'), allowedReadFields: parseJsonField(this.form.allowedReadFields, '允许读取字段'), allowedRetainFields: parseJsonField(this.form.allowedRetainFields, '允许保留字段'), allowedModelFields: parseJsonField(this.form.allowedModelFields, '允许模型字段'), schemaVersion: this.form.schemaVersion }; await this.execute(async () => { this.task = await confirmScope(this.task!.taskId, payload, this.task!.rowVersion) }) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '范围校验未完成，请检查输入' } },
    /** 统一处理保存中的加载、错误和 stale 状态。 */
    async execute(action: () => Promise<void>): Promise<void> { this.saving = true; this.errorMessage = ''; this.staleMessage = ''; try { await action() } catch (error: unknown) { if (isStaleError(error)) this.staleMessage = '范围或任务版本已变化，请刷新当前任务后再提交'; else this.errorMessage = error instanceof Error ? error.message : '请求未完成，请稍后重试' } finally { this.saving = false } },
  },
})
</script>
