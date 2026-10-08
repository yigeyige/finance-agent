<!-- 决策包业务页面，从现有证据自动组装技术结构，只向业务人员呈现确认、复核与发布动作。 -->
<template>
  <section class="packet-page" aria-labelledby="packet-title">
    <div class="page-heading"><div><p class="eyebrow">调查结论</p><h2 id="packet-title">形成业务决策包</h2><p class="page-description">系统会把证据、覆盖度和判断自动整理成决策包，你只需核对业务含义。</p></div><el-button @click="$router.push(`/tasks/${$route.params.taskId}/evidence`)">返回调查证据</el-button></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-card v-if="!result" shadow="never" class="packet-form-card guided-workflow">
      <div class="step-intro"><span class="step-intro__number">1</span><div><h3>核对系统汇总的调查结果</h3><p>技术数据已从当前任务和证据中自动准备，你只需确认业务内容。</p></div></div>
      <div v-if="evidence" class="business-summary-grid">
        <article><strong>{{ evidence.snapshots.length }}</strong><span>事实快照</span></article>
        <article><strong>{{ evidence.coverages.length }}</strong><span>覆盖记录</span></article>
        <article><strong>{{ evidence.findings.length }}</strong><span>调查结论</span></article>
      </div>
      <div v-if="evidence && evidence.findings.length" class="finding-list"><h4>规则和证据形成的判断</h4><ul><li v-for="finding in evidence.findings" :key="finding.findingId">{{ finding.statement }}</li></ul></div>
      <div v-else-if="evidence" class="empty-state" role="status">当前还没有可用于决策包的调查结论，请先完成证据采集。</div>
      <div v-if="evidence && evidence.findings.length" class="model-stage" :class="{ 'model-stage--ready': modelAnalysis }">
        <div class="model-stage__heading"><div><p class="eyebrow">大模型交互</p><h4>{{ modelLoading ? '正在分析证据' : modelAnalysis ? '模型分析已完成' : '让模型解释这些证据' }}</h4></div><el-tag v-if="modelAnalysis" :type="modelAnalysis.metadata.mode === 'LIVE' ? 'success' : 'info'">{{ modelAnalysis.metadata.mode === 'LIVE' ? modelAnalysis.metadata.modelName : '本地模拟模型' }}</el-tag></div>
        <p v-if="modelLoading" class="model-stage__status">模型正在阅读脱敏事实分类、覆盖状态和规则判断，不会接收未授权原始字段。</p>
        <template v-else-if="modelAnalysis"><p class="model-summary">{{ modelAnalysis.summary }}</p><div class="risk-summary"><el-tag :type="riskTagType">{{ riskLabel }}</el-tag><span>{{ modelAnalysis.riskExplanation }}</span></div><div class="finding-list"><h4>模型建议</h4><ul><li v-for="recommendation in modelAnalysis.recommendations" :key="recommendation">{{ recommendation }}</li></ul></div><el-alert title="模型只提供建议，仍需你核对业务影响后才能生成草稿。" type="warning" :closable="false" show-icon /></template>
        <el-button v-else type="primary" :loading="modelLoading" @click="analyzeEvidence">开始模型分析</el-button>
      </div>
      <el-form v-if="modelAnalysis" label-position="top" class="business-confirmation-form">
        <el-form-item label="本次决策包要回答的问题"><el-input v-model.trim="problem" :maxlength="2000" type="textarea" :rows="3" placeholder="例如：确认当前仍生效的配置及需要后续处理的事项" /></el-form-item>
        <el-checkbox v-model="impactConfirmed" class="impact-confirmation">我已核对上述事实和结论，确认其业务影响可以进入复核</el-checkbox>
        <div class="form-actions"><el-button type="primary" :loading="saving" :disabled="!evidence" @click="assemble">生成决策包草稿</el-button></div>
      </el-form>
    </el-card>
    <el-card v-else shadow="never" class="packet-result-card">
      <div class="result-heading"><div><p class="eyebrow">草稿已形成</p><h3>请复核业务结论</h3></div><el-tag type="success">待复核</el-tag></div>
      <div v-if="prepared" class="business-summary-grid business-summary-grid--wide"><article><strong>{{ prepared.summary.currentCount }}</strong><span>当前生效</span></article><article><strong>{{ prepared.summary.futureCount }}</strong><span>未来生效</span></article><article><strong>{{ prepared.summary.undeterminedCount }}</strong><span>待确定</span></article><article><strong>{{ prepared.summary.findingCount }}</strong><span>建议事项</span></article></div>
      <el-alert v-if="prepared" :title="prepared.summary.coverageLabel" type="success" :closable="false" show-icon />
      <div v-if="prepared && prepared.summary.recommendations.length" class="finding-list"><h4>建议关注</h4><ul><li v-for="recommendation in prepared.summary.recommendations" :key="recommendation">{{ recommendation }}</li></ul></div>
      <el-collapse class="technical-details"><el-collapse-item title="查看技术明细"><p class="panel-hint">以下内容供技术排障和审计使用，业务复核无需编辑。</p><dl class="task-summary"><dt>内容摘要</dt><dd class="mono">{{ result.contentDigest }}</dd><dt>草稿编号</dt><dd>{{ result.draftNo }}</dd></dl><el-tabs v-model="activeTab"><el-tab-pane label="结构化数据" name="json"><pre class="packet-json">{{ result.canonicalJson }}</pre></el-tab-pane><el-tab-pane label="归档快照" name="html"><iframe title="决策包归档快照" class="packet-preview" :srcdoc="result.htmlSnapshot" /></el-tab-pane></el-tabs></el-collapse-item></el-collapse>
      <h4 class="section-heading">第 2 步：复核结论</h4><el-alert title="批准表示你确认当前草稿准确反映了调查结果；退回不会发布。" type="info" :closable="false" show-icon />
      <el-form v-if="!reviewResult" label-position="top" class="review-form"><el-form-item label="复核决定"><el-radio-group v-model="review.decision"><el-radio label="APPROVE">批准进入发布</el-radio><el-radio label="REJECT">退回修改</el-radio></el-radio-group></el-form-item><el-form-item label="复核意见"><el-input v-model.trim="review.comment" type="textarea" :rows="3" :maxlength="2000" placeholder="说明批准依据或需要修改的内容" /></el-form-item><el-button type="primary" :loading="reviewSaving" @click="submitReview">提交复核</el-button></el-form>
      <div v-else class="next-action-panel"><div><strong>{{ reviewResult.decision === 'APPROVE' ? '复核已批准' : '草稿已退回' }}</strong><p>{{ reviewResult.comment || '未填写复核意见' }}</p></div><el-tag :type="reviewResult.decision === 'APPROVE' ? 'success' : 'warning'">{{ reviewResult.decision === 'APPROVE' ? '可发布' : '需修改' }}</el-tag></div>
      <template v-if="reviewResult && reviewResult.decision === 'APPROVE'">
        <h4 class="section-heading">第 3 步：发布决策包</h4><el-alert title="发布前系统会重新读取当前证据并检查是否发生变化；有变化会自动阻止发布。" type="warning" :closable="false" show-icon />
        <div v-if="!publicationResult" class="publish-confirmation"><div><strong>发布后将形成不可变修订</strong><p>来源版本、覆盖摘要和重查快照均由系统自动准备。</p></div><el-button type="primary" :loading="publicationSaving" @click="submitPublication">确认发布</el-button></div>
        <div v-else class="publication-result"><el-tag :type="publicationResult.published ? 'success' : 'warning'">{{ publicationResult.published ? '发布成功' : '证据有变化，已停止发布' }}</el-tag><p v-if="publicationResult.recheckDecision">系统重查结果：{{ publicationResult.recheckDecision }}</p><el-table v-if="publicationResult.diffs.length" :data="publicationResult.diffs"><el-table-column prop="objectKey" label="发生变化的对象" /><el-table-column prop="changes" label="变化内容" /></el-table><el-button v-if="publicationResult.published" type="primary" @click="$router.push(`/tasks/${$route.params.taskId}/published`)">查看已发布决策包</el-button></div>
      </template>
    </el-card>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { EvidenceOverview, PacketDraftResponse, PacketPublicationResponse, PacketReviewResponse } from '../api/generated'
