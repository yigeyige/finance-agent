<template>
  <section class="packet-page" aria-labelledby="packet-title">
    <div class="page-heading"><div><p class="eyebrow">决策包草稿</p><h2 id="packet-title">组装结构化决策包</h2><p class="page-description">canonical JSON 是权威内容，HTML 只是同一摘要的只读快照；实际业务影响必须显式确认。</p></div><el-button @click="$router.back()">返回证据</el-button></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-card v-if="!result" shadow="never" class="packet-form-card">
      <el-form label-position="top">
        <el-form-item label="范围修订标识"><el-input v-model.trim="form.scopeRevisionId" placeholder="填写当前范围修订 UUID" /></el-form-item><el-form-item label="运行版本标识"><el-input v-model.trim="form.runtimeBundleId" placeholder="填写已发布运行版本 UUID" /></el-form-item><el-form-item label="问题"><el-input v-model.trim="form.problem" maxlength="2000" show-word-limit type="textarea" :rows="3" /></el-form-item>
        <el-form-item v-for="field in jsonFields" :key="field.key" :label="field.label"><el-input v-model.trim="form[field.key]" type="textarea" :rows="4" /></el-form-item>
        <el-button type="primary" :loading="saving" @click="assemble">生成草稿</el-button>
      </el-form>
    </el-card>
    <el-card v-else shadow="never" class="packet-result-card">
      <div class="result-heading"><div><h3>草稿已生成</h3><p>摘要：<span class="mono">{{ result.contentDigest }}</span></p></div><el-tag type="success">{{ result.status }}</el-tag></div>
      <el-tabs v-model="activeTab"><el-tab-pane label="结构化 JSON" name="json"><pre class="packet-json">{{ result.canonicalJson }}</pre></el-tab-pane><el-tab-pane label="HTML 快照" name="html"><iframe title="决策包 HTML 快照" class="packet-preview" :srcdoc="result.htmlSnapshot" /></el-tab-pane></el-tabs>
    </el-card>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { PacketDraftResponse } from '../api/generated'
import { parseJsonField } from '../services/task-api'
import { assemblePacketDraft, PacketDraftPayload } from '../services/packet-api'

type JsonFieldKey = Exclude<keyof PacketDraftPayload, 'scopeRevisionId' | 'runtimeBundleId' | 'problem'>

export default Vue.extend({
  name: 'PacketDraft',
  data() { return { saving: false, errorMessage: '', result: null as PacketDraftResponse | null, activeTab: 'json', jsonFields: [{ key: 'premises' as JsonFieldKey, label: '问题/前提 JSON' }, { key: 'scope' as JsonFieldKey, label: '范围 JSON' }, { key: 'sources' as JsonFieldKey, label: '来源 JSON 数组' }, { key: 'facts' as JsonFieldKey, label: '事实 JSON（须包含 current/future/undetermined）' }, { key: 'computations' as JsonFieldKey, label: '计算 JSON' }, { key: 'professionalJudgments' as JsonFieldKey, label: '专业判断 JSON 数组' }, { key: 'recommendations' as JsonFieldKey, label: '建议 JSON 数组' }, { key: 'businessConfirmations' as JsonFieldKey, label: '业务确认 JSON（须包含 impactConfirmed/pendingItems）' }, { key: 'validationPreparation' as JsonFieldKey, label: '验证准备 JSON' }, { key: 'handoff' as JsonFieldKey, label: '交接占位 JSON' }], form: { scopeRevisionId: '', runtimeBundleId: '', problem: '核查已确认范围内的异常并形成可复核结论', premises: '{}', scope: '{}', sources: '[]', facts: '{"current":[],"future":[],"undetermined":[]}', computations: '{}', professionalJudgments: '[]', recommendations: '[]', businessConfirmations: '{"impactConfirmed":false,"pendingItems":[]}', validationPreparation: '{}', handoff: '{}' } as Record<string, string> } },
  methods: {
    async assemble(): Promise<void> { const taskId = String(this.$route.params.taskId); const scopeRevisionId = this.form.scopeRevisionId.trim(); const runtimeBundleId = this.form.runtimeBundleId.trim(); if (!scopeRevisionId || !runtimeBundleId || !this.form.problem.trim()) { this.errorMessage = '请填写范围版本、运行版本和问题描述'; return }; try { this.saving = true; this.errorMessage = ''; const payload: PacketDraftPayload = { scopeRevisionId, runtimeBundleId, problem: this.form.problem, premises: parseJsonField(this.form.premises, '问题/前提'), scope: parseJsonField(this.form.scope, '范围'), sources: parseJsonField(this.form.sources, '来源'), facts: parseJsonField(this.form.facts, '事实'), computations: parseJsonField(this.form.computations, '计算'), professionalJudgments: parseJsonField(this.form.professionalJudgments, '专业判断'), recommendations: parseJsonField(this.form.recommendations, '建议'), businessConfirmations: parseJsonField(this.form.businessConfirmations, '业务确认'), validationPreparation: parseJsonField(this.form.validationPreparation, '验证准备'), handoff: parseJsonField(this.form.handoff, '交接占位') }; this.result = await assemblePacketDraft(taskId, payload) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '决策包草稿未生成，请检查结构化内容' } finally { this.saving = false } },
  },
})
</script>
