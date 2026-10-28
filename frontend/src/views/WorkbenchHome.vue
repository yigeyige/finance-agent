<!-- 业务处理工作台：两步表单、受控附件初稿分析与推荐方案协作区。 -->
<template>
  <section class="task-workbench">
    <!-- 顶部业务处理概览：仅平铺展示标题与内容，替代原"业务处理向导"标题。 -->
    <div class="task-overview-bar">
      <template v-if="task">
        <span class="task-overview-field">
          <label class="task-overview-label">标题</label>
          <span class="task-overview-value">{{ task.title }}</span>
        </span>
        <span class="task-overview-field">
          <label class="task-overview-label">内容</label>
          <span class="task-overview-value">{{ areaLabels }}</span>
        </span>
        <el-tag size="small" class="task-overview-status">{{ taskStatusLabel }}</el-tag>
      </template>
      <span v-else class="task-overview-empty">等待创建业务处理</span>
    </div>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="never" class="form-card guided-workflow">
          <el-steps :active="activeStep" simple>
            <el-step title="业务说明" />
            <el-step title="模型协作与确认" />
          </el-steps>

          <!-- 第一步：填写业务标题与说明，选择可选附件后开始模型分析。 -->
          <el-form v-if="activeStep === 0" :model="form" label-position="top" class="task-form">
            <el-form-item label="业务标题">
              <el-input v-model.trim="form.title" />
            </el-form-item>
            <el-form-item label="业务说明">
              <el-input v-model.trim="form.question" type="textarea" :rows="4" />
            </el-form-item>
            <el-form-item label="业务场景 Skill">
              <el-select v-model="selectedSkillId" clearable filterable placeholder="选择业务场景 Skill（可选）" :loading="sceneSkillsLoading">
                <el-option v-for="skill in sceneSkills" :key="skill.skillId" :label="skill.name" :value="skill.skillId" />
              </el-select>
            </el-form-item>
            <el-form-item label="数据库实例">
              <el-select v-model="selectedDatabaseConfigId" clearable placeholder="选择数据库实例（可选）">
                <el-option v-for="instance in databaseInstances" :key="instance.configId" :label="instance.name" :value="instance.configId" />
              </el-select>
            </el-form-item>
            <el-form-item label="附件材料（可选）">
              <el-upload action="#" :auto-upload="false" :file-list="attachmentFiles" :on-change="selectAttachment" :on-remove="removeAttachment" accept=".txt,.md,.csv,.json,.xls,.xlsx" multiple>
                <el-button type="primary" plain>选择附件</el-button>
              </el-upload>
              <p class="attachment-note">最多 3 个附件，单个不超过 1 MB，支持 txt、md、csv、json、xls、xlsx 格式。</p>
            </el-form-item>
            <el-button type="primary" :loading="saving" @click="createTaskDraft">开始模型分析</el-button>
          </el-form>

          <!-- 第二步：确认对象与范围，查看检查结论并编辑推荐业务方案。 -->
          <template v-else>
            <div class="handling-workspace">
              <section class="handling-form-panel">
                <h4>对象与处理范围</h4>
                <p v-if="modelLoading" class="model-progress">
                  <i class="el-icon-loading"></i>
                  {{ progressStage || '模型正在整理初稿，请稍候' }}
                </p>
                <el-form v-else :model="form" label-position="top" class="task-form">
                  <el-form-item label="处理对象">
                    <el-input v-model.trim="form.subjectKey" />
                  </el-form-item>
                  <el-form-item label="业务说明">
                    <el-input v-model.trim="form.question" type="textarea" :rows="3" />
                  </el-form-item>
                  <el-form-item label="业务场景 Skill">
                    <el-select v-model="selectedSkillId" clearable filterable :loading="sceneSkillsLoading">
                      <el-option v-for="skill in sceneSkills" :key="skill.skillId" :label="skill.name" :value="skill.skillId" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="数据库实例">
                    <el-select v-model="selectedDatabaseConfigId" clearable>
                      <el-option v-for="instance in databaseInstances" :key="instance.configId" :label="instance.name" :value="instance.configId" />
                    </el-select>
                  </el-form-item>
                  <el-button type="primary" :loading="saving" @click="saveTaskInfo">保存任务信息</el-button>
                </el-form>
              </section>

              <section v-if="guidance" class="guidance-result-panel">
                <div class="card-heading">
                  <h4>检查结论</h4>
                  <el-button type="primary" size="mini" :loading="packetLoading" @click="generateDecisionPacket">生成业务决策包</el-button>
                </div>
                <p class="conclusion-text">{{ guidance.draft.conclusion || '暂无结论' }}</p>
                <template v-if="accumulatedEvidence.length">
                  <h4>关联证据（跨轮累积）</h4>
                  <div v-for="round in accumulatedEvidence" :key="round.roundNo" class="evidence-item">
                    <p class="evidence-meta">第 {{ round.roundNo }} 轮</p>
                    <div v-for="(item, ei) in round.evidence" :key="`${round.roundNo}-${ei}`" class="evidence-item">
                      <p class="evidence-meta">{{ item.schema }}.{{ item.table }} · {{ item.purpose }}</p>
                      <el-table :data="evidenceRowObjects(item)" border size="mini">
                        <el-table-column v-for="(col, ci) in item.columns" :key="ci" :prop="String(ci)" :label="col" />
                      </el-table>
                    </div>
                  </div>
                </template>
                <p v-else class="conclusion-text">尚无累积证据</p>
                <template v-if="decisionPacket">
                  <h4>业务决策包（修订 {{ decisionPacket.revision }}）</h4>
                  <p v-if="decisionPacket.message" class="evidence-meta">{{ decisionPacket.message }}</p>
                  <p class="conclusion-text">{{ decisionPacket.content.conclusion || '暂无结论' }}</p>
                  <p class="evidence-meta">推荐方案：{{ decisionPacket.content.recommendedGoal || '暂无' }}</p>
                  <div v-for="(item, ei) in decisionPacket.content.evidence" :key="ei" class="evidence-item">
                    <p class="evidence-meta">{{ item.schema }}.{{ item.table }} · {{ item.purpose }}</p>
                    <el-table :data="evidenceRowObjects(item)" border size="mini">
                      <el-table-column v-for="(col, ci) in item.columns" :key="ci" :prop="String(ci)" :label="col" />
                    </el-table>
                  </div>
                </template>
              </section>
            </div>
          </template>
        </el-card>
      </el-col>

      <!-- 右侧模型协助修改：以气泡式聊天界面持续修改推荐方案。 -->
      <el-col :span="8">
        <el-card shadow="never" class="detail-card journey-card chat-panel">
          <div class="card-heading"><h3>模型协助修改</h3></div>
          <div class="chat-history">
            <article v-for="message in chatMessages" :key="message.id" class="chat-message" :class="`chat-message--${message.role}`">
              <span class="chat-message__author">{{ message.role === 'operator' ? '管理员' : '模型' }}</span>
              <p class="chat-message__bubble">{{ message.content }}</p>
            </article>
            <p v-if="!chatMessages.length" class="chat-empty">模型分析完成后，可在此对话修改推荐方案。</p>
          </div>
          <div class="chat-composer">
            <el-input v-model.trim="chatMessage" type="textarea" :rows="2" :disabled="!guidance || chatLoading" placeholder="直接说明希望如何修改推荐方案" />
            <div class="chat-actions">
              <el-button type="primary" :loading="chatLoading" :disabled="!guidance || !chatMessage" @click="reviseGuidance">发送修改意见</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </section>
