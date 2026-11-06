<!-- 平台管理员在调度任务页查看 Java 类、调度时间及最新日志，可修改计划或确认后手动执行一次。 -->
<template>
  <section class="entry-page scheduled-jobs-page">
    <div class="page-heading entry-page-heading"><h2>调度任务</h2></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <div v-loading="loading" class="scheduled-jobs-layout">
      <el-card shadow="never" class="entry-content-card scheduled-jobs-list">
        <div class="scheduled-section-title">任务列表</div>
        <el-menu v-if="jobs.length" :default-active="selectedCode" class="scheduled-menu" @select="selectJob">
          <el-menu-item v-for="job in jobs" :key="job.code" :index="job.code">{{ job.name }}</el-menu-item>
        </el-menu>
        <div v-else-if="!loading" class="scheduled-empty">暂无调度任务</div>
      </el-card>
      <el-card shadow="never" class="entry-content-card scheduled-jobs-detail">
        <template v-if="selectedJob">
          <div class="scheduled-config-row">
            <div class="scheduled-class-panel">
              <div class="scheduled-section-title">Java 类</div>
              <code :title="selectedJob.javaClass">{{ javaClassLabel }}</code>
            </div>
            <div class="scheduled-time-panel">
              <div class="scheduled-section-title">执行时间</div>
              <div class="scheduled-time-row">
                <el-select v-model="draftFrequency" aria-label="执行频率" class="scheduled-frequency" @change="clearSaveNotice">
                  <el-option label="每天" value="DAILY" />
                  <el-option label="每周" value="WEEKLY" />
                  <el-option label="每月" value="MONTHLY" />
                </el-select>
                <el-select v-if="draftFrequency === 'WEEKLY'" v-model="draftDay" aria-label="执行星期" class="scheduled-day" @change="clearSaveNotice">
                  <el-option v-for="day in weekDays" :key="day.value" :label="day.label" :value="day.value" />
                </el-select>
                <el-select v-if="draftFrequency === 'MONTHLY'" v-model="draftMonthDay" aria-label="执行日期" class="scheduled-day" @change="clearSaveNotice">
                  <el-option v-for="day in monthDays" :key="day" :label="`每月 ${day} 日`" :value="day" />
                </el-select>
                <el-time-picker v-model="draftTime" aria-label="执行时刻" format="HH:mm" value-format="HH:mm"
                  placeholder="选择时间" class="scheduled-time" @change="clearSaveNotice" />
                <span class="scheduled-zone">北京时间</span>
                <el-button type="primary" :loading="saving" :disabled="!scheduleChanged" @click="saveSchedule">保存</el-button>
                <el-button class="scheduled-run-once" type="warning" :loading="runningNow" :disabled="saving || runningNow" @click="runOnce">立即执行一次</el-button>
              </div>
            </div>
          </div>
          <el-alert v-if="saveError" :title="saveError" type="error" :closable="false" show-icon />
          <el-alert v-if="saveNotice" :title="saveNotice" type="success" :closable="false" show-icon />
          <el-alert v-if="runError" :title="runError" type="error" :closable="false" show-icon />
          <el-alert v-if="runNotice" :title="runNotice" type="success" :closable="false" show-icon />
          <div class="scheduled-section-title scheduled-log-title">最新一次调度日志</div>
          <div v-if="!latestRun" class="scheduled-empty">暂无运行记录</div>
          <template v-else>
            <div class="scheduled-run-meta">
              <div><span>开始时间</span><strong>{{ formatTime(latestRun.startedAt) }}</strong></div>
              <div><span>结束时间</span><strong>{{ formatTime(latestRun.finishedAt) }}</strong></div>
              <div><span>执行结果</span><el-tag :type="latestRun.status === '成功' ? 'success' : 'danger'">{{ latestRun.status }}</el-tag></div>
              <div><span>数据库记录</span><strong>{{ deletedTotal(latestRun) }}</strong></div>
              <div><span>工作区文件</span><strong>{{ latestRun.workspaceFiles }}</strong></div>
              <div><span>临时对象</span><strong>{{ latestRun.objectFiles }}</strong></div>
              <div><span>启动日志</span><strong>{{ latestRun.localLogFiles }}</strong></div>
            </div>
            <el-alert v-if="latestRun.errors.length" :title="latestRun.errors.join('；')" type="error" :closable="false" show-icon />
            <div class="scheduled-section-title scheduled-breakdown-title">各表删除数量</div>
            <div v-if="deletedRows.length" class="scheduled-breakdown">
              <div v-for="row in deletedRows" :key="row.table"><span>{{ tableLabel(row.table) }}</span><strong>{{ row.count }}</strong></div>
            </div>
            <div v-else class="scheduled-empty">本次没有删除数据库记录</div>
          </template>
        </template>
        <div v-else-if="!loading" class="scheduled-empty">请选择调度任务</div>
      </el-card>
    </div>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { executeRetentionNow, fetchRetentionRuns, fetchScheduledJobs, updateRetentionSchedule, RetentionRun, ScheduledJob, ScheduleFrequency } from '../services/retention-api'

