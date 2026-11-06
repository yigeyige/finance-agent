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

    <section class="home-quick" aria-label="业务处理快捷入口">
      <div class="home-quick__icon" aria-hidden="true"><i class="el-icon-document" /></div>
      <div class="home-quick__body">
        <h2>业务处理</h2>
        <p>从一句业务说明出发，模型在受控范围内取数并生成初稿，最终形成可复核、可交接的决策包。</p>
      </div>
      <el-button type="primary" class="home-quick__action" @click="startHandling">进入业务处理</el-button>
    </section>

    <section class="home-steps" aria-label="使用流程">
      <div class="home-steps__heading">
        <h2>三步完成一次业务处理</h2>
        <p>每一步都可暂停回溯，关键结论必须由人工确认。</p>
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
    </section>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'

/** 首页只做介绍与导航，不读取业务数据，避免影响登录后的首屏速度。 */
export default Vue.extend({
  name: 'HomePage',
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

/* 业务处理快捷卡：左侧橙条强调，右侧仅保留一个主操作。 */
.home-quick {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 20px;
  padding: 22px 24px;
  border: 1px solid #e7dfd4;
  border-left: 4px solid #f9b334;
  border-radius: 14px;
  background: #fffdf9;
  box-shadow: 0 8px 20px rgba(83, 70, 48, 0.06);
}

.home-quick__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #fff4e2;
  color: #b0720f;
  font-size: 22px;
}

.home-quick h2 {
  margin: 0 0 6px;
  color: #4a4d55;
  font-size: 18px;
  font-weight: 600;
}

.home-quick p {
  margin: 0;
  color: #7b7164;
  font-size: 14px;
  line-height: 1.7;
}

.home-quick__action {
  flex: none;
}

/* 三步指南：信息型卡片，不做点击跳转，避免与快捷卡争夺注意力。 */
.home-steps {
  padding: 26px 28px 28px;
  border: 1px solid #e7dfd4;
  border-radius: 14px;
  background: #fffdf9;
  box-shadow: 0 8px 20px rgba(83, 70, 48, 0.05);
}

.home-steps__heading h2 {
  margin: 0;
  color: #4a4d55;
  font-size: 18px;
  font-weight: 600;
}

.home-steps__heading p {
  margin: 6px 0 22px;
  color: #8c8478;
  font-size: 13px;
}

.home-steps__list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.home-steps__list li {
  display: flex;
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

  .home-steps__list {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 760px) {
  .home-quick {
    grid-template-columns: auto minmax(0, 1fr);
  }

  .home-quick__action {
    grid-column: 1 / -1;
    width: 100%;
  }
}
</style>
