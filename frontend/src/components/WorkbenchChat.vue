<!-- 模型协作聊天面板：只管理消息展示、输入事件与滚动，不直接调用业务接口。 -->
<template>
  <section class="workbench-chat" :style="availableHeight ? { height: `${availableHeight}px` } : null" aria-label="模型协作对话">
    <header class="workbench-chat__header">
      <span class="workbench-chat__avatar" aria-hidden="true">助</span>
      <div class="workbench-chat__heading">
        <div class="workbench-chat__title-row"><h3>模型协作</h3><span v-if="statusText" class="workbench-chat__status" role="status"><i class="el-icon-loading" aria-hidden="true"></i>{{ statusText }}</span></div>
        <div class="workbench-chat__metadata"><span class="workbench-chat__model" :title="modelText">当前模型：{{ modelText }}</span><span v-if="usageText" :title="usageDescription">{{ usageText }}</span></div>
      </div>
    </header>

    <div ref="history" class="workbench-chat__history" role="log" aria-label="协作消息" aria-live="polite" aria-relevant="additions text" tabindex="0">
      <div v-if="!messages.length" class="workbench-chat__welcome">
        <span class="workbench-chat__welcome-mark" aria-hidden="true"><i class="el-icon-message"></i></span>
        <h4>一起梳理处理方案</h4>
        <p>{{ analyzing ? '正在根据业务说明整理初稿，请稍候。' : '填写业务说明并开始分析后，可以在这里补充信息、讨论结论或调整推荐方案。' }}</p>
      </div>
      <article v-for="message in messages" :key="message.id" class="workbench-chat__message" :class="{ 'workbench-chat__message--operator': message.role === 'operator' }">
        <span class="workbench-chat__message-avatar" aria-hidden="true">{{ message.role === 'operator' ? '我' : '助' }}</span>
        <div class="workbench-chat__message-content">
          <span class="workbench-chat__author">{{ message.role === 'operator' ? '我' : '模型助手' }}</span>
          <p class="workbench-chat__bubble">{{ message.content }}</p>
        </div>
      </article>
      <div v-if="loading" class="workbench-chat__reply" role="status"><i class="el-icon-loading" aria-hidden="true"></i>正在整理回复…</div>
    </div>

    <footer class="workbench-chat__composer">
      <el-input :value="value" type="textarea" :rows="2" :disabled="!enabled || loading || analyzing" aria-label="模型协作输入" placeholder="补充信息，或说说希望如何调整方案" @input="updateValue" @keydown.native.ctrl.enter.prevent="sendMessage" />
      <el-button type="primary" :loading="loading" :disabled="!canSend" @click="sendMessage">发送</el-button>
    </footer>
  </section>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'
import { ModelCallMetadata, ModelRuntimeSummary } from '../api/generated'

interface ChatMessage { id: string; role: 'operator' | 'model'; content: string }

