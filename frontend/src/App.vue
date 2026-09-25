<template>
  <el-container class="workbench-shell">
    <el-aside width="232px" class="workbench-sidebar">
      <div class="brand" aria-label="财务运维工作台">
        <span class="brand-mark" aria-hidden="true">财</span>
        <span class="brand-text">财务运维工作台</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#172033"
        text-color="#b8c4d9"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/">
          <span slot="title">任务总览</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="workbench-header">
        <div>
          <p class="eyebrow">财务共享智能运维 Agent</p>
          <h1>运维工作台</h1>
        </div>
        <el-tag type="info" effect="plain">开发骨架</el-tag>
      </el-header>
      <el-main class="workbench-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  name: 'App',
})
</script>
