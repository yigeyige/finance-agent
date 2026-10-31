<!-- 业务处理工作台：业务说明、模型协作与决策包三步，复用受控分析与会话组件。 -->
<template>
  <section class="task-workbench">
    <div class="page-heading entry-page-heading"><h2>业务处理</h2></div>
    <!-- 顶部步骤条：与任务信息分区一致的三步骤风格，替代原任务标题/内容摘要。 -->
    <div class="workflow-steps-bar">
      <el-steps :active="activeStep" simple>
        <el-step title="业务说明" />
        <el-step title="模型协作" />
        <el-step title="决策包" />
      </el-steps>
    </div>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />

    <el-row :gutter="16" class="workbench-columns">
      <el-col :span="16" class="workbench-primary">
        <el-card shadow="never" class="form-card guided-workflow">
          <!-- 第一步：填写业务标题与说明，选择可选附件后开始模型分析。 -->
          <el-form v-if="activeStep === 0" :model="form" label-position="top" class="task-form task-intake-form">
            <el-form-item label="业务标题" class="task-intake-form__wide">
              <el-input v-model.trim="form.title" />
            </el-form-item>
            <el-form-item label="业务说明" class="task-intake-form__wide">
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
            <el-form-item label="附件材料（可选）" class="task-intake-form__wide">
              <el-upload action="#" :auto-upload="false" :file-list="attachmentFiles" :on-change="selectAttachment" :on-remove="removeAttachment" accept=".txt,.md,.csv,.json,.xls,.xlsx" multiple>
                <el-button type="primary" plain>选择附件</el-button>
              </el-upload>
              <p class="attachment-note">最多 3 个附件，单个不超过 1 MB，支持 txt、md、csv、json、xls、xlsx 格式。</p>
            </el-form-item>
            <div class="task-intake-form__wide form-actions"><el-button type="primary" :loading="saving" @click="createTaskDraft">开始模型分析</el-button></div>
          </el-form>

          <!-- 第二步：确认任务信息、查看跨轮证据，通过右侧会话持续协作。 -->
          <template v-else-if="activeStep === 1">
            <div class="handling-workspace">
              <section class="handling-form-panel">
                <div class="handling-form-panel__heading">
                  <h4>任务信息</h4>
                  <el-button size="small" type="primary" :loading="saving" :disabled="modelLoading || chatLoading || packetLoading || Boolean(decisionPacket)" @click="saveTaskInfo">保存任务</el-button>
                </div>
                <p v-if="modelLoading" class="model-progress">
                  <i class="el-icon-loading"></i>
                  {{ progressStage || '模型正在整理初稿，请稍候' }}
                </p>
                <el-form v-else :model="form" label-position="top" class="task-form">
                  <el-form-item label="处理对象">
                    <el-input v-model.trim="form.subjectKey" :disabled="saving || chatLoading || packetLoading || Boolean(decisionPacket)" />
                  </el-form-item>
                  <el-form-item label="业务说明">
                    <el-input v-model.trim="form.question" type="textarea" :rows="3" :disabled="saving || chatLoading || packetLoading || Boolean(decisionPacket)" />
                  </el-form-item>
                  <el-form-item label="业务场景 Skill">
                    <el-select v-model="selectedSkillId" clearable filterable :loading="sceneSkillsLoading" :disabled="saving || chatLoading || packetLoading || Boolean(decisionPacket)">
                      <el-option v-for="skill in sceneSkills" :key="skill.skillId" :label="skill.name" :value="skill.skillId" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="数据库实例">
                    <el-select v-model="selectedDatabaseConfigId" clearable :disabled="saving || chatLoading || packetLoading || Boolean(decisionPacket)">
                      <el-option v-for="instance in databaseInstances" :key="instance.configId" :label="instance.name" :value="instance.configId" />
                    </el-select>
                  </el-form-item>
                </el-form>
                <el-button v-if="!guidance && !modelLoading" :disabled="saving" @click="requestGuidance">重新开始模型分析</el-button>
              </section>

              <section v-if="guidance" class="guidance-result-panel">
                <div class="card-heading">
                  <h4>检查结论</h4>
                  <div class="collaboration-actions"><el-button v-if="decisionPacket" :disabled="modelLoading || chatLoading || saving || packetLoading" @click="activeStep = 2">查看决策包</el-button><el-button type="primary" :loading="packetLoading" :disabled="modelLoading || chatLoading || saving" @click="generateDecisionPacket">生成决策包</el-button></div>
                </div>
                <p class="conclusion-text">{{ guidance.draft.conclusion || '暂无结论' }}</p>
                <template v-if="guidance.draft.processPlan">
                  <h4>流程事实</h4>
                  <p class="conclusion-text">{{ guidance.draft.processPlan.name }}：{{ guidance.draft.processPlan.nodes.map(node => node.name).join('、') || '尚无已确认节点' }}</p>
                  <p v-for="(flow, fi) in guidance.draft.processPlan.flows" :key="fi" class="conclusion-text">{{ flowNodeName(guidance.draft.processPlan, flow.source) }} → {{ flowNodeName(guidance.draft.processPlan, flow.target) }}{{ flow.condition ? '（' + flow.condition.variable + ' ' + flow.condition.operator + ' ' + flow.condition.value + '）' : '' }}</p>
                  <p v-if="guidance.draft.processPlan.missingItems.length" class="conclusion-text">待补信息：{{ guidance.draft.processPlan.missingItems.join('；') }}</p>
                  <p v-if="guidance.draft.processPlan.unsupported.length" class="conclusion-text">暂未支持：{{ guidance.draft.processPlan.unsupported.join('；') }}</p>
                </template>
                <template v-if="accumulatedEvidence.length">
                  <h4>关联证据（跨轮累积）</h4>
                  <div v-for="round in accumulatedEvidence" :key="round.roundNo" class="evidence-item">
                    <p class="evidence-meta">第 {{ round.roundNo }} 轮</p>
                    <div v-for="(item, ei) in round.evidence" :key="`${round.roundNo}-${ei}`" class="evidence-item">
                      <p class="evidence-meta">{{ item.schema }}.{{ item.table }} · {{ item.purpose }}</p>
                      <el-table v-workbench-table="'WorkbenchHome-1:' + item.schema + '.' + item.table" :data="evidenceRowObjects(item)" border size="mini">
                        <el-table-column show-overflow-tooltip min-width="160" v-for="(col, ci) in item.columns" :key="ci" :prop="String(ci)" :label="col" />
                      </el-table>
                    </div>
                  </div>
                </template>
                <p v-else class="conclusion-text">尚无累积证据</p>
              </section>
            </div>
          </template>
          <!-- 第三步：只展示已成功生成的决策包，可返回第二步继续协作。 -->
          <section v-else-if="decisionPacket" class="guidance-result-panel packet-result-panel">
            <div class="card-heading">
              <h3>业务决策包</h3>
              <div class="collaboration-actions">
                <el-tag type="info" size="small">修订 {{ decisionPacket.revision }}</el-tag>
                <el-button @click="activeStep = 1">返回模型协作</el-button>
              </div>
            </div>

            <!-- 四个独立卡片紧凑排列：数据对象、操作、结论、证据。 -->
            <div class="packet-grid">
              <el-card shadow="never" class="packet-card packet-card--object">
                <div slot="header" class="packet-card__header">数据对象</div>
                <div class="packet-object">
                  <span class="packet-object__item"><span class="packet-object__label">处理对象</span><span class="packet-object__value">{{ decisionPacket.content.subjectKey || '—' }}</span></span>
                  <span class="packet-object__item"><span class="packet-object__label">数据库实例</span><span class="packet-object__value">{{ decisionPacket.content.databaseInstanceName || '—' }}</span></span>
                </div>
              </el-card>

              <el-card shadow="never" class="packet-card packet-card--action">
                <div slot="header" class="packet-card__header">操作</div>
                <div class="packet-recommend__goal">
                  <el-tag type="primary" effect="dark" size="small">推荐</el-tag>
                  <p>{{ decisionPacket.content.recommendedGoal || '暂无' }}</p>
                </div>
                <p v-if="decisionPacket.content.reason" class="packet-recommend__reason">推荐理由：{{ decisionPacket.content.reason }}</p>
                <ul v-if="decisionPacket.content.alternativeGoals.length" class="packet-alternatives">
                  <li v-for="(goal, gi) in decisionPacket.content.alternativeGoals" :key="gi">
                    <span class="packet-alternatives__index">{{ gi + 1 }}</span>
                    <span>{{ goal }}</span>
                  </li>
                </ul>
              </el-card>

              <el-card shadow="never" class="packet-card packet-card--conclusion">
                <div slot="header" class="packet-card__header">结论</div>
                <p class="packet-conclusion">{{ conclusionText }}</p>
              </el-card>

              <el-card v-if="decisionPacket.content.evidence.length" shadow="never" class="packet-card packet-card--evidence">
                <div slot="header" class="packet-card__header">证据</div>
                <div v-for="(item, ei) in decisionPacket.content.evidence" :key="ei" class="evidence-item">
                  <p class="evidence-meta">{{ item.schema }}.{{ item.table }} · {{ item.purpose }}</p>
                  <el-table v-workbench-table="'WorkbenchHome-2:' + item.schema + '.' + item.table" :data="evidenceRowObjects(item)" border size="mini">
                    <el-table-column show-overflow-tooltip min-width="160" v-for="(col, ci) in item.columns" :key="ci" :prop="String(ci)" :label="col" />
                  </el-table>
                </div>
              </el-card>
            </div>
            <div v-if="decisionPacket.content.guidanceDraft && decisionPacket.content.guidanceDraft.processPlan" class="evidence-item">
              <h4>本次采用的流程调查结论</h4>
              <p class="conclusion-text">{{ decisionPacket.content.guidanceDraft.processPlan.name }}：{{ decisionPacket.content.guidanceDraft.processPlan.nodes.map(node => node.name).join('、') || '尚无已确认节点' }}</p>
              <p v-if="decisionPacket.content.guidanceDraft.processPlan.missingItems.length" class="conclusion-text">待补信息：{{ decisionPacket.content.guidanceDraft.processPlan.missingItems.join('；') }}</p>
            </div>
            <section v-if="decisionPacket.content.sceneSkillId === PROCESS_SKILL_ID" class="guidance-result-panel">
              <div class="card-heading">
                <h3>流程文件</h3>
                <el-button type="primary" :loading="artifactLoading" @click="generateArtifacts">生成文件</el-button>
              </div>
              <p v-if="!decisionPacket.content.guidanceDraft || !decisionPacket.content.guidanceDraft.processPlan" class="conclusion-text">当前决策包没有固定流程调查结论，请返回模型协作重新调查并生成决策包。</p>
              <el-button v-if="artifactBatches.length > 1" type="text" @click="showPrevious = !showPrevious">{{ showPrevious ? '收起上一批文件' : '查看上一批文件' }}</el-button>
              <template v-for="(batch, index) in artifactBatches">
                <div v-show="index === 0 || showPrevious" :key="batch.batchId" class="evidence-item">
                  <h4>{{ index === 0 ? '本次文件' : '上一批文件' }} · 决策包修订 {{ batch.packetRevision }}</h4>
                  <el-tag v-if="batch.packetRevision !== decisionPacket.revision" type="warning">基于旧决策包</el-tag>
                  <el-tag :type="batch.importStatus === 'IMPORT_CANDIDATE' ? 'success' : 'warning'">{{ batch.importStatus === 'IMPORT_CANDIDATE' ? '可作为导入候选' : '结构待修复' }}</el-tag>
                  <p v-if="batch.missingItems.length" class="conclusion-text">待补信息：{{ batch.missingItems.join('；') }}</p>
                  <div v-for="file in batch.files" :key="file.fileId">
                    <el-checkbox v-if="index === 0 && file.kind !== 'MODEL'" :value="selectedKinds.includes(file.kind)" @change="toggleArtifactKind(file.kind)">{{ file.fileName }}</el-checkbox>
                    <span v-else>{{ file.fileName }}</span>
                    <el-button type="text" @click="downloadArtifactFile(batch, file)">下载</el-button>
                  </div>
                  <el-button v-if="index === 0" :disabled="batch.importStatus !== 'IMPORT_CANDIDATE' || artifactLoading" @click="downloadArtifactZip(batch)">下载所选文件 ZIP</el-button>
                </div>
              </template>
              <p class="conclusion-text">下载后请在 EMS 中手动导入并核对流程；EMS 导入会自动部署并设置运行版本。</p>
            </section>
          </section>
        </el-card>
      </el-col>

      <!-- 会话独立成组件，页面继续管理模型请求与任务状态。 -->
      <el-col :span="8" class="workbench-conversation">
        <workbench-chat v-model="chatMessage" :messages="chatMessages" :enabled="activeStep === 1 && Boolean(guidance) && !saving && !packetLoading" :loading="chatLoading" :analyzing="modelLoading" :runtime-model="runtimeModel" :runtime-loading="runtimeModelLoading" :metadata="lastModelMetadata" @send="reviseGuidance" />
      </el-col>
    </el-row>
  </section>
