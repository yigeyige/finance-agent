<!-- 平台管理员只读查看本进程最近一次异常，进入时读取一次，不自动刷新。 -->
<template>
  <section class="entry-page latest-exception-page">
    <div class="page-heading entry-page-heading"><h2>异常堆栈</h2></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <el-card v-loading="loading" shadow="never" class="entry-content-card latest-exception-card">
      <div v-if="!loading && !errorMessage && !snapshot" class="latest-exception-empty" role="status">暂无异常记录</div>
      <template v-if="snapshot">
        <dl class="latest-exception-meta">
          <div><dt>发生时间</dt><dd>{{ formatTime(snapshot.occurredAt) }}</dd></div>
          <div><dt>异常来源</dt><dd>{{ sourceLabel }}</dd></div>
          <div><dt>异常类型</dt><dd>{{ snapshot.exceptionType }}</dd></div>
        </dl>
        <h3 class="latest-exception-heading">堆栈信息</h3>
        <pre class="latest-exception-stack">{{ snapshot.stackTrace }}</pre>
      </template>
    </el-card>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { fetchLatestException, LatestExceptionSnapshot } from '../services/latest-exception-api'

/** 平台管理员只读异常页面，进入时从服务端内存快照读取一次。 */
export default Vue.extend({
  name: 'LatestException',
  data: () => ({ snapshot: null as LatestExceptionSnapshot | null, loading: false, errorMessage: '' }),
  computed: {
    /** 将受控来源编码转换为页面中文标签。 */
    sourceLabel(): string { return this.snapshot?.source === 'MODEL_INTERACTION' ? '大模型交互' : '业务接口' },
  },
  created() { void this.load() },
  methods: {
    /** 页面进入时读取一次，无记录与失败分别展示。 */
    async load(): Promise<void> {
      this.loading = true
      this.snapshot = null
      this.errorMessage = ''
      try { this.snapshot = (await fetchLatestException()).item }
      catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '异常堆栈暂时无法读取' }
      finally { this.loading = false }
    },
    /** 按中文地区时间展示服务端 UTC 时间。 */
    formatTime(value: string): string {
      const date = new Date(value)
      return Number.isNaN(date.getTime()) ? '时间无法识别' : date.toLocaleString('zh-CN', { hour12: false })
    },
  },
})
</script>

<style scoped>
.latest-exception-card { min-height: 220px; }
.latest-exception-empty { padding: 56px 16px; text-align: center; color: #6b7280; }
.latest-exception-meta { display: flex; flex-wrap: wrap; gap: 16px 32px; margin: 0 0 24px; }
.latest-exception-meta div { min-width: 180px; }
.latest-exception-meta dt { margin-bottom: 6px; color: #6b7280; }
.latest-exception-meta dd { margin: 0; color: #172033; overflow-wrap: anywhere; }
.latest-exception-heading { margin: 0 0 12px; font-size: 16px; }
.latest-exception-stack { max-height: calc(100vh - 300px); min-height: 160px; margin: 0; padding: 16px; overflow: auto; border: 1px solid #e1dfd9; border-radius: 8px; background: #f8f7f4; color: #273244; font-size: 14px; line-height: 1.6; white-space: pre; }
</style>
