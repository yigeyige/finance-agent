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
        <el-menu-item v-if="canReadTasks" index="/">
          <span slot="title">任务总览</span>
        </el-menu-item>
        <el-menu-item v-if="canManageConfig" index="/configuration">
          <span slot="title">配置管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="workbench-header">
        <div>
          <p class="eyebrow">财务共享智能运维 Agent</p>
          <h1>运维工作台</h1>
        </div>
        <div class="identity-tools">
          <el-select
            v-if="testIdentityMode"
            v-model="selectedIdentity"
            size="small"
            aria-label="选择测试身份"
            @change="switchIdentity"
          >
            <el-option v-for="identity in testIdentities" :key="identity.value" :label="identity.label" :value="identity.value" />
          </el-select>
          <el-tag v-if="identity" type="info" effect="plain">{{ identity.displayName }}</el-tag>
          <el-tag v-else type="warning" effect="plain">身份加载中</el-tag>
        </div>
      </el-header>
      <el-main class="workbench-content">
        <el-alert v-if="identityError" :title="identityError" type="warning" :closable="false" show-icon />
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script lang="ts">
import Vue from 'vue'

import { CurrentIdentityResponse } from './api/generated'
import { fetchCurrentIdentity, selectTestIdentity, TEST_IDENTITIES } from './services/identity'

export default Vue.extend({
  name: 'App',
  data() {
    return {
      identity: null as CurrentIdentityResponse | null,
      identityError: '',
      selectedIdentity: typeof localStorage === 'undefined' ? 'operator' : localStorage.getItem('finance-agent.test-identity') || 'operator',
      testIdentities: TEST_IDENTITIES,
      testIdentityMode: import.meta.env.VITE_IDENTITY_MODE !== 'prod',
    }
  },
  computed: {
    canReadTasks(): boolean {
      return this.identity?.permissions.includes('TASK_READ') ?? false
    },
    canManageConfig(): boolean {
      return this.identity?.permissions.includes('CONFIG_MANAGE') ?? false
    },
  },
  async created() {
    await this.loadIdentity()
  },
  methods: {
    /** 加载服务端身份并驱动权限导航。 */
    async loadIdentity(): Promise<void> {
      try {
        this.identity = await fetchCurrentIdentity()
        this.identityError = ''
      } catch {
        this.identity = null
        this.identityError = '当前身份不可用，请检查本地服务配置'
      }
    },
    /** 切换 local/test 合成身份并刷新服务端权限。 */
    async switchIdentity(value: string): Promise<void> {
      selectTestIdentity(value)
      await this.loadIdentity()
      await this.$router.push('/')
    },
  },
})
</script>