</template>
<script lang="ts">
import Vue from 'vue'
import WorkbenchChat from '../components/WorkbenchChat.vue'
import { DraftEvidence, TaskResponse, ModelCallMetadata, ModelRuntimeSummary, ProcessArtifactPlan } from '../api/generated'
import { DEFAULT_RUNTIME_BUNDLE_ID } from '../services/business-workflow'
import { getCurrentIdentity } from '../services/identity'
import { BusinessDecisionPacketResponse, BusinessEvidenceAccumResponse, BusinessGuidanceResponse, generateBusinessDecisionPacket, generateBusinessGuidance, getBusinessEvidenceAccums, getGuidanceProgress, getModelRuntimeSummary, reviseBusinessGuidance } from '../services/model-api'
import { BusinessSceneSkill, listBusinessSceneSkills } from '../services/business-skill-api'
import { EmsDatabaseInstance, listEnabledDatabaseInstances } from '../services/ems-database-config-api'
import { createTask, isStaleError, updateTask } from '../services/task-api'
import { ProcessArtifactBatch, ProcessArtifactFileInfo, generateProcessArtifacts, listProcessArtifacts, downloadProcessArtifact } from '../services/process-artifact-api'
interface Form { title: string; question: string; subjectKey: string }
interface Chat { id: string; role: 'operator' | 'model'; content: string }
interface AttachmentFile { name: string; size?: number; raw?: File }
const ATTACHMENT_EXTENSIONS = ['txt', 'md', 'csv', 'json', 'xls', 'xlsx']
const PROCESS_SKILL_ID = 'a6a050ae-b699-4ca0-af72-4381e990eda0'
/** 生成每次新建业务处理所需的独立默认表单，避免复用上一份草稿引用。 */
const createInitialForm = (): Form => ({ title: '', question: '', subjectKey: '' })
/** 仅接受后端可受控分析的文本附件，避免浏览器选择无效格式。 */
const isSupportedAttachment = (file: AttachmentFile): boolean => { const extension = file.name.split('.').pop()?.toLowerCase(); return Boolean(file.raw) && file.size !== 0 && file.size !== undefined && file.size <= 1024 * 1024 && Boolean(extension && ATTACHMENT_EXTENSIONS.includes(extension)) }
export default Vue.extend({ name: 'WorkbenchHome', components: { WorkbenchChat }, data() { return { PROCESS_SKILL_ID, artifactBatches: [] as ProcessArtifactBatch[], artifactLoading: false, selectedKinds: ['MODEL'] as string[], showPrevious: false, runtimeModel: null as ModelRuntimeSummary | null, runtimeModelLoading: false, lastModelMetadata: null as ModelCallMetadata | null, activeStep: 0, saving: false, modelLoading: false, chatLoading: false, task: null as TaskResponse | null, guidance: null as BusinessGuidanceResponse | null, chatMessage: '', chatMessages: [] as Chat[], attachmentFiles: [] as AttachmentFile[], sceneSkills: [] as BusinessSceneSkill[], selectedSkillId: '', sceneSkillsLoading: false, databaseInstances: [] as EmsDatabaseInstance[], selectedDatabaseConfigId: '', progressStage: '', progressTimer: null as ReturnType<typeof setInterval> | null, errorMessage: '', accumulatedEvidence: [] as BusinessEvidenceAccumResponse[], decisionPacket: null as BusinessDecisionPacketResponse | null, packetLoading: false, form: createInitialForm() } }, computed: { /** 将调查结论按分号/句号换行，便于管理员逐条阅读。 */ conclusionText(): string { const text = this.decisionPacket && this.decisionPacket.content ? this.decisionPacket.content.conclusion : ''; return text.replace(/([；;。])\s*/g, '$1\n').trim() } }, created() { void this.loadSceneSkills(); void this.loadDatabaseInstances(); void this.loadRuntimeModel() }, /** 返回缓存页时刷新当前启用的模型摘要。 */ activated() { void this.loadRuntimeModel() }, /** 组件销毁前清理进度轮询定时器，避免离开页面产生僵尸请求。 */ beforeDestroy() { this.stopProgressPolling() }, watch: { /** 仅响应显式新建入口的草稿标识，普通导航继续保留当前向导状态。 */ '$route.query.draft'(): void { this.resetDraft() } }, methods: {
  /** 在无进行中写请求时清空本地草稿和待分析附件，不删除已创建的后端任务。 */ resetDraft(): void { if (this.saving || this.modelLoading || this.chatLoading || this.packetLoading || this.artifactLoading) return; this.activeStep = 0; this.task = null; this.guidance = null; this.chatMessage = ''; this.chatMessages = []; this.attachmentFiles = []; this.selectedSkillId = ''; this.progressStage = ''; this.stopProgressPolling(); this.errorMessage = ''; this.accumulatedEvidence = []; this.decisionPacket = null; this.artifactBatches = []; this.selectedKinds = ['MODEL']; this.showPrevious = false; this.lastModelMetadata = null; this.form = createInitialForm() },
  /** 读取安全运行摘要，失败不阻断业务表单，也不访问管理员配置列表。 */ async loadRuntimeModel(): Promise<void> { if (this.runtimeModelLoading) return; this.runtimeModelLoading = true; try { this.runtimeModel = await getModelRuntimeSummary() } catch { this.runtimeModel = null } finally { this.runtimeModelLoading = false } },
  /** 加载业务场景 Skill 列表供第一步选择，失败时保留空列表并提示。 */ async loadSceneSkills(): Promise<void> { this.sceneSkillsLoading = true; this.errorMessage = ''; try { this.sceneSkills = await listBusinessSceneSkills() } catch (error: unknown) { this.sceneSkills = []; this.errorMessage = error instanceof Error ? error.message : '业务场景列表暂时无法读取' } finally { this.sceneSkillsLoading = false } },
  /** 加载启用数据库实例列表供第一步选择，失败时保留空列表不阻断创建。 */ async loadDatabaseInstances(): Promise<void> { try { this.databaseInstances = await listEnabledDatabaseInstances() } catch { this.databaseInstances = [] } },
  /** 校验并保存当前选择的本地附件，不在浏览器读取附件正文。 */ selectAttachment(file: AttachmentFile, fileList: AttachmentFile[]): void { if (!isSupportedAttachment(file)) { this.errorMessage = '附件需为非空的 txt、md、csv、json、xls、xlsx 文件，且单个不超过 1 MB'; this.attachmentFiles = fileList.filter(item => item !== file); return } if (fileList.length > 3) { this.errorMessage = '最多可选择 3 个附件'; this.attachmentFiles = fileList.filter(item => item !== file); return } this.errorMessage = ''; this.attachmentFiles = fileList },
  /** 同步移除待分析附件，已创建的任务和模型草稿不受影响。 */ removeAttachment(file: AttachmentFile, fileList: AttachmentFile[]): void { this.attachmentFiles = fileList },
  /** 创建任务后请求模型初稿；附件仅在本次初稿调用中提交。 */ async createTaskDraft(): Promise<void> { if (this.saving || this.modelLoading || this.chatLoading || this.packetLoading) return; const identity = getCurrentIdentity(); if (!identity || !this.form.title || !this.form.question) { this.errorMessage = '请填写业务标题和业务说明'; return } await this.execute(async () => { this.task = await createTask({ title: this.form.title, originalQuestion: this.form.question, initiatorUserId: identity.userId, runtimeBundleId: DEFAULT_RUNTIME_BUNDLE_ID, businessSceneSkillId: this.selectedSkillId || undefined, databaseConfigId: this.selectedDatabaseConfigId || undefined }); this.activeStep = 1 }); await this.requestGuidance() },
  /** 发送当前任务与受控本地附件以获取可编辑初稿，失败时保留附件供重试。 */ async requestGuidance(): Promise<void> { if (!this.task || this.modelLoading || this.chatLoading || this.packetLoading || this.saving) return; this.errorMessage = ''; this.progressStage = '正在准备模型分析'; this.modelLoading = true; this.startProgressPolling(); try { const response = await generateBusinessGuidance(this.task.taskId, this.attachmentFiles.map(file => file.raw).filter((file): file is File => Boolean(file))); this.applyGuidance(response); this.chatMessages = [{ id: `m-${Date.now()}`, role: 'model', content: response.reply }] } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '模型暂时无法生成初稿' } finally { this.stopProgressPolling(); this.modelLoading = false } },
  /** 向模型发送管理员修改意见并以返回草稿更新界面。 */ async reviseGuidance(): Promise<void> { if (!this.task || !this.guidance || !this.chatMessage.trim() || this.chatLoading || this.modelLoading || this.packetLoading || this.saving || this.activeStep !== 1) return; this.errorMessage = ''; const message = this.chatMessage.trim(); this.chatLoading = true; this.chatMessage = ''; this.chatMessages.push({ id: `o-${Date.now()}`, role: 'operator', content: message }); try { const response = await reviseBusinessGuidance(this.task.taskId, message, this.guidance.draft); this.applyGuidance(response); this.chatMessages.push({ id: `m-${Date.now()}`, role: 'model', content: response.reply }) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '模型暂时无法修改方案' } finally { this.chatLoading = false } },
  /** 将受控模型初稿回填为可由管理员继续调整的业务表单。 */ applyGuidance(response: BusinessGuidanceResponse): void { this.decisionPacket = null; const draft = response.draft; this.guidance = response; this.lastModelMetadata = response.metadata || null; this.form.subjectKey = draft.objectKey || draft.subjectKey; void this.loadEvidenceAccums() },
  /** 把二维证据行转换为 el-table 可用的对象数组。 */ evidenceRowObjects(item: DraftEvidence): Array<Record<string, string>> { return item.rows.map((row) => { const record: Record<string, string> = {}; row.forEach((cell, index) => { record[String(index)] = cell }); return record }) },
  /** 将流程连线内部标识转为用户可读的节点名称。 */ flowNodeName(plan: ProcessArtifactPlan, id: string): string { if (id === 'start') return '开始'; if (id === 'end') return '结束'; return plan.nodes.find(node => node.id === id)?.name || id },
  /** 加载任务证据工作区的跨轮累积证据，供证据面板展示过程。 */ async loadEvidenceAccums(): Promise<void> { if (!this.task) return; try { this.accumulatedEvidence = await getBusinessEvidenceAccums(this.task.taskId) } catch (error: unknown) { this.accumulatedEvidence = [] } },
  /** 生成成功后进入第三步，失败保留协作上下文；忙碌时阻止重复请求。 */ async generateDecisionPacket(): Promise<void> { if (!this.task || !this.guidance || this.packetLoading || this.modelLoading || this.chatLoading || this.saving) return; this.packetLoading = true; this.errorMessage = ''; try { this.decisionPacket = await generateBusinessDecisionPacket(this.task.taskId); this.activeStep = 2; await this.loadArtifactBatches() } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '业务决策包生成失败' } finally { this.packetLoading = false } },
  /** 读取最近两批，文件生成失败时旧批次仍可下载。 */ async loadArtifactBatches(): Promise<void> { if (!this.task || this.decisionPacket?.content.sceneSkillId !== PROCESS_SKILL_ID) return; this.artifactBatches = await listProcessArtifacts(this.task.taskId) },
  /** 用户点击后按当前决策包生成新批次。 */ async generateArtifacts(): Promise<void> { if (!this.task || !this.decisionPacket || this.artifactLoading) return; this.artifactLoading = true; this.errorMessage = ''; try { await generateProcessArtifacts(this.task.taskId, this.decisionPacket.packetId); this.selectedKinds = ['MODEL']; await this.loadArtifactBatches() } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '流程文件生成失败' } finally { this.artifactLoading = false } },
  /** 选择当前批次可选配置文件。 */ toggleArtifactKind(kind: string): void { this.selectedKinds = this.selectedKinds.includes(kind) ? this.selectedKinds.filter(item => item !== kind) : [...this.selectedKinds, kind] },
  /** 下载批次内的单个文件。 */ async downloadArtifactFile(batch: ProcessArtifactBatch, file: ProcessArtifactFileInfo): Promise<void> { if (!this.task) return; try { await downloadProcessArtifact(this.task.taskId, batch.batchId, file.fileId, undefined, file.fileName) } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '文件下载失败' } },
  /** 只将用户选中的配置与必选模型打包。 */ async downloadArtifactZip(batch: ProcessArtifactBatch): Promise<void> { if (!this.task) return; try { const model = batch.files.find(file => file.kind === 'MODEL'); await downloadProcessArtifact(this.task.taskId, batch.batchId, undefined, this.selectedKinds, model ? model.fileName.replace('BPMN.xml', '.zip') : '流程文件.zip') } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '文件打包失败' } },
  /** 在模型调用期间轮询后端阶段进度并更新加载文案。 */ startProgressPolling(): void { this.stopProgressPolling(); if (!this.task) return; const taskId = this.task.taskId; this.progressTimer = setInterval(async () => { try { const progress = await getGuidanceProgress(taskId); if (progress.stage) this.progressStage = progress.stage } catch (error: unknown) { /* 进度轮询失败不打断主流程，保留上一阶段文案 */ } }, 800) },
  /** 停止阶段进度轮询并释放定时器。 */ stopProgressPolling(): void { if (this.progressTimer) { clearInterval(this.progressTimer); this.progressTimer = null } },
  /** 保存第二步可编辑的任务基础信息。 */ async saveTaskInfo(): Promise<void> { if (!this.task || this.modelLoading || this.chatLoading || this.packetLoading) return; await this.execute(async () => { this.task = await updateTask(this.task!.taskId, { originalQuestion: this.form.question, businessSceneSkillId: this.selectedSkillId || undefined, databaseConfigId: this.selectedDatabaseConfigId || undefined }); this.decisionPacket = null; this.$message.success('任务信息已保存') }) },
  /** 串行执行任务写操作并将可识别异常转换为页面错误文案。 */ async execute(action: () => Promise<void>): Promise<void> { if (this.saving) return; this.saving = true; this.errorMessage = ''; try { await action() } catch (error: unknown) { this.errorMessage = isStaleError(error) ? '当前范围已发生变化，请刷新后重新确认' : error instanceof Error ? error.message : '请求未完成，请稍后重试' } finally { this.saving = false } },
} })
</script>
<style scoped>
/* 输入阶段两列选择项，长文本和附件横跨整行；窄屏自动回到单列。 */
.task-intake-form { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); column-gap: 16px; }
.task-intake-form__wide { grid-column: 1 / -1; }
.task-workbench .el-select { width: 100%; }
.task-intake-form .form-actions { justify-content: flex-start; }
.workbench-conversation { position: sticky; top: 14px; }
.collaboration-actions { display: flex; flex-wrap: wrap; justify-content: flex-end; gap: 8px; }
.collaboration-actions .el-button + .el-button { margin-left: 0; }
.packet-result-panel { margin-top: 14px; }
.packet-result-panel > .card-heading { align-items: center; margin-bottom: 16px; }
@media (max-width: 1000px) {
  .workbench-columns > .workbench-primary, .workbench-columns > .workbench-conversation { width: 100%; }
  .workbench-conversation { position: static; margin-top: 16px; }
}
@media (max-width: 600px) { .task-intake-form { grid-template-columns: minmax(0, 1fr); } }
.attachment-note { margin: 8px 0 0; color: #606266; font-size: 14px; }
.model-progress { display: flex; align-items: center; gap: 8px; margin: 16px 0; color: #4a4640; line-height: 1.6; }
.model-progress .el-icon-loading { color: #dd7a13; }
.guidance-result-panel { margin-top: 0; padding: 16px; border: 1px solid #e7dfd4; border-radius: 10px; background: #fffdf9; }
.guidance-result-panel .conclusion-text { margin: 8px 0 16px; color: #4b5563; line-height: 1.6; }
.guidance-result-panel .evidence-item { margin-bottom: 12px; }
.guidance-result-panel .evidence-meta { margin: 8px 0 4px; color: #817b73; font-size: 13px; }
/* 决策包卡片化展示：四个独立卡片纵向紧凑排列，每个横向平铺。 */
.packet-grid { display: grid; grid-template-columns: minmax(0, 1fr); gap: 12px; }
.packet-card { margin-bottom: 0; border: 1px solid #e7dfd4; border-radius: 10px; }
.packet-object { display: flex; flex-wrap: wrap; gap: 24px; }
.packet-object__item { display: inline-flex; align-items: center; gap: 8px; }
.packet-object__label { color: #817b73; font-size: 13px; }
.packet-object__value { color: #303133; font-weight: 600; }
.packet-card__header { font-size: 15px; color: #4a4640; font-weight: 600; }
.packet-conclusion { margin: 0; padding: 12px 14px; border-left: 3px solid #dd7a13; background: #fbf6ee; color: #4b5563; line-height: 1.7; white-space: pre-line; border-radius: 0 8px 8px 0; }
.packet-recommend__goal { display: flex; align-items: flex-start; gap: 8px; margin-bottom: 8px; }
.packet-recommend__goal p { margin: 0; color: #303133; font-weight: 600; line-height: 1.6; }
.packet-recommend__reason { margin: 0 0 8px; color: #817b73; font-size: 13px; line-height: 1.6; }
.packet-alternatives { margin: 0; padding: 0; list-style: none; }
.packet-alternatives li { display: flex; gap: 8px; padding: 6px 0; color: #4b5563; line-height: 1.6; border-top: 1px dashed #ede6db; }
.packet-alternatives__index { flex: none; width: 18px; height: 18px; line-height: 18px; text-align: center; border-radius: 50%; background: #f3ede3; color: #dd7a13; font-size: 12px; }
</style>
