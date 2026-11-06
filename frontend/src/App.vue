<!-- 工作台应用外壳：统一顶栏标题、可信身份与左侧导航。 -->
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
        background-color="#ffffff"
        text-color="#4a4d55"
        active-text-color="#ffffff"
        @select="handleNavigationSelect"
      >
        <el-menu-item index="/home">
          <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item v-if="canReadTasks" index="/handling">
          <span slot="title">业务处理</span>
        </el-menu-item>
        <el-menu-item v-if="canReadTasks" index="/tasks">
          <span slot="title">任务列表</span>
        </el-menu-item>
        <el-menu-item v-if="canReadTasks" index="/business-skills">
          <span slot="title">业务场景</span>
        </el-menu-item>
        <el-menu-item v-if="canManageConfig" index="/model-configurations">
          <span slot="title">大模型配置</span>
        </el-menu-item>
        <el-menu-item v-if="canManageConfig" index="/ems-database-configurations">
          <span slot="title">数据库实例</span>
        </el-menu-item>
        <el-menu-item v-if="canManageConfig" index="/data-exploration">
          <span slot="title">数据探索</span>
        </el-menu-item>
        <el-menu-item v-if="canReadTasks" index="/latest-exception">
          <span slot="title">异常堆栈</span>
        </el-menu-item>
        <el-menu-item v-if="isPlatformAdmin" index="/platform-users">
          <span slot="title">用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="isPlatformAdmin" index="/scheduled-jobs">
          <span slot="title">调度任务</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="workbench-header">
        <div>
          <h1 v-if="!isHomeRoute">财务智能运维Agent</h1>
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
    /** 首页用 hero 大标题承担标题层级，顶栏不再重复展示产品名。 */
    isHomeRoute(): boolean {
      return this.$route.name === 'home'
    },
    canReadTasks(): boolean {
      // 必须直接依赖响应式 identity，避免模块级身份缓存导致菜单权限被首次计算结果锁定。
      return this.identity?.roles.includes('PLATFORM_ADMIN') || this.identity?.permissions.includes('TASK_READ') || false
    },
    canManageConfig(): boolean {
      return this.identity?.roles.includes('PLATFORM_ADMIN') || this.identity?.permissions.includes('CONFIG_MANAGE') || false
    },
    /** 平台管理员可见所有管理入口。 */
    isPlatformAdmin(): boolean { return this.identity?.roles.includes('PLATFORM_ADMIN') || false },
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
    /** 根据菜单入口执行导航；业务处理入口额外写入草稿标识以重置上一次表单。 */
    handleNavigationSelect(index: string): void {
      if (index === '/handling') {
        const route = { path: '/handling', query: { draft: String(Date.now()) } }
        if (this.$route.path === '/handling') void this.$router.replace(route)
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
