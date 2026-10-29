<!-- 业务工作区页面，复用公共表格与弹窗交互，保持既有业务流程。 -->
<template>
  <section class="evidence-page" aria-labelledby="evidence-title">
    <div class="page-heading"><div><p class="eyebrow">调查证据</p><h2 id="evidence-title">系统查到了什么</h2><p class="page-description">事实按当前生效、未来生效和待确定分类；覆盖不完整时会明确提示需要补查。</p></div><div><el-button type="primary" :disabled="!overview.taskId" @click="$router.push(`/tasks/${$route.params.taskId}/packet-draft`)">形成业务决策包</el-button><el-button @click="$router.push('/')">返回任务</el-button></div></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="warning" :closable="false" show-icon />
    <el-tabs v-else v-model="activeTab"><el-tab-pane label="查到的事实" name="snapshots"><el-table v-workbench-table="'EvidenceOverview-1'" border :data="overview.snapshots" stripe><el-table-column show-overflow-tooltip min-width="160" label="对象类型"><template slot-scope="scope">{{ objectTypeLabel(scope.row.sourceObjectType) }}</template></el-table-column><el-table-column show-overflow-tooltip min-width="160" prop="sourceObjectKey" label="对象编号" /><el-table-column show-overflow-tooltip min-width="160" label="生效情况"><template slot-scope="scope">{{ effectiveLabel(scope.row.effectiveClass) }}</template></el-table-column><el-table-column show-overflow-tooltip min-width="160" prop="checkedAt" label="核查时间" /></el-table><div v-if="!overview.snapshots.length" class="empty-state" role="status">暂未查到事实</div></el-tab-pane><el-tab-pane label="核查完整度" name="coverage"><el-table v-workbench-table="'EvidenceOverview-2'" border :data="overview.coverages" stripe><el-table-column show-overflow-tooltip min-width="160" label="核查状态"><template slot-scope="scope">{{ coverageLabel(scope.row.completeness) }}</template></el-table-column><el-table-column show-overflow-tooltip min-width="160" prop="traversedPaths" label="已核查内容" /><el-table-column show-overflow-tooltip min-width="160" prop="uncoveredPaths" label="尚未覆盖" /><el-table-column show-overflow-tooltip min-width="160" prop="observedTotal" label="已核查数量" /></el-table><div v-if="!overview.coverages.length" class="empty-state" role="status">暂无完整度记录</div></el-tab-pane><el-tab-pane label="调查结论" name="findings"><el-card v-for="finding in overview.findings" :key="finding.findingId" shadow="never" class="finding-card"><div class="finding-heading"><el-tag>{{ effectiveLabel(finding.effectiveClass) }}</el-tag><el-tag type="info">{{ findingStatusLabel(finding.status) }}</el-tag><span>{{ finding.findingType }}</span></div><p>{{ finding.statement }}</p><el-collapse v-if="finding.witness" class="technical-details"><el-collapse-item title="查看计算依据"><dl class="witness-summary"><dt>算法版本</dt><dd>{{ finding.witness.algorithmVersion }}</dd><dt>规则版本</dt><dd>{{ finding.witness.ruleSetVersion }}</dd><dt>复算状态</dt><dd>{{ finding.witness.recomputeStatus }}</dd><dt>输入摘要</dt><dd class="mono">{{ finding.witness.inputDigest }}</dd><dt>输出摘要</dt><dd class="mono">{{ finding.witness.outputDigest }}</dd></dl></el-collapse-item></el-collapse><el-alert v-else-if="finding.judgmentMode === 'DETERMINISTIC'" title="该判断缺少计算依据，暂不能发布" type="warning" :closable="false" /><div v-if="finding.confirmation" class="confirmation-summary"><p>专业确认：{{ confirmationLabel(finding.confirmation.decision) }}，确认时间：{{ finding.confirmation.confirmedAt }}</p><p>理由：{{ finding.confirmation.rationale }}</p></div><el-form v-else-if="finding.judgmentMode !== 'DETERMINISTIC'" label-position="top" class="confirmation-form"><el-form-item label="专业决定"><el-select v-model="drafts[finding.findingId].decision"><el-option label="确认" value="CONFIRM" /><el-option label="拒绝" value="REJECT" /><el-option label="需要更多证据" value="NEEDS_MORE_EVIDENCE" /></el-select></el-form-item><el-form-item label="判断理由"><el-input v-model.trim="drafts[finding.findingId].rationale" type="textarea" :rows="3" :maxlength="2000" placeholder="说明为什么确认、拒绝或需要补充证据" /></el-form-item><el-alert :title="`系统会自动关联当前 ${overview.snapshots.length} 条事实证据`" type="info" :closable="false" /><el-button type="primary" :loading="submitting === finding.findingId" @click="submitConfirmation(finding)">提交专业确认</el-button></el-form></el-card><div v-if="!overview.findings.length" class="empty-state" role="status">暂未形成调查结论</div></el-tab-pane></el-tabs>
  </section>
