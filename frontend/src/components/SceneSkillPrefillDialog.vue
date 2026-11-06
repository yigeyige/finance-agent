<!-- 业务场景 AI 预填弹窗：按目标生成单个提示词字段，预览后由页面应用。 -->
<template>
  <el-dialog v-workbench-dialog custom-class="workbench-dialog scene-prefill-dialog" title="AI 预填" width="900px"
    :visible="visible" :close-on-click-modal="false" :close-on-press-escape="!busy" @update:visible="syncVisible">
    <div class="scene-prefill">
      <p class="scene-prefill__hint">
        输入业务描述，可选择一个数据库实例让模型参考表名。内容会发送给当前启用的模型进行解析，生成结果仅用于预览，点击应用后才写入表单。
      </p>
      <el-input v-model="text" type="textarea" maxlength="4000" show-word-limit :disabled="busy"
        placeholder="例如：核查 basedata 中费用报销的重复审批单据，关注报销单号、审批人、审批时间与金额。" />
      <div class="scene-prefill__instance">
        <span class="scene-prefill__instance-label">数据库实例</span>
        <el-select v-model="databaseConfigId" clearable filterable :loading="instancesLoading" :disabled="busy"
          placeholder="不选择则不使用表结构" class="scene-prefill__instance-select">
          <el-option v-for="instance in instances" :key="instance.configId" :label="instance.name" :value="instance.configId" />
        </el-select>
      </div>
      <div class="scene-prefill__actions">
        <el-button :loading="loadingTarget === 'INVESTIGATION'" :disabled="busy" @click="generate('INVESTIGATION')">生成调查提示词</el-button>
        <el-button :loading="loadingTarget === 'DECISION_PACKET'" :disabled="busy" @click="generate('DECISION_PACKET')">生成决策包提示词</el-button>
        <el-button :loading="loadingTarget === 'PROCESS_ARTIFACT'" :disabled="busy" @click="generate('PROCESS_ARTIFACT')">生成流程文件提示词</el-button>
      </div>
      <p v-if="error" class="scene-prefill__error">{{ error }}</p>
      <section v-if="result" class="scene-prefill__preview">
        <header class="scene-prefill__preview-header">
          <h4>{{ targetLabel(result.target) }}</h4>
          <span class="scene-prefill__stats">使用表 {{ result.tableCount }} 张 · 排除 {{ result.excludedTableCount }} 张</span>
        </header>
        <p v-if="result.target === 'INVESTIGATION'" class="scene-prefill__name">场景名称：{{ result.name || '—' }}</p>
        <el-input :value="result.content" type="textarea" readonly class="scene-prefill__content" />
        <p v-if="result.warning" class="scene-prefill__warning">{{ result.warning }}</p>
      </section>
    </div>
    <span slot="footer" class="workbench-dialog__actions">
      <el-button :disabled="busy" @click="syncVisible(false)">关闭</el-button>
      <el-button type="primary" :disabled="!result || busy" @click="apply">应用到表单</el-button>
    </span>
  </el-dialog>
</template>

<script lang="ts">
import Vue from 'vue'

import { SceneSkillPrefillResult, SceneSkillPrefillTarget } from '../api/generated'
import { EmsDatabaseInstance, listEnabledDatabaseInstances } from '../services/ems-database-config-api'
import { requestSceneSkillPrefill } from '../services/scene-skill-prefill-api'

