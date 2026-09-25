<template>
  <section class="empty-workbench" aria-labelledby="workbench-title">
    <el-card shadow="never" class="empty-card">
      <div class="empty-card__icon" aria-hidden="true">◎</div>
      <h2 id="workbench-title">工作台已就绪</h2>
      <p>任务、证据和决策包能力将在后续任务中接入。</p>
      <el-alert
        title="当前为工程骨架"
        description="请先完成接口契约和身份边界配置，再开始创建运维任务。"
        type="info"
        :closable="false"
        show-icon
      />
    </el-card>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  name: 'WorkbenchHome',
})
</script>
