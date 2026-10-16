<template>
  <div>
    <el-container v-if="!isLoginRoute" class="workbench-shell">
    <el-aside width="232px" class="workbench-sidebar">
      <div class="brand" aria-label="财务运维工作台">
        <span class="brand-mark" aria-hidden="true">财</span>
        <span class="brand-text">财务运维工作台</span>
      </div>
      <el-menu
        :default-active="$route.path"
        background-color="#172033"
        text-color="#b8c4d9"
        active-text-color="#ffffff"
        @select="handleNavigationSelect"
      >
        <el-menu-item v-if="canReadTasks" index="/">
          <span slot="title">新建业务处理</span>
        </el-menu-item>
        <el-menu-item v-if="canReadTasks" index="/tasks">
          <span slot="title">任务列表</span>
        </el-menu-item>
        <el-menu-item v-if="canReadTasks" index="/business-skills">
          <span slot="title">业务场景 Skill</span>
        </el-menu-item>
        <el-menu-item v-if="canReadTasks" index="/ems-business-knowledge">
          <span slot="title">EMS业务知识域</span>
        </el-menu-item>
        <el-menu-item v-if="canManageConfig" index="/model-configurations">
          <span slot="title">大模型配置</span>
        </el-menu-item>
        <el-menu-item v-if="canManageConfig" index="/ems-database-configurations">
          <span slot="title">EMS数据库配置</span>
        </el-menu-item>
        <el-menu-item v-if="canManageConfig" index="/data-exploration">
          <span slot="title">数据探索</span>
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
          <el-dropdown v-if="identity" trigger="click" @command="handleUserCommand">
            <span class="identity-user">{{ identity.displayName }}<i class="el-icon-arrow-down"></i></span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-tag v-else type="warning">身份加载中</el-tag>
        </div>
      </el-header>
      <el-main class="workbench-content">
        <el-alert v-if="identityError" :title="identityError" type="warning" :closable="false" show-icon />
        <keep-alive include="WorkbenchHome">
          <router-view :key="viewKey" />
        </keep-alive>
      </el-main>
    </el-container>
    </el-container>
    <router-view v-else />
  </div>
</template>

<script lang="ts">
import Vue from 'vue'

import { CurrentIdentityResponse } from './api/generated'
import { ensureCurrentIdentity, logout } from './services/identity'

export default Vue.extend({
  name: 'App',
  data() {
    return {
      identity: null as CurrentIdentityResponse | null,
      identityError: '',
    }
  },
  computed: {
    /** 判断当前是否展示独立登录页。 */
    isLoginRoute(): boolean {
      return this.$route.name === 'login'
    },
    canReadTasks(): boolean {
      // 必须直接依赖响应式 identity，避免模块级身份缓存导致菜单权限被首次计算结果锁定。
      return this.identity?.roles.includes('PLATFORM_ADMIN') || this.identity?.permissions.includes('TASK_READ') || false
    },
    canManageConfig(): boolean {
      return this.identity?.roles.includes('PLATFORM_ADMIN') || this.identity?.permissions.includes('CONFIG_MANAGE') || false
    },
    viewKey(): string {
      return `${this.identity?.userId || 'identity-loading'}:${this.$route.name || this.$route.path}`
    },
  },
  async created() {
    await this.loadIdentity()
  },
  watch: {
    /** 登录页切回工作台时同步服务层的最新身份缓存，避免菜单等待页面刷新。 */
    '$route.name'(): void {
      if (!this.isLoginRoute && !this.identity) {
        void this.loadIdentity()
      }
    },
  },
  methods: {
    /** 根据菜单入口执行导航；新建入口额外写入草稿标识。 */
    handleNavigationSelect(index: string): void {
      if (index === '/') {
        const route = { path: '/', query: { draft: String(Date.now()) } }
        if (this.$route.path === '/') void this.$router.replace(route)
        else void this.$router.push(route)
        return
      }
      if (this.$route.path !== index) void this.$router.push(index)
    },
    /** 加载服务端身份并驱动权限导航。 */
    async loadIdentity(): Promise<void> {
      try {
        this.identity = await ensureCurrentIdentity()
        this.identityError = ''
      } catch {
        this.identity = null
        this.identityError = '当前身份不可用，请检查本地服务配置'
      }
    },
    /** 执行用户下拉菜单中的退出登录操作。 */
    async handleUserCommand(command: string): Promise<void> {
      if (command !== 'logout') return
      await logout()
      this.identity = null
      await this.$router.replace('/login')
    },
  },
})
</script>