/** 独立的模型协作展示组件，业务请求和错误反馈由父页面管理。 */
export default Vue.extend({
  name: 'WorkbenchChat',
  props: {
    messages: { type: Array as PropType<ChatMessage[]>, required: true },
    value: { type: String, default: '' },
    enabled: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
    analyzing: { type: Boolean, default: false },
    runtimeModel: { type: Object as PropType<ModelRuntimeSummary | null>, default: null },
    runtimeLoading: { type: Boolean, default: false },
    metadata: { type: Object as PropType<ModelCallMetadata | null>, default: null },
  },
  /** 仅保存视口交互资源，不保存任何业务输入或调用统计。 */
  data() { return { availableHeight: 0, viewportActive: true, resizeObserver: null as ResizeObserver | null, viewportFrame: null as number | null } },
  computed: {
    /** 仅在初稿可用、无并发请求且输入非空时允许发送。 */
    canSend(): boolean { return this.enabled && !this.loading && !this.analyzing && Boolean(this.value.trim()) },
    /** 用中文状态区分首次分析、修改回复和保留的历史会话。 */
    statusText(): string {
      if (this.analyzing) return '正在分析业务'
      if (this.loading) return '正在回复'
      return ''
    },
    /** 当前配置与最近调用来源分离，未启用或读取失败时明确说明。 */
    modelText(): string {
      if (this.runtimeLoading) return '读取中'
      if (this.runtimeModel?.mode === 'STUB') return `${this.runtimeModel.modelName || '本地模拟模型'}（模拟）`
      if (this.runtimeModel) return this.runtimeModel.modelName || '未启用模型'
      return '信息暂不可用'
    },
    /** 仅展示最近一次建议生成响应的 Token 与耗时，不冒充任务累计消耗。 */
    usageText(): string {
      if (!this.metadata) return ''
      const duration = (Math.round(this.metadata.latencyMillis / 100) / 10).toFixed(1)
      const prefix = this.metadata.mode === 'STUB' ? '最近模拟调用' : '最近调用'
      return `${prefix} ${this.metadata.tokens.toLocaleString('zh-CN')} Token · ${duration} 秒`
    },
    /** 完整说明最近调用的实际模型及统计边界，避免配置变化造成误读。 */
    usageDescription(): string {
      return this.metadata ? `最近建议调用模型：${this.metadata.modelName}；不包含选表、查询规划及其他调用消耗。` : ''
    },
  },
  watch: {
    /** 新消息或重新分析替换会话后，都滚动到最新内容。 */
    messages(): void { this.scrollToLatest() },
    /** 回复占位出现或消失时同步滚动位置。 */
    loading(): void { this.scrollToLatest() },
  },
  /** 初次挂载时观察实际页面布局，并监听视口变化。 */
  mounted() {
    this.scrollToLatest()
    this.fitViewport()
    window.addEventListener('resize', this.scheduleViewportFit)
    window.addEventListener('scroll', this.scheduleViewportFit, { capture: true, passive: true })
    if (typeof ResizeObserver !== 'undefined') {
      this.resizeObserver = new ResizeObserver(this.scheduleViewportFit)
      const workspace = this.$el.closest('.task-workbench')
      if (workspace) this.resizeObserver.observe(workspace)
    }
  },
  /** 从缓存页面返回时恢复最新会话与可用高度。 */
  activated() { this.viewportActive = true; this.scrollToLatest(); this.scheduleViewportFit() },
  /** 页面失活时取消待执行的布局帧，避免测量隐藏节点。 */
  deactivated() { this.viewportActive = false; this.cancelViewportFit() },
  /** 消息和任务摘要布局改变后重新测量顶部位置。 */
  updated() { this.scheduleViewportFit() },
  /** 销毁时释放观察器与视口监听。 */
  beforeDestroy() {
    this.viewportActive = false
    this.resizeObserver?.disconnect()
    if (typeof window !== 'undefined') {
      window.removeEventListener('resize', this.scheduleViewportFit)
      window.removeEventListener('scroll', this.scheduleViewportFit, true)
    }
    this.cancelViewportFit()
  },
  methods: {
    /** 合并同一帧的布局通知，减少 ResizeObserver 回调中的同步写入。 */
    scheduleViewportFit(): void {
      if (!this.viewportActive || this.viewportFrame !== null || typeof window === 'undefined') return
      this.viewportFrame = window.requestAnimationFrame(() => { this.viewportFrame = null; this.fitViewport() })
    },
    /** 清理尚未执行的布局帧。 */
    cancelViewportFit(): void {
      if (this.viewportFrame !== null && typeof window !== 'undefined') window.cancelAnimationFrame(this.viewportFrame)
      this.viewportFrame = null
    },
    /** 按实际顶部位置填充剩余视口，保留底部 16px，较小窗口保留基本输入高度。 */
    fitViewport(): void {
      if (!this.viewportActive || typeof window === 'undefined' || !this.$el) return
      const top = Math.max(0, this.$el.getBoundingClientRect().top)
      const height = Math.max(280, Math.floor(window.innerHeight - top - 16))
      if (height !== this.availableHeight) this.availableHeight = height
    },
    /** 通过 v-model 事件把输入交给页面，不修改 props。 */
    updateValue(value: string): void { this.$emit('input', value) },
    /** 按钮和 Ctrl+Enter 复用同一发送校验。 */
    sendMessage(): void { if (this.canSend) this.$emit('send') },
    /** 等待消息 DOM 更新后滚动，已销毁或未挂载时安全跳过。 */
    scrollToLatest(): void {
      void this.$nextTick(() => {
        const history = this.$refs.history as HTMLElement | undefined
        if (history) history.scrollTop = history.scrollHeight
      })
    },
  },
})
</script>

