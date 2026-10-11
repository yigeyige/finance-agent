import Vue from 'vue'
import Router from 'vue-router'

import WorkbenchHome from '../views/WorkbenchHome.vue'
import EvidenceOverview from '../views/EvidenceOverview.vue'
import PacketDraft from '../views/PacketDraft.vue'
import PublishedPacket from '../views/PublishedPacket.vue'
import TaskDiagnostics from '../views/TaskDiagnostics.vue'
import TaskList from '../views/TaskList.vue'
import TaskDetail from '../views/TaskDetail.vue'
import BusinessSkillCatalog from '../views/BusinessSkillCatalog.vue'
import EmsBusinessKnowledge from '../views/EmsBusinessKnowledge.vue'
import ModelConfiguration from '../views/ModelConfiguration.vue'
import Login from '../views/Login.vue'
import { IdentityPermission } from '../api/generated'
import { ensureCurrentIdentity, hasPermission } from '../services/identity'

Vue.use(Router)

const router = new Router({
  mode: 'hash',
  routes: [
    { path: '/login', name: 'login', component: Login, meta: { public: true } },
    {
      path: '/',
      name: 'workbench-home',
      component: WorkbenchHome,
      meta: {
        title: '任务总览',
      },
    },
    { path: '/tasks', name: 'task-list', component: TaskList, meta: { title: '任务列表', permission: 'TASK_READ' } },
    { path: '/tasks/:taskId', name: 'task-detail', component: TaskDetail, meta: { title: '业务处理详情', permission: 'TASK_READ' } },
    { path: '/business-skills', name: 'business-skills', component: BusinessSkillCatalog, meta: { title: '业务场景 Skill', permission: 'TASK_READ' } },
    { path: '/ems-business-knowledge', name: 'ems-business-knowledge', component: EmsBusinessKnowledge, meta: { title: 'EMS业务知识域', permission: 'TASK_READ' } },
    {
      path: '/model-configurations', name: 'model-configurations', component: ModelConfiguration,
      meta: { title: '大模型配置', permission: 'CONFIG_MANAGE' },
    },
    {
      path: '/tasks/:taskId/evidence',
      name: 'evidence-overview',
      component: EvidenceOverview,
      meta: { title: '证据与覆盖度', permission: 'TASK_READ' },
    },
    {
      path: '/tasks/:taskId/packet-draft',
      name: 'packet-draft',
      component: PacketDraft,
      meta: { title: '决策包草稿', permission: 'TASK_WRITE' },
    },
    {
      path: '/tasks/:taskId/published',
      name: 'published-packet',
      component: PublishedPacket,
      meta: { title: '已发布决策包', permissions: ['TASK_READ', 'PUBLISHED_PACKET_READ'] },
    },
    {
      path: '/tasks/:taskId/diagnostics',
      name: 'task-diagnostics',
      component: TaskDiagnostics,
      meta: { title: '任务诊断', permission: 'TASK_READ' },
    },
  ],
})

/** 依据已加载的服务端权限做前端路由防御，最终授权仍由后端执行。 */
router.beforeEach(async (to, _from, next) => {
  if (to.meta?.public) { next(); return }
  const permissions = Array.isArray(to.meta?.permissions)
    ? to.meta.permissions.filter((permission): permission is IdentityPermission => typeof permission === 'string')
    : typeof to.meta?.permission === 'string' ? [to.meta.permission as IdentityPermission] : []
  if (permissions.length || to.path === '/') {
    try {
      await ensureCurrentIdentity()
    } catch {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
    if (permissions.length > 0 && !permissions.some((permission) => hasPermission(permission))) {
      next('/')
      return
    }
  }
  next()
})

export default router