</template>
<script lang="ts">
import Vue from 'vue'
import { EvidenceOverview as EvidenceOverviewType } from '../api/generated'
import { confirmFinding, fetchEvidenceOverview } from '../services/evidence-api'
import { EvidenceFinding } from '../api/generated'

export default Vue.extend({
  name: 'EvidenceOverview',
  data() { return { overview: { taskId: '', snapshots: [], coverages: [], findings: [] } as EvidenceOverviewType, activeTab: 'snapshots', errorMessage: '', submitting: '', drafts: {} as Record<string, { decision: 'CONFIRM' | 'REJECT' | 'NEEDS_MORE_EVIDENCE'; rationale: string }> } },
  watch: { '$route.params.taskId': 'loadOverview' },
  async created() { await this.loadOverview() },
  methods: {
    /** 加载当前任务证据链并将越权或失败转为中文提示。 */
    async loadOverview(): Promise<void> { this.overview = { taskId: '', snapshots: [], coverages: [], findings: [] }; this.drafts = {}; this.errorMessage = ''; try { this.overview = await fetchEvidenceOverview(String(this.$route.params.taskId)); this.overview.findings.forEach((finding) => { if (!this.drafts[finding.findingId]) this.$set(this.drafts, finding.findingId, { decision: 'CONFIRM', rationale: '' }) }) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '证据查询未完成，请稍后重试' } },
    /** 保存专业判断，并由系统自动关联当前事实证据。 */
    async submitConfirmation(finding: EvidenceFinding): Promise<void> { const draft = this.drafts[finding.findingId]; if (!draft || !draft.rationale.trim()) { this.errorMessage = '请填写专业决定和判断理由'; return }; const evidenceRefs = this.overview.snapshots.map((snapshot) => snapshot.snapshotId).join(','); if (!evidenceRefs) { this.errorMessage = '当前没有可关联的事实证据'; return }; this.submitting = finding.findingId; this.errorMessage = ''; try { await confirmFinding(this.overview.taskId, finding.findingId, { scopeRevisionId: finding.scopeRevisionId, decision: draft.decision, rationale: draft.rationale, evidenceRefs }); await this.loadOverview() } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '专业确认未完成，请稍后重试' } finally { this.submitting = '' } },
    /** 把事实生效分类转换为业务文案。 */
    effectiveLabel(value: string): string { return ({ CURRENT_EFFECTIVE: '当前生效', FUTURE_SCHEDULED: '未来生效', UNDETERMINED: '待确定' } as Record<string, string>)[value] || '待确定' },
    /** 把来源对象类型转换为业务文案。 */
    objectTypeLabel(value: string): string { return ({ synthetic_reference: '配置引用', workflow: '审批流程', role: '角色权限', accounting: '会计配置' } as Record<string, string>)[value] || '业务配置' },
    /** 把覆盖状态转换为业务文案。 */
    coverageLabel(value: string): string { return ({ COMPLETE: '已完整核查', PARTIAL: '部分核查', UNKNOWN: '范围待确认' } as Record<string, string>)[value] || '范围待确认' },
    /** 把结论状态转换为业务文案。 */
    findingStatusLabel(value: string): string { return ({ DRAFT: '待确认', CONFIRMED: '已确认', REJECTED: '已拒绝' } as Record<string, string>)[value] || '处理中' },
    /** 把专业确认决定转换为业务文案。 */
    confirmationLabel(value: string): string { return ({ CONFIRM: '已确认', REJECT: '已拒绝', NEEDS_MORE_EVIDENCE: '需要更多证据' } as Record<string, string>)[value] || '待确认' },
  },
})
</script>
