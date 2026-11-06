<!-- 首页模型用量卡：展示个人或全平台近 30 天 token 用量与折线趋势。 -->
<template>
  <section class="usage-card" aria-label="模型 token 用量">
    <header class="usage-card__header">
      <div>
        <h2>模型用量</h2>
        <p class="usage-card__scope">{{ scopeLabel }}</p>
      </div>
      <span v-if="summary" class="usage-card__total">{{ formattedTotal }}<small>tokens</small></span>
    </header>
    <p v-if="loading" class="usage-card__state">正在读取用量…</p>
    <p v-else-if="errorMessage" class="usage-card__state usage-card__state--error">{{ errorMessage }}</p>
    <div v-else ref="chart" class="usage-card__chart" role="img" :aria-label="chartLabel" />
    <p class="usage-card__hint">近 30 天累计 · 仅统计真实模型调用</p>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import type { ECharts } from 'echarts/core'

import { TokenUsageSummary } from '../api/generated'
import { getCurrentIdentity } from '../services/identity'
import { fetchTokenUsage } from '../services/usage-api'

/** ECharts 工具提示参数的最小结构，避免在模板回调中使用 any。 */
interface TooltipParam {
  axisValue: string
  value: number
}

/** 用量卡只负责展示与图表生命周期，缓存与降级由服务层和自身状态共同保证。 */
export default Vue.extend({
  name: 'TokenUsageCard',
  data: () => ({
    loading: true,
    errorMessage: '',
    summary: null as TokenUsageSummary | null,
    chart: null as ECharts | null,
  }),
  computed: {
    /** 管理员看到全平台数据，普通用户看到本人数据。 */
    scopeLabel(): string {
      return this.summary?.scope === 'ALL' ? '全平台用量' : '我的用量'
    },
    formattedTotal(): string {
      return (this.summary?.totalTokens ?? 0).toLocaleString('zh-CN')
    },
    /** 为图表提供可读的文本替代。 */
    chartLabel(): string {
      return `${this.scopeLabel}近 30 天 token 用量折线图`
    },
  },
  mounted() {
    window.addEventListener('resize', this.resizeChart)
    void this.load()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart)
    this.disposeChart()
  },
  methods: {
    /** 读取用量并在数据就绪后渲染图表；失败只降级本卡片。 */
    async load(): Promise<void> {
      this.loading = true
      this.errorMessage = ''
      try {
        const identity = getCurrentIdentity()
        this.summary = await fetchTokenUsage(identity?.userId || 'anonymous')
      } catch {
        this.summary = null
        this.errorMessage = '用量暂不可用'
      } finally {
        this.loading = false
      }
      if (this.summary) {
        await this.$nextTick()
        await this.renderChart()
      }
    },
    /** 按需加载 ECharts 模块并绘制折线，避免图表库进入首屏主包。 */
    async renderChart(): Promise<void> {
      if (!this.summary || !this.$refs.chart) return
      try {
        const echarts = await import('echarts/core')
        const { LineChart } = await import('echarts/charts')
        const { GridComponent, TooltipComponent } = await import('echarts/components')
        const { CanvasRenderer } = await import('echarts/renderers')
        echarts.use([LineChart, GridComponent, TooltipComponent, CanvasRenderer])
        this.disposeChart()
        const instance = echarts.init(this.$refs.chart as HTMLElement)
        const points = this.summary.points
        instance.setOption({
          animationDuration: 400,
          grid: { left: 4, right: 8, top: 18, bottom: 0, containLabel: true },
          tooltip: {
            trigger: 'axis',
            backgroundColor: '#fffdf9',
            borderColor: '#e7dfd4',
            textStyle: { color: '#4a4d55', fontSize: 12 },
            formatter: (params: TooltipParam[]) => {
              const item = params[0]
              return item ? `${this.fullDate(item.axisValue)}<br/>${Number(item.value).toLocaleString('zh-CN')} tokens` : ''
            },
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: points.map((point) => point.date.slice(5)),
            axisLine: { lineStyle: { color: '#e7dfd4' } },
            axisTick: { show: false },
            axisLabel: { color: '#8c8478', fontSize: 11, interval: 4 },
          },
          yAxis: {
            type: 'value',
            splitNumber: 2,
            axisLabel: { color: '#8c8478', fontSize: 11 },
            splitLine: { lineStyle: { color: '#f1e7d8' } },
          },
          series: [{
            type: 'line',
            symbol: 'circle',
            symbolSize: 4,
            showSymbol: false,
            data: points.map((point) => point.tokens),
            lineStyle: { color: '#f9b334', width: 2 },
            itemStyle: { color: '#f9b334' },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(249, 179, 52, 0.28)' },
                  { offset: 1, color: 'rgba(249, 179, 52, 0.02)' },
                ],
              },
            },
          }],
        })
        this.chart = instance
      } catch {
        this.disposeChart()
        this.errorMessage = '用量暂不可用'
      }
    },
    /** 把 MM-DD 轴标签还原成完整日期，供 tooltip 展示。 */
    fullDate(axisValue: string): string {
      const point = this.summary?.points.find((item) => item.date.endsWith(axisValue))
      return point?.date || axisValue
    },
    resizeChart(): void {
      this.chart?.resize()
    },
    disposeChart(): void {
      this.chart?.dispose()
      this.chart = null
    },
  },
})
</script>

<style scoped>
.usage-card {
  display: flex;
  flex-direction: column;
  padding: 24px 24px 20px;
  border: 1px solid #e7dfd4;
  border-radius: 14px;
  background: #fffdf9;
  box-shadow: 0 8px 20px rgba(83, 70, 48, 0.05);
}

.usage-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.usage-card__header h2 {
  margin: 0;
  color: #4a4d55;
  font-size: 18px;
  font-weight: 600;
}

.usage-card__scope {
  margin: 4px 0 0;
  color: #8c8478;
  font-size: 13px;
}

.usage-card__total {
  color: #8a5c05;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
  white-space: nowrap;
}

.usage-card__total small {
  margin-left: 4px;
  color: #a99c88;
  font-size: 11px;
  font-weight: 400;
}

.usage-card__chart {
  flex: 1;
  min-height: 160px;
  margin-top: 14px;
}

.usage-card__state {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  min-height: 160px;
  margin: 14px 0 0;
  color: #a99c88;
  font-size: 13px;
}

.usage-card__state--error {
  color: #b06a3a;
}

.usage-card__hint {
  margin: 10px 0 0;
  color: #a99c88;
  font-size: 12px;
}
</style>
