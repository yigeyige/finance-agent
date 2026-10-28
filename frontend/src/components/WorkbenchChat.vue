<!-- 模型协作聊天面板：只管理消息展示、输入事件与滚动，不直接调用业务接口。 -->
<template>
  <section class="workbench-chat" aria-label="模型协作对话">
    <header class="workbench-chat__header">
      <span class="workbench-chat__avatar" aria-hidden="true">助</span>
      <div class="workbench-chat__heading">
        <h3>模型协作</h3>
        <span class="workbench-chat__status" role="status"><i :class="analyzing || loading ? 'el-icon-loading' : 'workbench-chat__status-dot'" aria-hidden="true"></i>{{ statusText }}</span>
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
      <el-input :value="value" type="textarea" :rows="3" :disabled="!enabled || loading || analyzing" aria-label="模型协作输入" placeholder="补充信息，或说说希望如何调整方案" @input="updateValue" @keydown.native.ctrl.enter.prevent="sendMessage" />
      <div class="workbench-chat__actions">
        <el-button type="primary" :loading="loading" :disabled="!canSend" @click="sendMessage">发送消息</el-button>
      </div>
    </footer>
  </section>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue'

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
  },
  computed: {
    /** 仅在初稿可用、无并发请求且输入非空时允许发送。 */
    canSend(): boolean { return this.enabled && !this.loading && !this.analyzing && Boolean(this.value.trim()) },
    /** 用中文状态区分首次分析、修改回复和保留的历史会话。 */
    statusText(): string {
      if (this.analyzing) return '正在分析业务'
      if (this.loading) return '正在回复'
      if (this.enabled) return '可以继续交流'
      return this.messages.length ? '会话已保留' : '等待业务说明'
    },
  },
  watch: {
    /** 新消息或重新分析替换会话后，都滚动到最新内容。 */
    messages(): void { this.scrollToLatest() },
    /** 回复占位出现或消失时同步滚动位置。 */
    loading(): void { this.scrollToLatest() },
  },
  /** 初次挂载时显示最新消息。 */
  mounted() { this.scrollToLatest() },
  /** 从缓存页面返回时恢复到最新会话。 */
  activated() { this.scrollToLatest() },
  methods: {
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
.workbench-chat { display: flex; flex-direction: column; height: min(720px, calc(100vh - 104px)); min-height: 500px; overflow: hidden; border: 1px solid #e7dfd4; border-radius: 12px; background: #fffdf9; box-shadow: 0 4px 16px rgba(83, 70, 48, .05); }
.workbench-chat__header { display: flex; flex: 0 0 auto; align-items: center; gap: 12px; padding: 16px; border-bottom: 1px solid #eee5d9; background: #fffaf2; }
.workbench-chat__avatar, .workbench-chat__message-avatar { display: grid; flex: 0 0 auto; place-items: center; width: 38px; height: 38px; border-radius: 12px; background: #f3e3ca; color: #9a5719; font-size: 16px; font-weight: 600; }
.workbench-chat__heading h3 { margin: 0 0 5px; color: #302c26; font-size: 17px; }
.workbench-chat__status { display: flex; align-items: center; gap: 6px; color: #817466; font-size: 14px; }
.workbench-chat__status-dot { width: 6px; height: 6px; border-radius: 50%; background: #b79059; }
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
.workbench-chat__composer { flex: 0 0 auto; padding: 14px 16px; border-top: 1px solid #eee5d9; background: #fffdf9; }
.workbench-chat__composer ::v-deep textarea { border-color: #e1d6c6; border-radius: 8px; resize: vertical; min-height: 80px; max-height: 160px; font-size: 14px; line-height: 1.7; }
.workbench-chat__actions { display: flex; justify-content: flex-end; margin-top: 10px; }
.workbench-chat :focus-visible { outline: 2px solid #c87422; outline-offset: 2px; }
@media (max-width: 1000px) { .workbench-chat { height: 560px; min-height: 400px; } }
</style>
