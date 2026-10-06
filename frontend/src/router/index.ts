import Vue from 'vue'
import Router from 'vue-router'

import WorkbenchHome from '../views/WorkbenchHome.vue'
import EvidenceOverview from '../views/EvidenceOverview.vue'
import PacketDraft from '../views/PacketDraft.vue'
import PublishedPacket from '../views/PublishedPacket.vue'
import TaskDiagnostics from '../views/TaskDiagnostics.vue'
import { IdentityPermission } from '../api/generated'
import { ensureCurrentIdentity, hasPermission } from '../services/identity'

Vue.use(Router)

const router = new Router({
  mode: 'hash',
  routes: [
    {
      path: '/',
      name: 'workbench-home',
      component: WorkbenchHome,
      meta: {
        title: '任务总览',
      },
    },
    {
      path: '/configuration',
      name: 'configuration',
      component: WorkbenchHome,
      meta: { title: '配置管理', permission: 'CONFIG_MANAGE' },
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
  const permissions = Array.isArray(to.meta?.permissions)
    ? to.meta.permissions.filter((permission): permission is IdentityPermission => typeof permission === 'string')
    : typeof to.meta?.permission === 'string' ? [to.meta.permission as IdentityPermission] : []
  if (permissions.length) {
    try {
      await ensureCurrentIdentity()
    } catch {
      next(to.path === '/' ? undefined : '/')
      return
    }
    if (!permissions.some((permission) => hasPermission(permission))) {
      next('/')
      return
    }
  }
  next()
})

export default router
