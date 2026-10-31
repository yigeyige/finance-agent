<!-- 业务工作区页面，复用公共表格与弹窗交互，保持既有业务流程。 -->
<template>
  <section class="evidence-page list-page skill-catalog entry-page">
    <div class="page-heading entry-page-heading">
      <h2>业务场景 Skill</h2>
    </div>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />

    <template v-else>
      <section class="entry-content-card">
        <div class="entry-content-toolbar">
          <span class="entry-content-toolbar__meta">共 {{ skills.length }} 项</span>
          <el-button type="primary" @click="openCreateDialog">新增业务场景</el-button>
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
                <el-button type="text" @click="startHandling(scope.row)">开始处理</el-button>
                <el-button type="text" @click="openEditDialog(scope.row)">编辑</el-button>
                <el-button type="text" class="skill-delete-action" @click="confirmDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </section>
    </template>

    <el-dialog v-workbench-dialog custom-class="workbench-dialog" :title="editingId ? '编辑业务场景' : '新增业务场景'" :visible.sync="dialogVisible" width="720px"
      :close-on-click-modal="false" @closed="resetForm">
      <el-form :model="form" label-position="top" class="workbench-dialog-form">
        <el-form-item label="场景名称" required>
          <el-input v-model.trim="form.name" :maxlength="100" show-word-limit placeholder="例如：人员变更处理" />
        </el-form-item>
        <el-form-item label="调查提示词" required>
          <el-input v-model.trim="form.investigationPrompt" type="textarea" :rows="8" :maxlength="2000" show-word-limit
            placeholder="说明该场景调查时需要的业务背景、关键表字段语义与查询注意事项" />
        </el-form-item>
        <el-form-item label="决策包提示词">
          <el-input v-model.trim="form.decisionPacketPrompt" type="textarea" :rows="8" :maxlength="2000" show-word-limit
            placeholder="可选，说明生成业务决策包时的结论、责任与后续处理要求" />
        </el-form-item>
        <el-form-item label="流程文件提示词">
          <el-input v-model.trim="form.processArtifactPrompt" type="textarea" :rows="8" :maxlength="4000" show-word-limit
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
      <span slot="footer" class="workbench-dialog__actions">
        <el-button :disabled="saving" @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'

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

/** 维护业务场景 Skill，供管理员集中新增、编辑与删除。 */
export default Vue.extend({
  name: 'BusinessSkillCatalog',
  data() {
    return {
      skills: [] as BusinessSceneSkill[],
      loading: false,
      saving: false,
      errorMessage: '',
      dialogVisible: false,
      editingId: '' as string | '',
      form: emptyForm(),
      attachments: [] as SceneSkillAttachment[],
      pendingFile: null as File | null,
      pendingSummary: '',
      attachmentUploading: false,
    }
  },
  created() {
    void this.loadSkills()
  },
  methods: {
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
    openCreateDialog() {
      this.editingId = ''
      this.form = emptyForm()
      this.dialogVisible = true
    },
    /** 从当前 Skill 发起业务处理，保留其标识供任务创建持久化。 */
    startHandling(skill: BusinessSceneSkill) {
      void this.$router.push({ path: '/', query: { skillId: skill.skillId, draft: String(Date.now()) } })
    },
    openEditDialog(skill: BusinessSceneSkill) {
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
      this.dialogVisible = true
      void this.loadAttachments(skill.skillId)
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
        this.dialogVisible = false
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
    resetForm() {
      this.editingId = ''
      this.form = emptyForm()
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
.skill-attachments { display: flex; flex-direction: column; gap: 8px; width: 100%; }
.skill-attachment-item { display: flex; align-items: center; justify-content: space-between; gap: 8px; padding: 8px 12px; border: 1px solid #e7dfd4; border-radius: 8px; background: #fffdf9; }
.skill-attachment-item__meta { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.skill-attachment-item__name { font-weight: 600; color: #303133; }
.skill-attachment-item__summary { color: #817b73; font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.skill-attachment-upload { display: flex; gap: 8px; align-items: center; }
.skill-attachment-upload .el-input { flex: 1; }
.skill-attachment-hint { margin: 0; color: #909399; font-size: 13px; }
</style>
