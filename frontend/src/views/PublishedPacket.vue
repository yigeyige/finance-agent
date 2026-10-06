<!-- 已发布决策包页面，以业务摘要和后续责任交接为主，原始结构仅保留为只读技术明细。 -->
<template>
  <section class="packet-page" aria-labelledby="published-packet-title">
    <div class="page-heading"><div><p class="eyebrow">已发布决策包</p><h2 id="published-packet-title">调查结论与后续处理</h2><p class="page-description">这里展示已经发布的业务结论、责任交接和调查关闭状态。</p></div><el-button @click="$router.push('/')">返回任务</el-button></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-card v-if="packet" shadow="never" class="packet-result-card">
      <div class="result-heading"><div><p class="eyebrow">第 {{ packet.revisionNo }} 次发布</p><h3>业务调查结论</h3></div><el-tag :type="packet.taskStatus === 'CLOSED' ? 'info' : 'success'">{{ taskStatusLabel }}</el-tag></div>
      <div class="business-summary-grid business-summary-grid--wide"><article><strong>{{ businessSummary.currentCount }}</strong><span>当前生效</span></article><article><strong>{{ businessSummary.futureCount }}</strong><span>未来生效</span></article><article><strong>{{ businessSummary.undeterminedCount }}</strong><span>待确定</span></article><article><strong>{{ businessSummary.findingCount }}</strong><span>建议事项</span></article></div>
      <el-alert :title="businessSummary.coverageLabel" type="success" :closable="false" show-icon />
      <div v-if="businessSummary.recommendations.length" class="finding-list"><h4>后续建议</h4><ul><li v-for="recommendation in businessSummary.recommendations" :key="recommendation">{{ recommendation }}</li></ul></div>
      <dl class="description-grid"><div><dt>核查完成时间</dt><dd>{{ packet.checkedAt }}</dd></div><div><dt>发布时间</dt><dd>{{ packet.publishedAt }}</dd></div><div class="description-grid__wide"><dt>限制说明</dt><dd>{{ limitationsLabel }}</dd></div></dl>
      <el-collapse class="technical-details"><el-collapse-item title="查看技术明细"><p class="panel-hint">以下信息用于技术排障和审计，不需要业务人员修改。</p><dl class="description-grid"><div><dt>内容摘要</dt><dd class="mono">{{ packet.contentDigest }}</dd></div><div><dt>来源版本</dt><dd class="mono">{{ packet.sourceVersions }}</dd></div><div class="description-grid__wide"><dt>覆盖摘要</dt><dd class="mono">{{ packet.coverageSummary }}</dd></div></dl><el-tabs v-model="activeTab"><el-tab-pane label="结构化数据" name="json"><pre class="packet-json">{{ packet.canonicalJson }}</pre></el-tab-pane><el-tab-pane label="归档快照" name="html"><iframe title="已发布决策包归档快照" class="packet-preview" sandbox="" :srcdoc="packet.htmlSnapshot" /></el-tab-pane></el-tabs></el-collapse-item></el-collapse>
      <h4 class="section-heading">后续责任交接</h4>
      <div v-if="packet.handoff" class="handoff-summary"><el-tag type="success">已交接</el-tag><span>责任人或团队：{{ packet.handoff.ownerReference }}</span><span>处理状态：{{ packet.handoff.downstreamStatus }}</span><span v-if="packet.handoff.externalProcessReference">外部处理单号：{{ packet.handoff.externalProcessReference }}</span><span>交接时间：{{ packet.handoff.handedOffAt }}</span></div>
      <div v-else class="empty-state" role="status">尚未指定后续处理责任人</div>
      <template v-if="isOperator && !packet.handoff && packet.taskStatus === 'PUBLISHED'"><el-form label-position="top" class="review-form"><el-form-item label="责任人或责任团队"><el-input v-model.trim="handoff.ownerReference" :maxlength="300" placeholder="填写负责后续处理的人员或团队" /></el-form-item><el-form-item label="外部处理单号（选填）"><el-input v-model.trim="handoff.externalProcessReference" :maxlength="500" placeholder="例如：整改工单或流程编号" /></el-form-item><el-form-item label="当前处理状态"><el-input v-model.trim="handoff.downstreamStatus" :maxlength="30" /></el-form-item><el-button type="primary" :loading="saving" @click="submitHandoff">确认交接</el-button></el-form></template>
      <template v-if="isOperator && packet.handoff && packet.taskStatus === 'HANDED_OFF'"><h4 class="section-heading">结束本次调查</h4><el-alert title="结束调查只表示本次核查流程完成，不代表外部整改已经完成。" type="warning" :closable="false" show-icon /><el-form label-position="top" class="review-form"><el-form-item label="结束说明"><el-input v-model.trim="closure.closureReason" :maxlength="2000" type="textarea" :rows="3" placeholder="说明调查已完成以及后续由谁跟进" /></el-form-item><el-button type="primary" :loading="saving" @click="submitClosure">结束调查</el-button></el-form></template>
      <el-alert v-if="packet.taskStatus === 'CLOSED'" title="本次调查已结束，外部整改进度仍需在对应流程中跟踪。" type="info" :closable="false" show-icon />
    </el-card>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { PublishedPacketResponse } from '../api/generated'