const TABLE_LABELS: Record<string, string> = {
  'task.investigation_task': '任务', 'task.scope_revision': '范围修订',
  'task.task_premise': '任务前提', 'task.investigation_step': '调查步骤',
  'task.step_approval': '步骤审批', 'task.task_lease': '任务租约',
  'task.tool_invocation': '工具调用', 'task.task_business_scene_skill': '任务场景关联',
  'task.guidance_draft': '业务初稿', 'task.business_evidence_accum': '证据累积',
  'task.business_decision_packet': '业务决策包', 'task.process_artifact_batch': '产物批次',
  'task.process_artifact_file': '产物文件', 'task.task_attachment': '上传附件',
  'evidence.fact_snapshot': '事实快照', 'evidence.coverage_record': '覆盖记录',
  'evidence.finding': '调查判断', 'evidence.finding_evidence': '判断证据关联',
  'evidence.computation_witness': '计算见证', 'evidence.professional_confirmation': '专业确认',
  'packet.packet_draft': '决策包草稿', 'packet.packet_review': '决策包复核',
  'packet.packet_revision': '已发布修订', 'packet.handoff': '决策包交接',
  'packet.scope_recheck': '范围重查', 'harness.session_state': '会话状态',
  'harness.checkpoint': '会话检查点', 'harness.plan_revision': '计划修订',
  'harness.workspace_ref': '工作区引用', 'governance.audit_event': '审计事件',
  'governance.outbox_event': '待办事件',
}

