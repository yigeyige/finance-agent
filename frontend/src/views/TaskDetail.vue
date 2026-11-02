<!-- 只读展示任务三步：业务说明、模型协作与决策包，与新建业务处理布局一致，但隐藏聊天框与操作按钮，仅步骤可切换。 -->
<template>
  <section class="task-workbench task-detail-page" :class="{ 'task-detail-page--scrollable': detail && activeStep !== 0 }" v-loading="loading">
    <div class="page-heading entry-page-heading">
      <div><h2>{{ detail ? detail.title : '业务处理详情' }}</h2></div>
      <el-button @click="$router.push('/tasks')">返回任务列表</el-button>
    </div>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />

    <template v-else-if="detail">
      <div class="workflow-steps-bar">
        <el-steps :active="activeStep" simple>
          <el-step title="业务说明" @click.native="activeStep = 0" />
          <el-step title="模型协作" @click.native="activeStep = 1" />
          <el-step title="决策包" @click.native="activeStep = 2" />
        </el-steps>
      </div>

      <el-card shadow="never" class="form-card guided-workflow detail-content-card">
        <div class="task-detail-content" tabindex="0" aria-label="任务详情内容">
        <!-- 第一步：业务说明只读信息。 -->
        <section v-if="activeStep === 0" class="readonly-intake-panel">
          <div class="card-heading"><h4>业务说明</h4></div>
          <el-form :model="detail" label-position="top" class="task-form readonly-intake-form">
            <el-form-item label="业务标题" class="readonly-intake-form__wide"><div class="readonly-field">{{ detail.title }}</div></el-form-item>
            <el-form-item label="业务说明" class="readonly-intake-form__wide"><div class="readonly-field readonly-field--multiline">{{ detail.description }}</div></el-form-item>
            <el-form-item label="业务场景"><div class="readonly-field">{{ detail.businessSceneName || '—' }}</div></el-form-item>
            <el-form-item label="数据库实例"><div class="readonly-field">{{ detail.databaseInstanceName || '—' }}</div></el-form-item>
            <el-form-item label="发起人"><div class="readonly-field">{{ detail.initiatorName }}</div></el-form-item>
            <el-form-item label="处理状态"><div class="readonly-field">{{ statusLabel(detail.status) }}</div></el-form-item>
            <el-form-item label="创建时间" class="readonly-intake-form__wide"><div class="readonly-field">{{ formatTime(detail.createdAt) }}</div></el-form-item>
          </el-form>
        </section>

        <!-- 第二步：检查结论与关联证据。 -->
        <section v-else-if="activeStep === 1" class="packet-grid detail-guidance-grid">
          <el-card shadow="never" class="packet-card">
            <div slot="header" class="packet-card__header">检查结论</div>
            <p v-if="!detail.conclusion" class="readonly-empty">尚未生成检查结论</p>
            <p v-else class="packet-conclusion guidance-conclusion">{{ guidanceConclusionText }}</p>
          </el-card>
          <el-card shadow="never" class="packet-card">
            <div slot="header" class="packet-card__header">关联证据</div>
            <template v-if="detail.evidence.length">
              <div v-for="(item, ei) in detail.evidence" :key="ei" class="evidence-item">
                <p class="evidence-meta">{{ item.schema }}.{{ item.table }} · {{ item.purpose }}</p>
                <el-table v-workbench-table="'TaskDetail-1:' + item.schema + '.' + item.table" :data="evidenceRowObjects(item)" border size="mini">
                  <el-table-column show-overflow-tooltip min-width="160" v-for="(col, ci) in item.columns" :key="ci" :prop="String(ci)" :label="col" />
                </el-table>
              </div>
            </template>
            <p v-else class="readonly-empty">暂无关联证据</p>
          </el-card>
        </section>

        <!-- 第三步：业务决策包。 -->
        <section v-else class="guidance-result-panel">
          <div class="card-heading packet-heading">
            <h4>业务决策包</h4>
            <el-button v-if="showArtifactButton" type="primary" plain @click="openArtifactDialog">流程产物</el-button>
          </div>
          <el-alert v-if="artifactAvailabilityError" :title="artifactAvailabilityError" type="warning" :closable="false" show-icon />
          <p v-if="!detail.decisionPacket" class="readonly-empty">尚未生成业务决策包</p>
          <template v-else>
            <!-- 四个独立卡片紧凑排列：数据对象、操作、结论、证据。 -->
            <div class="packet-grid">
              <el-card shadow="never" class="packet-card packet-card--object">
                <div slot="header" class="packet-card__header">数据对象</div>
                <div class="packet-object">
                  <span class="packet-object__item"><span class="packet-object__label">处理对象 <span class="field-path">{{ fieldAnnotation('subjectKey') }}</span></span><span class="packet-object__value">{{ detail.decisionPacket.subjectKey || '—' }}</span></span>
                  <span class="packet-object__item"><span class="packet-object__label">数据库实例</span><span class="packet-object__value">{{ detail.decisionPacket.databaseInstanceName || '—' }}</span></span>
                </div>
              </el-card>

              <el-card shadow="never" class="packet-card packet-card--action">
                <div slot="header" class="packet-card__header">操作</div>
                <div class="packet-recommend__goal">
                  <el-tag type="primary" effect="dark" size="small">推荐</el-tag> <span class="field-path">{{ fieldAnnotation('recommendedGoal') }}</span>
                  <p>{{ detail.decisionPacket.recommendedGoal || '暂无' }}</p>
                </div>
                <p v-if="detail.decisionPacket.reason" class="packet-recommend__reason">推荐理由 <span class="field-path">{{ fieldAnnotation('reason') }}</span>：{{ detail.decisionPacket.reason }}</p>
                <ul v-if="detail.decisionPacket.alternativeGoals.length" class="packet-alternatives">
                  <li v-for="(goal, gi) in detail.decisionPacket.alternativeGoals" :key="gi">
                    <span class="packet-alternatives__index">{{ gi + 1 }}</span>
                    <span>{{ goal }}</span>
                  </li>
                </ul>
              </el-card>

              <el-card shadow="never" class="packet-card packet-card--conclusion">
                <div slot="header" class="packet-card__header">结论 <span class="field-path">{{ fieldAnnotation('conclusion') }}</span> <span class="packet-card__revision">修订 {{ detail.decisionPacket.revision }}</span></div>
                <p class="packet-conclusion">{{ conclusionText }}</p>
              </el-card>

              <el-card v-if="detail.decisionPacket.evidence.length" shadow="never" class="packet-card packet-card--evidence">
                <div slot="header" class="packet-card__header">证据 <span class="field-path">{{ fieldAnnotation('evidence') }}</span></div>
                <div v-for="(item, ei) in detail.decisionPacket.evidence" :key="ei" class="evidence-item">
                  <p class="evidence-meta">{{ item.schema }}.{{ item.table }} · {{ item.purpose }}</p>
                  <el-table v-workbench-table="'TaskDetail-2:' + item.schema + '.' + item.table" :data="evidenceRowObjects(item)" border size="mini">
                    <el-table-column show-overflow-tooltip min-width="160" v-for="(col, ci) in item.columns" :key="ci" :prop="String(ci)" :label="col" />
                  </el-table>
                </div>
              </el-card>
            </div>
          </template>
        </section>
        </div>
      </el-card>

      <el-dialog title="流程产物" :visible.sync="artifactDialogVisible" width="760px" append-to-body>
        <div v-loading="artifactLoading" class="artifact-dialog">
          <p v-if="!artifactLoading && !artifactBatches.length" class="readonly-empty">暂无流程产物</p>
          <el-card v-for="batch in artifactBatches" :key="batch.batchId" shadow="never" class="artifact-batch">
            <div class="artifact-batch__header">
              <strong>批次 {{ batch.batchId.slice(0, 8) }}</strong>
              <span>决策包修订 {{ batch.packetRevision }}</span>
              <el-tag :type="batch.importStatus === 'IMPORT_CANDIDATE' ? 'success' : 'warning'">
                {{ batch.importStatus === 'IMPORT_CANDIDATE' ? '可作为导入候选' : '结构待修复' }}
              </el-tag>
            </div>
            <p class="artifact-meta">生成时间：{{ formatTime(batch.createdAt) }}</p>
            <p v-if="batch.missingItems.length" class="artifact-missing">待补信息：{{ batch.missingItems.join('；') }}</p>
            <div v-for="file in batch.files" :key="file.fileId" class="artifact-file">
              <span>{{ file.fileName }}</span>
              <el-button type="text" :disabled="artifactDownloading" @click="downloadArtifactFile(batch, file)">重新下载</el-button>
            </div>
            <el-button :disabled="batch.importStatus !== 'IMPORT_CANDIDATE' || artifactDownloading" @click="downloadArtifactZip(batch)">下载 ZIP</el-button>
          </el-card>
        </div>
        <span slot="footer"><el-button @click="artifactDialogVisible = false">关闭</el-button></span>
      </el-dialog>
    </template>
  </section>
