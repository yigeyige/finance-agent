<template>
  <section class="evidence-page" aria-labelledby="diagnostics-title">
    <div class="page-heading">
      <div><p class="eyebrow">任务诊断</p><h2 id="diagnostics-title">运行状态与恢复提示</h2><p class="page-description">仅展示脱敏摘要；页面不执行暂停、恢复、取消、重试或人工对账动作。</p></div>
      <el-button @click="$router.push('/')">返回任务</el-button>
    </div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-card v-if="diagnostics" shadow="never">
      <div class="result-heading"><div><h3>任务状态：{{ diagnostics.taskStatus }}</h3><p class="mono">{{ diagnostics.taskId }}</p></div><el-tag :type="isPaused ? 'warning' : 'info'">{{ statusLabel }}</el-tag></div>
      <el-row :gutter="16"><el-col :span="8"><el-card shadow="never"><p class="eyebrow">当前步骤</p><template v-if="diagnostics.currentStep"><p>步骤 {{ diagnostics.currentStep.sequenceNo }}：{{ diagnostics.currentStep.goal }}</p><el-tag>{{ diagnostics.currentStep.status }}</el-tag></template><div v-else class="empty-state" role="status">暂无当前步骤</div></el-card></el-col><el-col :span="8"><el-card shadow="never"><p class="eyebrow">租约</p><p>{{ diagnostics.lease.status }}</p><p v-if="diagnostics.lease.expiresAt">到期：{{ diagnostics.lease.expiresAt }}</p><p v-if="diagnostics.lease.heartbeatAt">心跳：{{ diagnostics.lease.heartbeatAt }}</p></el-card></el-col><el-col :span="8"><el-card shadow="never"><p class="eyebrow">审批有效性</p><el-tag :type="diagnostics.approvalValid ? 'success' : 'warning'">{{ diagnostics.approvalValid ? '有效' : '无有效审批' }}</el-tag></el-card></el-col></el-row>
      <h4 class="section-heading">暂停、错误与动作</h4>
      <dl class="description-grid">
        <div><dt>暂停原因</dt><dd>{{ diagnostics.pauseReason || '无' }}</dd></div>
        <div><dt>最后错误码</dt><dd class="mono">{{ diagnostics.lastErrorCode || '无' }}</dd></div>
        <div><dt>人工对账</dt><dd>{{ diagnostics.manualReconciliationRequired ? '需要人工对账' : '不需要' }}</dd></div>
        <div><dt>可执行恢复动作</dt><dd><el-tag v-for="action in diagnostics.recoveryActions" :key="action" type="info">{{ action }}</el-tag><span v-if="!diagnostics.recoveryActions.length">无</span></dd></div>
      </dl>
      <el-alert v-if="diagnostics.manualReconciliationRequired" title="存在未知工具结果，必须由人工对账后再决定下一步。" type="warning" :closable="false" show-icon />
    </el-card>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { TaskDiagnosticResponse } from '../api/generated'
import { fetchTaskDiagnostics } from '../services/task-diagnostic-api'

export default Vue.extend({
  name: 'TaskDiagnostics',
  data() { return { diagnostics: null as TaskDiagnosticResponse | null, errorMessage: '' } },
  computed: {
    isPaused(): boolean { return this.diagnostics?.taskStatus === 'PAUSED_MODEL_UNAVAILABLE' || this.diagnostics?.taskStatus === 'PAUSED_SOURCE_UNAVAILABLE' || this.diagnostics?.taskStatus === 'PAUSED_BUDGET' || this.diagnostics?.taskStatus === 'FAILED_RECOVERABLE' },
    statusLabel(): string { if (!this.diagnostics) return '加载中'; if (this.diagnostics.manualReconciliationRequired) return '需人工对账'; if (this.isPaused) return '已暂停或可恢复失败'; return '运行状态' },
  },
  watch: { '$route.params.taskId': 'loadDiagnostics' },
  async created(): Promise<void> { await this.loadDiagnostics() },
  methods: {
    async loadDiagnostics(): Promise<void> { this.diagnostics = null; this.errorMessage = ''; try { this.diagnostics = await fetchTaskDiagnostics(String(this.$route.params.taskId)) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '任务诊断查询未完成，请稍后重试' } },
  },
})
</script>
