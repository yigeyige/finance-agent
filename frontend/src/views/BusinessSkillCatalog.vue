<template>
  <section class="evidence-page list-page skill-catalog entry-page">
    <div class="page-heading entry-page-heading">
      <h2>业务场景 Skill</h2>
    </div>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />

    <template v-else>
      <section class="entry-overview" aria-label="业务场景概览">
        <div class="entry-overview__main">
          <span class="entry-overview__marker" aria-hidden="true"></span>
          <div><span>当前维护</span><strong>业务场景 Skill</strong></div>
        </div>
        <div class="entry-overview__item"><span>场景总数</span><strong>{{ skills.length }}<small>项</small></strong></div>
        <div class="entry-overview__item"><span>管理范围</span><strong>集中维护</strong></div>
      </section>

      <section class="entry-content-card">
        <div class="entry-content-toolbar">
          <span class="entry-content-toolbar__meta">共 {{ skills.length }} 项</span>
          <el-button type="primary" @click="openCreateDialog">新增业务场景</el-button>
        </div>
        <div class="standard-table-panel skill-catalog-table">
          <el-table v-loading="loading" :data="skills" row-key="skillId" class="standard-data-table" border stripe
            empty-text="暂无业务场景">
            <el-table-column prop="name" label="业务名称" min-width="180" show-overflow-tooltip />
            <el-table-column label="处理方式" width="128">
              <template slot-scope="scope">
                <el-tag :type="scope.row.handlingType === 'ADJUST' ? 'warning' : 'success'" effect="plain">
                  {{ handlingTypeLabel(scope.row.handlingType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="premiseType" label="业务场景" min-width="180" show-overflow-tooltip />
            <el-table-column prop="description" label="业务说明" min-width="320" show-overflow-tooltip />
            <el-table-column label="更新时间" width="190">
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

    <el-dialog :title="editingId ? '编辑业务场景' : '新增业务场景'" :visible.sync="dialogVisible" width="560px"
      :close-on-click-modal="false" @closed="resetForm">
      <el-form :model="form" label-width="92px">
        <el-form-item label="业务名称" required>
          <el-input v-model.trim="form.name" maxlength="100" show-word-limit placeholder="例如：人员变更处理" />
        </el-form-item>
        <el-form-item label="处理方式" required>
          <el-radio-group v-model="form.handlingType">
            <el-radio label="INVESTIGATE">调查核查</el-radio>
            <el-radio label="ADJUST">业务调整</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="业务场景" required>
          <el-input v-model.trim="form.premiseType" maxlength="100" placeholder="例如：人员变更后权限处理" />
        </el-form-item>
        <el-form-item label="业务说明" required>
          <el-input v-model.trim="form.description" type="textarea" :rows="4" maxlength="1000" show-word-limit
            placeholder="说明该场景需要处理的业务内容" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button :disabled="saving" @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'

import {
  BusinessHandlingType,
  BusinessSceneSkill,
  BusinessSceneSkillPayload,
  createBusinessSceneSkill,
  deleteBusinessSceneSkill,
  listBusinessSceneSkills,
  updateBusinessSceneSkill,
} from '../services/business-skill-api'

interface SkillForm {
  name: string
  description: string
  handlingType: BusinessHandlingType
  premiseType: string
}

const emptyForm = (): SkillForm => ({
  name: '',
  description: '',
  handlingType: 'INVESTIGATE',
  premiseType: '',
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
      void this.$router.push({ path: '/', query: { skillId: skill.skillId } })
    },
    openEditDialog(skill: BusinessSceneSkill) {
      this.editingId = skill.skillId
      this.form = {
        name: skill.name,
        description: skill.description,
        handlingType: skill.handlingType,
        premiseType: skill.premiseType,
      }
      this.dialogVisible = true
    },
    async save() {
      if (!this.form.name || !this.form.description || !this.form.premiseType) {
        this.notify('请补齐业务名称、业务场景和业务说明', 'warning')
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
    },
    handlingTypeLabel(type: BusinessHandlingType) {
      return type === 'ADJUST' ? '业务调整' : '调查核查'
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
</style>