</template>
<script lang="ts">
import Vue from 'vue'
import { DraftEvidence, TaskResponse } from '../api/generated'
import { DEFAULT_RUNTIME_BUNDLE_ID } from '../services/business-workflow'
import { getCurrentIdentity } from '../services/identity'
import { BusinessDecisionPacketResponse, BusinessEvidenceAccumResponse, BusinessGuidanceResponse, generateBusinessDecisionPacket, generateBusinessGuidance, getBusinessEvidenceAccums, getGuidanceProgress, reviseBusinessGuidance } from '../services/model-api'
import { BusinessSceneSkill, listBusinessSceneSkills } from '../services/business-skill-api'
import { EmsDatabaseInstance, listEnabledDatabaseInstances } from '../services/ems-database-config-api'
import { createTask, isStaleError, updateTask } from '../services/task-api'
interface Form { title: string; question: string; subjectKey: string }
interface Chat { id: string; role: 'operator' | 'model'; content: string }
interface AttachmentFile { name: string; size?: number; raw?: File }
const ATTACHMENT_EXTENSIONS = ['txt', 'md', 'csv', 'json', 'xls', 'xlsx']
/** 生成每次新建业务处理所需的独立默认表单，避免复用上一份草稿引用。 */
const createInitialForm = (): Form => ({ title: '', question: '', subjectKey: '' })
/** 仅接受后端可受控分析的文本附件，避免浏览器选择无效格式。 */
const isSupportedAttachment = (file: AttachmentFile): boolean => { const extension = file.name.split('.').pop()?.toLowerCase(); return Boolean(file.raw) && file.size !== 0 && file.size !== undefined && file.size <= 1024 * 1024 && Boolean(extension && ATTACHMENT_EXTENSIONS.includes(extension)) }
export default Vue.extend({ name: 'WorkbenchHome', data() { return { activeStep: 0, saving: false, modelLoading: false, chatLoading: false, task: null as TaskResponse | null, guidance: null as BusinessGuidanceResponse | null, chatMessage: '', chatMessages: [] as Chat[], attachmentFiles: [] as AttachmentFile[], sceneSkills: [] as BusinessSceneSkill[], selectedSkillId: '', sceneSkillsLoading: false, databaseInstances: [] as EmsDatabaseInstance[], selectedDatabaseConfigId: '', progressStage: '', progressTimer: null as ReturnType<typeof setInterval> | null, errorMessage: '', accumulatedEvidence: [] as BusinessEvidenceAccumResponse[], decisionPacket: null as BusinessDecisionPacketResponse | null, packetLoading: false, form: createInitialForm() } }, created() { void this.loadSceneSkills(); void this.loadDatabaseInstances() }, /** 组件销毁前清理进度轮询定时器，避免离开页面产生僵尸请求。 */ beforeDestroy() { this.stopProgressPolling() }, computed: {
  /** 返回当前任务的业务处理状态文案。 */ taskStatusLabel(): string { return this.task ? '处理中' : '未开始' },
  /** 展示当前任务的处理对象概要。 */ areaLabels(): string { return this.form.subjectKey || '待模型分析' },
}, watch: { /** 仅响应显式新建入口的草稿标识，普通导航继续保留当前向导状态。 */ '$route.query.draft'(): void { this.resetDraft() } }, methods: {
  /** 在无进行中写请求时清空本地草稿和待分析附件，不删除已创建的后端任务。 */ resetDraft(): void { if (this.saving || this.modelLoading || this.chatLoading) return; this.activeStep = 0; this.task = null; this.guidance = null; this.chatMessage = ''; this.chatMessages = []; this.attachmentFiles = []; this.selectedSkillId = ''; this.progressStage = ''; this.stopProgressPolling(); this.errorMessage = ''; this.accumulatedEvidence = []; this.decisionPacket = null; this.form = createInitialForm() },
  /** 加载业务场景 Skill 列表供第一步选择，失败时保留空列表并提示。 */ async loadSceneSkills(): Promise<void> { this.sceneSkillsLoading = true; this.errorMessage = ''; try { this.sceneSkills = await listBusinessSceneSkills() } catch (error: unknown) { this.sceneSkills = []; this.errorMessage = error instanceof Error ? error.message : '业务场景列表暂时无法读取' } finally { this.sceneSkillsLoading = false } },
  /** 加载启用数据库实例列表供第一步选择，失败时保留空列表不阻断创建。 */ async loadDatabaseInstances(): Promise<void> { try { this.databaseInstances = await listEnabledDatabaseInstances() } catch { this.databaseInstances = [] } },
  /** 校验并保存当前选择的本地附件，不在浏览器读取附件正文。 */ selectAttachment(file: AttachmentFile, fileList: AttachmentFile[]): void { if (!isSupportedAttachment(file)) { this.errorMessage = '附件需为非空的 txt、md、csv、json、xls、xlsx 文件，且单个不超过 1 MB'; this.attachmentFiles = fileList.filter(item => item !== file); return } if (fileList.length > 3) { this.errorMessage = '最多可选择 3 个附件'; this.attachmentFiles = fileList.filter(item => item !== file); return } this.errorMessage = ''; this.attachmentFiles = fileList },
  /** 同步移除待分析附件，已创建的任务和模型草稿不受影响。 */ removeAttachment(file: AttachmentFile, fileList: AttachmentFile[]): void { this.attachmentFiles = fileList },
  /** 创建任务后请求模型初稿；附件仅在本次初稿调用中提交。 */ async createTaskDraft(): Promise<void> { const identity = getCurrentIdentity(); if (!identity || !this.form.title || !this.form.question) { this.errorMessage = '请填写业务标题和业务说明'; return } await this.execute(async () => { this.task = await createTask({ title: this.form.title, originalQuestion: this.form.question, initiatorUserId: identity.userId, runtimeBundleId: DEFAULT_RUNTIME_BUNDLE_ID, businessSceneSkillId: this.selectedSkillId || undefined, databaseConfigId: this.selectedDatabaseConfigId || undefined }); this.activeStep = 1 }); await this.requestGuidance() },
  /** 发送当前任务与受控本地附件以获取可编辑初稿，失败时保留附件供重试。 */ async requestGuidance(): Promise<void> { if (!this.task) return; this.errorMessage = ''; this.progressStage = '正在准备模型分析'; this.modelLoading = true; this.startProgressPolling(); try { const response = await generateBusinessGuidance(this.task.taskId, this.attachmentFiles.map(file => file.raw).filter((file): file is File => Boolean(file))); this.applyGuidance(response); this.chatMessages = [{ id: `m-${Date.now()}`, role: 'model', content: response.reply }] } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '模型暂时无法生成初稿' } finally { this.stopProgressPolling(); this.modelLoading = false } },
  /** 向模型发送管理员修改意见并以返回草稿更新界面。 */ async reviseGuidance(): Promise<void> { if (!this.task || !this.guidance || !this.chatMessage.trim()) return; this.errorMessage = ''; const message = this.chatMessage.trim(); this.chatLoading = true; this.chatMessage = ''; this.chatMessages.push({ id: `o-${Date.now()}`, role: 'operator', content: message }); try { const response = await reviseBusinessGuidance(this.task.taskId, message, this.guidance.draft); this.applyGuidance(response); this.chatMessages.push({ id: `m-${Date.now()}`, role: 'model', content: response.reply }) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '模型暂时无法修改方案' } finally { this.chatLoading = false } },
  /** 将受控模型初稿回填为可由管理员继续调整的业务表单。 */ applyGuidance(response: BusinessGuidanceResponse): void { const draft = response.draft; this.guidance = response; this.form.subjectKey = draft.objectKey || draft.subjectKey; void this.loadEvidenceAccums() },
  /** 把二维证据行转换为 el-table 可用的对象数组。 */ evidenceRowObjects(item: DraftEvidence): Array<Record<string, string>> { return item.rows.map((row) => { const record: Record<string, string> = {}; row.forEach((cell, index) => { record[String(index)] = cell }); return record }) },
  /** 加载任务证据工作区的跨轮累积证据，供证据面板展示过程。 */ async loadEvidenceAccums(): Promise<void> { if (!this.task) return; try { this.accumulatedEvidence = await getBusinessEvidenceAccums(this.task.taskId) } catch (error: unknown) { this.accumulatedEvidence = [] } },
  /** 生成业务决策包并展示去重后的结论与证据。 */ async generateDecisionPacket(): Promise<void> { if (!this.task) return; this.packetLoading = true; this.errorMessage = ''; try { this.decisionPacket = await generateBusinessDecisionPacket(this.task.taskId) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '业务决策包生成失败' } finally { this.packetLoading = false } },
  /** 在模型调用期间轮询后端阶段进度并更新加载文案。 */ startProgressPolling(): void { this.stopProgressPolling(); if (!this.task) return; const taskId = this.task.taskId; this.progressTimer = setInterval(async () => { try { const progress = await getGuidanceProgress(taskId); if (progress.stage) this.progressStage = progress.stage } catch (error: unknown) { /* 进度轮询失败不打断主流程，保留上一阶段文案 */ } }, 800) },
  /** 停止阶段进度轮询并释放定时器。 */ stopProgressPolling(): void { if (this.progressTimer) { clearInterval(this.progressTimer); this.progressTimer = null } },
  /** 保存第二步可编辑的任务基础信息。 */ async saveTaskInfo(): Promise<void> { if (!this.task) return; await this.execute(async () => { this.task = await updateTask(this.task!.taskId, { originalQuestion: this.form.question, businessSceneSkillId: this.selectedSkillId || undefined, databaseConfigId: this.selectedDatabaseConfigId || undefined }); this.$message.success('任务信息已保存') }) },
  /** 串行执行任务写操作并将可识别异常转换为页面错误文案。 */ async execute(action: () => Promise<void>): Promise<void> { if (this.saving) return; this.saving = true; this.errorMessage = ''; try { await action() } catch (error: unknown) { this.errorMessage = isStaleError(error) ? '当前范围已发生变化，请刷新后重新确认' : error instanceof Error ? error.message : '请求未完成，请稍后重试' } finally { this.saving = false } },
} })
</script>
<style scoped>
.attachment-note { margin: 8px 0 0; color: #606266; font-size: 14px; }
.model-progress { display: flex; align-items: center; gap: 8px; margin: 16px 0; color: #4a4640; line-height: 1.6; }
.model-progress .el-icon-loading { color: #dd7a13; }
.guidance-result-panel { margin-top: 0; padding: 16px; border: 1px solid #e7dfd4; border-radius: 10px; background: #fffdf9; }
.guidance-result-panel .conclusion-text { margin: 8px 0 16px; color: #4b5563; line-height: 1.6; }
.guidance-result-panel .evidence-item { margin-bottom: 12px; }
.guidance-result-panel .evidence-meta { margin: 8px 0 4px; color: #817b73; font-size: 13px; }
</style>