</template>
<script lang="ts">
import Vue from 'vue'
import { DraftEvidence } from '../api/generated'
import { getTaskDetail, getTaskProcessArtifactAvailability, TaskDetail } from '../services/task-list-api'
import { ProcessArtifactBatch, ProcessArtifactFileInfo, listProcessArtifacts, downloadProcessArtifact } from '../services/process-artifact-api'
import { fieldAnnotation, fieldTooltip } from '../constants/field-labels'
/** 仅为展示插入结论分句换行，保留接口原始文本与小数、字段路径中的英文句点。 */
function formatConclusionText(text: string): string {
  return text.replace(/([；;。])\s*/g, '$1\n').replace(/(\.)(?=\s|$)\s*/g, '$1\n').trim()
}
/** 任务只读详情页，双击任务列表行进入，以三步布局展示且仅步骤可切换。 */
export default Vue.extend({ name: 'TaskDetail', data: () => ({ detail: null as TaskDetail | null, loading: false, errorMessage: '', activeStep: 0, artifactAvailable: false, artifactAvailabilityError: '', artifactDialogVisible: false, artifactLoading: false, artifactDownloading: false, artifactBatches: [] as ProcessArtifactBatch[] }), computed: {
  /** 将模型协作检查结论按句分行，保留原始详情字段。 */ guidanceConclusionText(): string { return formatConclusionText(this.detail ? this.detail.conclusion : '') },
  /** 将决策包结论按同一规则分行，维持原有展示。 */ conclusionText(): string { return formatConclusionText(this.detail && this.detail.decisionPacket ? this.detail.decisionPacket.conclusion : '') },
  /** 仅在决策包和关联场景提示词均确认可用后展示入口。 */ showArtifactButton(): boolean { return Boolean(this.detail?.decisionPacket && this.artifactAvailable) },
}, created() { void this.loadDetail() },   methods: {
    /** 返回字段路径与中文注释。 */
    fieldAnnotation,
    /** 返回字段中文说明，用于提示。 */
    fieldTooltip,
  /** 加载路由参数指向的只读详情，入口状态随后独立查询。 */ async loadDetail(): Promise<void> { const taskId = this.$route.params.taskId; if (!taskId) { this.errorMessage = '未识别任务标识'; return } this.loading = true; this.artifactAvailable = false; this.artifactAvailabilityError = ''; try { this.detail = await getTaskDetail(taskId); if (this.detail.decisionPacket) void this.loadArtifactAvailability(taskId) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '任务详情暂时无法读取' } finally { this.loading = false } },
  /** 异步确认关联场景提示词，仅接纳当前任务的响应。 */ async loadArtifactAvailability(taskId: string): Promise<void> { try { const available = await getTaskProcessArtifactAvailability(taskId); if (this.detail?.taskId === taskId) this.artifactAvailable = available } catch (_error: unknown) { if (this.detail?.taskId === taskId) this.artifactAvailabilityError = '流程产物入口暂时无法读取，请刷新页面重试' } },
  /** 转换状态为业务中文。 */ statusLabel(status: string): string { return ({ DRAFT: '草稿', WAITING_SCOPE_CONFIRMATION: '待确认范围', READY: '待生成方案', WAITING_STEP_APPROVAL: '待处理', RUNNING_STEP: '处理中', ANALYZING: '分析中', WAITING_PROFESSIONAL_CONFIRMATION: '待专业确认', DRAFT_PACKET: '决策包草稿', WAITING_REVIEW: '待复核', RECHECKING_SCOPE: '重新核查范围', READY_TO_PUBLISH: '待发布', PUBLISHED: '已发布', HANDED_OFF: '已移交', CLOSED: '已关闭', PAUSED_MODEL_UNAVAILABLE: '模型暂不可用', PAUSED_BUDGET: '预算已暂停', PAUSED_SOURCE_UNAVAILABLE: '来源暂不可用', STALE_APPROVAL: '审批已失效', FAILED_RECOVERABLE: '处理失败，可重试', CANCELLED: '已取消' } as Record<string, string>)[status] || '状态待确认' },
  /** 以中文地区格式展示创建时间。 */ formatTime(value: string): string { const date = new Date(value); return Number.isNaN(date.getTime()) ? '-' : date.toLocaleString('zh-CN', { hour12: false }) },
  /** 把二维证据行转换为 el-table 可用的对象数组。 */ evidenceRowObjects(item: DraftEvidence): Array<Record<string, string>> { return item.rows.map((row) => { const record: Record<string, string> = {}; row.forEach((cell, index) => { record[String(index)] = cell }); return record }) },
  /** 打开流程产物弹窗并加载最近批次。 */ async openArtifactDialog(): Promise<void> { this.artifactDialogVisible = true; this.artifactLoading = true; this.errorMessage = ''; try { this.artifactBatches = await listProcessArtifacts(String(this.$route.params.taskId)) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '流程产物加载失败' } finally { this.artifactLoading = false } },
  /** 重新下载指定批次中的单个流程文件。 */ async downloadArtifactFile(batch: ProcessArtifactBatch, file: ProcessArtifactFileInfo): Promise<void> { if (this.artifactDownloading) return; this.artifactDownloading = true; this.errorMessage = ''; try { await downloadProcessArtifact(String(this.$route.params.taskId), batch.batchId, file.fileId, undefined, file.fileName) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '文件下载失败' } finally { this.artifactDownloading = false } },
  /** 下载导入候选批次的模型文件 ZIP。 */ async downloadArtifactZip(batch: ProcessArtifactBatch): Promise<void> { if (this.artifactDownloading) return; this.artifactDownloading = true; this.errorMessage = ''; try { await downloadProcessArtifact(String(this.$route.params.taskId), batch.batchId, undefined, ['MODEL'], `流程产物-${batch.batchId.slice(0, 8)}.zip`) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : 'ZIP 下载失败' } finally { this.artifactDownloading = false } },
} })
</script>
<style scoped>
.task-detail-page ::v-deep .el-step { cursor: pointer; }
.readonly-intake-panel { padding: 16px; border: 1px solid #dce7e4; border-radius: 12px; background: #fff; }
.readonly-intake-form { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); column-gap: 16px; }
.readonly-intake-form__wide { grid-column: 1 / -1; }
.readonly-field { min-height: 40px; padding: 8px 12px; border: 1px solid #dcdfe6; border-radius: 4px; background: #f8fafc; color: #303133; line-height: 22px; overflow-wrap: anywhere; }
.readonly-field--multiline { min-height: 94px; white-space: pre-wrap; }
.task-detail-content { min-width: 0; }
.task-detail-content:focus-visible { outline: 2px solid #c87422; outline-offset: -2px; }
.detail-guidance-grid { min-width: 0; }
.guidance-conclusion { display: block; width: 100%; overflow-wrap: anywhere; }
.packet-heading { margin-bottom: 16px; }
@media (max-width: 600px) { .readonly-intake-form { grid-template-columns: minmax(0, 1fr); } }
/* 宽屏详情长内容在卡片内部滚动，页面标题和步骤栏留在视口内。 */
@media (min-width: 1001px) and (min-height: 620px) {
  .task-detail-page--scrollable { display: flex; flex-direction: column; height: calc(100vh - 92px); min-height: 0; }
  .task-detail-page--scrollable > .page-heading,
  .task-detail-page--scrollable > .workflow-steps-bar { flex: 0 0 auto; }
  .task-detail-page--scrollable > .detail-content-card { display: flex; flex: 1 1 auto; flex-direction: column; min-height: 0; }
  .task-detail-page--scrollable ::v-deep .detail-content-card > .el-card__body { display: flex; flex: 1 1 auto; flex-direction: column; min-height: 0; }
  .task-detail-page--scrollable .task-detail-content { flex: 1 1 auto; min-height: 0; overflow-y: auto; overscroll-behavior: contain; }
}
.readonly-empty { color: #909399; margin: 8px 0; }
.conclusion-text { white-space: pre-wrap; line-height: 1.6; margin: 10px 0 16px; }
.evidence-item { margin-bottom: 12px; }
.evidence-meta { margin: 8px 0 4px; color: #817b73; font-size: 13px; }
/* 决策包卡片化展示：四个独立卡片纵向紧凑排列，每个横向平铺。 */
.packet-grid { display: grid; grid-template-columns: minmax(0, 1fr); gap: 12px; }
.packet-card { margin-bottom: 0; border: 1px solid #e7dfd4; border-radius: 10px; }
.packet-object { display: flex; flex-wrap: wrap; gap: 24px; }
.packet-object__item { display: inline-flex; align-items: center; gap: 8px; }
.packet-object__label { color: #817b73; font-size: 13px; }
.packet-object__value { color: #303133; font-weight: 600; }
.packet-card__header { font-size: 15px; color: #4a4640; font-weight: 600; }
.packet-card__revision { margin-left: 8px; color: #817b73; font-size: 13px; font-weight: 400; }
.packet-conclusion { margin: 0; padding: 12px 14px; border-left: 3px solid #dd7a13; background: #fbf6ee; color: #4b5563; line-height: 1.7; white-space: pre-line; border-radius: 0 8px 8px 0; }
.packet-recommend__goal { display: flex; align-items: flex-start; gap: 8px; margin-bottom: 8px; }
.packet-recommend__goal p { margin: 0; color: #303133; font-weight: 600; line-height: 1.6; }
.packet-recommend__reason { margin: 0 0 8px; color: #817b73; font-size: 13px; line-height: 1.6; }
.packet-alternatives { margin: 0; padding: 0; list-style: none; }
.packet-alternatives li { display: flex; gap: 8px; padding: 6px 0; color: #4b5563; line-height: 1.6; border-top: 1px dashed #ede6db; }
.packet-alternatives__index { flex: none; width: 18px; height: 18px; line-height: 18px; text-align: center; border-radius: 50%; background: #f3ede3; color: #dd7a13; font-size: 12px; }
.packet-alternatives__index { flex: none; width: 18px; height: 18px; line-height: 18px; text-align: center; border-radius: 50%; background: #f3ede3; color: #dd7a13; font-size: 12px; }
.artifact-dialog { min-height: 80px; }
.artifact-batch { margin-bottom: 12px; border: 1px solid #e7dfd4; border-radius: 10px; }
.artifact-batch__header { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; color: #4a4640; }
.artifact-batch__header strong { font-size: 15px; }
.artifact-meta { margin: 8px 0 4px; color: #817b73; font-size: 13px; }
.artifact-missing { margin: 4px 0; color: #b26a00; font-size: 13px; line-height: 1.6; }
.artifact-file { display: flex; align-items: center; justify-content: space-between; padding: 6px 0; border-top: 1px dashed #ede6db; }
.field-path { margin-left: 6px; color: #9b948a; font-size: 12px; font-weight: 400; }
</style>