<style scoped>
.workbench-chat { display: flex; flex-direction: column; height: calc(100vh - 150px); min-height: 280px; overflow: hidden; border: 1px solid #e7dfd4; border-radius: 12px; background: #fffdf9; box-shadow: 0 4px 16px rgba(83, 70, 48, .05); }
.workbench-chat__header { display: flex; flex: 0 0 auto; align-items: center; gap: 10px; padding: 12px 14px; border-bottom: 1px solid #eee5d9; background: #fffaf2; }
.workbench-chat__avatar, .workbench-chat__message-avatar { display: grid; flex: 0 0 auto; place-items: center; width: 38px; height: 38px; border-radius: 12px; background: #f3e3ca; color: #9a5719; font-size: 16px; font-weight: 600; }
.workbench-chat__heading { flex: 1; min-width: 0; }
.workbench-chat__title-row { display: flex; flex-wrap: wrap; align-items: center; gap: 8px 12px; }
.workbench-chat__heading h3 { margin: 0; color: #302c26; font-size: 17px; }
.workbench-chat__metadata { display: flex; flex-wrap: wrap; gap: 4px 12px; margin-top: 6px; color: #817466; font-size: 14px; line-height: 20px; }
.workbench-chat__model { min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.workbench-chat__status { display: flex; align-items: center; gap: 6px; color: #817466; font-size: 14px; }
.workbench-chat__history { display: flex; flex: 1 1 auto; min-height: 0; flex-direction: column; gap: 18px; padding: 20px 16px; overflow-y: auto; overscroll-behavior: contain; background: #faf8f4; }
.workbench-chat__welcome { margin: auto; max-width: 320px; padding: 24px 8px; text-align: center; }
.workbench-chat__welcome-mark { display: grid; place-items: center; width: 54px; height: 54px; margin: 0 auto 16px; border: 1px solid #ead9bd; border-radius: 18px; background: #fffaf1; color: #b47530; font-size: 24px; }
.workbench-chat__welcome h4 { margin: 0 0 10px; color: #51483d; font-size: 16px; }
.workbench-chat__welcome p { margin: 0; color: #857768; font-size: 14px; line-height: 1.8; }
.workbench-chat__message { display: flex; align-items: flex-start; gap: 8px; }
.workbench-chat__message-avatar { width: 30px; height: 30px; border-radius: 9px; font-size: 14px; }
.workbench-chat__message-content { min-width: 0; max-width: calc(100% - 42px); }
.workbench-chat__author { display: block; margin: 0 2px 6px; color: #847667; font-size: 14px; }
.workbench-chat__bubble { margin: 0; padding: 12px 14px; border: 1px solid #e8dfd3; border-radius: 2px 14px 14px; background: #fff; color: #49433b; font-size: 14px; line-height: 1.8; white-space: pre-wrap; overflow-wrap: anywhere; }
.workbench-chat__message--operator { flex-direction: row-reverse; }
.workbench-chat__message--operator .workbench-chat__author { text-align: right; }
.workbench-chat__message--operator .workbench-chat__message-avatar { background: #e7eaf0; color: #596579; }
.workbench-chat__message--operator .workbench-chat__bubble { border-color: #ebd3ad; border-radius: 14px 2px 14px 14px; background: #f9ecd8; color: #654d30; }
.workbench-chat__reply { display: flex; gap: 8px; align-items: center; color: #957449; font-size: 14px; }
.workbench-chat__composer { display: flex; align-items: flex-end; gap: 10px; flex: 0 0 auto; padding: 12px 14px; border-top: 1px solid #eee5d9; background: #fffdf9; }
.workbench-chat__composer .el-textarea { flex: 1; min-width: 0; }
.workbench-chat__composer > .el-button { flex: 0 0 auto; margin: 0; }
.workbench-chat__composer ::v-deep textarea { border-color: #e1d6c6; border-radius: 8px; resize: vertical; min-height: 60px; max-height: 120px; font-size: 14px; line-height: 1.7; }
.workbench-chat :focus-visible { outline: 2px solid #c87422; outline-offset: 2px; }
</style>
