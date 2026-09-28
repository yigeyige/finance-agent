<template>
  <section class="packet-page" aria-labelledby="published-packet-title">
    <div class="page-heading"><div><p class="eyebrow">已发布决策包</p><h2 id="published-packet-title">发布结果与交接</h2><p class="page-description">OPERATOR 与 VIEWER 查看同一份不可变发布修订；调查关闭不代表整改已经完成。</p></div><el-button @click="$router.back()">返回任务</el-button></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-card v-if="packet" shadow="never" class="packet-result-card">
      <div class="result-heading"><div><h3>修订 v{{ packet.revisionNo }}</h3><p>摘要：<span class="mono">{{ packet.contentDigest }}</span></p></div><el-tag :type="packet.taskStatus === 'CLOSED' ? 'info' : 'success'">{{ packet.taskStatus }}</el-tag></div>
      <el-descriptions :column="2" border size="small"><el-descriptions-item label="核查时点">{{ packet.checkedAt }}</el-descriptions-item><el-descriptions-item label="发布时间">{{ packet.publishedAt }}</el-descriptions-item><el-descriptions-item label="来源版本"><span class="mono">{{ packet.sourceVersions }}</span></el-descriptions-item><el-descriptions-item label="覆盖摘要"><span class="mono">{{ packet.coverageSummary }}</span></el-descriptions-item><el-descriptions-item label="限制说明" :span="2">{{ packet.limitations }}</el-descriptions-item></el-descriptions>
      <el-tabs v-model="activeTab"><el-tab-pane label="结构化 JSON" name="json"><pre class="packet-json">{{ packet.canonicalJson }}</pre></el-tab-pane><el-tab-pane label="HTML 快照" name="html"><iframe title="已发布决策包 HTML 快照" class="packet-preview" sandbox="" :srcdoc="packet.htmlSnapshot" /></el-tab-pane></el-tabs>
      <el-divider content-position="left">交接状态</el-divider>
      <div v-if="packet.handoff" class="handoff-summary"><el-tag type="success">已交接</el-tag><span>责任：{{ packet.handoff.ownerReference }}</span><span>下游状态：{{ packet.handoff.downstreamStatus }}</span><span v-if="packet.handoff.externalProcessReference">流程引用：{{ packet.handoff.externalProcessReference }}</span><span>交接时间：{{ packet.handoff.handedOffAt }}</span></div>
      <el-empty v-else description="尚未登记外部责任交接" :image-size="48" />
      <template v-if="isOperator && !packet.handoff && packet.taskStatus === 'PUBLISHED'"><el-divider content-position="left">登记外部交接</el-divider><el-form label-position="top" class="review-form"><el-form-item label="责任引用"><el-input v-model.trim="handoff.ownerReference" maxlength="300" /></el-form-item><el-form-item label="外部流程引用"><el-input v-model.trim="handoff.externalProcessReference" maxlength="500" /></el-form-item><el-form-item label="下游状态"><el-input v-model.trim="handoff.downstreamStatus" maxlength="30" /></el-form-item><el-button type="primary" :loading="saving" @click="submitHandoff">确认交接</el-button></el-form></template>
      <template v-if="isOperator && packet.handoff && packet.taskStatus === 'HANDED_OFF'"><el-divider content-position="left">关闭调查</el-divider><el-alert title="关闭调查只表示本次调查流程结束，不代表外部整改已完成。" type="warning" :closable="false" /><el-form label-position="top" class="review-form"><el-form-item label="关闭原因"><el-input v-model.trim="closure.closureReason" maxlength="2000" type="textarea" :rows="3" /></el-form-item><el-button type="primary" :loading="saving" @click="submitClosure">关闭调查</el-button></el-form></template>
      <el-alert v-if="packet.taskStatus === 'CLOSED'" title="调查已关闭；整改完成状态仍需外部流程确认。" type="info" :closable="false" />
    </el-card>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { PublishedPacketResponse } from '../api/generated'
import { getCurrentIdentity } from '../services/identity'
import { closeTask, fetchPublishedPacket, handoffPacket } from '../services/packet-lifecycle-api'

export default Vue.extend({
  name: 'PublishedPacket',
  data() { return { packet: null as PublishedPacketResponse | null, activeTab: 'json', saving: false, errorMessage: '', handoff: { ownerReference: '', externalProcessReference: '', downstreamStatus: '待外部处理' }, closure: { closureReason: '' } } },
  computed: {
    isOperator(): boolean { return getCurrentIdentity()?.roles.includes('OPERATOR') ?? false },
  },
  async created(): Promise<void> { await this.loadPacket() },
  methods: {
    async loadPacket(): Promise<void> { try { this.packet = await fetchPublishedPacket(String(this.$route.params.taskId)); this.errorMessage = '' } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '已发布决策包查询未完成，请稍后重试' } },
    async submitHandoff(): Promise<void> { if (!this.packet || !this.handoff.ownerReference.trim() || !this.handoff.downstreamStatus.trim()) { this.errorMessage = '请填写责任引用和下游状态'; return }; await this.execute(async () => { await handoffPacket(this.packet!.taskId, this.packet!.packetRevisionId, { ownerReference: this.handoff.ownerReference, externalProcessReference: this.handoff.externalProcessReference, downstreamStatus: this.handoff.downstreamStatus }); await this.loadPacket() }) },
    async submitClosure(): Promise<void> { if (!this.packet || !this.closure.closureReason.trim()) { this.errorMessage = '请填写关闭原因'; return }; await this.execute(async () => { await closeTask(this.packet!.taskId, { closureReason: this.closure.closureReason }); await this.loadPacket() }) },
    async execute(action: () => Promise<void>): Promise<void> { this.saving = true; this.errorMessage = ''; try { await action() } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '请求未完成，请稍后重试' } finally { this.saving = false } },
  },
})
</script>