import { PreparedPacket, preparePacketFromEvidence } from '../services/business-workflow'
import { fetchEvidenceOverview } from '../services/evidence-api'
import { analyzeEvidenceWithModel, ModelEvidenceAnalysisResponse } from '../services/model-api'
import { assemblePacketDraft, publishPacket, reviewPacketDraft } from '../services/packet-api'

export default Vue.extend({
  name: 'PacketDraft',
  data() { return { saving: false, modelLoading: false, reviewSaving: false, publicationSaving: false, errorMessage: '', evidence: null as EvidenceOverview | null, modelAnalysis: null as ModelEvidenceAnalysisResponse | null, prepared: null as PreparedPacket | null, result: null as PacketDraftResponse | null, reviewResult: null as PacketReviewResponse | null, publicationResult: null as PacketPublicationResponse | null, problem: '核查已确认范围内的异常并形成可复核结论', impactConfirmed: false, review: { decision: 'APPROVE' as 'APPROVE' | 'REJECT', comment: '' }, publicationId: '', activeTab: 'json' } },
  computed: {
    /** 把模型风险等级转换为业务可见中文。 */
    riskLabel(): string { return this.modelAnalysis ? ({ LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险' }[this.modelAnalysis.riskLevel] || '待判断') : '待判断' },
    /** 为风险等级选择同时有文字说明的视觉状态。 */
    riskTagType(): string { return this.modelAnalysis?.riskLevel === 'HIGH' ? 'danger' : this.modelAnalysis?.riskLevel === 'MEDIUM' ? 'warning' : 'success' },
  },
  watch: { '$route.params.taskId': 'resetTaskState' },
  async created(): Promise<void> { await this.loadEvidence() },
  methods: {
    /** 清空上一任务的数据并读取当前任务证据。 */
    async resetTaskState(): Promise<void> { this.modelAnalysis = null; this.prepared = null; this.result = null; this.reviewResult = null; this.publicationResult = null; this.publicationId = ''; this.errorMessage = ''; await this.loadEvidence() },
    /** 查询自动组装所需的脱敏证据。 */
    async loadEvidence(): Promise<void> { this.evidence = null; try { this.evidence = await fetchEvidenceOverview(String(this.$route.params.taskId)) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '调查证据暂时无法读取，请稍后重试' } },
    /** 请求模型仅分析后端加载的脱敏证据摘要。 */
    async analyzeEvidence(): Promise<void> { if (!this.evidence || this.modelLoading) return; this.modelLoading = true; this.modelAnalysis = null; this.errorMessage = ''; try { this.modelAnalysis = await analyzeEvidenceWithModel(String(this.$route.params.taskId)) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '模型暂时无法分析证据，请稍后重试' } finally { this.modelLoading = false } },
    /** 从当前证据和已核对模型建议自动生成严格草稿请求。 */
    async assemble(): Promise<void> { if (!this.evidence || !this.modelAnalysis || !this.problem.trim()) { this.errorMessage = '请先完成模型分析并填写决策包要回答的问题'; return }; if (!this.impactConfirmed) { this.errorMessage = '请先确认已核对事实、模型建议和业务影响'; return }; try { this.saving = true; this.errorMessage = ''; this.prepared = preparePacketFromEvidence(this.evidence, this.problem, true, this.modelAnalysis); this.result = await assemblePacketDraft(String(this.$route.params.taskId), this.prepared.payload) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '决策包草稿未生成，请先完成调查证据' } finally { this.saving = false } },
    /** 提交绑定当前草稿摘要的业务复核决定。 */
    async submitReview(): Promise<void> { if (!this.result) return; try { this.reviewSaving = true; this.errorMessage = ''; this.reviewResult = await reviewPacketDraft(this.result.taskId, this.result.draftId, { contentDigest: this.result.contentDigest, scopeRevisionId: this.result.scopeRevisionId, runtimeBundleId: this.result.runtimeBundleId, decision: this.review.decision, comment: this.review.comment }) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '草稿复核未完成，请检查专业确认状态' } finally { this.reviewSaving = false } },
    /** 重新读取证据并自动提交发布前全范围重查。 */
    async submitPublication(): Promise<void> { if (!this.result || !this.reviewResult || !this.prepared) return; try { this.publicationSaving = true; this.errorMessage = ''; const currentEvidence = await fetchEvidenceOverview(this.result.taskId); const current = preparePacketFromEvidence(currentEvidence, this.problem, true); if (!this.publicationId) this.publicationId = typeof crypto.randomUUID === 'function' ? crypto.randomUUID() : `revision-${Date.now()}`; this.publicationResult = await publishPacket(this.result.taskId, { draftId: this.result.draftId, publicationId: this.publicationId, contentDigest: this.result.contentDigest, scopeRevisionId: this.result.scopeRevisionId, runtimeBundleId: this.result.runtimeBundleId, canonicalJson: this.result.canonicalJson, htmlSnapshot: this.result.htmlSnapshot, sourceVersions: current.sourceVersions, coverageSummary: current.coverageSummary, baseline: this.prepared.baseline, current: current.baseline }) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '发布前检查未完成，请稍后重试' } finally { this.publicationSaving = false } },
  },
})
</script>
