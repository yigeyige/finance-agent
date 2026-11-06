<!-- 业务场景页面：列表与全页表单在同一内容区切换，并提供按目标的 AI 预填。 -->
<template>
  <section class="evidence-page list-page skill-catalog entry-page">
    <div class="page-heading entry-page-heading" :class="{ 'skill-form-heading': view === 'form' }">
      <h2>{{ pageTitle }}</h2>
      <div v-if="view === 'form'" class="skill-form-actions">
        <el-button @click="prefillVisible = true">AI 预填</el-button>
        <el-button @click="backToList">返回</el-button>
      </div>
    </div>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />

    <template v-else>
      <section v-if="view === 'list'" class="entry-content-card">
        <div class="entry-content-toolbar">
          <span class="entry-content-toolbar__meta">共 {{ skills.length }} 项</span>
          <el-button type="primary" @click="openCreate">新增业务场景</el-button>
        </div>
        <div class="standard-table-panel skill-catalog-table">
          <el-table v-workbench-table="'BusinessSkillCatalog-1'" v-loading="loading" :data="skills" row-key="skillId" class="standard-data-table" border stripe
            empty-text="暂无业务场景">
            <el-table-column prop="name" label="场景名称" min-width="160" show-overflow-tooltip />
            <el-table-column prop="investigationPrompt" label="调查提示词" min-width="320" show-overflow-tooltip />
            <el-table-column prop="decisionPacketPrompt" label="决策包提示词" min-width="320" show-overflow-tooltip />
            <el-table-column prop="processArtifactPrompt" label="流程文件提示词" min-width="320" show-overflow-tooltip />
            <el-table-column show-overflow-tooltip label="更新时间" width="190">
              <template slot-scope="scope">{{ formatTime(scope.row.updatedAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="190" align="center">
              <template slot-scope="scope">
                <el-button type="text" @click="startHandling(scope.row)">业务处理</el-button>
                <el-button v-if="canManage(scope.row)" type="text" @click="openEdit(scope.row)">编辑</el-button>
                <el-button v-if="canManage(scope.row)" type="text" class="skill-delete-action" @click="confirmDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </section>

      <section v-else class="entry-content-card skill-form-card">
        <el-form :model="form" label-position="top" class="workbench-dialog-form skill-form">
          <el-form-item label="场景名称" required>
            <el-input v-model.trim="form.name" :maxlength="100" show-word-limit placeholder="例如：人员变更处理" />
          </el-form-item>
          <el-form-item label="调查提示词" required>
            <el-input v-model.trim="form.investigationPrompt" type="textarea" :rows="4" :maxlength="8000" show-word-limit
              placeholder="说明该场景调查时需要的业务背景、关键表字段语义与查询注意事项" />
          </el-form-item>
          <el-form-item label="决策包提示词">
            <el-input v-model.trim="form.decisionPacketPrompt" type="textarea" :rows="3" :maxlength="2000" show-word-limit
              placeholder="可选，说明生成业务决策包时的结论、责任与后续处理要求" />
          </el-form-item>
          <el-form-item label="流程文件提示词">
            <el-input v-model.trim="form.processArtifactPrompt" type="textarea" :rows="3" :maxlength="4000" show-word-limit
              placeholder="可选，说明根据决策包生成流程文件的要求" />
          </el-form-item>
          <el-form-item v-if="editingId" label="场景附件">
            <div class="skill-attachments">
              <div v-for="attachment in attachments" :key="attachment.attachmentId" class="skill-attachment-item">
                <div class="skill-attachment-item__meta">
                  <span class="skill-attachment-item__name">{{ attachment.fileName }}</span>
                  <span class="skill-attachment-item__summary">{{ attachment.summary }}</span>
                </div>
                <el-button type="text" class="skill-delete-action" @click="removeAttachment(attachment)">删除</el-button>
              </div>
              <div class="skill-attachment-upload">
                <el-upload action="#" :auto-upload="false" :show-file-list="false" accept=".txt,.md,.csv,.json,.xml,.xls,.xlsx"
                  :on-change="selectAttachmentFile">
                  <el-button type="primary" plain>选择文件</el-button>
                </el-upload>
                <el-input v-model.trim="pendingSummary" :maxlength="500" placeholder="附件摘要（必填）" />
                <el-button type="primary" :disabled="!pendingFile || !pendingSummary || attachmentUploading" :loading="attachmentUploading"
                  @click="uploadAttachment">上传</el-button>
              </div>
            </div>
          </el-form-item>
          <p v-else class="skill-attachment-hint">保存业务场景后可上传知识附件（json/xml/xlsx 等，单个不超过 100KB）</p>
        </el-form>
        <div class="skill-form-footer">
          <el-button :disabled="saving" @click="backToList">返回</el-button>
          <el-button type="primary" :loading="saving" @click="save">保存</el-button>
        </div>
      </section>
    </template>

    <scene-skill-prefill-dialog :visible.sync="prefillVisible" :skill-id="editingId" :scene-name="form.name"
      :investigation-prompt="form.investigationPrompt" :decision-packet-prompt="form.decisionPacketPrompt"
      @apply="applyPrefill" />
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { getCurrentIdentity } from '../services/identity'
import SceneSkillPrefillDialog from '../components/SceneSkillPrefillDialog.vue'
import { SceneSkillPrefillResult } from '../api/generated'

import {
  BusinessSceneSkill,
  BusinessSceneSkillPayload,
  SceneSkillAttachment,
  createBusinessSceneSkill,
  deleteBusinessSceneSkill,
  deleteSceneSkillAttachment,
  listBusinessSceneSkills,
  listSceneSkillAttachments,
  updateBusinessSceneSkill,
  uploadSceneSkillAttachment,
} from '../services/business-skill-api'

interface SkillForm {
  name: string
  investigationPrompt: string
  decisionPacketPrompt: string
  processArtifactPrompt: string
}

const emptyForm = (): SkillForm => ({
  name: '',
  investigationPrompt: '',
  decisionPacketPrompt: '',
  processArtifactPrompt: '',
})

/** 维护业务场景：列表与全页表单切换，支持按目标 AI 预填。 */
export default Vue.extend({
  name: 'BusinessSkillCatalog',
  components: { SceneSkillPrefillDialog },
  data() {
    return {
      skills: [] as BusinessSceneSkill[],
      loading: false,
      saving: false,
      errorMessage: '',
      view: 'list' as 'list' | 'form',
      editingId: '' as string | '',
      form: emptyForm(),
      savedSnapshot: JSON.stringify(emptyForm()),
      prefillVisible: false,
      attachments: [] as SceneSkillAttachment[],
      pendingFile: null as File | null,
      pendingSummary: '',
      attachmentUploading: false,
    }
  },
  computed: {
    /** 列表与表单标题共用同一页面标题。 */
    pageTitle(): string {
      if (this.view === 'list') return '业务场景'
      return this.editingId ? '编辑业务场景' : '新增业务场景'
    },
    /** 与打开表单时的快照比较，判断是否需要离开确认。 */
    isDirty(): boolean {
      return this.view === 'form' && JSON.stringify(this.form) !== this.savedSnapshot
    },
  },
  created() {
    void this.loadSkills()
  },
  beforeRouteLeave(_to: unknown, _from: unknown, next: (value?: unknown) => void) {
    if (!this.isDirty) {
      next()
      return
    }
    this.confirmDiscard()
      .then(() => next())
      .catch(() => next(false))
  },
  methods: {
    /** 仅向场景作者或管理员显示维护入口，服务端再次校验。 */
    canManage(skill: BusinessSceneSkill): boolean {
      const identity = getCurrentIdentity()
      return Boolean(identity && (identity.roles.includes('PLATFORM_ADMIN') || identity.userId === skill.createdBy))
    },
    async loadSkills() {
      this.loading = true
      this.errorMessage = ''
      try {
        this.skills = await listBusinessSceneSkills()
      } catch (error: unknown) {
        this.errorMessage = error instanceof Error ? error.message : '业务场景暂时无法读取'
      } finally {
        this.loading = false
      }
    },
    /** 进入新增表单，快照用于离开确认。 */
    openCreate() {
      this.editingId = ''
      this.form = emptyForm()
      this.attachments = []
      this.pendingFile = null
      this.pendingSummary = ''
      this.savedSnapshot = JSON.stringify(this.form)
      this.view = 'form'
    },
    /** 从当前业务场景发起业务处理，携带场景标识供业务处理页自动回填。 */
    startHandling(skill: BusinessSceneSkill) {
      void this.$router.push({ path: '/handling', query: { skillId: skill.skillId, draft: String(Date.now()) } })
    },
    openEdit(skill: BusinessSceneSkill) {
      this.editingId = skill.skillId
      this.form = {
        name: skill.name,
        investigationPrompt: skill.investigationPrompt,
        decisionPacketPrompt: skill.decisionPacketPrompt,
        processArtifactPrompt: skill.processArtifactPrompt,
      }
      this.attachments = []
      this.pendingFile = null
      this.pendingSummary = ''
      this.savedSnapshot = JSON.stringify(this.form)
      this.view = 'form'
      void this.loadAttachments(skill.skillId)
    },
    /** 返回列表；有未保存修改时先确认。 */
    async backToList(): Promise<void> {
      if (this.isDirty) {
        try {
          await this.confirmDiscard()
        } catch {
          return
        }
      }
      this.resetForm()
      this.view = 'list'
    },
    /** 把 AI 预填结果写回对应字段：调查目标同时回填名称。 */
    applyPrefill(result: SceneSkillPrefillResult): void {
      if (result.target === 'INVESTIGATION') {
        if (result.name) this.form.name = result.name
        this.form.investigationPrompt = result.content
      } else if (result.target === 'DECISION_PACKET') {
        this.form.decisionPacketPrompt = result.content
      } else {
        this.form.processArtifactPrompt = result.content
      }
      this.notify('已应用到表单', 'success')
    },
    async save() {
      if (!this.form.name || !this.form.investigationPrompt) {
        this.notify('请补齐场景名称和调查提示词', 'warning')
        return
      }
      this.saving = true
      try {
        const payload: BusinessSceneSkillPayload = { ...this.form }
        if (this.editingId) await updateBusinessSceneSkill(this.editingId, payload)
        else await createBusinessSceneSkill(payload)
        this.resetForm()
        this.view = 'list'
        await this.loadSkills()
        this.notify('业务场景已保存', 'success')
      } catch (error: unknown) {
        this.notify(error instanceof Error ? error.message : '业务场景保存失败', 'error')
      } finally {
        this.saving = false
      }
    },
    async confirmDelete(skill: BusinessSceneSkill) {
      try {
        await (this as Vue & { $confirm: Function }).$confirm(`确认删除“${skill.name}”吗？`, '删除业务场景', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning',
        })
      } catch {
        return
      }
      try {
        await deleteBusinessSceneSkill(skill.skillId)
        await this.loadSkills()
        this.notify('业务场景已删除', 'success')
      } catch (error: unknown) {
        this.notify(error instanceof Error ? error.message : '业务场景删除失败', 'error')
      }
    },
    /** 丢弃修改前的统一确认。 */
    confirmDiscard(): Promise<unknown> {
      return (this as Vue & { $confirm: Function }).$confirm('当前有未保存的修改，确认返回并放弃吗？', '放弃修改', {
        confirmButtonText: '放弃',
        cancelButtonText: '继续编辑',
        type: 'warning',
      })
    },
    resetForm() {
      this.editingId = ''
      this.form = emptyForm()
      this.savedSnapshot = JSON.stringify(this.form)
      this.attachments = []
      this.pendingFile = null
      this.pendingSummary = ''
    },
    /** 读取当前场景的附件清单。 */
    async loadAttachments(skillId: string) {
      try {
        this.attachments = await listSceneSkillAttachments(skillId)
      } catch (error: unknown) {
        this.attachments = []
        this.notify(error instanceof Error ? error.message : '附件清单读取失败', 'error')
      }
    },
    /** 记录待上传文件。 */
    selectAttachmentFile(file: { raw?: File }) {
      this.pendingFile = file.raw || null
    },
    /** 上传当前选中的附件与摘要。 */
    async uploadAttachment() {
      if (!this.editingId || !this.pendingFile || !this.pendingSummary.trim()) return
      this.attachmentUploading = true
      try {
        await uploadSceneSkillAttachment(this.editingId, this.pendingFile, this.pendingSummary.trim())
        this.pendingFile = null
        this.pendingSummary = ''
        await this.loadAttachments(this.editingId)
        this.notify('附件已上传', 'success')
      } catch (error: unknown) {
        this.notify(error instanceof Error ? error.message : '附件上传失败', 'error')
      } finally {
        this.attachmentUploading = false
      }
    },
    /** 删除当前场景附件。 */
    async removeAttachment(attachment: SceneSkillAttachment) {
      if (!this.editingId) return
      try {
        await deleteSceneSkillAttachment(this.editingId, attachment.attachmentId)
        await this.loadAttachments(this.editingId)
        this.notify('附件已删除', 'success')
      } catch (error: unknown) {
        this.notify(error instanceof Error ? error.message : '附件删除失败', 'error')
      }
    },
    formatTime(value: string) {
      const date = new Date(value)
      return Number.isNaN(date.getTime()) ? '-' : date.toLocaleString('zh-CN', { hour12: false })
    },
    notify(message: string, type: 'success' | 'warning' | 'error') {
      ;(this as Vue & { $message: Function }).$message({ message, type })
    },
  },
})
</script>
<style scoped>
.skill-catalog-table {
  border: 0;
  border-radius: 0;
}
.skill-form-actions { display: flex; gap: 8px; }
/* 表单页标题与操作吸顶，滚动时始终可见返回入口。 */
.skill-form-heading {
  margin-bottom: 10px;
}
/* 全页表单：卡片固定占满可用高度，字段区在卡片内滚动，底部操作区固定。 */
.skill-form-card {
  display: flex;
  flex-direction: column;
  min-width: 0;
  height: calc(100vh - 172px);
  min-height: 460px;
  padding: 0;
  overflow: hidden;
  border-radius: 16px;
  box-shadow: 0 10px 24px rgba(83, 70, 48, 0.08);
}
.skill-form {
  display: flex;
  flex-direction: column;
  flex: 1 1 auto;
  min-height: 0;
  width: 100%;
  max-width: none;
  overflow-y: auto;
  padding: 16px 24px 4px;
}
.skill-form ::v-deep .el-form-item { flex: none; margin-bottom: 14px; }
.skill-form ::v-deep .el-form-item__label { padding-bottom: 6px; color: #4a4640; font-weight: 600; }
.skill-form ::v-deep .el-textarea__inner { resize: none; line-height: 1.7; }
/* 调查提示词吃掉剩余高度，空表单时不留大片空白。 */
.skill-form ::v-deep .el-form-item:nth-child(2) {
  display: flex;
  flex-direction: column;
  flex: 1 1 auto;
  min-height: 140px;
}
.skill-form ::v-deep .el-form-item:nth-child(2) .el-form-item__content {
  display: flex;
  flex: 1 1 auto;
  min-height: 0;
}
.skill-form ::v-deep .el-form-item:nth-child(2) .el-textarea {
  display: flex;
  flex: 1 1 auto;
  min-height: 0;
}
.skill-form ::v-deep .el-form-item:nth-child(2) .el-textarea__inner {
  height: 100% !important;
}
.skill-form-footer {
  flex: none;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 24px;
  background: #fffaf3;
  border-top: 1px dashed #e8e1d8;
}
.skill-attachments { display: flex; flex-direction: column; gap: 6px; width: 100%; }
.skill-attachment-item { display: flex; align-items: center; justify-content: space-between; gap: 8px; padding: 6px 10px; border: 1px solid #e7dfd4; border-radius: 6px; background: #fffdf9; }
.skill-attachment-item__meta { display: flex; flex-direction: column; gap: 1px; min-width: 0; }
.skill-attachment-item__name { color: #303133; font-size: 13px; font-weight: 600; }
.skill-attachment-item__summary { color: #817b73; font-size: 12px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.skill-attachment-upload { display: flex; gap: 8px; align-items: center; }
.skill-attachment-upload .el-input { flex: 1; }
.skill-attachment-hint { margin: 0; color: #909399; font-size: 13px; }
</style>
