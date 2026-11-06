<!-- 工作台首页：介绍 Agent 定位与使用流程，并提供业务处理快捷入口。 -->
<template>
  <section class="home-page">
    <header class="home-hero">
      <div class="home-hero__content">
        <p class="home-hero__eyebrow">财务共享 · 智能运维</p>
        <h1>财务智能运维Agent</h1>
        <p class="home-hero__subtitle">从业务问题到处置方案，一次走完完整闭环</p>
        <ul class="home-hero__tags">
          <li>自动初筛</li>
          <li>模型协作</li>
          <li>决策可追溯</li>
        </ul>
      </div>
      <div class="home-hero__art" aria-hidden="true">
        <span class="home-hero__ring home-hero__ring--lg" />
        <span class="home-hero__ring home-hero__ring--sm" />
        <span class="home-hero__tile home-hero__tile--a" />
        <span class="home-hero__tile home-hero__tile--b" />
        <span class="home-hero__dot home-hero__dot--a" />
        <span class="home-hero__dot home-hero__dot--b" />
        <span class="home-hero__mark">财</span>
      </div>
    </header>

    <div class="home-main">
      <section class="home-steps" aria-label="使用流程">
        <div class="home-steps__heading">
          <div class="home-steps__title">
            <h2>三步完成一次业务处理</h2>
          </div>
          <el-button type="primary" class="home-steps__action" @click="startHandling">进入业务处理</el-button>
        </div>
        <ol class="home-steps__list">
          <li v-for="(step, index) in steps" :key="step.title">
            <span class="home-steps__index" aria-hidden="true">{{ index + 1 }}</span>
            <div class="home-steps__text">
              <h3>{{ step.title }}</h3>
              <p>{{ step.description }}</p>
            </div>
          </li>
        </ol>
        <p class="home-steps__footnote">从一句业务说明出发，模型在受控范围内取数并生成初稿，最终形成可复核、可交接的决策包。</p>
      </section>
      <token-usage-card />
    </div>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'

import TokenUsageCard from '../components/TokenUsageCard.vue'

/** 首页承担介绍、导航与模型用量展示，不读取任务业务数据。 */
export default Vue.extend({
  name: 'HomePage',
  components: { TokenUsageCard },
  data: () => ({
    steps: [
      { title: '业务说明', description: '填写业务标题与说明，按需选择业务场景、数据库实例并上传附件材料。' },
      { title: '模型协作', description: '模型在受控范围内取数、生成初稿，可多轮补充信息并修正处理方案。' },
      { title: '决策包', description: '确认结论与证据后生成决策包，支持复核、发布与责任交接。' },
    ],
  }),
  methods: {
    /** 进入业务处理页，并带上草稿标识以重置上一次未完成的表单。 */
    startHandling(): void {
      void this.$router.push({ path: '/handling', query: { draft: String(Date.now()) } })
    },
  },
})
</script>

<style scoped>
.home-page {
  display: grid;
  gap: 18px;
  max-width: 1440px;
  margin: 0 auto;
  padding-top: 4px;
}

