<template>
  <section class="evidence-page" aria-labelledby="evidence-title">
    <div class="page-heading"><div><p class="eyebrow">证据与覆盖度</p><h2 id="evidence-title">任务证据链</h2><p class="page-description">当前、未来和未确定结果分开呈现，PARTIAL/UNKNOWN 不会被当作全范围无匹配。</p></div><el-button @click="$router.back()">返回任务</el-button></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="warning" :closable="false" show-icon />
    <el-tabs v-else v-model="activeTab"><el-tab-pane label="当前/未来事实" name="snapshots"><el-table :data="overview.snapshots" stripe><el-table-column prop="sourceObjectType" label="对象类型" /><el-table-column prop="sourceObjectKey" label="对象标识" /><el-table-column prop="effectiveClass" label="生效分类" /><el-table-column prop="checkedAt" label="checked_at" /><el-table-column prop="sourceVersion" label="来源版本" /><el-table-column prop="resultDigest" label="结果摘要" /></el-table><el-empty v-if="!overview.snapshots.length" description="暂无事实快照" /></el-tab-pane><el-tab-pane label="覆盖度" name="coverage"><el-table :data="overview.coverages" stripe><el-table-column prop="completeness" label="覆盖状态" /><el-table-column prop="traversedPaths" label="已遍历路径" /><el-table-column prop="uncoveredPaths" label="路径缺口" /><el-table-column prop="observedTotal" label="已观察数量" /><el-table-column prop="resultSetDigest" label="结果摘要" /></el-table><el-empty v-if="!overview.coverages.length" description="暂无覆盖记录" /></el-tab-pane><el-tab-pane label="发现与见证" name="findings"><el-card v-for="finding in overview.findings" :key="finding.findingId" shadow="never" class="finding-card"><div class="finding-heading"><el-tag>{{ finding.effectiveClass }}</el-tag><el-tag type="info">{{ finding.status }}</el-tag><span>{{ finding.findingType }}</span></div><p>{{ finding.statement }}</p><dl v-if="finding.witness" class="witness-summary"><dt>算法版本</dt><dd>{{ finding.witness.algorithmVersion }}</dd><dt>规则版本</dt><dd>{{ finding.witness.ruleSetVersion }}</dd><dt>复算状态</dt><dd>{{ finding.witness.recomputeStatus }}</dd><dt>输入摘要</dt><dd class="mono">{{ finding.witness.inputDigest }}</dd><dt>输出摘要</dt><dd class="mono">{{ finding.witness.outputDigest }}</dd></dl><el-alert v-else title="该判断暂无计算见证" type="warning" :closable="false" /></el-card><el-empty v-if="!overview.findings.length" description="暂无发现" /></el-tab-pane></el-tabs>
  </section>
</template>
<script lang="ts">
import Vue from 'vue'
import { EvidenceOverview as EvidenceOverviewType } from '../api/generated'
import { fetchEvidenceOverview } from '../services/evidence-api'

export default Vue.extend({
  name: 'EvidenceOverview',
  data() { return { overview: { taskId: '', snapshots: [], coverages: [], findings: [] } as EvidenceOverviewType, activeTab: 'snapshots', errorMessage: '' } },
  async created() { await this.loadOverview() },
  methods: {
    /** 加载当前任务证据链并将越权或失败转为中文提示。 */
    async loadOverview(): Promise<void> { try { this.overview = await fetchEvidenceOverview(String(this.$route.params.taskId)) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '证据查询未完成，请稍后重试' } },
  },
})
</script>
