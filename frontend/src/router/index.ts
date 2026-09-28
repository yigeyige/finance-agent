import Vue from 'vue'
import Router from 'vue-router'

import WorkbenchHome from '../views/WorkbenchHome.vue'
import { hasPermission } from '../services/identity'

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
  ],
})

/** 依据已加载的服务端权限做前端路由防御，最终授权仍由后端执行。 */
router.beforeEach((to, _from, next) => {
  const permission = typeof to.meta?.permission === 'string' ? to.meta.permission : undefined
  if (permission && !hasPermission(permission as 'CONFIG_MANAGE')) {
    next('/')
    return
  }
  next()
})

export default router