/** 将已有清理日志页调整为调度任务与最新运行结果的左右分栏。 */
export default Vue.extend({
  name: 'RetentionRuns',
  data: () => ({
    jobs: [] as ScheduledJob[], runs: [] as RetentionRun[], selectedCode: '',
    draftFrequency: 'WEEKLY' as ScheduleFrequency, draftDay: 7, draftMonthDay: 1,
    draftTime: '02:00', loading: false, saving: false, runningNow: false,
    errorMessage: '', saveError: '', saveNotice: '', runError: '', runNotice: '',
    runBaseline: '' as string, runPollAttempts: 0,
    runPollTimer: null as ReturnType<typeof setTimeout> | null,
    weekDays: [
      { value: 1, label: '星期一' }, { value: 2, label: '星期二' }, { value: 3, label: '星期三' },
      { value: 4, label: '星期四' }, { value: 5, label: '星期五' }, { value: 6, label: '星期六' },
      { value: 7, label: '星期日' },
    ],
    monthDays: Array.from({ length: 28 }, (_, index) => index + 1),
  }),
  computed: {
    /** 当前选中任务及其调度设置。 */
    selectedJob(): ScheduledJob | undefined { return this.jobs.find(job => job.code === this.selectedCode) },
    /** 页面显示简单类名，悬停可查看完整包名。 */
    javaClassLabel(): string { return this.selectedJob?.javaClass.split('.').pop() || '' },
    /** 兼容旧日志缺少任务编号的记录，当前旧记录都属于清理任务。 */
    latestRun(): RetentionRun | undefined {
      return this.runs.find(run => (run.jobCode || 'retention-cleanup') === this.selectedCode)
    },
    /** 只列出本次确有删除的表，减少最新日志的视觉噪声。 */
    deletedRows(): { table: string; count: number }[] {
      return Object.entries(this.latestRun?.deleted || {}).filter(([, count]) => count > 0)
        .map(([table, count]) => ({ table, count }))
    },
    /** 只有时间改变且有效时才允许提交。 */
    scheduleChanged(): boolean {
      if (!this.selectedJob || !/^(?:[01]\d|2[0-3]):[0-5]\d$/.test(this.draftTime || '')) return false
      if (this.draftFrequency === 'WEEKLY' && (this.draftDay < 1 || this.draftDay > 7)) return false
      if (this.draftFrequency === 'MONTHLY' && (this.draftMonthDay < 1 || this.draftMonthDay > 28)) return false
      return this.draftFrequency !== this.selectedJob.frequency || this.draftTime !== this.selectedJob.time
        || (this.draftFrequency === 'WEEKLY' && this.draftDay !== this.selectedJob.dayOfWeek)
        || (this.draftFrequency === 'MONTHLY' && this.draftMonthDay !== this.selectedJob.dayOfMonth)
    },
  },
  created() { void this.load() },
  destroyed() { if (this.runPollTimer) clearTimeout(this.runPollTimer) },
  methods: {
    /** 页面进入时读取固定任务列表和有界清理日志。 */
    async load(): Promise<void> {
      this.loading = true
      this.errorMessage = ''
      try {
        const [jobs, runs] = await Promise.all([fetchScheduledJobs(), fetchRetentionRuns()])
        this.jobs = jobs.items
        this.runs = runs.items
        if (this.jobs.length) this.selectJob(this.jobs[0].code)
      } catch (error: unknown) {
        this.jobs = []
        this.runs = []
        this.errorMessage = error instanceof Error ? error.message : '调度任务暂时无法读取'
      } finally { this.loading = false }
    },
    /** 切换左侧任务时重置右侧频率和时间草稿。 */
    selectJob(code: string): void {
      this.selectedCode = code
      const job = this.jobs.find(item => item.code === code)
      if (job) {
        this.draftFrequency = job.frequency
        this.draftDay = job.dayOfWeek || 7
        this.draftMonthDay = job.dayOfMonth || 1
        this.draftTime = job.time
      }
      this.saveError = ''
      this.saveNotice = ''
    },
    /** 用户修改时间字段后清除上次提交成功提示。 */
    clearSaveNotice(): void { this.saveNotice = ''; this.saveError = '' },
    /** 保存当前任务的频率和执行时间；服务端负责最终校验和立即重排。 */
    async saveSchedule(): Promise<void> {
      if (!this.scheduleChanged || this.saving || !this.selectedJob) return
      this.saving = true
      this.saveError = ''
      this.saveNotice = ''
      try {
        const updated = await updateRetentionSchedule({
          frequency: this.draftFrequency,
          dayOfWeek: this.draftFrequency === 'WEEKLY' ? this.draftDay : null,
          dayOfMonth: this.draftFrequency === 'MONTHLY' ? this.draftMonthDay : null,
          time: this.draftTime,
        })
        this.jobs = this.jobs.map(job => job.code === updated.code ? updated : job)
        this.saveNotice = '调度时间已更新'
      } catch (error: unknown) {
        this.saveError = error instanceof Error ? error.message : '调度时间保存失败'
      } finally { this.saving = false }
    },
    /** 管理员确认后提交一次固定清理；未保存的调度草稿不会随请求发送。 */
    async runOnce(): Promise<void> {
      if (!this.selectedJob || this.runningNow || this.saving) return
      try {
        await this.$confirm('将立即清理超过 30 天的业务数据、受控文件和日志，是否继续？', '确认立即执行', {
          confirmButtonText: '立即执行', cancelButtonText: '取消', type: 'warning',
        })
      } catch { return }
      if (this.runningNow || this.saving) return
      this.runningNow = true
      this.runError = ''
      this.runNotice = ''
      this.runBaseline = this.latestRun?.startedAt || ''
      this.runPollAttempts = 0
      try {
        await executeRetentionNow()
        this.runNotice = '清理任务已提交，正在等待运行结果'
        await this.refreshRun()
      } catch (error: unknown) {
        this.runningNow = false
        this.runNotice = ''
        this.runError = error instanceof Error ? error.message : '立即执行提交失败'
      }
    },
    /** 有界轮询最近日志，发现新记录后停止，页面销毁时取消后续请求。 */
    async refreshRun(): Promise<void> {
      try {
        this.runs = (await fetchRetentionRuns()).items
        if (this.latestRun && this.latestRun.startedAt !== this.runBaseline) {
          this.runningNow = false
          this.runNotice = '本次清理已完成，结果见下方日志'
          return
        }
        if (++this.runPollAttempts >= 60) {
          this.runningNow = false
          this.runNotice = '清理任务已提交，请稍后刷新页面查看运行结果'
          return
        }
        this.runPollTimer = setTimeout(() => { void this.refreshRun() }, 2000)
      } catch (error: unknown) {
        this.runningNow = false
        this.runNotice = ''
        this.runError = error instanceof Error ? error.message : '运行日志读取失败，请稍后刷新页面'
      }
    },
    /** 汇总数据库中各表实际删除行数。 */
    deletedTotal(run: RetentionRun): number { return Object.values(run.deleted).reduce((sum, count) => sum + count, 0) },
    /** 使用中文业务名称展示表级计数。 */
    tableLabel(table: string): string { return TABLE_LABELS[table] || '其他记录' },
    /** 按中文地区时间显示服务端 UTC 时间。 */
    formatTime(value: string): string {
      const date = new Date(value)
      return Number.isNaN(date.getTime()) ? '时间无法识别' : date.toLocaleString('zh-CN', { hour12: false })
    },
  },
})
</script>