/** 场景预填弹窗只负责单目标生成与预览，字段落表由页面统一处理。 */
export default Vue.extend({
  name: 'SceneSkillPrefillDialog',
  props: {
    visible: { type: Boolean, default: false },
    skillId: { type: String, default: '' },
    sceneName: { type: String, default: '' },
    investigationPrompt: { type: String, default: '' },
    decisionPacketPrompt: { type: String, default: '' },
  },
  data: () => ({
    text: '',
    databaseConfigId: '',
    instances: [] as EmsDatabaseInstance[],
    instancesLoading: false,
    loadingTarget: '' as '' | SceneSkillPrefillTarget,
    result: null as SceneSkillPrefillResult | null,
    error: '',
  }),
  computed: {
    /** 任一次生成进行中时禁用所有生成入口，避免并发调用。 */
    busy(): boolean {
      return this.loadingTarget !== ''
    },
  },
  watch: {
    visible(value: boolean): void {
      if (!value) return
      this.error = ''
      this.result = null
      void this.loadInstances()
    },
  },
  methods: {
    syncVisible(value: boolean): void {
      if (this.busy && !value) return
      this.$emit('update:visible', value)
    },
    /** 启用实例只加载一次；失败时保留空列表，仍可不用表结构生成。 */
    async loadInstances(): Promise<void> {
      if (this.instances.length || this.instancesLoading) return
      this.instancesLoading = true
      try {
        this.instances = await listEnabledDatabaseInstances()
      } catch {
        this.instances = []
      } finally {
        this.instancesLoading = false
      }
    },
    /** 按目标生成；决策包与流程文件缺少前置提示词时直接阻断。 */
    async generate(target: SceneSkillPrefillTarget): Promise<void> {
      if (this.busy) return
      const text = this.text.trim()
      if (text.length < 10 || text.length > 4000) {
        this.error = 'AI 预填内容需为 10–4000 字'
        return
      }
      if (target === 'DECISION_PACKET' && !this.investigationPrompt.trim()) {
        this.error = '请先在表单填写或生成调查提示词'
        return
      }
      if (target === 'PROCESS_ARTIFACT' && !this.decisionPacketPrompt.trim()) {
        this.error = '请先在表单填写或生成决策包提示词'
        return
      }
      this.loadingTarget = target
      this.error = ''
      this.result = null
      try {
        this.result = await requestSceneSkillPrefill({
          target,
          text,
          skillId: this.skillId || undefined,
          databaseConfigId: this.databaseConfigId || undefined,
          sceneName: this.sceneName || undefined,
          investigationPrompt: this.investigationPrompt || undefined,
          decisionPacketPrompt: this.decisionPacketPrompt || undefined,
        })
      } catch (error: unknown) {
        this.error = error instanceof Error ? error.message : 'AI 预填暂时不可用，请稍后重试'
      } finally {
        this.loadingTarget = ''
      }
    },
    /** 把当前预览交给页面应用，并关闭弹窗。 */
    apply(): void {
      if (!this.result) return
      this.$emit('apply', this.result)
      this.$emit('update:visible', false)
    },
    targetLabel(target: SceneSkillPrefillTarget): string {
      return target === 'INVESTIGATION' ? '调查提示词' : target === 'DECISION_PACKET' ? '决策包提示词' : '流程文件提示词'
    },
  },
})
</script>

<style scoped>
/* 大弹窗默认高度，正文纵向撑满；拖动缩放时预览区跟随伸缩。 */
::v-deep .scene-prefill-dialog {
  display: flex;
  flex-direction: column;
  height: min(680px, calc(100vh - 48px));
}
::v-deep .scene-prefill-dialog .el-dialog__body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
.scene-prefill {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  gap: 10px;
}
.scene-prefill__hint {
  flex: none;
  margin: 0;
  color: #8c8478;
  font-size: 13px;
  line-height: 1.7;
}
.scene-prefill .el-textarea {
  display: flex;
  flex-direction: column;
  flex: 1 1 0;
  min-height: 120px;
}
.scene-prefill ::v-deep .el-textarea__inner {
  height: 100% !important;
  resize: none;
}
.scene-prefill__instance {
  flex: none;
  display: flex;
  align-items: center;
  gap: 10px;
}
.scene-prefill__instance-label {
  color: #4a4640;
  font-size: 13px;
  white-space: nowrap;
}
.scene-prefill__instance-select {
  flex: 1;
}
.scene-prefill__actions {
  flex: none;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.scene-prefill__error {
  flex: none;
  margin: 0;
  color: #c45656;
  font-size: 13px;
}
.scene-prefill__preview {
  display: flex;
  flex-direction: column;
  flex: 0 0 auto;
  max-height: 45%;
  gap: 8px;
  overflow-y: auto;
  padding: 12px 14px;
  border: 1px solid #e7dfd4;
  border-radius: 10px;
  background: #fffaf1;
}
.scene-prefill__preview-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
}
.scene-prefill__preview-header h4 {
  margin: 0;
  color: #4a4d55;
  font-size: 15px;
}
.scene-prefill__stats {
  color: #a99c88;
  font-size: 12px;
}
.scene-prefill__name {
  margin: 0;
  color: #303133;
  font-size: 13px;
}
.scene-prefill__content ::v-deep .el-textarea__inner {
  min-height: 140px !important;
  max-height: 260px;
  overflow-y: auto;
  color: #4b5563;
  font-size: 13px;
  line-height: 1.7;
}
.scene-prefill__warning {
  margin: 0;
  color: #b06a3a;
  font-size: 12px;
}
</style>