/* Hero：淡橙渐变 + 几何点缀，承担首页主视觉，不引入图片资源。 */
.home-hero {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  align-items: center;
  gap: 24px;
  overflow: hidden;
  padding: 40px 40px;
  border: 1px solid #f0dcb9;
  border-radius: 22px;
  background:
    radial-gradient(120% 150% at 86% 6%, #ffe4b3 0%, rgba(255, 228, 179, 0) 58%),
    linear-gradient(118deg, #fffaf0 0%, #fdf1da 54%, #fbe3ba 100%);
  box-shadow: 0 14px 32px rgba(160, 118, 40, 0.1);
}

.home-hero__content {
  position: relative;
  z-index: 1;
  min-width: 0;
}

.home-hero__eyebrow {
  margin: 0 0 12px;
  color: #a9761c;
  font-size: 12px;
  letter-spacing: 0.18em;
}

.home-hero h1 {
  margin: 0;
  color: #3d372f;
  font-size: 40px;
  font-weight: 700;
  letter-spacing: 0.01em;
  line-height: 1.2;
}

.home-hero__subtitle {
  max-width: 560px;
  margin: 14px 0 22px;
  color: #7b7164;
  font-size: 16px;
  line-height: 1.8;
}

.home-hero__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.home-hero__tags li {
  padding: 6px 14px;
  border: 1px solid #f0d8ad;
  border-radius: 999px;
  background: rgba(255, 252, 245, 0.85);
  color: #8a5c05;
  font-size: 13px;
}

.home-hero__art {
  position: relative;
  height: 220px;
}

.home-hero__ring {
  position: absolute;
  border: 1px solid rgba(249, 179, 52, 0.4);
  border-radius: 50%;
}

.home-hero__ring--lg {
  top: 6px;
  right: 4px;
  width: 210px;
  height: 210px;
}

.home-hero__ring--sm {
  top: 56px;
  right: 54px;
  width: 110px;
  height: 110px;
  border-color: rgba(249, 179, 52, 0.55);
}

.home-hero__tile {
  position: absolute;
  border-radius: 12px;
  background: rgba(249, 179, 52, 0.16);
}

.home-hero__tile--a {
  top: 22px;
  right: 168px;
  width: 46px;
  height: 46px;
  transform: rotate(18deg);
}

.home-hero__tile--b {
  bottom: 18px;
  right: 26px;
  width: 30px;
  height: 30px;
  transform: rotate(-12deg);
  background: rgba(249, 179, 52, 0.28);
}

.home-hero__dot {
  position: absolute;
  border-radius: 50%;
  background: #f9b334;
}

.home-hero__dot--a {
  top: 44px;
  right: 36px;
  width: 8px;
  height: 8px;
  opacity: 0.75;
}

.home-hero__dot--b {
  bottom: 62px;
  right: 132px;
  width: 6px;
  height: 6px;
  opacity: 0.6;
}

.home-hero__mark {
  position: absolute;
  top: 76px;
  right: 72px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: linear-gradient(135deg, #f9c96b 0%, #f9b334 100%);
  color: #ffffff;
  font-size: 30px;
  font-weight: 700;
  box-shadow: 0 10px 22px rgba(201, 141, 30, 0.28);
}

/* 步骤指南与用量卡左右分栏：步骤收窄为纵向列表，用量卡获得更宽的趋势空间。 */
.home-main {
  display: grid;
  grid-template-columns: minmax(0, 620px) minmax(0, 1fr);
  gap: 18px;
  align-items: stretch;
}

.home-main > * {
  min-width: 0;
}

/* 三步指南：纵向排列，标题右侧承载业务处理入口。 */
.home-steps {
  display: flex;
  flex-direction: column;
  padding: 26px 28px 28px;
  border: 1px solid #e7dfd4;
  border-radius: 14px;
  background: #fffdf9;
  box-shadow: 0 8px 20px rgba(83, 70, 48, 0.05);
}

.home-steps__heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.home-steps__title {
  min-width: 0;
}

.home-steps__action {
  flex: none;
}

.home-steps__heading h2 {
  margin: 0;
  color: #4a4d55;
  font-size: 18px;
  font-weight: 600;
}

.home-steps__list {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 12px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.home-steps__list li {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  border: 1px solid #f1e7d8;
  border-radius: 12px;
  background: #fffaf1;
}

.home-steps__index {
  flex: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #fff0d6;
  color: #8a5c05;
  font-size: 14px;
  font-weight: 700;
}

.home-steps__text h3 {
  margin: 2px 0 6px;
  color: #4a4d55;
  font-size: 15px;
  font-weight: 600;
}

.home-steps__text p {
  margin: 0;
  color: #7b7164;
  font-size: 13.5px;
  line-height: 1.7;
}

.home-steps__footnote {
  margin: auto 0 0;
  padding-top: 14px;
  border-top: 1px dashed #f1e7d8;
  color: #8c8478;
  font-size: 12.5px;
  line-height: 1.7;
}

@media (max-width: 1100px) {
  .home-hero {
    grid-template-columns: minmax(0, 1fr);
    padding: 32px;
  }

  .home-hero__art {
    display: none;
  }

  .home-hero h1 {
    font-size: 32px;
  }

  .home-main {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 1280px) {
  .home-main {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 760px) {
  .home-steps__heading {
    flex-direction: column;
    align-items: stretch;
  }

  .home-steps__action {
    width: 100%;
  }
}
</style>