<style scoped>
.scheduled-jobs-layout { display: grid; grid-template-columns: 248px minmax(0, 1fr); gap: 18px; align-items: start; }
.scheduled-section-title { font-size: 16px; font-weight: 600; color: #253043; margin-bottom: 12px; }
.scheduled-menu { border-right: 0; background: transparent; }
.scheduled-menu ::v-deep .el-menu-item { height: 48px; line-height: 48px; border-radius: 8px; font-size: 15px; }
.scheduled-menu ::v-deep .el-menu-item:hover { background: #fff6e6; color: #1a1b1e; }
.scheduled-menu ::v-deep .el-menu-item.is-active { background: #fff4e2; color: #8a5c05; font-weight: 600; box-shadow: inset 3px 0 0 #f9b334; }
.scheduled-menu ::v-deep .el-menu-item.is-active:hover { background: #ffeed4; color: #8a5c05; }
.scheduled-run-once.el-button--warning:not(.is-disabled) { background: #f9b334; border-color: #f9b334; color: #ffffff; }
.scheduled-run-once.el-button--warning:not(.is-disabled):hover,
.scheduled-run-once.el-button--warning:not(.is-disabled):focus { background: #f7a71f; border-color: #f7a71f; color: #ffffff; }
.scheduled-config-row { display: grid; grid-template-columns: 160px minmax(0, 1fr); gap: 20px; align-items: start; }
.scheduled-class-panel code { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 15px; line-height: 40px; color: #172033; }
.scheduled-time-row { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.scheduled-frequency { width: 104px; }
.scheduled-day { width: 126px; }
.scheduled-time { width: 126px; }
.scheduled-zone { color: #4b5563; font-size: 14px; white-space: nowrap; }
.scheduled-log-title { border-top: 1px solid #e8e2db; padding-top: 20px; margin-top: 24px; }
.scheduled-run-meta { display: grid; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); gap: 18px 22px; margin-bottom: 20px; }
.scheduled-run-meta > div { display: flex; flex-direction: column; gap: 8px; }
.scheduled-run-meta span { color: #667085; font-size: 14px; }
.scheduled-run-meta strong { color: #172033; font-size: 16px; font-weight: 600; }
.scheduled-breakdown-title { margin-top: 28px; }
.scheduled-breakdown { display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 10px 24px; }
.scheduled-breakdown > div { display: flex; justify-content: space-between; gap: 16px; border-bottom: 1px solid #e8e2db; padding: 8px 0; }
.scheduled-breakdown strong { color: #172033; }
.scheduled-empty { padding: 14px 0; color: #667085; }
@media (max-width: 1000px) { .scheduled-config-row { grid-template-columns: 1fr; } }
@media (max-width: 820px) { .scheduled-jobs-layout { grid-template-columns: 1fr; } }
</style>