import { PacketBusinessSummary, summarizeCanonicalPacket } from '../services/business-workflow'
import { getCurrentIdentity } from '../services/identity'
import { closeTask, fetchPublishedPacket, handoffPacket } from '../services/packet-lifecycle-api'

const STATUS_LABELS: Record<string, string> = { PUBLISHED: '已发布', HANDED_OFF: '已交接', CLOSED: '调查已结束' }

export default Vue.extend({
  name: 'PublishedPacket',
  data() { return { packet: null as PublishedPacketResponse | null, activeTab: 'json', saving: false, errorMessage: '', handoff: { ownerReference: '', externalProcessReference: '', downstreamStatus: '待外部处理' }, closure: { closureReason: '' } } },
  computed: {
    /** 判断当前用户能否执行交接和关闭动作。 */
    isOperator(): boolean { return getCurrentIdentity()?.roles.includes('OPERATOR') ?? false },
    /** 从权威结构中提取业务摘要，避免首屏展示原始 JSON。 */
    businessSummary(): PacketBusinessSummary { return this.packet ? summarizeCanonicalPacket(this.packet.canonicalJson) : { currentCount: 0, futureCount: 0, undeterminedCount: 0, findingCount: 0, coverageLabel: '正在读取调查结论', recommendations: [] } },
    /** 把内部任务状态转换为业务文案。 */
    taskStatusLabel(): string { return this.packet ? (STATUS_LABELS[this.packet.taskStatus] || '处理中') : '加载中' },
    /** 将空限制数组转换为业务可读结果。 */
    limitationsLabel(): string { return !this.packet || this.packet.limitations === '[]' ? '无额外限制' : this.packet.limitations },
  },
  watch: { '$route.params.taskId': 'loadPacket' },
  async created(): Promise<void> { await this.loadPacket() },
  methods: {
    /** 读取当前任务最新的不可变发布修订。 */
    async loadPacket(): Promise<void> { this.packet = null; this.errorMessage = ''; try { this.packet = await fetchPublishedPacket(String(this.$route.params.taskId)) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '已发布决策包查询未完成，请稍后重试' } },
    /** 保存后续责任人、处理状态和可选外部单号。 */
    async submitHandoff(): Promise<void> { if (!this.packet || !this.handoff.ownerReference.trim() || !this.handoff.downstreamStatus.trim()) { this.errorMessage = '请填写责任人或团队以及当前处理状态'; return }; await this.execute(async () => { await handoffPacket(this.packet!.taskId, this.packet!.packetRevisionId, { ownerReference: this.handoff.ownerReference, externalProcessReference: this.handoff.externalProcessReference, downstreamStatus: this.handoff.downstreamStatus }); await this.loadPacket() }) },
    /** 记录本次调查结束说明并刷新状态。 */
    async submitClosure(): Promise<void> { if (!this.packet || !this.closure.closureReason.trim()) { this.errorMessage = '请填写结束说明'; return }; await this.execute(async () => { await closeTask(this.packet!.taskId, { closureReason: this.closure.closureReason }); await this.loadPacket() }) },
    /** 统一处理写操作的加载和错误状态。 */
    async execute(action: () => Promise<void>): Promise<void> { this.saving = true; this.errorMessage = ''; try { await action() } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '请求未完成，请稍后重试' } finally { this.saving = false } },
  },
})
</script>
